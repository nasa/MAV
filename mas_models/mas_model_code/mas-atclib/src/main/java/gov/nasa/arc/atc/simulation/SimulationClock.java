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

public class SimulationClock {

	public static final String TIMED_CHANGED = "timedChanged";

	private final PropertyChangeSupport propertyChangeSupport;

	private int currentTime;

	public SimulationClock() {
		propertyChangeSupport = new PropertyChangeSupport(SimulationClock.this);
		currentTime = 0;
	}

	public void setTime(int time) {
		currentTime = time;
		propertyChangeSupport.firePropertyChange(TIMED_CHANGED, null, currentTime);
	}

	public void addPropertychangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertychangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void incrSimulationTime(int delta) {
		setTime(currentTime + delta);
	}

	public void decrSimulationTime(int delta) {
		setTime(currentTime + delta);
	}

	public int getCurrentSimTime() {
		return currentTime;
	}
}
