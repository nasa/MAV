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

package gov.nasa.arc.atc.parametrized;

import gov.nasa.arc.atc.algos.*;
import gov.nasa.arc.atc.algos.dsas.creator.DSASCreator;
import gov.nasa.arc.atc.algos.tss.creator.TSSCreator;
import gov.nasa.arc.atc.algos.viewer.reports.ReportAppLauncher;
import gov.nasa.arc.atc.export.ReportXMLExport;
import gov.nasa.arc.atc.export.StandaloneSimulationExporter;
import gov.nasa.arc.atc.parametrized.brahms.BrahmsScenarioProvider;
import gov.nasa.arc.atc.parametrized.empty.EmptyScenarioProvider;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.simulation.SimulationTask;
import gov.nasa.arc.atc.utils.SimulatedTrajectoryFactory;
import gov.nasa.arc.atc.viewer.AppLauncher;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.controlsfx.control.Notifications;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class ConfiguratorFXController implements Initializable {

	/*
     * TODO: change API to be able to re run simulations disable all controls. (except cancel) when running the simulation
	 */

    private static final Logger LOG = Logger.getGlobal();

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.###");

    private static final int DEFAULT_NUMBER_STEPS = 300;
    private static final int DEFAULT_STEP_DURATION = 1;

    /*
     * Scenario
     */
    @FXML
    private ComboBox<ScenarioProvider> scenarioTypeComboB;

    /*
     * Algorithms
     */
    @FXML
    private ListView<AlgorithmCreator> algorithmCreatorList;
    @FXML
    private Button createAlgoButton;
    @FXML
    private ListView<Algorithm> algoSequenceListView;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;
    @FXML
    private Button removeButton;

    /*
     * Configuration
     */
    @FXML
    private VBox scenarioConfigVBox;
    @FXML
    private TextField nbStepsField;
    @FXML
    private ComboBox<SimulationTaskCreator> simulatorsComboB;

    /*
     * Execution
     */
    @FXML
    private Button runButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label executionLabel;
    @FXML
    private ProgressBar progressBar;

    /*
     * Export
     */
    @FXML
    private Button sim2XMLButton;
    @FXML
    private Button exportReportButton;

    /*
     * Visualization
     */
    @FXML
    private Button launchVisualizationButton;
    @FXML
    private Button launchReportVizButton;

    /*
     * Console
     */
    @FXML
    private ScrollPane consoleScrollPane;
    @FXML
    private TextFlow consoleTextFlow;

    /*
     * Dialog
     */
    private final Dialog<Algorithm> dialog = new Dialog<>();
    private final ButtonType buttonTypeOk = new ButtonType("Create", ButtonData.OK_DONE);
    private final ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

    /*
     * File chooser
     */
    private final FileChooser fileChooser = new FileChooser();
    private final FileChooser.ExtensionFilter extFilterXML = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");

    private SimulationTaskCreator simulationTaskCreator;

    private Service<Void> simulationService;
    private SimulationTask currentTask;

    private ScenarioProvider scenarioProvider;
    private AlgorithmCreator algorithmCreator;

    private Algorithm selectedAlgorithm;

    private int nbSteps = DEFAULT_NUMBER_STEPS;
    private final int stepDuration = DEFAULT_STEP_DURATION;

    private SimulationContext simulationContext;

    private boolean simulationReady = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.getDialogPane().lookupButton(buttonTypeOk).setDisable(true);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return algorithmCreator.createAlgorithm();
            }
            return null;
        });
        //
        simulationService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                if (simulationReady) {
                    currentTask = simulationTaskCreator.createSimulationTask(simulationContext, nbSteps, stepDuration, algoSequenceListView.getItems());
                    currentTask.addPropertyChangeListener(ConfiguratorFXController.this::handleSimulationEvents);
                    return currentTask;
                }
                return null;
            }
        };
        // init methods
        initializeScenarioProviders();
        initializeSimulatorInvokers();
        initializeAlgorithmCreators();
        //
        nbStepsField.setText(Integer.toString(nbSteps));
        nbStepsField.textProperty().addListener((a1, a2, a3) -> {
            LOG.log(Level.FINE, "nbSteps text field changed {0}, {1} {2}", new Object[]{a1, a2, a3});
            checkOverallConfiguration();
        });
        checkOverallConfiguration();
    }

    /**
     * @param event the {@link ActionEvent} triggering the opening of the dialog responsible to create the selected algorithm
     */
    @FXML
    public void onCreateAlgorithm(ActionEvent event) {
        LOG.log(Level.INFO, "algorithm creation on event {0}", event);
        algorithmCreator.addConfigurationPropertyListener(this::algoConfigOkListener);
        Optional<Algorithm> algorithm = dialog.showAndWait();
        if (algorithm.isPresent()) {
            algoSequenceListView.getItems().add(algorithm.get());
        }
    }

    /**
     * @param event the event triggering the removal of the selected algorithm from the list of applied algorithms
     */
    @FXML
    public void onRemoveAlgorithmAction(ActionEvent event) {
        LOG.log(Level.INFO, "onRemoveAlgorithmAction on event {0}", event);
        algoSequenceListView.getItems().remove(algoSequenceListView.getSelectionModel().getSelectedItem());
    }

    /**
     * @param event the {@link ActionEvent} triggering the start of the simulation
     */
    @FXML
    public void onRunSimulationAction(ActionEvent event) {
        LOG.log(Level.INFO, "running the simulation on event {0}", event);
        // disabling button so users cannot attempt rerun while simulation in progress
        runButton.setDisable(true);
        // Mono threaded and without feedback at the moment!!!
        if (!simulationService.isRunning()) {
            simulationService.reset();
            simulationService.start();
        } else {
            LOG.log(Level.WARNING, "!!! service is already running");
        }
    }

    /**
     * @param event the {@link ActionEvent} triggering the simulation export to xml file
     */
    @FXML
    public void onExportSimuToXML(ActionEvent event) {
        LOG.log(Level.WARNING, "handle onExportSimuToXML on event {0}", event);
        fileChooser.setTitle("Save Simulation output to XML format");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(extFilterXML);
        // Show save file dialog
        File file = fileChooser.showSaveDialog(sim2XMLButton.getScene().getWindow());
        if (file != null) {
            boolean exportOk = StandaloneSimulationExporter.saveStandAloneSimulationRun(currentTask.getContext(), file, currentTask.getNbSteps(), currentTask.getStepDuration());
            if (exportOk) {
                Notifications.create().title("Simulation export to XML").text("Export completed").showInformation();
            } else {
                Notifications.create().title("Simulation export to XML").text("Export failed").showError();
            }
        }
    }

    /**
     * @param event the {@link ActionEvent} triggering the reports export
     */
    @FXML
    public void onExportReport(ActionEvent event) {
        // can we find a way to factor with previous method?
        LOG.log(Level.WARNING, "handle onExportReport on event {0}", event);
        fileChooser.setTitle("Save Algorithm reports to XML format");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(extFilterXML);
        File file = fileChooser.showSaveDialog(exportReportButton.getScene().getWindow());
        if (file != null) {
            boolean exportOk = ReportXMLExport.exportReports(file);
            // (currentTask.getContext(), file, currentTask.getNbSteps(), currentTask.getStepDuration());
            if (exportOk) {
                Notifications.create().title("Simulation export to XML").text("Export completed").showInformation();
            } else {
                Notifications.create().title("Simulation export to XML").text("Export failed").showError();
            }
        }
    }

    /**
     * @param event the {@link ActionEvent} triggering the display of the traffic visualization
     */
    @FXML
    public void onLaunchVisualizationButton(ActionEvent event) {
        LOG.log(Level.WARNING, "handle onLaunchVisualizationButton on event {0}", event);
        AppLauncher.runFromExternalApplication();
    }

    /**
     * @param event the {@link ActionEvent} triggering the display of the report viewer
     */
    @FXML
    public void onLaunchReportAction(ActionEvent event) {
        LOG.log(Level.WARNING, "handle onLaunchReportAction on event {0}", event);
        ReportAppLauncher.runFromExternalApplication();

    }

	/*
     * Initialization methods
	 */

    private void initializeScenarioProviders() {

        // alternate way to service providers...
        // URL[] urls = new URL[0];
        // URLClassLoader ucl = new URLClassLoader(urls);
        //
        // ServiceLoader<ScenarioProvider> sl = ServiceLoader.load(ScenarioProvider.class, ucl);
        // Iterator<ScenarioProvider> apit = sl.iterator();
        // while (apit.hasNext()) {
        // System.out.println(apit.next().getDisplayName());
        // }
        // System.out.println("done");

        // TODO fix lookup not working
        // List<? extends ScenarioProvider> scenarioProviders = new ArrayList<>(Lookup.getDefault().lookupAll(ScenarioProvider.class));
        // scenarioTypeChoiceB.getItems().setAll(scenarioProviders);
        // TEMP
        scenarioTypeComboB.getItems().add(new BrahmsScenarioProvider());
        scenarioTypeComboB.getItems().add(new EmptyScenarioProvider());
        // END TEMP
        scenarioTypeComboB.setButtonCell(new ScenarioProviderListCell());
        scenarioTypeComboB.setCellFactory(p -> {
            LOG.log(Level.INFO, "setting scenarioTypeComboB cell factory for {0}", p);
            return new ScenarioProviderListCell();
        });
        scenarioTypeComboB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            scenarioConfigVBox.getChildren().clear();
            if (newValue != null) {
                scenarioProvider = newValue;
                scenarioConfigVBox.getChildren().add(scenarioProvider.getNode());
                simulationContext = scenarioProvider.createScenario();
                checkOverallConfiguration();
            }
        });

    }

    private void initializeAlgorithmCreators() {
        // TODO: same: use lookup provider
        algorithmCreatorList.getItems().add(new TSSCreator());
        algorithmCreatorList.getItems().add(new DSASCreator());
        algorithmCreatorList.setCellFactory(p -> {
            LOG.log(Level.INFO, "setting cell factory for {0}", p);
            return new AlgorithmCreatorListCell();
        });
        algorithmCreatorList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (algorithmCreator != null) {
                algorithmCreator.removeConfigurationPropertyListener(this::algoConfigOkListener);
            }
            if (newValue != null) {
                algorithmCreator = newValue;
                algorithmCreator.setScenario(scenarioProvider.createScenario());
                dialog.setTitle(algorithmCreator.getClass().getSimpleName());
                dialog.getDialogPane().setContent(algorithmCreator.getNode());
                dialog.getDialogPane().setHeaderText(algorithmCreator.getHelpMessage());
                //
                createAlgoButton.setDisable(false);
            } else {
                createAlgoButton.setDisable(true);
            }
        });

        algoSequenceListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            removeButton.setDisable(newValue == null);
            if (newValue != null) {

            }
        });
    }

    private void initializeSimulatorInvokers() {
        // TODO: same: use lookup provider
        simulatorsComboB.getItems().add(new StandaloneSimulationTaskCreator());
        simulatorsComboB.setButtonCell(new SimulationTaskCreatorListCell());
        simulatorsComboB.setCellFactory(p -> {
            LOG.log(Level.INFO, "setting cell factory for {0}", p);
            return new SimulationTaskCreatorListCell();
        });
        simulatorsComboB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                simulationTaskCreator = newValue;
            }
            checkOverallConfiguration();
        });
    }

    private void updateAlgosControls() {
        boolean listDisabled = scenarioProvider == null;
        algorithmCreatorList.setDisable(listDisabled);
        algoSequenceListView.setDisable(listDisabled);
        createAlgoButton.setDisable(listDisabled || algorithmCreator == null);
        boolean algoEditDisabled = listDisabled || selectedAlgorithm == null;
        upButton.setDisable(algoEditDisabled);
        downButton.setDisable(algoEditDisabled);
        removeButton.setDisable(algoEditDisabled);
    }

	/*
	 * Check methods
	 */

    private boolean checkOverallConfiguration() {
        boolean isConfOK = scenarioProvider != null && scenarioProvider.isConfigurationOK();
        isConfOK = simulationTaskCreator != null && isConfOK;
        isConfOK = isConfOK && checkSimulationDuration();
        runButton.setDisable(!isConfOK);
        updateAlgosControls();
        simulationReady = isConfOK;
        return isConfOK;
    }

    private boolean checkSimulationDuration() {
        try {
            nbSteps = Integer.parseInt(nbStepsField.getText());
        } catch (Exception e) {
            LOG.log(Level.INFO, "Not a valid number for the simulation duration:: {0} -> {1}", new Object[]{nbStepsField.getText(), e});
            nbSteps = -1;
            return false;
        }
        return true;
    }

	/*
	 * Update methods on simulation progress
	 */

    private void handleSimulationEvents(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case SimulationTask.NEXT_SIMULATION_STEP:
                updateSimulationProgress(event);
                break;
            case SimulationTask.SIMULATION_COMPLETED:
                handleSimulationCompleted();
                break;
            case SimulatedTrajectoryFactory.PROGRESS_UPDATE:
                updateTrajectoryCalculationProgress(event);
                break;
            default:
                break;
        }
    }

    private void updateSimulationProgress(PropertyChangeEvent event) {
        // TODO pull it out
        Platform.runLater(() -> {
            String message = "Simulation in progress t=" + event.getNewValue() + "/" + nbSteps;
            executionLabel.setText(message);
            final double step = (int) event.getNewValue();
            progressBar.setProgress(step / nbSteps);
            printInFxConsole(message);
        });
    }

    private void updateTrajectoryCalculationProgress(PropertyChangeEvent event) {
        // TODO pull it out
        Platform.runLater(() -> {
            // could be simplified if formatter is changed
            String message = "Calculating trajectories: " + DECIMAL_FORMAT.format(100 * ((double) event.getNewValue())) + "%";
            executionLabel.setText(message);
            final double percentage = (double) event.getNewValue();
            progressBar.setProgress(percentage);
            printInFxConsole(message);
        });
    }

    private void handleSimulationCompleted() {
        Platform.runLater(() -> {
            runButton.setDisable(false);
            cancelButton.setDisable(true);
            Notifications.create().title("Simulation completed").text("Simulation completed").showInformation();
            // simulationCompleted = true;
            progressBar.setProgress(1.0);
            enableExportAndVisualization();
        });
    }

    private void algoConfigOkListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        LOG.log(Level.FINE, "configOkListener {0} {1} {2}", new Object[]{observable, oldValue, newValue});
        dialog.getDialogPane().lookupButton(buttonTypeOk).setDisable(!newValue);
    }

    private void enableExportAndVisualization() {
        sim2XMLButton.setDisable(false);
        exportReportButton.setDisable(false);
        launchVisualizationButton.setDisable(false);
        launchReportVizButton.setDisable(false);
    }

    /**
     * MUST be called within the JavaFX thread
     *
     * @param message the message to be displayed
     */
    private void printInFxConsole(String message) {
        final Text consoleMessage = new Text(message + "\n");
        consoleMessage.setStyle("-fx-fill: CHARTREUSE;-fx-font-weight:normal;");
        consoleTextFlow.getChildren().add(consoleMessage);
        consoleScrollPane.setVvalue(consoleScrollPane.getVmax());
    }

	/*
	 * Rendering class, to be moved in a while and factor the code in one unique class
	 */

    private class ScenarioProviderListCell extends ListCell<ScenarioProvider> {
        @Override
        protected void updateItem(ScenarioProvider item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getDisplayName());
            }
        }
    }

    private class SimulationTaskCreatorListCell extends ListCell<SimulationTaskCreator> {
        @Override
        protected void updateItem(SimulationTaskCreator item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getClass().getSimpleName());
            }
        }
    }

    private class AlgorithmCreatorListCell extends ListCell<AlgorithmCreator> {
        @Override
        protected void updateItem(AlgorithmCreator item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getClass().getSimpleName());
            }
        }
    }

    // to be removed
    protected class SimulatorInvokerListCell extends ListCell<SimulatorInvoker> {
        @Override
        protected void updateItem(SimulatorInvoker item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getClass().getSimpleName());
            }
        }
    }
}
