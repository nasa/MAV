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

package gov.nasa.arc.atc.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import gov.nasa.arc.atc.simulation.SlotTrajectory;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;

/**
 * 
 * @author ahamon
 *
 */
public class SimulatedTrajectoryFactory {

	public static final String PROGRESS_UPDATE = "Simulated Trajectory calculated";

	private final PropertyChangeSupport propertyChangeSupport;
	private final List<SimulatedSlotMarker> slots;
	private final int simulationDuration;

	/**
	 * 
	 * @param slots the slots to calculate trajectories for
	 * @param simulationDuration the simulation duration
	 */
	public SimulatedTrajectoryFactory(List<SimulatedSlotMarker> slots, int simulationDuration) {
		propertyChangeSupport = new PropertyChangeSupport(SimulatedTrajectoryFactory.this);
		this.slots = slots;
		this.simulationDuration = simulationDuration;
	}

	/**
	 * 
	 * @param listener the property change listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * 
	 * @param listener the property change listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * calculates the simulated trajectories for the slots given when instanciated
	 */
	public void calculateTrajectories() {
		double size = slots.size();
		double nbDone = 0.0;
		for (SimulatedSlotMarker slot : slots) {
			System.err.println(" => calculateTrajectories :: " + slot.getName());
			final SimulatedTrajectory trajectory;
//			switch (slot.getTrafficType()) {
//			case ARRIVAL:
//				trajectory = new SlotTrajectory(slot, simulationDuration);
//				break;
//			case DEPARTURE:
//				trajectory = new SlotTrajectory(slot, simulationDuration);
//				break;
//			default:
//				throw new UnsupportedOperationException("Traffic " + slot.getTrafficType() + " is not handled yet");
//			}

			throw  new UnsupportedOperationException();
//			trajectory = new SlotTrajectory(slot, simulationDuration);
//			nbDone++;
//			final double progress = nbDone / size;
//			propertyChangeSupport.firePropertyChange(PROGRESS_UPDATE, trajectory, progress);
		}

	}

}
