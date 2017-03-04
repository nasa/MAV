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

import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.atc2dviz.*;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.geography.Waypoint;
import gov.nasa.arc.atc.utils.FileUtils;
import gov.nasa.arc.atc.utils.MapDrawingUtils;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.controlsfx.control.RangeSlider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 */
public class ImageCreationConfiguratorController implements Initializable {

    private static final int DEFAULT_IMAGE_WIDTH = 800;
    private static final int DEFAULT_IMAGE_HEIGHT = 600;

    private static final Logger LOG = Logger.getGlobal();

    private final FileChooser fileChooser = new FileChooser();

    private MercatorAttributes mercatorAttributes = new MercatorAttributes();

    /*
     * Left pane
     */
    @FXML
    private ListView<PixelCreator> pixelCreatorListView;
    @FXML
    private AnchorPane mapPane;
    @FXML
    private TextField widthTextField;
    @FXML
    private TextField heightTextField;
    @FXML
    private TextField minTimeField;
    @FXML
    private TextField maxTimeField;
    //
    // background configuration
    //
    @FXML
    private CheckBox backgroundOptionCheckB;
    //
    @FXML
    private CheckBox mapBackgroundCheckB;
    //
    @FXML
    private CheckBox sectorCheckB;
    //
    @FXML
    private CheckBox routesCheckB;
    //
    @FXML
    private CheckBox waypointsCheckB;
    @FXML
    private Label opacityLabel;
    @FXML
    private Slider opacitySlider;
    //
    @FXML
    private RangeSlider timeSlider;
    @FXML
    private Button createButton;

    /*
     * Right pane
     */
    @FXML
    private AnchorPane previewPane;
    @FXML
    private Pane previewCanvas;
    @FXML
    private ProgressBar previewProgressB;
    @FXML
    private Button saveButton;

    private DataModel data;
    private PixelCreator pixelCreatorSelected;

    //
    private Rectangle background;
    private Rectangle clip;
    private Group backgroundGroup;
    private Group mapBackgroundGroup;
    private Group routesGroup;
    private Group waypointsGroup;
    private Group sectorsGroup;
    private Group imageGroup;
    private Rectangle interactionLayer;
    //
    private List<Region2D> region2ds;
    private List<Route2D> routes2D;
    private List<Waypoint2D> waypoint2Ds;
    private List<Sector2D> sector2Ds;

    // get rid of these?
    private int minAvailableTime = 0;
    private int maxAvailableTime = 0;

    private int imageWidth = DEFAULT_IMAGE_WIDTH;
    private int imageHeight = DEFAULT_IMAGE_HEIGHT;

    private boolean imageGenerated = false;

    // attributes used for the calculation of the translation
    private double lastXPressed;
    private double lastYPressed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPixelCreatorsList();
        //
        widthTextField.setText(Integer.toString(imageWidth));
        heightTextField.setText(Integer.toString(imageHeight));
        widthTextField.textProperty().addListener((obs, old, newV) -> {
            try {
                imageWidth = Integer.parseInt(widthTextField.getText());
                updateImage();
            } catch (Exception e) {
                LOG.log(Level.INFO, " Not a valid number: {0}", e);
            }
        });
        heightTextField.textProperty().addListener((obs, old, newV) -> {
            try {
                imageHeight = Integer.parseInt(heightTextField.getText());
                updateImage();
            } catch (Exception e) {
                LOG.log(Level.INFO, " Not a valid number: {0}", e);
            }
        });
        //
        createButton.setDisable(true);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(FileUtils.EX_PNG);
        //
        minTimeField.textProperty().addListener((obs, old, newValue) -> parseMinTime());
        maxTimeField.textProperty().addListener((obs, old, newValue) -> parseMaxTime());
        timeSlider.lowValueProperty().addListener((obs, old, newValue) -> rangeMinChanged());
        timeSlider.highValueProperty().addListener((obs, old, newValue) -> rangeMaxChanged());
        pixelCreatorListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newItem) -> {
            pixelCreatorSelected = newItem;
            createButton.setDisable(newItem == null);
        });
        //
        backgroundOptionCheckB.selectedProperty().addListener((obs, old, isSelected) -> {
            updateBackgoundControls();
            backgroundGroup.setVisible(isSelected);
        });
        mapBackgroundCheckB.selectedProperty().addListener((obs, old, isSelected) -> mapBackgroundGroup.setVisible(isSelected));
        routesCheckB.selectedProperty().addListener((obs, old, isSelected) -> routesGroup.setVisible(isSelected));
        waypointsCheckB.selectedProperty().addListener((obs, old, isSelected) -> waypointsGroup.setVisible(isSelected));
        sectorCheckB.selectedProperty().addListener((obs, old, isSelected) -> sectorsGroup.setVisible(isSelected));
        opacitySlider.valueProperty().addListener((obs, old, newOpacity) -> backgroundGroup.setOpacity((double) newOpacity));
        //
        background = new Rectangle();
        clip = new Rectangle();
        backgroundGroup = new Group();
        mapBackgroundGroup = new Group();
        routesGroup = new Group();
        waypointsGroup = new Group();
        sectorsGroup = new Group();
        backgroundGroup.getChildren().addAll(mapBackgroundGroup, sectorsGroup, routesGroup, waypointsGroup);
        imageGroup = new Group();
        //
        interactionLayer = new Rectangle();
        interactionLayer.setOpacity(0.0);
        interactionLayer.setWidth(imageWidth);
        interactionLayer.setHeight(imageHeight);
        interactionLayer.setFill(Color.FUCHSIA);
        initInteractivity();
        //
        previewCanvas.getChildren().addAll(background, backgroundGroup, imageGroup, interactionLayer);
        previewCanvas.setClip(clip);
        //
        region2ds = new LinkedList<>();
        routes2D = new LinkedList<>();
        waypoint2Ds = new LinkedList<>();
        sector2Ds = new LinkedList<>();
        //
        backgroundGroup.setVisible(false);
    }

    /**
     * @param event {@link ActionEvent} triggering the create action
     */
    @FXML
    public void handleCreateAction(ActionEvent event) {
        LOG.log(Level.INFO, "handleCreateAction {0}", event);
        saveButton.setDisable(false);
        startImageCreation();
    }

    /**
     * @param event {@link ActionEvent} triggering the save action
     */
    @FXML
    public void handleSaveAction(ActionEvent event) {
        LOG.log(Level.INFO, "handleSaveAction {0}", event);
        File f = fileChooser.showSaveDialog(saveButton.getScene().getWindow());
        WritableImage snapshot = previewCanvas.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = new BufferedImage(550, 400, BufferedImage.TYPE_INT_ARGB);
        BufferedImage image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
        try {
            Graphics2D gd = (Graphics2D) image.getGraphics();
            gd.translate(previewCanvas.getWidth(), previewCanvas.getHeight());
            ImageIO.write(image, "png", f);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception while saving: {0}", ex);
        }
    }

    protected void setDataModel(DataModel dataModel) {
        data = dataModel;
        updateFields();
        // update mercator projection
        mercatorAttributes.resetAttributes();
        mercatorAttributes.setMapWidth(imageWidth);
        mercatorAttributes.setMapHeight(imageHeight);
        mercatorAttributes.processCoordinates(SimulationManager.getATCGeography().getElements().stream().map(e -> new Coordinates(e.getLatitude(), e.getLongitude())).collect(Collectors.toList()));
        //
        createBackgroundElements();
        previewCanvas.setPrefWidth(imageWidth);
        previewCanvas.setPrefHeight(imageHeight);
        background.setWidth(imageWidth);
        background.setHeight(imageHeight);
        clip.setWidth(imageWidth);
        clip.setHeight(imageHeight);
        interactionLayer.setWidth(imageWidth);
        interactionLayer.setHeight(imageHeight);
    }

    private void initPixelCreatorsList() {
        //TODO
        // TEMP -- => lookup
        pixelCreatorListView.getItems().add(new MeteringPixelCreator());
        pixelCreatorListView.getItems().add(new TrajectoryPixelCreator());
        pixelCreatorListView.getItems().add(new HandOffPixelCreator());
        pixelCreatorListView.getItems().add(new StartInSimulationPixelCreator());
        // END TEMP
        pixelCreatorListView.setCellFactory(p -> {
            LOG.log(Level.INFO, "setting pixelCreatorListView cell factory for {0}", p);
            return new ClassFactoryListCell();
        });
    }

    private void initInteractivity() {
        interactionLayer.setOnMousePressed(event -> {
            lastXPressed = event.getX();
            lastYPressed = event.getY();
        });
        interactionLayer.setOnMouseDragged(event -> {
            double deltaX = event.getX() - lastXPressed;
            double deltaY = event.getY() - lastYPressed;
            double trX = backgroundGroup.getTranslateX() + deltaX;
            double trY = backgroundGroup.getTranslateY() + deltaY;
            backgroundGroup.setTranslateX(trX);
            backgroundGroup.setTranslateY(trY);
            imageGroup.setTranslateX(trX);
            imageGroup.setTranslateY(trY);
            lastXPressed = event.getX();
            lastYPressed = event.getY();
        });
        interactionLayer.setOnScroll(event -> {
            double scaleFactor;
            if (event.getDeltaY() > 0) {
                scaleFactor = mercatorAttributes.getZoomRatio() * World2D.ZOOM_FACTOR;
            } else {
                scaleFactor = mercatorAttributes.getZoomRatio() / World2D.ZOOM_FACTOR;
            }
            updateAllPositions(event.getX(), event.getY(), scaleFactor);
        });
        interactionLayer.setOnMouseClicked(e -> {

            Coordinates coordinates = mercatorAttributes.getCoordinates(e.getX() - backgroundGroup.getTranslateX(), e.getY() - backgroundGroup.getTranslateY());
            System.err.println(" " + coordinates.getLatitude() + " , " + coordinates.getLongitude());
        });
    }

    private void updateFields() {
        // hum..
        minAvailableTime = 0;
        maxAvailableTime = data.getSimulationDuration();
        // presentation part
        minTimeField.setText(Integer.toString(minAvailableTime));
        maxTimeField.setText(Integer.toString(maxAvailableTime));
        timeSlider.setMin(0);
        timeSlider.setMax(maxAvailableTime);
        timeSlider.adjustHighValue(maxAvailableTime);
        timeSlider.setMinorTickCount(1);
    }


    private void parseMinTime() {
        try {
            int newMinTime = Integer.parseInt(minTimeField.getText());
            if (newMinTime < minAvailableTime) {
                setMinTime(minAvailableTime);
            } else if (newMinTime >= maxAvailableTime) {
                setMinTime(maxAvailableTime - 1);
            } else {
                setMinTime(newMinTime);
            }
        } catch (Exception e) {
            LOG.log(Level.INFO, "Error minTextField: {0}", e);
        }
    }

    private void parseMaxTime() {
        try {
            int newMaxTime = Integer.parseInt(maxTimeField.getText());
            if (newMaxTime > maxAvailableTime) {
                setMaxTime(maxAvailableTime);
            } else if (newMaxTime <= minAvailableTime) {
                setMaxTime(minAvailableTime + 1);
            } else {
                setMaxTime(newMaxTime);
            }
        } catch (Exception e) {
            LOG.log(Level.INFO, "Error maxTextField: {0}", e);
        }
    }

    private void rangeMinChanged() {
        minTimeField.setText(Integer.toString((int) timeSlider.getLowValue()));
    }

    private void rangeMaxChanged() {
        maxTimeField.setText(Integer.toString((int) timeSlider.getHighValue()));
    }

    private void setMinTime(int time) {
        minTimeField.setText(Integer.toString(time));
        timeSlider.adjustLowValue(time);
    }

    private void setMaxTime(int time) {
        maxTimeField.setText(Integer.toString(time));
        timeSlider.adjustHighValue(time);
    }

	/*
     * Background elements
	 */

    private void createBackgroundElements() {
        createBackgroundRegions();
        createRoute2Ds();
        createWaypoint2Ds();
        createSector2Ds();
        updateBackgroundsElements();
    }

    private void createBackgroundRegions() {
        MapDrawingUtils.getWorldGeography().getBackgroundRegions().forEach(region -> {
            Region2D region2d = new Region2D(region, mercatorAttributes);
            mapBackgroundGroup.getChildren().add(region2d.getNode());
            region2ds.add(region2d);
        });
    }

    private void createSector2Ds() {
        SimulationManager.getATCGeography().getSectors().forEach(sector -> {
            Sector2D sector2D = new Sector2D(sector, mercatorAttributes);
            sector2Ds.add(sector2D);
            sectorsGroup.getChildren().add(sector2D.getNode());
        });
    }

    private void createWaypoint2Ds() {
        SimulationManager.getATCGeography().getWaypoints().forEach(wpt -> {
            Waypoint2D wpt2D = new Waypoint2D((Waypoint) wpt, mercatorAttributes);
            waypoint2Ds.add(wpt2D);
            waypointsGroup.getChildren().add(wpt2D.getNode());
        });
    }

    private void createRoute2Ds() {
        SimulationManager.getATCGeography().getArrivalRoutes().forEach(route -> {
            Route2D route2D = new Route2D(route, mercatorAttributes);
            routes2D.add(route2D);
            routesGroup.getChildren().add(route2D.getNode());
        });
    }

    private void updateBackgroundsElements() {
        region2ds.forEach(region2d -> region2d.updatePosition(true));
        waypoint2Ds.forEach(waypoint2D -> waypoint2D.updatePosition(true));
        routes2D.forEach(route2d -> route2d.updatePosition(true));
        sector2Ds.forEach(sector2D -> sector2D.updatePosition(true));
    }

    private void updateImage() {
        previewCanvas.setPrefWidth(imageWidth);
        previewCanvas.setPrefHeight(imageHeight);
        background.setWidth(imageWidth);
        background.setHeight(imageHeight);
        clip.setWidth(imageWidth);
        clip.setHeight(imageHeight);
        interactionLayer.setWidth(imageWidth);
        interactionLayer.setHeight(imageHeight);
        updateAllPositions(imageWidth / 2.0, imageHeight / 2.0, 1);
    }

    private void updateAllPositions(double centerX, double centerY, double newScaleFactor) {
        // retrieve coordinates at screen 0,0 -> world.translateX, Y
        Coordinates oldTopLeft = mercatorAttributes.getCoordinates(centerX - backgroundGroup.getTranslateX(), centerY - backgroundGroup.getTranslateY());
        mercatorAttributes.setZoomRatio(newScaleFactor);
        mercatorAttributes.setMapWidth(imageWidth);
        mercatorAttributes.setMapHeight(imageHeight);
        //
        Point2D newTopLeft = mercatorAttributes.getXYPosition(oldTopLeft);
        //
        runLater(() -> {
            backgroundGroup.setTranslateX(centerX - newTopLeft.getX());
            backgroundGroup.setTranslateY(centerY - newTopLeft.getY());
            imageGroup.setTranslateX(centerX - newTopLeft.getX());
            imageGroup.setTranslateY(centerY - newTopLeft.getY());
            updateBackgroundsElements();
            if (imageGenerated) {
                startImageCreation();
            }
        });
    }


	/*
     * Image creation
	 */

    private void startImageCreation() {
        imageGroup.getChildren().clear();
        // temp
        pixelCreatorSelected.setDataModel(data);
        pixelCreatorSelected.setProjection(mercatorAttributes);
        pixelCreatorSelected.setImageOffset((int) backgroundGroup.getTranslateX(), (int) backgroundGroup.getTranslateY());
        pixelCreatorSelected.display((int) timeSlider.getLowValue(), (int) timeSlider.getHighValue(), imageGroup);
        imageGenerated = true;

    }

    private void updateBackgoundControls() {
        boolean bDisabled = !backgroundOptionCheckB.isSelected();
        opacityLabel.setDisable(bDisabled);
        opacitySlider.setDisable(bDisabled);
        mapBackgroundCheckB.setDisable(bDisabled);
        routesCheckB.setDisable(bDisabled);
        waypointsCheckB.setDisable(bDisabled);
        sectorCheckB.setDisable(bDisabled);
    }

    private class ClassFactoryListCell extends ListCell<PixelCreator> {
        @Override
        protected void updateItem(PixelCreator item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getClass().getSimpleName());
            }
        }
    }
}
