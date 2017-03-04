/**
Copyright © 2016, United States Government, as represented
by the Administrator of the National Aeronautics and Space
Administration. All rights reserved.
 
The MAV - Modeling, analysis and visualization of ATM concepts
platform is licensed under the Apache License, Version 2.0
(the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0. 
 
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific
language governing permissions and limitations under the
License.
**/

package gov.nasa.arc.brahms.simulator.scheduler;


import gov.nasa.arc.brahms.model.Event;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.MultiAgentSystem;
import gov.nasa.arc.brahms.model.WorkFrame;
import gov.nasa.arc.brahms.simulator.Simulator;
import gov.nasa.arc.brahms.simulator.Utils;
import gov.nasa.arc.brahms.simulator.elems.ActiveFrame;
import gov.nasa.arc.brahms.simulator.elems.Detectable_Sim;
import gov.nasa.arc.brahms.simulator.elems.ThoughtFrame_Sim;
import gov.nasa.arc.brahms.simulator.elems.WorkFrame_Sim;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.concept.Object_b;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


public class Scheduler {

	MultiAgentSystem mas;
	EventDistributor eventDistributor;
	StringBuilder timeSteps = new StringBuilder();
	Map<Basic, Frame> currExecuting;
	public static Map<Basic, EventSequence> currSeq;
	
	int shortestDuration = -1;
	
	public static ArrayList<Integer> timeStepsOfBeliefUpdates =
			new ArrayList<Integer>();
	DeltaQueue dq;
	EventScheduler eventScheduler;

	public Scheduler(MultiAgentSystem mas) {
		this.mas = mas;
		eventDistributor = new EventDistributor();
		dq = new DeltaQueue();
		eventScheduler = new EventScheduler(dq);
		currExecuting = new HashMap<Basic, Frame>();
		currSeq = new HashMap<Basic, EventSequence>();
	}
	
	public boolean checkAndExecuteActiveFrames() {
		for (Agent agt : mas.getAgents()){ 
			scheduleEventsOfBasic(agt);
		}
		for(Object_b obj : mas.getObjects()) {
			scheduleEventsOfBasic(obj);
		}
		if(dq.head != null) {
			eventScheduler.fireEvents();
			return true;
		} else {
			return false;
		}
		
	}
	
	public void scheduleEventsOfBasic(Basic b) {
		//the currently executing frame may not yet be done
		if(currSeq.containsKey(b) &&
					!currSeq.get(b).isEmpty()) {
			return;
		}
		
		//there is no frame
		if(!currExecuting.containsKey(b)) {
			scheduleFrameForBasic(b);
		} else{
			//check whether another variable instance binding
			//needs to be executed
			
			//TODO: consider generating all the frames and
			//schedule them rather than schedule one frame at a time.
			Frame f = currExecuting.get(b);
			f.removeAllVarValues();
			f.firstInstanceVarsExecuted();
			f.setExecuted(true);
			f.removeFirstInst(); //remove current set of variables
			if (f.getVarInstances().isEmpty()) {
				currExecuting.remove(b);
				scheduleFrameForBasic(b);
			} else {
				createEventSequence(b, f);
			}
		}
	}
	

	public void scheduleFrameForBasic(Basic b) {
		List<Frame> activeThoughtFrames = 
				getActiveFrames(b, b.getAllThoughtFrames());
		if(activeThoughtFrames.size() > 0) {
			int index = Utils.getHighestPriorityFrameIndex(activeThoughtFrames.size());
			Frame thoughtFrame = activeThoughtFrames.get(index);
			createEventSequence(b, thoughtFrame);
			return;
		}
	
		List<Frame> activeWorkFrames = 
				getActiveFrames(b, b.getAllWorkFrames());
		
		
		if(activeWorkFrames.size() > 0) {
			int index = Utils.getHighestPriorityFrameIndex(activeWorkFrames.size());
			Frame workFrame = activeWorkFrames.get(index);
			createEventSequence(b, workFrame);
			return;
		}
	}
	
	public List<Frame> getActiveFrames(Basic b, Set<Frame> frames) {
		List<Frame> activeFrames = new ArrayList<Frame>();
		int priority = -1;
		for(Frame f : frames) {
			if(f.getExecuted() && f.getRepeat().equals("false")) {
				continue;
			}
			if(ActiveFrame.isFrameActive(b, f) &&
							f.getPriority() >= priority) {
				priority = f.getPriority();
				activeFrames.add(f);
			}
		}
		List<Frame> highPriorityFrames = new ArrayList<Frame>();
		for(Frame active : activeFrames) {
			if(active.getPriority() == priority) {
				highPriorityFrames.add(active);
			}
		}
		return highPriorityFrames;
	}
	
	public void createEventSequence(Basic b, Frame f) {
		//System.out.println("create event seqeuence for " + b.getName() + " ---> " + f.getName());
		EventSequence es0 = new EventSequence();
		for(Event event : f.getEvents()) {
			event.createDeltaQueueEvent(es0, b, f);
		}
		es0.setBasicAndFrame(b, f);
		dq.insertEventList(es0, es0.getHeadExecTime());
		currSeq.put(b, es0);
		currExecuting.put(b, f);
		
	}
	
	//Contains Sch_* rules: Sch_run, Sch_rcvd and Sch_Term

	public void Sch_Star() {	
		while(true){
			Sch_Run();
			if(shortestDuration != -1){
				Sch_rcvd();//Move clock forward
			}
			else{ /*Sch_term: Quit program*/
				Sch_term();
				break;
			}
		}
		
	}

	public void Sch_Run(){
		checkAllTFs(); //find and execute TFs
		checkAllWFs();
		checkAllDetectables();
		shortestDuration = getAllDurations(); //find currWF and min time
	}

	public void Sch_rcvd(){
		int nextTime = mas.getGlobalClock() + shortestDuration;
		mas.setGlobalClock(nextTime);
		mas.setDuration(shortestDuration);
		updateTimes();
	}

	public void Sch_term(){
		mas.setGlobalClock(-1);
	}

	/**
	 * Checks through all the thoughtframes for all agents and objects and
	 * executes activated ones
	 */
	public void checkAllTFs() {
		for (Agent agt : mas.getAgents()){ 
			ThoughtFrame_Sim.Set_TF(agt);
		}
		for (Object_b obj : mas.getObjects()){ 
			ThoughtFrame_Sim.Set_TF(obj);
		}
	}

	public void checkAllWFs() {
		for (Agent agent : mas.getAgents()) {
			WorkFrame_Sim.buildWorkStack(agent);
		}
		for (Object_b obj : mas.getObjects()) { 
			WorkFrame_Sim.buildWorkStack(obj);
		}
	}

	public void checkAllDetectables() {
		if (Simulator.DEBUG)
			System.out.println("$$$$$ Checking Detectables $$$$$ ");
		Iterator<Agent> AgIterator = mas.getAgents().iterator();
		while (AgIterator.hasNext()){ //build wf for each ag
			Agent a = AgIterator.next();
			Detectable_Sim.checkForDetectables(a);
		}
		Iterator<Object_b> ObjIterator = mas.getObjects().iterator();
		while (ObjIterator.hasNext()){ //check agents for tfs
			Object_b b = ObjIterator.next();
			Detectable_Sim.checkForDetectables(b);
		}
	}

	/**
	 * Finds the curr WF for each agent/obj
	 * and return the shortest duration
	 */
	public int getAllDurations() {
		if (Simulator.DEBUG)
			System.out.println("$$$$$ Get Durations $$$$$ ");
		int minTime = -1;
		//agents:
		Iterator<Agent> AgIterator = mas.getAgents().iterator();
		while (AgIterator.hasNext()){ //check agents for wfs
			Agent a = AgIterator.next();
			int agTime = WorkFrame_Sim.getNextDuration(a);
			if (!(a.getCurrentWorkFrame().isEmpty()) && (agTime >= 0)) {
				Stack<Object> stack = a.getCurrentWorkFrame();
				WorkFrame currWF = (WorkFrame) stack.get(stack.size()-2);
				if (Simulator.DEBUG)
					System.out.println("TIME: " + agTime + " sec for "
							+ a.getName() + "." + currWF.getName());
				if (minTime >= agTime || minTime == -1)
					minTime = agTime;
			}
			else
				if (Simulator.DEBUG)
					System.out.println("\t *There are no active WFs for Agent "
							+ a.getName());
		}
		//objects:
		Iterator<Object_b> ObjIterator = mas.getObjects().iterator();
		while (ObjIterator.hasNext()){ //check agents for wfs
			Object_b b = ObjIterator.next();
			int objTime = WorkFrame_Sim.getNextDuration(b);
			if (Simulator.DEBUG)
				System.out.println("objTime is: " + objTime);
			if (minTime >= objTime && objTime > -1) {
				minTime = objTime;
			}
			if (!(b.getCurrentWorkFrame().isEmpty())) {
				Stack<Object> stack = b.getCurrentWorkFrame();
				WorkFrame currWF = (WorkFrame) stack.get(stack.size()-2);
				if (Simulator.DEBUG)
					System.out.println("\t *The current WF is " + currWF.getName());
				if (minTime >= objTime || minTime == -1)
					minTime = objTime;
			}
			else
				if (Simulator.DEBUG)
					System.out.println("\t *There are no active WFs for Object "
							+ b.getName());
		}

		return minTime;
	}

	/**
	 * Goes through all agents and objs and subtracts the shortest duration from
	 * the remaining time on the current activity
	 */
	public void updateTimes() {
		for (Agent agt : mas.getAgents()) { // update all wfs
			agt.setCurrentTime(mas.getGlobalClock());
			WorkFrame_Sim.updateTime(agt, shortestDuration,
					mas.getGlobalClock());
		}
		for (Object_b obj : mas.getObjects()) {
			obj.setCurrentTime(mas.getGlobalClock());
			WorkFrame_Sim.updateTime(obj, shortestDuration,
					mas.getGlobalClock());
		}
	}



}
