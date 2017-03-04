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

/*
* *******************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.airborne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.nasa.arc.atc.Sequence;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.Comparators;

/**
 * 
 * @author hamon
 *
 */
public class DepartureSequence implements Sequence {

	private final ATCNode departureNode;
	private final List<SimulatedTrajectory> departureTrajectories;

	public DepartureSequence(ATCNode departure) {
		departureNode = departure;
		departureTrajectories = new ArrayList<>();
	}

	public DepartureSequence(ATCNode departure, List<SimulatedTrajectory> trajectories) {
		departureNode = departure;
		departureTrajectories = new ArrayList<>();
		addRelevantTrajectories(trajectories);
	}

	private void addRelevantTrajectories(List<SimulatedTrajectory> trajectories) {
		//TODO use filter
		trajectories.forEach(trajectory -> {
			if (trajectory.getSlotMarker().getFlightPlan().goesThrought(departureNode)) {
				addSimulatedTrajectory(trajectory);
			}
		});
	}

	@Override
	public ATCNode getATCNode() {
		return departureNode;
	}

	@Override
	public void addSimulatedTrajectory(SimulatedTrajectory arrivalTrajectory) {
        departureTrajectories.add(arrivalTrajectory);
		Collections.sort(departureTrajectories, Comparators.getSimulatedTrajectoryOrignalStartTimeComparator(departureNode));
	}

	@Override
	public List<SimulatedTrajectory> getSimulatedTrajectories() {
		return Collections.unmodifiableList(departureTrajectories);
	}

	@Override
	public SimulatedTrajectory getAtIndex(int arrivalIndex) {
		return departureTrajectories.get(arrivalIndex);
	}

	@Override
	public void clear() {
		departureTrajectories.clear();
	}

	public int getNbDepartures() {
		return departureTrajectories.size();
	}

	public ATCNode getDepartureNode() {
		return departureNode;
	}

	public void reOrder() {
		Collections.sort(departureTrajectories, Comparators.SIMULATED_TRAJECTORY_ORIGINAL_DEPARTURE_TIME_COMPARATOR);
	}

	@Override
	public String toString() {
		return 	getClass().getSimpleName()+" @"+departureNode.getName()+" : "+departureTrajectories;
	}

}
