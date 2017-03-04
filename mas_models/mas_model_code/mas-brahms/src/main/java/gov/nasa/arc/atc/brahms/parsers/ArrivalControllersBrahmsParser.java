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

package gov.nasa.arc.atc.brahms.parsers;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 
 * @author ahamon
 *
 */
public class ArrivalControllersBrahmsParser {

	public static final String B_AIRPORT_INSTANCE = "Airport";
	public static final String B_RUNWAY_INSTANCE = "Runway";
	public static final String B_AIRPORT_BELIEF = "airport";
	public static final String B_QFU_BELIEF = "qfu";

	private static final Logger LOG = Logger.getGlobal();

	private ArrivalControllersBrahmsParser() {
		// private utility constructor
	}

	/**
	 * 
	 * @param arrControllersFile the file to parse
	 * @return the map containing each controller associated with the waypoint names they controll
	 */
	public static Map<String, List<String>> parseArrivalControllers(File arrControllersFile) {
		LOG.log(Level.INFO, "parseArrivalControllers file :: {0}", arrControllersFile);
		throw new UnsupportedOperationException("not implemented yet");
	}

}
