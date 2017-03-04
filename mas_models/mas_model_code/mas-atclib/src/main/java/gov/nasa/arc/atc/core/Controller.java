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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.atc.geography.Waypoint;

/**
 * 
 * @author ahamon
 *
 */
public class Controller implements SimulatedElement{

	private final String name;
	private int nbAircrafts;
	
/*	private Center center;
	private TraconController tracon;
	private Tower tower;*/
	private final PropertyChangeSupport pcs; //?//

	private final Map<Integer, List<String>> updates;
	
	//ALL Controller attributes
    protected Controller handoffTo;
	protected Waypoint handoffWaypoint;
	protected int m_iTimeStamp;
	public int totalClearances;
/*	beliefs
	(current.m_iTimeStamp = 0);
	(current.totalClearances = 0);*/
	
	private int simulationTime;
	private List<String> currentPlanes;
	// temp variable
	private List<String> update;
	
	public Controller(String controllerName,Map<Integer, List<String>> controllerUpdates) {
		pcs = new PropertyChangeSupport(Controller.this);//?//
		updates=controllerUpdates;
		name = controllerName;
		nbAircrafts = 0;
		simulationTime=0;
		currentPlanes = Collections.emptyList();
	}
	
	/**
	 * 
	 * @param controllerName the controller's name
	 */
	public Controller(String controllerName) {
		this(controllerName, new HashMap<>());
	}
	
	@Override
	public int getSimTime() {
		return simulationTime;
	}
	
	@Override
	public void update(int simulationTime) {

		update = updates.get(simulationTime);
		currentPlanes = update != null ? update : currentPlanes;
		
	}

	public String getName() {
		return name;
	}

	public int getNbAircrafts() {
		return nbAircrafts;
	}

	public void setNbAircrafts(int newNbAircrafts) {
		nbAircrafts = newNbAircrafts;
	}
	
	// channel for other classes to listen to this property and match updates
	public void addPropertyListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	public void removePropertyListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
}
