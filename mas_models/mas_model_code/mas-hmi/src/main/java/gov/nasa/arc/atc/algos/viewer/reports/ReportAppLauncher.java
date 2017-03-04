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

import static javafx.application.Platform.runLater;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLauncher;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import javafx.application.Application;
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
public class ReportAppLauncher extends Application {

	private static final Logger LOG = Logger.getGlobal();

	private static Object barrier = new Object();
	private Scene scene;

	private StandaloneReportViewerController viewerController;

	@Override
	public void start(final Stage primaryStage) throws Exception {
		synchronized (barrier) {
			barrier.notify();
			LOG.log(Level.WARNING, "Launching application");
			ConsoleUtils.setLoggingLevel(Level.WARNING);
			Parent root = new Pane();
			try {
				FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("StandaloneReportViewer.fxml"));
				fXMLLoader.load();
				root = fXMLLoader.getRoot();
				viewerController = fXMLLoader.getController();
				viewerController.setOwner(primaryStage);
				viewerController.setReports();
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, "Error while creating ReportApp : {0}", ex);
			}
			scene = new Scene(root, 900, 900);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(SimulationConfigurationLauncher.class.getResourceAsStream("NASA_logo.png")));
			primaryStage.setTitle("Standalone Report Viewer v0.1");
			primaryStage.show();
		}
	}

	private static void initialize(String[] args) throws InterruptedException {
		Thread t = new JFXThread("JavaFX Init Thread", args);
		t.setDaemon(true);
		t.start();
		synchronized (barrier) {
			boolean wait = true;
			while (wait) {
				barrier.wait();
				wait = false;
			}
		}
	}

	/**
	 * Main method. No argument is yet useful
	 * 
	 * @param args launch arguments
	 */
	public static void main(String[] args) {
		try {
			ReportAppLauncher.initialize(args);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Launches the application from another exising application
	 */
	public static final void runFromExternalApplication() {
		runLater(() -> {
			try {
				new ReportAppLauncher().start(new Stage());
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Exception while starting application {0}", e);
			}
		});
	}

	private static class JFXThread extends Thread {

		private final String[] launchArgs;

		private JFXThread(String message, String[] args) {
			super(message);
			launchArgs = args;
		}

		@Override
		public void run() {
			ConsoleUtils.setLoggingLevel(Level.WARNING);
			Application.launch(ReportAppLauncher.class, launchArgs);
		}

	}

}
