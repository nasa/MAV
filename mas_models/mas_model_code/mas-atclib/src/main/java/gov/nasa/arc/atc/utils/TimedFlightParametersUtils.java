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

import gov.nasa.arc.atc.simulation.TimedFlightParameters;

/**
 * @author ahamon
 */
public final class TimedFlightParametersUtils {

    private TimedFlightParametersUtils() {
        // private utility constructor
    }

    public static boolean compareWithoutTime(TimedFlightParameters p1, TimedFlightParameters p2) {
        if (Double.doubleToLongBits(p1.getAirSpeed()) != Double.doubleToLongBits(p2.getAirSpeed())) {
            return false;
        }
        if (Double.doubleToLongBits(p1.getHeading()) != Double.doubleToLongBits(p2.getHeading())) {
            return false;
        }
        if (p1.getPosition() == null) {
            if (p2.getPosition() != null)
                return false;
        } else if (!p1.getPosition().equals(p2.getPosition())) {
            return false;
        }
        if (p1.getStatus() != p2.getStatus()) {
            return false;
        }
        if (Double.doubleToLongBits(p1.getVerticalSpeed()) != Double.doubleToLongBits(p2.getVerticalSpeed())) {
            return false;
        }
        return p1.getToWPT().equals(p2.getToWPT());
    }
}
