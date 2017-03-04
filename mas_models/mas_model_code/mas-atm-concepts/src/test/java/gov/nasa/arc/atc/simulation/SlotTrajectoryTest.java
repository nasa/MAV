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

package gov.nasa.arc.atc.simulation;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.TimedFlightParametersUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * @author ahamon
 */
public class SlotTrajectoryTest {

    private static final int DEFAULT_SIM_DURATION = 200;

    private static final boolean PRINT_ON = true;


    // Geography creation
    private static final Airport DEP_AIRPORT = new Airport("DEP_AIRPORT", 40.8, -71);
    private static final Runway DEP_R = new Runway("DEP_R", DEP_AIRPORT, 0);
    private static final Waypoint wpt1 = new Waypoint("WPT_1", 40.2, -70);
    private static final Waypoint wpt2 = new Waypoint("WPT_2", 40, -70.2);
    private static final Airport ARR_AIRPORT = new Airport("ARR_AIRPORT", 40, -70.2);
    private static final Runway ARR_R = new Runway("ARR_R", ARR_AIRPORT, 10);
    //
    private static final ATCGeography ATC_GEOGRAPHY = new ATCGeography(SlotTrajectoryTest.class.getSimpleName());


    private static final Logger LOG = Logger.getGlobal();

    @BeforeClass
    public static void setupClass() {
        ConsoleUtils.setLoggingLevel(Level.WARNING);
        DEP_AIRPORT.addRunway(DEP_R);
        ARR_AIRPORT.addRunway(ARR_R);
        ATC_GEOGRAPHY.addAirport(DEP_AIRPORT);
        ATC_GEOGRAPHY.addAirport(ARR_AIRPORT);
        ATC_GEOGRAPHY.addWaypoint(wpt1);
        ATC_GEOGRAPHY.addWaypoint(wpt2);
    }

    @Test
    public void testSimpleFlightCreation() {
        if (PRINT_ON) {
            System.err.println("--------------------------------");
            System.err.println("--- testSimpleFlightCreation ---");
        }
        final int startTime = 3;
        final int departureTime = 8;
        SlotTrajectory oracleTraj = createSimpleFullFlight(startTime, departureTime);
        LOG.log(Level.FINE, "testSimpleFlightCreation {0}", oracleTraj);

        final int originalStartTime = oracleTraj.getOriginalStartTime();
        Assert.assertEquals(startTime, originalStartTime);

        final int slotDepartureTime = oracleTraj.getSlotMarker().getDepartureTime();
        Assert.assertEquals(departureTime, slotDepartureTime);

        if (PRINT_ON) {
            System.err.println("\n--------------------------------");
        }
    }


    @Test
    public void testSimpleFlightDelay0() {
        if (PRINT_ON) {
            System.err.println("-------------------------------");
            System.err.println("--- testSimpleFlightDelay 0 ---");
        }
        final int startTime = 3;
        final int departureTime = 8;
        SlotTrajectory oracleTraj = createSimpleFullFlight(startTime, departureTime);
        LOG.log(Level.FINE, "testSimpleFlightCreation {0}", oracleTraj);
        List<TimedFlightParameters> originalParameters = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        if (PRINT_ON) {
            for (int i = 0; i < 10; i++) {
                System.err.println(originalParameters.get(i));
            }
        }


        // recalculate 1 (at t = 0)
        if (PRINT_ON) {
            System.err.println("--- 1 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(0, 0);
        List<TimedFlightParameters> parameters1 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        for (int i = 0; i < Math.min(originalParameters.size(), parameters1.size()); i++) {
            TimedFlightParameters originalP = originalParameters.get(i);
            TimedFlightParameters p1 = parameters1.get(i);
            if (!originalP.equals(p1) && PRINT_ON) {
                System.err.println(" original: " + originalP);
                System.err.println("       p1: " + p1);
                System.err.println("");
            }
            Assert.assertEquals(originalP, p1);
        }

        // recalculate 2 (before start time)
        if (PRINT_ON) {
            System.err.println("--- 2 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(startTime - 1, 0);
        List<TimedFlightParameters> parameters2 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        for (int i = 0; i < Math.min(originalParameters.size(), parameters2.size()); i++) {
            TimedFlightParameters originalP = originalParameters.get(i);
            TimedFlightParameters p2 = parameters2.get(i);
            if (!originalP.equals(p2) && PRINT_ON) {
                System.err.println(" original: " + originalP);
                System.err.println("       p2: " + p2);
                System.err.println("");
            }
            Assert.assertEquals(originalP, p2);
        }


        // recalculate 3 (between start and departure time)
        if (PRINT_ON) {
            System.err.println("--- 3 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(departureTime - 1, 0);
        List<TimedFlightParameters> parameters3 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        for (int i = 0; i < Math.min(originalParameters.size(), parameters3.size()); i++) {
            TimedFlightParameters originalP = originalParameters.get(i);
            TimedFlightParameters p3 = parameters3.get(i);
            if (!originalP.equals(p3) && PRINT_ON) {
                System.err.println(" original: " + originalP);
                System.err.println("       p3: " + p3);
                System.err.println("");
            }
            Assert.assertEquals(originalP, p3);
        }


        // recalculate 4 (after departure time)
        if (PRINT_ON) {
            System.err.println("--- 4 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(departureTime + 1, 0);
        List<TimedFlightParameters> parameters4 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        for (int i = 0; i < Math.min(originalParameters.size(), parameters4.size()); i++) {
            TimedFlightParameters originalP = originalParameters.get(i);
            TimedFlightParameters p4 = parameters4.get(i);
            if (!originalP.equals(p4)) {
                System.err.println(" original: " + originalP);
                System.err.println("       p4: " + p4);
                System.err.println("");
            }
            Assert.assertEquals(originalP, p4);
        }

        if (PRINT_ON) {
            System.err.println("\n--------------------------------");
        }
    }


    @Test
    public void testSimpleFlightDelay5s() {
        if (PRINT_ON) {
            System.err.println("-------------------------------");
            System.err.println("--- testSimpleFlightDelay5s ---");
        }
        final int startTime = 3;
        final int departureTime = 8;
        final int delayValue = 5;
        SlotTrajectory oracleTraj = createSimpleFullFlight(startTime, departureTime);
        List<TimedFlightParameters> originalParameters = new LinkedList<>(oracleTraj.getTimedFlightParameters());

        // recalculate adding delay before start time
        if (PRINT_ON) {
            System.err.println("--- 1 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(startTime - 1, delayValue);
        final int startTime1 = oracleTraj.getSlotMarker().getStartTime();
        Assert.assertEquals(startTime, startTime1);
        final int departureTime1 = oracleTraj.getSlotMarker().getDepartureTime();
        Assert.assertEquals(departureTime + delayValue, departureTime1);
        List<TimedFlightParameters> parameters1 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
//        for (int i = 0; i < 20; i++) {
//            System.err.println(parameters1.get(i));
//        }
        // should be identical until start time
        for (int i = 0; i < startTime; i++) {
            TimedFlightParameters originalP = originalParameters.get(i);
            TimedFlightParameters p1 = parameters1.get(i);
            if (!originalP.equals(p1)) {
                System.err.println(" original: " + originalP);
                System.err.println("       p1: " + p1);
                System.err.println("");
            }
            Assert.assertEquals(originalP, p1);
        }
        // the parameters while on the ground should be have the same content
        TimedFlightParameters originalStartP = originalParameters.get(startTime);
        for (int i = startTime; i < departureTime1; i++) {
            TimedFlightParameters p1 = parameters1.get(i);
            boolean haveSameContent = TimedFlightParametersUtils.compareWithoutTime(originalStartP, p1);
            if (!haveSameContent) {
                System.err.println(" original: " + originalStartP);
                System.err.println("       p1: " + p1);
                System.err.println("");
            }
            Assert.assertTrue(haveSameContent);
        }
        // the content of the flight parameters shall be the same during the flight, modulo the delay
        for (int i = departureTime1; i < parameters1.size(); i++) {
            TimedFlightParameters originalP = originalParameters.get(i - delayValue);
            TimedFlightParameters p1 = parameters1.get(i);
            boolean haveSameContent = TimedFlightParametersUtils.compareWithoutTime(originalP, p1);
            if (!haveSameContent) {
                System.err.println(" original: " + originalP);
                System.err.println("       p1: " + p1);
                System.err.println("");
            }
            Assert.assertTrue(haveSameContent);
        }


        // recalculate adding delay between start and departure time
        if (PRINT_ON) {
            System.err.println("--- 2 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(departureTime1 - 1, delayValue);
        final int startTime2 = oracleTraj.getSlotMarker().getStartTime();
        Assert.assertEquals(startTime, startTime2);
        final int departureTime2 = oracleTraj.getSlotMarker().getDepartureTime();
        Assert.assertEquals(departureTime1 + delayValue, departureTime2);
        List<TimedFlightParameters> parameters2 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        for (int i = 0; i < startTime; i++) {
            TimedFlightParameters originalP = originalParameters.get(i);
            TimedFlightParameters p2 = parameters2.get(i);
            if (!originalP.equals(p2)) {
                System.err.println(" original2 " + originalP);
                System.err.println("       p2: " + p2);
                System.err.println("");
            }
            Assert.assertEquals(originalP, p2);
        }
        // the parameters while on the ground should be have the same content
        for (int i = startTime; i < departureTime2; i++) {
            TimedFlightParameters p2 = parameters2.get(i);
            boolean haveSameContent = TimedFlightParametersUtils.compareWithoutTime(originalStartP, p2);
            if (!haveSameContent) {
                System.err.println(" original: " + originalStartP);
                System.err.println("       p2: " + p2);
                System.err.println("");
            }
            Assert.assertTrue(haveSameContent);
        }
        // the content of the flight parameters shall be the same during the flight, modulo the delay
        for (int i = departureTime2; i < parameters2.size(); i++) {
            TimedFlightParameters originalP = originalParameters.get(i - delayValue * 2);
            TimedFlightParameters p2 = parameters2.get(i);
            boolean haveSameContent = TimedFlightParametersUtils.compareWithoutTime(originalP, p2);
            if (!haveSameContent) {
                System.err.println(" original: " + originalP);
                System.err.println("       p2: " + p2);
                System.err.println("");
            }
            Assert.assertTrue(haveSameContent);
        }


        // recalculate adding delay after departure time
        if (PRINT_ON) {
            System.err.println("--- 3 ---");
        }
        final int calculationTime3 = departureTime2 + delayValue * 2 + 1;
        oracleTraj.recalculateTrajectoryWithDelayAt(calculationTime3, delayValue);
        final int startTime3 = oracleTraj.getSlotMarker().getStartTime();
        Assert.assertEquals(startTime, startTime3);
        final int departureTime3 = oracleTraj.getSlotMarker().getDepartureTime();
        Assert.assertEquals(departureTime2, departureTime3);
        List<TimedFlightParameters> parameters3 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        // trajectory shall be identical the the last one before calculation time
        for (int i = 0; i < calculationTime3; i++) {
            TimedFlightParameters p2 = parameters2.get(i);
            TimedFlightParameters p3 = parameters3.get(i);
            if (!p3.equals(p2)) {
                System.err.println("   p2: " + p2);
                System.err.println(" ->p3: " + p3);
                System.err.println("");
            }
            Assert.assertEquals(p2, p3);
        }
        // after calculation time, parameters shall have same content modulo new delay added
        for (int i = calculationTime3; i < parameters3.size(); i++) {
            TimedFlightParameters p2 = parameters2.get(i - delayValue);
            TimedFlightParameters p3 = parameters3.get(i);
            boolean haveSameContent = TimedFlightParametersUtils.compareWithoutTime(p2, p3);
            if (!haveSameContent) {
                System.err.println("   p2: " + p2);
                System.err.println(" ->p3: " + p3);
                System.err.println("");
            }
            Assert.assertTrue(haveSameContent);
        }

        // recalculate adding delay after departure time
        if (PRINT_ON) {
            System.err.println("--- 4 ---");
        }
        final int calculationTime4 = calculationTime3 + delayValue * 2 + 1;
        oracleTraj.recalculateTrajectoryWithDelayAt(calculationTime4, delayValue);
        final int startTime4 = oracleTraj.getSlotMarker().getStartTime();
        Assert.assertEquals(startTime, startTime4);
        final int departureTime4 = oracleTraj.getSlotMarker().getDepartureTime();
        Assert.assertEquals(departureTime3, departureTime4);
        List<TimedFlightParameters> parameters4 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        // trajectory shall be identical the the last one before calculation time
        for (int i = 0; i < calculationTime4; i++) {
            TimedFlightParameters p3 = parameters3.get(i);
            TimedFlightParameters p4 = parameters4.get(i);
            if (!p3.equals(p4)) {
                System.err.println("   p3: " + p3);
                System.err.println(" ->p4: " + p4);
                System.err.println("");
            }
            Assert.assertEquals(p3, p4);
        }
        // after calculation time, parameters shall have same content modulo new delay added
        for (int i = calculationTime4; i < parameters4.size(); i++) {
            TimedFlightParameters p3 = parameters3.get(i - delayValue);
            TimedFlightParameters p4 = parameters4.get(i);
            boolean haveSameContent = TimedFlightParametersUtils.compareWithoutTime(p3, p4);
            if (!haveSameContent) {
                System.err.println("   p3: " + p3);
                System.err.println(" ->p4: " + p4);
                System.err.println("");
            }
            Assert.assertTrue(haveSameContent);
        }

        if (PRINT_ON) {
            System.err.println("\n--------------------------------");
        }
    }


    /*
    *  SIMPLE AIRBORNE FLIGHT
     */

    @Test
    public void testSimpleAirborneFlightCreation() {
        if (PRINT_ON) {
            System.err.println("----------------------------------------");
            System.err.println("--- testSimpleAirborneFlightCreation ---");
        }
        final int startTime = 3;
        final int departureTime = -1;
        SlotTrajectory oracleTraj = createSimpleAirborneFlight(startTime);
        LOG.log(Level.FINE, "testSimpleFlightCreation {0}", oracleTraj);

        final int originalStartTime = oracleTraj.getOriginalStartTime();
        Assert.assertEquals(startTime, originalStartTime);

        final int slotDepartureTime = oracleTraj.getSlotMarker().getDepartureTime();
        Assert.assertEquals(departureTime, slotDepartureTime);

        if (PRINT_ON) {
            System.err.println("\n--------------------------------");
        }
    }

    //TODO factor testing methods

    @Test
    public void testSimpleAirborneFlightDelay0() {
        if (PRINT_ON) {
            System.err.println("-------------------------------");
            System.err.println("--- testSimpleFlightDelay 0 ---");
        }
        final int startTime = 3;
        SlotTrajectory oracleTraj = createSimpleAirborneFlight(startTime);
        LOG.log(Level.FINE, "testSimpleFlightCreation {0}", oracleTraj);
        List<TimedFlightParameters> originalParameters = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        if (PRINT_ON) {
            for (int i = 0; i < 10; i++) {
                System.err.println(originalParameters.get(i));
            }
        }


        // recalculate 1 (at t = 0)
        if (PRINT_ON) {
            System.err.println("--- 1 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(0, 0);
        List<TimedFlightParameters> parameters1 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        for (int i = 0; i < Math.min(originalParameters.size(), parameters1.size()); i++) {
            TimedFlightParameters originalP = originalParameters.get(i);
            TimedFlightParameters p1 = parameters1.get(i);
            if (!originalP.equals(p1) && PRINT_ON) {
                System.err.println(" original: " + originalP);
                System.err.println("       p1: " + p1);
                System.err.println("");
            }
            Assert.assertEquals(originalP, p1);
        }

        // recalculate 2 (before start time)
        if (PRINT_ON) {
            System.err.println("--- 2 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(startTime - 1, 0);
        List<TimedFlightParameters> parameters2 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        for (int i = 0; i < Math.min(originalParameters.size(), parameters2.size()); i++) {
            TimedFlightParameters originalP = originalParameters.get(i);
            TimedFlightParameters p2 = parameters2.get(i);
            if (!originalP.equals(p2) && PRINT_ON) {
                System.err.println(" original: " + originalP);
                System.err.println("       p2: " + p2);
                System.err.println("");
            }
            Assert.assertEquals(originalP, p2);
        }


        if (PRINT_ON) {
            System.err.println("\n--------------------------------");
        }
    }



    @Test
    public void testSimpleAirborneFlightDelay5s() {
        if (PRINT_ON) {
            System.err.println("-------------------------------");
            System.err.println("--- testSimpleFlightDelay5s ---");
        }
        final int startTime = 3;
        final int delayValue = 7;
        SlotTrajectory oracleTraj = createSimpleAirborneFlight(startTime);
        List<TimedFlightParameters> originalParameters = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        System.err.println(" -- nb original Parameters = "+originalParameters.size());
        int originalArrivalTime = oracleTraj.getArrivalTime();

        // recalculate adding delay before start time
        if (PRINT_ON) {
            System.err.println("--- 1 ---");
        }
        oracleTraj.recalculateTrajectoryWithDelayAt(startTime - 1, delayValue);
        final int startTime1 = oracleTraj.getSlotMarker().getStartTime();
        Assert.assertEquals(startTime, startTime1);
        List<TimedFlightParameters> parameters1 = new LinkedList<>(oracleTraj.getTimedFlightParameters());
        System.err.println(" -- nb parameters1 parameters = "+parameters1.size());
        int arrivalTime1 = oracleTraj.getArrivalTime();

        Assert.assertEquals(originalArrivalTime+delayValue,arrivalTime1);

        if (PRINT_ON) {
            System.err.println("\n--------------------------------");
        }
    }




    /*
    * PRIVATE SLOT CREATION METHODS
     */



    private static SlotTrajectory createSimpleFullFlight(int startTime, int departureTime) {
        if (startTime <= 0) {
            System.err.println("TODO deal with it");
        }
        //
        String slotName = "Oracle_SimpleFullFlight";
        //
        SimulatedSlotMarker slotMarker;
        SlotTrajectory trajectory;
        FlightPlan flightPlan;
        //
        //
        FlightSegment segment0 = new FlightSegment("0->1", slotName, DEP_R, wpt1, 5000, 200);
        FlightSegment segment1 = new FlightSegment("1->2", slotName, wpt1, wpt2, 10000, 250);
        FlightSegment segment2 = new FlightSegment("1->2", slotName, wpt2, ARR_R, 0, 250);
        //
        flightPlan = new FlightPlan("oracle_Simple_FPL");
        flightPlan.addSegment(segment0);
        flightPlan.addSegment(segment1);
        flightPlan.addSegment(segment2);
        flightPlan.setInitialSegment(segment0);
        //
        slotMarker = new SimulatedSlotMarker(slotName, new Position(DEP_R.getLatitude(), DEP_R.getLongitude(), 0), 0, 0, 0, startTime, departureTime, Constants.ON_GROUND);
        slotMarker.setFlightPlan(flightPlan);
        //
        trajectory = new SlotTrajectory(ATC_GEOGRAPHY, slotMarker, DEFAULT_SIM_DURATION);
        return trajectory;
    }

    private static SlotTrajectory createSimpleAirborneFlight(int startTime) {
        if (startTime < 0) {
            System.err.println("TODO deal with it");
        }
        int departureTime = -1;
        //
        String slotName = "Oracle_SimpleAirborneFlight";
        //
        SimulatedSlotMarker slotMarker;
        SlotTrajectory trajectory;
        FlightPlan flightPlan;
        //
        FlightSegment segment1 = new FlightSegment("1->2", slotName, wpt1, wpt2, 10000, 250);
        FlightSegment segment2 = new FlightSegment("1->2", slotName, wpt2, ARR_R, 0, 250);
        //
        flightPlan = new FlightPlan("oracle_Simple_FPL");
        flightPlan.addSegment(segment1);
        flightPlan.addSegment(segment2);
        flightPlan.setInitialSegment(segment1);
        //
        slotMarker = new SimulatedSlotMarker(slotName, new Position(wpt1.getLatitude(), wpt1.getLongitude(), 10000), 0, 0, 0, startTime, departureTime, Constants.ON_GROUND);
        slotMarker.setFlightPlan(flightPlan);
        //
        trajectory = new SlotTrajectory(ATC_GEOGRAPHY, slotMarker, DEFAULT_SIM_DURATION);
        return trajectory;
    }

}
