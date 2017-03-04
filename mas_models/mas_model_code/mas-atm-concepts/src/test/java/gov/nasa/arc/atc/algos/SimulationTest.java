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

package gov.nasa.arc.atc.algos;

import org.junit.Test;

//import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLoader;


/**
 * 
 * @author ahamon
 *
 */
public class SimulationTest {

	@Test
	public void todoTest(){
		//TODO fix this class
	}
//
//	private static final Logger LOG = Logger.getGlobal();
//
//	@BeforeClass
//	public static void initFX() {
//		LOG.setUseParentHandlers(false);
//		CustomRecordFormatter formatter = new CustomRecordFormatter();
//		ConsoleHandler consoleHandler = new ConsoleHandler();
//		consoleHandler.setFormatter(formatter);
//		Handler[] handlers = LOG.getHandlers();
//		for (int i = 0; i < handlers.length; i++) {
//			LOG.removeHandler(handlers[i]);
//		}
//		consoleHandler.setLevel(Level.FINE);
//		LOG.addHandler(consoleHandler);
//		LOG.log(Level.CONFIG, "LOG Level is {0}", Level.ALL);
//
//		JFXPanel jfxPanel = new JFXPanel();
//		assertNotNull(jfxPanel);
//	}
//
//	@Ignore
//	@Test
//	public void test1() {
//		File configFile = new File(Test1.class.getResource("test1.properties").getPath());
//		SimulationConfigurationLoader.loadConfiguration(configFile);
//		SimulationContext context = SimulationConfigurationLoader.getSimulationContext();
//		JavaStandaloneSimulation simulation;
//		simulation = new JavaStandaloneSimulation();
//		simulation.setScenario(context);
//		assertNotNull(simulation);
//		//
//		for (ATCNode node : context.getArrivalSequences().keySet()) {
//			TSS tss = new TSS(new DistanceSeparationFunction(node));
//			tss.initializeData(context, node);
//			simulation.addAlgorithm(tss);
//		}
//		// S1
//		SimulatedSlotMarker s1 = context.getSlot("S1");
//		assertNotNull(s1);
//		assertEquals(TrafficType.ARRIVAL, s1.getTrafficType());
//		System.err.println(" > S1 departs at: " + s1.getStartTime());
//		// S2
//		SimulatedSlotMarker s2 = context.getSlot("S2");
//		assertNotNull(s2);
//		assertEquals(TrafficType.ARRIVAL, s2.getTrafficType());
//		System.err.println(" > S2 departs at: " + s2.getStartTime());
//		//
//		System.err.println(" *********  BEFORE SIM ********** ");
//
//		System.err.println(" > S2 FLP: " + s2.getFlightPlan());
//		System.err.println(" > S2 FLP init: " + s2.getFlightPlan().getInitialSegment().getSegmentName());
//		System.err.println(" > S2 FLP curr: " + s2.getFlightPlan().getCurrentSegment().getSegmentName());
//
//		System.err.println(" ******************* ");
//		//
//		simulation.setSimulationNbSteps(30);
////		simulation.runSimulation();
//		// TEMP
//		try {
//			Thread.sleep(3000);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		//
//		System.err.println(" *********  AFTER SIM ********** ");
//
//		System.err.println(" > S1 departs at: " + s1.getStartTime());
//		System.err.println(" > S2 departs at: " + s2.getStartTime());
//
//		context.getArrivalSequences().forEach((node, sequence) -> {
//			assertEquals(node, sequence.getArrivalNode());
//			System.err.println("Node: " + node);
//			System.err.println("Node's name: " + node.getName());
//			System.err.println(" > sequence size= " + sequence.getSimulatedTrajectories().size());
//			if ("E".equals(node.getName())) {
//				System.err.println(" ... found E");
//				assertEquals(2, sequence.getSimulatedTrajectories().size());
//				for (SimulatedTrajectory trajectory : sequence.getSimulatedTrajectories()) {
//					System.err.println(" >> trajectory: " + trajectory);
//					for (SimulatedTrajectory t2 : sequence.getSimulatedTrajectories()) {
//						// not optimized
//						if (trajectory != t2) {
//							System.err.println(" >>> Comparing with " + t2);
//							Object result = TrajectoryChecker.checkHorizontalSeparation(node, trajectory, t2);
//							System.err.println(" >>> ... result is " + result);
//							assertEquals(true, result);
//						}
//					}
//				}
//			} else {
//				System.err.println(" ... _" + node.getName() + "_");
//			}
//
//		});
//	}
//
//	// @Test
//	public void test2() {
//		File configFile = new File(Test2.class.getResource("test2.properties").getPath());
//		basicTest(configFile);
//	}
//
//	// @Test
//	public void test3() {
//		File configFile = new File(Test3.class.getResource("test3.properties").getPath());
//		basicTest(configFile);
//	}
//
//	// @Test
//	public void test4() {
//		File configFile = new File(Test4.class.getResource("test4.properties").getPath());
//		basicTest(configFile);
//	}
//
//	public static void basicTest(File configFile) {
//		SimulationConfigurationLoader.loadConfiguration(configFile);
//		SimulationContext context = SimulationConfigurationLoader.getSimulationContext();
//		JavaStandaloneSimulation simulation;
//		simulation = new JavaStandaloneSimulation();
//		simulation.setScenario(context);
//		assertNotNull(simulation);
//		//
//		for (ATCNode node : context.getArrivalSequences().keySet()) {
//			TSS tss = new TSS(new DistanceSeparationFunction((Airport) node));
//			tss.initializeData(context, node);
//			simulation.addAlgorithm(tss);
//		}
//
//		//
//		simulation.setSimulationNbSteps(30);
////		simulation.runSimulation();
//		// TEMP
//		try {
//			Thread.sleep(3000);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		//
//
//		context.getArrivalSequences().forEach((node, sequence) -> {
//			assertEquals(node, sequence.getArrivalNode());
//			System.err.println("Node: " + node);
//			System.err.println(" > sequence size= " + sequence.getSimulatedTrajectories().size());
//
//			for (SimulatedTrajectory trajectory : sequence.getSimulatedTrajectories()) {
//				System.err.println(" >> trajectory: " + trajectory);
//				for (SimulatedTrajectory t2 : sequence.getSimulatedTrajectories()) {
//					// not optimized
//					if (trajectory != t2) {
//						System.err.println(" >>> Comparing with " + t2);
//						Object result = TrajectoryChecker.checkHorizontalSeparation(node, trajectory, t2);
//						System.err.println(" >>> ... result is " + result);
//						assertEquals(true, result);
//					}
//				}
//			}
//
//		});
//	}

}
