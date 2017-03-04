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

package gov.nasa.arc.atc.viewer;

import gov.nasa.arc.atc.GeographyFileType;
import gov.nasa.arc.atc.MainResources;
import gov.nasa.arc.atc.SimulationFileType;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.metrics.Metrics;
import gov.nasa.arc.atc.parsing.log.ThreadedAlternateLogParser;
import gov.nasa.arc.atc.parsing.trac.TracGeographyFileParser;
import gov.nasa.arc.atc.parsing.xml.queue.ATCGeographyQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.ScenarioParserQueued;
import gov.nasa.arc.atc.parsing.xml.queue.ScenarioXMLQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.utils.FileUtils;
import gov.nasa.arc.atc.utils.SimulationProperties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 */
public class ViewerConfigController implements Initializable {

    private static final String CONFIG_CHANGED_EVENT = "configChanged";

    private static final File DEFAULT_GEOGRAPHY_FILE = new File(MainResources.class.getResource("Geography_Week2.xml").getPath());
    private static final File DEFAULT_BELIEF_CONFIG_FILE = new File(MainResources.class.getResource("config.properties").getPath());
    private static final File DEFAULT_SIMULATION_FILE = new File(MainResources.class.getResource("log_small2A2D.txt").getPath());

    private static final Logger LOG = Logger.getGlobal();

    private static final String CHOOSE = "Choose ";
    private static final String SIMULATION_FILE_TEXT = " simulation file";
    private static final String PARSING_OF_TYPE = " parsing of type ";

    private static final boolean LOAD_THREADED = false;
    private static final boolean LOAD_QUEUED = true;

    @FXML
    private CheckBox ghostCheckBox;
    @FXML
    private TextField geographyFileField;
    @FXML
    private Button loadButton;
    @FXML
    private ProgressBar loadingProgressBar;
    @FXML
    private Label feedbackLabel;
    @FXML
    private ChoiceBox<GeographyFileType> geoFileTypeBox;
    @FXML
    private TextFlow console;
    @FXML
    private ComboBox<SimulationFileType> mainSimFileTypeBox;
    @FXML
    private TextField mainSimuFileField;
    @FXML
    private TextField mainConfigTextfield;
    @FXML
    private Label ghostTitleLabel;
    @FXML
    private Label ghostSimConfLabel;
    @FXML
    private Label ghostSimFileTypeLabel;
    @FXML
    private Label ghostSimFileLabel;
    @FXML
    private AnchorPane shadowConfigPane;

    /*
     * GHOST
     */
    @FXML
    private ComboBox<SimulationFileType> ghostSimFileTypeBox;
    @FXML
    private TextField ghostSimuFileField;
    @FXML
    private TextField ghostConfigTextfield;
    @FXML
    private Button ghostConfigFileButton;
    @FXML
    private Button ghostSimFileButton;

    /*
     * FILES
     */
    private File geographyFile;
    // main simu
    private File mainConfigFile;
    private File mainSimulationFile;
    // ghost simu
    private File ghostConfigFile;
    private File ghostSimulationFile;

    /*
     * DATA MODELS
     */
    private DataModel mainDataModel;
    private DataModel ghostDataModel;

    private Stage ownStage;
    private final FileChooser fileChooser = new FileChooser();
    private final Text debugText = new Text();
    private final StringBuilder debugMessage = new StringBuilder("\n");

    private PropertyChangeSupport propertyChangeSupport;
    //
    private BooleanProperty configHasChanged;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        configHasChanged = new SimpleBooleanProperty(true);
        //
        geographyFile = DEFAULT_GEOGRAPHY_FILE;
        mainConfigFile = DEFAULT_BELIEF_CONFIG_FILE;
        mainSimulationFile = DEFAULT_SIMULATION_FILE;
        ghostConfigFile = DEFAULT_BELIEF_CONFIG_FILE;
        //
        geographyFileField.setText(geographyFile.getPath());
        mainConfigTextfield.setText(mainConfigFile.getPath());
        mainSimuFileField.setText(mainSimulationFile.getPath());
        ghostConfigTextfield.setText(ghostConfigFile.getPath());
        //
        geoFileTypeBox.getItems().addAll(GeographyFileType.values());
        geoFileTypeBox.getSelectionModel().select(GeographyFileType.XML);
        //
        mainSimFileTypeBox.getItems().addAll(SimulationFileType.values());
        mainSimFileTypeBox.getSelectionModel().select(SimulationFileType.BRAHMS_SIM);
        ghostSimFileTypeBox.getItems().addAll(SimulationFileType.values());
        ghostSimFileTypeBox.getSelectionModel().select(SimulationFileType.BRAHMS_SIM);
        //
        runLater(() -> {
            console.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(4), new Insets(0))));
            debugText.setStyle("-fx-fill: CHARTREUSE;-fx-font-weight:normal;");
            debugText.setText(debugMessage.toString());
            console.getChildren().add(debugText);
        });
        //
        ghostCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> setGhostPaneDisabled(!ghostCheckBox.isSelected()));
        setGhostPaneDisabled(true);
        ghostCheckBox.setSelected(false);

        // TODO: change
        SimulationProperties.parseProperties(MainResources.class.getResourceAsStream("config.properties"));
    }

    /**
     * When clicked on the button to change the geography file
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onGeographyFileAction(ActionEvent event) {
        LOG.log(Level.INFO, " handle onGeographyFileAction {0}", new Object[]{event});
        String previousPath;
        if (geographyFile != null) {
            previousPath = geographyFile.getPath();
        } else {
            previousPath = "";
        }
        //
        FileChooser.ExtensionFilter ext = geoFileTypeBox.getSelectionModel().getSelectedItem().getExt();
        fileChooser.setTitle(CHOOSE + ext + SIMULATION_FILE_TEXT);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(ext);
        File tempFile = fileChooser.showOpenDialog(ownStage);
        if (tempFile != null) {
            geographyFile = tempFile;
            geographyFileField.setText(geographyFile.getPath());
            if (!previousPath.equals(geographyFile.getPath())) {
                configHasChanged.set(true);
            }
        }
        checkSelectedFiles();
    }

	/*
     * GHOST
	 * 
	 */

    /**
     * When clicked on the button to change the ghost simulation log file
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onGhostConfigurationFileAction(ActionEvent event) {
        LOG.log(Level.INFO, " handle onGhostConfigurationFileAction {0}", new Object[]{event});
        String previousPath;
        if (ghostConfigFile != null) {
            previousPath = ghostConfigFile.getPath();
        } else {
            previousPath = "";
        }
        fileChooser.setTitle(CHOOSE + FileUtils.EX_PROPERTIES + " configuration file");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(FileUtils.EX_PROPERTIES);
        File tempConfigFile = fileChooser.showOpenDialog(ownStage);
        if (tempConfigFile != null) {
            ghostConfigFile = tempConfigFile;
            ghostConfigTextfield.setText(ghostConfigFile.getPath());
            if (!previousPath.equals(ghostConfigFile.getPath())) {
                configHasChanged.set(true);
            }
        }
        checkSelectedFiles();
    }

    @FXML
    public void onGhostSimulationFileAction(ActionEvent event) {
        LOG.log(Level.INFO, " onGhostSimulationFileAction onSimulationFileAction {0}", new Object[]{event});
        String previousPath;
        if (ghostSimulationFile != null) {
            previousPath = ghostSimulationFile.getPath();
        } else {
            previousPath = "";
        }
        //
        FileChooser.ExtensionFilter ext = ghostSimFileTypeBox.getSelectionModel().getSelectedItem().getExt();
        fileChooser.setTitle(CHOOSE + ext + SIMULATION_FILE_TEXT);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(ext);
        File tempGhostFile = fileChooser.showOpenDialog(ownStage);
        if (tempGhostFile != null) {
            ghostSimulationFile = tempGhostFile;
            ghostSimuFileField.setText(ghostSimulationFile.getPath());
            if (!previousPath.equals(ghostSimulationFile.getPath())) {
                // simulationFileHasChanged = true;
                configHasChanged.set(true);
            }
        }
        checkSelectedFiles();
    }

    @FXML
    public void onMainConfigurationFileAction(ActionEvent event) {
        LOG.log(Level.INFO, " handle onMainConfigurationFileAction {0}", new Object[]{event});
        String previousPath;
        if (mainConfigFile != null) {
            previousPath = mainConfigFile.getPath();
        } else {
            previousPath = "";
        }
        fileChooser.setTitle(CHOOSE + FileUtils.EX_PROPERTIES + " configuration file");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(FileUtils.EX_PROPERTIES);
        File tempMainConfigFile = fileChooser.showOpenDialog(ownStage);
        if (tempMainConfigFile != null) {
            mainConfigFile = tempMainConfigFile;
            mainConfigTextfield.setText(mainConfigFile.getPath());
            if (!previousPath.equals(mainConfigFile.getPath())) {
                configHasChanged.set(true);
            }
        }
        checkSelectedFiles();
    }

    @FXML
    public void onMainSimulationFileAction(ActionEvent event) {
        LOG.log(Level.INFO, " handle onMainSimulationFileAction {0}", new Object[]{event});
        String previousPath;
        if (mainSimulationFile != null) {
            previousPath = mainSimulationFile.getPath();
        } else {
            previousPath = "";
        }
        //
        FileChooser.ExtensionFilter ext = mainSimFileTypeBox.getSelectionModel().getSelectedItem().getExt();
        fileChooser.setTitle(CHOOSE + ext + SIMULATION_FILE_TEXT);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(ext);
        File tempMainFile = fileChooser.showOpenDialog(ownStage);
        if (tempMainFile != null) {
            mainSimulationFile = tempMainFile;
            mainSimuFileField.setText(mainSimulationFile.getPath());
            if (!previousPath.equals(mainSimulationFile.getPath())) {
                // simulationFileHasChanged = true;
                configHasChanged.set(true);
            }
        }
        checkSelectedFiles();
    }

	/*
     * LOADING
	 */

    @FXML
    public void onCancelAction(ActionEvent event) {
        LOG.log(Level.INFO, " handle onCancelAction {0}", new Object[]{event});
        hide();
    }

    @FXML
    public void onLoadAction(ActionEvent event) {
        LOG.log(Level.INFO, "handle onLoadAction {0}", new Object[]{event});
        mainDataModel = null;
        ghostDataModel = null;
        loadChangedFiles();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public File getGeographyFile() {
        return geographyFile;
    }

    public File getSimulationFile() {
        return mainSimulationFile;
    }

    protected void setStage(Stage stage) {
        ownStage = stage;
    }


    void showAndWait() {
        //Temporary way
        //TODO: design proper mechanism
        if (AppLauncher.geographyFile != null && AppLauncher.geographyFile.exists()) {
            geographyFile = AppLauncher.geographyFile;
            mainSimulationFile = AppLauncher.simulationFile;
            mainConfigFile = AppLauncher.simulationConfigurationFile;
            if (geographyFile.getPath().endsWith(".xml")) {
                geoFileTypeBox.getSelectionModel().select(GeographyFileType.XML);
            } else {
                geoFileTypeBox.getSelectionModel().select(GeographyFileType.MACS_DATA);

            }
            geographyFileField.setText(geographyFile.getPath());

            // main simulation file
            if (mainSimulationFile.getPath().endsWith(".xml")) {
                mainSimFileTypeBox.getSelectionModel().select(SimulationFileType.XML);
            } else {
                mainSimFileTypeBox.getSelectionModel().select(SimulationFileType.BRAHMS_SIM);
            }

            mainSimuFileField.setText(mainSimulationFile.getPath());

            mainConfigTextfield.setText(mainConfigFile.getPath());
            // parse config file
            SimulationProperties.parseProperties(mainConfigFile);
        }
        ownStage.showAndWait();
    }

    private void hide() {
        runLater(() -> {
            loadingProgressBar.setProgress(0.0);
            ownStage.hide();
        });
    }

	/*
     * PARSING METHODS
	 */

    private void loadChangedFiles() {
        if (LOAD_THREADED) {
            throw new UnsupportedOperationException("Threaded parsing is not implemented");
        } else if (LOAD_QUEUED) {
            parseGeographyFile();
            // Parsing the simulation log
            parseMainSimulationLog();
        } else {
            hide();
            if (configHasChanged.get()) {
                propertyChangeSupport.firePropertyChange(CONFIG_CHANGED_EVENT, null, configHasChanged);
                configHasChanged.set(false);
            }
        }
    }

    private void handleParsingPropertyChange(PropertyChangeEvent event) {
        runLater(() -> updateParsingProgress(event));
    }

    private void updateParsingProgress(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                loadingProgressBar.setProgress(-1);
                feedbackLabel.setText("Opening file: " + event.getNewValue() + "...");
                addDebugLine("Opening file: " + event.getNewValue() + "...");
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                loadingProgressBar.setProgress((double) event.getNewValue());
                feedbackLabel.setText("Parsing file...");
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                addDebugLine(" !! PARSING FAILED AT LINES ::");
                try {
                    @SuppressWarnings("unchecked")
                    List<String> errorLines = (List<String>) event.getNewValue();
                    for (String s : errorLines) {
                        addDebugLine(s);
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Exception while retreiving error message {0}", e);
                }
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                loadingProgressBar.setProgress((double) event.getNewValue());
                feedbackLabel.setText("Creating agents...");
                break;
            // TODO add processing update
            // case ThreadedAlternateLogParser.LOADING_DATA_MODEL:
            // loadingProgressBar.setProgress((double) event.getNewValue());
            // feedbackLabel.setText("Loading data model...");
            // addDebugLine("Loading data model...");
            // break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                handleDataModelParsed(event);
                break;
            default:
                break;
        }
    }

    private void handleDataModelParsed(PropertyChangeEvent event) {
        File modelFile = (File) event.getOldValue();
        if (modelFile.getName().equals(mainSimulationFile.getName()) && mainDataModel == null) {
            mainDataModel = (DataModel) event.getNewValue();

            if (ghostCheckBox.isSelected()) {
                parseGhostSimulationLog();
            } else {
                //TODO progress and update on this one
                Metrics.getSimulationCalculation(mainDataModel);
                SimulationManager.setSimulationDataModel(mainDataModel);
                hide();
            }
        } else if (modelFile.getName().equals(ghostSimulationFile.getName())) {
            ghostDataModel = (DataModel) event.getNewValue();
            mainDataModel.setGhostModel(ghostDataModel);
            Metrics.getSimulationCalculation(mainDataModel);
            Metrics.getSimulationCalculation(ghostDataModel);
            //TODO progress and update on this one
            SimulationManager.setSimulationDataModel(mainDataModel);
            hide();
        } else {
            throw new IllegalStateException("unknonw file parsed:: " + modelFile);
        }

    }

    private void parseGeographyFile() {
        ATCGeography geography;
        switch (geoFileTypeBox.getSelectionModel().getSelectedItem()) {
            case XML:
                geography = (ATCGeography) XMLMaster.requestParsing(geographyFile, new ATCGeographyQueueParser(), this);
                break;
            case MACS_DATA:
                geography = TracGeographyFileParser.parseTracFile(geographyFile);
                break;
            default:
                throw new UnsupportedOperationException(PARSING_OF_TYPE + geoFileTypeBox.getSelectionModel().getSelectedItem());
        }
        LOG.log(Level.WARNING, " ATCGeography parsed = {0}", new Object[]{geography});
        SimulationManager.setATCGeography(geography);
    }

    private void parseMainSimulationLog() {
        addDebugLine("parsing main simulation model");
        switch (mainSimFileTypeBox.getSelectionModel().getSelectedItem()) {
            case XML:
//			DataModel dataModel = (DataModel) XMLMaster.requestParsing(mainSimulationFile, new ScenarioParserQueued(), this);
                DataModel dataModel = (DataModel) XMLMaster.requestParsing(mainSimulationFile, new ScenarioXMLQueueParser(), this);
                LOG.log(Level.WARNING, " XML DataModel parsed= {0}", new Object[]{dataModel});
                SimulationManager.setSimulationDataModel(dataModel);
                hide();
                break;
            case BRAHMS_SIM:
                ThreadedAlternateLogParser threadedAlternateLogParser = new ThreadedAlternateLogParser(mainSimulationFile, mainConfigFile, this::handleParsingPropertyChange);
                threadedAlternateLogParser.start();
                break;
            default:
                throw new UnsupportedOperationException(PARSING_OF_TYPE + mainSimFileTypeBox.getSelectionModel().getSelectedItem());
        }
    }

    private void parseGhostSimulationLog() {
        addDebugLine("parsing ghost model");
        switch (ghostSimFileTypeBox.getSelectionModel().getSelectedItem()) {
            case XML:
                ghostDataModel = (DataModel) XMLMaster.requestParsing(mainSimulationFile, new ScenarioParserQueued(), this);
                LOG.log(Level.WARNING, " XML Ghost DataModel parsed= {0}", new Object[]{ghostDataModel});
                mainDataModel.setGhostModel(ghostDataModel);
                SimulationManager.setSimulationDataModel(mainDataModel);
                hide();
                break;
            case BRAHMS_SIM:
                ThreadedAlternateLogParser threadedAlternateLogParser = new ThreadedAlternateLogParser(ghostSimulationFile, ghostConfigFile, this::handleParsingPropertyChange);
                threadedAlternateLogParser.start();
                break;
            default:
                throw new UnsupportedOperationException(PARSING_OF_TYPE + ghostSimFileTypeBox.getSelectionModel().getSelectedItem());
        }
    }

    private void addDebugLine(String newLine) {
        debugMessage.append("  > ").append(newLine).append("\n");
        debugText.setText(debugMessage.toString());
    }

    private void setGhostPaneDisabled(boolean disabled) {
        ghostSimFileTypeBox.setDisable(disabled);
        ghostSimuFileField.setDisable(disabled);
        ghostConfigTextfield.setDisable(disabled);
        ghostSimFileButton.setDisable(disabled);
        ghostConfigFileButton.setDisable(disabled);
        ghostTitleLabel.setDisable(disabled);
        ghostSimConfLabel.setDisable(disabled);
        ghostSimFileTypeLabel.setDisable(disabled);
        ghostSimFileLabel.setDisable(disabled);
        checkSelectedFiles();
    }

    private void checkSelectedFiles() {
        runLater(() -> {
            boolean mainSimuOK = mainConfigFile != null && mainSimulationFile != null;
            if (!ghostCheckBox.isSelected()) {
                loadButton.setDisable(!mainSimuOK);
                return;
            }
            boolean ghostSimuOK = ghostConfigFile != null && ghostSimulationFile != null;
            loadButton.setDisable(!mainSimuOK || !ghostSimuOK);
        });
    }

}
