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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.Map;

/**
 * Creates a single or multiple series line chart from chartInfo
 * tableItemsMap in MetricModel must be set in order to bind each chartseries to each tablerow
 *
 * @author Kelsey
 */
public class ChartController {
    private Series<Integer, Integer> series;
    private int startTime;
    private int maxTime;
    private int colorIncrement = 0;
    @FXML
    private LineChart<Integer, Integer> lineChartId;

//    /**
//     * --------------------------------
//     */
//    public ChartController() {
//    }

    public void setInfo(ChartInfo chart) {
        if (!chart.getSingleChartInfo().isEmpty()) {
            fillSingleChart(chart);
        }
        if (!chart.getMultipleChartInfo().isEmpty()) {
            fillMultipleSeriesChart(chart);
        }
    }

    /**
     * ----- load overall chart ----
     */

    private void fillSingleChart(ChartInfo chartInfo) {
        Map<Integer, Integer> timePlot = chartInfo.getSingleChartInfo();
        System.err.println(" --- ChartController  > timePlot="+timePlot);
        series = new XYChart.Series<>();
        lineChartId.setTitle(chartInfo.getChartName());
        startTime = Collections.min(timePlot.keySet());
        maxTime = Collections.max(timePlot.keySet());
        series.getData().add(new XYChart.Data<>(startTime, timePlot.get(startTime)));
        for (int i = startTime + 1; i < maxTime; i++) {
            if ((int) timePlot.get(i) != timePlot.get(i - 1)) {
                series.getData().add(new XYChart.Data<>(i, timePlot.get(i)));
            }
        }
        series.getData().add(new XYChart.Data<>(maxTime, timePlot.get(maxTime)));
        lineChartId.getData().add(series);
        lineChartId.setCreateSymbols(false); // NO data points
        String strokeStyle = "-fx-stroke: " + MetricsUtil.toRGBCode(Color.DIMGREY) + ";";
        series.getNode().setStyle(strokeStyle);
    }

    /**
     * ----- load charts with multiple series ----
     */

    private void fillMultipleSeriesChart(ChartInfo chart) {
        lineChartId.setTitle(chart.getChartName());
        colorIncrement = 0;
        for (Map.Entry<String, Map<Integer, Integer>> entry : chart.getMultipleChartInfo().entrySet()) {
            String key = entry.getKey();
            Map<Integer, Integer> plot = entry.getValue();
            TableViewRow tableRow = MetricModel.getAllTableItemsMap().get(key);
            System.err.println(" searching for " + key);
            addPlane(key, tableRow.tableStructure, plot);
        }
    }

    private void addPlane(String plane, TableViewRow.TableViewStructure tableRow, Map<Integer, Integer> currPlot) {
        colorIncrement++;
        // plot series
        buildSeries(plane, currPlot);
        lineChartId.getData().add(series);
        setSeriesColor();
        // bind to table row
        series.getNode().visibleProperty().bindBidirectional(tableRow.view);
        String planeWithColorCode = plane.replace("plane_", "") + "__" + Integer.toString(colorIncrement); // example JBU123__1
        series.getNode().visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    // plane hidden
                    String greyPlane = plane.replace("plane_", "") + "__GREY";
                    tableRow.setColumnID(greyPlane);
                } else {
                    tableRow.setColumnID(planeWithColorCode);
                }
            }
        });
    }

    public void buildSeries(String plane, Map<Integer, Integer> currPlot) {
        startTime = Collections.min(currPlot.keySet());
        maxTime = Collections.max(currPlot.keySet());
        series = new XYChart.Series<>();
        series.setName(plane.replace("plane_", ""));
        series.getData().add(new XYChart.Data<>(startTime, currPlot.get(startTime)));
        // plot
        for (int i = startTime; i < maxTime; i++) {
            if (currPlot.get(i) != currPlot.get(i - 1) && currPlot.get(i - 1) != null) {
                series.getData().add(new XYChart.Data<>(i, currPlot.get(i - 1)));
                series.getData().add(new XYChart.Data<>(i, currPlot.get(i)));
            }
        }
        series.getData().add(new XYChart.Data<>(maxTime, currPlot.get(maxTime - 1)));
        series.getData().add(new XYChart.Data<>(maxTime, currPlot.get(maxTime)));
    }

    public void setSeriesColor() {
        Color colorValue = MetricsUtil.COLORS_OPTIONS[colorIncrement % MetricsUtil.COLOR_COUNT];
        String strokeStyle = "-fx-stroke: " + MetricsUtil.toRGBCode(colorValue) + ";";
        // set color of LINE
        series.getNode().setStyle(strokeStyle);
        final String backgroundColorStyle = "-fx-background-color: " + MetricsUtil.toRGBCode(colorValue)
                + ", white;";
        // hide data point color
        boolean viewDataPoints = false;
        lineChartId.setCreateSymbols(viewDataPoints);
        if (viewDataPoints) {
            for (XYChart.Data<Integer, Integer> data : series.getData()) {
                if (data != null) {
                    if (data.getNode() != null) {
                        data.getNode().setStyle(backgroundColorStyle);
                    }
                }
            }
        }
    }

}
