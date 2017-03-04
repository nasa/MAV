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

import gov.nasa.arc.atc.MainResources;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.parsing.xml.queue.ATCGeographyQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.scenariogen.base.FlightPlanDataLoader;
import gov.nasa.arc.atc.scenariogen.base.FlightPlanDataSet;
import gov.nasa.arc.atc.scenariogen.core.*;
import gov.nasa.arc.atc.scenariogen.export.ScenarioXMLExporter;
import gov.nasa.arc.atc.scenariogen.hmi.flightplans.ScheduledFlightPlanContainerController;
import gov.nasa.arc.atc.scenariogen.hmi.flow.FlowViewerContainerController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 */
public class ScenarioGeneratorController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    private static final String FLOW_CREATOR_FXML = "FlowCreator.fxml";
    private static final String FLOW_VIEWER_CONTAINER_FXML = "FlowViewerContainer.fxml";
    private static final String SCHEDULED_FLIGHT_PLANS_CONTAINER_FXML = "ScheduledFlightPlanContainer.fxml";

    private static final String DEFAULT_DATA_SET_PATH = FlightPlanDataSet.class.getResource("FlightDataWeek2Run6.xml").getPath();
    //    private static final String DEFAULT_OUTPUT_PATH = DefaultOutput.class.getResource("defaultOutputLocation.txt").getPath();
    private static final String DEFAULT_OUTPUT_DIR_PATH = "/Desktop/ScenarioGen";
    private static final String DEFAULT_OUTPUT_FILE_NAME = "defaultScenario.xml";
    private static final File DEFAULT_GEOGRAPHY_FILE = new File(MainResources.class.getResource("Geography_Week2.xml").getPath());

    private static final int DEFAULT_SIMULATION_START = 0;
    private static final int DEFAULT_SIMULATION_DURATION = 1;

    private File dataSetFile;
    private int startTime;
    private int nbQuarters;
    private String outputDirectory;

    // inputs
    @FXML
    private TextField dataSetField;

    // configuration
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField simulationDurationField;
    @FXML
    private TabPane flowsTabPane;
    @FXML
    private ListView<Flow> trafficFlowListView;

    // output
    @FXML
    private ChoiceBox<ScenarioOutputFormat> outputFormatChoiceB;
    @FXML
    private TextField outputFolderField;
    @FXML
    private Button exportButton;

    // scenario generation
    @FXML
    private Button generateB;


    // data set
    private FlightPlanDataSet dataSet;

    //
    private FlowViewerContainerController flowViewerContainerController;
    private ScheduledFlightPlanContainerController scheduledFlightPlanContainerController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //setting default values
        dataSetFile = new File(DEFAULT_DATA_SET_PATH);
        startTime = DEFAULT_SIMULATION_START;
        nbQuarters = DEFAULT_SIMULATION_DURATION;
        outputDirectory = DEFAULT_OUTPUT_DIR_PATH;

        // parsing airports
        ATCGeography geography = (ATCGeography) XMLMaster.requestParsing(DEFAULT_GEOGRAPHY_FILE, new ATCGeographyQueueParser(), this);
        SimulationManager.setATCGeography(geography);
        // creating flow viewer container
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FLOW_VIEWER_CONTAINER_FXML));
            fXMLLoader.load();
            AnchorPane flowViewerContainerNode = fXMLLoader.getRoot();
            flowViewerContainerController = fXMLLoader.getController();
            Scene flowViewerScene = new Scene(flowViewerContainerNode, 600, 1000);
            Stage flowViewerStage = new Stage();
            flowViewerStage.setScene(flowViewerScene);
            flowViewerStage.show();
            flowViewerStage.setX(1000);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception while loading flow viewer {0}", e);
        }

        // creating scheduled flight plans viewer container
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(SCHEDULED_FLIGHT_PLANS_CONTAINER_FXML));
            fXMLLoader.load();
            SplitPane fplViewerContainerNode = fXMLLoader.getRoot();
            scheduledFlightPlanContainerController = fXMLLoader.getController();
            Scene fplViewerScene = new Scene(fplViewerContainerNode, 1000, 800);
            Stage fplViewerStage = new Stage();
            fplViewerStage.setScene(fplViewerScene);
            fplViewerStage.show();
            fplViewerStage.setX(1500);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception while loading scheduled flight plan container {0}", e);
        }


        // updating controls
        dataSetField.setText(dataSetFile.getName());
        startTimeField.setText(Integer.toString(startTime));
        simulationDurationField.setText(Integer.toString(nbQuarters));
        outputFolderField.setText(outputDirectory);
        outputFormatChoiceB.setItems(FXCollections.observableList(Arrays.asList(ScenarioOutputFormat.values())));
        outputFormatChoiceB.getSelectionModel().select(ScenarioOutputFormat.XML);

        // adding listener
        startTimeField.textProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "start time changed {0} {1} {2}", new Object[]{obs, old, newV});
            try {
                startTime = Integer.parseInt(startTimeField.getText().trim());
            } catch (Exception e) {
                LOG.log(Level.INFO, "Exception while trying to retrieve start time {0}", e);
            }
        });
        simulationDurationField.textProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "simulation duration changed {0} {1} {2}", new Object[]{obs, old, newV});
            try {
                nbQuarters = Integer.parseInt(simulationDurationField.getText().trim());
            } catch (Exception e) {
                LOG.log(Level.INFO, "Exception while trying to retrieve simulation duration {0}", e);
            }
        });
        //
        dataSet = FlightPlanDataLoader.parseXML(dataSetFile);
        //
        createDebugFlows();

        // request focus on run button
        runLater(generateB::requestFocus);
        //
        TextFields.bindAutoCompletion(startTimeField, "4", "8", "12");

    }

    @FXML
    protected void onCreateNewFlowAction(ActionEvent event) {
        LOG.log(Level.INFO, "onCreateNewFlowAction {0}", event);
        // create new flow creator
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FLOW_CREATOR_FXML));
            fXMLLoader.load();
            Parent flowCreator = fXMLLoader.getRoot();
            FlowCreatorController controller = fXMLLoader.getController();
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(flowCreator);
            AnchorPane.setBottomAnchor(flowCreator, 0.0);
            AnchorPane.setTopAnchor(flowCreator, 0.0);
            AnchorPane.setRightAnchor(flowCreator, 0.0);
            AnchorPane.setLeftAnchor(flowCreator, 0.0);
            Tab tab = new Tab("New Flow", anchorPane);
            //todo use a method
            controller.addPropertyChangeListener(FlowCreatorController.CONFIG_CHANGED, e -> {
                tab.setText(controller.getDisplayName());
                trafficFlowListView.refresh();
            });
            controller.setFlightDataSet(dataSet);
            flowsTabPane.getTabs().add(tab);
            flowsTabPane.getSelectionModel().select(tab);
            trafficFlowListView.getItems().add(controller.getFlow());
            flowViewerContainerController.addFlow(controller.getFlow());
            scheduledFlightPlanContainerController.addFlow(controller.getFlow());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception while loading flow creator {0}", e);
        }

    }

    @FXML
    protected void onCreateSchedule(ActionEvent event) {
        LOG.log(Level.INFO, "onCreateSchedule {0}", event);
        // create schedule for each flow
        trafficFlowListView.getItems().forEach(Flow::createSchedule);
        //
        String[] myArray = {"this", "is", "a", "sentence"};
        String result = Arrays.stream(myArray)
                .reduce("", (a, b) -> a + b);
        System.err.println("!!! " + result);
    }


    @FXML
    protected void onGenerate(ActionEvent event) {
        LOG.log(Level.INFO, "onGenerate {0}", event);
        System.err.println("TOTO: generate");

//        System.err.println(" > configuring flows...");
//        System.err.println(" >> TODO");
//
//
//        System.err.println(" > parsing data set file... HUM>>> redo??");
//        FlightPlanDataSet dataSet = FlightPlanDataLoader.parseXML(dataSetFile);
//        System.err.println(" > populating flows...");
//        trafficFlowListView.getItems().forEach(flow -> flow.populate(dataSet));
//        System.err.println(" > generate flows...");
//        List<ScheduledFlightPlan> generatedFlightPlans = new LinkedList<>();
////        trafficFlowListView.getItems().forEach(flow->flow.generate(false).forEach(fpl-> System.err.println(flow.toString()+" :: "+fpl)));
//        trafficFlowListView.getItems().forEach(flow -> generatedFlightPlans.addAll(flow.generate(false)));
//        System.err.println(" > generating corresponding AFOs...");
//        List<ScheduledAFO> scheduledArrivals = generatedFlightPlans.stream().map(scheduledFlightPlan -> scheduledFlightPlan.generateAFO(0, dataSet.getControllers())).collect(Collectors.toList());
//        System.err.println(" > exporting brahms files");
//        //TODO include departures
//        ScenarioGeneratorUtils.generateScenarioFiles(dataSet.getNodes(), dataSet.getControllers(), scheduledArrivals, Collections.emptyList());
//        System.err.println(" > done");

        // for each flow
        trafficFlowListView.getItems().forEach(Flow::generateTraffic);
        System.err.println("done");
    }

    @FXML
    protected void onExportAction(ActionEvent event) {
        LOG.log(Level.INFO, "onExportAction {0}", event);
        switch (outputFormatChoiceB.getValue()) {
            case XML:
                ScenarioXMLExporter.exportScenarioConfiguration(trafficFlowListView.getItems(), new File(DEFAULT_OUTPUT_DIR_PATH + File.separator + DEFAULT_OUTPUT_FILE_NAME), true);
                break;
            case BRAHMS:
                exportBrahmsFiles();
                break;
            default:
                throw new UnsupportedOperationException("unsupported export type : " + outputFormatChoiceB.getValue());
        }
    }

    private void exportBrahmsFiles() {
//        trafficFlowListView.getItems().forEach(flow -> {
//            flow.generate();
//        });
//        List<ScheduledAFO> allAFOs =  new LinkedList<>();
//        allAFOs.addAll(trafficFlowListView.getItems().stream().map(Flow::getScheduledAFOs)));
//        Stream<ScheduledAFO> stream = Stream.empty();

        Object a = trafficFlowListView.getItems();
        System.err.println(a);

        Object b = trafficFlowListView.getItems().stream().map(flow -> flow.getScheduledAFOs().stream());
        System.err.println(b);

        List<ScheduledAFO> allAFOs = trafficFlowListView.getItems().stream().map(flow -> flow.getScheduledAFOs().stream()).flatMap(Function.identity()).collect(Collectors.toList());
//        trafficFlowListView.getItems().stream().map(Flow::getScheduledAFOs).flatMap(Function.identity());
//      trafficFlowListView.getItems().stream().map(Flow::getScheduledAFOs).reduce(stream,(a,b)-> Stream.concat(a.stream(),b.stream()));
        ScenarioGeneratorUtils.generateScenarioFiles(dataSet.getNodes(), dataSet.getControllers(), allAFOs);
    }


    private void createDebugFlows() {
        String arrivalRunway = "LGA22";
        String departureRunway = "LGA31";
        String westIdentifierPoint = "MARRC";
        String northIdentifierPoint = "IGN";
        String southIdentifierPoint = "SKIPY";
        //
        // TODO: factor code
        // default arrival flow
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FLOW_CREATOR_FXML));
            fXMLLoader.load();
            Parent flowCreator = fXMLLoader.getRoot();
            FlowCreatorController controller = fXMLLoader.getController();
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(flowCreator);
            AnchorPane.setBottomAnchor(flowCreator, 0.0);
            AnchorPane.setTopAnchor(flowCreator, 0.0);
            AnchorPane.setRightAnchor(flowCreator, 0.0);
            AnchorPane.setLeftAnchor(flowCreator, 0.0);
            Tab tab = new Tab("New Flow", anchorPane);
            //todo use a method
            controller.addPropertyChangeListener(FlowCreatorController.CONFIG_CHANGED, e -> {
                tab.setText(controller.getDisplayName());
                trafficFlowListView.refresh();
            });
            controller.setFlightDataSet(dataSet);
            controller.setFlowType(FlowType.ARRIVAL);
            controller.setRunwayName(arrivalRunway);
            controller.setNbQuarters(nbQuarters);
            controller.setDeliveryPoint("OMAAR");
            controller.createSubFlow("North", northIdentifierPoint, Color.MAGENTA);
            controller.createSubFlow("West", westIdentifierPoint, Color.ORANGERED);
            controller.createSubFlow("South", southIdentifierPoint, Color.CORNFLOWERBLUE);
            flowsTabPane.getTabs().add(tab);
            flowsTabPane.getSelectionModel().select(tab);
            trafficFlowListView.getItems().add(controller.getFlow());
            flowViewerContainerController.addFlow(controller.getFlow());
            scheduledFlightPlanContainerController.addFlow(controller.getFlow());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception while loading flow creator {0}", e);
        }
        // default departure flow
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FLOW_CREATOR_FXML));
            fXMLLoader.load();
            Parent flowCreator = fXMLLoader.getRoot();
            FlowCreatorController controller = fXMLLoader.getController();
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(flowCreator);
            AnchorPane.setBottomAnchor(flowCreator, 0.0);
            AnchorPane.setTopAnchor(flowCreator, 0.0);
            AnchorPane.setRightAnchor(flowCreator, 0.0);
            AnchorPane.setLeftAnchor(flowCreator, 0.0);
            Tab tab = new Tab("New Flow", anchorPane);
            //todo use a method
            controller.addPropertyChangeListener(FlowCreatorController.CONFIG_CHANGED, e -> {
                tab.setText(controller.getDisplayName());
                trafficFlowListView.refresh();
            });
            controller.setFlightDataSet(dataSet);
            controller.setRunwayName(departureRunway);
            controller.setDeliveryPoint(departureRunway);
            controller.setFlowType(FlowType.DEPARTURE);
            controller.setNbQuarters(nbQuarters);
            controller.createSubFlow("Dep", departureRunway, Color.CHOCOLATE);
            controller.setBaseRate(11);
            flowsTabPane.getTabs().add(tab);
            flowsTabPane.getSelectionModel().select(tab);
            trafficFlowListView.getItems().add(controller.getFlow());
            flowViewerContainerController.addFlow(controller.getFlow());
            scheduledFlightPlanContainerController.addFlow(controller.getFlow());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception while loading flow creator {0}", e);
        }

    }
}
