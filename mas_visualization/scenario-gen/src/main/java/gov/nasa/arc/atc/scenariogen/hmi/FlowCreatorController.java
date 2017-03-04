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

package gov.nasa.arc.atc.scenariogen.hmi;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.scenariogen.base.FlightPlanDataSet;
import gov.nasa.arc.atc.scenariogen.core.Flow;
import gov.nasa.arc.atc.scenariogen.core.FlowType;
import gov.nasa.arc.atc.scenariogen.core.SubFlow;
import gov.nasa.arc.atc.scenariogen.hmi.flow.FlowRateChart;
import gov.nasa.arc.atc.utils.CustomReadOnlyStringWrapper;

import static javafx.application.Platform.runLater;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.controlsfx.control.textfield.TextFields;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class FlowCreatorController implements Initializable {

    static final String CONFIG_CHANGED = "configChanged";

    private static final double DEFAULT_QUARTER_COLUMN_WIDTH = 30;

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private final List<TableColumn<SubFlow, String>> rateColumns = new LinkedList<>();

    // main container
    @FXML
    private Accordion accordion;

    @FXML
    private TitledPane flowCompositionPane;
    @FXML
    private TitledPane flowRateEditionPane;

    // composition
    @FXML
    private ChoiceBox<FlowType> flowTypeCBox;
    @FXML
    private TextField runwayField;
    @FXML
    private TextField baseRateField;
    @FXML
    private ListView<SubFlow> subFlowsListView;


    // rates
    @FXML
    private SplitPane rateSplitPane;
    @FXML
    private TableView<SubFlow> rateTable;

    private FlowRateChart customFlowRateChart;

    private Flow flow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        flow = new Flow();
        flow.addPropertyChangeListener(this::handleFlowChange);
        //
        flowTypeCBox.setItems(FXCollections.observableList(Arrays.asList(FlowType.values())));
        flowTypeCBox.getSelectionModel().select(FlowType.THROUGH);
        //
        baseRateField.setText(Integer.toString(flow.getBaseRate()));
        //TODO add listener on baseFieldRateField
        // temp remove
        accordion.setExpandedPane(flowCompositionPane);
        //
        initListeners();
        initTable();
        accordion.setExpandedPane(flowRateEditionPane);
        //
        customFlowRateChart = new FlowRateChart();
        customFlowRateChart.setFlow(flow);
        rateSplitPane.getItems().add(customFlowRateChart);
        //
        runLater(this::fireConfigChanged);
    }

    void setFlightDataSet(FlightPlanDataSet dataSet) {
        flow.setDataSet(dataSet);
        TextFields.bindAutoCompletion(runwayField, dataSet.getNodes().stream().map(ATCNode::getName).collect(Collectors.toList()));
    }

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    String getDisplayName() {
        return flow.getDisplayName();
    }

    protected Flow getFlow() {
        return flow;
    }

    // used for populating HMI during debug, will have to use config files


    void setNbQuarters(int nbQuarters) {
        flow.setSimulationDuration(nbQuarters);
        reloadCharts();
    }

    void setRunwayName(String runwayName) {
        flow.setRunway(runwayName);
        fireConfigChanged();
    }

    void setDeliveryPoint(String deliveryPointName) {
        flow.setDeliveryPoint(deliveryPointName);
        fireConfigChanged();
    }

    void setFlowType(FlowType type) {
        flow.setFlowType(type);
        flowTypeCBox.getSelectionModel().select(type);
    }

    void createSubFlow(String subFlowName, String nodeName, Color color) {
        SubFlow subFlow = flow.addSubFlow(subFlowName, nodeName, color);
        subFlowsListView.getItems().add(subFlow);
        rateTable.getItems().add(subFlow);
        reloadCharts();
    }


    void setBaseRate(int rate) {
        flow.setBaseRate(rate);
        baseRateField.setText(Integer.toString(rate));
    }

    private void initListeners() {
        flowTypeCBox.getSelectionModel().selectedItemProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "flowTypeCBox selection changed {0} {1} {2}", new Object[]{obs, old, newV});
            flow.setFlowType(newV);
            fireConfigChanged();
        });
        runwayField.textProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "runwayField text changed {0} {1} {2}", new Object[]{obs, old, newV});
            flow.setRunway(runwayField.getText().trim());
            fireConfigChanged();
        });

    }

    private void initTable() {
        TableColumn<SubFlow, String> nameColumn = new TableColumn<>("SubFlow Name");
        nameColumn.setPrefWidth(100);
        nameColumn.setCellValueFactory((TableColumn.CellDataFeatures<SubFlow, String> param) -> new CustomReadOnlyStringWrapper(param.getValue().getName()));
        rateTable.getColumns().add(nameColumn);
        TableColumn<SubFlow, String> colorColumn = new TableColumn<>("");
        colorColumn.setPrefWidth(25);
        colorColumn.setCellValueFactory((TableColumn.CellDataFeatures<SubFlow, String> param) -> new CustomReadOnlyStringWrapper(param.getValue().getColor().toString()));
        colorColumn.setCellFactory(column ->
                new TableCell<SubFlow, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setBackground(new Background(new BackgroundFill(Color.valueOf(item), new CornerRadii(4), new Insets(4, 4, 4, 4))));
                        }
                    }
                }
        );

        rateTable.getColumns().add(colorColumn);


    }

    private void reloadCharts() {
        runLater(() -> {
            //
            customFlowRateChart.updateFlowBars();
            // clearing the table and the series data
            rateTable.getColumns().removeAll(rateColumns);
            rateColumns.clear();
            for (int i = 0; i < flow.getNbQuarters(); i++) {
                TableColumn<SubFlow, String> rateColumn = new TableColumn<>("Q" + i);
                final int quarterID = i;
                rateColumn.setPrefWidth(DEFAULT_QUARTER_COLUMN_WIDTH);
                rateColumn.setCellValueFactory((TableColumn.CellDataFeatures<SubFlow, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getRateAtQuarter(quarterID))));
                rateColumn.setEditable(true);
                rateColumn.setCellFactory(param -> new EditingCell(this));

                rateTable.getColumns().add(rateColumn);
                rateColumns.add(rateColumn);
            }
        });
    }

    private void fireConfigChanged() {
        propertyChangeSupport.firePropertyChange(CONFIG_CHANGED, null, null);
    }

    private void handleFlowChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Flow.VALUE_CHANGED:
                //TODO in separate method
                customFlowRateChart.updateValues();
                rateTable.refresh();
                break;
            case Flow.SCHEDULE_GENERATED:
                //nothing to do
                break;
            case Flow.TRAFFIC_GENERATED:
                //nothing to do
                break;
            default:
                throw new UnsupportedOperationException("unsupported property change :: " + event.getPropertyName());
        }
    }


    private static class EditingCell extends TableCell<SubFlow, String> {

        private final FlowCreatorController controller;

        private TextField textField;

        private EditingCell(FlowCreatorController flowCreatorController) {
            controller = flowCreatorController;
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void commitEdit(String newValue) {
            super.commitEdit(newValue);
            TablePosition pos = getTableView().getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();

            SubFlow item = getTableView().getItems().get(row);
            TableColumn col = pos.getTableColumn();
            int id = Integer.parseInt(col.getText().replace("Q", ""));
            int value = Integer.parseInt(newValue);
            item.setRateAtQuarter(id, value);
            controller.reloadCharts();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getItem());
            setGraphic(null);
        }


        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty().addListener((obs, old, newV) -> {
                if (!newV) {
//                        commitEdit(textField.getText());
                    cancelEdit();
                }
            });
            textField.setOnAction(event -> commitEdit(textField.getText()));
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }


}
