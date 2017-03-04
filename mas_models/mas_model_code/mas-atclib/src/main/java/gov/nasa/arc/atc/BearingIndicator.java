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
package gov.nasa.arc.atc;

/**
 * BearingIndicator enumeration defines headings/bearings.  
 * 
 * Based on code from Brahms Mobile Agents Project:
 * @author Ron van Hoof, Chin Seah, Hamon Arnaud
 * @version $Revision:$ $Date: $ $Author: $ 
 */
public enum BearingIndicator {

	
	  NORTH("North",1),
	  NORTH_NORTH_EAST("North-North-East",2),
	  NORTH_EAST("North-East",3),
	  EAST_NORTH_EAST("East-North-East",4),
	  EAST("East",5),
	  EAST_SOUTH_EAST("East-South-East",6),
	  SOUTH_EAST("South-East",7),
	  SOUTH_SOUTH_EAST("South-South-East",8),
	  SOUTH("South",9),
	  SOUT_SOUTH_WEST("South-South-West",10),
	  SOUTH_WEST("South-West",11),
	  WEST_SOUTH_WEST("West-South-West",12),
	  WEST("West",13),
	  WEST_NORTH_WEST("West-North-West",14),
	  NORTH_WEST("North-West",15),
	  NORTH_NORTH_WEST("North-North-West",16);


	private final String displayName;
	private final int value;
	
	private BearingIndicator(String name,int intValue) {
		displayName=name;
		value=intValue;
	}

/**
 * Get bearing indicator based on specified order value
 * 
 * @param value is order of bearing of indicators
 * @return BearingIndicator at given order value
 */
  public static BearingIndicator fromInt(int value) {
	   for(BearingIndicator indicator  :BearingIndicator.values()){
		   if(indicator.value==value){
			   return indicator;
		   }
	   }
	   return null;
  }// from_int
	
  /**
	 * Returns the user-friendly display String for the BearingIndicator.
	 *
	 * @return String the user-friendly display
	 */
	public int to_int(){
		switch(this){
			case NORTH: return 1; 
			case NORTH_NORTH_EAST: return 2; 
			case NORTH_EAST: return 3; 
			case EAST_NORTH_EAST: return 4;
			case EAST: return 5; 
			case EAST_SOUTH_EAST: return 6; 
			case SOUTH_EAST: return 7; 
			case SOUTH_SOUTH_EAST: return 8;
			case SOUTH: return 9; 
			case SOUT_SOUTH_WEST: return 10;
			case SOUTH_WEST: return 11; 
			case WEST_SOUTH_WEST: return 12;
			case WEST: return 13; 
			case WEST_NORTH_WEST: return 14; 
			case NORTH_WEST: return 15;
			case NORTH_NORTH_WEST: return 16;
			default: return 0;
		}// end switch
	}// toDisplay
  
  /**
   * Returns the bearing indicator giving a simple compass bearing.
   *
   * @param bearing simple compass brearing
   * @return BearingIndicator returns the simple compass bearing, null if the
   *         bearing is invalid
   */
  public static BearingIndicator getBearingIndicator(double bearing) {
    double dRangeStart = 348.75;
    double dRangeEnd = 11.25;
    if (bearing > dRangeStart || bearing <= dRangeEnd) {
      return BearingIndicator.NORTH;
    } else {
      dRangeStart = dRangeEnd;
      dRangeEnd += 22.5;
      for (int i = 1; i < 16; i++) {
    	  // bearing > dRangeStart is checked already 
        if (bearing <= dRangeEnd) {
          return fromInt(i);
        } // end if
        dRangeStart = dRangeEnd;
        dRangeEnd += 22.5;
      } // end for
    } // end if
    return null;
  } // getBearingIndicator
  
	/**
	 * Returns the user-friendly display String for the BearingIndicator.
	 *
	 * @return String the user-friendly display
	 */
	public String toDisplay(){
		return displayName;
	}// toDisplay
}
