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

package gov.nasa.arc.atc.charts;

import gov.nasa.arc.atc.metrics.MetricsUtil;
import gov.nasa.arc.atc.metrics.PlaneCalculation;

import java.util.HashMap;
import java.util.Map;

/**
 * Design functions to return specific ChartInfo for specific HashMaps to plot
 *
 * @author Kelsey
 */
public class MetricModelUtil {

    public static ChartInfo getAllPlaneSeriesFromMap(String metricCategory, String plotType, Map<String, PlaneCalculation> allCalculatedInfo) {
        Map<String, PlaneCalculation> allPlaneInfo = allCalculatedInfo;
        Map<String, Map<Integer, Integer>> specificChart = new HashMap<>();
        ChartInfo chartInfo = new ChartInfo();
        for (Map.Entry<String, PlaneCalculation> info : allPlaneInfo.entrySet()) {
            String plane = info.getKey();
            //specific
            if (metricCategory.equals(MetricsUtil.DELAY) && plotType.equals(MetricsUtil.NON_CUMULATIVE)) {
                specificChart.put(plane, info.getValue().getDelayTimesMap());
            } else if (metricCategory.equals(MetricsUtil.METER) && plotType.equals(MetricsUtil.NON_CUMULATIVE)) {
                specificChart.put(plane, info.getValue().getMeterPlotsMap());
            } else if (metricCategory.equals(MetricsUtil.DELAY) && plotType.equals(MetricsUtil.CUMULATIVE)) {
                specificChart.put(plane, info.getValue().getCumDelayTimesMap());
            } else if (metricCategory.equals(MetricsUtil.METER) && plotType.equals(MetricsUtil.CUMULATIVE)) {
                specificChart.put(plane, info.getValue().getCumDelayTimesMap());
            }
        }
        //specific
        chartInfo.setChartName(metricCategory + " " + plotType);
        chartInfo.setMultipleSeriesChart(specificChart);
//        System.err.println(" # MetricModelUtil for - "+metricCategory+" _ "+plotType+" -  => "+chartInfo);
        return chartInfo;
    }

}
