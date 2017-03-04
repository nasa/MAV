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

package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.airborne.AircraftType;
import gov.nasa.arc.atc.algos.dsas.ArrivalGapType;
import gov.nasa.arc.atc.algos.dsas.Gap;
import gov.nasa.arc.atc.algos.dsas.SimpleArrivalGap;
import java.util.Comparator;

/**
 * 
 * @author ahamon
 *
 */
public class GapUtils {

    public static final Comparator<Gap> START_TIME_GAP_COMPARATOR = (Gap gap1, Gap gap2) -> Integer.compare(gap1.getStartTime(), gap2.getStartTime());

	private GapUtils() {
		// private utility constructor
	}

	public static ArrivalGapType calculateArrivalType(int gapDuration) {
		if (gapDuration >= Constants.B757) {
			return ArrivalGapType.B757;
		} else if (gapDuration >= Constants.TRIPLE) {
			return ArrivalGapType.TRIPLE;
		} else if (gapDuration >= Constants.DOUBLE) {
			return ArrivalGapType.DOUBLE;
		} else if (gapDuration >= Constants.SINGLE) {
			return ArrivalGapType.SINGLE;
		}
		return ArrivalGapType.NO_SPACE;
	}

	public static int getEarliestDeparture(SimpleArrivalGap gap, AircraftType aircraftType) {
		// TODO: fix when Brahms models support aircraft type
		return gap.getStartTime() + Constants.ARR_DEP_MIN;
	}

	/**
	 * 
	 * @param gap the gap to analyze
	 * @return the maximum number of arrivals possible in the gap
	 */
	public static int calculateNbArrivals(Gap gap) {
		if (gap.getGapDuration() < Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN) {
			return 0;
		} else if (gap.getGapDuration() < Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN + Constants.ARR_ARR_MIN) {
			return 1;
		} else {
			double dNbArrival = (gap.getGapDuration() - Constants.DEP_ARR_MIN - Constants.ARR_DEP_MIN) / (double) Constants.ARR_ARR_MIN;
			return (int) dNbArrival + 1;
		}
	}

	/**
	 * 
	 * @param gap the gap to analyze
	 * @return the maximum number of departures possible in the gap
	 */
	public static int calculateNbDepartures(Gap gap) {
		if (gap.getGapDuration() < Constants.ARR_DEP_MIN + Constants.DEP_ARR_MIN) {
			return 0;
		} else if (gap.getGapDuration() < Constants.ARR_DEP_MIN + Constants.DEP_DEP_MIN + Constants.DEP_ARR_MIN) {
			return 1;
		} else {
			double dNbArrival = (gap.getGapDuration() - Constants.DEP_ARR_MIN - Constants.ARR_DEP_MIN) / (double) Constants.DEP_DEP_MIN;
			return (int) dNbArrival + 1;
		}
	}

}
