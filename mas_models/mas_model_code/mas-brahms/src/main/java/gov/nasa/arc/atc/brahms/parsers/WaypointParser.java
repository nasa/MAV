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
package gov.nasa.arc.atc.brahms.parsers;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.brahms.BrahmsInstanceInitialConfiguration;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.Waypoint;

/**
 * @author hamon
 */
public class WaypointParser {

    // Constants for Waypoints parsing

    public static final double WESTERLY_COORDINATES = -1.0;

    public static final String WAYPOINT_LATITUDE = "latitude";
    public static final String WAYPOINT_LONGITUDE = "longitude";
    public static final String WAYPOINT_IS_METERFIX = "isMeterFix";

    private static final Logger LOG = Logger.getGlobal();

    private WaypointParser() {
        // private constructor for utility class
    }

    /**
     * Generates a Map from containing all waypoints, the map key being the waypoint's name.
     *
     * @param file      the file to parse
     * @param geography the geography to populate
     */
    public static void parseWaypoints(File file, ATCGeography geography) {
        LOG.log(Level.INFO, "parseWaypoints from file: {0}", file);

        List<BrahmsInstanceInitialConfiguration> instances = BrahmsAgentInitializationParser.parseBrahmsInstances(file);

        instances.forEach(wpInstance -> {
            final double wptLat = Double.parseDouble(wpInstance.getInitialFacts().get(WAYPOINT_LATITUDE));
            final double wptLong = WESTERLY_COORDINATES * Double.parseDouble(wpInstance.getInitialFacts().get(WAYPOINT_LONGITUDE));
            final Waypoint waypoint;
            if (wpInstance.getInitialFacts().containsKey(WAYPOINT_IS_METERFIX)) {
                final boolean isMeterFix = Boolean.parseBoolean(wpInstance.getInitialFacts().get(WAYPOINT_IS_METERFIX));
                waypoint = new Waypoint(wpInstance.getName(), wptLat, wptLong, isMeterFix);
            } else {
                waypoint = new Waypoint(wpInstance.getName(), wptLat, wptLong);
            }
            geography.addWaypoint(waypoint);
        });

        LOG.log(Level.INFO, "END parseWaypoints, found {0} waypoints", geography.getWaypoints().size());

    }

}
