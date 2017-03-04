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


public class DeltaQueue {

	 DQElement head;
	
	public void insertEventList(EventSequence eventLst, long time) {
		
		if(head == null) {
			head = new DQElement(eventLst, time);
			return;
		}
		
		DQElement prev = null;
		DQElement curr = head;
		
		while(curr != null && time > curr.delta) {
			time -= curr.delta;
			prev = curr;
			curr = curr.nextElem;
		}
		if(curr == null) {
			insertInBetween(prev, null, eventLst, time); 
		}
		else if (time == curr.delta) {
			//add to an existing sequence
			curr.add(eventLst);
		} else {
			insertInBetween(prev, curr, eventLst, time); 
		}
	}
	
	private void insertInBetween(DQElement prev, DQElement curr,
				EventSequence eventLst, long time) {
		if(prev != null) {
			prev.nextElem = new DQElement(eventLst, time);
			if(curr != null)  {
				curr.delta -= time;
			}
			prev.nextElem.nextElem = curr;
		}
		else {
			head = new DQElement(eventLst, time);
			head.nextElem = curr;
			curr.delta -= time;
		}
	}
	
	public boolean isEmpty() {
		return (head == null);
	}
	
	public DQElement deQueue() {
		DQElement topElem = head;
		head = head.nextElem;
		return topElem;
	}
	

}
