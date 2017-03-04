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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kelsey
 * @author ahamon
 */
public class ChartInfo {


    private String chartName;
    // a ChartInfo object can have either a chart map, OR a multiplePlots map
    private final Map<Integer, Integer> chart = new HashMap<>();
    private final Map<String, Map<Integer, Integer>> multiplePlots= new HashMap<>();


    public ChartInfo() {
        chartName = "fill in";
    }

//    public ChartInfo(Map<Integer, Integer> newChart) {
//        chart = newChart;
//    }

    public ChartInfo(Map<Integer, Integer> newChart, String name) {
        chartName = name;
        chart.putAll(newChart);
//        System.err.println(" **** chart:: "+chartName+ " init SingleChart ::"+newChart);
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public void setSingleChart(Map<Integer, Integer> newChart) {
//        System.err.println(" **** chart:: "+chartName+ " setSingleChart ::"+newChart);
        chart.putAll(newChart);
    }

    public Map<Integer, Integer> getSingleChartInfo() {
//        System.err.println(" **** chart:: "+chartName+ " has chart ::"+chart);
        return Collections.unmodifiableMap(chart);
    }

    public void setMultipleSeriesChart(Map<String, Map<Integer, Integer>> mChart) {
        multiplePlots.putAll(mChart);
    }

    public Map<String, Map<Integer, Integer>> getMultipleChartInfo() {
        return multiplePlots;
    }


}
