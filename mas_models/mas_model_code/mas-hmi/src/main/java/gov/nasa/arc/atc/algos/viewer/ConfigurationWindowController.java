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

package gov.nasa.arc.atc.algos.viewer;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.simulation.SimulationTask;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.ProgressDialog;

import gov.nasa.arc.atc.algos.DepartureArrivalAlgorithm;
import gov.nasa.arc.atc.algos.dsas.FixedDepartureTime;
import gov.nasa.arc.atc.algos.dsas.DSASConcept;
import gov.nasa.arc.atc.algos.dsas.NoScheduling;
import gov.nasa.arc.atc.algos.dsas.ParametricDSAS;
import gov.nasa.arc.atc.algos.dsas.StandardDepartureAlgo;
import gov.nasa.arc.atc.algos.viewer.reports.DSASReportViewer;
import gov.nasa.arc.atc.algos.viewer.reports.TSSReportViewer;
import gov.nasa.arc.atc.scenarios.dsas2.test1.Dsas2Test1;
import gov.nasa.arc.atc.scenarios.week1.run1.Week1Run1;
import gov.nasa.arc.atc.scenarios.week2.run6.Week2Run6;
import static javafx.application.Platform.runLater;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * 
 * @author ahamon
 *
 */
public class ConfigurationWindowController implements Initializable {

	private static final Logger LOG = Logger.getGlobal();
	private static final String NO_FILE_TEXT = "no file selected";
	private static final int DEFAULT_DURATION = 1000;
	//
	private final Image okImage = new Image(ConfigurationWindowController.class.getResourceAsStream("ok.png"));
	private final Image notOkImage = new Image(ConfigurationWindowController.class.getResourceAsStream("notok.png"));
	private final FileChooser fileChooser = new FileChooser();
	private final FileChooser.ExtensionFilter extFilterXML = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
	private final FileChooser.ExtensionFilter extFilterProperties = new FileChooser.ExtensionFilter("Configuration files (*.properties)", "*.properties");

	@FXML
	private ImageView configFileCheck;
	@FXML
	private ImageView airportFileCheck;
	@FXML
	private ImageView wpFileCheck;
	@FXML
	private ImageView scenarioFileCheck;
	@FXML
	private ImageView durationCheck;
	@FXML
	private ImageView flightSegFileCheck;
	@FXML
	private ImageView departuresFileCheck;
	@FXML
	private TextField configurationFileField;
	@FXML
	private TextField airportFileField;
	@FXML
	private TextField wpFileField;
	@FXML
	private TextField scenarioFileField;
	@FXML
	private TextField flightSegFileField;
	@FXML
	private TextField departuresFileField;
	@FXML
	private CheckBox tssCheckBox;
	@FXML
	private ComboBox<Class<? extends DepartureArrivalAlgorithm>> dSAScomboBox;
	@FXML
	private TextField durationField;
	@FXML
	private Button runButton;
	@FXML
	private Button cancelButton;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label exportWpXMLLabel;
	@FXML
	private Label exportSimuXMLLabel;
	@FXML
	private Label exportReportXMLLabel;
	@FXML
	private Button exportWpXMLButton;
	@FXML
	private Button exportSimuXMLButton;
	@FXML
	private Button exportReportXMLButton;
	@FXML
	private Button launchVisualizationButton;
	@FXML
	private Tab tssTab;
	@FXML
	private Tab dsasTab;

	//
	private Stage primaryStage;
	private ProgressDialog progDiag;
	//

	private TSSReportViewer tssReportViewer;
	private DSASReportViewer dsasReportViewer;
	//
	private File configurationFile;
	private double simulationDuration = -1;
	private boolean simulationAborted = false;
	private boolean simulationCompleted = false;
	//

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		LOG.log(Level.INFO, "Initializing ConfigurationWindowController with {0}, {1}", new Object[] { arg0, arg1 });
		//
		initDefaultConfiguration();
		durationField.textProperty().addListener((a1, a2, a3) -> {
			LOG.log(Level.FINE, "Duration text field changed {0}, {1} {2}", new Object[] { a1, a2, a3 });
			checkSimulationDuration();
		});
		tssReportViewer = new TSSReportViewer();
		tssTab.setContent(tssReportViewer.getNode());
		dsasReportViewer = new DSASReportViewer();
		dsasTab.setContent(dsasReportViewer.getNode());
		//
		runLater(this::checkConfiguration);
	}

	protected void setStage(Stage stage) {
		primaryStage = stage;
	}

	@FXML
	public void onRunAction(ActionEvent event) {
		LOG.log(Level.INFO, "handle onRunAction on event {0}", event);
//		simulationAborted = false;
//		simulationCompleted = false;
//		createSimulation();
//		LOG.log(Level.INFO, "Simulation created {0}", simulation);
//		cancelButton.setDisable(false);
//		progDiag = new ProgressDialog(simulation.getService());
//		progDiag.setTitle("Simulation progress");
//		progDiag.initOwner(primaryStage);
//		progDiag.setHeaderText("Running the simulation");
//		progDiag.initModality(Modality.NONE);
//		progDiag.setOnCloseRequest(evt -> runLater(() -> {
//			LOG.log(Level.WARNING, "handle setOnCloseRequest on event {0}", evt);
//			if (!simulationAborted) {
//				Notifications.create().title("Traffic Simulation").text("Simulation completed").showInformation();
//			} else {
//				Notifications.create().title("Traffic Simulation").text("Simulation aborted").showWarning();
//			}
//		}));
//		//
//		try {
//			LOG.log(Level.WARNING, "Starting simulation {0}", simulation);
//			simulation.runSimulation();
//		} catch (Exception e) {
//			LOG.log(Level.WARNING, "Exception while running simulation :: {0}", e);
//		}
	}

	// TODO: break down in several methods
	private void createSimulation() {
//		simulation = new JavaStandaloneSimulation();
//		simulation.setScenario(SimulationConfigurationLoader.getSimulationContext());
//		simulation.setSimulationNbSteps((int) simulationDuration);
//		// add algorithms
//		String departureRunwayName = SimulationConfigurationLoader.getDepartureRunway();
//		String arrivalRunwayName = SimulationConfigurationLoader.getArrivalRunway();
//		ATCNode departureR = null;
//		ATCNode arrivalR = null;
//		for (ATCNode depNode : SimulationConfigurationLoader.getSimulationContext().getGeography().getWaypoints()) {
//			if (departureRunwayName.equals(depNode.getName())) {
//				departureR = depNode;
//				break;
//			}
//		}
//		if (departureR == null) {
//			throw new IllegalStateException("Could not find departure runway: " + departureRunwayName);
//		}
//		for (ATCNode arrNode : SimulationConfigurationLoader.getSimulationContext().getGeography().getWaypoints()) {
//			if (arrivalRunwayName.equals(arrNode.getName())) {
//				arrivalR = arrNode;
//				break;
//			}
//		}
//		if (arrivalR == null) {
//			throw new IllegalStateException("Could not find arrival runway: " + arrivalRunwayName);
//		}
//		// part to do automatically
//		if (tssCheckBox.isSelected()) {
//			// TODO: ACK
//			TSS tss = new TSS(new DistanceSeparationFunction(SimulationConfigurationLoader.getSimulationContext().getGeography().getAirports().get(0)));
//			tss.initializeData(SimulationConfigurationLoader.getSimulationContext(), arrivalR);
//			simulation.addAlgorithm(tss);
//
//		}
//		DepartureArrivalAlgorithm algo;
//		try {
//			// hum... to improve
//			Class<? extends DepartureArrivalAlgorithm> algoClass = dSAScomboBox.getSelectionModel().getSelectedItem();
//			if (algoClass.equals(ParametricDSAS.class)) {
//				System.err.println(" found parameteric dsas !");
//				algo = AlgorithmFactory.createParameteredDSAS(SimulationConfigurationLoader.getSimulationContext().getGeography());
//			} else {
//				algo = dSAScomboBox.getSelectionModel().getSelectedItem().newInstance();
//			}
//			algo.initializeData(SimulationConfigurationLoader.getSimulationContext(), departureR, arrivalR);
//			simulation.addAlgorithm(algo);
//		} catch (InstantiationException | IllegalAccessException e) {
//			LOG.log(Level.SEVERE, "Could not instanciate {0} because: {1}", new Object[] { dSAScomboBox.getSelectionModel().getSelectedItem(), e });
//		}
//		simulation.addPropertyChangeListener(this::handleSimulationEvents);
//		//
//		// config reporters
//		// TODO link step duration
//		TSSReporter.logScenarioName(SimulationConfigurationLoader.getDirectory().getName());
//		DSASReporter.logScenarioName(SimulationConfigurationLoader.getDirectory().getName());
//		TSSReporter.logSimulationDuration((int) simulationDuration);
//		TSSReporter.logSimulationTimeIncrement(1);
//		DSASReporter.logSimulationDuration((int) simulationDuration);
//		DSASReporter.logSimulationTimeIncrement(1);

	}

	@FXML
	public void onCancelAction(ActionEvent event) {
		LOG.log(Level.INFO, "handle onCancelAction on event {0}", event);
//		simulationAborted = true;
//		simulation.stopSimulation();
	}

	/**
	 */
	@FXML
	public void onExportWpToXML(ActionEvent event) {
		LOG.log(Level.INFO, "handle onExportWpToXML on event {0}", event);
//		fileChooser.setTitle("Save Waypoints to XML format");
//		fileChooser.getExtensionFilters().clear();
//		fileChooser.getExtensionFilters().add(extFilterXML);
//		// Show save file dialog
//		File file = fileChooser.showSaveDialog(primaryStage);
//		if (file != null) {
//			boolean exportOk = ATCGeographyExporter.exportGeographyToXML(simulation.getSimulationContext().getGeography(), file);
//			if (exportOk) {
//				Notifications.create().title("Geography export to XML").text("Export completed").showInformation();
//			} else {
//				Notifications.create().title("Geography export to XML").text("Export failed").showError();
//			}
//		}
	}

	@FXML
	public void onExportSimuToXML(ActionEvent event) {
		LOG.log(Level.WARNING, "handle onExportSimuToXML on event {0}", event);
		fileChooser.setTitle("Save Simulation output to XML format");
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(extFilterXML);
		// Show save file dialog
		File file = fileChooser.showSaveDialog(primaryStage);
		if (file != null) {
			// boolean exportOk = StandaloneSimulationExporter.saveStandAloneSimulationRun(simulation, file);
			boolean exportOk = false;
			if (exportOk) {
				Notifications.create().title("Simulation export to XML").text("Export completed").showInformation();
			} else {
				Notifications.create().title("Simulation export to XML").text("Export failled").showError();
			}
		}
	}

	@FXML
	public void onLaunchVizu(ActionEvent event) {
		LOG.log(Level.WARNING, "handle onLaunchVizu on event {0}", event);
	}

	@FXML
	public void onChangeConfigFileAction(ActionEvent event) {
		LOG.log(Level.INFO, "handle onChangeConfigFileAction on event {0}", event);
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(extFilterProperties);
		File tmpConfig = fileChooser.showOpenDialog(primaryStage);
		if (tmpConfig != null) {
			configurationFile = tmpConfig;
			SimulationConfigurationLoader.loadConfiguration(configurationFile);
		}
	}

	@FXML
	public void onExportReportToXML(ActionEvent event) {
		LOG.log(Level.INFO, "handle onExportReportToXML on event {0}", event);
//		fileChooser.getExtensionFilters().clear();
//		fileChooser.getExtensionFilters().add(extFilterXML);
//		File reportFile = fileChooser.showSaveDialog(primaryStage);
//		if (reportFile != null) {
//			TSSReport tssReport = null;
//			if (tssCheckBox.isSelected()) {
////				tssReport = TSSReporter.exportReport();
//			}
//			DSASReport dsasReport = null;
//			if (dSAScomboBox.getSelectionModel().getSelectedItem().equals(FixedDepartureTime.class) || dSAScomboBox.getSelectionModel().getSelectedItem().equals(DSASConcept.class)) {
//				dsasReport = DSASReporter.exportReport();
//			}
//			ReportXMLExport.exportReports(reportFile, tssReport, dsasReport);
//		}

	}

	private void initDefaultConfiguration() {
		// configurationFile = new File(Test1.class.getResource("test1.properties").getPath());
		// configurationFile = new File(Test2.class.getResource("test2.properties").getPath());
		// configurationFile = new File(Test4.class.getResource("test4.properties").getPath());
		// configurationFile = new File(Test3.class.getResource("test3.properties").getPath());
		// configurationFile = new File(Scenario1.class.getResource("scenario1.properties").getPath());
		configurationFile = new File(Week1Run1.class.getResource("scenario.properties").getPath());
		configurationFile = new File(Dsas2Test1.class.getResource("scenario.properties").getPath());
		configurationFile = new File(Week2Run6.class.getResource("scenario.properties").getPath());

		durationField.setText(Integer.toString(DEFAULT_DURATION));
		simulationDuration = DEFAULT_DURATION;
		SimulationConfigurationLoader.loadConfiguration(configurationFile);
		//
		dSAScomboBox.setButtonCell(new DSASListCell());
		dSAScomboBox.setCellFactory(new Callback<ListView<Class<? extends DepartureArrivalAlgorithm>>, ListCell<Class<? extends DepartureArrivalAlgorithm>>>() {
			@Override
			public ListCell<Class<? extends DepartureArrivalAlgorithm>> call(ListView<Class<? extends DepartureArrivalAlgorithm>> p) {
				return new DSASListCell();
			}
		});
		@SuppressWarnings("unchecked")
		ObservableList<Class<? extends DepartureArrivalAlgorithm>> options = FXCollections.observableArrayList(NoScheduling.class, StandardDepartureAlgo.class, FixedDepartureTime.class, DSASConcept.class, ParametricDSAS.class);
		dSAScomboBox.setItems(options);
		dSAScomboBox.getSelectionModel().select(StandardDepartureAlgo.class);

	}

	private void checkSimulationDuration() {
		try {
			simulationDuration = Integer.parseInt(durationField.getText());
		} catch (Exception e) {
			LOG.log(Level.INFO, "Not a valid number for the simulation duration:: {0} -> {1}", new Object[] { durationField.getText(), e });
			simulationDuration = -1;
		}
		checkConfiguration();
	}

	private void checkConfiguration() {
		boolean configOK;
		// Configuration set up
		configOK = checkConfigurationFile();
		// Airport set up
		configOK = configOK && checkAirportSetup();
		// Waypoint set up
		configOK = configOK && checkWaypointsSetup();
		// Slots set up
		configOK = configOK && checkScenarioSetup();
		// Segments set up
		configOK = configOK && checkFlightSegmentsSetup();
		// departure set up
		configOK = configOK && checkDepartureSetup();
		// Simulation duration
		if (simulationDuration < 0) {
			configOK = false;
		}
		// set enable if configuration OK
		runButton.setDisable(!configOK);
	}

	private boolean checkConfigurationFile() {
		if (SimulationConfigurationLoader.getConfigurationFile() != null) {
			configurationFileField.setText(SimulationConfigurationLoader.getConfigurationFile().getAbsolutePath());
			if (SimulationConfigurationLoader.isConfigurationFileValid()) {
				configFileCheck.setImage(okImage);
				return true;
			} else {
				configFileCheck.setImage(notOkImage);
			}
		} else {
			configFileCheck.setImage(notOkImage);
			configurationFileField.setText(NO_FILE_TEXT);
		}
		return false;
	}

	private boolean checkAirportSetup() {
		if (SimulationConfigurationLoader.getAirportsFile() != null) {
			airportFileField.setText(SimulationConfigurationLoader.getAirportsFile().getAbsolutePath());
			if (SimulationConfigurationLoader.isAirportsFileValid()) {
				airportFileCheck.setImage(okImage);
				return true;
			} else {
				airportFileCheck.setImage(notOkImage);
			}
		} else {
			airportFileCheck.setImage(notOkImage);
			airportFileField.setText(NO_FILE_TEXT);
		}
		return false;
	}

	private boolean checkWaypointsSetup() {
		if (SimulationConfigurationLoader.getWaypointsFile() != null) {
			wpFileField.setText(SimulationConfigurationLoader.getWaypointsFile().getAbsolutePath());
			if (SimulationConfigurationLoader.isWaypointsFileValid()) {
				wpFileCheck.setImage(okImage);
				return true;
			} else {
				wpFileCheck.setImage(notOkImage);
			}
		} else {
			wpFileCheck.setImage(notOkImage);
			wpFileField.setText(NO_FILE_TEXT);
		}
		return false;
	}

	private boolean checkFlightSegmentsSetup() {
		if (SimulationConfigurationLoader.getFlightSegmentsFile() != null) {
			flightSegFileField.setText(SimulationConfigurationLoader.getFlightSegmentsFile().getAbsolutePath());
			if (SimulationConfigurationLoader.isFlightSegmentFileValid()) {
				flightSegFileCheck.setImage(okImage);
				return true;
			} else {
				flightSegFileCheck.setImage(notOkImage);
			}
		} else {
			flightSegFileCheck.setImage(notOkImage);
			flightSegFileField.setText(NO_FILE_TEXT);
		}
		return false;
	}

	private boolean checkScenarioSetup() {
		if (SimulationConfigurationLoader.getScenarioFile() != null) {
			scenarioFileField.setText(SimulationConfigurationLoader.getScenarioFile().getAbsolutePath());
			if (SimulationConfigurationLoader.isScenarioFileValid()) {
				scenarioFileCheck.setImage(okImage);
				return true;
			} else {
				scenarioFileCheck.setImage(notOkImage);
			}
		} else {
			scenarioFileCheck.setImage(notOkImage);
			scenarioFileField.setText(NO_FILE_TEXT);
		}
		return false;
	}

	private boolean checkDepartureSetup() {
		if (SimulationConfigurationLoader.getDepartureQueueFile() != null) {
			departuresFileField.setText(SimulationConfigurationLoader.getDepartureQueueFile().getAbsolutePath());
			if (SimulationConfigurationLoader.isDeparturesFileValid()) {
				departuresFileCheck.setImage(okImage);
				return true;
			} else {
				departuresFileCheck.setImage(notOkImage);
			}
		} else {
			departuresFileCheck.setImage(notOkImage);
			departuresFileField.setText(NO_FILE_TEXT);
		}
		return false;
	}

	private void handleSimulationEvents(PropertyChangeEvent event) {
		switch (event.getPropertyName()) {
		case SimulationTask.NEXT_SIMULATION_STEP:
			uptadeSimulationProgress(event);
			break;
		case SimulationTask.SIMULATION_COMPLETED:
			handleSimulationCompleted();
			break;
		default:
			break;
		}
	}

	private void uptadeSimulationProgress(PropertyChangeEvent event) {
		final double step = (int) event.getNewValue();
		progressBar.setProgress(step / simulationDuration);
	}

	private void handleSimulationCompleted() {
		runLater(() -> {
			runButton.setDisable(false);
			cancelButton.setDisable(true);
			simulationCompleted = true;
			progressBar.setProgress(1.0);
			enableExportAndVisu();
			displayReport();
		});
	}

	private void enableExportAndVisu() {
		boolean exportReady = simulationCompleted && (!simulationAborted);
		exportWpXMLLabel.setDisable(!exportReady);
		exportSimuXMLLabel.setDisable(!exportReady);
		exportReportXMLLabel.setDisable(!exportReady);
		exportWpXMLButton.setDisable(!exportReady);
		exportSimuXMLButton.setDisable(!exportReady);
		exportReportXMLButton.setDisable(!exportReady);
		launchVisualizationButton.setDisable(!exportReady);
	}

	private void displayReport() {
//		TSSReport tssReport = TSSReporter.exportReport();
//		LOG.log(Level.WARNING, "Simulation TSS report {0}", tssReport);
//		LOG.log(Level.WARNING, "Simulation total delay {0}", tssReport.getTotalDelayAdded());
//		LOG.log(Level.WARNING, "Simulation nb slot delayed {0}", tssReport.getNbSlotDelayed());
//		tssReportViewer.displayReport(tssReport);
//		DSASReport dsasReport = DSASReporter.exportReport();
//		dsasReportViewer.displayReport(dsasReport);
	}

	/*
	 * 
	 */
	private class DSASListCell extends ListCell<Class<? extends DepartureArrivalAlgorithm>> {
		@Override
		protected void updateItem(Class<? extends DepartureArrivalAlgorithm> item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null) {
				setText(item.getSimpleName());
			}
		}
	}

}
