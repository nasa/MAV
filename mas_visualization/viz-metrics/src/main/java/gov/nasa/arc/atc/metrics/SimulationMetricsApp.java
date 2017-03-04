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

package gov.nasa.arc.atc.metrics;

import gov.nasa.arc.atc.ManagedApp;
import gov.nasa.arc.atc.ManagedAppEvents;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * Viewer for overall simulation info Displays box of overall info, and multiple plots for each category, with a tableview to sort what is displayed on the charts
 *
 * @author Kelsey
 */
public class SimulationMetricsApp implements ManagedApp {

    private static final Logger LOG = Logger.getGlobal();
    private static final String DISPLAY_NAME = "Simulation Metrics";

    public static final String APP_FXML_FILE = "MetricsPopUp.fxml";
    public static final String APP_TITLE = "View Simulation Metrics";
    public static final int SCENE_WIDTH = 1400;
    public static final int SCENE_HEIGHT = 900;


    private final PropertyChangeSupport propertyChangeSupport;
    private final Stage stage = new Stage();

    /**
     * --------------------------------
     */
    public SimulationMetricsApp() {
        propertyChangeSupport = new PropertyChangeSupport(SimulationMetricsApp.this);
        runLater(SimulationMetricsApp.this::buildApp);
    }

    private void buildApp() {
        Parent root;
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(APP_FXML_FILE));
        try {
            fXMLLoader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error while creating SimulationMetricsApp : {0}", ex);
        }
        root = fXMLLoader.getRoot();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            LOG.log(Level.SEVERE, "Hiding {0}", DISPLAY_NAME);
            hide();
        });
        stage.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_SHOWN, null, null);
            } else {
                propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_HIDDEN, null, null);
            }
        });
        stage.setTitle(APP_TITLE);
    }

    public void openApp() {
        stage.show();
        propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_SHOWN, null, null);
    }

    public void closeApp() {
        hide();
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
        stage.show();
        stage.toFront();
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

}
