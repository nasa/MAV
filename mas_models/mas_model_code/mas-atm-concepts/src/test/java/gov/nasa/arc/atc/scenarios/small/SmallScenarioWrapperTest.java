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

package gov.nasa.arc.atc.scenarios.small;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

//import gov.nasa.arc.atc.airborne.AFO;
//import gov.nasa.arc.atc.airborne.SlotMarker;
//import gov.nasa.arc.atc.airborne.impl.SlotMarkerImpl;
//import gov.nasa.arc.atc.brahms.BrahmsFlightPlan;
//import gov.nasa.arc.atc.brahms.BrahmsFlightPlanFactory;
//import gov.nasa.arc.atc.brahms.parsers.AirportBrahmsParser;
//import gov.nasa.arc.atc.brahms.parsers.FlightPlanBrahmsParser;
//import gov.nasa.arc.atc.brahms.parsers.SegmentBrahmsParser;
//import gov.nasa.arc.atc.brahms.parsers.WaypointParser;
//import gov.nasa.arc.atc.geography.ATCGeography;
//import gov.nasa.arc.atc.geography.Airport;
//import gov.nasa.arc.atc.geography.FlightPlan;
//import gov.nasa.arc.atc.geography.FlightSegment;
//import gov.nasa.arc.atc.geography.Waypoint;
//import gov.nasa.arc.atc.simulation.wrapper.BrahmsWrapper;

/**
 * 
 * @author ahamon
 *
 */
public class SmallScenarioWrapperTest {

//	private static Map<String, Airport> airports;
//	private static Map<String, Waypoint> waypoints;
//	private static Map<String, FlightSegment> flightSegments;
//	private static Map<String, BrahmsFlightPlan> brahmsFlightPlans;
//	private static ATCGeography geography;

	@Before
	public void initBefore() {
		//TODO fix class
//		File waypointsFile = new File(SmallScenarioWrapperTest.class.getResource("allwaypoints.b").getPath());
//		File airportsFile = new File(SmallScenarioWrapperTest.class.getResource("Airports.b").getPath());
//		File flightSegmentsFile = new File(SmallScenarioWrapperTest.class.getResource("allFlightSegments.b").getPath());
//		File flightPlanFile = new File(SmallScenarioWrapperTest.class.getResource("allFlightPlans.b").getPath());
//		airports = AirportBrahmsParser.parseAirports(airportsFile);
//		waypoints = WaypointParser.parseWaypoints(waypointsFile);
//		flightSegments = SegmentBrahmsParser.parseSegments(waypoints, flightSegmentsFile);
//		brahmsFlightPlans = FlightPlanBrahmsParser.parseBrahmsFPLs(flightPlanFile, flightSegments);
//		geography = new ATCGeography("No Name");
//		airports.values().stream().forEach(airport -> geography.addAirport(airport));
//		waypoints.values().stream().forEach(waypoint -> geography.addWaypoint(waypoint));
//		// allFlights = SimulatedSlotBrahmsParser.parseSlotAgents(geography, brahmsFlightPlans, flightSegments, scenarioFile);
	}

	@Test
    public void todoTest(){
        System.err.println("TODO");
    }
//
//	@Ignore
//	@Test
//	public void testT14() {
//		List<AFO> slots = new ArrayList<>();
//		slots.add(createSlotSWA1837());
//		slots.add(createSlotJBU6365());
//		System.out.println("SLOTS:: " + slots);
//		BrahmsWrapper.invokeTSS(this.getClass().getName(), slots, 12);
//	}
//
//	private static SlotMarker createSlotSWA1837() {
//		// // !! had to set vspeed AND heading to 0 !!
//		SlotMarkerImpl s = new SlotMarkerImpl("SWA1837", 218, 0, 40.68986, 74.0087, 8561.12, 0);
//		s.setStartTime(12);
//		FlightPlan flightPlan = BrahmsFlightPlanFactory.createFullFlightPlan(brahmsFlightPlans.get("SWA1837"), 0, geography, flightSegments);
//		System.out.println("SWA1837 FPL=" + flightPlan);
//		s.setFlightPlan(flightPlan);
//		return s;
//	}
//
//	private static SlotMarker createSlotJBU6365() {
//		// // !! had to set vspeed AND heading to 0 !!
//		SlotMarkerImpl s = new SlotMarkerImpl("JBU6365", 239, 0, 41.25311, 73.71723, 8755.76, 0);
//		s.setStartTime(40);
//		FlightPlan flightPlan = BrahmsFlightPlanFactory.createFullFlightPlan(brahmsFlightPlans.get("JBU6365"), 0, geography, flightSegments);
//		System.out.println("JBU6365 FPL=" + flightPlan);
//		s.setFlightPlan(flightPlan);
//		// s.setFlightPlan(flightPlan);
//		// for(SimulatedSlotMarker s : allFlights){
//		// if(s.getName().endsWith("JBU6365")){
//		// return s;
//		// }
//		// }
//		return s;
//	}

}
