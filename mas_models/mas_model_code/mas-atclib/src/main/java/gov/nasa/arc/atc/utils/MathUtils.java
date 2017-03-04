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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.utils;

import static java.lang.Double.valueOf;

/**
 *
 * @author hamon
 */
public class MathUtils {
	
	/**
	 * An arbitrary small number used for double comparison
	 */
	public static final double EPSILON = 0.0000001;
	

	/**
	 * An arbitrary small number used for miles comparison
	 */
	public static final double EPSILON_MILES = 0.5;

	private MathUtils() {
	}

	/**
	 *
	 * @param latitude
	 * @return
	 */
	public static double tanLatitude(double latitude) {
		return Math.tan((latitude - 5.0) / 180.0 * Math.PI) * 45;
	}

	public static double atanLatitude(double tanLatitude) {
		return Math.atan(tanLatitude / 45.0) * 180.0 / Math.PI + 5;
	}

	public static final double coordStringToDouble(String coordinate) {
		return valueOf(coordinate.substring(0, 2)) + valueOf(coordinate.substring(2, 4)) / 60.0 + valueOf(coordinate.substring(4)) / 3600.0;
	}

	/**
	 * Truncate double value to 2 decimal places
	 * 
	 * @param value to truncate
	 * @return value truncated to 2 decimal places
	 */
	@Deprecated
	public static final double truncateToTwoDecimals(double value) {
//		if (value > 0)
//			return Math.floor(value * 100) / 100;
//		else
//			return Math.ceil(value * 100) / 100;
		return value;
	}// truncateToTwoDecimals

}
