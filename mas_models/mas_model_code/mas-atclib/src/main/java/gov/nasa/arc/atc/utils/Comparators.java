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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import java.util.Comparator;

/**
 * 
 * @author hamon
 * @author krantz
 *
 */
public class Comparators {

	/*
	 * COMPARATORS
	 */

    public static final Comparator<AFO> START_TIME_COMPARATOR = Comparators::compareAfoStartTime;

	public static final Comparator<AfoUpdate> ARRIVING_AFOUPDATE_COMPARATOR = Comparators::compareArrivingAFOUpdate;
	public static final Comparator<AfoUpdate> TIMESTAMP_AFOUPDATE_COMPARATOR = Comparators::compareTimeStampAFOUpdate;

	/**
	 * Comparator for distance to meter fix.
     	 * 
	 */
	public static final Comparator<AFO> DISTANCE_TO_METERFIX_COMPARATOR = Comparators::compareAfoMeterFixDistance;

	/**
	 * ETA Comparator. Comparator for arrival time.
	 */
    public static final Comparator<SimulatedTrajectory> SIMULATED_TRAJECTORY_ORIGINAL_ARRIVAL_TIME_COMPARATOR = Comparators::compareSimulatedTrajectoryOrigialArrivalTime;

	/**
	 * ETA Comparator. Comparator for arrival time.
	 */
    public static final Comparator<SimulatedTrajectory> SIMULATED_TRAJECTORY_ORIGINAL_DEPARTURE_TIME_COMPARATOR = Comparators::compareSimulatedTrajectoryOriginalDepartureTime;

	/*
	 * PRIVATE CONSTRUCTOR
	 */

	private Comparators() {
		// private utility constructor
	}

	/*
	 * COMPARE METHODS
	 */

	/**
	 * 
	 * @param afo1 first {@link AFO} to compare
	 * @param afo2 second {@link AFO} to compare
	 * @return {@link Integer} compararison of the arrival time at respective meterfix
	 */
	public static final int compareAfoMeterFixDistance(AFO afo1, AFO afo2) {
		return Double.compare(FlightPlanUtils.calculateDistanceInTrackTo(afo1, FlightPlanUtils.findMeterfix(afo1.getFlightPlan())), FlightPlanUtils.calculateDistanceInTrackTo(afo2, FlightPlanUtils.findMeterfix(afo2.getFlightPlan())));
	}

	/**
	 * 
	 * @param afo1 first {@link AFO} to compare
	 * @param afo2 second {@link AFO} to compare
	 * @return {@link Integer} compararison of the arrival time at respective meterfix
	 */
	public static final int compareAfoStartTime(AFO afo1, AFO afo2) {
		return Integer.compare(afo1.getStartTime(), afo2.getStartTime());
	}

	/**
	 * 
	 * @param update1 first {@link AfoUpdate} to compare
	 * @param update2 second {@link AfoUpdate} to compare
	 * @return {@link Integer} compararison of respective arrival time
	 */
	public static final int compareArrivingAFOUpdate(AfoUpdate update1, AfoUpdate update2) {
		return Double.compare(update1.getEta(), update2.getEta());
	}

	/**
	 * 
	 * @param update1 first {@link AfoUpdate} to compare
	 * @param update2 second {@link AfoUpdate} to compare
	 * @return {@link Integer} compararison of respective time stamp
	 */
	public static final int compareTimeStampAFOUpdate(AfoUpdate update1, AfoUpdate update2) {
		return Integer.compare(update1.getTimeStamp(), update2.getTimeStamp());
	}

	/**
	 * 
	 * @param arr1 first {@link SimulatedTrajectory} to compare
	 * @param arr2 second {@link SimulatedTrajectory} to compare
	 * @return {@link Integer} compararison of respective original arrival time
	 */
	public static final int compareSimulatedTrajectoryOrigialArrivalTime(SimulatedTrajectory arr1, SimulatedTrajectory arr2) {
		return Integer.compare(arr1.getOriginalArrivalTime(), arr2.getOriginalArrivalTime());
	}

	/**
	 * 
	 * @param arr1 first {@link SimulatedTrajectory} to compare
	 * @param arr2 second {@link SimulatedTrajectory} to compare
	 * @return {@link Integer} compararison of respective original departure time
	 */
	public static final int compareSimulatedTrajectoryOriginalDepartureTime(SimulatedTrajectory arr1, SimulatedTrajectory arr2) {
		return Integer.compare(arr1.getOriginalStartTime(), arr2.getOriginalStartTime());
	}

	/**
	 * 
	 * @param node the node for the comparison
	 * @return {@link Integer} compararison of respective arrival time at that node
	 */
	public static final Comparator<SimulatedTrajectory> getSimulatedTrajectoryOrignalWptTimeComparator(ATCNode node) {
		return new ATCNodeSpecificComparator<SimulatedTrajectory>(node) {

			@Override
			public int compare(SimulatedTrajectory arr1, SimulatedTrajectory arr2) {
				double eta1 = arr1.getOriginalArrivalTimeAtNode(node.getName());
				double eta2 = arr2.getOriginalArrivalTimeAtNode(node.getName());
				return Double.compare(eta1, eta2);
			}
		};
	}

	/**
	 * 
	 * @param node the node for the comparison
	 * @return {@link Integer} compararison of respective arrival start time at that node
	 */
	public static final Comparator<SimulatedTrajectory> getSimulatedTrajectoryOrignalStartTimeComparator(ATCNode node) {
		return new ATCNodeSpecificComparator<SimulatedTrajectory>(node) {

			@Override
			public int compare(SimulatedTrajectory dep1, SimulatedTrajectory dep2) {
//                System.err.println(" ------- ");
//                System.err.println(" Comparing "+dep1);
//                System.err.println(" ... with  "+dep2);
				return Integer.compare(dep1.getOriginalStartTime(), dep2.getOriginalStartTime());
			}
		};
	}
}
