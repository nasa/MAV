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

package gov.nasa.arc.atc.physics;

import gov.nasa.arc.atc.core.Coordinates;

/**
 * @author ahamon
 */
public class WindCell {

    //
    private final Coordinates northWestCorner;
    // distance in [km]
    private final double widthEast;
    private final double widthSouth;
    private final int minimumAltitude;
    private final int maximumAltitude;

    public WindCell(Coordinates northWestCorner, double widthEast, double widthSouth, int minimumAltitude, int maximumAltitude) {
        this.northWestCorner = northWestCorner;
        this.widthEast = widthEast;
        this.widthSouth = widthSouth;
        this.minimumAltitude = minimumAltitude;
        this.maximumAltitude = maximumAltitude;
    }

    public WindCell(Coordinates northWestCorner, double width, int minimumAltitude, int maximumAltitude) {
        this(northWestCorner,width,width,minimumAltitude,maximumAltitude);
    }

    public Coordinates getNorthWestCorner() {
        return northWestCorner;
    }

    public double getWidthEast() {
        return widthEast;
    }

    public double getWidthSouth() {
        return widthSouth;
    }

    public int getMinimumAltitude() {
        return minimumAltitude;
    }

    public int getMaximumAltitude() {
        return maximumAltitude;
    }
}
