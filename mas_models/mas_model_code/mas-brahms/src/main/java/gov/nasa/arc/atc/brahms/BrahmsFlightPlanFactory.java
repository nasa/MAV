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

package gov.nasa.arc.atc.brahms;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Route;

/**
 * 
 * @author ahamon
 *
 */
public final class BrahmsFlightPlanFactory {

	private static final Logger LOG = Logger.getGlobal();

	private BrahmsFlightPlanFactory() {
		// private utility constructor
	}

	public static final FlightPlan createFullFlightPlan(final BrahmsFlightPlan bFPL, final int initialBSegment, ATCGeography geography, Map<String, FlightSegment> flightSegments) {
		FlightPlan result = new FlightPlan(bFPL.getName());
		bFPL.getSegmentIDs().values().stream().forEach(result::addSegment);
		// set initial segment
		LOG.log(Level.FINE, "initialBSegment={0}", initialBSegment);
		FlightSegment initialSegment = bFPL.getSegmentIDs().get(initialBSegment);
		result.setInitialSegment(initialSegment);
		// complete the flight plan with known routes if necessary
		completeFlightPlan(result, geography, flightSegments);
		// TODO: put all the segments in the simulation context?
		assert result.isValid();
		return result;
	}

	private static void completeFlightPlan(final FlightPlan flightPlan, ATCGeography geography, final Map<String, FlightSegment> flightSegments) {
		if (!geography.isAnArrivalNode(flightPlan.getLastWaypoint().getName())) {
			return;
		}
		// if the flight plan is an arrival
		geography.getArrivalRoutes().stream().filter(route -> route.passesBy(flightPlan.getFirstWaypoint())).map(route -> getExistingSlotNameOnRoute(route, flightSegments)).forEach(slotName -> // find an existing afo flying that route
		addPreviousSegmentToFPL(flightPlan, slotName, flightSegments));
	}

	private static void addPreviousSegmentToFPL(FlightPlan flightPlan, String slotName, Map<String, FlightSegment> flightSegments) {
		final ATCNode endPrevious = flightPlan.getFirstWaypoint();
		final FlightSegment previous = fingSegmentEndingAt(endPrevious.getName(), slotName, flightSegments);
		if (previous != null) {
			flightPlan.addSegment(previous);
			addPreviousSegmentToFPL(flightPlan, slotName, flightSegments);
		}
	}

	private static FlightSegment fingSegmentEndingAt(String endWPT, String slotName, Map<String, FlightSegment> flightSegments) {
		for (String name : flightSegments.keySet()) {
			if (name.contains("TO_" + endWPT) && name.contains(slotName)) {
				return flightSegments.get(name);
			}
		}
		return null;
	}

	private static String getExistingSlotNameOnRoute(Route route, Map<String, FlightSegment> flightSegments) {
		String routeOrigin = route.getStart().getName();
		for (String name : flightSegments.keySet()) {
			if (name.contains(routeOrigin + "_TO")) {
				String[] splitName = name.split("_");
				return splitName[3];
			}
		}
		return null;
	}

}
