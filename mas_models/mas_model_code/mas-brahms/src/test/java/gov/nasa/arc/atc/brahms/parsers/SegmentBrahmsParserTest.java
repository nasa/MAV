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
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.scenarios.testconf.test1.Test1;
import gov.nasa.arc.atc.scenarios.testconf.test2.Test2;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * The test class tests if segments are correctly produced from .b files by {@link WaypointParser}, {@link SegmentBrahmsParser} and {@link FlightPlanBrahmsParser}
 *
 * @author ahamon
 */
public class SegmentBrahmsParserTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ConsoleUtils.setLoggingLevel(Level.WARNING);
    }


    // @Test
    // public void testSegmentBrahmsParserTest() {
    //
    // File waypointsFile = new File(Test2.class.getResource("WayPoints.b").getPath());
    // File flightSegmentsFile = new File(Test2.class.getResource("FlightSegments.b").getPath());
    // //
    // final Map<String, Waypoint> waypointList;
    // final Map<String, FlightSegment> segments;
    // waypointList = WaypointParser.parseWaypoints(waypointsFile);
    // for (String wayPoints : waypointList.keySet()) {
    // System.out.println("WP: " + wayPoints + " -> " + waypointList.get(wayPoints));
    // }
    // System.out.println("stating parsing segments");
    // segments = SegmentBrahmsParser.parseSegments(waypointList, flightSegmentsFile);
    // assertNotNull(segments);
    // }

    @Test
    public void testParseSegments1() {
        testExample(Test1.class);
    }

    @Test
    public void testParseSegments2() {
        testExample(Test2.class);
    }

    @SuppressWarnings("rawtypes")
    private static void testExample(Class exampleClass) {
        File waypointsFile = new File(exampleClass.getResource("WayPoints.b").getPath());
        File flightSegmentsFile = new File(exampleClass.getResource("FlightSegments.b").getPath());
        File flightPlanFile = new File(exampleClass.getResource("FlightPlans.b").getPath());
        //
        final Map<String, FlightSegment> segments;
        final Map<String, BrahmsFlightPlan> brahmsFlightPlans;
        ATCGeography geography = new ATCGeography("testSegments");
        WaypointParser.parseWaypoints(waypointsFile, geography);
        segments = SegmentBrahmsParser.parseSegments(flightSegmentsFile, geography);
        brahmsFlightPlans = FlightPlanBrahmsParser.parseBrahmsFPLs(flightPlanFile, segments);
        testBFPL2FPLSimpleConversion(brahmsFlightPlans);
    }

    private static void testBFPL2FPLSimpleConversion(Map<String, BrahmsFlightPlan> brahmsFlightPlans) {
        brahmsFlightPlans.forEach((name, bFPL) -> {
            FlightPlan fpl = new FlightPlan(name);
            bFPL.getSegmentIDs().values().forEach(fpl::addSegment);
            assertTrue(fpl.isValid());
        });
    }

}
