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

package gov.nasa.arc.atc.viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.arc.atc.DisplayType;

/**
 * Class that stores the display options of the current applications
 * 
 * @author ahamon
 *
 */
public class DisplayOptionsManager {

	public static final String AIRCRAFT_VISIBILITY = "aircraftVisibility";
	public static final String AIRCRAFT_NAME_VISIBILITY = "aircraftNameVisibility";
	public static final String AIRCRAFT_TRAJECTORY_VISIBILITY = "aircraftTrajectoryVisibility";
	public static final String AIRCRAFT_GHOST_VISIBILITY = "aircraftGhostVisibility";
	public static final String WAY_POINT_VISIBILITY = "wpVisibility";
	public static final String WAY_POINT_NAME_VISIBILITY = "wpNameVisibility";
	public static final String SLOT_VISIBILITY = "slotVisibility";
	public static final String SLOT_NAME_VISIBILITY = "slotNameVisibility";
	public static final String SLOT_GHOST_VISIBILITY = "slotGhostVisibility";
	public static final String ROUTE_VISIBILITY = "routeVisibility";
	public static final String SEGMENT_VISIBILITY = "segmentVisibility";
	public static final String AIRPORT_VISIBILITY = "airportVisibility";
	public static final String SECTOR_VISIBILITY = "sectorsVisibility";
	public static final String DISPLAY_TYPE_CHANGED = "displayTypeChanged";
	public static final String BACKGROUND_MAP_VISIBILITY = "backgroundMapVisibility";

	private static final DisplayOptionsManager INSTANCE = new DisplayOptionsManager();
	private static final PropertyChangeSupport PPTY_CHANGE_SUPPORT = new PropertyChangeSupport(INSTANCE);
	private static final Map<String, Boolean> OPTIONS = new HashMap<>();
	private static DisplayType displayType = DisplayType.NAME_ONLY;

	// TODO maybe use lookup and service provider one day

	private DisplayOptionsManager() {
		// private constructor for utility class
	}

	/**
	 * 
	 * @param listener the listener to add
	 */
	public static void addPropertyChangeListener(PropertyChangeListener listener) {
		PPTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
	}

	/**
	 * 
	 * @param listener the listener to remove
	 */
	public static void removePropertyChangeListener(PropertyChangeListener listener) {
		PPTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
	}

	/**
	 * Fires a {@link PropertyChangeEvent}
	 * 
	 * @param optionName the display option's name
	 * @param value the option's value
	 */
	public static void setDisplayOption(String optionName, boolean value) {
		OPTIONS.put(optionName, value);
		PPTY_CHANGE_SUPPORT.firePropertyChange(optionName, !value, value);
	}

	/**
	 * 
	 * @param optionName the display option's name
	 * @return the option value
	 */
	public static boolean getDisplayOption(String optionName) {
		return OPTIONS.get(optionName);
	}

	public static DisplayType getDisplayType() {
		return displayType;
	}

	public static void setDisplayType(DisplayType displayType) {
		DisplayOptionsManager.displayType = displayType;
		PPTY_CHANGE_SUPPORT.firePropertyChange(DISPLAY_TYPE_CHANGED, null, displayType);
	}

}
