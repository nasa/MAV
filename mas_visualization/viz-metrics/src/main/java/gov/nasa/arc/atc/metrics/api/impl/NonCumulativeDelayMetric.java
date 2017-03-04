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

package gov.nasa.arc.atc.metrics.api.impl;

import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.metrics.Metrics;
import gov.nasa.arc.atc.metrics.PlaneCalculation;
import gov.nasa.arc.atc.metrics.api.Metric;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ahamon
 */
public class NonCumulativeDelayMetric implements Metric<NewPlane, Integer> {


    private final Map<NewPlane, PlaneCalculation> calculations;

    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    public NonCumulativeDelayMetric() {
        calculations = new HashMap<>();
    }

    @Override
    public void addData(NewPlane dataObject) {
        calculations.put(dataObject, Metrics.getPlaneCalculation(dataObject));
    }

    @Override
    public Integer getMetricAt(int time, NewPlane dataObject) {
        int delay = calculations.get(dataObject).getDelayAt(time) - calculations.get(dataObject).getDelayAt(time - 1);
        min = Math.min(delay, min);
        max = Math.max(delay, max);
        return delay;
    }

    @Override
    public Integer getMin() {
        return min;
    }

    @Override
    public Integer getMax() {
        return max;
    }
}
