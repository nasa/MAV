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

package gov.nasa.arc.atc.brahms.parsers;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.brahms.BrahmsInstanceInitialConfiguration;
import gov.nasa.arc.atc.scenarios.week2.run6.Week2Run6;

public class BrahmsAgentInitializationParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testW2R6waypoitns() {
		File waypointsFile = new File(Week2Run6.class.getResource("allwaypoints.b").getPath());
		List<BrahmsInstanceInitialConfiguration> instances = BrahmsAgentInitializationParser.parseBrahmsInstances(waypointsFile);
		assertEquals(75, instances.size());
		instances.stream().forEach(instance -> {
			assertEquals("waypoint", instance.getClassName());
			checkBeliefsFactsmatch(instance);
		});

	}

	@Test
	public void testW2R6airports() {
		File airportsFile = new File(Week2Run6.class.getResource("Airports.b").getPath());
		List<BrahmsInstanceInitialConfiguration> instances = BrahmsAgentInitializationParser.parseBrahmsInstances(airportsFile);
		assertEquals(5, instances.size());
		instances.stream().forEach(instance -> {
			checkBeliefsFactsmatch(instance);
		});
	}

	@Test
	public void testW2R6aircrafts() {
		File airplanes = new File(Week2Run6.class.getResource("allAirplanes.b").getPath());
		List<BrahmsInstanceInitialConfiguration> instances = BrahmsAgentInitializationParser.parseBrahmsInstances(airplanes);
		assertEquals(104, instances.size());
		instances.stream().forEach(instance -> {
			assertEquals("Airplane", instance.getClassName());
			checkBeliefsFactsmatch(instance);
		});
	}

	
	private void checkBeliefsFactsmatch(BrahmsInstanceInitialConfiguration instance){
		instance.getInitialBeliefs().forEach((name,value)->assertEquals(instance.getInitialFacts().get(name), value));
	}
}
