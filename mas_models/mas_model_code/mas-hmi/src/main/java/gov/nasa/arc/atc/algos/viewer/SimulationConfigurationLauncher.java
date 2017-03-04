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

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.utils.ConsoleUtils;
import gov.nasa.arc.atc.utils.SimulationProperties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author ahamon
 */
public class SimulationConfigurationLauncher extends Application {

    private static final Logger LOG = Logger.getGlobal();

    private static final String CONFIGURATION_WINDOW_FXML = "ConfigurationWindow.fxml";

    private static final Object BARRIER = new Object();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        synchronized (BARRIER) {
            BARRIER.notify();
            LOG.log(Level.WARNING, "Launching application");
            SimulationProperties.parseProperties(SimulationConfigurationLauncher.class.getResourceAsStream("config.properties"));
            Parent root;
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(CONFIGURATION_WINDOW_FXML));
            fXMLLoader.load();
            ConfigurationWindowController controller = fXMLLoader.getController();
            controller.setStage(primaryStage);
            root = fXMLLoader.getRoot();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("NASA_logo.png")));
            primaryStage.setTitle("Simulation configurator v0.1");
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
     * @param args run argments (nonr needed)
     */
    public static void main(String[] args) {
        try {
            SimulationConfigurationLauncher.initialize();
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
            Application.launch(SimulationConfigurationLauncher.class);
        }

    }

}
