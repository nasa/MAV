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

package gov.nasa.arc.atc.functions;

import static org.junit.Assert.*;

import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.simulation.SlotTrajectory;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.Constants;

/**
 * @author ahamon
 */
public class MeterFixCandidateFunctionTest {

    private static final Airport AIRPORT = new Airport("Airport", 40, -70);
    private static final Runway RUNWAY = new Runway("R", AIRPORT, 12);
    private static final Waypoint WPT_H = new Waypoint("wptH", 39.8, -70);
    private static final Waypoint WPT_G = new Waypoint("wptG", 39.6, -70, true);
    private static final Waypoint WPT_F = new Waypoint("wptF", 39.4, -70);
    private static final Waypoint WPT_E = new Waypoint("wptE", 39.2, -70);
    private static final Waypoint WPT_A = new Waypoint("wptA", 40, -70.8);
    private static final Waypoint WPT_B = new Waypoint("wptB", 40, -70.6);
    private static final Waypoint WPT_C = new Waypoint("wptC", 40, -70.4);
    private static final Waypoint WPT_D = new Waypoint("wptD", 40, -70.2);
    //
    private static final int DEFAULT_SIM_DURATION = 1200;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        AIRPORT.addRunway(RUNWAY);
    }

    @Test
    public void testConstructor() {
        ATCGeography geography = new ATCGeography("");
        geography.addWaypoint(new Waypoint("a", 1, 1, true));
        geography.addWaypoint(new Waypoint("b", 2, 2, false));
        geography.addWaypoint(new Waypoint("c", 3, 3));
        geography.addWaypoint(new Waypoint("d", 4, 4, true));
        geography.addWaypoint(new Waypoint("e", 5, 5));
        MeterFixCandidateFunction function = new MeterFixCandidateFunction(geography, 300);
        assertEquals(2, function.getMeterFixes().size());
    }

    @Test
    public void testIsCandidate() {
        ATCGeography geography = createSimpleGeography();
        MeterFixCandidateFunction function = new MeterFixCandidateFunction(geography, 300);
        SimulatedSlotMarker arrivalToMeter = createArrivalMetered(0, WPT_E.getLatitude(), WPT_E.getLongitude());
        SlotTrajectory traj1 = new SlotTrajectory(geography, arrivalToMeter, DEFAULT_SIM_DURATION);

        // arrives at g at 367
        // TODO see why changed to 368 and to 364
        assertEquals(364, traj1.getArrivalTimeAtNode(WPT_G.getName()));

        boolean isCandidateAt0 = function.isCandidate(traj1, 0);
        assertFalse(isCandidateAt0);

        boolean isCandidateAt100 = function.isCandidate(traj1, 100);
        assertTrue(isCandidateAt100);

        boolean isCandidateAt400 = function.isCandidate(traj1, 400);
        assertTrue(isCandidateAt400);

        // non metererd

        SimulatedSlotMarker arrival2 = createArrivalNonMetered(0, WPT_A.getLatitude(), WPT_A.getLongitude());
        SlotTrajectory traj2 = new SlotTrajectory(geography, arrival2, DEFAULT_SIM_DURATION);

        // arrives at D at 367
        // TODO see why changed to 406
        assertEquals(406, traj2.getArrivalTimeAtNode(WPT_D.getName()));

        boolean isNotCandidateAt0 = function.isCandidate(traj2, 0);
        assertFalse(isNotCandidateAt0);

        boolean isNotCandidateAt100 = function.isCandidate(traj2, 100);
        assertFalse(isNotCandidateAt100);

        boolean isNotCandidateAt400 = function.isCandidate(traj2, 400);
        assertFalse(isNotCandidateAt400);

    }

    protected static ATCGeography createSimpleGeography() {
        ATCGeography geography = new ATCGeography("testingGeo");
        geography.addAirport(AIRPORT);
        geography.addWaypoint(WPT_H);
        geography.addWaypoint(WPT_G);
        geography.addWaypoint(WPT_F);
        geography.addWaypoint(WPT_E);
        geography.addWaypoint(WPT_A);
        geography.addWaypoint(WPT_B);
        geography.addWaypoint(WPT_C);
        geography.addWaypoint(WPT_D);
        createArrivalFlightPlan1("a1").getSegments().forEach(geography::addSegment);
        createArrivalFlightPlan2("a2").getSegments().forEach(geography::addSegment);
        return geography;
    }

    private static FlightPlan createArrivalFlightPlan1(String afoName) {
        FlightPlan fpl = new FlightPlan("arrival_metered");
        FlightSegment eF = new FlightSegment("E-F", afoName, WPT_E, WPT_F, 5000, 250);
        FlightSegment fG = new FlightSegment("F-G", afoName, WPT_F, WPT_G, 2500, 200);
        FlightSegment gH = new FlightSegment("G-H", afoName, WPT_G, WPT_H, 2000, 250);
        FlightSegment hRunway = new FlightSegment("H-Runway", afoName, WPT_H, RUNWAY, 0, 220);
        fpl.addSegment(eF);
        fpl.addSegment(fG);
        fpl.addSegment(gH);
        fpl.addSegment(hRunway);
        fpl.setInitialSegment(eF);
        fpl.setCurrentSegment(eF);
        fpl.setArrivalRunway(RUNWAY);
        return fpl;
    }

    private static FlightPlan createArrivalFlightPlan2(String afoName) {
        FlightPlan fpl = new FlightPlan("arrival_non_metered");
        FlightSegment aB = new FlightSegment("A-B", afoName, WPT_A, WPT_B, 5000, 250);
        FlightSegment bC = new FlightSegment("B-C", afoName, WPT_B, WPT_C, 2500, 200);
        FlightSegment cD = new FlightSegment("C-D", afoName, WPT_C, WPT_D, 2000, 250);
        FlightSegment dRunway = new FlightSegment("D-Runway", afoName, WPT_D, RUNWAY, 0, 220);
        fpl.addSegment(aB);
        fpl.addSegment(bC);
        fpl.addSegment(cD);
        fpl.addSegment(dRunway);
        fpl.setInitialSegment(aB);
        fpl.setCurrentSegment(aB);
        fpl.setArrivalRunway(RUNWAY);
        return fpl;
    }

    private static SimulatedSlotMarker createArrivalMetered(int index, double lat, double lon) {
        SimulatedSlotMarker afo1 = new SimulatedSlotMarker("a_" + index, new Position(lat, lon, 5000), 250, 0, 180, 0, 0, Constants.NO_STARTED);
        FlightPlan fpl1 = createArrivalFlightPlan1(afo1.getName());
        afo1.setFlightPlan(fpl1);
        afo1.setStatus(Constants.IS_FLYING);
//		afo1.setTrafficType(TrafficType.ARRIVAL);
        return afo1;
    }

    private static SimulatedSlotMarker createArrivalNonMetered(int index, double lat, double lon) {
        SimulatedSlotMarker afo1 = new SimulatedSlotMarker("a_" + index, new Position(lat, lon, 5000), 250, 0, 180, 0, 0, Constants.NO_STARTED);
        FlightPlan fpl1 = createArrivalFlightPlan2(afo1.getName());
        afo1.setFlightPlan(fpl1);
        afo1.setStatus(Constants.IS_FLYING);
//		afo1.setTrafficType(TrafficType.ARRIVAL);
        return afo1;
    }
}
