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

package gov.nasa.arc.atc.core;

import java.text.DecimalFormat;

/**
 * @author ahamon
 */
public class Coordinates {

    /**
     * Maximum absolute value for latitudes
     */
    public static final double ABSOLUTE_LATITUDE_MAX = 90;

    /**
     * Maximum absolute value for longitudes
     */
    public static final double ABSOLUTE_LONGITUDE_MAX = 180;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.###");

    private final double latitude;
    private final double longitude;

    /**
     * @param latitude  latitude in [degree]
     * @param longitude longitude in [degree]
     */
    public Coordinates(double latitude, double longitude) {
//		if (Math.abs(latitude) > ABSOLUTE_LATITUDE_MAX) {
//			throw new IllegalArgumentException(" Given latitude (" + latitude + ") absolute value is greater than " + ABSOLUTE_LATITUDE_MAX);
//		}
//		if (Math.abs(longitude) > ABSOLUTE_LONGITUDE_MAX) {
//			throw new IllegalArgumentException(" Given longitude (" + longitude + ") absolute value is greater than " + ABSOLUTE_LONGITUDE_MAX);
//		}
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return the latitude value in [degree]
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return longitude in [degree]
     */
    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "[[ lat:" + DECIMAL_FORMAT.format(latitude) + " ; lon:" + DECIMAL_FORMAT.format(longitude) + " ]]";
    }

}
