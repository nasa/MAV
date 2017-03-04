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

package gov.nasa.arc.atc.factories;

import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Runway;

/**
 * 
 * @author ahamon
 *
 */
public final class AirportFactory {

	private AirportFactory() {
		// private utility constructor
	}

	/**
	 * 
	 * @return LaGuardia airport
	 */
	public static Airport createLGA() {
		Airport lGA = new Airport("KLGA","LGA", 40.77725, 73.872611,21);
		Runway lga4 = new Runway("LGA4", lGA, 4);
		Runway lga22 = new Runway("LGA22", lGA, 22);
		Runway lga13 = new Runway("LGA13", lGA, 13);
		Runway lga31 = new Runway("LGA31", lGA, 31);
		lGA.addRunway(lga4);
		lGA.addRunway(lga22);
		lGA.addRunway(lga13);
		lGA.addRunway(lga31);
		return lGA;
	}
}
