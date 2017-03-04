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

/**
 * @author ahamon
 */
public interface SimulatedElement {

    /**
     * @return the simulation time the {@link SimulatedElement} is currently set to
     */
    int getSimTime();

    /**
     * Updates the {@link SimulatedElement} to the new simulation time
     *
     * @param simulationTime the simulation time to be set
     */
    void update(int simulationTime);

}
