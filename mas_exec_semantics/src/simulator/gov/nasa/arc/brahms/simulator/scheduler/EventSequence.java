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

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.concept.Basic;

public class EventSequence {
	
		List<DQEvent> eventLst;
		Basic b;
		Frame f;
		
		public EventSequence() {
			eventLst = new ArrayList<DQEvent>();
		}

		public EventSequence(DQEvent curr) {
			eventLst = new ArrayList<DQEvent>();
			eventLst.add(curr);
		}
		
		public void addDQEvent(DQEvent newEvent) {
			eventLst.add(newEvent);
		}
		
		public void setBasicAndFrame(Basic b, Frame f) {
			this.b = b;
			this.f = f;
		}
		
		public Basic getOwningBasic() {
			return b;
		}
		
		public Frame getOwningFrame() {
			return f;
		}
		
		public DQEvent getHead() {
			assert(eventLst.size() > 0);
			return eventLst.get(0);
		}
		
		public long getHeadExecTime() {
			assert(eventLst.size() > 0);
			return eventLst.get(0).execTime;
		}
		
		public DQEvent deQueue() {
			assert(eventLst.size() > 0);
			return eventLst.remove(0);
		}
		
		public boolean isEmpty() {
			return (eventLst.size() == 0);
		}
		
	
}
