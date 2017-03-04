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

import gov.nasa.arc.atc.DisplayType;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.utils.ColorFXFactory;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import gov.nasa.arc.atc.viewer.DisplayOptionsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;

import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author hamon
 */
public class Aircraft2D implements Element2D {

    private static final Logger LOG = Logger.getLogger(Aircraft2D.class.getName());

    private static final double DEFAULT_ECHO_SIZE = 6;
    private static final double HIGHLIGHT_ECHO_SIZE = DEFAULT_ECHO_SIZE * 2;
    private static final Color DEFAULT_COLOR = Color.CHARTREUSE;
    private static final Color DEBUG_COLOR = Color.PINK;
    private static final double DEFAULT_TRAJECTORY_WIDTH = 2.0;

    private static final double LABEL_TRX = 2.0 * DEFAULT_ECHO_SIZE;
    private static final double LABEL_TRY = 8.0;

    // could be done via css
    private final Font defaultFont = new Font(10);

    private final NewPlane plane;
    private final NewPlane ghost;
    private final boolean hasGhost;

    private final MercatorAttributes mercatorAttributes;

    private String controllerName = "";
    //
    private final Group mainNode;
    private final Group tagGroup;
    private final Polyline trajectory;
    // MAIN ECHO
    private final Group simpleNameTagNode;
    private final Rectangle echo;
    private final Text nameText;
    private final ControllerTag controllerTag;
    // GHOST ECHO
    private Rectangle ghostEcho;

    private Point2D xyPosition;
    private Point2D xyGhostPosition;
    //
    private final PopOver aircraftInfoPopOver;
    private PopOverAFOContent popOverAFOContent;
    //
    private boolean display = true;
    private boolean visibility = false;

    /**
     * @param mainPlane   the aircraft from the main data model
     * @param ghostPlane  the corresponding aircraft from the ghost data model
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Aircraft2D(NewPlane mainPlane, NewPlane ghostPlane, MercatorAttributes mAttributes) {
        plane = mainPlane;
        ghost = ghostPlane;
        hasGhost = ghost != null;
        //
        mercatorAttributes = mAttributes;
        //
        mainNode = new Group();
        tagGroup = new Group();
        trajectory = new Polyline();
        simpleNameTagNode = new Group();
        echo = new Rectangle();
        nameText = new Text();
        controllerTag = new ControllerTag(plane);
        //
        xyPosition = new Point2D(0, 0);
        //
        aircraftInfoPopOver = new PopOver();
        initGraphicPrimitives();
        initInteractivity();
        setDisplayType(DisplayOptionsManager.getDisplayType());
    }

    /**
     * @param mainPlane   the aircraft from the main data model
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Aircraft2D(NewPlane mainPlane, MercatorAttributes mAttributes) {
        this(mainPlane, null, mAttributes);
    }

    private void initGraphicPrimitives() {
        setEchoSize(DEFAULT_ECHO_SIZE);
        echo.setFill(DEFAULT_COLOR);
        //
        nameText.setText(plane.getSimpleName());
        nameText.setFont(defaultFont);
        nameText.setTranslateX(LABEL_TRX);
        nameText.setTranslateY(LABEL_TRY);
        nameText.setFontSmoothingType(FontSmoothingType.LCD);
        nameText.setSmooth(true);
        nameText.setFill(DEFAULT_COLOR);
        //
        trajectory.setFill(null);
        trajectory.setStroke(HighlightManager.HIGHLIGHT_COLOR);
        trajectory.setStrokeWidth(DEFAULT_TRAJECTORY_WIDTH);
        trajectory.setVisible(false);
        mainNode.getChildren().add(trajectory);
        //
        if (hasGhost) {
            ghostEcho = new Rectangle();
            ghostEcho.setWidth(DEFAULT_ECHO_SIZE);
            ghostEcho.setHeight(DEFAULT_ECHO_SIZE);
            ghostEcho.setX(-DEFAULT_ECHO_SIZE / 2.0);
            ghostEcho.setY(-DEFAULT_ECHO_SIZE / 2.0);
            ghostEcho.setFill(DEFAULT_COLOR);
            ghostEcho.setOpacity(0.5);
            mainNode.getChildren().add(ghostEcho);
        }
        //
        tagGroup.getChildren().add(simpleNameTagNode);
        tagGroup.getChildren().add(controllerTag.getNode());
        mainNode.getChildren().add(tagGroup);
        mainNode.getChildren().add(echo);
        simpleNameTagNode.getChildren().add(nameText);
        //
        aircraftInfoPopOver.setAutoHide(false);
        aircraftInfoPopOver.setHideOnEscape(false);
        aircraftInfoPopOver.setTitle("Aircraft: " + plane.getSimpleName());
        popOverAFOContent = PopOverFactory.createPopOverAFOContent(aircraftInfoPopOver, plane);
        aircraftInfoPopOver.setContentNode(popOverAFOContent.getNode());
        aircraftInfoPopOver.setOnShown(event -> popOverAFOContent.setFillColor(ColorFXFactory.getColor()));
        aircraftInfoPopOver.setOnHidden(event -> popOverAFOContent.setFillColor(DEBUG_COLOR));
        //
        updatePosition(true);
        updateColor(ColorFXFactory.getATCColor(controllerName));
    }

    private void initInteractivity() {
        nameText.setOnMouseClicked(event -> {
            LOG.log(Level.INFO, "Name clicked for plane {0} via event {1}", new Object[]{plane.getFullName(), event});
            if (aircraftInfoPopOver.isShowing()) {
                aircraftInfoPopOver.hide();
                updateColor(ColorFXFactory.getATCColor(controllerName));
            } else {
                updateColor(ColorFXFactory.getInteractiveColor());
                aircraftInfoPopOver.show(nameText);
            }
        });
    }

    //////////////////////////////
    // pop up from simulation link
    public void openPopUp() {
        updateColor(ColorFXFactory.getInteractiveColor());
        aircraftInfoPopOver.show(nameText);
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // nothing to do
    }

    @Override
    public void updatePosition(final boolean onScreenResize) {
        xyPosition = mercatorAttributes.getXYPosition(plane.getLatitude(), plane.getLongitude());

        echo.setX(xyPosition.getX() - echo.getWidth() / 2.0);
        echo.setY(xyPosition.getY() - echo.getHeight() / 2.0);
        simpleNameTagNode.setTranslateX(xyPosition.getX());
        simpleNameTagNode.setTranslateY(xyPosition.getY());
        if (!controllerName.equals(plane.getController())) {
            controllerName = plane.getController();
            updateColor(ColorFXFactory.getATCColor(controllerName));
        }
        if (hasGhost) {
            xyGhostPosition = mercatorAttributes.getXYPosition(ghost.getLatitude(), ghost.getLongitude());
            ghostEcho.setX(xyGhostPosition.getX() - DEFAULT_ECHO_SIZE / 2.0);
            ghostEcho.setY(xyGhostPosition.getY() - DEFAULT_ECHO_SIZE / 2.0);
        }
        //
        controllerTag.update(xyPosition);
        //
        if (visibility != plane.getSimTime() >= plane.getStartTime() && plane.getSimTime() <= Math.max(plane.getEta(), plane.getLastUpdateTime())) {
            visibility = !visibility;
            updateVisibility();
        }
        if (onScreenResize) {
            ObservableList<Double> points = FXCollections.observableArrayList();
            SimulationManager.getSimulationDataModel().getAllDataUpdates().get(plane.getFullName()).forEach((time, update) -> {
                Point2D position = mercatorAttributes.getXYPosition(update.getPosition().getLatitude(), update.getPosition().getLongitude());
                points.add(position.getX());
                points.add(position.getY());
            });
            trajectory.getPoints().setAll(points);
        }
        popOverAFOContent.updateInfos();
    }

    @Override
    public void setVisible(boolean visibility) {
        display = visibility;
        updateVisibility();
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        if (highlighted) {
            updateColor(HighlightManager.HIGHLIGHT_COLOR);
            setEchoSize(HIGHLIGHT_ECHO_SIZE);
            trajectory.setVisible(true);
        } else {
            updateColor(ColorFXFactory.getATCColor(controllerName));
            setEchoSize(DEFAULT_ECHO_SIZE);
            trajectory.setVisible(false);
        }
    }

    String getAircraftName() {
        return plane.getFullName();
    }

    void setNameVisible(boolean visibility) {
        tagGroup.setVisible(visibility);
    }

    void setGhostVisible(boolean aircraftsGhostVisibility) {
        if (hasGhost) {
            ghostEcho.setVisible(aircraftsGhostVisibility);
        }
    }

    private void updateVisibility() {
        mainNode.setVisible(display && visibility);
    }

    private void updateColor(Color c) {
        echo.setFill(c);
        nameText.setFill(c);
        controllerTag.setColor(c);
        popOverAFOContent.setFillColor(c);
    }

    private void setEchoSize(double size) {
        echo.setWidth(size);
        echo.setHeight(size);
        echo.setX(xyPosition.getX() - echo.getWidth() / 2.0);
        echo.setY(xyPosition.getY() - echo.getHeight() / 2.0);
    }

    void setDisplayType(DisplayType displayType) {
        switch (displayType) {
            case CONTROLLER:
                simpleNameTagNode.setVisible(false);
                controllerTag.setVisible(true);
                break;
            case NAME_ONLY:
                simpleNameTagNode.setVisible(true);
                controllerTag.setVisible(false);
                break;
            case SUPERVISOR:
                simpleNameTagNode.setVisible(false);
                controllerTag.setVisible(false);
                break;
            default:
                throw new UnsupportedOperationException("display mode: " + displayType);
        }
    }

    private static class ControllerTag {

        private static final double DEFAULT_LABEL_HEIGHT = 20;
        private static final double PADDING = 5;

        // could be done via css
        private final Font defaultFont = new Font(10);

        private final NewPlane plane;

        private final Group controllerTagNode;
        private final Line line;
        private final Label nameText;
        private final Label altitudeText;
        private final Label speedText;
        private final Label destinationText;

        public ControllerTag(NewPlane aPlane) {
            plane = aPlane;
            controllerTagNode = new Group();
            line = new Line();
            nameText = new Label(plane.getSimpleName());
            altitudeText = new Label();
            speedText = new Label();
            destinationText = new Label(plane.getDestination());
            initControllerTag();
        }

        private void initControllerTag() {
            //
            nameText.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
            altitudeText.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
            speedText.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
            destinationText.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
            //
            nameText.setPrefHeight(DEFAULT_LABEL_HEIGHT);
            altitudeText.setPrefHeight(DEFAULT_LABEL_HEIGHT);
            speedText.setPrefHeight(DEFAULT_LABEL_HEIGHT);
            destinationText.setPrefHeight(DEFAULT_LABEL_HEIGHT);
            //
            nameText.setFont(defaultFont);
            altitudeText.setFont(defaultFont);
            speedText.setFont(defaultFont);
            destinationText.setFont(defaultFont);
            //
            controllerTagNode.getChildren().addAll(line, nameText, altitudeText, speedText, destinationText);

        }

        private void update(Point2D echoCenter) {
            altitudeText.setText("Alt: " + Integer.toString((int) plane.getAltitude()));
            speedText.setText("Speed: " + Integer.toString((int) plane.getSpeed()));
            //
            nameText.setTranslateX(echoCenter.getX() + PADDING);
            altitudeText.setTranslateX(echoCenter.getX() + PADDING);
            speedText.setTranslateX(echoCenter.getX() + PADDING);
            destinationText.setTranslateX(echoCenter.getX() + PADDING);
            //
            nameText.setTranslateY(echoCenter.getY() - PADDING - 5.0 * DEFAULT_LABEL_HEIGHT);
            altitudeText.setTranslateY(echoCenter.getY() - PADDING - 4.0 * DEFAULT_LABEL_HEIGHT);
            speedText.setTranslateY(echoCenter.getY() - PADDING - 3.0 * DEFAULT_LABEL_HEIGHT);
            destinationText.setTranslateY(echoCenter.getY() - PADDING - 2.0 * DEFAULT_LABEL_HEIGHT);
            //
            line.setStartX(echoCenter.getX());
            line.setEndX(echoCenter.getX());
            line.setStartY(echoCenter.getY() - PADDING - DEFAULT_LABEL_HEIGHT);
            line.setEndY(echoCenter.getY() - PADDING - 5.0 * DEFAULT_LABEL_HEIGHT);
        }

        private Node getNode() {
            return controllerTagNode;
        }

        private void setVisible(boolean visibility) {
            controllerTagNode.setVisible(visibility);
        }

        private void setColor(Color c) {
            line.setStroke(c);
            nameText.setTextFill(c);
            altitudeText.setTextFill(c);
            speedText.setTextFill(c);
            destinationText.setTextFill(c);
        }

    }
}
