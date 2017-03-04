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

package gov.nasa.arc.atc.parametrized.brahms;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.algos.viewer.ConfigurationWindowController;
import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLoader;
import gov.nasa.arc.atc.scenarios.dsas.week2.run6.DSASWeek2Run6;
import gov.nasa.arc.atc.simulation.SimulationContext;
import javafx.fxml.Initializable;
import static javafx.application.Platform.runLater;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * @author ahamon
 *
 */
public class BrahmsInputConfiguratorFXController implements Initializable {

	private static final Logger LOG = Logger.getGlobal();

	private static final String NO_FILE_TEXT = "no file selected";
	private static final String NO_YET = "this method is not yet implemented";

	private final Image okImage = new Image(ConfigurationWindowController.class.getResourceAsStream("ok.png"));
	private final Image notOkImage = new Image(ConfigurationWindowController.class.getResourceAsStream("notok.png"));
	private final FileChooser.ExtensionFilter extFilterProperties = new FileChooser.ExtensionFilter("Configuration files (*.properties)", "*.properties");

	@FXML
	private TextField configurationFileField;
	@FXML
	private ImageView configFileCheck;
	@FXML
	private TextField airportFileField;
	@FXML
	private ImageView wpFileCheck;
	@FXML
	private TextField wpFileField;
	@FXML
	private TextField flightSegFileField;
	@FXML
	private ImageView flightSegFileCheck;
	@FXML
	private TextField scenarioFileField;
	@FXML
	private ImageView scenarioFileCheck;
	@FXML
	private ImageView departuresFileCheck;
	@FXML
	private TextField departuresFileField;
	@FXML
	private ImageView airportFileCheck;

	private final FileChooser fileChooser = new FileChooser();
	private File configurationFile;

	private Stage stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initDefaultConfiguration();
		checkConfiguration();
		runLater(() -> stage = (Stage) configurationFileField.getScene().getWindow());
	}

	@FXML
	public void onChangeConfigFileAction(ActionEvent event) {
		LOG.log(Level.INFO, "handle onChangeConfigFileAction on event {0}", event);

		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(extFilterProperties);
		File tmpConfig = fileChooser.showOpenDialog(stage);
		if (tmpConfig != null) {
			configurationFile = tmpConfig;
			SimulationConfigurationLoader.loadConfiguration(configurationFile);
		}
	}

	@FXML
	public void onChangeAirports(ActionEvent event) {
		LOG.log(Level.INFO, "handle onChangeAirports on event {0}", event);
		throw new UnsupportedOperationException(NO_YET);
	}

	@FXML
	public void onChangeDepartureQueue(ActionEvent event) {
		LOG.log(Level.INFO, "handle onChangeDepartureQueue on event {0}", event);
		throw new UnsupportedOperationException(NO_YET);
	}

	@FXML
	public void onChangeFlightSegments(ActionEvent event) {
		LOG.log(Level.INFO, "handle onChangeFlightSegments on event {0}", event);
		throw new UnsupportedOperationException(NO_YET);
	}

	@FXML
	public void onChangeScenario(ActionEvent event) {
		LOG.log(Level.INFO, "handle onChangeScenario on event {0}", event);
		throw new UnsupportedOperationException(NO_YET);
	}

	@FXML
	public void onChangeWaypoints(ActionEvent event) {
		LOG.log(Level.INFO, "handle onChangeWaypoints on event {0}", event);
		throw new UnsupportedOperationException(NO_YET);
	}

	SimulationContext getScenario() {
		return SimulationConfigurationLoader.getSimulationContext();
	}

	private void initDefaultConfiguration() {
		configurationFile = new File(DSASWeek2Run6.class.getResource("scenario.properties").getPath());
//		configurationFile = new File("/Documents/Code/mas_models/exampleModels/DSAS/brahms-src/gov/nasa/arc/atm/atmmodel/scenarios/generated/scenario.properties");
		// still the right way?
		SimulationConfigurationLoader.loadConfiguration(configurationFile);
	}

	/*
	 * Methods to check scenario configuration validity
	 * 
	 */

	/**
	 * 
	 * @return if the configuration file points towards files for each filed needed
	 */
	 boolean checkConfiguration() {
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
		return configOK && checkDepartureSetup();
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

}
