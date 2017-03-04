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

package gov.nasa.arc.atc.scenariogen.hmi;

import gov.nasa.arc.atc.utils.ConsoleUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 */
public class ScenarioGenAppLauncher extends Application {

    private static final String FXML = "ScenarioGenerator.fxml";


    private static final Logger LOG = Logger.getGlobal();
    private static final Object BARRIER = new Object();


    @Override
    public void start(final Stage primaryStage) throws Exception {
        synchronized (BARRIER) {
            BARRIER.notify();
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FXML));
            fXMLLoader.load();
            Parent root = fXMLLoader.getRoot();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            LOG.log(Level.WARNING, "Starting application ScenarioGenAppLauncher");
        }
    }

    private static void initialize() throws InterruptedException {
        Thread t = new Thread("JavaFX Init Thread") {
            @Override
            public void run() {
                Application.launch(ScenarioGenAppLauncher.class);
            }
        };
        t.setDaemon(true);
        t.start();
        synchronized (BARRIER) {
            boolean waited = false;
            while (!waited) {
                BARRIER.wait();
                waited = true;
            }
        }
    }

    /**
     * Runs the application and sets the logging level
     *
     * @param args no arguments needed or (optional) geography, configuration and scenario file paths
     * @throws InterruptedException due to thread synchronization
     */
    public static void main(String[] args) throws InterruptedException {
        ConsoleUtils.setLoggingLevel(Level.WARNING);
        String javaVersion =  System.getProperty("java.version");
        LOG.log(Level.WARNING, " Java version used: {0}", javaVersion);
        ScenarioGenAppLauncher.initialize();
    }

    /**
     * Launches the application from another existing application
     */
    public static void runFromExternalApplication() {
        runLater(() -> {
            try {
                new ScenarioGenAppLauncher().start(new Stage());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Exception while starting application {0}", e);
            }
        });
    }


}
