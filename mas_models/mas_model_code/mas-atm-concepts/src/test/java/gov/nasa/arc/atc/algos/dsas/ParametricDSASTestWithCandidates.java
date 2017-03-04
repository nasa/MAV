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

import gov.nasa.arc.atc.geography.Position;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.simulation.SimulationContext;
//import gov.nasa.arc.atc.algos.viewer.reports.ReportAppLauncher;
import gov.nasa.arc.atc.functions.MeterFixCandidateFunction;
import gov.nasa.arc.atc.functions.SimpleGapScheludingFunction;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.Constants;

/**
 * @author ahamon
 */
public class ParametricDSASTestWithCandidates {

    @BeforeClass
    public static void setUpBeforeClass() {
        DSASTestUtils utils = new DSASTestUtils();
        System.err.println(" utils created ::" + utils);
    }

    /**
     *
     */
    @Test
    public void test1() {
        ATCGeography geography = DSASTestUtils.getSimpleGeography();
        SimulationContext context = new SimulationContext(geography);
        context.setSimulationDuration(Integer.MAX_VALUE);
        List<SimulatedSlotMarker> afos = new ArrayList<>();
        //
        int startTime1 = 8;
        int startTime2 = startTime1 + Constants.SINGLE;
        int startTime3 = startTime2 + Constants.SINGLE;
        int startTime4 = startTime3 + Constants.DOUBLE + DSASConcept.NONE_TO_MID_SINGLE;// + 1000;//
        //
        System.err.println("startTime1 = " + startTime1);
        System.err.println("startTime2 = " + startTime2);
        System.err.println("startTime3 = " + startTime3);
        System.err.println("startTime4 = " + startTime4);
        // arrivals
        SimulatedSlotMarker arr1 = DSASTestUtils.createLongArrival(1, startTime1, 39.4, -70);
        SimulatedSlotMarker arr2 = DSASTestUtils.createLongArrival(2, startTime2, 39.4, -70);
        SimulatedSlotMarker arr3 = DSASTestUtils.createLongArrival(3, startTime3, 39.4, -70);
        SimulatedSlotMarker arr4 = DSASTestUtils.createLongArrival(4, startTime4, 39.4, -70);
        // departures
        // TODO see why need to be >400
        SimulatedSlotMarker dep1 = createDeparture(1, 400);
        SimulatedSlotMarker dep2 = createDeparture(2, 410);
        SimulatedSlotMarker dep3 = createDeparture(3, 420);
        SimulatedSlotMarker dep4 = createDeparture(4, 430);
        //
        afos.add(arr1);
        afos.add(arr2);
        afos.add(arr3);
        afos.add(arr4);
        afos.add(dep1);
        afos.add(dep2);
        afos.add(dep3);
        afos.add(dep4);
        //
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        context.getClock().setTime(startTime4);
        context.updateActiveSlots();
        //
        final int originalArrivalTime1 = context.getArrivalSequences().get(DSASTestUtils.RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        final int originalArrivalTime2 = context.getArrivalSequences().get(DSASTestUtils.RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        final int originalArrivalTime3 = context.getArrivalSequences().get(DSASTestUtils.RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
        final int originalArrivalTime4 = context.getArrivalSequences().get(DSASTestUtils.RUNWAY).getSimulatedTrajectories().get(3).getArrivalTime();
        System.err.println(" originalArrivalTime1 = " + originalArrivalTime1);
        System.err.println(" originalArrivalTime2 = " + originalArrivalTime2);
        System.err.println(" originalArrivalTime3 = " + originalArrivalTime3);
        System.err.println(" originalArrivalTime4 = " + originalArrivalTime4);
        //
        int simTime = startTime4 + 10;
        int meterFixTime = context.getArrivalSequences().get(DSASTestUtils.RUNWAY).getSimulatedTrajectories().get(2).getArrivalTimeAtNode(DSASTestUtils.WPT_H.getName());
        int maxCandidateTime = meterFixTime - simTime + 1;
        //
        // scheduling departures
        int d1StartTime = originalArrivalTime1 + 1;
        dep1.setDepartureTime(d1StartTime);
        dep2.setDepartureTime(d1StartTime + 1);
        dep3.setDepartureTime(d1StartTime + 2);
        dep4.setDepartureTime(d1StartTime + 3);
        //
        context.getClock().setTime(originalArrivalTime1 - 1);
        context.updateActiveSlots();
        //
        MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(geography, maxCandidateTime);
        ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
        dsas.initializeData(context, DSASTestUtils.RUNWAY, DSASTestUtils.RUNWAY);
        context.getClock().setTime(simTime);
        dsas.execute(simTime);
        System.err.println("------ EXEC t=" + simTime + " ------");
        //
        final int finalArrivalTime1 = context.getArrivalSequences().get(DSASTestUtils.RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
        final int finalArrivalTime2 = context.getArrivalSequences().get(DSASTestUtils.RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
        final int finalArrivalTime3 = context.getArrivalSequences().get(DSASTestUtils.RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
        System.err.println(" finalArrivalTime1 = " + finalArrivalTime1);
        System.err.println(" finalArrivalTime2 = " + finalArrivalTime2);
        System.err.println(" finalArrivalTime3 = " + finalArrivalTime3);
        assertEquals(originalArrivalTime1, finalArrivalTime1);
        assertEquals(originalArrivalTime2, finalArrivalTime2);
        assertEquals(originalArrivalTime3, finalArrivalTime3);
        //
        System.err.println(" // " + context.getDepartureSequences());
        System.err.println(" => " + context.getDepartureSequences().entrySet());
        //
        int newDepartureTime1 = context.getDepartureSequences().get(DSASTestUtils.RUNWAY).getAtIndex(0).getSlotMarker().getDepartureTime();
        int newDepartureTime2 = context.getDepartureSequences().get(DSASTestUtils.RUNWAY).getAtIndex(1).getSlotMarker().getDepartureTime();
        int newDepartureTime3 = context.getDepartureSequences().get(DSASTestUtils.RUNWAY).getAtIndex(2).getSlotMarker().getDepartureTime();
        int newDepartureTime4 = context.getDepartureSequences().get(DSASTestUtils.RUNWAY).getAtIndex(3).getSlotMarker().getDepartureTime();
        System.err.println(" departure 1 at t=" + newDepartureTime1);
        System.err.println(" departure 2 at t=" + newDepartureTime2);
        System.err.println(" departure 3 at t=" + newDepartureTime3);
        System.err.println(" departure 4 at t=" + newDepartureTime4);


    }

	/*
     * Not using DSASTestUtils since need longer and custom trajectories
	 */

    protected static SimulatedSlotMarker createDeparture(int index, int initStartTime) {
        SimulatedSlotMarker afo2 = new SimulatedSlotMarker("a_" + index, new Position(DSASTestUtils.AIRPORT.getLatitude(), DSASTestUtils.AIRPORT.getLongitude(), 0), 250, 0, 0, initStartTime, initStartTime, Constants.IS_FLYING);
        FlightPlan fpl1 = createDepartureFlighPlan(afo2);
        afo2.setFlightPlan(fpl1);
        afo2.setStatus(Constants.ON_GROUND);
//		afo2.setTrafficType(TrafficType.DEPARTURE);
//		afo2.setStartTime(initStartTime);
        return afo2;
    }

    protected static FlightPlan createDepartureFlighPlan(AFO afo) {
        FlightPlan fpl = new FlightPlan("departure");
        FlightSegment departSeg = new FlightSegment("R_A", afo.getName(), DSASTestUtils.RUNWAY, DSASTestUtils.WPT_A, 10000, 220);
        fpl.addSegment(departSeg);
        fpl.setInitialSegment(departSeg);
        fpl.setCurrentSegment(departSeg);
        fpl.setDepartureRunway(DSASTestUtils.RUNWAY);
        return fpl;
    }


    public static void main(String[] args) {
        DSASTestUtils utils = new DSASTestUtils();
        System.err.println(utils);
        new ParametricDSASTestWithCandidates().test1();
//		ReportAppLauncher.main(new String[0]);
    }
}
