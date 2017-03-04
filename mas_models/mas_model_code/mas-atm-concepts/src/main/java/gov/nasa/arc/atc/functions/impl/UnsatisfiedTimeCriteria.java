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

package gov.nasa.arc.atc.functions.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import gov.nasa.arc.atc.functions.UnsatisfiedCriteria;

/**
 * 
 * @author ahamon
 *
 */
public class UnsatisfiedTimeCriteria implements UnsatisfiedCriteria {
	
	private final double requiredDistance;
	private final double actualDistance;
	
	private final List<Object> controlValues;

	/**
	 * @param requiredDistance
	 * @param actualDistance
	 */
	public UnsatisfiedTimeCriteria(double requiredDistance, double actualDistance) {
		this.requiredDistance = requiredDistance;
		this.actualDistance = actualDistance;
		controlValues= new LinkedList<>();
		controlValues.add(this.requiredDistance);
	}

	@Override
	public List<Object> getControlValues() {
		return Collections.unmodifiableList(controlValues);
	}


	@Override
	public Object getFailedValue() {
		return actualDistance;
	}

}
