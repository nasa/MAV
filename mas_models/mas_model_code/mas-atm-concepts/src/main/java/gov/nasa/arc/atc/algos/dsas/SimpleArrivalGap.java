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

package gov.nasa.arc.atc.algos.dsas;

import gov.nasa.arc.atc.utils.GapUtils;

/**
 * @author ahamon
 *
 */
public class SimpleArrivalGap implements Gap {

	private final int startTime;
	private final int endTime;
	private final int gapDuration;
	private final ArrivalGapType gapType;

	public SimpleArrivalGap(final int sTime, final int eTime) {
		startTime = sTime;
		endTime = eTime;
		gapDuration = endTime - startTime;
		gapType = GapUtils.calculateArrivalType(gapDuration);
	}

	@Override
	public int getStartTime() {
		return startTime;
	}

	@Override
	public int getEndTime() {
		return endTime;
	}

	@Override
	public int getGapDuration() {
		return gapDuration;
	}

	public ArrivalGapType getGapType() {
		return gapType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" ");
		sb.append(gapType);
		sb.append(" starting at t=");
		sb.append(startTime);
		sb.append(" duration=");
		sb.append(gapDuration);
		return sb.toString();
	}

}
