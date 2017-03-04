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

import gov.nasa.arc.atc.airborne.AFO;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Queue;
import java.util.TreeMap;

public class Tower extends Controller {
	// queue position -> aircraft name
	public TreeMap<Integer, String> departureQueue = new TreeMap<>();
	// time -> currentQueue or updatedQueue
	public TreeMap<Integer, Queue<String>> currentQueue = new TreeMap<>();
	public Queue<String> towerQueue;
	
	public AFO topArrival;
	public double topArrivalETA;
	public int lastArrival;
	public int departureCounter;
	public int lastDepartureTime;

	protected int m_Time;
	
	//initial_beliefs:
	//the first departure
/*	(current.departureCounter = 1);
	(current.lastDepartureTime = -100);
	(current.m_Time = 0);*/

	public String otherAttribute;
	
	private final PropertyChangeSupport pcs; //?//

	public Tower(String towerControllerName) {
	        super(towerControllerName); // constructs controller name and nbAircrafts
	        otherAttribute = "Fill_in_info";
	        
			pcs = new PropertyChangeSupport(Tower.this);//?//

		// TODO Auto-generated constructor stub
	}
	
	// channel for other classes to listen to this property and match updates

    @Override
	public void addPropertyListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
    @Override
	public void removePropertyListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}
