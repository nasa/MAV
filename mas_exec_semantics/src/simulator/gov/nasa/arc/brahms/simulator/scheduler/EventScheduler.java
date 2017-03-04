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

import gov.nasa.arc.brahms.model.Conclude;
import gov.nasa.arc.brahms.model.activity.ActivityInstance;
import gov.nasa.arc.brahms.simulator.elems.Activity_Sim;
import gov.nasa.arc.brahms.simulator.elems.Conclude_Sim;

public class EventScheduler {
	DeltaQueue dq;
	long globalClock = 0;
	
	public EventScheduler(DeltaQueue dq) {
		this.dq = dq;	
	}
	
	public void fireEvents() {
		//while(!dq.isEmpty()) {
			DQElement elem = dq.deQueue();	
			
			//TODO: figure out the ordering issue.. where you randomize
			//which eventsequence gets to go first -- this is related jpf-mas
			//if its synchronous it does not matter becuase every steps
			// we need to determine whether its one event in that time step 
			// or all events in that time step. (my guess is the latter but
			// need to create some tests to verify) 
			
			//int size = elem.getEventList().size();
			//int fireIndex = Utils.getRandomIndex(size);
			//EventSequence eventLst = elem.getEventList().get(fireIndex);
			
			globalClock += elem.delta;
			System.out.println( "delta" + elem.delta + ","
					+ " globalClock "+ globalClock);
		
			
			for(EventSequence eventLst : elem.getEventList()) {
				//do activity
				DQEvent e = eventLst.deQueue();
				//if(e instanceof ActivityInstance) {
				//	ActivityInstance actInstance = (ActivityInstance) e;
					//System.out.println(actInstance.getActivity().getParams().toString());
				//}
				//anything within the same time is treated
				//is atomic 
				executeEvent(eventLst, e);
			
				//while(!eventLst.isEmpty() &&
				//		eventLst.getHeadExecTime() == 0) {
				//	e = eventLst.deQueue();
				//	executeEvent(eventLst, e);
					
				//}
				if(eventLst.isEmpty()) {
					if(removeFinishedEventSequence(eventLst)) {
						Scheduler.currSeq.
							remove(eventLst.getOwningBasic());
					}
				} else {
					//put the rest of the event sequence back on the delta queue
					dq.insertEventList(eventLst, eventLst.getHeadExecTime());				
				}
						
			}

	//	}

	}
	
	private boolean removeFinishedEventSequence(EventSequence eventLst) {
		return Scheduler.currSeq != null &&
				eventLst.getOwningBasic() != null && 
				Scheduler.currSeq.containsKey
				(eventLst.getOwningBasic());
	}
	
	private void executeEvent(EventSequence es, DQEvent event) {
		if(event instanceof ActivityInstance) {
			ActivityInstance actInstant = (ActivityInstance) event;
			Activity_Sim.performActivity(actInstant.getActivity(),
								es.getOwningBasic(), es.getOwningFrame());
		} else if(event instanceof Conclude) {
			Conclude_Sim.concludeStatement(es.getOwningBasic(),
								(Conclude) event, es.getOwningFrame());
		} else {
			event.doActivity();
		}
	}
	
	public long getGlobalClockValue() {
		return globalClock;
	}
}
