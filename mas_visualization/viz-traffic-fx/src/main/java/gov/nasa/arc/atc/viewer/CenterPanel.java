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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.arc.atc.viewer;

import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.atc2dviz.World2D;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.utils.MapDrawingUtils;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * @author hamon
 */
public class CenterPanel {

    private static final Logger LOG = Logger.getGlobal();
    // offset used so the the inner nodes size are less than the scroll pane one
    // -> not nice
    private static final double OFFSET = 2;
    private static final double DEFAULT_DIMENSION = 400;
    private static final int DEFAULT_PADDING = 8;

    private final AnchorPane pane;

    private final Dimension2D defaultButtonDimension = new Dimension2D(50, 30);

    private final Group contentPaneGroup;
    private final ToggleButton world2DButton;
    private final ToggleButton world3DButton;
    //
    private final Button zoomInButton;
    private final Button zoomOutButton;
    //
    private double centerAreaWidth = 0;
    private double centerAreaHeight = 0;
    //
    private final World2D world2D;
    //
    private final Timer resizeTimer;

    /**
     * Creates the center panel for the visualization
     */
    public CenterPanel() {
        pane = new AnchorPane();
        pane.setPrefSize(DEFAULT_DIMENSION, DEFAULT_DIMENSION);
        pane.setMinSize(DEFAULT_DIMENSION, DEFAULT_DIMENSION);
        contentPaneGroup = new Group();
        //
        world2DButton = new ToggleButton("2D");
        world3DButton = new ToggleButton("3D");
        //
        zoomInButton = new Button("+");
        zoomOutButton = new Button("-");
        //
        world2D = World2D.getInstance();
        ViewerComponents.registerWorld2D(world2D);
        DisplayOptionsManager.addPropertyChangeListener(world2D);
        //
        resizeTimer = new Timer(50, this::performResize);
        resizeTimer.setRepeats(false);
        //
        initPanel();
        runLater(CenterPanel.this::initSizeListeners);

    }

    protected Node getNode() {
        return pane;
    }

    //not optimal signature
    //TODO: consolidate atclib api (geography part)
    void handleZoomOnARegion(List<Coordinates> edges) {
        //TODO: handle when using world3D
        world2D.updateMercatorAttributes(edges);
    }

    private void initPanel() {
        world2DButton.setPrefSize(defaultButtonDimension.getWidth(), defaultButtonDimension.getHeight());
        world3DButton.setPrefSize(defaultButtonDimension.getWidth(), defaultButtonDimension.getHeight());
        world3DButton.setTranslateX(defaultButtonDimension.getWidth());
        //
        zoomInButton.setPrefSize(defaultButtonDimension.getWidth(), defaultButtonDimension.getHeight());
        zoomOutButton.setPrefSize(defaultButtonDimension.getWidth(), defaultButtonDimension.getHeight());
        //
        world2DButton.setOnAction(event -> {
            LOG.log(Level.FINE, "world2DButton.setOnAction {0}", event);
            if (world2DButton.isSelected()) {
                set2D();
            } else {
                throw new IllegalStateException();
            }
        });
        world3DButton.setOnAction(event -> {
            LOG.log(Level.FINE, "world3DButton.setOnAction {0}", event);
            if (world3DButton.isSelected()) {
                set3D();
            } else {
                throw new IllegalStateException();
            }
        });
        set2D();
        //
        zoomInButton.setOnAction(event -> {
            LOG.log(Level.FINE, "zoomInButton.setOnAction {0}", event);
//            MapDrawingUtils.discreteZoomIn();
            world2D.requestMultiplyZoomLevel(MapDrawingUtils.DEFAULT_ZOOM_STEP);
        });
        zoomOutButton.setOnAction(event -> {
            LOG.log(Level.FINE, "zoomOutButton.setOnAction {0}", event);
//            MapDrawingUtils.discreteZoomOut();
            world2D.requestMultiplyZoomLevel(1.0 / MapDrawingUtils.DEFAULT_ZOOM_STEP);
        });
        //
        pane.getChildren().add(contentPaneGroup);
        contentPaneGroup.getChildren().add(world2D.getNode());
        //world buttons are not added
        contentPaneGroup.getChildren().add(zoomInButton);
        contentPaneGroup.getChildren().add(zoomOutButton);
        //
    }

    private void initSizeListeners() {
        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            LOG.log(Level.FINE, "Center panel width changed {0} {1} {2}", new Object[]{observable, oldValue, newValue});
            centerAreaWidth = (double) newValue - OFFSET;
            updateResizeTimer();

        });
        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            LOG.log(Level.FINE, "Center panel height changed {0} {1} {2}", new Object[]{observable, oldValue, newValue});
            centerAreaHeight = (double) newValue - OFFSET;
            updateResizeTimer();
        });
        SimulationManager.addPropertyChangeListener(this::handleSimulationChange);
        performResize(null);
    }

    private void set2D() {
        world2DButton.setSelected(true);
        world3DButton.setSelected(false);
        world2DButton.setDisable(true);
        world3DButton.setDisable(false);
        world2D.setVisible(true);
    }

    private void set3D() {
        world2DButton.setSelected(false);
        world3DButton.setSelected(true);
        world2DButton.setDisable(false);
        world3DButton.setDisable(true);
        world2D.setVisible(false);
    }

    private void updateResizeTimer() {
        if (resizeTimer.isRunning()) {
            resizeTimer.restart();
        } else {
            resizeTimer.start();
        }
    }

    private void performResize(java.awt.event.ActionEvent timerEvent) {
        LOG.log(Level.INFO, "performResize on timer event {0}", timerEvent);
        world2D.updateSize(centerAreaWidth, centerAreaHeight);
        zoomInButton.setTranslateX(DEFAULT_PADDING);
        zoomOutButton.setTranslateX(DEFAULT_PADDING);
        zoomInButton.setTranslateY(centerAreaHeight - 2.0 * defaultButtonDimension.getHeight() - 2.0 * DEFAULT_PADDING);
        zoomOutButton.setTranslateY(centerAreaHeight - defaultButtonDimension.getHeight() - DEFAULT_PADDING);
        resizeTimer.stop();
    }

    private void handleSimulationChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case SimulationManager.DATA_MODEL_CHANGED:
                DataModel dataModel = (DataModel) evt.getNewValue();
                world2D.setSimulationDataModel(dataModel);
                break;
            default:
                break;
        }

    }

}
