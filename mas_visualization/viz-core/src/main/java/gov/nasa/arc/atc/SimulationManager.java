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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.arc.atc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.utils.MapDrawingUtils;
import gov.nasa.arc.atc.utils.SimulationProperties;

/**
 *
 * @author hamon
 */
public final class SimulationManager {

	public static final String ATC_GEOGRAPHY_CHANGED = "atcGeographyChanged";
	public static final String DATA_MODEL_CHANGED = "dataModelChanged";

	private static final Logger LOG = Logger.getGlobal();
	private static final SimulationManager INSTANCE = new SimulationManager();
	// Not so nice...
	private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(INSTANCE);
	//

	private static ATCGeography currentATCGeography;
	private static DataModel simulationDataModel;

	private SimulationManager() {
		// private constructor for utility class
		retreiveConfigProperties();
	}
	

	public static DataModel getSimulationDataModel() {
		return simulationDataModel;
	}

	public static void setSimulationDataModel(DataModel dataModel) {
		simulationDataModel = dataModel;
		PROPERTY_CHANGE_SUPPORT.firePropertyChange(DATA_MODEL_CHANGED, null, simulationDataModel);
	}

	private static void retreiveConfigProperties() {
		SimulationProperties.parseProperties(MainResources.class.getResourceAsStream("config.properties"));
	}

	public static void setATCGeography(ATCGeography geography) {
		LOG.log(Level.INFO, "updating world geography with atcGeography: {0}", geography);
		currentATCGeography = geography;
		MapDrawingUtils.setATCGeography(currentATCGeography);
		PROPERTY_CHANGE_SUPPORT.firePropertyChange(ATC_GEOGRAPHY_CHANGED, null, currentATCGeography);
	}

	public static ATCGeography getATCGeography() {
		return currentATCGeography;
	}

	/**
	 * 
	 * @param changeListener the listener to add
	 */
	public static void addPropertyChangeListener(PropertyChangeListener changeListener) {
		PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(changeListener);
	}

	/**
	 * 
	 * @param changeListener the listener to remove
	 */
	public static void removePropertyChangeListener(PropertyChangeListener changeListener) {
		PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(changeListener);
	}
}
