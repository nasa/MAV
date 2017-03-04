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
package gov.nasa.arc.atc.atc2dviz;

import gov.nasa.arc.atc.SimulationEngine;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.WorldGeography;
import gov.nasa.arc.atc.WorldVisualization;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Route;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.MapDrawingUtils;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import gov.nasa.arc.atc.viewer.DisplayOptionsManager;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static javafx.application.Platform.runLater;

/**
 * @author hamon
 */
public class World2D implements WorldVisualization {

    public static final double ZOOM_FACTOR = 1.2;

    private static final Logger LOG = Logger.getLogger(World2D.class.getName());
    private static final double DEFAULT_WIDTH = 800;
    private static final double DEFAULT_HEIGHT = 600;
    private static final double POLYGON_RADIUS = 4;

    private static World2D instance = null;


    private final MercatorAttributes mercatorAttributes;

    private final AnchorPane mainNode;
    private final Rectangle glassInteractiveLayer;
    private final Group worldGroup;
    private final Group slotsGroup;
    private final Group aircraftGroup;
    private final Group sectorsGroup;
    //
    private final Canvas backgroundCanvas;
    private final GraphicsContext backgroundContext;
    // duplication ? getNode children and cast and update
    private final List<Slot2D> slot2Ds;
    private final List<Aircraft2D> aircraft2ds;
    // size of the display area in the center panel
    private final Rectangle clipRectangle;
    private double displayWidth = 0;
    private double displayHeight = 0;
    //
    // attributes used for the calculation of the translation
    private double lastXPressed;
    private double lastYPressed;

    private ATCGeography2D currentGeography;
    private ATCGeography geography;


    // visibility attributes
    private boolean wpVisibility = true;
    private boolean wpNameVisibility = true;
    private boolean mapVisibility = true;
    private boolean routeVisibility = true;
    private boolean segmentVisibility = true;
    private boolean airportVisibility = true;


    public static World2D getInstance() {
        if (instance == null) {
            instance = new World2D();
        }
        return instance;
    }


    //<editor-fold defaultstate="collapsed" desc="Instantiation and initialization">
    private World2D() {
        //
        mercatorAttributes = new MercatorAttributes();
        //
        mainNode = new AnchorPane();
        mainNode.setStyle("-fx-background-color: black;");
        glassInteractiveLayer = new Rectangle();
        worldGroup = new Group();
        slotsGroup = new Group();
        aircraftGroup = new Group();
        sectorsGroup = new Group();
        clipRectangle = new Rectangle();
        //
        backgroundCanvas = new Canvas();
        backgroundContext = backgroundCanvas.getGraphicsContext2D();
        //
        slot2Ds = new LinkedList<>();
        aircraft2ds = new LinkedList<>();
        SimulationEngine.addPropertyChangeListener(this::handleSimulationTimeChange);
        MapDrawingUtils.addPropertyChangeListener(this::handleGeographyChange);
        //
        runLater(() -> {
            initWorld2D();
            initBackGroundInteractivity();
        });
    }

    private void initWorld2D() {
        displayWidth = DEFAULT_WIDTH;
        displayHeight = DEFAULT_HEIGHT;
        mercatorAttributes.setMapWidth(displayWidth);
        mercatorAttributes.setMapHeight(displayHeight);
        //
        glassInteractiveLayer.setOpacity(0.0);
        // a "non-conventional" color to be sure we see if there is a problem (since shall not be visible: opacity=0)
        glassInteractiveLayer.setFill(Color.BLUEVIOLET);
        worldGroup.getChildren().addAll(sectorsGroup, slotsGroup, aircraftGroup);
        mainNode.getChildren().addAll(backgroundCanvas, glassInteractiveLayer, worldGroup);
        mainNode.setClip(clipRectangle);
        //
        updateSize(displayWidth, displayHeight);
        //
        List<Coordinates> mapCoordinates = new LinkedList<>();
        MapDrawingUtils.getWorldGeography().getBackgroundRegions().forEach(region -> mapCoordinates.addAll(region.getVertices()));
        updateMercatorAttributes(mapCoordinates);
    }


    private void initBackGroundInteractivity() {
        glassInteractiveLayer.setOnScroll(this::handleScrollEvent);
        glassInteractiveLayer.setOnMousePressed(this::handleBackgroundMousePressed);
        glassInteractiveLayer.setOnMouseDragged(this::handleBackgroundMouseDragged);
        glassInteractiveLayer.setOnMouseReleased(this::handleBackgroundMouseReleased);
        glassInteractiveLayer.setOnMouseClicked(this::handleMouseClicked);
    }
    //</editor-fold>


    public void requestMultiplyZoomLevel(double zoomFactor) {
        updateAllPositions(displayWidth / 2.0, displayHeight / 2.0, mercatorAttributes.getZoomRatio() * zoomFactor);
    }


    @Override
    public final Node getNode() {
        return mainNode;
    }

    @Override
    public void setVisible(boolean visibility) {
        mainNode.setVisible(visibility);
    }


    /**
     * @param edges the coordinates defining the edges of the new point of view
     */
    public void updateMercatorAttributes(List<Coordinates> edges) {
        worldGroup.setTranslateX(0);
        worldGroup.setTranslateY(0);
        mercatorAttributes.resetAttributes();
        mercatorAttributes.processCoordinates(edges);
        updateAllPositions(displayWidth / 2.0, displayHeight / 2.0, 1);
    }


    public final void updateSize(double newWidth, double newHeight) {
        displayWidth = newWidth;
        displayHeight = newHeight;

        backgroundCanvas.setWidth(displayWidth);
        backgroundCanvas.setHeight(displayHeight);

        updateAllPositions(displayWidth / 2.0, displayHeight / 2.0, 1);
    }

    private void updateAllPositions(double centerX, double centerY, double newScaleFactor) {
        // retrieve coordinates at screen 0,0 -> world.translateX, Y
        Coordinates oldTopLeft = mercatorAttributes.getCoordinates(centerX - worldGroup.getTranslateX(), centerY - worldGroup.getTranslateY());

        mercatorAttributes.setMapWidth(displayWidth);
        mercatorAttributes.setMapHeight(displayHeight);
        mercatorAttributes.setZoomRatio(newScaleFactor);
        //
        Point2D newTopLeft = mercatorAttributes.getXYPosition(oldTopLeft);
        //
        runLater(() -> {
            worldGroup.setTranslateX(centerX - newTopLeft.getX());
            worldGroup.setTranslateY(centerY - newTopLeft.getY());
            clipRectangle.setWidth(displayWidth);
            clipRectangle.setHeight(displayHeight);
            glassInteractiveLayer.setWidth(displayWidth);
            glassInteractiveLayer.setHeight(displayHeight);
            redrawCanvas();
            if (currentGeography != null) {
                currentGeography.getSectorsList().forEach(sector2D -> sector2D.updatePosition(true));
            }
            updateAircraftPosition(true);
            updateSlotsPosition(true);
        });
    }


    @Override
    public void setSimulationDataModel(DataModel simulationDataModel) {
        runLater(() -> {
            clean();
            createAircraft2D(simulationDataModel);
            createSlots2D(simulationDataModel);
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case DisplayOptionsManager.WAY_POINT_VISIBILITY:
                handleWptVisibilityChange(event);
                break;
            case DisplayOptionsManager.WAY_POINT_NAME_VISIBILITY:
                handleWptNameVisibilityChange(event);
                break;
            case DisplayOptionsManager.AIRCRAFT_VISIBILITY:
                handleAircraftVisibilityChange(event);
                break;
            case DisplayOptionsManager.AIRCRAFT_NAME_VISIBILITY:
                handleAircraftNameVisibilityChange(event);
                break;
            case DisplayOptionsManager.SLOT_VISIBILITY:
                handleSlotVisibilityChange(event);
                break;
            case DisplayOptionsManager.SLOT_NAME_VISIBILITY:
                handleSlotNameVisibilityChange(event);
                break;
            case DisplayOptionsManager.ROUTE_VISIBILITY:
                handleRouteVisibilityChange(event);
                break;
            case DisplayOptionsManager.AIRPORT_VISIBILITY:
                handleAirportVisibilityChange(event);
                break;
            case DisplayOptionsManager.SECTOR_VISIBILITY:
                handleSectorVisibilityChange(event);
                break;
            case DisplayOptionsManager.AIRCRAFT_GHOST_VISIBILITY:
                handleAircraftGhostVisibility(event);
                break;
            case DisplayOptionsManager.SLOT_GHOST_VISIBILITY:
                handleSlotGhostVisibility(event);
                break;
            case DisplayOptionsManager.DISPLAY_TYPE_CHANGED:
                handleDisplayOptionChanged();
                break;
            case DisplayOptionsManager.BACKGROUND_MAP_VISIBILITY:
                handleMapVisibility(event);
                break;
            case DisplayOptionsManager.SEGMENT_VISIBILITY:
                handleSegmentVisibilityChange(event);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported display property: " + event);
        }
    }

    private void clean() {
        aircraft2ds.clear();
        aircraftGroup.getChildren().clear();
        slot2Ds.clear();
        slotsGroup.getChildren().clear();
    }


    private void updateAircraftPosition(final boolean onResize) {
        aircraft2ds.forEach(aircraft2D -> aircraft2D.updatePosition(onResize));
    }

    private void updateSlotsPosition(final boolean onResize) {
        slot2Ds.forEach(slot2D -> slot2D.updatePosition(onResize));
    }


    //<editor-fold defaultstate="collapsed" desc="Canvas drawing methods">
    private void redrawCanvas() {
        backgroundContext.clearRect(0, 0, displayWidth, displayHeight);
        backgroundContext.beginPath();
        backgroundContext.setFill(Color.BLACK);
        backgroundContext.fillRect(0, 0, displayWidth, displayHeight);
        backgroundContext.closePath();
        //
        if (mapVisibility) {
            drawMap();
        }
        if (geography != null) {
            drawSectors();
            if (segmentVisibility) {
                drawSegments();
            }
        }
        //TODO store when ppty changed
        List<Route> routes = SimulationManager.getSimulationDataModel() != null ? SimulationManager.getSimulationDataModel().getMainRoutes() : Collections.emptyList();
        if (routeVisibility) {
            drawRoutes(routes);
        }
        if (geography != null) {
            if (airportVisibility) {
                drawAirports();
            }
            if (wpVisibility) {
                drawWaypoints();
            }
        }
    }

    private void drawMap() {
        double trX = worldGroup.getTranslateX();
        double trY = worldGroup.getTranslateY();
        backgroundContext.beginPath();
        // TODO introduce constant
        backgroundContext.setFill(new Color(0.14, 0.14, 0.14, 1));
        backgroundContext.setStroke(new Color(0.20, 0.20, 0.20, 1));
        backgroundContext.setLineWidth(1);
        MapDrawingUtils.getWorldGeography().getBackgroundRegions().forEach(region -> {
            int nbVertices = region.getVertices().size();
            double[] xPts = new double[nbVertices];
            double[] yPts = new double[nbVertices];
            for (int i = 0; i < nbVertices; i++) {
                Coordinates c = region.getVertices().get(i);
                final Point2D screenP = mercatorAttributes.getXYPosition(c.getLatitude(), c.getLongitude());
                xPts[i] = screenP.getX() + trX;
                yPts[i] = screenP.getY() + trY;
            }
            backgroundContext.fillPolygon(xPts, yPts, nbVertices);
            backgroundContext.strokePolygon(xPts, yPts, nbVertices);
        });
        backgroundContext.closePath();
    }

    private void drawSectors() {

    }

    private void drawSegments() {
        double trX = worldGroup.getTranslateX();
        double trY = worldGroup.getTranslateY();
        backgroundContext.beginPath();
        backgroundContext.setStroke(Color.GREY);
        backgroundContext.setLineWidth(0.5);
        backgroundContext.setLineDashes(2d, 5d);
        geography.getSegments().forEach(segment -> {
            Point2D xyStart = mercatorAttributes.getXYPosition(segment.getFromWaypoint().getLatitude(), segment.getFromWaypoint().getLongitude());
            Point2D xyEnd = mercatorAttributes.getXYPosition(segment.getToWaypoint().getLatitude(), segment.getToWaypoint().getLongitude());
            backgroundContext.moveTo(xyStart.getX() + trX, xyStart.getY() + trY);
            backgroundContext.lineTo(xyEnd.getX() + trX, xyEnd.getY() + trY);
        });
        backgroundContext.stroke();
        backgroundContext.closePath();
        backgroundContext.setLineDashes(1);
    }

    private void drawRoutes(List<Route> routes) {
        double trX = worldGroup.getTranslateX();
        double trY = worldGroup.getTranslateY();
        backgroundContext.beginPath();
        backgroundContext.setStroke(Color.GREY);
        backgroundContext.setLineWidth(0.8);
        routes.forEach(route -> {
            Point2D xyStart;
            Point2D xyEnd;
            ATCNode start;
            ATCNode end;
            for (int i = 0; i < route.getRoute().size() - 1; i++) {
                start = route.getRoute().get(i);
                end = route.getRoute().get(i + 1);
                xyStart = mercatorAttributes.getXYPosition(start.getLatitude(), start.getLongitude());
                xyEnd = mercatorAttributes.getXYPosition(end.getLatitude(), end.getLongitude());
                backgroundContext.moveTo(xyStart.getX() + trX, xyStart.getY() + trY);
                backgroundContext.lineTo(xyEnd.getX() + trX, xyEnd.getY() + trY);
            }
        });
        backgroundContext.stroke();
        backgroundContext.closePath();
    }

    private void drawAirports() {
        double halfPI = Math.PI / 2.0;
        double length = 8;
        double trX = worldGroup.getTranslateX();
        double trY = worldGroup.getTranslateY();
        backgroundContext.beginPath();
        backgroundContext.setStroke(Color.DODGERBLUE);
        backgroundContext.setFill(Color.CHOCOLATE);
        backgroundContext.setLineWidth(1);
        geography.getAirports().forEach(airport -> {
                    if (airport.getRunways().isEmpty()) {
                        Point2D pos = mercatorAttributes.getXYPosition(airport.getLatitude(), airport.getLongitude());
                        double[] edgesX = {pos.getX()+trX, pos.getX() + POLYGON_RADIUS+trX, pos.getX() - POLYGON_RADIUS+trX};
                        double[] edgesY = {pos.getY() - POLYGON_RADIUS+trY, pos.getY() + POLYGON_RADIUS+trY, pos.getY() + POLYGON_RADIUS+trY};
                        backgroundContext.fillPolygon(edgesX, edgesY, 3);
                    } else {

                        airport.getRunways().forEach(runway -> {
                            Point2D xyPosition = mercatorAttributes.getXYPosition(runway.getLatitude(), runway.getLongitude());
                            backgroundContext.moveTo(
                                    xyPosition.getX() - length * Math.cos(runway.getQFU() * 10 * Constants.DEG_TO_RAD + halfPI) + trX,
                                    xyPosition.getY() - length * Math.sin(runway.getQFU() * 10 * Constants.DEG_TO_RAD + halfPI) + trY);
                            backgroundContext.lineTo(
                                    xyPosition.getX() + length * Math.cos(runway.getQFU() * 10 * Constants.DEG_TO_RAD + halfPI) + trX,
                                    xyPosition.getY() + length * Math.sin(runway.getQFU() * 10 * Constants.DEG_TO_RAD + halfPI) + trY);
                        });
                    }
                }
        );
        backgroundContext.stroke();
        backgroundContext.closePath();
    }

    private void drawWaypoints() {
        double trX = worldGroup.getTranslateX();
        double trY = worldGroup.getTranslateY();
        double radius = 3;
        double fixRadius = radius * 2;
        backgroundContext.beginPath();
        backgroundContext.setFill(Color.LIGHTGREY);
        backgroundContext.setFont(new Font(10));
        geography.getWaypoints().forEach(wpt -> {
            Point2D xyPosition = mercatorAttributes.getXYPosition(wpt.getLatitude(), wpt.getLongitude());
            double x = xyPosition.getX() + trX;
            double y = xyPosition.getY() + trY;
            if (wpt.isMeterFix()) {
                backgroundContext.closePath();
                backgroundContext.beginPath();
                backgroundContext.setFill(Color.ORANGERED);
                double[] edgesX = {x, x + fixRadius, x - fixRadius};
                double[] edgesY = {y - fixRadius, y + fixRadius, y + fixRadius};
                backgroundContext.fillPolygon(edgesX, edgesY, 3);
                backgroundContext.closePath();
                backgroundContext.beginPath();
                backgroundContext.setFill(Color.LIGHTGREY);
            } else {
                double[] edgesX = {x, x + radius, x - radius};
                double[] edgesY = {y - radius, y + radius, y + radius};
                backgroundContext.fillPolygon(edgesX, edgesY, 3);
            }
            if (wpNameVisibility) {
                backgroundContext.fillText(wpt.getName(), x, y - 8);
            }
        });
        backgroundContext.closePath();
    }
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="2D primitives creation methods">
    private void createAircraft2D(DataModel model) {
        // create Plane representation
        model.getAllPlanes().forEach(plane -> {
            final Aircraft2D aircraft2d;
            if (model.hasGhostModel()) {
                aircraft2d = new Aircraft2D(plane, model.getGhostPlane(plane), mercatorAttributes);
            } else {
                aircraft2d = new Aircraft2D(plane, mercatorAttributes);
            }
            HighlightManager.registerAircraft2D(aircraft2d);
            aircraft2ds.add(aircraft2d);
            aircraftGroup.getChildren().add(aircraft2d.getNode());
        });
    }

    private void createSlots2D(DataModel model) {
        model.getSlots().forEach(slot -> {
            final Slot2D slot2d;
            if (model.hasGhostModel()) {
                slot2d = new Slot2D(slot, model.getGhostSlot(slot), mercatorAttributes);
            } else {
                slot2d = new Slot2D(slot, mercatorAttributes);
            }
            slot2Ds.add(slot2d);
            HighlightManager.registerSlot2D(slot2d);
            slotsGroup.getChildren().add(slot2d.getNode());
        });
    }
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Visibility methods">
    private void handleWptVisibilityChange(PropertyChangeEvent event) {
        wpVisibility = (boolean) event.getNewValue();
        redrawCanvas();
    }

    private void handleWptNameVisibilityChange(PropertyChangeEvent event) {
        wpNameVisibility = (boolean) event.getNewValue();
        redrawCanvas();
    }

    private void handleMapVisibility(PropertyChangeEvent event) {
        mapVisibility = (boolean) event.getNewValue();
        redrawCanvas();
    }

    private void handleSectorVisibilityChange(PropertyChangeEvent event) {
        final boolean sectorVisibility = (boolean) event.getNewValue();
        sectorsGroup.setVisible(sectorVisibility);
    }

    private void handleAircraftVisibilityChange(PropertyChangeEvent event) {
        final boolean aircraftVisibility = (boolean) event.getNewValue();
        aircraft2ds.forEach(aircraft2d -> aircraft2d.setVisible(aircraftVisibility));
    }

    private void handleAircraftNameVisibilityChange(PropertyChangeEvent event) {
        final boolean aircraftNameVisibility = (boolean) event.getNewValue();
        aircraft2ds.forEach(aircraft2d -> aircraft2d.setNameVisible(aircraftNameVisibility));
    }

    private void handleAircraftGhostVisibility(PropertyChangeEvent event) {
        final boolean aircraftGhostVisibility = (boolean) event.getNewValue();
        aircraft2ds.forEach(aircraft2d -> aircraft2d.setGhostVisible(aircraftGhostVisibility));
    }

    private void handleSlotVisibilityChange(PropertyChangeEvent event) {
        final boolean slotVisibility = (boolean) event.getNewValue();
        slot2Ds.forEach(slot2d -> slot2d.setVisible(slotVisibility));
    }

    private void handleSlotNameVisibilityChange(PropertyChangeEvent event) {
        final boolean slotNameVisibility = (boolean) event.getNewValue();
        slot2Ds.forEach(slot2d -> slot2d.setNameVisible(slotNameVisibility));
    }

    private void handleSlotGhostVisibility(PropertyChangeEvent event) {
        final boolean slotGhostVisibility = (boolean) event.getNewValue();
        slot2Ds.forEach(slot2d -> slot2d.setGhostVisible(slotGhostVisibility));
    }

    private void handleRouteVisibilityChange(PropertyChangeEvent event) {
        routeVisibility = (boolean) event.getNewValue();
        redrawCanvas();
    }

    private void handleSegmentVisibilityChange(PropertyChangeEvent event) {
        segmentVisibility = (boolean) event.getNewValue();
        redrawCanvas();
    }

    private void handleAirportVisibilityChange(PropertyChangeEvent event) {
        airportVisibility = (boolean) event.getNewValue();
        redrawCanvas();
    }
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Mouse events handling methods">
    private void handleBackgroundMousePressed(MouseEvent event) {
        lastXPressed = event.getX();
        lastYPressed = event.getY();
    }

    private void handleBackgroundMouseDragged(MouseEvent event) {
        double deltaX = event.getX() - lastXPressed;
        double deltaY = event.getY() - lastYPressed;
        worldGroup.setTranslateX(worldGroup.getTranslateX() + deltaX);
        worldGroup.setTranslateY(worldGroup.getTranslateY() + deltaY);
        lastXPressed = event.getX();
        lastYPressed = event.getY();
        redrawCanvas();
    }

    private void handleBackgroundMouseReleased(MouseEvent event) {
        LOG.log(Level.FINE, "handleBackgroundMouseReleased {0}", event);
    }

    private void handleMouseClicked(MouseEvent event) {
        LOG.log(Level.FINE, "handleMouseClicked {0}", event);
        Coordinates clickCoordinates = mercatorAttributes.getCoordinates(event.getX() - worldGroup.getTranslateX(), event.getY() - worldGroup.getTranslateY());
        System.err.println("Clicked at " + clickCoordinates + " -> ");
    }


    private void handleScrollEvent(ScrollEvent event) {
        double scaleFactor;
        if (event.getDeltaY() > 0) {
            scaleFactor = mercatorAttributes.getZoomRatio() * ZOOM_FACTOR;
        } else {
            scaleFactor = mercatorAttributes.getZoomRatio() / ZOOM_FACTOR;
        }
        updateAllPositions(event.getX(), event.getY(), scaleFactor);
    }
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Property change handling methods">
    private void handleGeographyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            // TODO remove previous elements
            case WorldGeography.ATC_GEOGRAPHY_ADDED:
                System.err.println(" //////////////// world geo added" + event);
                geography = (ATCGeography) event.getNewValue();
                loadGeography();
                break;

            case WorldGeography.ATC_GEOGRAPHY_CHANGED:
                // TODO remove duplicate
                break;
            case MapDrawingUtils.NEW_WORLD_GEOGRAPHY:
                System.err.println(" !!!!!!!!!!!! new world geo " + event);
                // TODO
                break;
            default:
                // TODO
                break;
        }
    }

    private void loadGeography() {
        currentGeography = new ATCGeography2D(geography, mercatorAttributes);
        sectorsGroup.getChildren().add(currentGeography.getSectors());
        updateMercatorAttributes(geography.getElements().stream().map(e -> new Coordinates(e.getLatitude(), e.getLongitude())).collect(Collectors.toList()));
        updateAllPositions(displayWidth / 2.0, displayHeight / 2.0, 1);
    }

    private void handleDisplayOptionChanged() {
        runLater(() -> {
            aircraft2ds.forEach(aircraft2d -> aircraft2d.setDisplayType(DisplayOptionsManager.getDisplayType()));
            slot2Ds.forEach(slot2d -> slot2d.setDisplayType(DisplayOptionsManager.getDisplayType()));
        });
    }

    private void handleSimulationTimeChange(PropertyChangeEvent event) {
        LOG.log(Level.FINE, "handleSimulationTimeChange on event {0}", event);
        runLater(() -> {
            updateAircraftPosition(false);
            updateSlotsPosition(false);
        });
    }
    //</editor-fold>

}
