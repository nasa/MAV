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

package gov.nasa.arc.atc.simulation;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.CalculationTools;

public class TrajectoryChecker {

	private static final double PRECISON = 0.1;

	private TrajectoryChecker() {
		// private utility constructor
	}

	public static Object checkHorizontalSeparation(ATCNode arrivalNode, SimulatedTrajectory traj1, SimulatedTrajectory traj2) {
		List<SeparationViolation> sepViolations = new ArrayList<>();
		// calculate the period for which the distance need to be checked. Once landed, no check needed
		int maxWindow = Math.min(traj1.getArrivalTime(), traj2.getArrivalTime());
		// for each simulation step, check the separation
		FlightParameters param1;
		FlightParameters param2;
		// verification shall only occur when both slots are flying
		int startCheck = Math.max(traj1.getSlotMarker().getStartTime(), traj2.getSlotMarker().getStartTime());

		for (int i = startCheck; i < maxWindow; i++) {
			// retrieve coordinates
			param1 = traj1.getParametersAtSimulationTime(i);
			param2 = traj2.getParametersAtSimulationTime(i);
			// calculate the distances to the airport of the closest
			double airportD1 = AfoUtils.getHorizontalDistance(arrivalNode.getLatitude(), arrivalNode.getLongitude(), param1.getLatitude(), param1.getLongitude());
			double airportD2 = AfoUtils.getHorizontalDistance(arrivalNode.getLatitude(), arrivalNode.getLongitude(), param2.getLatitude(), param2.getLongitude());

			// determining the minimum separation distance,
			double minSeparationReq = Math.max(CalculationTools.minumumRequiredSeparation(airportD1), CalculationTools.minumumRequiredSeparation(airportD2));

			// calculate the horizontal separation
			double hSeparation = AfoUtils.getHorizontalDistance(param1.getLatitude(), param1.getLongitude(), param2.getLatitude(), param2.getLongitude());

			if (hSeparation < minSeparationReq - PRECISON) {
				// create the violation report
				sepViolations.add(new SeparationViolation(minSeparationReq, hSeparation, traj1.getSlotMarker().getName(), traj2.getSlotMarker().getName(), i));
			}
		}
		if (!sepViolations.isEmpty()) {
			return sepViolations;
		}

		// every distance check went OK
		return true;
	}

}
