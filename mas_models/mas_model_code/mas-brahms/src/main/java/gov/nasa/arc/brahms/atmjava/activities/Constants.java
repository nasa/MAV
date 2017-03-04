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

package gov.nasa.arc.brahms.atmjava.activities;

public class Constants {
	// Distance
	public final static double	EARTH_RADIOUS	= 6371;		// Earth's mean raduis in km
	public final static double	FT2METER		= 0.3048;	// Convert Feet to meters
	public final static double	KM2NM			= (1/1.852);// Convert km to nautical miles
	public final static double	KM2METER		= 1000;		// Convert km to meters
	public final static double	NM2METER		= 1852;		// Convert nautical miles to meter
	public final static double	KTS2MS			= 1852.0/3600.0; // Convert kts to m/s.
	
	//Time
	public final static double 	HOURS2SEC 		= 3600.0;	// Convert hours to seconds
	public final static double	SEC2HOURS		= (1/3600.0);
	public final static double	SEC2MIN			= 60;
	
	//States
	public final static int 	NOT_FLYING 		= 0;		// Status of the aircraft is not flying
	public final static int		IS_FLYING		= 1;		// Status of the aircraft is flying
	public final static int		ON_GROUND		= 2;		// Status of the aircraft is on ground
	public final static int		FINISHED		= 3;		// Status of the aircraft is finished
	public final static int		NO_STA			= 8888888;	// A big number, indicating flight not yet sheduled
	public final static int		NO_STD			= 0;
	
	//Aircraft
	/* Generic Speed restrictions (From table 1. Design and 
	 * Evaluation of the Terminal Area Precision Scheduling 
	 * and Spacing System, Swansson)
	 */
	public final static int		MAX_SPD_AFL100	= 300;		// Maximum Speed above FL100
	public final static int		MIN_SPD_AFL100	= 250;		// Minimum Speed above FL100
	public final static int		MAX_SPD_BFL100	= 250;		// Maximum Speed below FL100
	public final static int		MIN_SPD_BFL100	= 210;		// Minimum Speed below FL100	
	public final static int		MAX_SPD_BFL50_12= 220;		// Maximum Speed below FL50
	public final static int		MIN_SPD_BFL50_12= 200;		// Minimum Speed below FL50
	public final static int		MAX_SPD_BFL50_OM= 180;
	public final static int		MIN_SPD_BFL50_OM= 170;
	public final static int		MAX_SPD_BFL50_LDG=140;
	public final static int		MIN_SPD_BFL50_LDG=120;
	
	public final static int		AC_MIN_SPEED	= 150;		// Generic minimum speed of an aircraft in knots
	public final static int		AC_MAX_SPEED	= 320;		// Generic maximum speed of an aircraft in knots
	
	
	
	
	//Separation
	public final static double	MIN_SEP			= 2.5;		// Minimum separation distance between arivals in NM
	public final static double	TSS_BUFFER		= 0.3;
	public final static int		ARR_ARR_MIN		= 72;		// Minimum separation between two arrivals in seconds
	public final static int		ARR_DEP_MIN		= 17;		// Minimum separation between arrival - departure in seconds
	public final static int		DEP_ARR_MIN		= 40;		// Minimum separation between departure - arrival in seconds
	public final static int		DEP_DEP_MIN		= 50;		// Minimum separation between departure - departure in seconds
	public final static int		SINGLE			= 75;		// Min separation between arrivals to allow for single departure	
	public final static int		DOUBLE			= 135;		// Arrival spacing for double
	public final static int		TRIPLE			= 195;		// Arrival spacing for triple
	public final static int		B757			= 210;		// Arrival spacing for B757
	public final static int		MAX_CATCH_UP	= 60;

	
	public final static int		FREEZE_HORIZON	= 150;		// Distance to meter fix, for which the STA becomes frozen
	public final static int		FH_START 		= 0;
	public final static String	FINAL_APP_FIX	= "KYLIE";	// The FAF 
	public final static String	LDG_RUNWAY		= "LGA22";	// The landing runway in use;
	
	//Config
	public final static int		START_TIME		= 0;
	public final static int		FIRST_DEP_TIME	= 0;
	public final static int 	TIME_INCREMENT 	= 1;		// Time increment to run simulation
	public final static int		TIME_LIMIT		= 9000;		// Time limit of the simulation in sec.
	public final static int		LOG_START_TIME	= START_TIME;		// Time when logging starts
	public final static int		PRINT_OUT_DELAY	= 100;		// Delay time for live print out, in milli sec.
	public final static boolean LIVE_PRINT_OUT	= false;	// Weather or not to print out the timeline live.
	
}
