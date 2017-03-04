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

public class DSASUtils {
	
	private DSASUtils(){
		// private utility constructor
	}
	
	
	
	/**
	 * maxSpeed. Method returning a generic max speed given an altitude. 
	 * (From table 1. Design and Evaluation of the Terminal Area Precision 
	 * Scheduling and Spacing System, Swansson)
	 * @param dAltitude
     * @param dist2APT
   	 * @return
	 */
	public static int maxSpeed(double dAltitude, double dist2APT) {
		if (dAltitude > 10000) {
			return Constants.MAX_SPD_AFL100;
		}
		else if (dAltitude <= 10000 && dAltitude > 5000) {
			return Constants.MAX_SPD_BFL100;
		}
		else if (dAltitude <= 5000 && dist2APT >= 12) {
			return Constants.MAX_SPD_BFL50_12;
		}
		else if (dAltitude <= 5000 && dist2APT < 12 && dist2APT > 6) {
			return Constants.MAX_SPD_BFL50_OM;
		}
			return Constants.MAX_SPD_BFL50_LDG;
	}
	
	
	/**
	 * maxSpeed. Method returning a generic max speed given an altitude. 
	 * (From table 1. Design and Evaluation of the Terminal Area Precision 
	 * Scheduling and Spacing System, Swansson)
	 * @param dAltitude
     * @param dist2APT
   	 * @return
	 */
	public static int minSpeed(double dAltitude, double dist2APT) {
		if (dAltitude > 10000) {
			return Constants.MIN_SPD_AFL100;
		}
		else if (dAltitude <= 10000 && dAltitude > 5000) {
			return Constants.MIN_SPD_BFL100;
		}
		else if (dAltitude <= 5000 && dist2APT >= 12) {
			return Constants.MIN_SPD_BFL50_12;
		}
		else if (dAltitude <= 5000 && dist2APT < 12 && dist2APT > 6) {
			return Constants.MIN_SPD_BFL50_OM;
		}
        return Constants.MIN_SPD_BFL50_LDG;
	}

}
