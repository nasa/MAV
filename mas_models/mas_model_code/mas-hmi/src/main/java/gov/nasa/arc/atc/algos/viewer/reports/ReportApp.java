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

package gov.nasa.arc.atc.algos.viewer.reports;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLauncher;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * 
 * @author ahamon
 *
 */
public class ReportApp {

	private static final Logger LOG = Logger.getGlobal();

	private Stage stage;
	private Scene scene;

	private StandaloneReportViewerController viewerController;

	public ReportApp(Object... params) {
		// in case JavaFX platform is not set up yet
		JFXPanel initFxPanel = new JFXPanel();
		LOG.log(Level.INFO, "JavaFX panel created , thrown {0}", initFxPanel);
		Platform.runLater(() -> createApp(params));
	}

	private void createApp(Object... params) {
		stage = new Stage();
		// temp
		Parent root = new Pane();
		try {
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("StandaloneReportViewer.fxml"));
			fXMLLoader.load();
			root = fXMLLoader.getRoot();
			viewerController = fXMLLoader.getController();
			viewerController.setOwner(stage);
			viewerController.setReports(params);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Error while creating ReportApp : {0}", ex);
		}
		scene = new Scene(root, 900, 900);
		stage.setScene(scene);
		stage.getIcons().add(new Image(SimulationConfigurationLauncher.class.getResourceAsStream("NASA_logo.png")));
		stage.setTitle("Standalone Report Viewer v0.1");
		stage.show();
	}


}
