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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author hamon
 *
 */
public final class SimulationProperties {

	public static final String ALTITUDE_PPTY = "altitude";
	public static final String BEARING_PPTY = "bearing";
	public static final String CONTROLLER_PPTY = "controller";
	public static final String DEPART_PPTY = "depart";
	public static final String ETA_PPTY = "eta";
	public static final String FLYING_PPTY = "flying";
	public static final String LANDED_PPTY = "landed";
	public static final String LATITUDE_PPTY = "latitude";
	public static final String LONGITUDE_PPTY = "longitude";
	public static final String METERING_PPTY = "metering";
	public static final String PLANE_FRONT_PPTY = "planeFront";
	public static final String SEP_DIST_PPTY = "sepDist";
	public static final String SPEED_PPTY = "speed";
	public static final String VERTICAL_SPEED_PPTY = "verticalSpeed";
	public static final String STARTED_TIME_PPTY = "startTime";
	public static final String MINIMUM_SEPARATION_PPTY = "minimumSeparation";
	//
	public static final String SLOT_UPDATED = "slotUpdated";

	//
	private static final Logger LOG = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();

	private SimulationProperties() {
		// private constructor for utility class
	}

	public static void resetProperties() {
		PROPERTIES.clear();
	}
	
	public static void parseProperties(File configFile){
		try {
			PROPERTIES.load( new FileInputStream(configFile));
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception while importing properties: {0}", e);
		}
	}

	public static void parseProperties(InputStream inStream) {
		try {
			PROPERTIES.load(inStream);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception while importing properties: {0}", e);
		}
	}

	public static String getProperty(String propertyName) {
		return PROPERTIES.getProperty(propertyName);
	}
	

	public static Properties getProperties() {
		//TODO make it immutable with a map
		return PROPERTIES;
	}

}
