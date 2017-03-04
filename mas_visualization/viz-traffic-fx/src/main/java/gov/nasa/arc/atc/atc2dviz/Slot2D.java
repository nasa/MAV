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

package gov.nasa.arc.atc.atc2dviz;

import gov.nasa.arc.atc.DisplayType;
import gov.nasa.arc.atc.core.NewSlot;
import gov.nasa.arc.atc.utils.*;
import gov.nasa.arc.atc.viewer.DisplayOptionsManager;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import org.controlsfx.control.PopOver;

import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 */
public class Slot2D implements Element2D {

    private static final Logger LOG = Logger.getGlobal();

    private static final Color DEFAULT_COLOR = Color.PALEGOLDENROD;
    private static final double DEBUG_RADIUS = 8;
    private static final double LABEL_TRANSLATE = 3;
    private static final double DEFAULT_CIRCLE_WIDTH = 0.8;
    private static final double DEFAULT_CENTER_RADIUS = 1.5;
    private static final double HIGHLIGHT_CIRCLE_WIDTH = 2.5;

    // could be done via css
    private final Font defaultFont = new Font(10);

    private final NewSlot slot;
    private final NewSlot ghost;
    private final boolean withGhost;
    //
    private final MercatorAttributes mercatorAttributes;
    //
    private final Group mainNode;
    // main slot
    private final Circle slotDrawing;
    private final Circle center;
    private final Label slotName;
    // ghost slot
    private Circle ghostDrawing;
    private Line ghostLink;

    //
    private final PopOver slotInfoPopOver;
    //
    private boolean display = true;
    private boolean visibility = false;
    //
    private PopOverSlotContent popOverSlotContent;
    private DisplayType slotDisplayType;
    private boolean slotNameVisibility = true;

    /**
     * @param mainSlot    the slot from the main data model
     * @param ghostSlot   the slot form the ghost model
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Slot2D(NewSlot mainSlot, NewSlot ghostSlot, MercatorAttributes mAttributes) {
        slot = mainSlot;
        ghost = ghostSlot;
        withGhost = ghostSlot != null;
        slot.addPropertyChangeListener(Slot2D.this);
        //
        mercatorAttributes = mAttributes;
        //
        slotInfoPopOver = new PopOver();
        //
        mainNode = new Group();
        slotDrawing = new Circle();
        center = new Circle();
        slotName = new Label(slot.getSimpleName());
        runLater(Slot2D.this::initSlot2D);
    }

    /**
     * creates a JavaFX 2D slot representation
     *
     * @param mainSlot    the slot to represent
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Slot2D(NewSlot mainSlot, MercatorAttributes mAttributes) {
        this(mainSlot, null, mAttributes);
    }

    private void initSlot2D() {
        slotDrawing.setRadius(DEBUG_RADIUS);
        slotDrawing.setStrokeWidth(DEFAULT_CIRCLE_WIDTH);
        slotDrawing.setFill(null);
        slotDrawing.setStroke(DEFAULT_COLOR);
        //
        slotName.setTextFill(DEFAULT_COLOR);
        slotName.setTranslateX(LABEL_TRANSLATE);
        slotName.setFont(defaultFont);
        //
        center.setFill(DEFAULT_COLOR);
        center.setRadius(DEFAULT_CENTER_RADIUS);
        //
        if (withGhost) {
            ghostDrawing = new Circle();
            ghostDrawing.setRadius(DEBUG_RADIUS);
            ghostDrawing.setStrokeWidth(DEFAULT_CIRCLE_WIDTH);
            ghostDrawing.setFill(null);
            ghostDrawing.setStroke(DEFAULT_COLOR);
            ghostDrawing.setOpacity(0.5);
            ghostLink = new Line();
            ghostLink.setStroke(DEFAULT_COLOR);
            ghostLink.setOpacity(0.5);
            mainNode.getChildren().add(ghostDrawing);
            mainNode.getChildren().add(ghostLink);
        }
        //
        mainNode.getChildren().add(center);
        mainNode.getChildren().add(slotDrawing);
        mainNode.getChildren().add(slotName);
        //
        slotInfoPopOver.setAutoHide(false);
        slotInfoPopOver.setHideOnEscape(false);
        slotInfoPopOver.setTitle("SLOT: " + slot.getFullName());
        popOverSlotContent = PopOverFactory.createPopOverSlotContent(slotInfoPopOver, slot);
        slotInfoPopOver.setContentNode(popOverSlotContent.getNode());
        slotInfoPopOver.setOnHidden(event -> setColor(DEFAULT_COLOR));
        //
        updatePosition(true);
        updateVisibility();
        initSlotInteractivity();
        setDisplayType(DisplayOptionsManager.getDisplayType());
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public void setVisible(boolean toDisplay) {
        display = toDisplay;
        runLater(this::updateVisibility);
    }

    void setNameVisible(boolean visibility) {
        slotNameVisibility = visibility;
        setDisplayType(slotDisplayType);
    }

    String getSlotName() {
        return slot.getFullName();
    }

    @Override
    public void updatePosition(final boolean onScreenResize) {
        // the onScreenResize parameter is not necessary, calculations are identical
        final Point2D xyPos = mercatorAttributes.getXYPosition(slot.getLatitude(), slot.getLongitude());
        final Point2D xyPosRadius = calculatePos30s(slot);
        if (visibility != slot.getSimTime() >= slot.getStartTime() && slot.getSimTime() <= Math.max(slot.getEta(), slot.getLastUpdateTime())) {
            visibility = !visibility;
            updateVisibility();
        }
        //
        mainNode.setTranslateX(xyPos.getX());
        mainNode.setTranslateY(xyPos.getY());
        //
        if (withGhost) {
            final Point2D xyGhostPos = mercatorAttributes.getXYPosition(ghost.getLatitude(), ghost.getLongitude());
            final Point2D xyGhostRadius = calculatePos30s(ghost);
            double ghostX = xyGhostPos.getX() - xyPos.getX();
            double ghostY = xyGhostPos.getY() - xyPos.getY();
            ghostDrawing.setCenterX(ghostX);
            ghostDrawing.setCenterY(ghostY);
            ghostLink.setEndX(ghostX);
            ghostLink.setEndY(ghostY);
            ghostDrawing.setRadius(xyGhostRadius.getX() - xyGhostPos.getX());
        }
        //
        slotDrawing.setRadius(xyPosRadius.getX() - xyPos.getX());
        slotName.setTranslateX(xyPosRadius.getX() - xyPos.getX() + LABEL_TRANSLATE);
        //
        if (popOverSlotContent != null) {
            popOverSlotContent.updateInfos();
        } else {
            LOG.log(Level.WARNING, "pop up content is null for slot {0}", slot.getFullName());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // nothing to do
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        if (highlighted) {
            setColor(HighlightManager.HIGHLIGHT_COLOR);
            slotDrawing.setStrokeWidth(HIGHLIGHT_CIRCLE_WIDTH);
        } else {
            setColor(DEFAULT_COLOR);
            slotDrawing.setStrokeWidth(DEFAULT_CIRCLE_WIDTH);
        }

    }

    private void initSlotInteractivity() {
        slotName.setOnMouseClicked(event -> {
            LOG.log(Level.INFO, "Name clicked for slot {0} via event {1}", new Object[]{slot.getFullName(), event});
            if (slotInfoPopOver.isShowing()) {
                slotInfoPopOver.hide();
                setColor(DEFAULT_COLOR);
            } else {
                setColor(ColorFXFactory.getInteractiveColor());
                slotInfoPopOver.show(slotName);
            }
        });
    }

    private void setColor(Color color) {
        center.setFill(color);
        slotDrawing.setStroke(color);
        slotName.setTextFill(color);
        popOverSlotContent.setFillColor(color);
    }

    private void updateVisibility() {
        mainNode.setVisible(visibility && display);
    }

    private Point2D calculatePos30s(NewSlot s) {
        double leadSpeed = s.getSpeed();
        double altitude = s.getAltitude();
        double tAS = Aerodynamics.trueAirSpeedISA(altitude * Constants.FT2METER, leadSpeed * Constants.KTS2MS);
        tAS /= Constants.KTS2MS;
        double slotRadiusNm = (tAS * 30) / Constants.NM2METER;
        // Angular Distance in radiant
        double dAngDist = CalculationTools.calculateAngularDistance(slotRadiusNm, altitude);
        // New latitude in degrees
        double lat2 = CalculationTools.newLatitude(s.getLatitude(), dAngDist, 90);
        // New long. in degrees
        double long2 = CalculationTools.newLongitude(s.getLatitude(), s.getLongitude(), lat2, dAngDist, 90);
        return mercatorAttributes.getXYPosition(lat2, long2);
    }

    void setGhostVisible(boolean slotGhostVisibility) {
        if (withGhost) {
            ghostDrawing.setVisible(slotGhostVisibility);
            ghostLink.setVisible(slotGhostVisibility);
        }
    }

    final void setDisplayType(DisplayType displayType) {
        slotDisplayType = displayType;
        switch (slotDisplayType) {
            case CONTROLLER:
                slotName.setVisible(slotNameVisibility);
                slotName.setText(Integer.toString((int) slot.getSpeed()));
                break;
            case NAME_ONLY:
                slotName.setText(slot.getSimpleName());
                slotName.setVisible(slotNameVisibility);
                break;
            case SUPERVISOR:
                slotName.setVisible(false);
                break;
            default:
                throw new UnsupportedOperationException("display mode: " + displayType);
        }
    }
}
