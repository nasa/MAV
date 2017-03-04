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

package deltaqueue;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nasa.arc.brahms.simulator.scheduler.DQEvent;
import gov.nasa.arc.brahms.simulator.scheduler.DeltaQueue;
import gov.nasa.arc.brahms.simulator.scheduler.EventScheduler;
import gov.nasa.arc.brahms.simulator.scheduler.EventSequence;

public class checkInsertion {

	@Test
	public void test00() {
		DQEvent dqEvent00 = new DQEvent(2);
		dqEvent00.setName("a0");
		DQEvent dqEvent01 = new DQEvent(0);
		dqEvent01.setName("a1");
		DQEvent dqEvent02 = new DQEvent(0);
		dqEvent02.setName("a2");
		DQEvent dqEvent03 = new DQEvent(1);
		dqEvent03.setName("a3");
		EventSequence es0 = new EventSequence(dqEvent00);
		es0.addDQEvent(dqEvent01);
		es0.addDQEvent(dqEvent02);
		es0.addDQEvent(dqEvent03);
		DeltaQueue dq = new DeltaQueue();
		dq.insertEventList(es0, es0.getHeadExecTime());
		EventScheduler eventScheduler = new EventScheduler(dq);
		while(!dq.isEmpty())
			eventScheduler.fireEvents();
		assertTrue(eventScheduler.getGlobalClockValue() == 3);
	}
	
	@Test
	public void test01() {
		/**
		 * ConcludeA0
		   ConcludeA1
		   ActivityA2(3)
		   ConcludeA3
		   ActivityA4(3)
		 */
		
		DQEvent dqEvent00 = new DQEvent(0);
		dqEvent00.setName("a0");
		DQEvent dqEvent01 = new DQEvent(0);
		dqEvent01.setName("a1");
		DQEvent dqEvent02 = new DQEvent(3);
		dqEvent02.setName("a2");
		DQEvent dqEvent03 = new DQEvent(0);
		dqEvent03.setName("a3");
		DQEvent dqEvent04 = new DQEvent(3);
		dqEvent04.setName("a4");

		EventSequence es0 = new EventSequence(dqEvent00);
		es0.addDQEvent(dqEvent01);
		es0.addDQEvent(dqEvent02);
		es0.addDQEvent(dqEvent03);
		es0.addDQEvent(dqEvent04);
		

		/**
		 * 	ConcludeB0
			ConcludeB1
			ActivityB2(2)
			ConcludeB3
			ActivityB4(5)
		 */
		DQEvent dqEvent10 = new DQEvent(0);
		dqEvent10.setName("b0");
		DQEvent dqEvent11 = new DQEvent(0);
		dqEvent11.setName("b1");
		DQEvent dqEvent12 = new DQEvent(2);
		dqEvent12.setName("b2");
		DQEvent dqEvent13 = new DQEvent(0);
		dqEvent13.setName("b3");
		DQEvent dqEvent14 = new DQEvent(5);
		dqEvent14.setName("b4");
		EventSequence es1 = new EventSequence(dqEvent10);
		es1.addDQEvent(dqEvent11);
		es1.addDQEvent(dqEvent12);
		es1.addDQEvent(dqEvent13);
		es1.addDQEvent(dqEvent14);

		
		DeltaQueue dq = new DeltaQueue();
		dq.insertEventList(es0, es0.getHeadExecTime());
		dq.insertEventList(es1, es1.getHeadExecTime());
		
		EventScheduler eventScheduler = new EventScheduler(dq);
		while(!dq.isEmpty())
			eventScheduler.fireEvents();
		assertTrue(eventScheduler.getGlobalClockValue() == 7);
	}

	@Test
	public void test02() {
		/**
		 * ConcludeA0
		   ConcludeA1
		   ActivityA2(4)
		   ConcludeA3
		   ActivityA4(3)
		 */
		
		DQEvent dqEvent00 = new DQEvent(0);
		dqEvent00.setName("a0");
		DQEvent dqEvent01 = new DQEvent(0);
		dqEvent01.setName("a1");
		DQEvent dqEvent02 = new DQEvent(4);
		dqEvent02.setName("a2");
		DQEvent dqEvent03 = new DQEvent(0);
		dqEvent03.setName("a3");
		DQEvent dqEvent04 = new DQEvent(3);
		dqEvent04.setName("a4");

		EventSequence es0 = new EventSequence(dqEvent00);
		es0.addDQEvent(dqEvent01);
		es0.addDQEvent(dqEvent02);
		es0.addDQEvent(dqEvent03);
		es0.addDQEvent(dqEvent04);
		

		/**
		 * 	ConcludeB0
			ConcludeB1
			ActivityB2(2)
			ConcludeB3
			ActivityB4(5)
		 */
		DQEvent dqEvent10 = new DQEvent(0);
		dqEvent10.setName("b0");
		DQEvent dqEvent11 = new DQEvent(0);
		dqEvent11.setName("b1");
		DQEvent dqEvent12 = new DQEvent(2);
		dqEvent12.setName("b2");
		DQEvent dqEvent13 = new DQEvent(0);
		dqEvent13.setName("b3");
		DQEvent dqEvent14 = new DQEvent(5);
		dqEvent14.setName("b4");
		EventSequence es1 = new EventSequence(dqEvent10);
		es1.addDQEvent(dqEvent11);
		es1.addDQEvent(dqEvent12);
		es1.addDQEvent(dqEvent13);
		es1.addDQEvent(dqEvent14);
		
		/**
		 * 	ConcludeC0
			ConcludeC1
			ActivityC2(3)
			ConcludeB3
			ActivityB4(5)
		 */
		DQEvent dqEvent20 = new DQEvent(0);
		dqEvent20.setName("c0");
		DQEvent dqEvent21 = new DQEvent(0);
		dqEvent21.setName("c1");
		DQEvent dqEvent22 = new DQEvent(3);
		dqEvent22.setName("c2");
		DQEvent dqEvent23 = new DQEvent(0);
		dqEvent23.setName("c3");
		DQEvent dqEvent24 = new DQEvent(5);
		dqEvent24.setName("c4");
		EventSequence es2 = new EventSequence(dqEvent20);
		es2.addDQEvent(dqEvent21);
		es2.addDQEvent(dqEvent22);
		es2.addDQEvent(dqEvent23);
		es2.addDQEvent(dqEvent24);
		
		

		
		DeltaQueue dq = new DeltaQueue();
		dq.insertEventList(es0, es0.getHeadExecTime());
		dq.insertEventList(es1, es1.getHeadExecTime());
		dq.insertEventList(es2, es2.getHeadExecTime());

		
		EventScheduler eventScheduler = new EventScheduler(dq);
		while(!dq.isEmpty())
			eventScheduler.fireEvents();
		assertTrue(eventScheduler.getGlobalClockValue() == 8);
	}
	
}
