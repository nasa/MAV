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

package gov.nasa.arc.atc.algos.ctop;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.Constants;

/**
 * For the geography diagram, one can read the dot or the png file in the 'resources' folder
 *
 * @author ahamon
 */
public class CTOPTestUtils {


    static final Airport AP_1 = new Airport("A_1", "A01", 40, -70);
    static final Runway A1_R1 = new Runway("A1_R1", AP_1, 12);


    static final Airport AP_2 = new Airport("A_2", "A02", 40, -70);
    static final Runway A2_R1 = new Runway("A2_R1", AP_1, 12);


    static final Airport AP_3 = new Airport("A_3", "A03", 40, -70);
    static final Runway A3_R1 = new Runway("A3_R1", AP_3, 12);


    static final Airport AP_4 = new Airport("A_4", "A04", 40, -70);
    static final Runway A4_R1 = new Runway("A4_R1", AP_4, 9);
    static final Runway A4_R2 = new Runway("A4_R2", AP_4, 18);

    static final Waypoint A = new Waypoint("A", 50, -70.4);
    static final Waypoint B = new Waypoint("B", 49.5, -70.4);
    static final Waypoint C = new Waypoint("C", 49, -70.3);
    static final Waypoint D = new Waypoint("D", 45, -70.3);
    static final Waypoint E = new Waypoint("E", 42, -70.3);
    static final Waypoint F = new Waypoint("F", 41, -70.3);
    static final Waypoint G = new Waypoint("G", 40.5, -70);


    static final Waypoint AA = new Waypoint("AA", 50, -70.2);
    static final Waypoint BB = new Waypoint("BB", 49.5, -70.2);


    static final Waypoint AAA = new Waypoint("AAA", 50, -69.3);
    static final Waypoint BBB = new Waypoint("BBB", 49.5, -69.3);
    static final Waypoint CCC = new Waypoint("CCC", 49, -69.30);
    static final Waypoint DDD = new Waypoint("DDD", 45, -69.3);
    static final Waypoint EEE = new Waypoint("EEE", 42, -69.3);
    static final Waypoint FFF = new Waypoint("FFF", 41, -69.3);


    static final Waypoint Z = new Waypoint("Z", 39.9, -69.90);
    static final Waypoint Y = new Waypoint("Y", 39.7, -69.7);

    private static ATCGeography geography = null;


    /*
     * utils methods
	 */

    protected static ATCGeography getSimpleGeography() {
        if (geography == null) {
            AP_1.addRunway(A1_R1);
            AP_2.addRunway(A2_R1);
            AP_3.addRunway(A3_R1);
            AP_4.addRunway(A4_R1);
            AP_4.addRunway(A4_R2);
            geography = new ATCGeography("testing_CTOP");
            geography.addAirport(AP_1);
            geography.addAirport(AP_2);
            geography.addAirport(AP_3);
            geography.addAirport(AP_4);
            geography.addWaypoint(A);
            geography.addWaypoint(B);
            geography.addWaypoint(C);
            geography.addWaypoint(D);
            geography.addWaypoint(E);
            geography.addWaypoint(F);
            geography.addWaypoint(G);
            geography.addWaypoint(AA);
            geography.addWaypoint(BB);
            geography.addWaypoint(AAA);
            geography.addWaypoint(BBB);
            geography.addWaypoint(CCC);
            geography.addWaypoint(DDD);
            geography.addWaypoint(EEE);
            geography.addWaypoint(FFF);
            geography.addWaypoint(Z);
            geography.addWaypoint(Y);
        }
        return geography;
    }

    static SimulatedSlotMarker createArrivalA(int index, int startTime, int depTime) {
        SimulatedSlotMarker afo = new SimulatedSlotMarker("a_" + index, new Position(AP_1.getLatitude(), AP_1.getLongitude(), 0), 280, 0, 180, startTime, depTime, 0);
        FlightPlan fpl = createFlightPlanA(afo);
        afo.setFlightPlan(fpl);
        afo.setStatus(Constants.NO_STARTED);
        return afo;
    }


    static SimulatedSlotMarker createArrivalAA(int index, int startTime, int depTime) {
        SimulatedSlotMarker afo = new SimulatedSlotMarker("aa_" + index, new Position(AP_1.getLatitude(), AP_1.getLongitude(), 0), 280, 0, 180, startTime, depTime, 0);
        FlightPlan fpl = createFlightPlanAA(afo);
        afo.setFlightPlan(fpl);
        afo.setStatus(Constants.NO_STARTED);
        return afo;
    }


    static SimulatedSlotMarker createArrivalAAA(int index, int startTime, int depTime) {
        SimulatedSlotMarker afo = new SimulatedSlotMarker("aaa_" + index, new Position(AP_1.getLatitude(), AP_1.getLongitude(), 0), 280, 0, 180, startTime, depTime, 0);
        FlightPlan fpl = createFlightPlanAAA(afo);
        afo.setFlightPlan(fpl);
        afo.setStatus(Constants.NO_STARTED);
        return afo;
    }


    protected static SimulatedSlotMarker createDeparture(int index, int startTime, int depTime) {
        SimulatedSlotMarker afo = new SimulatedSlotMarker("d_" + index, new Position(AP_4.getLatitude(), AP_4.getLongitude(), 0), 280, 0, 180, startTime, depTime, 0);
        FlightPlan fpl = createFlightPlanDep(afo);
        afo.setFlightPlan(fpl);
        afo.setStatus(Constants.NO_STARTED);
        return afo;
    }


    private static FlightPlan createFlightPlanA(AFO afo) {
        FlightPlan fpl = new FlightPlan("arrival_A");
        FlightSegment s1 = new FlightSegment("s1", afo.getName(), A1_R1, A, 5000, 200);
        FlightSegment s2 = new FlightSegment("s2", afo.getName(), A, B, 15000, 300);
        FlightSegment s3 = new FlightSegment("s3", afo.getName(), B, C, 30000, 350);
        FlightSegment s4 = new FlightSegment("s4", afo.getName(), C, D, 30000, 350);
        FlightSegment s5 = new FlightSegment("s5", afo.getName(), D, E, 30000, 350);
        FlightSegment s6 = new FlightSegment("s6", afo.getName(), E, F, 1500, 350);
        FlightSegment s7 = new FlightSegment("s7", afo.getName(), F, G, 5000, 220);
        FlightSegment s8 = new FlightSegment("s8", afo.getName(), G, A4_R2, 0, 180);
        fpl.addSegment(s1);
        fpl.addSegment(s2);
        fpl.addSegment(s3);
        fpl.addSegment(s4);
        fpl.addSegment(s5);
        fpl.addSegment(s6);
        fpl.addSegment(s7);
        fpl.addSegment(s8);
        fpl.setInitialSegment(s1);
        fpl.setCurrentSegment(s1);
        fpl.setDepartureRunway(A1_R1);
        fpl.setArrivalRunway(A4_R2);
        return fpl;
    }

    private static FlightPlan createFlightPlanAA(AFO afo) {
        FlightPlan fpl = new FlightPlan("arrival_AA");
        FlightSegment s1 = new FlightSegment("s1", afo.getName(), A2_R1, AA, 5000, 200);
        FlightSegment s2 = new FlightSegment("s2", afo.getName(), AA, BB, 15000, 300);
        FlightSegment s3 = new FlightSegment("s3", afo.getName(), BB, C, 30000, 350);
        FlightSegment s4 = new FlightSegment("s4", afo.getName(), C, D, 30000, 350);
        FlightSegment s5 = new FlightSegment("s5", afo.getName(), D, E, 30000, 350);
        FlightSegment s6 = new FlightSegment("s6", afo.getName(), E, F, 1500, 350);
        FlightSegment s7 = new FlightSegment("s7", afo.getName(), F, G, 5000, 220);
        FlightSegment s8 = new FlightSegment("s8", afo.getName(), G, A4_R2, 0, 180);
        fpl.addSegment(s1);
        fpl.addSegment(s2);
        fpl.addSegment(s3);
        fpl.addSegment(s4);
        fpl.addSegment(s5);
        fpl.addSegment(s6);
        fpl.addSegment(s7);
        fpl.addSegment(s8);
        fpl.setInitialSegment(s1);
        fpl.setCurrentSegment(s1);
        fpl.setDepartureRunway(A2_R1);
        fpl.setArrivalRunway(A4_R2);
        return fpl;
    }

    private static FlightPlan createFlightPlanAAA(AFO afo) {
        FlightPlan fpl = new FlightPlan("arrival_AAA");
        FlightSegment s1 = new FlightSegment("s1", afo.getName(), A3_R1, AAA, 5000, 200);
        FlightSegment s2 = new FlightSegment("s2", afo.getName(), AAA, BBB, 15000, 300);
        FlightSegment s3 = new FlightSegment("s3", afo.getName(), BBB, CCC, 30000, 350);
        FlightSegment s4 = new FlightSegment("s4", afo.getName(), CCC, DDD, 30000, 350);
        FlightSegment s5 = new FlightSegment("s5", afo.getName(), DDD, EEE, 30000, 350);
        FlightSegment s6 = new FlightSegment("s6", afo.getName(), EEE, FFF, 1500, 350);
        FlightSegment s7 = new FlightSegment("s7", afo.getName(), FFF, G, 5000, 220);
        FlightSegment s8 = new FlightSegment("s8", afo.getName(), G, A4_R2, 0, 180);
        fpl.addSegment(s1);
        fpl.addSegment(s2);
        fpl.addSegment(s3);
        fpl.addSegment(s4);
        fpl.addSegment(s5);
        fpl.addSegment(s6);
        fpl.addSegment(s7);
        fpl.addSegment(s8);
        fpl.setInitialSegment(s1);
        fpl.setCurrentSegment(s1);
        fpl.setDepartureRunway(A3_R1);
        fpl.setArrivalRunway(A4_R2);
        return fpl;
    }


    private static FlightPlan createFlightPlanDep(AFO afo) {
        FlightPlan fpl = new FlightPlan("departure");
        FlightSegment s1 = new FlightSegment("s1", afo.getName(), A4_R1, Z, 5000, 200);
        FlightSegment s2 = new FlightSegment("s2", afo.getName(), Z, Y, 15000, 300);
        fpl.addSegment(s1);
        fpl.addSegment(s2);
        fpl.setInitialSegment(s1);
        fpl.setCurrentSegment(s1);
        fpl.setDepartureRunway(A4_R1);
        return fpl;
    }

}
