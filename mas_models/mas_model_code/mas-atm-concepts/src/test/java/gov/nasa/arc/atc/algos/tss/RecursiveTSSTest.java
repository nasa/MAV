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

package gov.nasa.arc.atc.algos.tss;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.algos.BasicSequencer;
import gov.nasa.arc.atc.simulation.SimulationClock;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.algos.SimulationContextTest;
import gov.nasa.arc.atc.functions.DistanceSeparationFunction;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCGeographyTest;
import gov.nasa.arc.atc.geography.ATCNode;

public class RecursiveTSSTest {

	private static final int DEFAULT_SIM_DURATION = 1200;
	
	private static TSS instance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Ignore
	@Test
	public void test1() {
		// instance= new RecursiveTSS();
		// //
		// SimulationClock clock = new SimulationClock();
		// ATCGeography geography = ATCGeographyTest.createTestGeography1();
		// SimulationContext context =
		// SimulationContextTest.createContext1(clock,geography);
		// //
		// clock.setTime(4);
		// context.updateActiveSlots();
		// //
		// instance.initiliaze(context);
		// instance.computeSlots(4);
		// List<ATCNode> arrivalNodes
		// =instance.getGeography().getArrivalNodes();
		// assertEquals(1, arrivalNodes.size());
		// System.err.println("ARRIVAL NODE : "+arrivalNodes.get(0));

		// ********

		// List<TimedArrival> arrivals =
		// instance.getArrivalSequenceAt(arrivalNodes.get(0));
		// assertEquals(2, arrivals.size());
		// int time1 = arrivals.get(0).getInitialArrivalTime();
		// int time2 = arrivals.get(1).getInitialArrivalTime();
		// assertEquals(arrivals.get(1).getAFO().getStartTime()-arrivals.get(0).getAFO().getStartTime(),
		// time2-time1);

	}

	@Ignore
	@Test
	public void testGetArrivalSequenceAt() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void test2() {
		//
		ATCGeography geography = ATCGeographyTest.createTestGeography1();
		instance = new TSS(new DistanceSeparationFunction(geography.getAirports().get(0)));
		SimulationContext context = SimulationContextTest.createContext2(geography);
		SimulationClock clock = context.getClock();
		//
		Map<ATCNode, ArrivalSequence> arrivalSequence = new HashMap<>();
		context.getGeography().getArrivalNodes().forEach(node -> arrivalSequence.put(node, BasicSequencer.createArrivalSequence(geography,node, context,DEFAULT_SIM_DURATION)));
		//
		instance.initializeData(context, arrivalSequence.keySet().iterator().next());
		//

		for (int i = 0; i < 15; i++) {

			System.err.println(" *******************START LOOP " + i + "************* ");
			System.err.println("  -> setting time " + i);
			clock.setTime(i);
			System.err.println("  -> updating active slots");
			final boolean newSLot = context.updateActiveSlots();
			System.err.println("  ---> result: " + newSLot);
			if (newSLot) {
				context.getArrivalSequences().forEach((node, arrival) -> {
					System.err.println("  --> at " + node + "   nb slots =" + arrival.getSimulatedTrajectories().size());
					// arrival.geSimulatedTrajectories().stream().forEach(trj -> {
					// System.err.println(" ----> " + trj);
					// });

				});
				// , context.getArrivalSequences()
				instance.execute(i);
			}

			System.err.println(" *******************END LOOP " + i + "************* ");
			System.err.println(" \n\n ");
		}

		// List<TimedArrival> arrivals =
		// instance.getArrivalSequenceAt(arrivalNodes.get(0));
		// assertEquals(2, arrivals.size());
		// int time1 = arrivals.get(0).getInitialArrivalTime();
		// int time2 = arrivals.get(1).getInitialArrivalTime();
		// assertEquals(arrivals.get(1).getAFO().getStartTime()-arrivals.get(0).getAFO().getStartTime(),
		// time2-time1);

	}

}
