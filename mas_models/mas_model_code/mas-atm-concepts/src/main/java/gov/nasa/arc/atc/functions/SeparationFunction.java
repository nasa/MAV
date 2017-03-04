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

import gov.nasa.arc.atc.simulation.SimulatedTrajectory;

/**
 * 
 * @author ahamon
 *
 */
public interface SeparationFunction {

	/**
	 * 
	 * @param simuationTime the time in the simulation
	 * @param lead the leading slot's trajectory
	 * @param trail the trailing slot's trajectory
	 * @return if lead and trail are separated at the simulationTime
	 */
	boolean areSeparated(final int simuationTime, final SimulatedTrajectory lead, final SimulatedTrajectory trail);

	/**
	 * !! this method does not introduces delays
	 * 
	 * @param time
	 * @param lead
	 * @param trail
	 * @return delay added
	 */
	int calculateDelayNeeded(final int time, final SimulatedTrajectory lead, final SimulatedTrajectory trail);

}
