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

package gov.nasa.arc.atc;

import java.util.List;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;

/**
 * 
 * @author ahamon
 *
 */
public interface Sequence {

	/**
	 * removes all the trajectories from the sequence
	 */
	void clear();

	/**
	 * 
	 * @return the node considered for the sequence
	 */
	ATCNode getATCNode();

	/**
	 * 
	 * @param trajectory the trajectory to add to the sequence
	 */
	void addSimulatedTrajectory(SimulatedTrajectory trajectory);

	/**
	 * 
	 * @return all trajectories sequenced
	 */
	List<SimulatedTrajectory> getSimulatedTrajectories();

	/**
	 * 
	 * @param index the index in the SimulatedTrajectory sequence
	 * @return the corresponding SimulatedTrajectory
	 */
	SimulatedTrajectory getAtIndex(int index);

}
