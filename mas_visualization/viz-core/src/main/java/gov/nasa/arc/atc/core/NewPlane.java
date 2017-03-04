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

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.FlightPlanUpdate;
import gov.nasa.arc.atc.utils.Comparators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author ahamon
 *
 */
public class NewPlane extends SimulatedFlyingElement {

//	private final Map<Integer, Integer> meterCounts;

	/**
	 * Creates a new Plane
	 * 
	 * @param planeName plane's name
	 * @param planeSimpleName plane's name without its prefix
	 * @param allUpdates the AFOUpdates from the simulation for this plane
	 */
	public NewPlane(String planeName, String planeSimpleName, Map<Integer, AfoUpdate> allUpdates, FlightPlanUpdate firstFlightPlanUpdate) {
		super(planeName, planeSimpleName, allUpdates,firstFlightPlanUpdate);
//		meterCounts = new HashMap<>();
		List<AfoUpdate> sortedUpdates = allUpdates.values().stream().sorted(Comparators.TIMESTAMP_AFOUPDATE_COMPARATOR).collect(Collectors.toList());
		//
		if (!sortedUpdates.isEmpty()) {
			AfoUpdate prevUpdate = sortedUpdates.get(0);
			AfoUpdate currUpdate;
			// ...
//			meterCounts.put(prevUpdate.getTimeStamp(), prevUpdate.isMetering());
			for (int i = 1; i < sortedUpdates.size(); i++) {
				prevUpdate = sortedUpdates.get(i - 1);
				currUpdate = sortedUpdates.get(i);
//				if (currUpdate.isMetering() == 1 && prevUpdate.isMetering() == 0) {
//					meterCounts.put(currUpdate.getTimeStamp(), meterCounts.get(prevUpdate.getTimeStamp()) + 1);
//				} else {
//					meterCounts.put(currUpdate.getTimeStamp(), meterCounts.get(prevUpdate.getTimeStamp()));
//				}
			}
		}
	}

	/**
	 * 
	 * @return if the plane has landed
	 */
	@Override
	public boolean isLanded() {
		//TODO
//		if (!isDeparture()) {
//			return getSimTime() > getStartTime() && getEta() <= 0;
//		}
		return false;
	}

	/**
	 * 
	 * @return if the plane is currently flying
	 */
	@Override
	public boolean isFlying() {
		//TODO
//		if (isDeparture()) {
//			return getSimTime() <= getStartTime();
//		}
//		return getSimTime() > getStartTime() && getEta() > 0;
		return true;
	}

//	/**
//	 *
//	 * @param time the simulation time observed
//	 * @return the number of times the plane has been metered (including currently) since its start in the simulation
//	 */
//	public int getMeterCountAt(int time) {
//		return meterCounts.get(time);
//	}

	/**
	 * 
	 * @return the plane's heading, {@link Double} max value if not set
	 */
	public double getHeading() {
		return getCurrentUpdate() != null ? getCurrentUpdate().getHeading() : Double.MAX_VALUE;
	}

	@Override
	public int compareTo(SimulatedFlyingElement p) {
		return Double.compare(getEta(), p.getEta());
	}

}
