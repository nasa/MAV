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

/**
 * 
 * @author ahamon
 *
 */
public class DepartureInfo {

	private final String planeName;
	private final int originalDepartureTime;
	private final int currentDepartureTime;
	private final int queueIndex;

	/**
	 * @param planeName
	 * @param originalDepartureTime
	 * @param currentDepartureTime
	 * @param queueIndex
	 */
	public DepartureInfo(String planeName, int originalDepartureTime, int currentDepartureTime, int queueIndex) {
		this.planeName = planeName;
		this.originalDepartureTime = originalDepartureTime;
		this.currentDepartureTime = currentDepartureTime;
		this.queueIndex = queueIndex;
	}

	public String getPlaneName() {
		return planeName;
	}

	public int getOriginalDepartureTime() {
		return originalDepartureTime;
	}

	public int getCurrentDepartureTime() {
		return currentDepartureTime;
	}

	public int getQueueIndex() {
		return queueIndex;
	}

}
