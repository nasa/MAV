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

/**
 * @author ahamon
 */
public class WindParameters {

    // A wind from the north has a direction of 180

    // direction in degree
    private final double direction;

    // speed in Knot [kt]
    private final double speed;


    /**
     * @param direction the wind direction in [degree]
     * @param speed     the wind speed in [kt]. If the speed is negative, then the direction is changed to its opposite and the speed is given the absolute value
     */
    public WindParameters(double direction, double speed) {
        if (speed < 0) {
            this.direction = (direction + 180) % 360;
            this.speed = -speed;
        } else {
            this.direction = direction % 360;
            this.speed = speed;
        }
    }

    public double getDirection() {
        return direction;
    }

    public double getSpeed() {
        return speed;
    }
}
