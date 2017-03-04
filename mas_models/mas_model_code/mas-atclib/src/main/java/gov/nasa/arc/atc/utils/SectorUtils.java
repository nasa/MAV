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

package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.GeographyElementBounds;
import gov.nasa.arc.atc.geography.Region;
import gov.nasa.arc.atc.geography.Sector;

/**
 * 
 * @author ahamon
 *
 */
public class SectorUtils {

	public static final double DEFAULT_COORDINATES = 100.0;

	private SectorUtils() {
		// private utility constructor
	}

	/**
	 * 
	 * @param sectors the sectors to analyze
	 * @return the sector's bounds
	 */
	public static GeographyElementBounds getSectorsBounds(Sector... sectors) {
		if (sectors == null) {
			return null;
		}
		//
		double minLat = DEFAULT_COORDINATES;
		double maxLat = -DEFAULT_COORDINATES;
		double minLong = DEFAULT_COORDINATES;
		double maxLong = -DEFAULT_COORDINATES;
		double minTanLat = DEFAULT_COORDINATES;
		double maxTanLat = -DEFAULT_COORDINATES;
		double tanLat;
		double lon;

        for (Sector sector : sectors) {
            // stream does not work here due to non final variables
            for (Region region : sector.getRegions()) {
                for (Coordinates vertex : region.getVertices()) {
                    minLat = Math.min(minLat, vertex.getLatitude());
                    maxLat = Math.max(maxLat, vertex.getLatitude());
                    tanLat = MathUtils.tanLatitude(vertex.getLatitude());
                    lon = vertex.getLongitude();
                    if (minTanLat > tanLat) {
                        minTanLat = tanLat;
                    } else if (maxTanLat < tanLat) {
                        maxTanLat = tanLat;
                    }
                    if (minLong > lon) {
                        minLong = lon;
                    } else if (maxLong < lon) {
                        maxLong = lon;
                    }
                }
            }
        }
		//
		double sectorLatOffset = maxTanLat - minTanLat;
		double sectorLongOffset = maxLong - minLong;
		//
		return new GeographyElementBounds(minLat, maxLat, minLong, maxLong, sectorLatOffset, sectorLongOffset);
	}

}
