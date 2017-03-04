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

import java.util.Collections;
import java.util.List;

/**
 * @author ahamon
 */
public class ControllerInitBlockImpl implements ControllerInitBlock {

    private final String controllerName;
    private final String handOffController;
    private final String handOffWaypoint;
    private final List<String> waypoints;

    /**
     * @param controllerName    the controller name
     * @param handOffController the name of the controller receiving hand-offs from this controller
     * @param handOffWaypoint   the waypoint at which hand-off occur
     * @param waypoints         the waypoints the controller is responsible for
     */
    public ControllerInitBlockImpl(String controllerName, String handOffController, String handOffWaypoint, List<String> waypoints) {
        this.controllerName = controllerName;
        this.handOffController = handOffController;
        this.handOffWaypoint = handOffWaypoint;
        this.waypoints = waypoints;
    }

    @Override
    public String getControllerName() {
        return controllerName;
    }

    @Override
    public String getHandOffWaypoint() {
        return handOffWaypoint;
    }

    @Override
    public String getHandOffController() {
        return handOffController;
    }

    @Override
    public List<String> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }

}
