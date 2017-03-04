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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Route;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.FlightPlanUtils;

/**
 * 
 * @author ahamon
 *
 */
public class MeterFixCandidateFunction implements CandidateFunction {

	public static final int DEFAULT_INBOUND_METERFIX_TIME = 1200;

	private final List<ATCNode> meterFixes;
	private final ATCGeography atcGeography;
	private final int timeToFix;

	/**
	 * 
	 * @param geography the {@link ATCGeography} containing the waypoints and meterfixes
	 * @param timeToMeterFix the time, in [s] before the meter fix when candidates are eligible. If the time is negative, candidate become eligible only after flying by the meter fix point.
	 */
	public MeterFixCandidateFunction(ATCGeography geography, int timeToMeterFix) {
		atcGeography = geography;
		meterFixes = atcGeography.getWaypoints().stream().filter(wpt -> wpt.isMeterFix()).collect(Collectors.toList());
		timeToFix = timeToMeterFix;
	}

	@Override
	public boolean isCandidate(SimulatedTrajectory trajectory, final int currentTime) {
		// since in current version, flight plan are not complete, we need to find if they are on a route that passes via a meterfix point

		// first case, the meter fix is in the flight plan
		for (ATCNode meterFix : meterFixes) {
			switch (FlightPlanUtils.getInitialNodeNavigationType(meterFix, trajectory.getSlotMarker().getFlightPlan())) {
			case FROM:
				return true;
			case TOWARDS:
				return trajectory.getArrivalTimeAtNode(meterFix.getName()) - currentTime <= timeToFix;
			default:
				// nothing to do
				break;
			}
		}

		// second case, the meter fix is not in the flight plan
		//// then if a route from the geography goes throught a meter fix
		ATCNode firstToWaypoint = trajectory.getSlotMarker().getFlightPlan().getInitialSegment().getToWaypoint();
		for (Route route : atcGeography.getArrivalRoutes()) {
			for (ATCNode meterFix : meterFixes) {
				if (route.passesBy(firstToWaypoint) && route.passesBy(meterFix)) {
					// assume true since may have passed the meterfix
					return true;
				}
			}

		}

		return false;
	}

	public List<ATCNode> getMeterFixes() {
		return Collections.unmodifiableList(meterFixes);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" timeToFix=").append(timeToFix);
		return sb.toString();
	}

}
