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

package gov.nasa.arc.atc.brahms.parsers;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.brahms.BrahmsFlightPlan;
import gov.nasa.arc.atc.brahms.BrahmsFlightPlanFactory;
import gov.nasa.arc.atc.brahms.BrahmsInstanceInitialConfiguration;
import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.FlightPlanUtils;
import gov.nasa.arc.atc.utils.SimulationProperties;

/**
 * 
 * @author ahamon
 *
 */
public class SimulatedSlotBrahmsParser {

	public static final String CURRENT_SEGMENT_ID = "iCurrentSegment";
	// TODO clean the files
	public static final String ALTERNATE_START_TIME = "startTime";

	private static final Logger LOG = Logger.getGlobal();

	private SimulatedSlotBrahmsParser() {
		// private utility constructor
	}

	/**
	 * 
	 * @param geography
	 * @param flightPlans
	 * @param flightSegments
	 * @param agentsFile
	 * @return
	 */
	public static List<SimulatedSlotMarker> parseSlotAgents(ATCGeography geography, Map<String, BrahmsFlightPlan> flightPlans, Map<String, FlightSegment> flightSegments, File agentsFile) {
		List<SimulatedSlotMarker> allSlots = new LinkedList<>();
		BrahmsAgentInitializationParser.parseBrahmsInstances(agentsFile).stream().forEach(slotInstance -> allSlots.add(parseSingleSlot(slotInstance, geography, flightPlans, flightSegments)));
		return Collections.unmodifiableList(allSlots);
	}

	private static SimulatedSlotMarker parseSingleSlot(BrahmsInstanceInitialConfiguration slotInstance, ATCGeography geography, Map<String, BrahmsFlightPlan> flightPlans, Map<String, FlightSegment> flightSegments) {
		// TODO test or use a input parameter
		final String sNameOfFlight = slotInstance.getName().split("_")[1];
		//
		final double dStartLat = Double.parseDouble(slotInstance.getInitialFacts().get(SimulationProperties.getProperty(SimulationProperties.LATITUDE_PPTY)));
		final double dStartLong = WaypointParser.WESTERLY_COORDINATES * Double.parseDouble(slotInstance.getInitialFacts().get(SimulationProperties.getProperty(SimulationProperties.LONGITUDE_PPTY)));
		//
		final int iStartTime;
		if (slotInstance.getInitialFacts().containsKey(SimulationProperties.getProperty(SimulationProperties.STARTED_TIME_PPTY))) {
			iStartTime = (int) Double.parseDouble(slotInstance.getInitialFacts().get(SimulationProperties.getProperty(SimulationProperties.STARTED_TIME_PPTY)));
		} else if (slotInstance.getInitialFacts().containsKey(ALTERNATE_START_TIME)) {
			iStartTime = (int) Double.parseDouble(slotInstance.getInitialFacts().get(ALTERNATE_START_TIME));
		} else {
			iStartTime = 0;
			LOG.log(Level.INFO, "{0} has no start time", slotInstance.getName());
		}
		//
		final int iStartSpeed = (int) Double.parseDouble(slotInstance.getInitialFacts().get(SimulationProperties.getProperty(SimulationProperties.SPEED_PPTY)));
		//
		final int dStartAlt;
		if (slotInstance.getInitialFacts().containsKey(SimulationProperties.getProperty(SimulationProperties.ALTITUDE_PPTY))) {
			dStartAlt = (int) Double.parseDouble(slotInstance.getInitialFacts().get(SimulationProperties.getProperty(SimulationProperties.ALTITUDE_PPTY)));
		} else {
			dStartAlt = 0;
			LOG.log(Level.INFO, "{0} has no start altitude, did not find key {1}", new Object[]{slotInstance.getName(),SimulationProperties.getProperty(SimulationProperties.ALTITUDE_PPTY)});
		}
		final int segmentID = Integer.parseInt(slotInstance.getInitialFacts().get(CURRENT_SEGMENT_ID));

		// TODO: ensure not saved in file
		int iStartVSpeed = 0;
		double dStartBearing = 0;
		System.err.println("TODO: set proper departure time in Brahms model, by default using startTime");

		SimulatedSlotMarker slotMarker = new SimulatedSlotMarker(sNameOfFlight,new Position(dStartLat, dStartLong, dStartAlt), iStartSpeed, iStartVSpeed,  dStartBearing, iStartTime,iStartTime,0);
		BrahmsFlightPlan bFPL = flightPlans.get(sNameOfFlight);
		FlightPlan flightPlan = BrahmsFlightPlanFactory.createFullFlightPlan(bFPL, segmentID, geography, flightSegments);

		slotMarker.setFlightPlan(flightPlan);
//		slotMarker.setTrafficType(FlightPlanUtils.getTrafficType(flightPlan, geography));
		//
		addingFlightPlanDepArrival(slotMarker, geography);
		return slotMarker;
	}

	private static void addingFlightPlanDepArrival(SimulatedSlotMarker slotMarker, ATCGeography geography) {
		System.err.println(" !@_addingFlightPlanDepArrival TODO ");
//		switch (slotMarker.getTrafficType()) {
//		case ARRIVAL:
//			setArrivalRunway(slotMarker, geography);
//			break;
//		case DEPARTURE:
//			setDepartureRunway(slotMarker, geography);
//			break;
//		default:
//			break;
//		}
	}

	private static void setArrivalRunway(SimulatedSlotMarker slotMarker, ATCGeography geography) {
		FlightPlan fPL = slotMarker.getFlightPlan();
		ATCNode lastWPT = fPL.getLastWaypoint();
		Runway arrivalR = (Runway) geography.getNodeByName(lastWPT.getName());
		fPL.setArrivalRunway(arrivalR);

	}

	private static void setDepartureRunway(SimulatedSlotMarker slotMarker, ATCGeography geography) {
		FlightPlan fPL = slotMarker.getFlightPlan();
		ATCNode firstWPT = slotMarker.getFlightPlan().getFirstWaypoint();
		ATCNode depR = geography.getNodeByName(firstWPT.getName());
		if (depR instanceof Runway) {
			Runway departureR = (Runway) depR;
			fPL.setDepartureRunway(departureR);
		} else {
			throw new IllegalArgumentException("" + depR.getName() + " is not a runway. aflo=" + slotMarker);
		}

	}

}
