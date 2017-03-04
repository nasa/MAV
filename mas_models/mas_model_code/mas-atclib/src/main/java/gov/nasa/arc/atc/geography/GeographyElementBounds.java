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

package gov.nasa.arc.atc.geography;

/**
 * 
 * @author ahamon
 *
 */
public class GeographyElementBounds {

	private final double minLatitude;
	private final double maxLatitude;
	private final double minLongitude;
	private final double maxLongitude;
	private final double latitudeOffset;
	private final double longitudeOffset;

	/**
	 * @param minLatitude
	 * @param maxLatitude
	 * @param minLongitude
	 * @param maxLongitude
     * @param latitudeOffset
     * @param longitudeOffset
     */
	public GeographyElementBounds(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude, double latitudeOffset, double longitudeOffset) {
		this.minLatitude = minLatitude;
		this.maxLatitude = maxLatitude;
		this.minLongitude = minLongitude;
		this.maxLongitude = maxLongitude;
		this.latitudeOffset = latitudeOffset;
		this.longitudeOffset = longitudeOffset;
	}

	public double getMinLatitude() {
		return minLatitude;
	}

	public double getMaxLatitude() {
		return maxLatitude;
	}

	public double getMinLongitude() {
		return minLongitude;
	}

	public double getMaxLongitude() {
		return maxLongitude;
	}

	public double getLatitudeOffset() {
		return latitudeOffset;
	}

	public double getLongitudeOffset() {
		return longitudeOffset;
	}

	public double getDeltaLatitude() {
		return maxLatitude - minLatitude;
	}

	public double getDeltaLongitude() {
		return maxLongitude - minLongitude;
	}

}
