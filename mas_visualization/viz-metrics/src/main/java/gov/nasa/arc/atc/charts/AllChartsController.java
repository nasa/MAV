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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * loads all charts for a given category
 *
 * @author Kelsey
 */

public class AllChartsController {
    private static final Logger LOG = Logger.getGlobal();
    private ChartController filterChartController;
    @FXML
    private ScrollPane sPane;
    @FXML
    private VBox chartBox;

    /**
     * --------------------------------
     */
    public AllChartsController() {
    }

    public void addAllCharts(MetricCategory category) {
        System.err.println("  % AllChartsController 3.1 : addAllCharts "+category);
//        System.err.println(" Loading all charts for category:: "+category.getCategoryName());
        for (ChartInfo chart : category.getAllCharts()) {
            System.err.println("  % AllChartsController 3.2 : "+category.getCategoryName());
            System.err.println(" => loading chart:: "+chart.getChartName());
            System.err.println(" => loading chart:: sI"+chart.getSingleChartInfo());
            System.err.println(" => loading chart:: mI"+chart.getMultipleChartInfo());
            loadChart(chart);
        }
    }

    private void loadChart(ChartInfo chart) {
        AnchorPane fChart = new AnchorPane();
        try {
            FXMLLoader f = new FXMLLoader(getClass().getResource(MetricsUtil.METRIC_CHART));
            f.load();
            fChart = f.getRoot();
            filterChartController = f.getController();
            filterChartController.setInfo(chart);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error while creating filter chart 2 : {0}", ex);
            ex.printStackTrace();
        }
        chartBox.getChildren().add(fChart);
    }


}
