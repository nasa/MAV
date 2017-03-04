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

/*
* *******************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.geography;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author hamon
 */
public class ATCGeographyTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddWPOnReturnedList() {
        ATCGeography geography = new ATCGeography("addWp");
        geography.getWaypoints().add(new Waypoint("", 0, 0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddSegmentOnReturnedList() {
        ATCGeography geography = new ATCGeography("addSeg");
        geography.getSegments().add(new FlightSegment("", "-", new Waypoint("-", 0, 0), new Waypoint("_", 0, 0), 0, 0));
    }

    @Test
    public void test() {
        ATCGeography geography = createTestGeography1();
        //
        assertEquals(11, geography.getWaypoints().size());
        assertEquals(11, geography.getSegments().size());
        //
        assertEquals(0, geography.getPreviousNodes("wpDeparture").size());
        assertEquals(2, geography.getNextNodes("wpDeparture").size());
        //
        assertEquals(2, geography.getPreviousNodes("E").size());
        assertEquals(1, geography.getNextNodes("E").size());
        //
        assertEquals(3, geography.getPreviousNodes("H").size());
        assertEquals(1, geography.getNextNodes("H").size());
        //
        assertEquals(1, geography.getPreviousNodes("LGA").size());
        assertEquals(0, geography.getNextNodes("LGA").size());
        //
        assertEquals(0, geography.getPreviousNodes("No_WP").size());
        assertEquals(0, geography.getNextNodes("No_WP").size());
        //
        List<Route> routes = geography.getPathsToArrival("LGA");
        assertEquals(4, routes.size());
    }

    public static ATCGeography createTestGeography1() {
        ATCGeography geography = new ATCGeography("Test geography");
        Airport airport = new Airport("AAA", "AAA", 37.5, -70);
        Waypoint wpStart = new Waypoint("wpDeparture", 39.5, -70);
        Waypoint wpA = new Waypoint("A", 40, -70.5);
        Waypoint wpB = new Waypoint("B", 39.5, -70.5);
        Waypoint wpC = new Waypoint("C", 40, -69.5);
        Waypoint wpD = new Waypoint("D", 39.5, -69.5);
        Waypoint wpE = new Waypoint("E", 39, -70);
        Waypoint wpF = new Waypoint("F", 39, -71);
        Waypoint wpG = new Waypoint("G", 39, -69);
        Waypoint wpH = new Waypoint("H", 38.5, -70);
        Waypoint wpI = new Waypoint("I", 38, -70);
        Runway wpArrival = new Runway("LGA", airport, 12);
        airport.addRunway(wpArrival);
        //
        geography.addAirport(airport);
        // geography.addDepartureNode(wpStart);
        geography.addWaypoint(wpStart);
        geography.addWaypoint(wpA);
        geography.addWaypoint(wpB);
        geography.addWaypoint(wpC);
        geography.addWaypoint(wpD);
        geography.addWaypoint(wpE);
        geography.addWaypoint(wpF);
        geography.addWaypoint(wpG);
        geography.addWaypoint(wpH);
        geography.addWaypoint(wpI);
        geography.addArrivalNode(wpArrival);
        //
        //
        FlightSegment segDepA = new FlightSegment("Departure_A", "testPlane", wpStart, wpA, 5000, 250);
        FlightSegment segDepC = new FlightSegment("Departure_C", "testPlane", wpStart, wpC, 5000, 250);
        FlightSegment seg1 = new FlightSegment("A_B", "testPlane", wpA, wpB, 10000, 300);
        FlightSegment seg2 = new FlightSegment("B_E", "testPlane", wpB, wpE, 10000, 350);
        FlightSegment seg3 = new FlightSegment("C_D", "testPlane", wpC, wpD, 10000, 300);
        FlightSegment seg4 = new FlightSegment("D_E", "testPlane", wpD, wpE, 10000, 350);
        FlightSegment seg5 = new FlightSegment("E_H", "testPlane", wpE, wpH, 10000, 350);
        FlightSegment seg6 = new FlightSegment("F_H", "testPlane", wpF, wpH, 10000, 350);
        FlightSegment seg7 = new FlightSegment("G_H", "testPlane", wpG, wpH, 10000, 350);
        FlightSegment seg8 = new FlightSegment("H_I", "testPlane", wpH, wpI, 5000, 300);
        FlightSegment seg9 = new FlightSegment("LGA", "testPlane", wpI, wpArrival, 0, 250);
        //
        geography.addSegment(segDepA);
        geography.addSegment(segDepC);
        geography.addSegment(seg1);
        geography.addSegment(seg2);
        geography.addSegment(seg3);
        geography.addSegment(seg4);
        geography.addSegment(seg5);
        geography.addSegment(seg6);
        geography.addSegment(seg7);
        geography.addSegment(seg8);
        geography.addSegment(seg9);
        //
        return geography;
    }

    /**
     * Tests that a waypoint corresponding the an existing runway cannot be added
     */
    @Test
    public void testAddWaypointRunway() {
        ATCGeography geography = new ATCGeography("addWptRunway");
        Airport a = new Airport("AAAA", "AAA", 10, 12, 250);
        Runway r = new Runway("Arrival", a, 15);
        a.addRunway(r);
        geography.addAirport(a);
        Waypoint arr = new Waypoint(r.getName(), r.getLatitude(), r.getLongitude());
        geography.addWaypoint(arr);
        assertEquals(1, geography.getWaypoints().size());

    }


}
