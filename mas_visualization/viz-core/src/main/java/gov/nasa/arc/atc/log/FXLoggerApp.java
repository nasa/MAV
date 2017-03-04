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

package gov.nasa.arc.atc.log;

import gov.nasa.arc.atc.ManagedApp;
import gov.nasa.arc.atc.ManagedAppEvents;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 * @author Kelsey
 */
class FXLoggerApp implements ManagedApp {

    private static final Logger LOG = Logger.getGlobal();

    private static final String APP_NAME = "Logging Console";
    private static final String FXML_FILE = "LoggerConsole.fxml";

    private final PropertyChangeSupport propertyChangeSupport;
    private final Stage stage;

    private FXLoggerController controller;

    /**
     * Creates the frame for displaying the console logs
     */
    FXLoggerApp() {
        // in case JavaFX platform is not set up yet
        JFXPanel initFxPanel = new JFXPanel();
        LOG.log(Level.INFO, "JavaFX panel created , thrown {0}", initFxPanel);
        propertyChangeSupport = new PropertyChangeSupport(FXLoggerApp.this);
        stage = new Stage();
        runLater(FXLoggerApp.this::createApp);
    }

    @Override
    public String getDisplayName() {
        return APP_NAME;
    }


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void hide() {
        stage.hide();
        propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_HIDDEN, null, null);
    }

    @Override
    public void toFront() {
        stage.toFront();
        propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_SHOWN, null, null);
    }

    /**
     * Shows the frame
     */
    public void show() {
        runLater(stage::show);
        propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_SHOWN, null, null);
    }

    private void createApp() {
        Parent root = new Pane();
        try {
            System.err.println(""+FXLogger.class.getResource(FXML_FILE).getPath());
            FXMLLoader fXMLLoader = new FXMLLoader(FXLogger.class.getResource(FXML_FILE));
            fXMLLoader.load();
            root = fXMLLoader.getRoot();
                controller = fXMLLoader.getController();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error while creating FXLoggerApp : {0}", ex);
            ex.printStackTrace();
        }
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
//            stage.getIcons().add(new Image(SimulationConfigurationLauncher.class.getResourceAsStream("NASA_logo.png")));
        stage.setTitle("Console log");
        stage.setOnCloseRequest(e -> {
            LOG.log(Level.SEVERE, "Hiding {0} on event {1}", new Object[]{APP_NAME, e});
            hide();
        });
        stage.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_SHOWN, null, null);
            } else {
                propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_HIDDEN, null, null);
            }
        });
    }

    void log(Level level, String msg, Object[] params) {
        controller.log(level, msg, params);
    }
}
