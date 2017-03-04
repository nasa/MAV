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

import gov.nasa.arc.atc.FlightPlanUpdate;
import gov.nasa.arc.atc.geography.ATCNode;

/**
 * 
 * @author ahamon
 *
 */
public class DataModelUtils {

	private DataModelUtils() {
		// private utility class constructor
	}

	/**
	 * 
	 * @param dataModel the data model containing the flight plan updates
	 * @param runway the runway analyzed
	 * @param afoSimpleName the afo simple name
	 * @return true if the afo either takes off or land from/on the runway
	 */
	public static boolean fliesToFromRunway(DataModel dataModel, ATCNode runway, String afoSimpleName) {
		for (FlightPlanUpdate flightPlan : dataModel.getFlighPlanUpdates()) {
			if (flightPlan.getAFOName().contains(afoSimpleName)) {
				boolean isDeparture = flightPlan.getFromRunway() != null && flightPlan.getFromRunway().getName().equals(runway.getName()) ;
				boolean isArrival = flightPlan.getToRunway() != null && flightPlan.getToRunway().getName().equals(runway.getName());
				if (isDeparture || isArrival) {
					return true;
				}
			}
		}
		return false;
	}
}
