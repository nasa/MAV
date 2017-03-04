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

import gov.nasa.arc.brahms.model.Conclude;
import gov.nasa.arc.brahms.model.activity.ActivityInstance;
import gov.nasa.arc.brahms.model.activity.CompositeActivity;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.ThoughtFrame;
import gov.nasa.arc.brahms.model.Event;
import gov.nasa.arc.brahms.simulator.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class ThoughtFrame_Sim {
	
	public static Set<ThoughtFrame> activeThoughtFrames;

	/**
	 * Finds all thoughtframes with preconditions that have been met
	 * and sends them to Tf_Star to select one.  Continues until there are
	 * no more active thoughtframes
	 * @param b must be an instance of either Agent or Object_b
	 * @param tfSet is the set of all tfs being looked through (can change if
	 * a composite activity is being used)
	 * @exception RuntimeException if obj is not Agent or Object_b
	 */
	public static void Set_TF(Basic b) {
		Set<ThoughtFrame> tfSet = new HashSet<ThoughtFrame>();
		Iterator<ThoughtFrame> tfItr = b.getThoughtFrames().iterator();
		while (tfItr.hasNext()) {
			tfSet.add(tfItr.next());
		}
		//add tfs in running composite activities
		Stack<Object> stack = b.getCurrentWorkFrame();
		if (stack.size() > 1) {
			for (int i = stack.size() - 1; i > 0; i--) {
				if (stack.get(i) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) stack.get(i);
					if (ai.getActivity() instanceof CompositeActivity) {
						CompositeActivity comp = (CompositeActivity) ai.getActivity();
						Iterator<ThoughtFrame> tmpItr = comp.getThoughtFrames().iterator();
						while (tmpItr.hasNext()) {
							ThoughtFrame tmp = tmpItr.next();
							tfSet.add(tmp);
						}
					}
				}
			}
		}
		activeThoughtFrames = ThoughtFrame_Sim.findActiveThoughtframes(b, tfSet);
		while(!activeThoughtFrames.isEmpty()){
				Tf_Star(b, tfSet, activeThoughtFrames);
		}
	}
	
	/**
	 * Selects the thoughtframe with the highest priority to be executed first
	 * sets "executed" variable if necessary (i.e. for repeat: false)
	 * and sends to Pop_TfStar to be executed, then finds new active tfs
	 * @param b must be an instance of either Agent or Object_b
	 * @param tfSet is the set of thoughtframes that are available to look at
	 * (not necessarily active)
	 * @param activeThoughtframes= a set of tfs whose preconditions are met
	 */
	public static void Tf_Star(Basic b, Set<ThoughtFrame> tfSet,
			Set<ThoughtFrame> activeThoughtframes) {
		int maxPri = 0;
		List<ThoughtFrame> highestPriorityFrames = new ArrayList<ThoughtFrame>();
		
		//find the thought frame with the highest priority
		for(Iterator<ThoughtFrame> tfIterator = activeThoughtframes.iterator(); 
				tfIterator.hasNext();){
			ThoughtFrame tf = tfIterator.next();
			int pri = tf.getPriority();
			if(pri == maxPri || pri > maxPri) { // if equal to max add to set
				maxPri = pri;
				highestPriorityFrames.clear();
				highestPriorityFrames.add(tf);
			} else if (pri == maxPri) {
				highestPriorityFrames.add(tf);
			}
		}
		int index =  Utils.getHighestPriorityFrameIndex(highestPriorityFrames.size());
		ThoughtFrame curr = highestPriorityFrames.get(index);
		//if(Simulator.SELECT_THOUGHTFRAMES){
		//	System.out.println("--------------------"+b.getName()+"------------------------------");
		//	System.out.println("*****Executing Thoughtframe " + curr.getName() + "*****");
		//	System.out.println("--------------------------------------------------");
		//}
		resetVarsNotInUse(b, curr);
		String rp = curr.getRepeat();
		if (rp.equals("true")) //"executed" doesn't matter if true
			ThoughtFrame_Sim.Pop_TfStar(b, curr);
		else { //repeat: false;
			b.removeThoughtFrame(curr);
			ThoughtFrame_Sim.Pop_TfStar(b, curr); //execute it
		}
		
		if(!curr.hasVarInstances())
			activeThoughtFrames.remove(curr);
	//	activeThoughtFrames = ThoughtFrame_Sim.findActiveThoughtframes(b, tfSet);
	}
	
	public static void resetVarsNotInUse(Basic b, ThoughtFrame curr) {
		Set<ThoughtFrame> allTfs = b.getThoughtFrames();	
		Iterator<ThoughtFrame> tfIterator = allTfs.iterator();
		while (tfIterator.hasNext()){ //check agents for tfs
			ThoughtFrame tf = tfIterator.next();
			if (curr != tf)
				tf.removeAllVarValues();
		}
	}
	
	/**
	 * Sends to ActiveFrame.isFrameActive to check the preconditions to
	 * determine if each tf is active, returns the set of active tfs
	 * @param b is a basic concept - should be an agent or object_b
	 * @param tfSet is the set of available (not active) tfs to look through
	 * @return the set of active thought frames
	 */
	public static Set<ThoughtFrame> findActiveThoughtframes (Basic b,
			Set<ThoughtFrame> tfSet){
		//if(Simulator.SELECTFRAME)
		//	System.out.println("****Finding active Thoughtframes****");
		Set<ThoughtFrame> activeFrames = new HashSet<ThoughtFrame>();
		for (ThoughtFrame tf : tfSet) {
			if(ActiveFrame.isFrameActive(b, tf)) 
				activeFrames.add(tf);
		}
		return activeFrames;
	}

	/**
	 * Gets the old belief, type checks, and concludes
	 * @param obj must be an instance of Agent or Object_b
	 * @param currentThoughtFrame is the frame being executed
	 */
	public static void Pop_TfStar(Basic b, ThoughtFrame currentThoughtFrame) {
	//	if (Simulator.DEBUG)
	//		System.out.println("*Current TF is: " + b.getName() + "." +
	//				currentThoughtFrame.getName());
		List<Event> events = currentThoughtFrame.getEvents();
		for (int index = 0; index < events.size(); index++) {
			Conclude c = (Conclude) events.get(index);
			Conclude_Sim.concludeStatement(b, c, currentThoughtFrame);
		}
		currentThoughtFrame.firstInstanceVarsExecuted();
		currentThoughtFrame.removeFirstInst();
		currentThoughtFrame.setExecuted(true);
		currentThoughtFrame.removeAllVarValues();
	}
}
