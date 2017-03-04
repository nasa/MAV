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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.concurrent.Task;

/**
 * 
 * @author ahamon
 *
 */
public abstract class SimulationTask extends Task<Void> {


	/**
	 * Name of the event sent when the simulation proceeds to the next step
	 */
	public static final String NEXT_SIMULATION_STEP = "nextSimulationStep";

	/**
	 * Name of the event sent when the simulation is completed
	 */
	public static final String SIMULATION_COMPLETED = "simulationCompleted";


	private final PropertyChangeSupport propertyChangeSupport;

	/**
	 * Creates and initializes an empty SimulationTask,
	 */
	public SimulationTask() {
		propertyChangeSupport = new PropertyChangeSupport(SimulationTask.this);
	}

	/**
	 * 
	 * @param listener the listener to subscribe to the changes
	 */
	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * 
	 * @param listener the listener stopping the subscription to the changes
	 */
	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public abstract SimulationContext getContext();
	
	public abstract int getNbSteps();
	
	public abstract int getStepDuration();

}
