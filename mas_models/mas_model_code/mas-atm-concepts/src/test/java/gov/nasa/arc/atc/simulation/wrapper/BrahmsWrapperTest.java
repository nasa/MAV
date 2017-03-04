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

package gov.nasa.arc.atc.simulation.wrapper;

import java.io.File;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.nasa.arc.atc.airborne.impl.SlotMarkerImpl;
//import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLoader;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightPlanTest;
import gov.nasa.arc.atc.scenarios.week1.run1.Week1Run1;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * 
 * @author ahamon
 *
 */
public class BrahmsWrapperTest {

	@BeforeClass
	public static void setUp() {
		ConsoleUtils.setLoggingLevel(Level.OFF);
	}

	// @Test
//	public void testRunEmptyList() {
//		BrahmsWrapper.invokeTSS("testRunEmptyList", Collections.emptyList(), 10);
//	}

	@Test
	public void testRunArrivalList() {
		System.err.println("TODO");
		//TODO
//		ConsoleUtils.setLoggingLevel(Level.OFF);
//		AFO afoA1 = createArrivalTypeA("a1", 5);
//		AFO afoA2 = createArrivalTypeA("a2", 10);
//		AFO afoB1 = createArrivalTypeB("b1", 23);
//		AFO afoD1 = createDepartureTypeA("d1", 23);
//		List<AFO> aFOs = new ArrayList<>();
//		aFOs.add(afoA1);
//		aFOs.add(afoA2);
//		aFOs.add(afoB1);
//		aFOs.add(afoD1);
//		//
//		int simTime = 11;
//		for (int i = 1; i < simTime; i++) {
//			aFOs.stream().forEach(afo -> afo.updatePosition());
//		}
//		aFOs.stream().forEach(afo -> System.err.println(afo.getName() + " start time :: " + afo.getSimulationTime()));
//		// where to set sim time
//		BrahmsWrapper.invokeTSS("testRunArrivalList", aFOs, simTime);
//
//		assertTrue(true);
	}

	/*
	 * Testing wrong input sets
	 */
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testInErr1() {
//		ConsoleUtils.setLoggingLevel(Level.OFF);
//		final AFO afoA1 = createErr1();
//		List<AFO> aFOs = new ArrayList<>();
//		aFOs.add(afoA1);
//		BrahmsWrapper.invokeTSS("testInErr1", aFOs, 10);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testInErr2() {
//		ConsoleUtils.setLoggingLevel(Level.OFF);
//		final AFO afoA1 = createErr2();
//		List<AFO> aFOs = new ArrayList<>();
//		aFOs.add(afoA1);
//		BrahmsWrapper.invokeTSS("testInErr2", aFOs, 10);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testInErr3() {
//		ConsoleUtils.setLoggingLevel(Level.OFF);
//		final AFO afoA1 = createErr3();
//		List<AFO> aFOs = new ArrayList<>();
//		aFOs.add(afoA1);
//		BrahmsWrapper.invokeTSS("testInErr3", aFOs, 10);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testInErr4() {
//		ConsoleUtils.setLoggingLevel(Level.OFF);
//		final AFO afoA1 = createErr4();
//		List<AFO> aFOs = new ArrayList<>();
//		aFOs.add(afoA1);
//		BrahmsWrapper.invokeTSS("testInErr4", aFOs, 10);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testInErr5() {
//		ConsoleUtils.setLoggingLevel(Level.OFF);
//		final AFO afoA1 = createErr5();
//		List<AFO> aFOs = new ArrayList<>();
//		aFOs.add(afoA1);
//		BrahmsWrapper.invokeTSS("testInErr5", aFOs, 10);
//	}

	/*
	 * Testing with real scenarios
	 */
	@Ignore
	@Test
	public void testWeek1run1() {
		File configurationFile = new File(Week1Run1.class.getResource("scenario.properties").getPath());
		basicTestWithWrapper(configurationFile, 100);
	}

	/*
	 * Private factory like methods
	 */

	private static SlotMarkerImpl createArrivalTypeA(String name, int startTime) {
		final SlotMarkerImpl slot = new SlotMarkerImpl(name, 250, 0, 40, -70.5, 10000, 180,startTime);
//		slot.setStartTime(startTime);
		final FlightPlan fplA = FlightPlanTest.createTestPlanArrival1();
		slot.setFlightPlan(fplA);
//		slot.setTrafficType(TrafficType.ARRIVAL);
		return slot;
	}

	private static SlotMarkerImpl createArrivalTypeB(String name, int startTime) {
		final SlotMarkerImpl slot = new SlotMarkerImpl(name, 250, 0, 40, -70.5, 10000, 180,startTime);
//		slot.setStartTime(startTime);
		final FlightPlan fplA = FlightPlanTest.createTestPlanArrival2();
		slot.setFlightPlan(fplA);
//		slot.setTrafficType(TrafficType.ARRIVAL);
		return slot;
	}

	private static SlotMarkerImpl createDepartureTypeA(String name, int startTime) {
		final SlotMarkerImpl slot = new SlotMarkerImpl(name, 250, 0, 40, -70.5, 10000, 180,startTime);
//		slot.setStartTime(startTime);
		final FlightPlan fplA = FlightPlanTest.createTestPlanDeparture1();
		slot.setFlightPlan(fplA);
//		slot.setTrafficType(TrafficType.DEPARTURE);
		return slot;
	}

	/*
	 * Not well initialized inputs
	 */

	// name : ""
	private static SlotMarkerImpl createErr1() {
		SlotMarkerImpl slot = new SlotMarkerImpl("", 250, 0, 40, -70.5, 10000, 180,0);
//		slot.setStartTime(0);
		FlightPlan fplA = FlightPlanTest.createTestPlanArrival2();
		slot.setFlightPlan(fplA);
//		slot.setTrafficType(TrafficType.ARRIVAL);
		return slot;
	}

	// name : null
	private static SlotMarkerImpl createErr2() {
		SlotMarkerImpl slot = new SlotMarkerImpl(null, 250, 0, 40, -70.5, 10000, 180,0);
//		slot.setStartTime(0);
		FlightPlan fplA = FlightPlanTest.createTestPlanArrival2();
		slot.setFlightPlan(fplA);
//		slot.setTrafficType(TrafficType.ARRIVAL);
		return slot;
	}

	// startTime negative
	private static SlotMarkerImpl createErr3() {
		SlotMarkerImpl slot = new SlotMarkerImpl("test", 250, 0, 40, -70.5, 10000, 180,-2);
//		slot.setStartTime(-2);
		FlightPlan fplA = FlightPlanTest.createTestPlanArrival2();
		slot.setFlightPlan(fplA);
//		slot.setTrafficType(TrafficType.ARRIVAL);
		return slot;
	}

	// no flight plan
	private static SlotMarkerImpl createErr4() {
		SlotMarkerImpl slot = new SlotMarkerImpl("test", 250, 0, 40, -70.5, 10000, 180,10);
//		slot.setStartTime(10);
//		slot.setTrafficType(TrafficType.UNKNOWN);
		return slot;
	}

	// unknown traffic type
	private static SlotMarkerImpl createErr5() {
		SlotMarkerImpl slot = new SlotMarkerImpl("test", 250, 0, 40, -70.5, 10000, 180,10);
//		slot.setStartTime(10);
		FlightPlan fplA = FlightPlanTest.createTestPlanArrival2();
		slot.setFlightPlan(fplA);
//		slot.setTrafficType(TrafficType.UNKNOWN);
		return slot;
	}

	private static void basicTestWithWrapper(File configFile, int simulationDuration) {
//		SimulationConfigurationLoader.loadConfiguration(configFile);
//		SimulationContext context = SimulationConfigurationLoader.getSimulationContext();
//		System.err.println(" 0 > airports :: " + context.getGeography().getAirports());
//		context.getGeography().getAirports().forEach(airport -> {
//			System.err.println(" 01 >> airport: " + airport);
//		});
//		// context.calculateReferenceTrajectories();
//		// context.getClock().setTime(100);
//
//		List<AFO> slots = new ArrayList<>();
//		slots.addAll(context.getAllSlots());
//
//		BrahmsWrapper.invokeTSS("basicTestWithWrapper_" + configFile.getName(), slots, 100);
//		// Simulation simulation;
//		// simulation = new Simulation(context);
//		// assertNotNull(simulation);
//		// //
//		// for(ATCNode node : context.getArrivalSequences().keySet()){
//		// RecursiveTSS tss = new RecursiveTSS();
//		// tss.initiliaze(context, node);
//		// simulation.addAlgorithm(tss);
//		// }
//		//
//		// //
//		// simulation.setSimulationNbSteps(30);
//		// simulation.runSimulation();
//		// // TEMP
//		// try {
//		// Thread.sleep(3000);
//		// } catch (Exception e) {
//		// // TODO: handle exception
//		// }
//		//
//
//		// context.getArrivalSequences().forEach((node, sequence) -> {
//		// assertEquals(node, sequence.getArrivalNode());
//		// System.err.println("Node: " + node);
//		// System.err.println(" > sequence size= " + sequence.getSimulatedTrajectories().size());
//		//
//		// for (SimulatedTrajectory trajectory : sequence.getSimulatedTrajectories()) {
//		// System.err.println(" >> trajectory: " + trajectory);
//		// for (SimulatedTrajectory t2 : sequence.getSimulatedTrajectories()) {
//		// // not optimized
//		// if (trajectory != t2) {
//		// System.err.println(" >>> Comparing with " + t2);
//		// Object result = TrajectoryChecker.checkHorizontalSeparation(node, trajectory, t2);
//		// System.err.println(" >>> ... result is " + result);
//		// assertEquals(true, result);
//		// }
//		// }
//		// }
//		//
//		// });
	}

}
