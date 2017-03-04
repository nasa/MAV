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

package gov.nasa.arc.atc.parametrized;

import gov.nasa.arc.atc.algos.SimulatorInvoker;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import static javafx.application.Platform.runLater;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * 
 * @author ahamon
 *
 */
public abstract class AbstractSimulatorInvoker extends Task<Void> implements SimulatorInvoker {

	// is there a need to add a set method for simulation context? or is it too much constraints?

	private final PropertyChangeSupport propertyChangeSupport;
	private Service<Void> service;

	/**
	 * Creates and initializes an empty SimulatorInvoker,
	 */
	public AbstractSimulatorInvoker() {
		propertyChangeSupport = new PropertyChangeSupport(AbstractSimulatorInvoker.this);
		
	}

	public final Service<Void> getService() {
		return service;
	}

	/**
	 * triggers the simulation's start
	 */
    @Override
	public void runSimulation() {
		runLater(() -> {
			service = new SimulatorInvokerService(AbstractSimulatorInvoker.this);
			if (service.getState().equals(State.READY) ) {
				service.start();
			} else {
				service.cancel();
				service.restart();
			}
		});
	}

	/**
	 * Stops the simulation. Also notifies the simulation was stopped
	 */
	public void stopSimulation() {
		runLater(() -> {
			service.cancel();
			cancel(true);
		});
	}

	@Override
	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	private final class SimulatorInvokerService extends Service<Void> {

		private final AbstractSimulatorInvoker simulation;

		private SimulatorInvokerService(AbstractSimulatorInvoker simu) {
			simulation = simu;
		}

		@Override
		protected Task<Void> createTask() {
			return simulation;
		}
	}

}
