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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.scenarios.testconf.test3;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.scenarios.testconf.test1.Test1;
import javafx.embed.swing.JFXPanel;

/**
 * 
 * @author hamon
 *
 */
public class Test3 {

	@BeforeClass
	public static void setUpClass() {
		JFXPanel initFXPanel = new JFXPanel();
		Logger.getGlobal().log(Level.FINE, " JFX {0}", initFXPanel);
	}

	@Test
	public void test() {

		File waypointsFile = new File(Test1.class.getResource("WayPoints.b").getPath());
		File scenarioFile = new File(Test1.class.getResource("Aircrafts.b").getPath());
		File flightSegmentsFile = new File(Test1.class.getResource("FlightSegments.b").getPath());
		//TODO fix test
//		TestData.parseData(waypointsFile, scenarioFile, flightSegmentsFile);

	}

}
