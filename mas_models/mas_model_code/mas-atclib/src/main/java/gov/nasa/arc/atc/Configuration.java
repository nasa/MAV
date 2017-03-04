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

package gov.nasa.arc.atc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ahamon
 *
 */
public final class Configuration {

	public static final String AIRPORTS_PPTY = "airports";
	public static final String WAYPOINTS_PPTY = "waypoints";
	public static final String AIRCRAFTS_PPTY = "aircrafts";
	public static final String FLIGHT_SEGMENTS_PPTY = "flightsegments";
	public static final String FLIGHT_PLANS_PPTY = "flightplans";
	public static final String DEPARTURE_QUEUE_PPTY = "departureQueue";
	public static final String DEPARTURE_RUNWAY ="departureRunway";
	public static final String ARRIVAL_RUNWAY ="arrivalRunway";
	
	private static final Logger LOG = Logger.getGlobal();
	
	private static final String PROPERTY_LOG = " Property {0} : {1}";

	private Configuration() {
		// private utility constructor
	}

	public static Properties readConfigurationFile(File configurationFile) {
		Properties properties = new Properties();
        try (FileReader reader = new FileReader(configurationFile)) {
			properties.load(reader);
			checkProperties(properties);
        } catch (FileNotFoundException ex) {
			LOG.log(Level.SEVERE, "file does not exist: {0}", ex);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "I/O error: {0}", ex);
		}
		return properties;
	}
	
	public static void writeConfigurationFile(Properties properties, String path){
		File configFile = new File(path);
		try {
		    FileWriter writer = new FileWriter(configFile);
		    properties.store(writer, "simulation configuration");
			checkProperties(properties);
		    writer.close();
		} catch (FileNotFoundException ex) {
			LOG.log(Level.SEVERE, "file does not exist: {0}", ex);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "I/O error: {0}", ex);
		}
	}
	
	private static void checkProperties(Properties properties){
		boolean airportPptyOK = properties.getProperty(AIRPORTS_PPTY)!= null;
		boolean waypointsPptyOK = properties.getProperty(WAYPOINTS_PPTY)!= null;
		boolean aircraftsPptyOK = properties.getProperty(AIRCRAFTS_PPTY)!= null;
		boolean flightSegPptyOK = properties.getProperty(FLIGHT_SEGMENTS_PPTY)!= null;
		boolean fplPptyOK = properties.getProperty(FLIGHT_PLANS_PPTY)!= null;
		boolean departurePptyOK = properties.getProperty(DEPARTURE_QUEUE_PPTY)!= null;
		LOG.log(Level.INFO, PROPERTY_LOG, new Object[]{AIRPORTS_PPTY,airportPptyOK});
		LOG.log(Level.INFO, PROPERTY_LOG, new Object[]{WAYPOINTS_PPTY,waypointsPptyOK});
		LOG.log(Level.INFO, PROPERTY_LOG, new Object[]{AIRCRAFTS_PPTY,aircraftsPptyOK});
		LOG.log(Level.INFO, PROPERTY_LOG, new Object[]{FLIGHT_SEGMENTS_PPTY,flightSegPptyOK});
		LOG.log(Level.INFO, PROPERTY_LOG, new Object[]{FLIGHT_PLANS_PPTY,fplPptyOK});
		LOG.log(Level.INFO, PROPERTY_LOG, new Object[]{DEPARTURE_QUEUE_PPTY,departurePptyOK});
		assert airportPptyOK;
		assert waypointsPptyOK;
		assert aircraftsPptyOK;
		assert flightSegPptyOK;
		assert fplPptyOK;
		assert departurePptyOK;
	}
}
