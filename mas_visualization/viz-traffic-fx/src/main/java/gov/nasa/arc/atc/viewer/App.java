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

import gov.nasa.arc.atc.SimulationEngine;
import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.runLater;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Visualization application
 * 
 * @author ahamon
 */
public final class App {

	private static final Logger LOG = Logger.getGlobal();
	private static final String MAIN_FRAME_FXML = "MainFrame.fxml";
	private static final Dimension DEFAULT_DIMENSION = new Dimension(1600, 900);
	//
	private final Stage stage;

	App(Stage primaryStage) {
		stage = primaryStage;
		JFXPanel jfxPanel = new JFXPanel();
		LOG.log(Level.INFO, "JavaFX Platform initialized with {0}", new Object[] { jfxPanel });
        runLater(App.this::initWindow);
	}

	private void initWindow() {
		try {
			ViewerComponents.registerStage(stage);
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(MAIN_FRAME_FXML));
			fXMLLoader.load();
			Parent root = fXMLLoader.getRoot();
            Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("NASA_logo.png")));
			stage.setTitle("ATC viewer v0.1");
			//
			stage.setWidth(DEFAULT_DIMENSION.getWidth());
			stage.setHeight(DEFAULT_DIMENSION.getHeight());
			stage.setOnCloseRequest(event -> {
				LOG.log(Level.SEVERE, "Closing the application");
				SimulationEngine.stopEngine();
				LOG.log(Level.WARNING, "closing app is NOT yet implemented correctly via this method");
				Platform.exit();
				//Runtime.getRuntime().exit(0);
			});
			stage.show();
			//
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Exception while loading application: {0}", new Object[] { ex });
		}
	}

}
