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

package gov.nasa.arc.atc.metrics.imagecreation;

import gov.nasa.arc.atc.ManagedApp;
import gov.nasa.arc.atc.ManagedAppEvents;
import gov.nasa.arc.atc.core.DataModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class ImageCreationCreator implements ManagedApp {

    private static final Logger LOG = Logger.getGlobal();
    private static final String FXML_NAME = "ImageCreationConfigurator.fxml";
    private static final String DISPLAY_NAME = "Image Creator";

    private final Stage stage;
    //
    private final ImageCreationConfiguratorController controller;

    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * Creates a new {@link ImageCreationCreator}
     */
    public ImageCreationCreator() {
        propertyChangeSupport = new PropertyChangeSupport(ImageCreationCreator.this);
        stage = new Stage();
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
        try {
            fXMLLoader.load();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception {0}", e);
            e.printStackTrace();
        }
        Parent rootNode = fXMLLoader.getRoot();
        controller = fXMLLoader.getController();
        //
        Scene scene = new Scene(rootNode);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setOnCloseRequest(event -> {
            LOG.log(Level.SEVERE, "Hiding {0} on event {1}", new Object[]{DISPLAY_NAME, event});
            hide();
        });
        stage.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_SHOWN, null, null);
            } else {
                propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_HIDDEN, null, null);
            }
        });
        stage.show();
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
        propertyChangeSupport.firePropertyChange(ManagedAppEvents.STAGE_SHOWN, null, null);
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public void setDataModel(DataModel dataModel) {
        controller.setDataModel(dataModel);
        toFront();
    }
}
