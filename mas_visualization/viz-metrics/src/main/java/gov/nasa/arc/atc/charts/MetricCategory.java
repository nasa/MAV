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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetricCategory {

    private String categoryName;
    private ChartInfo singleSeriesChart;
    private final List<ChartInfo> multipleSeriesCharts = new ArrayList<>();
    private final List<ChartInfo> charts = new ArrayList<>();

    /**
     * Category Name
     *
     * @param name the category name
     */
    public void setCategoryName(String name) {
        categoryName = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    /**
     * ----- All Charts ----
     *
     * @return all charts
     */
    public List<ChartInfo> getAllCharts() {
        return charts;
    }

    /**
     * ----- Single Chart ----
     *
     * @param newChartInfo the chart info for the single series
     */
    public void setSingleSeriesChartInfo(ChartInfo newChartInfo) {
        singleSeriesChart = newChartInfo;
        charts.add(singleSeriesChart);
    }

    public void setSingleSeriesChart(Map<Integer, Integer> chart) {
        singleSeriesChart = new ChartInfo();
        singleSeriesChart.setChartName("no name");
        singleSeriesChart.setSingleChart(chart);
        charts.add(singleSeriesChart);
    }

    public ChartInfo getSingleSeriesChart() {
        return singleSeriesChart;
    }

    /**
     * ----- Charts with Multiple Series ----
     *
     * @param chart the chart info for the single series
     */
    public void addMultipleSeriesChart(ChartInfo chart) {
        charts.add(chart);
        multipleSeriesCharts.add(chart);
    }

    public List<ChartInfo> getAllMultipleSeriesCharts() {
        return multipleSeriesCharts;
    }

}
