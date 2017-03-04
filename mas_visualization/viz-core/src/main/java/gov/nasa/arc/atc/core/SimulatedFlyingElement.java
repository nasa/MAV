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

package gov.nasa.arc.atc.core;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.AfoUpdateFactory;
import gov.nasa.arc.atc.FlightPlanUpdate;
import gov.nasa.arc.atc.utils.MathUtils;
import gov.nasa.arc.brahms.atmjava.activities.Constants;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author ahamon
 *
 */
public abstract class SimulatedFlyingElement implements SimulatedElement, Comparable<SimulatedFlyingElement> {

	public static final String METERING_CST = "1";

	private final PropertyChangeSupport propertyChangeSupport;

	private final String fullName;
	private final String simpleName;
	private final Map<Integer, AfoUpdate> updates;
	private final int lastUpdateTime;

	private final String destinationName;

	private AfoUpdate currentUpdate;
	// temp variable
	private AfoUpdate update;

	// distance plane is from plane directly in front of it
	// private double separationDist;

	// private int meterCount;
	// private String planeInFront;

	private int simTime = 0;

	public SimulatedFlyingElement(String elementFullName, String elementSimpleName, Map<Integer, AfoUpdate> allUpdates, FlightPlanUpdate firstFlightPlanUpdate) {
		fullName = elementFullName;
		simpleName = elementSimpleName;
		updates = allUpdates;
		propertyChangeSupport = new PropertyChangeSupport(SimulatedFlyingElement.this);
		//
		// find first update
		// simTime = updates.firstKey();
		// currentUpdate = updates.get(updates.firstKey());
		// lastUpdateTime= allUpdates.lastKey();
		// hum...
		int temp = Integer.MAX_VALUE;
		int temp2 = -1;
		for (Entry<Integer, AfoUpdate> e : allUpdates.entrySet()) {
			if (e.getKey() < temp) {
				temp = e.getKey();
				currentUpdate = e.getValue();
			}
			if (e.getKey() > temp2) {
				temp2 = e.getKey();
			}
		}
		if (allUpdates.isEmpty()) {
			currentUpdate = AfoUpdateFactory.EMPTY_UPDATE;
		}
		simTime = temp;
		lastUpdateTime = temp2;
		if (lastUpdateTime > 0) {
			destinationName = allUpdates.get(lastUpdateTime).getToWaypoint();
		} else {
			destinationName = "unknown";
		}
	}

	@Override
	public int getSimTime() {
		return simTime;
	}

	public int getLastUpdateTime() {
		return lastUpdateTime;
	}

	public double getLatitude() {
		return currentUpdate.getPosition().getLatitude();
	}

	public double getLongitude() {
		return currentUpdate.getPosition().getLongitude();
	}

	public double getAltitude() {
		return currentUpdate.getPosition().getAltitude();
	}

	public String getDestination() {
		return destinationName;
	}

	public double getSpeed() {
		return currentUpdate.getAirSpeed();
	}

	public String getController() {
		return currentUpdate.getController();
	}

	public int getStatus(){
		//TODO ensure OK with flying ones
		return currentUpdate!= null ?currentUpdate.getStatus() : Constants.ON_GROUND;
	}

	public boolean isLanded() {
		// TODO !!!
		return false;
	}

	public double getEta() {
		return currentUpdate.getEta();
	}

	public boolean isFlying() {
		// TODO !!
		return true;
	}

	// public int getLandedSimTime() {
	// return landedSimTime;
	// }

//	public boolean isDeparture() {
//		return currentUpdate.isDeparture();
//	}

	public int getStartTime() {
		return currentUpdate.getStartTime();
	}

	public String getFullName() {
		return fullName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public Map<Integer, AfoUpdate> getUpdates(){
		return Collections.unmodifiableMap(updates);
	}

	// public double getSeparationDist() {
	// return separationDist;
	// }

	// public String getPlaneInFront() {
	// return planeInFront;
	// }
	//

	public boolean isMetering() {
		// TODO: fix using constant or redefine log block
		return currentUpdate.isMetering() == 1;
	}

	protected AfoUpdate getCurrentUpdate() {
		return currentUpdate;
	}

	// public int getMeterCount() {
	// return meterCount;
	// }

	// public boolean equals(SimulatedFlyingElement p) {
	// if (!p.getName().equals(this.getName()))
	// return false;
	//
	// return true;
	// }

	// @Override
	// public int compareTo(SimulatedFlyingElement p) {
	// return (int) (this.getEta() - p.getEta());
	// }

	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void update(int time) {
		simTime = time;
		update = updates.get(time);
		currentUpdate = update != null ? update : currentUpdate;
	}

	public double getTanLatitude() {
		return MathUtils.tanLatitude(currentUpdate.getPosition().getLatitude());
	}
}
