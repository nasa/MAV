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
import java.util.Map;
import java.util.logging.Level;

import gov.nasa.arc.atc.geography.ATCGeography;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.brahms.BrahmsFlightPlan;
import gov.nasa.arc.atc.brahms.parsers.FlightPlanBrahmsParser;
import gov.nasa.arc.atc.brahms.parsers.SegmentBrahmsParser;
import gov.nasa.arc.atc.brahms.parsers.WaypointParser;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Waypoint;
import gov.nasa.arc.atc.scenarios.week1.run1.Week1Run1;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * @author ahamon
 */
public class FlightPlanBrahmsParserTest {
    @BeforeClass
    public static void intiTestClass() {
        ConsoleUtils.setLoggingLevel(Level.OFF);
    }

    @Test
    public void testParseBrahmsFPLs() {
        Map<String, FlightSegment> flightSegments;
        Map<String, BrahmsFlightPlan> result;
        File waypointsFile = new File(Week1Run1.class.getResource("allwaypoints.b").getPath());
        File flightSegmentsFile = new File(Week1Run1.class.getResource("allFlightSegments.b").getPath());
        File fplsFile = new File(Week1Run1.class.getResource("allFlightPlans.b").getPath());

        ATCGeography geography = new ATCGeography("testParseBrahmsFPLs");
        WaypointParser.parseWaypoints(waypointsFile, geography);
        flightSegments = SegmentBrahmsParser.parseSegments(flightSegmentsFile, geography);
        result = FlightPlanBrahmsParser.parseBrahmsFPLs(fplsFile, flightSegments);
        //
        assertNotNull(result);
        // TODO: proper unit testing
    }

}
