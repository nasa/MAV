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

package gov.nasa.arc.atc.reports;

import gov.nasa.arc.atc.simulation.SimulatedTrajectory;

/**
 * 
 * @author ahamon
 *
 */
public class DepartureInfo {

	private final String name;
	private final int originalDepartureTime;
	private final int currentDepartureTime;
	private final String departureRunway;

	public DepartureInfo(SimulatedTrajectory departure) {
		name = departure.getSlotMarker().getName();
		originalDepartureTime = departure.getOriginalStartTime();
		currentDepartureTime = departure.getSlotMarker().getStartTime();
		departureRunway = departure.getSlotMarker().getFlightPlan().getDepartureRunway().getName();
	}
	
	public DepartureInfo(String afoName,int originDepTime,int currentDepTime,String runwayName) {
		name = afoName;
		originalDepartureTime = originDepTime;
		currentDepartureTime =currentDepTime;
		departureRunway = runwayName;
	}

	public String getName() {
		return name;
	}

	public int getCurrentDepartureTime() {
		return currentDepartureTime;
	}

	public int getOriginalDepartureTime() {
		return originalDepartureTime;
	}

	public String getDepartureRunway() {
		return departureRunway;
	}

}
