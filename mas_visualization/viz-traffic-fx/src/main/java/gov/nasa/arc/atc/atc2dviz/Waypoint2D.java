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

import gov.nasa.arc.atc.geography.Waypoint;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import gov.nasa.arc.atc.viewer.DisplayOptionsManager;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import org.controlsfx.control.PopOver;

import java.beans.PropertyChangeEvent;

/**
 * @author hamon
 */
public class Waypoint2D implements Element2D {

    private static final Color DEFAULT_FONT_COLOR = Color.WHITESMOKE;
    private static final Color DEFAULT_FILL_COLOR = Color.LIGHTGRAY;
    private static final Color METER_FIX_FONT_COLOR = Color.ORANGERED;
    private static final Color METER_FIX_FILL_COLOR = Color.ORANGERED;
    private static final double POLYGON_RADIUS = 3;

    private final Group mainNode;
    //
    private final Waypoint waypoint;
    private final MercatorAttributes mercatorAttributes;
    //
    PopOver waypointPopOver = new PopOver();
    private final Label nameLabel;
    private final Polygon shape;
    private Point2D xyPosition;

    /**
     * @param aWaypoint
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Waypoint2D(Waypoint aWaypoint, MercatorAttributes mAttributes) {
        waypoint = aWaypoint;
        mercatorAttributes = mAttributes;
        // graphical attributes
        mainNode = new Group();
        nameLabel = new Label(waypoint.getName());
        shape = new Polygon();
        initWaypoint2D();
    }

    private void initWaypoint2D() {
        // name label
        nameLabel.setFont(new Font(10));
        nameLabel.setTranslateX(6);
        nameLabel.setTranslateY(-8);
        nameLabel.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(1), new Insets(0))));
        //
        if (waypoint.isMeterFix()) {
            nameLabel.setTextFill(METER_FIX_FONT_COLOR);
            // shape
            double radius = 2.0 * POLYGON_RADIUS;
            Double[] edges = {0.0, -radius, radius, radius, -radius, radius};
            shape.getPoints().setAll(edges);
            shape.setFill(METER_FIX_FILL_COLOR);
        } else {
            nameLabel.setTextFill(DEFAULT_FONT_COLOR);
            // shape
            Double[] edges = {0.0, -POLYGON_RADIUS, POLYGON_RADIUS, POLYGON_RADIUS, -POLYGON_RADIUS, POLYGON_RADIUS};
            shape.getPoints().setAll(edges);
            shape.setFill(DEFAULT_FILL_COLOR);
        }
        // adding children
        mainNode.getChildren().add(shape);
        mainNode.getChildren().add(nameLabel);
        //
        setNameVisible(DisplayOptionsManager.getDisplayOption(DisplayOptionsManager.WAY_POINT_NAME_VISIBILITY));
        setVisible(DisplayOptionsManager.getDisplayOption(DisplayOptionsManager.WAY_POINT_VISIBILITY));

        addInteractivity();
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public void updatePosition(final boolean onScreenResize) {
        // the onScreenResize parameter is not necessary, calculations are identical
        xyPosition = mercatorAttributes.getXYPosition(waypoint.getLatitude(), waypoint.getLongitude());
        mainNode.setTranslateX(xyPosition.getX());
        mainNode.setTranslateY(xyPosition.getY());

    }

    @Override
    public void propertyChange(PropertyChangeEvent arg0) {
        // A way point is not responsive (yet)
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        if (highlighted) {
            updateColor(HighlightManager.HIGHLIGHT_COLOR);
        } else {
            updateColor(HighlightManager.WHITE_COLOR);
        }
    }

    private void updateColor(Color color) {
        shape.setFill(color);
        this.setNameVisible(true);
    }

    @Override
    public void setVisible(boolean visibility) {
        mainNode.setVisible(visibility);
    }

    public void setNameVisible(boolean visibility) {
        nameLabel.setVisible(visibility);
    }

    public String getName() {
        return waypoint.getName();
    }

    //click to display name
    private void addInteractivity() {
        shape.setOnMouseClicked(this::handleMouseClick);
    }

    private void handleMouseClick(MouseEvent event) {
        if (nameLabel.isVisible()) {
            this.setNameVisible(false);
        } else {
            this.setNameVisible(true);
        }
    }
}
