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

package gov.nasa.arc.atc.metrics;

import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.NewPlane;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ahamon
 */
public final class Metrics {

    private static final Map<DataModel, SimulationCalculations> ALL_SIMULATION_CALCULATIONS = new HashMap<>();
    // TODO: think about an alternate solution
    private static final Map<NewPlane, PlaneCalculation> ALL_PLANE_CALCULATIONS = new HashMap<>();

    private Metrics() {
        // private utility constructor
    }

    public static SimulationCalculations getSimulationCalculation(DataModel dataModel) {
        if (ALL_SIMULATION_CALCULATIONS.containsKey(dataModel)) {
            return ALL_SIMULATION_CALCULATIONS.get(dataModel);
        }
        SimulationCalculations simulationCalculations = new SimulationCalculations(dataModel);
        simulationCalculations.getAllPlanesCalculatedInfo().values().stream().forEach(entry -> ALL_PLANE_CALCULATIONS.put(entry.getPlane(), entry));
        ALL_SIMULATION_CALCULATIONS.put(dataModel, simulationCalculations);
        return simulationCalculations;
    }

    public static PlaneCalculation getPlaneCalculation(NewPlane plane) {
    return ALL_PLANE_CALCULATIONS.get(plane);
    }

}
