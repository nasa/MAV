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

package gov.nasa.arc.atc.export;

import gov.nasa.arc.atc.BearingIndicator;
import gov.nasa.arc.atc.utils.Constants;

/**
 * 
 * @author ahamon
 *
 */
public final class AfoUpdateLogger {

	/*
	 * 
	 */
	public static final String AFO_BLOCK_HEADER = "***_START_AFOUpdate_***";
	public static final String AFO_BLOCK_FOOTER = "***_END_AFOUpdate_***";



	/*
	 * 
	 */
	public static final String UNKNOWN_AFO_TYPE = "unknonw-Afo-Type";

	// TODO: find the right way
	public static final String PLANE_UPDATE = "plane-update";
	public static final String SLOT_UPDATE = "slot-update";

	/*
	 * DEFAULT VALUES
	 */
	public static final String DEFAULT_NAME = "NoName";
	public static final double DEFAULT_AIR_SPEED = 0;
	public static final double DEFAULT_VERTICAL_SPEED = 0;
	public static final double DEFAULT_LATITUDE = 0;
	public static final double DEFAULT_LONGITUDE = 0;
	public static final double DEFAULT_ALTITUDE = 0;
	public static final double DEFAULT_HEADING = 0;
	// TODO: to be deprecated
	public static final String DEFAULT_HEADING_ENUM = BearingIndicator.NORTH.toDisplay();
	public static final String DEFAULT_FLIGHT_PLAN = "NOFLIGHTPLAN";
	public static final int DEFAULT_CURRENT_SEGMENT = 0;
	public static final String DEFAULT_TO_WAYPOINT = "no-to-wpt";
	public static final int DEFAULT_STATUS = Constants.IS_FLYING;
	public static final int DEFAULT_SIMULATION_TIME = -1;
	public static final int DEFAULT_START_TIME = -1;
	public static final boolean DEFAULT_IS_DEPARTURE = false;
	public static final double DEFAULT_ETA = 0;
	public static final String DEFAULT_CONTROLLER_NAME = "No-assigned-controller";
	public static final int DEFAULT_IS_METERING = 0;

	private AfoUpdateLogger() {
		// private utility constructor
	}

}
