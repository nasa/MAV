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

package gov.nasa.arc.atc.metrics.comparison.radar.sectordelay;

import gov.nasa.arc.atc.metrics.comparison.radar.RadarAxisFunction;

/**
 * @author ahamon
 */
abstract class SimulationFunction implements RadarAxisFunction<SimulationW2R6> {

    private int min = -1;
    private int max = -1;

    @Override
    public void addDataSet(SimulationW2R6 dataSet) {
        Number value = calculate(dataSet);
        min = min == -1 ? value.intValue() : Math.min(min, value.intValue());
        max = max == -1 ? value.intValue() : Math.max(max, value.intValue());
    }

    @Override
    public Integer getMin() {
        return 0;
    }

    @Override
    public Integer getMax() {
        return Math.max(1,max);
    }

}
