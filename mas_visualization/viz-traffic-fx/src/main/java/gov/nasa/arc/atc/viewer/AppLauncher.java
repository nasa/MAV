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

import com.sun.javafx.runtime.VersionInfo;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * Launches the visualization application
 *
 * @author ahamon
 */
public class AppLauncher extends Application {


    public static File geographyFile;
    public static File simulationConfigurationFile;
    public static File simulationFile;

    private static final Logger LOG = Logger.getGlobal();
    private static final Object BARRIER = new Object();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        synchronized (BARRIER) {
            BARRIER.notify();
            App myApp = new App(primaryStage);
            LOG.log(Level.WARNING, "Starting application {0}", myApp);
        }
    }

    private static void initialize() throws InterruptedException {
        Thread t = new Thread("JavaFX Init Thread") {
            @Override
            public void run() {
                Application.launch(AppLauncher.class);
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
        String javaFXVersion = VersionInfo.getVersion();
        LOG.log(Level.WARNING, " Java version used: {0}", javaFXVersion);
        for (int i = 0; i < args.length; i++) {
            LOG.log(Level.WARNING, " arg_{0} = {1}",new Object[]{i,args[i]});
        }
        parseArgs(args);
        AppLauncher.initialize();
    }

    /**
     * Launches the application from another existing application
     */
    public static void runFromExternalApplication() {
        runLater(() -> {
            try {
                new AppLauncher().start(new Stage());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Exception while starting application {0}", e);
            }
        });
    }

    /*
    Private methods to retrieve parameters
     */
    private static void parseArgs(String[] args){
        if(args == null || args.length==0){
            return;
        }
        if(args.length!=3){
            LOG.log(Level.SEVERE,"Incorrect number of arguments. Needs 0 or 3 and were given ",args.length);
            return;
        }
        geographyFile = new File(args[0]);
        simulationConfigurationFile = new File(args[1]);
        simulationFile = new File(args[2]);
        //
        //TODO test files
    }

}
