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

/**
 * @author ahamon
 */
public class ControllerHandOff {
    // TODO: temporary : need to by operation/modelling compliant

    private final int simulationTime;
    private final String controllerFullName;
    private final String aircraftFullName;
    private final double aircraftLatitude;
    private final double aircraftLongitude;

    // TODO: extend to position in general AND think about the necessary parameters

    /**
     * @param simulationTime     simulation time at which the hand-off occurs
     * @param controllerFullName the controller full name (includes its package)
     * @param aircraftFullName   aircraft full name (includes its package)
     * @param aircraftLatitude   aircraft latitude
     * @param aircraftLongitude  aircraft longitude
     */
    public ControllerHandOff(int simulationTime, String controllerFullName, String aircraftFullName, double aircraftLatitude, double aircraftLongitude) {
        this.simulationTime = simulationTime;
        this.controllerFullName = controllerFullName;
        this.aircraftFullName = aircraftFullName;
        this.aircraftLatitude = aircraftLatitude;
        this.aircraftLongitude = aircraftLongitude;
    }

    public int getSimulationTime() {
        return simulationTime;
    }

    public String getControllerFullName() {
        return controllerFullName;
    }

    public String getAircraftFullName() {
        return aircraftFullName;
    }

    public double getAircraftLatitude() {
        return aircraftLatitude;
    }

    public double getAircraftLongitude() {
        return aircraftLongitude;
    }

}
