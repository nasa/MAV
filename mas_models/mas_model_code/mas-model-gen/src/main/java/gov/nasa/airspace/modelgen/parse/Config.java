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

package gov.nasa.airspace.modelgen.parse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config extends Properties {

	private static final long serialVersionUID = 1L;
	
	private static final String autoGenKey = "autogen.folder";
	private static final String flightSegPNKey = "flightsegments.package.name";
	private static final String flightPlanPNKey = "flightplans.package.name";
	private static final String waypointPNKey = "waypoints.package.name";
	private static final String airplanesPNKey = "airplanes.package.name";
	private static final String slotsPNKey = "slots.package.name";
	private static final String westernHemisphereKey = "western.hemisphere";
	private static final String towerControllerKey = "tower.controller";
	

	Properties properties;

	public Config(String configFileName){ 
		System.out.println("Setting up configuration...");
		properties = new Properties();
	
		try {
			properties.load(new FileInputStream(configFileName));
		} catch (IOException e) {
			System.err.println("error when loading the properties file");
			e.printStackTrace();
		}		
	}
	
	public String getTowerController() {
		if(properties.containsKey(towerControllerKey))
			return properties.getProperty(towerControllerKey);
		return "NO_TOWER_CONTROLLER_DEFINED";
	}
	
	public String getFlightSegmentPackageName() {
		if(properties.containsKey(flightSegPNKey))
			return "package "+properties.getProperty(flightSegPNKey) + ";\n\n";
		return "";
	}
	
	public String getFlightPlanPackageName() {
		if(properties.containsKey(flightPlanPNKey))
			return "package "+properties.getProperty(flightPlanPNKey) + ";\n\n";
		return "";
	}
	
	public String getWayPointPackageName() {
		if(properties.containsKey(waypointPNKey))
			return "package "+properties.getProperty(waypointPNKey) +";\n\n";
		return "";
	}
	
	public String getAirplanesPackageName() {
		if(properties.containsKey(airplanesPNKey)) 
			return "package "+properties.getProperty(airplanesPNKey) + ";\n\n";
		return "";
	}
	
	public String getSlotsPackageName() {
		if(properties.containsKey(slotsPNKey))
			return "package "+properties.getProperty(slotsPNKey) + ";\n\n";
		return "";
	}
	
	public boolean isWesternHemisphere() {
		if(properties.contains(westernHemisphereKey))
			Boolean.valueOf(properties.getProperty(westernHemisphereKey));
		return true;
	}
	
	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}
	
	public String getPropertyString(String key) {
		return properties.getProperty(key).toString();
	}
	
	public String getAutoGenFolder() {
		if(properties.containsKey(autoGenKey)) {
			return getPropertyString(autoGenKey);
		} 
		throw new RuntimeException("autogen.folder not specified");
	}

	public static void main(String[] args) {
		Config config = new Config("global.properties");
		System.out.println(config.getAutoGenFolder());
	}
	
}
