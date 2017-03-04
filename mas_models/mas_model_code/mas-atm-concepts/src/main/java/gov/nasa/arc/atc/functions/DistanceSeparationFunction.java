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

package gov.nasa.arc.atc.functions;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.CalculationTools;

/**
 * 
 * @author ahamon
 *
 */
public class DistanceSeparationFunction implements SeparationFunction {

	private final ATCNode airport;

	// private FlightParameters leadParameters;
	// private FlightParameters trailParameters;
	// private double leadDistanceToAirport;
	// private double trailDistanceToAirport;
	// private double minSeparationReq;
	// private double horizontalDistance;

	public DistanceSeparationFunction(ATCNode arrivalAirport) {
		airport = arrivalAirport;
	}

	@Override
	public boolean areSeparated(final int time, SimulatedTrajectory lead, SimulatedTrajectory trail) {
		// hum... duplication!!

		final FlightParameters leadParameters = lead.getParametersAtSimulationTime(time);
		final FlightParameters trailParameters = trail.getParametersAtSimulationTime(time);

		// calculate the distance of both slots to the airport
		// -> in some cases, the trailer can be closer to the airport
		final double leadDistanceToAirport = AfoUtils.getHorizontalDistance(airport.getLatitude(), airport.getLongitude(), leadParameters.getLatitude(), leadParameters.getLongitude());
		final double trailDistanceToAirport = AfoUtils.getHorizontalDistance(airport.getLatitude(), airport.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());

		// deduce the minimum separation needed: it is the maximum separation
		// needed at each slot's position
		final double minSeparationReq = Math.max(CalculationTools.minumumRequiredSeparation(leadDistanceToAirport), CalculationTools.minumumRequiredSeparation(trailDistanceToAirport));

		// calculate the separation distance between the slots' centers
		final double horizontalDistance = AfoUtils.getHorizontalDistance(leadParameters.getLatitude(), leadParameters.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());
		//
		return horizontalDistance > minSeparationReq;
	}

	@Override
	public int calculateDelayNeeded(final int time, SimulatedTrajectory lead, SimulatedTrajectory trail) {

		// TODO: some test for airport destination ?? too specific ??

		final FlightParameters leadParameters = lead.getParametersAtSimulationTime(time);
		FlightParameters trailParameters = trail.getParametersAtSimulationTime(time);
		// if (withLog) {
		// LOG.log(FINE, " ~~~ lead.getArrivalTime() :: {0} ", lead.getArrivalTime());
		// LOG.log(FINE, " ~~~ trail.getArrivalTime() :: {0} ", trail.getArrivalTime());
		// LOG.log(FINE, " ~~~ leadParameters :: {0} ", leadParameters);
		// LOG.log(FINE, " ~~~ trailParameters :: {0} ", trailParameters);
		// }

		// calculate the distance of both slots to the airport
		// -> in some cases, the trailer can be closer to the airport
		final double leadDistanceToAirport = AfoUtils.getHorizontalDistance(airport.getLatitude(), airport.getLongitude(), leadParameters.getLatitude(), leadParameters.getLongitude());
		final double trailDistanceToAirport = AfoUtils.getHorizontalDistance(airport.getLatitude(), airport.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());

		// deduce the minimum separation needed: it is the maximum separation
		// needed at each slot's position
		final double minSeparationReq = Math.max(CalculationTools.minumumRequiredSeparation(leadDistanceToAirport), CalculationTools.minumumRequiredSeparation(trailDistanceToAirport));

		// calculate the separation distance between the slots' centers
		double horizontalDistance = AfoUtils.getHorizontalDistance(leadParameters.getLatitude(), leadParameters.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());

		// if too close, or trail in front meter
		// if (horizontalDistance < minSeparationReq ) { //|| additionalDelay > 0
		// if (withLog)
		// LOG.log(FINE, " Metering 2 planes : :: {0} and {1} ", new Object[] { lead.getSlotMarker().getName(), trail.getSlotMarker().getName() });

		// !! Not optimal for performance but more accurate
		// calculate time difference to meet separation
		int delayNeeded = 0;

		while (horizontalDistance < minSeparationReq) {// || additionalDelay > delayNeeded
			delayNeeded++;
			// if (withLog) {
			// LOG.log(FINE, " $ incr delayNeeded -> {0} ", delayNeeded);
			// LOG.log(FINE, " $ etaLead - delayNeeded -> {0} ", etaLead - delayNeeded);
			// }
			// TODO: account for the fact that etaLead - delayNeed can be negative !!!
			trailParameters = trail.getParametersAtSimulationTime(time - delayNeeded);
			horizontalDistance = AfoUtils.getHorizontalDistance(leadParameters.getLatitude(), leadParameters.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());
			// if (withLog) {
			// LOG.log(FINE, " $ horizontalDistance -> {0} ", horizontalDistance);
			// }
		}
		//
		// if (withLog) {
		// LOG.log(FINE, " $ horizontalDistance -> {0} ", horizontalDistance);
		// LOG.log(INFO, " -> delay needed : {0} ", delayNeeded);
		// }

		// ReverseSimulationUtils.introduceDelay(context, simulationTime, trail, delayNeeded);
		// reportValues.put(ReportItemProperties.DELAY_NEEDED, delayNeeded);
		// String newTrailToWaypoint = trail.getWaypointAimedAtSimulationTime(simulationTime);
		// reportValues.put(ReportItemProperties.TRAIL_NEW_PARAMETERS, FlightParameters.clone(trailParameters));
		// reportValues.put(ReportItemProperties.TRAIL_NEW_SEGMENT, trail.getSlotMarker().getFlightPlan().getSegmentEndingAt(newTrailToWaypoint));
		// //
		// if (logWithDSAS) {
		// DSASReporter.logScheduleConflictDetected(simulationTime, reportValues);
		// } else {
		// TSSReporter.logScheduleConflictDetected(simulationTime, reportValues);
		// }

		// }

		return delayNeeded;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" using distance to ").append(airport.getName());
		return sb.toString();
	}
}
