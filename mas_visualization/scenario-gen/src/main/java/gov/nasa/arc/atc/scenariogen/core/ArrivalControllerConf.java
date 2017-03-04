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

package gov.nasa.arc.atc.scenariogen.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ahamon
 */
public class ArrivalControllerConf {

    private final String name;
    private final String type;
    // nodes controlled by the controller
    private final List<String> nodes;
    private final String handOffWaypoint;
    private final String handOffController;
    private final List<String> aircraftList;

    /**
     * @param name
     * @param nodes
     * @param handOffWaypoint
     * @param handOffController
     */
    public ArrivalControllerConf(String name, String type, List<String> nodes, String handOffWaypoint, String handOffController) {
        this.name = name;
        this.type = type;
        System.err.println("ATC " + name + " type = " + type);
        this.nodes = Collections.unmodifiableList(nodes);
        this.handOffWaypoint = handOffWaypoint;
        this.handOffController = handOffController;
        aircraftList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getAircraftList() {
        return Collections.unmodifiableList(aircraftList);
    }

    public String getHandOffController() {
        return handOffController;
    }

    public String getHandOffWaypoint() {
        return handOffWaypoint;
    }

    public List<String> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public String getType() {
        return type;
    }

    public void addAircraft(String acName) {
        aircraftList.add(acName);
    }

    @Override
    public String toString() {
        return "[" + type + "]" + name + " " + handOffWaypoint + "->" + handOffController + " " + nodes;
    }


}
