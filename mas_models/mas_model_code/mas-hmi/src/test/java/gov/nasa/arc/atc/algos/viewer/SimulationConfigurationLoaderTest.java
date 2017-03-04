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

package gov.nasa.arc.atc.algos.viewer;

import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.scenarios.week2.run6.Week2Run6;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * @author ahamon
 */
public class SimulationConfigurationLoaderTest {

    @BeforeClass
    public static void beforeClass() {
        ConsoleUtils.setLoggingLevel(Level.INFO);
    }


    @Test
    public void testWeek2run6() {
        File configurationFile = new File(Week2Run6.class.getResource("scenario.properties").getPath());
        SimulationConfigurationLoader.loadConfiguration(configurationFile);

        ATCGeography geography = SimulationConfigurationLoader.getSimulationContext().getGeography();

        SimulationConfigurationLoader.getSimulationContext().getAllSlots().forEach(slot -> {

            // testing departure node
            ATCNode depNode = slot.getFlightPlan().getFirstWaypoint();
            if (depNode != null && geography.isRunway(depNode.getName())) {
                assertTrue( depNode instanceof Runway);

            }

            // testing arrival node
            ATCNode arrNode = slot.getFlightPlan().getLastWaypoint();
            if (arrNode != null && geography.isRunway(arrNode.getName())) {
                assertTrue( arrNode instanceof Runway);
            }


        });
    }

}
