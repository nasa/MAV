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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLoader;
import gov.nasa.arc.atc.scenarios.week1.run1.Week1Run1;
import gov.nasa.arc.atc.scenarios.week1.run2.Week1Run2;
import gov.nasa.arc.atc.scenarios.week1.run3.Week1Run3;
import gov.nasa.arc.atc.scenarios.testconf.test1.Test1;
import gov.nasa.arc.atc.scenarios.testconf.test2.Test2;
import gov.nasa.arc.atc.scenarios.testconf.test3.Test3;
import gov.nasa.arc.atc.scenarios.testconf.test4.Test4;
import gov.nasa.arc.atc.utils.ConsoleUtils;

public class SimulationNoFXTest {

	private static final Logger LOG = Logger.getGlobal();
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ConsoleUtils.setLoggingLevel(Level.WARNING);
	}

	@Ignore
	@Test
	public void testNoFXTest1() {
		File configurationFile = new File(Test1.class.getResource("test1.properties").getPath());
		testFile(configurationFile, 50);
	}

	@Ignore
	@Test
	public void testNoFXTest2() {
		File configurationFile = new File(Test2.class.getResource("test2.properties").getPath());
		testFile(configurationFile, 800);
	}

	@Ignore
	@Test
	public void testNoFXTest3() {
		File configurationFile = new File(Test3.class.getResource("test3.properties").getPath());
		testFile(configurationFile, 1500);
	}

	@Ignore
	@Test
	public void testNoFXTest4() {
		File configurationFile = new File(Test4.class.getResource("test4.properties").getPath());
		testFile(configurationFile, 1500);
	}


	@Ignore
	@Test
	public void testNoFXTestWeek1Run1() {
		File configurationFile = new File(Week1Run1.class.getResource("scenario.properties").getPath());
		testFile(configurationFile, 3500);
	}

	@Ignore
	@Test
	public void testNoFXTestWeek1Run2() {
		File configurationFile = new File(Week1Run2.class.getResource("scenario.properties").getPath());
		testFile(configurationFile, 1200);
	}


	@Ignore
	@Test
	public void testNoFXTestWeek1Run3() {
		File configurationFile = new File(Week1Run3.class.getResource("scenario.properties").getPath());
		testFile(configurationFile, 1200);
	}

	public static void testFile(File file, int duration) {
		//TODO fix class
		System.err.println("TODO");
//		// setting formatter
//		LOG.setUseParentHandlers(false);
//		CustomRecordFormatter formatter = new CustomRecordFormatter();
//		ConsoleHandler consoleHandler = new ConsoleHandler();
//		consoleHandler.setFormatter(formatter);
//		Handler[] handlers = LOG.getHandlers();
//		for (int i = 0; i < handlers.length; i++) {
//			LOG.removeHandler(handlers[i]);
//		}
//		consoleHandler.setLevel(Level.ALL);
//		LOG.addHandler(consoleHandler);
//		LOG.log(Level.CONFIG, "LOG Level is {0}", Level.ALL);
//		//
//		SimulationConfigurationLoader.loadConfiguration(file);
//		SimulationContext context = SimulationConfigurationLoader.getSimulationContext();
//		//
//		LOG.log(Level.INFO, "SimulationContext is {0}", context);
//		LOG.log(Level.INFO, "context.getGeography() is {0}", context.getGeography());
//		LOG.log(Level.INFO, "context.getGeography() arrival nodes is {0}", context.getGeography().getArrivalNodes());
//		//
//		SimulationNoFX simulation;
//		simulation = new SimulationNoFX(context);
//		assertNotNull(simulation);
//		//
//		for(ATCNode node : context.getArrivalSequences().keySet()){
//			TSS tss = new TSS(new DistanceSeparationFunction(node));
//			tss.initializeData(context, node);
//			simulation.addAlgorithm(tss);
//		}
//		//
//		simulation.setSimulationNbSteps(duration);
//		simulation.runSimulation();
//		//
//
//		context.getArrivalSequences().forEach((node, sequence) -> {
//			assertEquals(node, sequence.getArrivalNode());
//			// System.err.println("Node: " + node);
//			// System.err.println(" > sequence size= " + sequence.getSimulatedTrajectories().size());
//
//			for (SimulatedTrajectory trajectory : sequence.getSimulatedTrajectories()) {
//				// System.err.println(" >> trajectory: " + trajectory);
//				for (SimulatedTrajectory t2 : sequence.getSimulatedTrajectories()) {
//					// not optimized
//					if (trajectory != t2) {
//						// System.err.println(" >>> Comparing with " + t2);
//						Object result = TrajectoryChecker.checkHorizontalSeparation(node, trajectory, t2);
//						 System.err.println(" >>> ... result is " + result);
//						assertEquals(true, result);
//					}
//				}
//			}
//
//		});
	}

}
