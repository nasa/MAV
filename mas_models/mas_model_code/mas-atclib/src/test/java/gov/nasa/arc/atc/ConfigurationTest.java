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

package gov.nasa.arc.atc;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.scenarios.testconf.baseconf.TestConfig;
import gov.nasa.arc.atc.scenarios.week1.run1.Week1Run1;
import gov.nasa.arc.atc.scenarios.week1.run2.Week1Run2;
import gov.nasa.arc.atc.scenarios.week1.run3.Week1Run3;
import gov.nasa.arc.atc.scenarios.week1.run4.Week1Run4;
import gov.nasa.arc.atc.scenarios.week1.run5.Week1Run5;
import gov.nasa.arc.atc.scenarios.week1.run6.Week1Run6;
import gov.nasa.arc.atc.scenarios.week2.run2.Week2Run2;
import gov.nasa.arc.atc.scenarios.week2.run3.Week2Run3;
import gov.nasa.arc.atc.scenarios.week2.run4.Week2Run4;
import gov.nasa.arc.atc.scenarios.week2.run6.Week2Run6;
import gov.nasa.arc.atc.scenarios.week2.run5.Week2Run5;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * The goal of this testing class is to verify that the default simulation property files in the TestConfig package and each dsas sceanrio package are valid
 * 
 * @author ahamon
 *
 */
public class ConfigurationTest {

	private static final Logger LOG = Logger.getGlobal();

	@BeforeClass
	public static void setUpClass() {
		ConsoleUtils.setLoggingLevel(Level.INFO);
		System.err.println("TEMP setting up Configuration Test class");
	}

	@Test
	public void testBasicConfigRead() {
		LOG.log(Level.INFO, "testBasicConfigRead ...");
		File configFile = new File(TestConfig.class.getResource("simu.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		String airports = properties.getProperty("airports");
		String waypoints = properties.getProperty("waypoints");
		String aircrafts = properties.getProperty("aircrafts");
		String flightsegments = properties.getProperty("flightsegments");
		String flightplans = properties.getProperty("flightplans");
		//
		assertTrue(airports.endsWith(".b"));
		assertTrue(waypoints.endsWith(".b"));
		assertTrue(aircrafts.endsWith(".b"));
		assertTrue(flightsegments.endsWith(".b"));
		assertTrue(flightplans.endsWith(".b"));
		LOG.log(Level.SEVERE, "testBasicConfigRead ... ended OK");
	}

	@Test
	public void testBasicConfigWrite() {
		LOG.log(Level.INFO, "testBasicConfigWrite ...");
		File configFile = new File(TestConfig.class.getResource("simu.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		Configuration.writeConfigurationFile(properties, configFile.getPath());
		LOG.log(Level.SEVERE, "testBasicConfigWrite ... ended OK");
	}

	@Test
	public void testScenarioW1R1() {
		File configFile = new File(Week1Run1.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW1R2() {
		File configFile = new File(Week1Run2.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW1R3() {
		File configFile = new File(Week1Run3.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW1R4() {
		File configFile = new File(Week1Run4.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW1R5() {
		File configFile = new File(Week1Run5.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW1R6() {
		File configFile = new File(Week1Run6.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW2R2() {
		File configFile = new File(Week2Run2.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW2R3() {
		File configFile = new File(Week2Run3.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW2R4() {
		File configFile = new File(Week2Run4.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW2R5() {
		File configFile = new File(Week2Run5.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

	@Test
	public void testScenarioW2R6() {
		File configFile = new File(Week2Run6.class.getResource("scenario.properties").getPath());
		Properties properties = Configuration.readConfigurationFile(configFile);
		assertNotNull(properties);
	}

}
