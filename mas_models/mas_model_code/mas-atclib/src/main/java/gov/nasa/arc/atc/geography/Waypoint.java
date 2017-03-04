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
package gov.nasa.arc.atc.geography;

/**
 *
 * @author hamon
 */
public class Waypoint extends ATCNode {

	/**
	 * Creates a non meter fix {@link Waypoint}
	 * 
	 * @param wpName the {@link Waypoint}'s name
	 * @param wpLatitude the {@link Waypoint}'s latitude, in degrees
	 * @param wpLongitude the {@link Waypoint}'s longitude, in degrees
	 * @param altitude the {@link Waypoint}'s altitude, in feet
	 */
	public Waypoint(String wpName, double wpLatitude, double wpLongitude, double altitude) {
		super(wpName, wpLatitude, wpLongitude, altitude);
	}

	/**
	 * 
	 * Creates a non meter fix {@link Waypoint} at altitude 0 feet
	 * 
	 * @param wpName the {@link Waypoint}'s name
	 * @param wpLatitude the {@link Waypoint}'s latitude, in degrees
	 * @param wpLongitude the {@link Waypoint}'s longitude, in degrees
	 */
	public Waypoint(String wpName, double wpLatitude, double wpLongitude) {
		// A waypoints can have no altitude and by default is set to 0
		this(wpName, wpLatitude, wpLongitude, 0);
	}

	/**
	 * 
	 * @param wpName the {@link Waypoint}'s name
	 * @param wpLatitude the {@link Waypoint}'s latitude, in degrees
	 * @param wpLongitude the {@link Waypoint}'s longitude, in degrees
	 * @param altitude the {@link Waypoint}'s altitude, in feet
	 * @param isMeterFix if the {@link Waypoint} is a meter fix point
	 */
	public Waypoint(String wpName, double wpLatitude, double wpLongitude, double altitude, boolean isMeterFix) {
		super(wpName, wpLatitude, wpLongitude, altitude, isMeterFix);
	}

	/**
	 * 
	 * Creates a {@link Waypoint} at altitude 0 feet
	 * 
	 * @param wpName the {@link Waypoint}'s name
	 * @param wpLatitude the {@link Waypoint}'s latitude, in degrees
	 * @param wpLongitude the {@link Waypoint}'s longitude, in degrees
	 * @param isMeterFix if the {@link Waypoint} is a meter fix point
	 */
	public Waypoint(String wpName, double wpLatitude, double wpLongitude, boolean isMeterFix) {
		// A waypoints can have no altitude and by default is set to 0
		this(wpName, wpLatitude, wpLongitude, 0, isMeterFix);
	}

	@Override
	public String toString() {
		return "Waypoint [sName=" + getName() + ", dLatitude=" + getLatitude() + ", dLongitude=" + getLongitude() + "]";
	}

}
