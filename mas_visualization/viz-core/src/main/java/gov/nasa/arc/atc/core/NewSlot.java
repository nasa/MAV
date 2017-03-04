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

import java.util.Map;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.FlightPlanUpdate;

// class to be merged with other slots classes
/**
 * 
 * @author ahamon
 *
 */
public class NewSlot extends SimulatedFlyingElement {

	/**
	 * Creates a new Slot
	 * 
	 * @param slotName slot's name
	 * @param afoSimpleName the simple name of the AFO the slot belongs to
	 * @param allUpdates the AFOUpdates from the simulation for this slot
	 */
	public NewSlot(String slotName, String afoSimpleName, Map<Integer, AfoUpdate> allUpdates, FlightPlanUpdate firstFlightPlanUpdate) {
		super(slotName,afoSimpleName, allUpdates,firstFlightPlanUpdate);
	}

	@Override
	public int compareTo(SimulatedFlyingElement o) {
		return Double.compare(getEta(), o.getEta());
	}

	@Override
	public boolean isFlying() {
		return getSimTime() > getStartTime() && getEta() > 0;
	}

}
