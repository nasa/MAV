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

package gov.nasa.arc.brahms.atmjava.activities;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.arc.atc.FlightParameters;

public final class PersistentState {
	private static Map<String, FlightParameters> initialParameters =
						new HashMap<String, FlightParameters>();
	
	public static void addInitialFlightParameters(String name, 
								FlightParameters flightParams) {
		PersistentState.initialParameters.put(name, flightParams);
	}
	
	public static FlightParameters getFlightParameters(String name) {
		assert(PersistentState.initialParameters.containsKey(name));
		return PersistentState.initialParameters.get(name);
	}
	
	public static boolean containsInitialParameters(String name) {
		return (PersistentState.initialParameters.containsKey(name));
	}
}
