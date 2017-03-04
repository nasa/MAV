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

package gov.nasa.arc.atc.atc2dviz;

import org.controlsfx.control.PopOver;

import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.core.NewSlot;

/**
 * 
 * @author ahamon
 *
 */
public final class PopOverFactory {

	private PopOverFactory() {
		// private constructor for utility class
	}

	/**
	 * 
	 * @param popOver the pop up
	 * @param afo the afo to represent
	 * @return the corresponding content node
	 */
	public static PopOverAFOContent createPopOverAFOContent(PopOver popOver, NewPlane afo) {
		return new PopOverAFOContent(popOver, afo);
	}

	/**
	 * 
	 * @param popOver the pop up
	 * @param slot the slot to represent
	 * @return the corresponding content node
	 */
	public static PopOverSlotContent createPopOverSlotContent(PopOver popOver, NewSlot slot) {
		return new PopOverSlotContent(popOver, slot);
	}
}
