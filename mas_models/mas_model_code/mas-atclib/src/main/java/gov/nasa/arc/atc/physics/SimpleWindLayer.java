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

import gov.nasa.arc.atc.geography.Position;

/**
 * @author ahamon
 */
public class SimpleWindLayer implements WindLayer {

    private final int minimumAltitude;
    private final int maximumAltitude;
    private final WindParameters windParameters;

    public SimpleWindLayer(int minimumAltitude, int maximumAltitude, WindParameters windParameters) {
        if (minimumAltitude > maximumAltitude) {
            throw new IllegalArgumentException("min altitude (" + minimumAltitude + ") cannot be greater than max altitude (" + maximumAltitude + ")");
        }
        this.minimumAltitude = minimumAltitude;
        this.maximumAltitude = maximumAltitude;
        this.windParameters = windParameters;
    }

    @Override
    public WindParameters getWindParameters() {
        return windParameters;
    }

    @Override
    public boolean contains(Position position) {
        return contains(position.getAltitude());
    }

    public boolean contains(double altitude) {
        return altitude >= minimumAltitude && altitude < maximumAltitude;
    }

    public int getMinimumAltitude() {
        return minimumAltitude;
    }

    public int getMaximumAltitude() {
        return maximumAltitude;
    }

}
