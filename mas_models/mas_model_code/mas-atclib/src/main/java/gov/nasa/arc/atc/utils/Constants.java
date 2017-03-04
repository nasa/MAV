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
 *                 (c)2004 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.utils;

/**
 * The IConstants interface defines constants used by location manipulation functions.
 *
 * Based on code from Brahms Mobile Agents Project:
 * 
 * @author Ron van Hoof, Chin Seah, Hamon Arnaud
 * @version $Revision:$ $Date:$ $Author:$
 */
public final class Constants {

	// Constants

	/**
	 * 3.14159265
	 */
	public static final double PI = Math.PI;

	public static final double DEG_TO_RAD = PI / 180;

	public static final double RAD_TO_DEG = 180.0 / PI;

	/**
	 * Radiant to Nautical Mile
	 */
	public static final double RAD_TO_NM = 3437.7387;

	public static final double NM_TO_METER = 1852;

	/**
	 * Nautical Mile to Mile
	 */
	public static final double NM_TO_MILE = 1.150779;

	public static final double MILE_TO_METER = 1609.344;

	public static final double MILE_TO_FEET = 5280;

	//
	// Distance
	//
	/**
	 * Earth's mean radius in KM
	 */
	public static final double EARTH_RADIOUS = 6371;

	/**
	 * Convert Feet to meters
	 */
	public static final double FT2METER = 0.3048;

	/**
	 * Convert KM to nautical miles
	 */
	public static final double KM2NM = 1.0 / 1.852;

	/**
	 * Convert KM to meters
	 */
	public static final double KM2METER = 1000;

	/**
	 * Convert nautical miles to meter
	 */
	public static final double NM2METER = 1852;

	/**
	 * Convert kts to m/s
	 */
	public static final double KTS2MS = 1852.0 / 3600.0;

	// Time
	/**
	 * Convert hours to seconds
	 */
	public static final double HOURS2SEC = 3600.0;
	public static final double SEC2HOURS = 1.0 / 3600.0;
	public static final double SEC2MIN = 60;

	// ******************************
	// *** States ****************
	// ******************************

	/**
	 * not started yet in the simulation
	 */
	public static final int NO_STARTED = -1;
	/**
	 * Status of the aircraft is flying
	 */
	public static final int IS_FLYING = 1;

	/**
	 * Status of the aircraft is on ground
	 */
	public static final int ON_GROUND = 2;

	/**
	 * Status of the aircraft is finished
	 */
	public static final int FINISHED = 3;

	/**
	 * A big number, indicating flight not yet scheduled
	 */
	public static final int NO_STA = 8888888;
	public static final int NO_STD = 0;

	// ** ANDREW's part

	// Aircraft
	/*
	 * Generic Speed restrictions (From table 1. Design and Evaluation of the Terminal Area Precision Scheduling and Spacing System, Swansson)
	 */

	/**
	 * Maximum Speed above FL100
	 */
	public static final int MAX_SPD_AFL100 = 300;

	/**
	 * Minimum Speed above FL100
	 */
	public static final int MIN_SPD_AFL100 = 250;

	/**
	 * Maximum Speed below FL100
	 */
	public static final int MAX_SPD_BFL100 = 250;

	/**
	 * Minimum Speed below FL100
	 */
	public static final int MIN_SPD_BFL100 = 210;

	/**
	 * Maximum Speed below FL50
	 */
	public static final int MAX_SPD_BFL50_12 = 220;

	/**
	 * Minimum Speed below FL50
	 */
	public static final int MIN_SPD_BFL50_12 = 200;
	public static final int MAX_SPD_BFL50_OM = 180;
	public static final int MIN_SPD_BFL50_OM = 170;
	public static final int MAX_SPD_BFL50_LDG = 140;
	public static final int MIN_SPD_BFL50_LDG = 120;
	/**
	 * Generic minimum speed of an aircraft in knots
	 */
	public static final int AC_MIN_SPEED = 150;

	/**
	 * Generic maximum speed of an aircraft in knots
	 */
	public static final int AC_MAX_SPEED = 320;

	// Separation
	/**
	 * Minimum separation distance between arivals in NM
	 */
	public static final double MIN_SEP_10 = 2.5;
	public static final double MIN_SEP_40 = 3.0;
	public static final double MIN_SEP_150 = 5.0;
	public static final double TSS_BUFFER = 0.3;

	/**
	 * Minimum separation between two arrivals in seconds
	 */
	public static final int ARR_ARR_MIN = 75;

	/**
	 * Minimum separation between arrival - departure in seconds
	 */
	public static final int ARR_DEP_MIN = 17;

	/**
	 * Minimum separation between departure - arrival in seconds
	 */
	public static final int DEP_ARR_MIN = 40;

	/**
	 * Minimum separation between departure - departure in seconds
	 */
	public static final int DEP_DEP_MIN = 50;

	/**
	 * Min separation between arrivals to allow for single departure
	 */
	public static final int SINGLE = 75;

	/**
	 * Arrival spacing for double
	 */
	public static final int DOUBLE = 135;

	/**
	 * Arrival spacing for triple
	 */
	public static final int TRIPLE = 195;

	/**
	 * Arrival spacing for B757
	 */
	public static final int B757 = 210;
	public static final int MAX_CATCH_UP = 60;

	/**
	 * Distance to meter fix, for which the STA becomes frozen
	 */
	public static final int FREEZE_HORIZON = 150;
	public static final int FH_START = 0;

	// TODO: remove for constants, use config file for simulation dependent variables
	/**
	 * The FAF
	 * 
	 * @deprecated remove for constants, use config file for simulation dependent variables
	 */
	public static final String FINAL_APP_FIX = "KYLIE";

	// TODO: remove for constants, use config file for simulation dependent variables
	/**
	 * The landing runway in use
	 * 
	 * @deprecated remove for constants, use config file for simulation dependent variables
	 */
	public static final String LDG_RUNWAY = "LGA22";

	// Config
	public static final int START_TIME = 0;
	public static final int FIRST_DEP_TIME = 0;

	/**
	 * Time increment to run simulation
	 */
	public static final int TIME_INCREMENT = 1;

	/**
	 * Time limit of the simulation in sec.
	 */
	public static final int TIME_LIMIT = 9000;

	/**
	 * Time when logging starts
	 */
	public static final int LOG_START_TIME = START_TIME;

	/**
	 * Delay time for live print out, in milli sec.
	 */
	public static final int PRINT_OUT_DELAY = 100;

	/**
	 * Weather or not to print out the timeline live.
	 */
	public static final boolean LIVE_PRINT_OUT = true;

	// Separation in TSS
	/**
	 * the radius of a slot marker in seconds
	 */
	public static final int TIME_SLOT_RADIUS = 1;

	private Constants() {
		// private utility class constructor
	}

}
