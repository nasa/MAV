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

import gov.nasa.arc.atc.core.Coordinates;
import java.text.DecimalFormat;

/**
 * 
 * @author hamon
 *
 */
public class Position {

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.###");

	private final double latitude;
	private final double longitude;
	private final double altitude;

	/**
	 * 
	 * @param lat position's latitude
	 * @param lon position's longitude
	 * @param alt position's altitude in feet
	 */
	public Position(double lat, double lon, double alt) {
		latitude = lat;
		longitude = lon;
		altitude = alt;
	}

	/**
	 * 
	 * @param coordinates position's coordinates
	 * @param alt position's altitude in feet
	 */
	public Position(Coordinates coordinates, double alt) {
		this(coordinates.getLatitude(), coordinates.getLongitude(), alt);
	}

	/**
	 * 
	 * @return the position's latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @return the position's longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @return the position's altitude in feet
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Uses {@link DecimalFormat} "##.###" for latitude and longitude
     * @return
   	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[[ lat:");
		sb.append(DECIMAL_FORMAT.format(latitude));
		sb.append(" ; lon:");
		sb.append(DECIMAL_FORMAT.format(longitude));
		sb.append(" ; alt:");
		sb.append((int) altitude);
		sb.append(" ]]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(altitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (Double.doubleToLongBits(altitude) != Double.doubleToLongBits(other.altitude))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
        return Double.doubleToLongBits(longitude) == Double.doubleToLongBits(other.longitude);
	}

}
