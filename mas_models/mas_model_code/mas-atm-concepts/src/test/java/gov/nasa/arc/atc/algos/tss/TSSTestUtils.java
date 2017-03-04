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

package gov.nasa.arc.atc.algos.tss;

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
 * 
 * @author ahamon
 *
 */
public class TSSTestUtils {

	protected static final Airport AIRPORT = new Airport("Airport", 40, -70);
	private static final Runway RUNWAY = new Runway("R", AIRPORT, 12);
	private static final Waypoint WPT_H = new Waypoint("wptH", 39.8, -70);
	private static final Waypoint WPT_G = new Waypoint("wptG", 39.6, -70);
	private static final Waypoint WPT_F = new Waypoint("wptF", 39.4, -70);
	private static final Waypoint WPT_E = new Waypoint("wptE", 39.2, -70);

	protected TSSTestUtils() {
		ConsoleUtils.setLoggingLevel(Level.OFF);
		AIRPORT.addRunway(RUNWAY);
	}

	public static void testDistance1(TSS tss) {
		// schedule a departure in a single gap (gap being created)
		SimulationContext context = new SimulationContext(createSimpleGeography());
		context.setSimulationDuration(Integer.MAX_VALUE);
		List<SimulatedSlotMarker> afos = new ArrayList<>();
		int startTime1 = 1;
		int startTime2 = startTime1 + 5;
		int startTime3 = startTime2 + 5;
		SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
		SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
		SimulatedSlotMarker arr3 = createArrival(2, startTime3, 39.4, -70);
		afos.add(arr1);
		afos.add(arr2);
		afos.add(arr3);
		context.addSlots(afos);
		context.calculateReferenceTrajectories();
		context.getClock().setTime(startTime3);
		context.updateActiveSlots();
		final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
		final int arrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
		final int arrivalTime3 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
		//
		System.err.println( "testDistance1 :: arrivalTime1="+arrivalTime1);
		System.err.println( "testDistance1 :: arrivalTime2="+arrivalTime2);
		System.err.println( "testDistance1 :: arrivalTime3="+arrivalTime3);
		//
		tss.initializeData(context, RUNWAY);
		tss.execute(startTime3 + 10);
		//

		final int arrivalTime2TSS = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
		final int arrivalTime3TSS = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
		System.err.println( "testDistance1 :: arrivalTime2TSS="+arrivalTime2TSS);
		System.err.println( "testDistance1 :: arrivalTime3TSS="+arrivalTime3TSS);
		// TEMP TODO: remove after functional TSS is working
		assertEquals(543, arrivalTime1);
		assertEquals(548, arrivalTime2);
		assertEquals(553, arrivalTime3);
		assertEquals(585, arrivalTime2TSS);
		assertEquals(627, arrivalTime3TSS);
	}
	
	public static void testTime1(TSS tss,int timeSep) {
		// schedule a departure in a single gap (gap being created)
		SimulationContext context = new SimulationContext(createSimpleGeography());
		List<SimulatedSlotMarker> afos = new ArrayList<>();
		int startTime1 = 1;
		int startTime2 = startTime1 + 5;
		int startTime3 = startTime2 + 5;
		SimulatedSlotMarker arr1 = createArrival(startTime1, 1, 39.4, -70);
		SimulatedSlotMarker arr2 = createArrival(2, startTime2, 39.4, -70);
		SimulatedSlotMarker arr3 = createArrival(2, startTime3, 39.4, -70);
		afos.add(arr1);
		afos.add(arr2);
		afos.add(arr3);
		context.addSlots(afos);
		context.calculateReferenceTrajectories();
		context.getClock().setTime(startTime3);
		context.updateActiveSlots();
		final int arrivalTime1 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(0).getArrivalTime();
		final int arrivalTime2 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
		final int arrivalTime3 = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
		//
		System.err.println( "testTime1 :: arrivalTime1="+arrivalTime1);
		System.err.println( "testTime1 :: arrivalTime2="+arrivalTime2);
		System.err.println( "testTime1 :: arrivalTime3="+arrivalTime3);
		//
		tss.initializeData(context, RUNWAY);
		tss.execute(startTime3 + 10);
		//

		final int arrivalTime2TSS = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(1).getArrivalTime();
		final int arrivalTime3TSS = context.getArrivalSequences().get(RUNWAY).getSimulatedTrajectories().get(2).getArrivalTime();
		System.err.println( "testTime1 :: arrivalTime2TSS="+arrivalTime2TSS);
		System.err.println( "testTime1 :: arrivalTime3TSS="+arrivalTime3TSS);
		// TEMP TODO: remove after functional TSS is working
		assertEquals(543, arrivalTime1);
		assertEquals(548, arrivalTime2);
		assertEquals(553, arrivalTime3);
		assertEquals(arrivalTime1+timeSep, arrivalTime2TSS);
		assertEquals(arrivalTime2TSS+timeSep, arrivalTime3TSS);
	}


	/*
	 * utils methods
	 */

	private static ATCGeography createSimpleGeography() {
		ATCGeography geography = new ATCGeography("testingTSSGeo");
		geography.addAirport(AIRPORT);
		geography.addWaypoint(WPT_H);
		geography.addWaypoint(WPT_G);
		geography.addWaypoint(WPT_F);
		geography.addWaypoint(WPT_E);
		return geography;
	}

	private static SimulatedSlotMarker createArrival(int index, int startTime, double lat, double lon) {
		SimulatedSlotMarker afo1 = new SimulatedSlotMarker("a_" + index, new Position(lat, lon, 5000), 280, 0,180,startTime,startTime, Constants.IS_FLYING);
		FlightPlan fpl1 = createArrivalFlightPlan(afo1);
		afo1.setFlightPlan(fpl1);
		afo1.setStatus(Constants.IS_FLYING);
//		afo1.setTrafficType(TrafficType.ARRIVAL);
//		afo1.setStartTime(startTime);
		return afo1;
	}

	

	private static FlightPlan createArrivalFlightPlan(AFO afo) {
		FlightPlan fpl = new FlightPlan("arrival");
		FlightSegment eF = new FlightSegment("E-F", afo.getName(), WPT_E, WPT_F, 5000, 280);
		FlightSegment fG = new FlightSegment("F-G", afo.getName(), WPT_F, WPT_G, 2500, 250);
		FlightSegment gH = new FlightSegment("G-H", afo.getName(), WPT_G, WPT_H, 2000, 230);
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

}
