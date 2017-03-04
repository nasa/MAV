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

import gov.nasa.arc.atc.utils.ConsoleUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.viewer.AppLauncher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 
 * @author ahamon
 *
 */
public class ConfiguratorLauncher extends Application {

	/**
	 * Default dimension used for the application creation
	 */
	private static final Dimension2D DEFAULT_DIMENSION = new Dimension2D(1200, 900);

	private static final Logger LOG = Logger.getGlobal();

	private static final String CONFIGURATION_WINDOW_FXML = "ParameterizedConfigurator.fxml";

    private static final Object BARRIER = new Object();

	@Override
	public void start(final Stage primaryStage) throws Exception {
        synchronized (BARRIER) {
            BARRIER.notify();
			LOG.log(Level.WARNING, "Launching application");
			FXMLLoader fXMLLoader = new FXMLLoader(ConfiguratorLauncher.class.getResource(CONFIGURATION_WINDOW_FXML));
			fXMLLoader.load();
			Parent root = fXMLLoader.getRoot();
			Scene scene = new Scene(root, DEFAULT_DIMENSION.getWidth(), DEFAULT_DIMENSION.getHeight());
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(AppLauncher.class.getResourceAsStream("NASA_logo.png")));
			primaryStage.setTitle("Simulation configurator v0.2");
			primaryStage.show();
		}
	}

	private static void initialize() throws InterruptedException {
		Thread t = new JFXThread("JavaFX Init Thread");
		t.setDaemon(true);
		t.start();
        synchronized (BARRIER) {
			boolean wait = true;
			while (wait) {
                BARRIER.wait();
				wait = false;
			}
		}
	}

	/**
	 * Launches the simulation configuration
	 * 
	 * @param args run arguments (not needed)
   	 */
	public static void main(String[] args) {
		try {
			ConfiguratorLauncher.initialize();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static class JFXThread extends Thread {

		private JFXThread(String message) {
			super(message);
		}

		@Override
		public void run() {
			ConsoleUtils.setLoggingLevel(Level.WARNING);
			Application.launch(ConfiguratorLauncher.class);
		}

	}

}
