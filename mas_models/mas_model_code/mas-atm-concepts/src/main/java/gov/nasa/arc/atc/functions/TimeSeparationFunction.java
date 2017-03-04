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
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.AfoUtils;

/**
 * 
 * @author ahamon
 *
 */
public class TimeSeparationFunction implements SeparationFunction {

	public static final double DISTANCE_EPSILON = 0.001;

	private final int timeSep;

	private FlightParameters leadParameters;
	private FlightParameters trailParameters;

	/**
	 * 
	 * @param separationTime the separation time between two consecutive slots
   	 */
	public TimeSeparationFunction(int separationTime) {
		timeSep = separationTime;
	}

    /**
     * invokes calculateDelayNeeded
     *
     * @param time
     * @param lead
     * @param trail
     * @return
     */
	@Override
	public boolean areSeparated(final int time, SimulatedTrajectory lead, SimulatedTrajectory trail) {
		return calculateDelayNeeded(time, lead, trail) == 0;
	}

	@Override
	public int calculateDelayNeeded(final int time, SimulatedTrajectory lead, SimulatedTrajectory trail) {
		// TODO: some test for aircraft route ?? too specific ??

		leadParameters = lead.getParametersAtSimulationTime(time);
		trailParameters = trail.getParametersAtSimulationTime(time);

		// calculate the separation distance between the slots' centers
		double horizontalDistance = AfoUtils.getHorizontalDistance(leadParameters.getLatitude(), leadParameters.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());

		int delay = 0;

		while (horizontalDistance > DISTANCE_EPSILON) {
			delay++;
			trailParameters = trail.getParametersAtSimulationTime(time + delay);
			horizontalDistance = AfoUtils.getHorizontalDistance(leadParameters.getLatitude(), leadParameters.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());
		}

		return Math.max(0, timeSep - delay);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" separationTime=").append(timeSep);
		return sb.toString();
	}

}
