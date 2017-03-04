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

import java.util.Comparator;

/**
 * 
 * @author ahamon
 *
 */
public class CoreComparator {

	/**
	 * A comparator to compare slots based on their ETA
	 */
	public static final Comparator<NewSlot> SLOT_ETA_COMPARATOR = CoreComparator::compareSlotETA;

	private CoreComparator() {
		// private utility constructor
	}

	/**
	 * 
	 * @param s1 the first {@link NewSlot} to compare
	 * @param s2 the second {@link NewSlot} to compare
	 * @return the {@link Double} comparison of their ETA
	 */
	private static int compareSlotETA(NewSlot s1, NewSlot s2) {
		return Double.compare(s1.getEta(), s2.getEta());
	}

}
