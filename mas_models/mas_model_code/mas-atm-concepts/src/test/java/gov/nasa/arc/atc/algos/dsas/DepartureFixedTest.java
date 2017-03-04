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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import gov.nasa.arc.atc.geography.*;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.algos.tss.TSS;
import gov.nasa.arc.atc.functions.DistanceSeparationFunction;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import gov.nasa.arc.atc.utils.Constants;

/**
 * @author ahamon
 */
public class DepartureFixedTest {

    private static final Airport AIRPORT = new Airport("Airport", "AAA", 40, -70);
    private static final Runway RUNWAY = new Runway("LGA", AIRPORT, 12);
    private static final Waypoint WPT_H = new Waypoint("wptH", 39.8, -70);
    private static final Waypoint WPT_G = new Waypoint("wptG", 39.6, -69.8);
    private static final Waypoint WPT_F = new Waypoint("wptF", 39.2, -70.2);
    private static final Waypoint WPT_E = new Waypoint("wptE", 39.0, -70.4);
    // waypoints for departures
    private static final Waypoint WPT_A = new Waypoint("wptA", 40, -70);
    private static final Waypoint WPT_B = new Waypoint("wptB", 40.1, -70.1);

    private static ATCGeography ATC_GEOGRAPHY;

    @BeforeClass
    public static void setUp() {
        ConsoleUtils.setLoggingLevel(Level.SEVERE);
    }

    static ATCGeography getAtcGeography() {
        if (ATC_GEOGRAPHY == null) {
            initAirport();
        }
        return ATC_GEOGRAPHY;
    }

    private static void initAirport() {
        ATC_GEOGRAPHY = new ATCGeography("DepartureFixedTest");
        AIRPORT.addRunway(RUNWAY);
        ATC_GEOGRAPHY.addAirport(AIRPORT);
        ATC_GEOGRAPHY.addWaypoint(WPT_A);
        ATC_GEOGRAPHY.addWaypoint(WPT_B);
        ATC_GEOGRAPHY.addWaypoint(WPT_E);
        ATC_GEOGRAPHY.addWaypoint(WPT_F);
        ATC_GEOGRAPHY.addWaypoint(WPT_G);
        ATC_GEOGRAPHY.addWaypoint(WPT_H);
    }

    @Test
    public void test1ArrOKBefore() {
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        afos.add(arr1);
        afos.add(dep1);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        context.getClock().setTime(0);
        context.updateActiveSlots();
        // context.getArrivalSequences().forEach((node,sequence)->{
        // System.err.println("arriving at node "+node+" :: "+sequence);
        // });
        // context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().forEach(trajectory ->{
        // System.err.println(trajectory.getSlotMarker().getName()+" arrives at t="+trajectory.getArrivalTime());
        //// for(int i =0;i<trajectory.getArrivalTime();i++){
        //// System.out.println( "t="+i+" -> "+trajectory.getParametersAtSimulationTime(i));
        //// }
        // });
        // 98
        int arrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1DepartureTime = arrivalTime + Constants.ARR_DEP_MIN;
        dep1.setDepartureTime(d1DepartureTime);
        //
        FixedDepartureTime fixedDeparture = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        fixedDeparture.initializeData(context, RUNWAY, RUNWAY);
        context.getClock().setTime(arrivalTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        // context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().forEach(trajectory ->{
        // System.err.println("A> "+trajectory.getSlotMarker().getName()+" arrives at t="+trajectory.getArrivalTime());
        // });
        // context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().forEach(trajectory ->{
        // System.err.println("D> "+trajectory.getSlotMarker().getName()+" departs at t="+trajectory.getSlotMarker().getStartTime());
        // });
        fixedDeparture.execute(context.getClock().getCurrentSimTime());
        // System.err.println(" >>>> DSAS ");
        // context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().forEach(trajectory ->{
        // System.err.println("A> "+trajectory.getSlotMarker().getName()+" arrives at t="+trajectory.getArrivalTime());
        // });
        // context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().forEach(trajectory ->{
        // System.err.println("D> "+trajectory.getSlotMarker().getName()+" departs at t="+trajectory.getSlotMarker().getStartTime());
        // });
        int newArrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newD1DepartureTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getSlotMarker().getDepartureTime();
        assertEquals(arrivalTime, newArrivalTime);
        assertEquals(d1DepartureTime, newD1DepartureTime);
    }

    @Test
    public void test1ArrNotOKBefore() {
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        afos.add(arr1);
        afos.add(dep1);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        context.getClock().setTime(0);
        context.updateActiveSlots();
        // 98
        int delayNeeded = 1;
        int arrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1DepartureTime = arrivalTime + Constants.ARR_DEP_MIN - delayNeeded;

        System.err.println("\n ---- ");
        System.err.println(" > arrivalTime = " + arrivalTime);
        System.err.println(" > d1DepartureTime = " + d1DepartureTime);

        dep1.setDepartureTime(d1DepartureTime);
        context.getClock().setTime(d1DepartureTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        //
        System.err.println(" depart is " + dep1.getDepartureTime());
        //
        FixedDepartureTime dsas1 = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        dsas1.initializeData(context, RUNWAY, RUNWAY);
        context.getClock().setTime(arrivalTime - 1);
        context.updateActiveSlots();
        dsas1.execute(context.getClock().getCurrentSimTime());
        int newArrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newD1StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getSlotMarker().getDepartureTime();
        assertEquals(d1DepartureTime, newD1StartlTime);
        assertEquals(dep1.getDepartureTime() + Constants.DEP_ARR_MIN, newArrivalTime);
    }

    @Test
    public void test1ArrNotOKBefore3() {
        // intended sequence: d1 - d2 - d3 - a1
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        SimulatedSlotMarker dep2 = createDeparture(2, 10);
        SimulatedSlotMarker dep3 = createDeparture(3, 10);
        afos.add(arr1);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        context.getClock().setTime(0);
        context.updateActiveSlots();
        // 98
        int delayNeeded = 1;
        int arrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime + Constants.ARR_DEP_MIN - delayNeeded;
        dep1.setDepartureTime(d1StartTime);
        int d2StartTime = d1StartTime + Constants.DEP_DEP_MIN;
        int d3StartTime = d2StartTime + Constants.DEP_DEP_MIN;
        dep2.setDepartureTime(d2StartTime);
        dep3.setDepartureTime(d3StartTime);
        context.getClock().setTime(arrivalTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();


        //
        FixedDepartureTime dsas1 = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        dsas1.initializeData(context, RUNWAY, RUNWAY);
        context.getClock().setTime(arrivalTime - 1);
        context.updateActiveSlots();
        dsas1.execute(context.getClock().getCurrentSimTime());
        int newArrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newD1StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getSlotMarker().getDepartureTime();
        int newD2StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getSlotMarker().getDepartureTime();
        int newD3StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getSlotMarker().getDepartureTime();
        assertEquals(d1StartTime, newD1StartlTime);
        assertEquals(d2StartTime, newD2StartlTime);
        assertEquals(d3StartTime, newD3StartlTime);
        assertEquals(dep3.getDepartureTime() + Constants.DEP_ARR_MIN, newArrivalTime);
    }

    @Test
    public void test1ArrNotOKBetween2and3() {
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        SimulatedSlotMarker dep2 = createDeparture(2, 10);
        SimulatedSlotMarker dep3 = createDeparture(3, 10);
        afos.add(arr1);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        context.addSlots(afos);
        context.getClock().setTime(0);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        // 98
        int delayNeeded = 1;
        int arrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime + Constants.ARR_DEP_MIN - delayNeeded;
        dep1.setDepartureTime(d1StartTime);
        int d2StartTime = d1StartTime + Constants.DEP_DEP_MIN;
        int d3StartTime = d2StartTime + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN;
        dep2.setDepartureTime(d2StartTime);
        dep3.setDepartureTime(d3StartTime);
        //
        FixedDepartureTime dsas1 = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        dsas1.initializeData(context, RUNWAY, RUNWAY);
        context.getClock().setTime(arrivalTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas1.execute(context.getClock().getCurrentSimTime());
        int newArrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newD1StartTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getSlotMarker().getDepartureTime();
        int newD2StartTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getSlotMarker().getDepartureTime();
        int newD3StartTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getSlotMarker().getDepartureTime();
        assertEquals(d1StartTime, newD1StartTime);
        assertEquals(d2StartTime, newD2StartTime);
        assertEquals(d3StartTime, newD3StartTime);
        assertEquals(dep2.getDepartureTime() + Constants.DEP_ARR_MIN, newArrivalTime);
    }

    @Test
    public void test2ArrNotOKBetween2and3() {
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker arr2 = createArrival(2);
//        arr1.setDepartureTime(10);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        SimulatedSlotMarker dep2 = createDeparture(2, 10);
        SimulatedSlotMarker dep3 = createDeparture(3, 10);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        context.getClock().setTime(0);
        context.updateActiveSlots();
        // 98
        int delayNeeded = 1;
        int arrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime + Constants.ARR_DEP_MIN - delayNeeded;
        dep1.setDepartureTime(d1StartTime);
        int d2StartTime = d1StartTime + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN;
        int d3StartTime = d2StartTime + Constants.DEP_DEP_MIN;
        dep2.setDepartureTime(d2StartTime);
        dep3.setDepartureTime(d3StartTime);
        //
        FixedDepartureTime dsas1 = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        dsas1.initializeData(context, RUNWAY, RUNWAY);
        context.getClock().setTime(arrivalTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas1.execute(context.getClock().getCurrentSimTime());
        int newArrival1Time = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrival2Time = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newD1StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getSlotMarker().getDepartureTime();
        int newD2StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getSlotMarker().getDepartureTime();
        int newD3StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getSlotMarker().getDepartureTime();
        assertEquals(d1StartTime, newD1StartlTime);
        assertEquals(d2StartTime, newD2StartlTime);
        assertEquals(d3StartTime, newD3StartlTime);
        assertEquals(dep1.getDepartureTime() + Constants.DEP_ARR_MIN, newArrival1Time);
        assertEquals(dep3.getDepartureTime() + Constants.DEP_ARR_MIN, newArrival2Time);
    }

    @Test
    public void test2ArrNotOKDoubleGap() {
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker arr2 = createArrival(2);
//        arr1.setDepartureTime(10);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        SimulatedSlotMarker dep2 = createDeparture(2, 10);
        SimulatedSlotMarker dep3 = createDeparture(3, 10);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        context.addSlots(afos);
        context.getClock().setTime(10);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        // applying TSS
        TSS tss = new TSS(new DistanceSeparationFunction(context.getGeography().getAirports().get(0)));
        tss.initializeData(context, RUNWAY);
        tss.execute(10);
        // 98
        int delayNeeded = 1;
        int arrival1Time = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int arrival2Time = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int deltaArrivals = arrival2Time - arrival1Time;
        // ??
        deltaArrivals = Math.max(deltaArrivals, Constants.ARR_ARR_MIN);
        System.err.println(" XXX  : arrival2Time" + arrival2Time);
        //
        int d1StartTime = arrival1Time + Constants.ARR_DEP_MIN - delayNeeded;
        dep1.setDepartureTime(d1StartTime);
        int d2StartTime = d1StartTime + Constants.DEP_ARR_MIN + deltaArrivals + Constants.ARR_DEP_MIN + 5;
        int d3StartTime = d2StartTime + Constants.DEP_DEP_MIN;
        dep2.setDepartureTime(d2StartTime);
        dep3.setDepartureTime(d3StartTime);
        //
        FixedDepartureTime dsas1 = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        dsas1.initializeData(context, RUNWAY, RUNWAY);
        context.getClock().setTime(arrival1Time - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        dsas1.execute(context.getClock().getCurrentSimTime());
        int newArrival1Time = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrival2Time = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newD1StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getSlotMarker().getDepartureTime();
        int newD2StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getSlotMarker().getDepartureTime();
        int newD3StartlTime = context.getDepartureSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getSlotMarker().getDepartureTime();
        assertEquals(d1StartTime, newD1StartlTime);
        assertEquals(d2StartTime, newD2StartlTime);
        assertEquals(d3StartTime, newD3StartlTime);
        assertEquals(dep1.getDepartureTime() + Constants.DEP_ARR_MIN, newArrival1Time);
        assertEquals(newArrival1Time + deltaArrivals, newArrival2Time);
    }

    @Test
    public void test1ArrOKAfter() {
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        afos.add(arr1);
        afos.add(dep1);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        context.getClock().setTime(0);
        context.updateActiveSlots();
        // 98
        int arrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime - Constants.DEP_ARR_MIN;
        dep1.setDepartureTime(d1StartTime);
        //
        FixedDepartureTime dsas1 = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        dsas1.initializeData(context, RUNWAY, RUNWAY);
        context.getClock().setTime(arrivalTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        //
        assertEquals(1, context.getTakeOffs().get(RUNWAY.getName()).size());
        //
        dsas1.execute(context.getClock().getCurrentSimTime());
        int newArrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        assertEquals(arrivalTime, newArrivalTime);
    }

    @Test
    public void test4ArrOKAfter() {
        System.err.println(" -- test4ArrOKAfter --");
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker arr2 = createArrival(2);
        SimulatedSlotMarker arr3 = createArrival(3);
        SimulatedSlotMarker arr4 = createArrival(4);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(arr3);
        afos.add(arr4);
        afos.add(dep1);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        context.getClock().setTime(0);
        context.updateActiveSlots();
        // 98
        int arrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        System.err.println(" >> arrivalTime = " + arrivalTime);
        int d1StartTime = arrivalTime - Constants.DEP_ARR_MIN;
        dep1.setDepartureTime(d1StartTime);
        //
        context.getClock().setTime(arrivalTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        //
        System.err.println(" >> tss ... ");
        TSS tss = new TSS(new DistanceSeparationFunction(context.getGeography().getAirports().get(0)));
        tss.initializeData(context, RUNWAY);
        tss.execute(context.getClock().getCurrentSimTime());

        int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int arrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int arrivalTime3 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
        int arrivalTime4 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(3).getArrivalTime();
        System.err.println(" >> arrivalTime1 = " + arrivalTime1);
        System.err.println(" >> arrivalTime2 = " + arrivalTime2);
        System.err.println(" >> arrivalTime3 = " + arrivalTime3);
        System.err.println(" >> arrivalTime4 = " + arrivalTime4);
        //
        FixedDepartureTime dsas1 = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        dsas1.initializeData(context, RUNWAY, RUNWAY);
        //
        assertEquals(1, context.getTakeOffs().get(RUNWAY.getName()).size());
        //
        dsas1.execute(context.getClock().getCurrentSimTime());
        int newArrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int newArrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        int newArrivalTime3 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
        int newArrivalTime4 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(3).getArrivalTime();
        System.err.println(" >> newArrivalTime1 = " + newArrivalTime1);
        System.err.println(" >> newArrivalTime2 = " + newArrivalTime2);
        System.err.println(" >> newArrivalTime3 = " + newArrivalTime3);
        System.err.println(" >> newArrivalTime4 = " + newArrivalTime4);
        //
        assertEquals(arrivalTime, newArrivalTime1);
    }

    @Test
    public void test1ArrNotOKAfter() {
        SimulationContext context = new SimulationContext(createSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        SimulatedSlotMarker arr1 = createArrival(1);
        SimulatedSlotMarker dep1 = createDeparture(1, 10);
        afos.add(arr1);
        afos.add(dep1);
        context.addSlots(afos);
        context.getClock().setTime(10);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        // 98
        int delayNeeded = 1;
        int arrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        int d1StartTime = arrivalTime - Constants.DEP_ARR_MIN + delayNeeded;
        dep1.setDepartureTime(d1StartTime);
        //
        FixedDepartureTime dsas1 = new FixedDepartureTime(new DistanceSeparationFunction(AIRPORT));
        dsas1.initializeData(context, RUNWAY, RUNWAY);
        context.getClock().setTime(arrivalTime - 1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        //
        assertEquals(1, context.getTakeOffs().get(RUNWAY.getName()).size());
        //
        dsas1.execute(context.getClock().getCurrentSimTime());
        int newArrivalTime = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        assertEquals(arrivalTime + delayNeeded, newArrivalTime);
    }

    private static ATCGeography createSimpleGeography() {
        ATCGeography geography = new ATCGeography("testingG");
        geography.addAirport(AIRPORT);
        geography.addWaypoint(WPT_H);
        geography.addWaypoint(WPT_G);
        geography.addWaypoint(WPT_F);
        geography.addWaypoint(WPT_E);
        return geography;
    }

    private static SimulatedSlotMarker createArrival(int index) {
        SimulatedSlotMarker afo1 = new SimulatedSlotMarker("a_" + index, new Position(WPT_E.getLatitude(), WPT_E.getLongitude(), 5000), 250, 0, 180, 0, -1, 0);
        FlightPlan fpl1 = createArrivalFlightPlan(afo1);
        afo1.setFlightPlan(fpl1);
        afo1.setStatus(Constants.IS_FLYING);
        return afo1;
    }

    static SimulatedSlotMarker createDeparture(int index, int initStartTime) {
        SimulatedSlotMarker afo2 = new SimulatedSlotMarker("d_" + index, new Position(AIRPORT.getLatitude(), AIRPORT.getLongitude(), 0), 0, 0, 0, initStartTime, initStartTime, Constants.NO_STARTED);
        FlightPlan fpl1 = createDepartureFlightPlan(afo2);
        afo2.setFlightPlan(fpl1);
        afo2.setStatus(Constants.ON_GROUND);
        return afo2;
    }

    private static FlightPlan createArrivalFlightPlan(AFO afo) {
        FlightPlan fpl = new FlightPlan("arrival");
        FlightSegment eF = new FlightSegment("E-F", afo.getName(), WPT_E, WPT_F, 5000, 250);
        FlightSegment fG = new FlightSegment("F-G", afo.getName(), WPT_F, WPT_G, 2500, 200);
        FlightSegment gH = new FlightSegment("G-H", afo.getName(), WPT_G, WPT_H, 2000, 250);
        FlightSegment hRunway = new FlightSegment("H-Runway", afo.getName(), WPT_H, RUNWAY, 0, 220);
        fpl.addSegment(eF);
        fpl.addSegment(fG);
        fpl.addSegment(gH);
        fpl.addSegment(hRunway);
        fpl.setInitialSegment(eF);
        fpl.setCurrentSegment(eF);
        fpl.setArrivalRunway(RUNWAY);
        return fpl;
    }

    private static FlightPlan createDepartureFlightPlan(AFO afo) {
        FlightPlan fpl = new FlightPlan("departure");
        FlightSegment departSeg = new FlightSegment("R_A", afo.getName(), RUNWAY, WPT_A, 5000, 250);
        FlightSegment departSeg2 = new FlightSegment("A_B", afo.getName(), WPT_A, WPT_B, 10000, 220);
        fpl.addSegment(departSeg);
        fpl.addSegment(departSeg2);
        fpl.setInitialSegment(departSeg);
        fpl.setCurrentSegment(departSeg);
        fpl.setDepartureRunway(RUNWAY);
        return fpl;
    }


}
