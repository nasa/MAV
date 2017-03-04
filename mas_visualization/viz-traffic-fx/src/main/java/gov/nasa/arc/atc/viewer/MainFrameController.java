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

/*
  * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.arc.atc.viewer;

import gov.nasa.arc.atc.*;
import gov.nasa.arc.atc.algos.viewer.reports.ReportApp;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.log.FXLogger;
import gov.nasa.arc.atc.metrics.imagecreation.ImageCreationConfiguratorController;
import gov.nasa.arc.atc.parsing.xml.queue.ATCGeographyQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.utils.SimulationProperties;
import gov.nasa.arc.atc.viewer.flightplan.FlightPlanViewerApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.SegmentedButton;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static javafx.application.Platform.runLater;

/**
 * FXML Controller class
 *
 * @author hamon
 */
public class MainFrameController implements Initializable {

    @FXML
    private BorderPane mainPane;
    @FXML
    private SegmentedButton segmentedButton;

    private CenterPanel centerPanel;
    private AnchorPane leftPanel;
    private TabPane rightPanel;
    // config
    private Stage configStage = null;
    private ViewerConfigController configController;
    //
    private ToggleButton mainWindowB;
    private Map<ToggleButton, ManagedApp> apps;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //
        createTripleSlipPane();
        createSimulationPlayer();
        //
        mainWindowB = new ToggleButton("Main Window");
        segmentedButton.getButtons().add(mainWindowB);
        mainWindowB.setSelected(true);
        runLater(this::bindStageFocus);


        DisplayViewConfigurations.addPropertyChangeListener(this::handleStageChanges);
        apps = new HashMap<>();
        // TODO: add this
        //

        // TEMP HACK
        if (!SimulationResultsHolder.getContexts().isEmpty()) {
            runLater(() -> {
                SimulationProperties.parseProperties(MainResources.class.getResourceAsStream("config.properties"));
                File geographyFile = new File(MainResources.class.getResource("Geography_Week2.xml").getPath());
                ATCGeography geography = (ATCGeography) XMLMaster.requestParsing(geographyFile, new ATCGeographyQueueParser(), this);
                SimulationManager.setATCGeography(geography);
                // SimulationManager.setSimulationDataModel(mainDataModel);
            });
        } else {
            // runLater to avoid null pointer on scene when createConfigViewer
            runLater(this::openConfigViewer);
        }
    }

    /**
     * @param event {@link ActionEvent}
     */
    @FXML
    public void handleCloseAction(ActionEvent event) {
        FXLogger.log(Level.INFO, "handleCloseAction {0}", event);
        FXLogger.log(Level.WARNING, "closing app is NOT yet implented via this method");
        Runtime.getRuntime().exit(0);
    }

    /**
     * Opens the report viewer
     *
     * @param event {@link ActionEvent}
     */
    @FXML
    public void handleOpenViewerAction(ActionEvent event) {
        FXLogger.log(Level.INFO, "handleOpenViewerAction {0}", event);
        ReportApp reportApp = DisplayViewConfigurations.getReport();
        FXLogger.log(Level.INFO, "opening new instance of ReportApp {0}", reportApp);
    }

    @FXML
    public void handleViewSpecsAction(ActionEvent event) throws IOException, InterruptedException {
        FXLogger.log(Level.INFO, "handleViewSpecsAction {0}", event);
        DisplayViewConfigurations.openSM();
        FXLogger.log(Level.INFO, "opening new popup for SimulationSpecs {0}");
    }

    /**
     * Opens the {@link ViewerConfigController} to select files to visualize
     *
     * @param event {@link ActionEvent}
     */
    @FXML
    public void handleEditConfigurationAction(ActionEvent event) {
        FXLogger.log(Level.INFO, "handleEditConfigurationAction {0}", event);
        openConfigViewer();
    }

    /**
     * Opens the {@link ImageCreationConfiguratorController} to create snapshots of the simulation
     *
     * @param event {@link ActionEvent}
     */
    public void handleImageCreatorAction(ActionEvent event) {
        FXLogger.log(Level.INFO, "handleImageCreatorAction {0}", event);
        runLater(() -> DisplayViewConfigurations.getImageCreator().setDataModel(SimulationManager.getSimulationDataModel()));
    }

    /**
     * Opens the raw data viewer
     *
     * @param event {@link ActionEvent}
     */
    @FXML
    public void displayRawData(ActionEvent event) {
        FXLogger.log(Level.INFO, "displayRawData {0}", event);
        runLater(() -> {
            RawDataApp rawDataApp = DisplayViewConfigurations.getRawDataApp();
            rawDataApp.show();
            rawDataApp.displayDataModel(SimulationManager.getSimulationDataModel());
        });
    }

    /**
     * Opens the flight plan viewer
     *
     * @param event {@link ActionEvent}
     */
    @FXML
    public void displayFlightPlans(ActionEvent event) {
        FXLogger.log(Level.INFO, "displayFlightPlans {0}", event);
        FlightPlanViewerApp fplViewerApp = DisplayViewConfigurations.getFlightViewerApp();
        fplViewerApp.show();
        fplViewerApp.displayDataModel(SimulationManager.getSimulationDataModel());
    }


    /**
     * Opens the log file of errors
     *
     * @param event {@link ActionEvent} triggering the display of the error log view
     */
    @FXML
    void handleViewErrorLog(ActionEvent event) {
        FXLogger.log(Level.INFO, "handleViewErrorLog {0}", event);
        FXLogger.showConsole();
    }


    private void createSimulationPlayer() {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("Player.fxml"));
            fXMLLoader.load();
            Pane playerRoot = fXMLLoader.getRoot();
            mainPane.setBottom(playerRoot);
        } catch (IOException ex) {
            FXLogger.log(Level.SEVERE, "Error while creating simulation player: {0}", ex);
        }
    }

    private void createTripleSlipPane() {
        centerPanel = new CenterPanel();
        createLeftPanel();
        createRightPanel();
        mainPane.setLeft(leftPanel);
        mainPane.setCenter(centerPanel.getNode());
        mainPane.setRight(rightPanel);
    }

    private void createLeftPanel() {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("LeftPanel.fxml"));
            fXMLLoader.load();
            leftPanel = fXMLLoader.getRoot();
            LeftPanelController leftPanelController = fXMLLoader.getController();
            FXLogger.log(Level.FINE, "Left panel controller: {0}", leftPanelController);
        } catch (IOException ex) {
            FXLogger.log(Level.SEVERE, "Error while creating left panel: {0} ", ex.getMessage());
        }
    }

    private void createRightPanel() {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("RightPanel.fxml"));
            fXMLLoader.load();
            rightPanel = fXMLLoader.getRoot();
            RightPanelController rightPanelController = fXMLLoader.getController();
            rightPanelController.addPropertyChangeListener(this::handleRightPanelPropertyEvents);
            FXLogger.log(Level.FINE, "Right panel controller: {0}", rightPanelController);
        } catch (IOException ex) {
            FXLogger.log(Level.SEVERE, "Error while creating right panel: {0}", ex);
        }
    }

    private void bindStageFocus() {
        mainWindowB.getScene().getWindow().focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                mainWindowB.setSelected(true);
            } else {
                mainWindowB.setSelected(false);
            }
        });
    }

    private void openConfigViewer() {
        runLater(() -> {
            if (configStage == null) {
                createConfigViewer();
            }
            configController.showAndWait();
        });

    }

    private void createConfigViewer() {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("ViewerConfigurator.fxml"));
            fXMLLoader.load();
            Parent configContent = fXMLLoader.getRoot();
            configController = fXMLLoader.getController();
            FXLogger.log(Level.FINE, "ViewerConfigController : {0}", configController);
            configStage = new Stage();
            configStage.setScene(new Scene(configContent));
            configStage.setTitle("Configuration window");
            configStage.initModality(Modality.APPLICATION_MODAL);
            configStage.initOwner(mainPane.getScene().getWindow());
            configController.setStage(configStage);
        } catch (IOException ex) {
            FXLogger.log(Level.SEVERE, "Error while creating ViewerConfigController: {0}", ex);
            ex.printStackTrace();
        }
    }

    private void handleStageChanges(PropertyChangeEvent event) {
        if (DisplayViewConfigurations.NEW_STAGE_EVENT.equals(event.getPropertyName()) && event.getNewValue() instanceof ManagedApp) {
            ManagedApp app = (ManagedApp) event.getNewValue();
            ToggleButton tgB = new ToggleButton(app.getDisplayName());
            segmentedButton.getButtons().add(tgB);
            tgB.setSelected(true);
            apps.put(tgB, app);
            tgB.setOnAction(this::updateStageFocus);
            app.addPropertyChangeListener(e -> {
                if (ManagedAppEvents.STAGE_HIDDEN.equals(e.getPropertyName())) {
                    tgB.setSelected(false);
                } else if (ManagedAppEvents.STAGE_SHOWN.equals(e.getPropertyName())) {
                    tgB.setSelected(true);
                }
            });
        }
    }

    private void updateStageFocus(ActionEvent event) {
        FXLogger.log(Level.INFO, "updating stage focus on event {0}", event);
        apps.forEach((tgB, stage) -> {
            if (tgB.isSelected()) {
                stage.toFront();
            }
        });
    }

    private void handleRightPanelPropertyEvents(PropertyChangeEvent event) {
        try {
            @SuppressWarnings("unchecked")
            List<Coordinates> coordinates = (List<Coordinates>) event.getNewValue();
            centerPanel.handleZoomOnARegion(coordinates);
        } catch (Exception e) {
            FXLogger.log(Level.SEVERE, "Could not parse event new value in List<Coordinates> :: {0}", e);
        }
    }

}
