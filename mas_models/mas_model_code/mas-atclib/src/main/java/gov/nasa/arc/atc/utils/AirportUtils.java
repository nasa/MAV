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

package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.TrafficType;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.FlightPlan;

/**
 * 
 * @author ahamon
 *
 */
public class AirportUtils {
	
	private AirportUtils(){
		// private utility constructor
	}
	
	public static boolean arrivesAt(FlightPlan flightPlan, Airport airport){
        return airport.getRunways().stream().anyMatch(runway -> flightPlan.getLastWaypoint().getName().equals(runway.getName()));
	}
	

	public static boolean departsFrom(FlightPlan flightPlan, Airport airport){
        return airport.getRunways().stream().anyMatch(runway -> flightPlan.getInitialSegment().getFromWaypoint().getName().equals(runway.getName()));
	}
	
	public static TrafficType getTrafficType(AFO afo,Airport airport){
		return getTrafficType(afo.getFlightPlan(), airport);
	}

	public static TrafficType getTrafficType(FlightPlan flightPlan,Airport airport){
		if(departsFrom(flightPlan, airport)){
			return TrafficType.DEPARTURE;
		}
		if(arrivesAt(flightPlan, airport)){
			return TrafficType.ARRIVAL;
		}
		return TrafficType.TRANSIT;
	}
}
