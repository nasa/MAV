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
public class ArrivalInfo {

	private final String name;
	private final int arrivalTime;
	private final String arrivalRunway;

	public ArrivalInfo(SimulatedTrajectory arrival) {
		name = arrival.getSlotMarker().getName();
		arrivalTime = arrival.getArrivalTime();
		arrivalRunway = arrival.getSlotMarker().getFlightPlan().getArrivalRunway().getName();
	}

	public ArrivalInfo(String arrivalName, int time, String runway) {
		name = arrivalName;
		arrivalTime = time;
		arrivalRunway = runway;
	}

	public String getName() {
		return name;
	}

	public String getArrivalRunway() {
		return arrivalRunway;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" ");
		sb.append(name);
		sb.append(" at t=");
		sb.append(arrivalTime);
		sb.append(" at runway");
		sb.append(arrivalRunway);
		return sb.toString();
	}

}
