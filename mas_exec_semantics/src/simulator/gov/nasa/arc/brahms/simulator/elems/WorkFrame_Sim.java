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

package gov.nasa.arc.brahms.simulator.elems;

import gov.nasa.arc.brahms.model.Detectable;
import gov.nasa.arc.brahms.model.DetectableAction;
import gov.nasa.arc.brahms.model.Event;
import gov.nasa.arc.brahms.model.Conclude;
import gov.nasa.arc.brahms.model.ThoughtFrame;
import gov.nasa.arc.brahms.model.WorkFrame;
import gov.nasa.arc.brahms.model.EventActivity;
import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.activity.*;
import gov.nasa.arc.brahms.model.comparison.ResultComparison;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.Simulator;
import gov.nasa.arc.brahms.simulator.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Stack;

public class WorkFrame_Sim {
	
	public static Set<WorkFrame> activeWorkFrames;
	public static Basic b; //the current agent or object

	/**
	 * Sends to ActiveFrame.isFrameActive to check the preconditions to
	 * determine if each wf is active, returns the set of active wfs
	 * @param wfs is the set of workframes to look through
	 * @return the set of active work frames
	 * @exception throws exception if an agent does not have a dataframe
	 */
	private static Set<WorkFrame> findActiveWorkframes(Set<WorkFrame> wfs) {
		Set<WorkFrame> activeFrames = new HashSet<WorkFrame>();
		for (WorkFrame workFrame : wfs) {
			if (workFrame.getExecuted()
					&& workFrame.getRepeat().equals("false"))
				continue;
			if (ActiveFrame.isFrameActive(b, workFrame))
				activeFrames.add(workFrame);
		}
		return activeFrames;
	}
	
	private static int updateMaxPriority(int currPriority, int maxPriority) {
		if (currPriority > maxPriority) {//if > maxPri, use pri
			return currPriority;
		}
		return maxPriority;
	}
	
	/**
	 * Returns the current workframe (that with the highest priority) and
	 * ensures that wfs with repeat: false are being tracked accordingly
	 * @param obj must be an instance of Agent, Object_b, or CompositeActivity
	 * (something that contains wfs)
	 * @param activeWorkFrames the set of wfs whose preconditions have been met
	 * @return the current workframe
	 */
	private static WorkFrame Wf_getPrior(Set<WorkFrame> activeWorkFrames) {
		int maxPri = 0;
		if (activeWorkFrames.size() == 0) return null;
		//find the wf with the highest priority
		for (WorkFrame wf : activeWorkFrames) {
			if (wf.getPriority() > 0) {
				maxPri = updateMaxPriority(wf.getPriority(), maxPri);
				continue;
			} 
			//get the priority from the activities 
			int maxActPri = 0;
			for (Event event : wf.getEvents()) {
				if (!(event instanceof EventActivity))	continue;

				Activity tmpAct = getActivity(b, (EventActivity) event);
				//maxActPri = updateMaxPriority(tmpAct.getPriority(b), maxActPri);
			}
			wf.setPriority(maxActPri);
			maxPri = updateMaxPriority(wf.getPriority(), maxPri);

		}
		List<WorkFrame> highestPriorityFrames = new ArrayList<WorkFrame>();
		for(WorkFrame wfFrame : activeWorkFrames) {
			if(wfFrame.getPriority() != maxPri) continue;
			highestPriorityFrames.add(wfFrame);
		}
		
		int index = Utils.getHighestPriorityFrameIndex(highestPriorityFrames.size());
		if (index < 0)
			return null;
	
		return highestPriorityFrames.get(index);
	}
	
	/**
	 * Takes in a set of workframes to search within, finds active wfs and
	 * then returns the one with highest priority
	 * @return wf to execute
	 */
	private static WorkFrame getFrameToExecute(Set<WorkFrame> wfs) {
		Set<WorkFrame> newSet = new HashSet<WorkFrame>();
		List<Stack<Object>> impassed = b.getInterruptedFrames();
		Iterator<WorkFrame> wfItr = wfs.iterator();
		while (wfItr.hasNext()) {
			WorkFrame wfSet = wfItr.next();
			boolean inImpassed = false;
			for (int i = 0; i < impassed.size(); i++) {
				if (impassed.get(i).get(0) instanceof WorkFrame) {
					WorkFrame tmp = (WorkFrame) impassed.get(i).get(0);
					if (tmp.getName().equals(wfSet.getName())) {
						inImpassed = true;
						if (checkIfImpasseOver(tmp)) {
							wfSet.setExecuted(false);
							newSet.add(wfSet);
						}
					}
				}
			}
			if (!(inImpassed)){
				newSet.add(wfSet);
			}
		}
		Set<WorkFrame> activeFrames = findActiveWorkframes(newSet);
		WorkFrame wf = Wf_getPrior(activeFrames);
		if(wf != null){
			b.currentWFName = wf.getName();
			//Loop through all the impassed workframes to check whether the selected workframe is an impassed workframe
			for(int i = 0; i < impassed.size(); i++){
				if (impassed.get(i).get(0) instanceof WorkFrame) {
					WorkFrame tmp = (WorkFrame) impassed.get(i).get(0);
					if (tmp.getName().equals(wf.getName())) {
						//since it is being executed it will no longer be impassed so remove
						b.removeFromImpassed(wf);
					}
				}
			}
		}			
		return wf;
	}
	
	//TODO test
	private static boolean checkIfImpasseOver(WorkFrame wf) {
		List<Detectable> detectables = wf.getDetectables();
		for (int i = 0; i < detectables.size(); i++) {
			Detectable d = detectables.get(i);
			if (d.getAction() == DetectableAction.IMPASSE) {
				ResultComparison resultComp = d.getResultComparison();
				return !(Detectable_Sim.checkCondition(b, resultComp));
			}
		}
		throw new RuntimeException("***ERROR in WorkFrame_Sim: must have " +
				"detectables, and one must be impasse");
	}
	
	 /**
	  * finds the activity associated with the event activity
	  * starts with searching for potential composite sub-activities, in case
	  * they override 'global' ones
	  * @param evAct
	  * @return Activity
	  */
	public static Activity getActivity(Basic obj, EventActivity evAct) {
		b = obj;
		Stack<Object> stack = obj.getCurrentWorkFrame();
		if (stack.size() > 1) { //check composite acts first
			for (int i = stack.size() - 1; i > 0; i--) {
				if (stack.get(i) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) stack.get(i);
					if (ai.getActivity() instanceof CompositeActivity) {
						CompositeActivity comp = (CompositeActivity) ai.getActivity();
						Iterator<Activity> actItr = comp.getActivities().iterator();
						while (actItr.hasNext()) {
							Activity tmp = actItr.next();
							if (tmp.getName().equals(evAct.getName()))
								return tmp;
						}
					}
				}
			}
		}
		Iterator<Activity> acts = b.getActivities().iterator();
		while (acts.hasNext()) {
			Activity act = acts.next();
			if (act.getName().equals(evAct.getName()))
				return act;
		}
		return null;
	}

	/**if stack is empty, push the highest priority frame
	 * else check stack
	 * @param b owner of stack
	 */
	public static void buildWorkStack(Basic basic) {

		b = basic;
		Stack<Object> stack = b.getCurrentWorkFrame();
		if ((stack.size() == 0) || (stack.isEmpty())) {
			WorkFrame wf = getFrameToExecute(b.getWorkFrames()); // global prior
																	// wf
			if (wf == null) {
				return;
			}

			wf.setLevel(0);
			wf.setExecuted(true);
			stack.push(wf);
			b.setCurrentWorkFrame(stack);
			b.setActualWorkFrame(wf);
			
		} /*else {
			checkRestOfStack(0, null);
			if (!(b.getCurrentWorkFrame().isEmpty())) {
				CompositeActivity comp = findHighestCompositeForTopWF();
				pushNextEvent(basic, comp);
			}
		}*/
		//resetVarsNotInUse(); // TODO this needs to be here? how to reset ALL?

	}
	
	private static CompositeActivity findHighestCompositeForTopWF() {
		Stack<Object> stack = b.getCurrentWorkFrame();
		WorkFrame wf = null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i) instanceof WorkFrame) {
				wf = (WorkFrame) stack.get(i); //assign highest wf
				i = -1;
			}
		}
		if (wf == null)
			return null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i) instanceof ActivityInstance) {
				ActivityInstance ai = (ActivityInstance) stack.get(i);
				if (ai.getActivity() instanceof CompositeActivity) {
					CompositeActivity comp = (CompositeActivity) ai.getActivity();
					if (comp.getLevel() == (wf.getLevel()-1))
						return comp;
				}
			}
		}
		return null;
	}
	
	/**
	 * Check stack to ensure that the wf closest to top of stack is priority
	 * recurses from searching global level to deepest level (in composites)
	 * @param level; 0 = global level
	 * @param ca; composite activity that the level is within (null for global)
	 */
	/*private static void checkRestOfStack(int level, CompositeActivity ca) {
		Stack<Object> stack = b.getCurrentWorkFrame();
		//start at top of stack (peek/pop) and work down until you reach level
		for (int i = stack.size() - 1; i >= 0; i--) { 
			Object tmp = stack.get(i);
			if (tmp instanceof WorkFrame) { //only care about wfs here
				WorkFrame tmpWF = (WorkFrame) tmp;
				if (level > tmpWF.getLevel()) {
					return; //done, have checked all levels
				}
				if (tmpWF.getLevel() == level) {
					WorkFrame newWF;
					if (ca == null) //get global wfs
						newWF = getFrameToExecute(b.getWorkFrames());
					else //get composite wf priority
						newWF = getFrameToExecute(ca.getWorkFrames()); 
					//check if the new wf and wf on stack match
					if (newWF != null) {
						if ((!(tmpWF.getName().equals(newWF.getName()))) && 
								(newWF.getPriority() > tmpWF.getPriority())) {
							if (ca == null)
								newWF.setLevel(0);
							else
								newWF.setLevel(ca.getLevel()+1);
							newWF.setExecuted(true);
							if (Simulator.DEBUG)
								System.out.println("pushing again " + newWF.getName());
							if (inImpassed(newWF)) {
								Stack<Object> miniStack = getImpassed(newWF);
								for (int j = 0; j < miniStack.size(); j++) {
									if (miniStack.get(j) instanceof WorkFrame)
										((WorkFrame) miniStack.get(j)).setExecuted(true);
									stack.push(miniStack.get(j));
								}
								b.removeFromImpassed(newWF);
							}
							else{
								stack.push(newWF); //push new wf if: not match & higher prior
							}
							b.setCurrentWorkFrame(stack);
						} else checkNextElementInStack(stack, i, level);
					} else { //else continue checking stack
						checkNextElementInStack(stack, i, level);
					}
					return;
				}
			}
		}
	}*/
/*	
	private static boolean inImpassed(WorkFrame wf) {
		List<Stack<Object>> impassed = b.getInterruptedFrames();
		for (int i = 0; i < impassed.size(); i++) {
			Stack<Object> stack = impassed.get(i);
			if (stack.get(i) instanceof WorkFrame) {
				WorkFrame top = (WorkFrame) stack.get(i);
				if (top.getName().equals(wf.getName()))
					return true;
			}
		}
		return false;
	}
	
	private static Stack<Object> getImpassed(WorkFrame wf) {
		List<Stack<Object>> impassed = b.getInterruptedFrames();
		for (int i = 0; i < impassed.size(); i++) {
			Stack<Object> stack = impassed.get(i);
			if (stack.get(0) instanceof WorkFrame) {
				WorkFrame top = (WorkFrame) stack.get(0);
				if (top.getName().equals(wf.getName()))
					return stack;
			}
		}
		return null;
	}
		
	private static void checkNextElementInStack(Stack<Object> stack, int i, int level) {
	if (Simulator.DEBUG)
			System.out.println("in the else... i " + i + b.printStack());
		if (i >= stack.size())
			return; //have reached top of stack
		else if ((i+1) >= stack.size()) {
			Object top = stack.get(i);
			if (top instanceof WorkFrame) {
				WorkFrame wf = (WorkFrame) top;
				EventActivity ea = (EventActivity) wf.getEvents().get(wf.getIndex());
				Activity act1 = getActivity(b, ea);
				if (act1 == null) {
					if (stack.size() > 1) {
						b.printStack();
						ActivityInstance ca = (ActivityInstance) stack.get(i - 1);
						if (ca.getActivity() instanceof CompositeActivity) {
							CompositeActivity comp = (CompositeActivity) ca.getActivity();
							Set<Activity> acts = comp.getActivities();
							Iterator<Activity> actItr = acts.iterator();
							while (actItr.hasNext()) {
								Activity tmp = actItr.next();
								if (tmp.getName().equals(ea.getName()))
									act1 = tmp;
							}
						}
					}
				}
				ActivityInstance ai = new ActivityInstance(b, act1);
				if (act1 instanceof MoveActivity &&
						ai.getActivity().getMaxDuration(b) == 0) {
					//only get path/distance if there is no max given
					MoveActivity ma = ((MoveActivity) act1);
					int distance = ma.getDistance(((Agent) b));
					if(distance < Integer.MAX_VALUE)
						ai.setDuration(distance);
				}
				stack.push(ai);
			} else throw new RuntimeException("top of stack should be wf");
		}
		else if (stack.get(i+1) instanceof ActivityInstance) {
			ActivityInstance ai = (ActivityInstance) stack.get(i+1);
			if (ai.getActivity() instanceof CompositeActivity) {
				checkRestOfStack(level+1, (CompositeActivity) ai.getActivity());
			}
			else //the activity is not composite
				return; //you've reached the end of the stack
		} else {
			System.out.println(b.printStack());
			throw new RuntimeException("problem with " +
				"WF_Sim: checkRestOfStack algorithm");
		}
	}
	*/
	/**
	 * Returns the duration of the activity on the top of the stack
	 * public so need to set b
	 * @param b
	 * @return
	 */
	public static int getNextDuration(Basic b) {
		WorkFrame_Sim.b = b;
		pushNextEvent(b, null);
		if (!(b.getCurrentWorkFrame().isEmpty()))
			return getDuration();
		else
			return -2; //finished wf, so 0? (-1 means no active wfs)?
	}
	
	/**
	 * Get next event will look at the last workframe pushed onto the stack
	 * if there are no more events, reset the wf index and pop it from the
	 * stack if the next event is a conclude statement, execute it otherwise
	 * it's an activity, so push an activity instance to the stack and set the
	 * initial duration
	 * @exception throws exception if the next object on the stack is not a wf
	 */
	public static void pushNextEvent(Basic obj, CompositeActivity ca) {
		b = obj;
		Stack<Object> stack = b.getCurrentWorkFrame();
		if (stack.isEmpty()) {
			return;
		}
		if (stack.peek() instanceof ActivityInstance) {
			ActivityInstance actInst = (ActivityInstance) stack.peek(); 
			if (!(actInst.getActivity() instanceof CompositeActivity))
				return; //already have an event pushed
		}
		WorkFrame wf = null;
		CompositeActivity comp = null;
		//get the top of the stack, wf or composite
		if (stack.peek() instanceof WorkFrame) {
			wf = (WorkFrame) stack.peek();
			if (Simulator.DEBUG)
				System.out.println("wf: " + wf.getName() + " idx " + wf.getIndex() + wf.getEvents().toString());
		} else {
			comp = (CompositeActivity) ((ActivityInstance) stack.peek()).getActivity();
		}
		Event currEvent = null;
		if (wf == null) {
			//If wf is null then it found a composite activity, so need to find next event in the composite
			//activity
			currEvent = getNextEvent(comp);
		} else {
			currEvent = getNextEvent(wf);
		}
		if (currEvent == null) {
			if (comp == null) {
				if (Simulator.DEBUG) System.out.println("popping " + wf.getName());
			} else if (comp.getEndCondition() == EndCondition.DETECTABLE) {
				if (Simulator.DEBUG)
					System.out.println("no work left for " + comp.getName() + 
						" but need detectable to end");
				return;	
			} else {
				if (frameInImpassed(comp) && comp.getEndCondition() == EndCondition.NOWORK)
					return;
			}
			stack.pop();
			if (Simulator.DEBUG) {
				System.out.println("push next event from: ");
				System.out.println(b.printStack());
			}
			if (stack.isEmpty())
				buildWorkStack(b);
			pushNextEvent(b, null);
			return;
		}
		else{
			//After getting the next event, the stack will change so now a wf will be at the top.  Another
			//Composite can't be at the top because you need another workframe to call a composite 
			wf = (WorkFrame) stack.peek();
		}
		if (stack.size() > 2) {
			ca = findHighestCompositeForTopWF();
		}
		if (currEvent instanceof Conclude) { //if event is conclude
			Conclude_Sim.concludeStatement(b, (Conclude) currEvent, wf);
			if (wf == null)
				wf = (WorkFrame) stack.peek();
			wf.incIndex();
			pushNextEvent(b, ca);
		} else if (currEvent instanceof EventActivity) { //if event is activity
			if (Simulator.DEBUG)
				System.out.println("eventactivity...");
			EventActivity ea = (EventActivity) currEvent;	
			boolean eventPushed = false;
			//may need to change next line for composites (more acts to search)
			if (ca != null) { //check composite's activities
				Iterator<Activity> cactItr = ca.getActivities().iterator();
				while (cactItr.hasNext()) { //look through composite activities first
					Activity act = cactItr.next();
					List<Value> params = ea.getParams();
					if(act.getParams().size() > 0)
						act.setParams(params, obj, wf);
					ActivityInstance newAI = getNextAI(act, ea);
					if (newAI != null) {
						if (wf == null)
							wf = (WorkFrame) stack.peek();
						newAI.getActivity().setLevel(wf.getLevel());
						stack.push(newAI);
						b.setCurrentWorkFrame(stack);
						if (Simulator.DEBUG)
							System.out.println(b.printStack());
						pushNextEvent(b, null);
						eventPushed = true;
						
					}
				}
			}
			//Event has not yet been pushed
			if (!eventPushed) { //check 'global' activities if not yet found
				if(wf == null)
					wf = (WorkFrame) stack.peek();
				//Loop through all activities to see which activity it is
				Iterator<Activity> actItr = b.getActivities().iterator();
				while (actItr.hasNext()) {
					Activity act = actItr.next();
					List<Value> params = ea.getParams();
					if(act.getParams().size() > 0 && !(act instanceof CompositeActivity) && act.getName().equals(ea.getName())){
						act.setParams(params, obj, wf);
					}
					//Generate an instance of the activity
					ActivityInstance newAI = getNextAI(act, ea);
					if (newAI != null) {
						if (wf == null)
							wf = (WorkFrame) stack.peek();
						newAI.getActivity().setLevel(wf.getLevel());
						newAI.getActivity().setParams(ea.getParams(), b, wf);
						stack.push(newAI);
						b.setCurrentWorkFrame(stack);
						if (Simulator.DEBUG)
							System.out.println(b.printStack());
						if (newAI.getActivity() instanceof CompositeActivity) {
							WorkFrame newWF = getFrameToExecute(((CompositeActivity) newAI.getActivity()).getWorkFrames());
							if (newWF != null) {
								newWF.setLevel(((CompositeActivity) newAI.getActivity()).getLevel() + 1);
								newWF.setExecuted(true);
								stack.push(newWF);
								b.setCurrentWorkFrame(stack);
								if (Simulator.DEBUG)
									System.out.println(b.printStack());
							}
							pushNextEvent(b, null);
						}
						return;
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param act1
	 * @param ea
	 * @return
	 */
	private static ActivityInstance getNextAI(Activity act1, EventActivity ea) {
		if (act1.getName().equals(ea.getName())) {
			if (Simulator.DEBUG)
				System.out.println("name of act: " + act1.getName());
			ActivityInstance ai = new ActivityInstance(b, act1);
			if (act1 instanceof MoveActivity
					//&&
				//	ai.getActivity().getMaxDuration(b) == 0
					) {
				//only get path/distance if there is no max given
				MoveActivity ma = ((MoveActivity) act1);
				int distance = ma.getDistance(((Agent) b));
				if(distance < Integer.MAX_VALUE)
					ai.setDuration(distance);
			}
			return ai;
		}
		return null;
	}
	
	/**
	 * Peeks at the last object on the stack, which should be an
	 * ActivityInstance. Composites should find active wfs and getNextEvent 
	 * first - not implemented yet
	 * @return the remaining duration for that activity pushed onto the stack
	 * @exception if the obj on stack is a wf instead of an activity instance
	 * @exception for composites - they need to be implemented
	 */
	private static int getDuration() {
		Stack<Object> stack = b.getCurrentWorkFrame();
		Object obj = stack.peek();
		if (Simulator.DEBUG)
			System.out.println(b.printStack() + "...");
		if (obj instanceof WorkFrame)
			throw new RuntimeException("***ERROR: the next obj on stack should"
					+ "be an activity! WF_Sim.getDuration" + obj.toString());
		ActivityInstance ai = (ActivityInstance) stack.peek();
		if (ai.getActivity() instanceof CompositeActivity) {
			CompositeActivity comp = (CompositeActivity) ai.getActivity();
			if (frameInImpassed(comp))
				return -1;
			else
				throw new RuntimeException("***ERROR: push shouldn't have ended " +
					"with a composite on top");
		}
		return ai.getDuration();
	}
	
	/**
	 * Returns the next event based on the index var of the given wf
	 * public, but don't need basic, 
	 * @param wf the current workframe
	 * @return the next event, or null if finished with events
	 */
	public static Event getNextEvent(WorkFrame wf) {
		List<Event> events = wf.getEvents();
		if (events.size() == wf.getIndex()) {
			wf.removeAllVarValues();
			wf.resetIndex();
			wf.firstInstanceVarsExecuted();
			wf.removeFirstInst(); //remove current set of variables
			if (wf.getVarInstances().isEmpty()) //if no more variables, wf done
				return null;
			return getNextEvent(wf); //more variables, start this wf over with new set
		}
		else {
			return events.get(wf.getIndex());
		}
	}
	
	/**
	 * Returns the next event after finding the next wf within the composite
	 * @param comp the current composite activity
	 * @return calls getNextEvent, or null if composite activity has no wfs to add
	 */
	@SuppressWarnings("unused")
	private static WorkFrame getNextWorkframe(CompositeActivity comp) {
		if (Simulator.DEBUG)
			System.out.println("getting next wf for composite " + comp.getName());
		WorkFrame newWF = getFrameToExecute(comp.getWorkFrames());
		if (newWF == null) {
			completeComposite(comp);
			System.out.println("---------- Composite Activity " + comp.getName() + " has ended -------------");
			return null;
		}
		if (Simulator.DEBUG)	
			System.out.println("continuing composite act " + comp.getName());
		Stack<Object> stack = b.getCurrentWorkFrame();
		newWF.setLevel(comp.getLevel() + 1);
		if (Simulator.DEBUG)
			System.out.println("pushing a wf " + newWF.getName());
		newWF.setExecuted(true);
		stack.push(newWF);
		if (Simulator.DEBUG)
			System.out.println(b.printStack());
		return newWF;
	}	
	
	
	
	
	
	/**
	 * Returns the next event after finding the next wf within the composite
	 * @param comp the current composite activity
	 * @return calls getNextEvent, or null if composite activity has no wfs to add
	 */
	private static Event getNextEvent(CompositeActivity comp) {
		if (Simulator.DEBUG)
			System.out.println("getting next wf for composite " + comp.getName());
		WorkFrame newWF = getFrameToExecute(comp.getWorkFrames());
		if (newWF == null) {
			completeComposite(comp);
			return null;
		}
		Stack<Object> stack = b.getCurrentWorkFrame();
		newWF.setLevel(comp.getLevel() + 1);
		if (Simulator.DEBUG)
			System.out.println("pushing a wf " + newWF.getName());
		newWF.setExecuted(true);
		stack.push(newWF);
		if (Simulator.DEBUG)
			System.out.println(b.printStack());
		if (stack.peek() instanceof WorkFrame)
			return getNextEvent((WorkFrame) stack.peek());
		else {
			throw new RuntimeException("WF_Sim getNextEvent: shouldn't have " +
					"a composite right after another");
		}
	}
	
	/**
	 * Composite is done, remove all variables from wfs and tfs, reset index and
	 * executed vars if endcondition = nowork
	 * @param comp
	 */
	private static void completeComposite(CompositeActivity comp) {
		if (Simulator.DEBUG)	
			System.out.println("finished events for composite act " + comp.getName());
		if (!(frameInImpassed(comp)) && (comp.getEndCondition() == EndCondition.NOWORK)) {
			Set<ThoughtFrame> tfs = comp.getThoughtFrames();
			Iterator<ThoughtFrame> tfItr = tfs.iterator();
			while (tfItr.hasNext()) { //clear out all the composite's tfs
				ThoughtFrame tf = tfItr.next();
				tf.removeAllInstances();
				tf.removeAllVarValues();
				tf.removeAllPrevBindings();
				tf.setExecuted(false);
			}
			Set<WorkFrame> wfs = comp.getWorkFrames();
			Iterator<WorkFrame> wfItr = wfs.iterator();
			while (wfItr.hasNext()) { //clear out all the composite's wfs
				WorkFrame wf = wfItr.next();
				wf.removeAllInstances();
				wf.removeAllVarValues();
				wf.resetIndex();
				wf.removeAllPrevBindings();
				wf.setExecuted(false);
			}
			WorkFrame parentwf = (WorkFrame) b.getCurrentWorkFrame().get(b.getCurrentWorkFrame().size() - 2);
			if (Simulator.DEBUG)
				System.out.println("incrementing index for " + parentwf.getName());
			parentwf.incIndex();
		}
	}
	
	private static boolean frameInImpassed(CompositeActivity comp) {
		Set<WorkFrame> wfs = comp.getWorkFrames();
		Iterator<WorkFrame> wfItr = wfs.iterator();
		while (wfItr.hasNext()) {
			WorkFrame tmp = wfItr.next();
			List<Stack<Object>> impassed = b.getInterruptedFrames();
			for (int i = 0; i < impassed.size(); i++) {
				if (((WorkFrame) impassed.get(i).get(0)).getName().equals(tmp.getName()))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Subtracts timePassed from the duration of the current activity
	 * needs to be public, needs to set b
	 * @param b must be an Agent or Object_b
	 * @param timePassed must be the shortest duration to be subtracted
	 */
	public static void updateTime(Basic b, int timePassed, int clock) {
		
		WorkFrame_Sim.b = b;
		Stack<Object> stack = b.getCurrentWorkFrame();
		
		if (stack.size() <= 0) { //this may happen if agent/obj has no active wf
			return;
		}
		if (!(stack.peek() instanceof ActivityInstance))
			throw new RuntimeException("Top of the stack should hold an " +
					"activity or nothing! This is neither! WF_Sim.updateTime");
		ActivityInstance ai = (ActivityInstance) stack.peek();
		if (!(ai.getActivity() instanceof CompositeActivity)) {
			
			int oldTime = ai.getDuration();
			if (oldTime == ai.getStartDuration()) {
				Activity_Sim.performActivity(ai.getActivity(), b, "start", clock-timePassed);
			}
			int newTime = oldTime - timePassed;
			if (newTime < 0)
				throw new RuntimeException("you got the wrong min time! WF_Sim");
			else if (newTime == 0) {
				//perform activities at the end
				Activity_Sim.performActivity(ai.getActivity(), b, "end", clock); 
				stack.pop(); //done with this activity
				((WorkFrame) stack.peek()).incIndex();
				stack = concludesAfterActivity(b, stack);
				b.setCurrentWorkFrame(stack);
			}
			else {
				
				ai.setDuration(newTime);
			}
		}
	}
	/**
	 * When an activity finishes it needs to process all the concludes after it and it then stops
	 * once the workframe finishes or it finds an activity.  This was created because
	 * pushEvent(b, ca) will process all the concludes and if the workframe finishes it will then 
	 * select a new workframe and process concludes until it reaches an activity.  We don't want
	 * it to find the next workframe just yet.
	 * 
	 * @param obj
	 * @param stack
	 * @return
	 */
	public static Stack<Object> concludesAfterActivity(Basic obj, Stack<Object> stack){
		b = obj;
		WorkFrame wf = (WorkFrame) stack.peek();


		Event nextEvent = getNextEvent(wf);
		//If the workframe has finished we pop it off the stack and return
		if(nextEvent == null){
			 stack.pop();
			 return stack;
		}
		//If it is a conclude we `concludeStatement' and recurse
		else if (nextEvent instanceof Conclude) { //if event is conclude
			Conclude_Sim.concludeStatement(b, (Conclude) nextEvent, wf);
			wf.incIndex();
			concludesAfterActivity(obj, stack);
		} 
		//If we come across an activity we can simply call pushNextEvent, as we won't have
		//the issue of it executing workframes until it finds an activity
		else if (nextEvent instanceof EventActivity){
			pushNextEvent(b, null);
		}
		
		return stack;
	}
}
