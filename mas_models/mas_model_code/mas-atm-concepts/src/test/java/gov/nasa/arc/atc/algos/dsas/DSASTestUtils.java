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

package gov.nasa.arc.atc.algos.dsas;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import gov.nasa.arc.atc.utils.Constants;

/**
 * @author ahamon
 */
public class DSASTestUtils {

    protected static final Airport AIRPORT = new Airport("Airport", 40, -70);
    protected static final Runway RUNWAY = new Runway("LGA", AIRPORT, 12);
    protected static final Waypoint WPT_H = new Waypoint("wptH", 39.8, -70, true);
    protected static final Waypoint WPT_G = new Waypoint("wptG", 39.6, -70);
    protected static final Waypoint WPT_F = new Waypoint("wptF", 39.4, -70);
    protected static final Waypoint WPT_E = new Waypoint("wptE", 39.2, -70);
    protected static final Waypoint WPT_D = new Waypoint("wptD", 38.2, -70);
    protected static final Waypoint WPT_C = new Waypoint("wptC", 37.2, -70);
    // waypoints for departures
    protected static final Waypoint WPT_A = new Waypoint("wpt@", 42, -72);

    private static ATCGeography geography;

    protected DSASTestUtils() {
        ConsoleUtils.setLoggingLevel(Level.WARNING);
    }

    public static void test1(DSAS dsas) {
        // schedule a departure in a single gap (gap being created)
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + Constants.ARR_ARR_MIN;
        SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
        SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 100);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        context.getClock().setTime(startTime2);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        final int arrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int d1StartTime = arrivalTime1 + 1;
        dep1.setDepartureTime(d1StartTime);
        context.getClock().setTime(d1StartTime - 10);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        assertEquals(arrivalTime1 + Constants.ARR_ARR_MIN, arrivalTime2);
        dsas.initializeData(context, RUNWAY, RUNWAY);
        dsas.execute(arrivalTime1 - 10);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        assertEquals(arrivalTime1, newArrivalTime1);
        // assertEquals(arrivalTime1 + ArrivalGapType.SINGLE.getGapDuration(), newArrivalTime2);
        assertEquals(arrivalTime1 + Constants.SINGLE, newArrivalTime2);
        assertEquals(arrivalTime1 + Constants.ARR_DEP_MIN, newDepartureTime1);
    }

    public static void test2(DSAS dsas) {
        // schedule a departure in a single gap (gap being created)
        // and a second one after the gap
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + Constants.ARR_ARR_MIN;
        SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
        SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 100);
        SimulatedSlotMarker dep2 = DepartureFixedTest.createDeparture(2, 110);
        SimulatedSlotMarker dep3 = DepartureFixedTest.createDeparture(3, 120);
        SimulatedSlotMarker dep4 = DepartureFixedTest.createDeparture(4, 130);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        afos.add(dep4);
        context.addSlots(afos);
        context.getClock().setTime(startTime2);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime1 + 1;
        dep1.setDepartureTime(d1StartTime);
        dep2.setDepartureTime(d1StartTime + 1);
        dep3.setDepartureTime(d1StartTime + 2);
        dep4.setDepartureTime(d1StartTime + 3);

        context.getClock().setTime(arrivalTime1 - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas.initializeData(context, RUNWAY, RUNWAY);
        dsas.execute(arrivalTime1 - 10);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        int newDepartureTime3 = context.getDepartureSequences().get(RUNWAY).getAtIndex(2).getSlotMarker().getDepartureTime();
        int newDepartureTime4 = context.getDepartureSequences().get(RUNWAY).getAtIndex(3).getSlotMarker().getDepartureTime();
        assertEquals(arrivalTime1, newArrivalTime1);
        assertEquals(arrivalTime1 + Constants.SINGLE, newArrivalTime2);
        // assertEquals(arrivalTime1 + ArrivalGapType.SINGLE.getGapDuration(), newArrivalTime2);
        assertEquals(arrivalTime1 + Constants.ARR_DEP_MIN, newDepartureTime1);
        assertEquals(newArrivalTime2 + Constants.ARR_DEP_MIN, newDepartureTime2);
        assertEquals(newDepartureTime2 + Constants.DEP_DEP_MIN, newDepartureTime3);
        assertEquals(newDepartureTime3 + Constants.DEP_DEP_MIN, newDepartureTime4);
    }

    /**
     * schedule 2 departures in a double gap (gap being created)
     */
    public static void test3(DSAS dsas) {
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + DSASConcept.SINGLE_TO_MID_DOUBLE;
        SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
        SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 410);
        SimulatedSlotMarker dep2 = DepartureFixedTest.createDeparture(2, 411);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        afos.add(dep2);
        context.addSlots(afos);
        context.getClock().setTime(startTime2);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime1 + 1;
        dep1.setDepartureTime(d1StartTime);
        dep2.setDepartureTime(d1StartTime + 1);
        context.getClock().setTime(arrivalTime1 - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas.initializeData(context, RUNWAY, RUNWAY);
        dsas.execute(arrivalTime1 - 10);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        assertEquals(arrivalTime1, newArrivalTime1);
        // assertEquals(arrivalTime1 + ArrivalGapType.DOUBLE.getGapDuration(), newArrivalTime2);
        assertEquals(arrivalTime1 + Constants.DOUBLE, newArrivalTime2);
        assertEquals(newDepartureTime1 + Constants.DEP_DEP_MIN, newDepartureTime2);
    }

    /**
     * schedule 3 departures in a triple gap (gap being created)
     */
    public static void test4(DSAS dsas) {
        System.err.println("DSASConcept 2 -- Test 4");
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + DSASConcept.DOUBLE_TO_MID_TRIPLE;
        System.err.println("> startTime1 = " + startTime1);
        System.err.println("> startTime2 = " + startTime2);
        SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
        SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 410);
        SimulatedSlotMarker dep2 = DepartureFixedTest.createDeparture(2, 411);
        SimulatedSlotMarker dep3 = DepartureFixedTest.createDeparture(3, 412);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        context.addSlots(afos);
        context.getClock().setTime(startTime2);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        // final int arrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
        System.err.println("> arrivalTime1 = " + arrivalTime1);
        // System.err.println("> arrivalTime2 = "+arrivalTime2);
        int d1StartTime = arrivalTime1 + 1;
        dep1.setDepartureTime(d1StartTime);
        dep2.setDepartureTime(d1StartTime + 1);
        dep3.setDepartureTime(d1StartTime + 2);

        context.getClock().setTime(arrivalTime1 - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        System.err.println("> d1StartTime = " + d1StartTime);
        System.err.println("> d2StartTime = " + dep2.getStartTime());
        System.err.println("> d3StartTime = " + dep3.getStartTime());
        System.err.println("> EXE...");
        dsas.initializeData(context, RUNWAY, RUNWAY);
        dsas.execute(arrivalTime1 - 10);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        int newDepartureTime3 = context.getDepartureSequences().get(RUNWAY).getAtIndex(2).getSlotMarker().getDepartureTime();
        System.err.println("> newArrivalTime1 = " + newArrivalTime1);
        System.err.println("> newArrivalTime2 = " + newArrivalTime2);
        System.err.println("> newDepartureTime1 = " + newDepartureTime1);
        System.err.println("> newDepartureTime2 = " + newDepartureTime2);
        System.err.println("> newDepartureTime3 = " + newDepartureTime3);
        assertEquals(arrivalTime1, newArrivalTime1);
        // assertEquals(arrivalTime1 + ArrivalGapType.TRIPLE.getGapDuration(), newArrivalTime2);
        assertEquals(arrivalTime1 + Constants.TRIPLE, newArrivalTime2);
        assertEquals(newDepartureTime1 + Constants.DEP_DEP_MIN, newDepartureTime2);
        assertEquals(newDepartureTime2 + Constants.DEP_DEP_MIN, newDepartureTime3);
        System.err.println("-----\n");
    }

    /**
     * schedule 4 departures in a double and single gap and after (gaps being created)
     */
    public static void test5(DSAS dsas) {
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 8;
        int startTime2 = startTime1 + DSASConcept.SINGLE_TO_MID_DOUBLE;
        int startTime3 = startTime1 + Constants.DOUBLE + DSASConcept.NONE_TO_MID_SINGLE;
        System.out.println(" start1=" + startTime1);
        System.out.println(" start2=" + startTime2);
        System.out.println(" start3=" + startTime3);
        SimulatedSlotMarker arr1 = createArrival(1, startTime1, 39.4, -70);
        SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
        SimulatedSlotMarker arr3 = createArrival(3, startTime3, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 410);
        SimulatedSlotMarker dep2 = DepartureFixedTest.createDeparture(2, 411);
        SimulatedSlotMarker dep3 = DepartureFixedTest.createDeparture(3, 412);
        SimulatedSlotMarker dep4 = DepartureFixedTest.createDeparture(4, 413);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(arr3);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        afos.add(dep4);
        context.addSlots(afos);
        context.getClock().setTime(startTime3);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime1 + 1;
        dep1.setDepartureTime(d1StartTime);
        dep2.setDepartureTime(d1StartTime + 1);
        dep3.setDepartureTime(d1StartTime + 2);
        dep4.setDepartureTime(d1StartTime + 3);
        context.getClock().setTime(arrivalTime1 - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        System.out.println(" dep time = " + d1StartTime);
        dsas.initializeData(context, RUNWAY, RUNWAY);
        dsas.execute(arrivalTime1 - 10);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newArrivalTime3 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        int newDepartureTime3 = context.getDepartureSequences().get(RUNWAY).getAtIndex(2).getSlotMarker().getDepartureTime();
        int newDepartureTime4 = context.getDepartureSequences().get(RUNWAY).getAtIndex(3).getSlotMarker().getDepartureTime();
        assertEquals(arrivalTime1, newArrivalTime1);
        // assertEquals(arrivalTime1 + ArrivalGapType.DOUBLE.getGapDuration(), newArrivalTime2);
        // assertEquals(newArrivalTime2 + ArrivalGapType.SINGLE.getGapDuration(), newArrivalTime3);
        assertEquals(arrivalTime1 + Constants.DOUBLE, newArrivalTime2);
        assertEquals(newArrivalTime2 + Constants.SINGLE, newArrivalTime3);
        assertEquals(arrivalTime1 + Constants.ARR_DEP_MIN, newDepartureTime1);
        assertEquals(newDepartureTime1 + Constants.DEP_DEP_MIN, newDepartureTime2);
        assertEquals(newArrivalTime2 + Constants.ARR_DEP_MIN, newDepartureTime3);
        assertEquals(newArrivalTime3 + Constants.ARR_DEP_MIN, newDepartureTime4);
        System.out.println(" arrivalTime1 = " + arrivalTime1);
        System.out.println(" arrivalTime2 = " + newArrivalTime2);
        System.out.println(" arrivalTime3 = " + newArrivalTime3);
        System.out.println(" dep1 time = " + newDepartureTime1);
        System.out.println(" dep2 time = " + newDepartureTime2);
        System.out.println(" dep3 time = " + newDepartureTime3);
        System.out.println(" dep4 time = " + newDepartureTime4);
    }

    public static void test6(DSAS dsas) {
        // schedule a departure before a gap
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + Constants.ARR_ARR_MIN;
        SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
        SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 100);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        context.addSlots(afos);
        context.getClock().setTime(startTime2);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        final int arrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int d1StartTime = arrivalTime1 - Constants.DEP_ARR_MIN;
        dep1.setDepartureTime(d1StartTime);
        context.getClock().setTime(d1StartTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        assertEquals(arrivalTime1 + Constants.ARR_ARR_MIN, arrivalTime2);
        dsas.initializeData(context, RUNWAY, RUNWAY);
        dsas.execute(d1StartTime - 10);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        assertEquals(arrivalTime1, newArrivalTime1);
        // assertEquals(arrivalTime1 + ArrivalGapType.SINGLE.getGapDuration(), newArrivalTime2);
        assertEquals(arrivalTime1 + Constants.SINGLE, newArrivalTime2);
        assertEquals(d1StartTime, newDepartureTime1);
    }

    public static void test7(DSAS dsas) {
        // schedule only one departure in a double, the next departure after the gap
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + DSASConcept.SINGLE_TO_MID_DOUBLE;
        SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
        SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 100);
        SimulatedSlotMarker dep2 = DepartureFixedTest.createDeparture(2, 110);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        afos.add(dep2);
        context.addSlots(afos);
        context.getClock().setTime(startTime2);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime1 + Constants.DOUBLE - Constants.DEP_ARR_MIN;
        dep1.setDepartureTime(d1StartTime);
        dep2.setDepartureTime(d1StartTime + 1);
        context.getClock().setTime(arrivalTime1 - 10);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas.initializeData(context, RUNWAY, RUNWAY);
        dsas.execute(arrivalTime1 - 10);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        //
        assertEquals(arrivalTime1, newArrivalTime1);
        // assertEquals(arrivalTime1 + ArrivalGapType.DOUBLE.getGapDuration(), newArrivalTime2);
        assertEquals(arrivalTime1 + Constants.DOUBLE, newArrivalTime2);
        assertEquals(newArrivalTime2 - Constants.DEP_ARR_MIN, newDepartureTime1);
        assertEquals(newArrivalTime2 + Constants.ARR_DEP_MIN, newDepartureTime2);
    }

    /**
     * / one arrival, depatures after
     */
    public static void test8(DSAS dsas) {
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + DSASConcept.SINGLE_TO_MID_DOUBLE;
        SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 100);
        SimulatedSlotMarker dep2 = DepartureFixedTest.createDeparture(2, 110);
        SimulatedSlotMarker dep3 = DepartureFixedTest.createDeparture(3, 120);
        afos.add(arr1);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        context.addSlots(afos);
        context.getClock().setTime(startTime2);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime1 + 1;
        dep1.setDepartureTime(d1StartTime);
        dep2.setDepartureTime(d1StartTime + 1);
        dep3.setDepartureTime(d1StartTime + 2);
        context.getClock().setTime(arrivalTime1 - 10);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas.initializeData(context, RUNWAY, RUNWAY);
        dsas.execute(arrivalTime1 - 10);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        int newDepartureTime3 = context.getDepartureSequences().get(RUNWAY).getAtIndex(2).getSlotMarker().getDepartureTime();
        //
        assertEquals(arrivalTime1, newArrivalTime1);
        assertEquals(newArrivalTime1 + Constants.ARR_DEP_MIN, newDepartureTime1);
        assertEquals(newDepartureTime1 + Constants.DEP_DEP_MIN, newDepartureTime2);
        assertEquals(newDepartureTime2 + Constants.DEP_DEP_MIN, newDepartureTime3);
    }

    /**
     * / two departures before an arrival, on depatures after
     */
    public static void test9(DSAS dsas) {
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + DSASConcept.SINGLE_TO_MID_DOUBLE;
        SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 100);
        SimulatedSlotMarker dep2 = DepartureFixedTest.createDeparture(2, 110);
        SimulatedSlotMarker dep3 = DepartureFixedTest.createDeparture(3, 120);
        afos.add(arr1);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        context.addSlots(afos);
        context.getClock().setTime(startTime2);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int dStartTime1 = arrivalTime1 - Constants.DEP_DEP_MIN - Constants.DEP_ARR_MIN;
        int dStartTime2 = arrivalTime1 - Constants.DEP_ARR_MIN;
        int dStartTime3 = arrivalTime1 + 1;
        dep1.setDepartureTime(dStartTime1);
        dep2.setDepartureTime(dStartTime2);
        dep3.setDepartureTime(dStartTime3);
        //
        int simuTime = dStartTime1 - 10;
        context.getClock().setTime(simuTime);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas.initializeData(context, RUNWAY, RUNWAY);
        //
        dsas.execute(simuTime);
        //
        // arrival 1 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        int newDepartureTime3 = context.getDepartureSequences().get(RUNWAY).getAtIndex(2).getSlotMarker().getDepartureTime();
        //
        assertEquals(arrivalTime1, newArrivalTime1);
        assertEquals(dStartTime1, newDepartureTime1);
        assertEquals(dStartTime2, newDepartureTime2);
        assertEquals(arrivalTime1 + Constants.ARR_DEP_MIN, newDepartureTime3);
        //
        simuTime = arrivalTime1 - 10;
        context.getClock().setTime(simuTime);
        // context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas.execute(simuTime);
        //
        newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        newDepartureTime3 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        assertEquals(arrivalTime1, newArrivalTime1);
        assertEquals(arrivalTime1 + Constants.ARR_DEP_MIN, newDepartureTime3);
        //
        simuTime = arrivalTime1 + 1;
        context.getClock().setTime(simuTime);
        // context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas.execute(simuTime);
        //
        newDepartureTime3 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        assertEquals(arrivalTime1 + Constants.ARR_DEP_MIN, newDepartureTime3);
    }

    /**
     * / six departures in a very large gap
     */
    public static void test10(DSAS dsas) {
        SimulationContext context = new SimulationContext(getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int startTime1 = 1;
        int startTime2 = startTime1 + 5 * DSASConcept.TRIPLE_TO_MID_B757;
        SimulatedSlotMarker arr1 = createLongArrival( 1,startTime1, 37.2, -70);
        SimulatedSlotMarker arr2 = createLongArrival( 2,startTime2, 37.2, -70);
        SimulatedSlotMarker dep1 = DepartureFixedTest.createDeparture(1, 100);
        SimulatedSlotMarker dep2 = DepartureFixedTest.createDeparture(2, 110);
        SimulatedSlotMarker dep3 = DepartureFixedTest.createDeparture(3, 120);
        SimulatedSlotMarker dep4 = DepartureFixedTest.createDeparture(3, 130);
        SimulatedSlotMarker dep5 = DepartureFixedTest.createDeparture(3, 140);
        SimulatedSlotMarker dep6 = DepartureFixedTest.createDeparture(3, 150);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        afos.add(dep4);
        afos.add(dep5);
        afos.add(dep6);
        context.addSlots(afos);
        context.getClock().setTime(startTime2 + 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        final int arrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int dStartTime1 = arrivalTime1 + 2;
        int dStartTime2 = dStartTime1 + 2;
        int dStartTime3 = dStartTime2 + 2;
        int dStartTime4 = dStartTime3 + 2;
        int dStartTime5 = dStartTime4 + 2;
        int dStartTime6 = dStartTime5 + 2;
        dep1.setDepartureTime(dStartTime1);
        dep2.setDepartureTime(dStartTime2);
        dep3.setDepartureTime(dStartTime3);
        dep4.setDepartureTime(dStartTime4);
        dep5.setDepartureTime(dStartTime5);
        dep6.setDepartureTime(dStartTime6);
        //
        System.err.println("dStartTime1 = " + dStartTime1);
        System.err.println("dStartTime2 = " + dStartTime2);
        System.err.println("dStartTime3 = " + dStartTime3);
        System.err.println("dStartTime4 = " + dStartTime4);
        System.err.println("dStartTime5 = " + dStartTime5);
        System.err.println("dStartTime6 = " + dStartTime6);
        //
        int simuTime = dStartTime1 - 10;
        context.getClock().setTime(simuTime);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas.initializeData(context, RUNWAY, RUNWAY);
        //
        dsas.execute(simuTime);
        //
        // arrival 1 & 2 shall not have been delayed
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newDepartureTime1 = context.getDepartureSequences().get(RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        int newDepartureTime3 = context.getDepartureSequences().get(RUNWAY).getAtIndex(2).getSlotMarker().getDepartureTime();
        int newDepartureTime4 = context.getDepartureSequences().get(RUNWAY).getAtIndex(3).getSlotMarker().getDepartureTime();
        int newDepartureTime5 = context.getDepartureSequences().get(RUNWAY).getAtIndex(4).getSlotMarker().getDepartureTime();
        int newDepartureTime6 = context.getDepartureSequences().get(RUNWAY).getAtIndex(5).getSlotMarker().getDepartureTime();
        //
        assertEquals(arrivalTime1, newArrivalTime1);
        assertEquals(arrivalTime2, newArrivalTime2);
        //
        System.err.println(" DEP-DEP = " + Constants.DEP_DEP_MIN);
        //
        System.err.println("newDepartureTime1 = " + newDepartureTime1);
        System.err.println("newDepartureTime2 = " + newDepartureTime2);
        System.err.println("newDepartureTime3 = " + newDepartureTime3);
        System.err.println("newDepartureTime4 = " + newDepartureTime4);
        System.err.println("newDepartureTime5 = " + newDepartureTime5);
        System.err.println("newDepartureTime6 = " + newDepartureTime6);

        assertEquals(arrivalTime1 + Constants.ARR_DEP_MIN, newDepartureTime1);
        assertEquals(newDepartureTime1 + Constants.DEP_DEP_MIN, newDepartureTime2);
        assertEquals(newDepartureTime2 + Constants.DEP_DEP_MIN, newDepartureTime3);
        assertEquals(newDepartureTime3 + Constants.DEP_DEP_MIN, newDepartureTime4);
        assertEquals(newDepartureTime4 + Constants.DEP_DEP_MIN, newDepartureTime5);
        assertEquals(newDepartureTime5 + Constants.DEP_DEP_MIN, newDepartureTime6);
    }

	/*
     * utils methods
	 */

    protected static ATCGeography getSimpleGeography() {
        if (geography == null) {
            AIRPORT.addRunway(RUNWAY);
            geography = new ATCGeography("testingG");
            geography.addAirport(AIRPORT);
            geography.addWaypoint(WPT_H);
            geography.addWaypoint(WPT_G);
            geography.addWaypoint(WPT_F);
            geography.addWaypoint(WPT_E);
        }
        return geography;
    }

    protected static SimulatedSlotMarker createArrival(int index, int startTime, double lat, double lon) {
        SimulatedSlotMarker afo1 = new SimulatedSlotMarker("a_" + index, new Position(lat, lon, 5000), 280, 0, 180, startTime, -1, 0);
        FlightPlan fpl1 = createArrivalFlightPlan(afo1);
        afo1.setFlightPlan(fpl1);
        afo1.setStatus(Constants.IS_FLYING);
        return afo1;
    }

    protected static SimulatedSlotMarker createLongArrival(int index, int startTime, double lat, double lon) {
        SimulatedSlotMarker afo1 = new SimulatedSlotMarker("a_" + index, new Position(lat, lon, 5000), 280, 0, 180, startTime, -1, 0);
        FlightPlan fpl1 = createLongArrivalFlightPlan(afo1);
        afo1.setFlightPlan(fpl1);
        afo1.setStatus(Constants.IS_FLYING);
        return afo1;
    }

    protected static FlightPlan createArrivalFlightPlan(AFO afo) {
        FlightPlan fpl = new FlightPlan("arrival");
        FlightSegment eF = new FlightSegment("E-F", afo.getName(), WPT_E, WPT_F, 5000, 280);
        FlightSegment fG = new FlightSegment("F-G", afo.getName(), WPT_F, WPT_G, 2500, 250);
        FlightSegment gH = new FlightSegment("G-H", afo.getName(), WPT_G, WPT_H, 2000, 230);
        FlightSegment hRunway = new FlightSegment("H-LGA", afo.getName(), WPT_H, RUNWAY, 0, 220);
        fpl.addSegment(eF);
        fpl.addSegment(fG);
        fpl.addSegment(gH);
        fpl.addSegment(hRunway);
        fpl.setInitialSegment(eF);
        fpl.setCurrentSegment(eF);
        fpl.setArrivalRunway(RUNWAY);
        return fpl;
    }

    protected static FlightPlan createLongArrivalFlightPlan(AFO afo) {
        FlightPlan fpl = new FlightPlan("arrival");
        FlightSegment cD = new FlightSegment("C-D", afo.getName(), WPT_C, WPT_D, 15000, 280);
        FlightSegment dE = new FlightSegment("D-E", afo.getName(), WPT_D, WPT_E, 10000, 280);
        FlightSegment eF = new FlightSegment("E-F", afo.getName(), WPT_E, WPT_F, 5000, 280);
        FlightSegment fG = new FlightSegment("F-G", afo.getName(), WPT_F, WPT_G, 2500, 250);
        FlightSegment gH = new FlightSegment("G-H", afo.getName(), WPT_G, WPT_H, 2000, 230);
        FlightSegment hRunway = new FlightSegment("H-LGA", afo.getName(), WPT_H, RUNWAY, 0, 220);
        fpl.addSegment(cD);
        fpl.addSegment(dE);
        fpl.addSegment(eF);
        fpl.addSegment(fG);
        fpl.addSegment(gH);
        fpl.addSegment(hRunway);
        fpl.setInitialSegment(cD);
        fpl.setCurrentSegment(cD);
        fpl.setArrivalRunway(RUNWAY);
        return fpl;
    }
}
