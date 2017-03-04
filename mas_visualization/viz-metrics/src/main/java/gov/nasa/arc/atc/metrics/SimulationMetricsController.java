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

import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.charts.AllChartsController;
import gov.nasa.arc.atc.charts.BoxInfo;
import gov.nasa.arc.atc.charts.MetricCategory;
import gov.nasa.arc.atc.charts.MetricModel;
import gov.nasa.arc.atc.charts.TableViewRow;
import gov.nasa.arc.atc.utils.IndexComparator;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.runLater;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Loads simulation Boxes and Charts from MetricModel Loads the TableView structure and sets the tableItemsMap in MetricModel
 *
 * @author Kelsey
 *
 */
public class SimulationMetricsController {

    private static final Logger LOG = Logger.getGlobal();

    private TableView<TableViewRow.TableViewStructure> tableView = new TableView<>();
    private final ObservableList<TableViewRow.TableViewStructure> tableList = FXCollections.observableArrayList();
    private Map<String, PlaneCalculation> planeCalcInfo;
    private BoxMetricsController boxController;
    private AllChartsController allChartsController;
    @FXML
    private Button closeButton;
    @FXML
    private VBox vbox;
    @FXML
    private Accordion accordian;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    public CheckBox selectAll;
    @FXML
    private CheckBox deselectAll;
    @FXML
    private CheckBox selectArrivals;
    @FXML
    private CheckBox selectDepartures;

    /**
     * --------------------------------
     */
    public SimulationMetricsController() {
        runLater(SimulationMetricsController.this::load);
    }

    private void load() {
        System.err.println("  % SimulationMetricsController 1 : load");
        createTableView();
        loadAllSimulationMetricBoxes(MetricModel.getAllBoxes());
        loadAllCategories(MetricModel.getMetricCategories());
    }

    private void createTableView() {
        fillTableView();
        anchorPane.getChildren().add(tableView);
        anchorPane.setMinWidth(300);
        anchorPane.setPrefWidth(400);
        anchorPane.setMaxWidth(500);
        AnchorPane.setBottomAnchor(tableView, (double) 30);
        AnchorPane.setTopAnchor(tableView, (double) 5);
        AnchorPane.setLeftAnchor(tableView, (double) 5);
        AnchorPane.setRightAnchor(tableView, (double) 5);
    }

    /**
     * --------Load Metric Boxes---------
     */
    private void loadAllSimulationMetricBoxes(List<BoxInfo> allBoxes) {
        allBoxes.stream().forEach(this::loadBox);
    }

    private void loadBox(BoxInfo boxInfo) {
        System.err.println(" * Loading box :: "+boxInfo.getName());
        TitledPane node;
        FXMLLoader f = new FXMLLoader(getClass().getResource(MetricsUtil.SIMPLE_METRICS_BOX));
        try {
            f.load();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error while creating simple metrics : {0}", ex);
        }
        node = f.getRoot();
        boxController = f.getController();
        boxController.setInfo(boxInfo);
        vbox.getChildren().add(node);
    }

    /**
     * ----------Load All Charts-----------
     */
    private void loadAllCategories(List<MetricCategory> categories) {
        System.err.println("  % SimulationMetricsController 2 : loadAllCategories "+categories);
        categories.stream().forEach(this::loadAllCharts);
    }

    private void loadAllCharts(MetricCategory cat) {
        System.err.println("  % SimulationMetricsController 3 : loadAllCharts "+cat);
        TitledPane tPane;
        FXMLLoader f = new FXMLLoader(getClass().getResource(MetricsUtil.ALL_CHARTS));
        try {
            f.load();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error while creating chart layout: {0}", ex);
        }
        tPane = f.getRoot();
        tPane.setText(cat.getCategoryName());
        allChartsController = f.getController();
        allChartsController.addAllCharts(cat);
        accordian.getPanes().add(tPane);
    }

    /**
     * ----------Handle TableView-----------
     */
    private void fillTableView() {
        /**
         * --------- customize table info/rows to load here ----------
         */
        //-------------
        SimulationCalculations simulationCalculations = Metrics.getSimulationCalculation(SimulationManager.getSimulationDataModel());
        planeCalcInfo = simulationCalculations.getAllPlanesCalculatedInfo();
        int colorIncrement = 0;
        //TODO
        for (Map.Entry<String, PlaneCalculation> entry : planeCalcInfo.entrySet()) {
            //TODO no use for a map here -> see if could be removed in plane calculations
            colorIncrement++;
            String key = entry.getValue().getSimpleName();
            PlaneCalculation value = entry.getValue();
            // sneaky format to change the text color
            String columnIdWithColorCode = key.replace("plane_", "") + "__" + Integer.toString(colorIncrement); //example JBU123__1
            TableViewRow row = new TableViewRow(true, columnIdWithColorCode, Integer.toString(value.getDelayTime()),Integer.toString(value.getMeterCount()), value.getFlightType(), MetricsUtil.getAirline(key));

            //-------------
//	    	tableInfo = DisplayViewConfigurations.getSimulationCalculations().getOTHERINFO();
//			int colorIncrement = 0;
//			for( Map.Entry<String, OtherCalculation> entry : tableInfo.entrySet()){
//				colorIncrement++;
//				String key = entry.getKey();
//				OtherCalculation value = entry.getValue();
//				// sneaky format to change the text color
//				String columnIdWithColorCode = key+"__"+Integer.toString(colorIncrement); //example JBU123__1
//				TableViewRow row = new TableViewRow(true, columnIdWithColorCode, value.getValue1() ),
//						Integer.toString( value.getValue2() ) , value.getValue3(), value.getValue3());
            //-------------
            row.tableStructure.view.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue) {
                    // a row is unselected
                    uncheckSelectAll();
                    if (row.tableStructure.sort3Property().getValue().equals(MetricModel.COLUMN_SORT_THREE_OPTION_1)) {
                        uncheckArrival();
                    } else if (row.tableStructure.sort3Property().getValue().equals(MetricModel.COLUMN_SORT_THREE_OPTION_2)) {
                        uncheckDeparture();
                    }
                } else {
                    // a row is selected
                    uncheckDeselectAll();
                    updateCheckBoxes();
                }
            });
            tableList.add(row.tableStructure);
            MetricModel.addTableItemsMap(key, row);
        }

        createTableStructure();
    }

    @SuppressWarnings("unchecked")
    private void createTableStructure() {
        selectArrivals.setText(MetricModel.COLUMN_SORT_THREE_OPTION_1 + "s");
        selectDepartures.setText(MetricModel.COLUMN_SORT_THREE_OPTION_2 + "s");
        setTableView(new TableView<>());
        getTableView().setItems(tableList);
        final TableColumn<TableViewRow.TableViewStructure, String> columnID = new TableColumn<>(MetricModel.COLUMN_LABEL_ID); // Column Label
        final TableColumn<TableViewRow.TableViewStructure, Boolean> viewColumn = new TableColumn<>(MetricModel.COLUMN_LABEL_VIEW);
        final TableColumn<TableViewRow.TableViewStructure, String> sortColumn1 = new TableColumn<>(MetricModel.COLUMN_LABEL_SORT_1);
        final TableColumn<TableViewRow.TableViewStructure, String> sortColumn2 = new TableColumn<>(MetricModel.COLUMN_LABEL_SORT_2);
        final TableColumn<TableViewRow.TableViewStructure, String> sortColumn3 = new TableColumn<>(MetricModel.COLUMN_LABEL_SORT_3); // arrival or departure
        final TableColumn<TableViewRow.TableViewStructure, String> sortColumn4 = new TableColumn<>(MetricModel.COLUMN_LABEL_SORT_4); // airline
        getTableView().getColumns().addAll(columnID, viewColumn, sortColumn1, sortColumn2, sortColumn3, sortColumn4);
        columnID.setCellValueFactory(new PropertyValueFactory<>(MetricsUtil.COLUMN_ID)); // must match first part of property method name in TableViewRow
        columnID.setCellFactory((TableColumn<TableViewRow.TableViewStructure, String> param) -> new TableCell<TableViewRow.TableViewStructure, String>() {
            // on initialization
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    String[] newItem = item.split("__");
                    Color colorValue;
                    if (newItem[1].contains("GREY")) {
                        colorValue = Color.DIMGREY;
                    } else {
                        int colorCode = Integer.parseInt(newItem[1]);
                        colorValue = MetricsUtil.COLORS_OPTIONS[colorCode % MetricsUtil.COLOR_COUNT];
                    }
                    this.setTextFill(colorValue);
                    setText(newItem[0]);
                }
            }

        });

        viewColumn.setCellValueFactory(new PropertyValueFactory<>(MetricsUtil.COLUMN_VIEW));
        viewColumn.setCellFactory(CheckBoxTableCell.forTableColumn(viewColumn));
        viewColumn.setEditable(true);

        sortColumn1.setCellValueFactory(new PropertyValueFactory<>(MetricsUtil.COLUMN_SORT_1)); //must match
        sortColumn1.setComparator(new IndexComparator());
        sortColumn2.setCellValueFactory(new PropertyValueFactory<>(MetricsUtil.COLUMN_SORT_2));
        sortColumn2.setComparator(new IndexComparator());
        sortColumn3.setCellValueFactory(new PropertyValueFactory<>(MetricsUtil.COLUMN_SORT_3));
        sortColumn4.setCellValueFactory(new PropertyValueFactory<>(MetricsUtil.COLUMN_SORT_4));

        getTableView().setEditable(true);
    }

    public TableView<TableViewRow.TableViewStructure> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<TableViewRow.TableViewStructure> tableView) {
        this.tableView = tableView;
    }

    /**
     * ----------Handle Buttons-----------
     *
     * @param event the event triggering the close action
     */
    @FXML
    public void handleClose(ActionEvent event) {
//        DisplayViewConfigurations.closeSM();
        throw new UnsupportedOperationException("TODO");
    }

    @FXML
    public void handleDeselectAll(ActionEvent event) {
        tableView.getItems().stream().forEach(p -> p.setView(false));
    }

    @FXML
    public void handleSelectAll(ActionEvent event) {
        tableView.getItems().stream().forEach(p -> p.setView(true));
    }

    @FXML
    public void handleSelectArrivals(ActionEvent event) {
        boolean select = selectArrivals.isSelected();
        tableView.getItems().stream().filter(p -> p.sort3Property().get().equals(MetricModel.COLUMN_SORT_THREE_OPTION_1)).forEach(p -> p.setView(select));
    }

    @FXML
    public void handleSelectDepartures(ActionEvent event) {
        boolean select = selectDepartures.isSelected();
        tableView.getItems().stream().filter(p -> p.sort3Property().get().equals(MetricModel.COLUMN_SORT_THREE_OPTION_2)).forEach(p -> p.setView(select));
    }

    protected void uncheckDeparture() {
        selectDepartures.setSelected(false);
    }

    protected void uncheckArrival() {
        selectArrivals.setSelected(false);
    }

    protected void uncheckDeselectAll() {
        deselectAll.setSelected(false);
    }

    protected void uncheckSelectAll() {
        selectAll.setSelected(false);
    }

    private void updateCheckBoxes() {
        boolean all = true;
        boolean arrivals = true;
        boolean departures = true;
        boolean notAll = true;
        for (TableViewRow.TableViewStructure p : tableView.getItems()) {
            if (!p.viewProperty().get()) {
                all = false;
                if (p.sort3Property().get().equals(MetricModel.COLUMN_SORT_THREE_OPTION_1)) {
                    arrivals = false;
                } else if (p.sort3Property().get().equals(MetricModel.COLUMN_SORT_THREE_OPTION_2)) {
                    departures = false;
                }
            } else {
                notAll = false;
            }
        }
        selectAll.setSelected(all);
        deselectAll.setSelected(notAll);
        selectArrivals.setSelected(arrivals);
        selectDepartures.setSelected(departures);
    }

}
