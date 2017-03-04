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

import java.util.*;

/**
 * Manages the 3 main components loaded in the SimulationMetrics Display [boxes, charts and tableView]
 *
 * @author Kelsey
 */
public class MetricModel {

    //todo make it so it supports several data model calculations at the same time

    /**
     * --------- customize table column names here ----------
     */
    public static final String COLUMN_LABEL_ID = "Aircraft";
    public static final String COLUMN_LABEL_VIEW = "View";
    public static final String COLUMN_LABEL_SORT_1 = "Delay";
    public static final String COLUMN_LABEL_SORT_2 = "Meter";
    public static final String COLUMN_LABEL_SORT_3 = "Flight";
    public static final String COLUMN_LABEL_SORT_4 = "Airline";
    //toggle button options
    public static final String COLUMN_SORT_THREE_OPTION_1 = "Arrival"; // is compared to col3 value in tablerow
    public static final String COLUMN_SORT_THREE_OPTION_2 = "Departure";

    /**
     * ----- Metric Model components ----
     */
    private static final List<BoxInfo> ALL_SUMMARY_BOXES = new ArrayList<>();
    private static final List<MetricCategory> ALL_METRIC_CATEGORIES = new ArrayList<>();
    private static final Map<String, TableViewRow> tableItemsMap = new HashMap<>();

    /**
     * tableView labels are defined above tableItems [or rows] are added in SimulationMetricsController this function is called by FilterChartController when loading multiple
     * series for a chart
     *
     * @return Map[String-TableViewRow]
     */
    public static Map<String, TableViewRow> getAllTableItemsMap() {
        return Collections.unmodifiableMap(tableItemsMap);
    }

    public static void addTableItemsMap(String name, TableViewRow tableViewRow) {
        tableItemsMap.put(name, tableViewRow);
    }

    /**
     * boxes are added in SimulationCalculations by category this function is called by SimulationMetricsController to load/use
     *
     * @return List of {@link BoxInfo}
     */
    public static List<BoxInfo> getAllBoxes() {
        return Collections.unmodifiableList(ALL_SUMMARY_BOXES);
    }

    public static void addBox(BoxInfo box) {
        ALL_SUMMARY_BOXES.add(box);
    }

    /**
     * metric categories are added in SimulationCalculations this function is called by SimulationMetricsController to load/use
     *
     * @return List of {@link MetricCategory}
     */
    public static List<MetricCategory> getMetricCategories() {
        return Collections.unmodifiableList(ALL_METRIC_CATEGORIES);
    }

    public static void addMetricCategory(MetricCategory metricCategory) {
        ALL_METRIC_CATEGORIES.add(metricCategory);
    }

}
