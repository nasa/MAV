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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.algos.dsas.NamedArrivalGap;
import gov.nasa.arc.atc.utils.Comparators;
import gov.nasa.arc.atc.utils.Constants;

/**
 * 
 * @author ahamon
 *
 */
public class DataCalculationUtilities {

	private DataCalculationUtilities() {
		// private utility constructor
	}

	/**
	 * 
	 * @param data the {@link DataModel} to analyze
	 * @return a {@link Map} containing, for each relevant simulation time, the list of arrival gaps
	 */
	public static Map<Integer, List<NamedArrivalGap>> calculateArrivalGaps(DataModel data) {
		Map<Integer, List<NamedArrivalGap>> result = new HashMap<>();

		// retrieving
		List<String> arrivingNames = data.getSlots().stream().map(slot -> slot.getFullName()).collect(Collectors.toList());

		// arrival plane updates by time
		Map<Integer, List<AfoUpdate>> timedUpdates = new HashMap<>();

		// for each arriving plane puts the updates in the timedUpdates map
		data.getAllDataUpdates().entrySet().stream().filter(entry -> arrivingNames.contains(entry.getKey())).forEach(slotEntry -> slotEntry.getValue().forEach((time, update) -> {
			if (update.getStatus() == Constants.IS_FLYING) {
				if (!timedUpdates.containsKey(time)) {
					timedUpdates.put(time, new ArrayList<>());
				}
				timedUpdates.get(time).add(update);
			}
		}));
		
		// build arrival gap list for each simulation time
		timedUpdates.forEach((time, updateList) -> {
			List<NamedArrivalGap> gaps = new ArrayList<>();
			updateList.sort(Comparators.ARRIVING_AFOUPDATE_COMPARATOR);
			for (int i = 0; i < updateList.size() - 1; i++) {
				gaps.add(new NamedArrivalGap(time, updateList.get(i), updateList.get(i + 1)));
			}
			result.put(time, gaps);
		});

		return Collections.unmodifiableMap(result);
	}

}
