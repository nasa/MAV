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

package gov.nasa.arc.atc.algos;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.utils.Constants;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.ConsoleUtils;

public class SimulationContextTest {

    // TEMP
    private static final int DEFAULT_SIM_DURATION = Integer.MAX_VALUE;

    private static SimulationContext instance;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ConsoleUtils.setLoggingLevel(Level.SEVERE);
        ATCGeography geography = ATCGeographyTest.createTestGeography1();
        instance = createContext1(geography);
    }

    @Test
    public void testGetGeography() {
        assertNotNull(instance.getGeography());
    }

    @Test
    public void testAt0() {
        instance.getClock().setTime(0);
        instance.updateActiveSlots();
        assertNotNull(instance.getAllSlots());
        assertEquals(2, instance.getAllSlots().size());
        assertNotNull(instance.getActiveSlots());
        assertEquals(0, instance.getActiveSlots().size());
    }

    @Test
    public void testAt2() {
        instance.getClock().setTime(2);
        instance.updateActiveSlots();
        assertNotNull(instance.getAllSlots());
        assertEquals(2, instance.getAllSlots().size());
        assertNotNull(instance.getActiveSlots());
        assertEquals(1, instance.getActiveSlots().size());
    }

    @Test
    public void testAt4() {
        instance.getClock().setTime(4);
        instance.updateActiveSlots();
        assertNotNull(instance.getAllSlots());
        assertEquals(2, instance.getAllSlots().size());
        assertNotNull(instance.getActiveSlots());
        assertEquals(2, instance.getActiveSlots().size());
    }

    /**
     * Tests that a departure not started is not added to a departure sequence
     * and tests that departures are removed from departure queues at departureTime +1
     */
    @Test
    public void testTakeOffs1() {
        int startTime1 = 10;
        int startTime2 = 13;
        //
        FlightPlan plan1 = FlightPlanTest.createTestPlanDeparture1();
        SimulatedSlotMarker dep1 = new SimulatedSlotMarker("testDep1", new Position(37.5, -70, 0), 400, 0, 0, startTime1, startTime1, Constants.NO_STARTED);
        dep1.setFlightPlan(plan1);
        //
        FlightPlan plan2 = FlightPlanTest.createTestPlanDeparture1();
        SimulatedSlotMarker dep2 = new SimulatedSlotMarker("testDep2", new Position(37.5, -70, 0), 400, 0, 0, startTime2, startTime2, Constants.NO_STARTED);
        dep2.setFlightPlan(plan2);

        List<SimulatedSlotMarker> afos = new ArrayList<>();
        afos.add(dep1);
        afos.add(dep2);
        //
        ATCGeography geography = ATCGeographyTest.createTestGeography1();
        SimulationContext context = new SimulationContext(geography);
        context.setSimulationDuration(DEFAULT_SIM_DURATION);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        //
        context.getClock().setTime(startTime1);
        context.updateActiveSlots();

        Runway runway = context.getGeography().getAirports().get(0).getRunways().get(0);
        // shall be 1 since departure 2 is not started yet
        assertEquals(1, context.getDepartureSequences().get(runway).getNbDepartures());


        context.getClock().setTime(startTime1 + 1);
        context.updateActiveSlots();
        // shall be 0 since departure 1 took off and departure 2 is not started yet
        assertEquals(0, context.getDepartureSequences().get(runway).getNbDepartures());


        context.getClock().setTime(startTime2);
        context.updateActiveSlots();
        // shall be 1 since departure 1 took off and departure 2 has just started
        assertEquals(1, context.getDepartureSequences().get(runway).getNbDepartures());


        context.getClock().setTime(startTime2 + 1);
        context.updateActiveSlots();
        // shall be 0 since departure 1 and 2 took off
        assertEquals(0, context.getDepartureSequences().get(runway).getNbDepartures());
    }

    /**
     * Tests that a departure not started is not added to a departure sequence
     */
    @Test
    public void testTakeOffs2() {
        int startTime = 10;
        int departureTime1 = startTime + 1;
        int departureTime2 = departureTime1 + 1;
        //
        FlightPlan plan1 = FlightPlanTest.createTestPlanDeparture1();
        SimulatedSlotMarker dep1 = new SimulatedSlotMarker("testDep1", new Position(37.5, -70, 0), 400, 0, 0, startTime, departureTime1, Constants.NO_STARTED);
        dep1.setFlightPlan(plan1);
        //
        FlightPlan plan2 = FlightPlanTest.createTestPlanDeparture1();
        SimulatedSlotMarker dep2 = new SimulatedSlotMarker("testDep2", new Position(37.5, -70, 0), 400, 0, 0, startTime, departureTime2, Constants.NO_STARTED);
        dep2.setFlightPlan(plan2);

        List<SimulatedSlotMarker> afos = new ArrayList<>();
        afos.add(dep1);
        afos.add(dep2);
        //
        ATCGeography geography = ATCGeographyTest.createTestGeography1();
        SimulationContext context = new SimulationContext(geography);
        context.setSimulationDuration(DEFAULT_SIM_DURATION);
        context.addSlots(afos);
        context.calculateReferenceTrajectories();
        //
        context.getClock().setTime(startTime);
        context.updateActiveSlots();

        Runway runway = context.getGeography().getAirports().get(0).getRunways().get(0);
        // shall be 2 since both departures are started and have not taken off
        assertEquals(2, context.getDepartureSequences().get(runway).getNbDepartures());


        context.getClock().setTime(departureTime1 + 1);
        context.updateActiveSlots();
        // shall be 0 since departure 1 took off and departure 2 has not
        assertEquals(1, context.getDepartureSequences().get(runway).getNbDepartures());

        context.getClock().setTime(departureTime2 + 1);
        context.updateActiveSlots();
        // shall be 0 since departure 1 and 2 took off
        assertEquals(0, context.getDepartureSequences().get(runway).getNbDepartures());
    }


    private static SimulationContext createContext1(ATCGeography geography) {
        SimulationContext context = new SimulationContext(geography);
        context.setSimulationDuration(DEFAULT_SIM_DURATION);
        //
        FlightPlan plan1 = FlightPlanTest.createTestPlanArrival1();
        FlightPlan plan2 = FlightPlanTest.createTestPlanArrival2();
        //
        int startTime1 = 2;
        int startTime2 = 4;
        //
        SimulatedSlotMarker s1 = new SimulatedSlotMarker("s1", new Position(39.5, -70, 0), 250, 0, 0, startTime1, -1, Constants.NO_STARTED);
        SimulatedSlotMarker s2 = new SimulatedSlotMarker("s2", new Position(39.5, -70, 0), 250, 0, 0, startTime2, -1, Constants.NO_STARTED);
        //
        s1.setFlightPlan(plan1);
        s2.setFlightPlan(plan2);
        //
        plan1.start();
        plan2.start();
        //
        List<SimulatedSlotMarker> slots = new LinkedList<>();
        slots.add(s1);
        slots.add(s2);
        context.addSlots(slots);
        context.calculateReferenceTrajectories();
        //
        return context;
    }

    public static SimulationContext createContext2(ATCGeography geography) {
        SimulationContext context = new SimulationContext(geography);
        //
        FlightPlan plan1 = FlightPlanTest.createTestPlanArrival1();
        FlightPlan plan2 = FlightPlanTest.createTestPlanArrival2();
        FlightPlan plan3 = FlightPlanTest.createTestPlanArrival1();
        //
        SimulatedSlotMarker s1 = new SimulatedSlotMarker("s1", new Position(39.5, -70, 0), 250, 0, 0, 2, 2, Constants.NO_STARTED);
        SimulatedSlotMarker s2 = new SimulatedSlotMarker("s2", new Position(39.5, -70, 0), 250, 0, 0, 4, 4, Constants.NO_STARTED);
        SimulatedSlotMarker s3 = new SimulatedSlotMarker("s3", new Position(39.5, -70, 0), 250, 0, 0, 10, 10, Constants.NO_STARTED);
        //
        s1.setFlightPlan(plan1);
        s2.setFlightPlan(plan2);
        s3.setFlightPlan(plan3);
        //
        plan1.start();
        plan2.start();
        plan3.start();
        //
        List<SimulatedSlotMarker> slots = new LinkedList<>();
        slots.add(s1);
        slots.add(s2);
        slots.add(s3);
        context.addSlots(slots);
        context.calculateReferenceTrajectories();
        //
        return context;
    }

}
