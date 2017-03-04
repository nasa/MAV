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

package gov.nasa.arc.atc.factories;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLoader;
import gov.nasa.arc.atc.geography.Route;
import gov.nasa.arc.atc.scenarios.week1.run1.Week1Run1;
import gov.nasa.arc.atc.scenarios.week1.run2.Week1Run2;
import gov.nasa.arc.atc.scenarios.week1.run3.Week1Run3;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * 
 * @author ahamon
 *
 */
public class RouteFactoryTest {

	@BeforeClass
	public static void initTestClass() {
		ConsoleUtils.setLoggingLevel(Level.OFF);
	}

	@Ignore
	@Test
	public void testCreateArrivalRoutesW1R1() {
		File configurationFile = new File(Week1Run1.class.getResource("scenario.properties").getPath());
		List<Route> routes = testSimulationConfiguration(configurationFile);
		System.err.println("******** Week 1 Run 1 *******");
		int i = 0;
		for (Route route : routes) {
			i++;
			System.err.println(" " + i + " " + route);
		}
		System.err.println("******** END Week 1 Run 1 *******");
		assertEquals(17, routes.size());
	}

	@Ignore
	@Test
	public void testCreateArrivalRoutesW1R2() {
		File configurationFile = new File(Week1Run2.class.getResource("scenario.properties").getPath());
		List<Route> routes = testSimulationConfiguration(configurationFile);
		System.err.println("******** Week 1 Run 2 *******");
		for (Route route : routes) {
			System.err.println(" " + route);
		}
		System.err.println("******** END Week 1 Run 2 *******");
		assertEquals(11, routes.size());
	}

	@Ignore
	@Test
	public void testCreateArrivalRoutesW1R3() {
		File configurationFile = new File(Week1Run3.class.getResource("scenario.properties").getPath());
		List<Route> routes = testSimulationConfiguration(configurationFile);
		assertEquals(17, routes.size());
	}

	private List<Route> testSimulationConfiguration(File configurationFile) {
//		// Load configuration
//		SimulationConfigurationLoader.loadConfiguration(configurationFile);
//		// Retrieve context
//		SimulationContext context = SimulationConfigurationLoader.getSimulationContext();
		//
//		ATCGeography geography = context.getGeography();
//		return RouteFactory.createArrivalRoutes(geography);
		//TODO
		return null;
	}

}
