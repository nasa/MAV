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
package gov.nasa.arc.atc.airborne;

/**
 * 
 * @author hamon
 *
 */
public class TimedArrival {

	// TODO: work proper heritage

	private final AFO arrival;
	// The arrival time with respect to the simulation start time
	private final int initialArrivalTime;
	private int meteredArrivalTime;

	public TimedArrival(AFO afo, int time) {
		arrival = afo;
		initialArrivalTime = time;
		meteredArrivalTime = initialArrivalTime;
	}

	public AFO getAFO() {
		return arrival;
	}

	public int getInitialArrivalTime() {
		return initialArrivalTime;
	}

	public int getMeteredArrivalTime() {
		return meteredArrivalTime;
	}

	public void updateMeteredArrivalTime(int newMeteredTime) {
		meteredArrivalTime = newMeteredTime;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TimedArrival of ");
		sb.append(arrival.getName());
		sb.append(" initially scheduled at ");
		sb.append(initialArrivalTime);
		sb.append(" and arriving at ");
		sb.append(meteredArrivalTime);
		return sb.toString();
	}
}
