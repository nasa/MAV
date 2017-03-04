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
package gov.nasa.arc.atc.simulation;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;

import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * @author hamon
 *
 */
public abstract class SimulatedTrajectory<T extends AFO> {

	static final boolean DEBUG = false;

	protected static final Logger LOG = Logger.getGlobal();

	private final T slot;
	private final int simDuration;

	public SimulatedTrajectory(T slotMarker, int simulationDuration) {
		slot = slotMarker;
		simDuration = simulationDuration;
	}

	@Deprecated
	public SimulatedTrajectory(T slotMarker) {
		this(slotMarker, Integer.MAX_VALUE);
	}

    public final T getSlotMarker() {
		return slot;
	}

	public int getSimulationDuration() {
		return simDuration;
	}

	/**
	 * 
	 * @param simulationTime simulates the trajectory, starting from the specified time
   	 * @param delay the delay induced to the slot for metering purposes
	 */
	public abstract void recalculateTrajectoryWithDelayAt(int simulationTime, int delay);

	public abstract TimedFlightParameters getParametersAtSimulationTime(int time);

	public abstract String getWaypointAimedAtSimulationTime(int time);

	public abstract int getOriginalArrivalTime();

	public abstract int getOriginalStartTime();

	public abstract int getOriginalArrivalTimeAtNode(String nodeName);

	public abstract int getArrivalTime();

	public abstract int getArrivalTimeAtNode(String nodeName);

	public abstract int getETAAtSimulationTime(int simulationTime);

	public abstract void printDebug();

	public abstract List<TimedFlightParameters> getTimedFlightParameters();

	public abstract int getInFlightDelay();

	public abstract int getCrossingTime(ATCNode node, int meterFixDistance);

}
