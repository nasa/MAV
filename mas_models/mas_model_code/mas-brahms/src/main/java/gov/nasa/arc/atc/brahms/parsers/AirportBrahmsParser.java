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

package gov.nasa.arc.atc.brahms.parsers;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.brahms.BrahmsInstanceInitialConfiguration;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Runway;

/**
 * 
 * @author ahamon
 *
 */
public class AirportBrahmsParser {

	public static final String B_AIRPORT_INSTANCE = "Airport";
	public static final String B_RUNWAY_INSTANCE = "Runway";
	public static final String B_AIRPORT_BELIEF = "airport";
	public static final String B_QFU_BELIEF = "qfu";

	private static final Logger LOG = Logger.getGlobal();

	private AirportBrahmsParser() {
		// private utility constructor
	}

	/**
	 * 
	 * @param airportFile the file to parse
	 * @return the map containing each airport associated with their name
	 */
	public static Map<String, Airport> parseAirports(File airportFile) {
		LOG.log(Level.INFO, "parseAirports file :: {0}", airportFile);
		Map<String, Airport> airports = new HashMap<>();

		List<BrahmsInstanceInitialConfiguration> instances = BrahmsAgentInitializationParser.parseBrahmsInstances(airportFile);

		// creating airports
		instances.stream().filter(instance -> instance.getClassName().equals(B_AIRPORT_INSTANCE)).forEach(airportInstance -> {
			final double latitide = Double.parseDouble(airportInstance.getInitialFacts().get(WaypointParser.WAYPOINT_LATITUDE));
			final double longitude = WaypointParser.WESTERLY_COORDINATES * Double.parseDouble(airportInstance.getInitialFacts().get(WaypointParser.WAYPOINT_LONGITUDE));
			airports.put(airportInstance.getName(), new Airport(airportInstance.getName(), latitide, longitude));
		});

		// creating runways
		instances.stream().filter(instance -> instance.getClassName().equals(B_RUNWAY_INSTANCE)).forEach(runwayInstance -> {
			final String airportName = runwayInstance.getInitialFacts().get(B_AIRPORT_BELIEF);
			final int qfu = Integer.parseInt(runwayInstance.getInitialFacts().get(B_QFU_BELIEF));
			final Airport airport = airports.get(airportName);
			Runway runway = new Runway(runwayInstance.getName(), airport, qfu);
			airport.addRunway(runway);
		});

		LOG.log(Level.INFO, "# END parseAirports, found {0} airports", airports.values().size());
		return Collections.unmodifiableMap(airports);
	}

}
