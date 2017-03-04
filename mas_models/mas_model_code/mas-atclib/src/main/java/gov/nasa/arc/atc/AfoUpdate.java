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

package gov.nasa.arc.atc;

import gov.nasa.arc.atc.geography.Position;

//This class describes the values available for the {@link UpdateAfoDataBlock} when logged in {@link Utils}

/**
 *
 * 
 * @author ahamon
 *
 */
public interface AfoUpdate {

	/**
	 * 
	 * @return the afo full name
	 */
	String getAfoName();

	/**
	 * 
	 * @return the time stamp of the update
	 */
	int getTimeStamp();

	/**
	 * 
	 * @return the AFO position
	 */
	Position getPosition();

	/**
	 * 
	 * @return the AFO airspeed
	 */
	double getAirSpeed();

	/**
	 * 
	 * @return the AFO vertical speed
	 */
	double getVSpeed();

	/**
	 * 
	 * @return the AFO heading in [degrees]
	 */
	double getHeading();

	/**
	 * 
	 * @return the segment index for the flight plan the AFO is currently flying
	 */
	int getCurrentSegment();

	/**
	 * 
	 * @return the waypoint's name the AFO is flying towards
	 */
	String getToWaypoint();

	/**
	 * 
	 * @return the AFO status
	 */
	int getStatus();

	/**
	 * 
	 * @return the AFO start time in the simulation
	 */
	int getStartTime();

	/**
	 * 
	 * @return the AFO departure time in the simulation
	 */
	int getDepartureTime();


	/**
	 * 
	 * @return the ETA or remaining time before the AFO reaches its destination
	 */
	double getEta();

	/**
	 * 
	 * @return the name of the ATController in handling this AFO
	 */
	String getController();

	/**
	 * 
	 * @return the metering status for this AFO
	 */
	int isMetering();

}
