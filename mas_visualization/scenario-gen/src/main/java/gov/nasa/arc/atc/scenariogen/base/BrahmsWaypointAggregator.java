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

package gov.nasa.arc.atc.scenariogen.base;

import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLoader;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run1.Week1Run1;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run2.Week1Run2;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run3.Week1Run3;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run4.Week1Run4;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run5.Week1Run5;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run6.Week1Run6;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run2.Week2Run2;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run3.Week2Run3;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run4.Week2Run4;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run5.Week2Run5;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run6.Week2Run6;
import gov.nasa.arc.atc.utils.ConsoleUtils;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

/**
 * @author ahamon
 */
public class BrahmsWaypointAggregator {


    private static final Map<String, ATCNode> NODES = new HashMap<>();


    public static void main(String[] args) {
        ConsoleUtils.setLoggingLevel(Level.SEVERE);
        aggregate();
    }

    public static Map<String, ATCNode> getNODES() {
        return Collections.unmodifiableMap(NODES);
    }

    @SuppressWarnings("rawtypes")
    public static void aggregate() {
        long startT = System.currentTimeMillis();
        //
        List<Class> classes = new LinkedList<>();
        // week 1
        classes.add(Week1Run1.class);
        classes.add(Week1Run2.class);
        classes.add(Week1Run3.class);
        classes.add(Week1Run4.class);
        classes.add(Week1Run5.class);
        classes.add(Week1Run6.class);
        // week 2
        classes.add(Week2Run2.class);
        classes.add(Week2Run3.class);
        classes.add(Week2Run4.class);
        classes.add(Week2Run5.class);
        classes.add(Week2Run6.class);

        for (Class c : classes) {
            String path = c.getResource("scenario.properties").getPath();
            File file = new File(path);
            SimulationConfigurationLoader.loadConfiguration(file);
            SimulationConfigurationLoader.getSimulationContext().getGeography().getWaypoints().forEach(node -> {
                if (!NODES.containsKey(node.getName())) {
                    NODES.put(node.getName(), node);
                }
            });
        }

        // Time debug
        long endT = System.currentTimeMillis();
        System.err.println(" execution time => " + (endT - startT));
        System.err.println(" => found " + NODES.size() + " nodes");

    }

}
