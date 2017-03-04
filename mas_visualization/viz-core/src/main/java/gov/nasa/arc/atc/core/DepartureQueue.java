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

import gov.nasa.arc.atc.SimulationManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class DepartureQueue {

    private final List<NewPlane> departurePlanes;
    private final Map<String, Integer> departureTimes;

    public DepartureQueue(List<NewPlane> planes, Map<String, Integer> depTimes) {
        departurePlanes = Collections.unmodifiableList(planes);
        departureTimes = Collections.unmodifiableMap(depTimes);
    }

    public List<NewPlane> getAllDepartures() {
        return departurePlanes;
    }

    public List<NewPlane> getCurrentDepartureQueue() {
        int simTime = SimulationManager.getSimulationDataModel().getSimTime();
        return departurePlanes.stream().filter(p -> departureTimes.get(p.getSimpleName()) >= simTime).collect(Collectors.toList());
    }
}
