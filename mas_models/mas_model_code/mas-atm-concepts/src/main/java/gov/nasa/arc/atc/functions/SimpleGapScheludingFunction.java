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

package gov.nasa.arc.atc.functions;

import gov.nasa.arc.atc.algos.dsas.Gap;
import gov.nasa.arc.atc.utils.Constants;

/**
 * 
 * @author ahamon
 *
 */
public class SimpleGapScheludingFunction implements GapSchedulingFunction {

	public static final int NONE_TO_MID_SINGLE = Constants.DEP_DEP_MIN + (Constants.SINGLE - Constants.DEP_DEP_MIN) / 2;
	public static final int SINGLE_TO_MID_DOUBLE = Constants.SINGLE + (Constants.DOUBLE - Constants.SINGLE) / 2;
	public static final int DOUBLE_TO_MID_TRIPLE = Constants.DOUBLE + (Constants.TRIPLE - Constants.DOUBLE) / 2;
	public static final int TRIPLE_TO_MID_B757 = Constants.TRIPLE + (Constants.B757 - Constants.TRIPLE) / 2;

	@Override
	public int getNewGapDuration(Gap gap) {
		if (gap.getGapDuration() < NONE_TO_MID_SINGLE) {
			// the gap is too small
			return gap.getGapDuration();
		} else if (gap.getGapDuration() < SINGLE_TO_MID_DOUBLE) {
			return Constants.SINGLE;
		} else if (gap.getGapDuration() < DOUBLE_TO_MID_TRIPLE) {
			return Constants.DOUBLE;
		} else if (gap.getGapDuration() < TRIPLE_TO_MID_B757) {
			return Constants.TRIPLE;
		} else if (gap.getGapDuration() == TRIPLE_TO_MID_B757) {
			return Constants.B757;
		} else {
			return gap.getGapDuration();
		}
	}

}
