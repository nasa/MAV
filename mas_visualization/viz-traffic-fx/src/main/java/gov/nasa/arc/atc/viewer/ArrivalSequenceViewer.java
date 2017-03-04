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

import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.algos.dsas.ArrivalGapType;
import gov.nasa.arc.atc.algos.dsas.NamedArrivalGap;
import gov.nasa.arc.atc.core.CoreComparator;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.core.NewSlot;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.utils.GapUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 */
public class ArrivalSequenceViewer {

    protected static final double PADDING = 10;
    protected static final double LABEL_PADDING = 5;
    protected static final double MAJOR_LINE_SPACING = 20;
    //
    private static final Color DEFAULT_LINE_COLOR = Color.WHITESMOKE;
    private static final Color INTERACTIVE_LINE_COLOR = Color.CHARTREUSE;
    private static final Color DEFAULT_AIRPORT_COLOR = Color.DARKGRAY;
    private static final Font DEFAULT_FONT = new Font(10);

    private static final Logger LOG = Logger.getGlobal();

    /**
     * Max arrival time represented in [MIN]
     */
    private static final double MAX_ARRIVAL_TIME = 60;
    private static final double MIN_ARRIVAL_TIME = 10;
    private static final double SCROLL_AMOUNT_ARRIVAL_TIME = 5;

    private Map<Integer, List<NamedArrivalGap>> allGaps;

    // Useful?
    private final Group mainNode;
    private final Line leftVLine;
    private final Line rightVLine;
    private final Line upperHorizontalLine;
    private final Line lowerHorizontalLine;
    private final Text etaText;
    private final Text airportText;
    private final Text staText;
    private final Group gapGroup;
    private final Group etaGroup;
    private final Map<Integer, Line> etaLines;
    private final Map<Integer, Label> etaLabels;
    private final Rectangle scaleForeground;
    private final List<Rectangle> gapRectangles;
    private final Rectangle firstGapRectangle;
    // clipping
    private final Rectangle clip;
    //
    private final Map<NewSlot, SlotEtaTag> slotEtaTags;
    private final Map<NewPlane, AircraftEtaTag> planeEtaTags;
    //
    private double width = 0;
    private double height = 0;
    private double columnWidth = 1;
    // trick
    private double footerTextWidth;
    private double footerTextHeight;
    private double secondsToPixelRatio;
    // remove? and use property change ?
    private int simulationTime = 0;

    private double currentMaxTime = MAX_ARRIVAL_TIME;

    /**
     * Creates an empty arrival sequence viewer
     */
    public ArrivalSequenceViewer() {
        mainNode = new Group();
        leftVLine = new Line();
        rightVLine = new Line();
        upperHorizontalLine = new Line();
        lowerHorizontalLine = new Line();
        etaText = new Text("ETA");
        airportText = new Text("XXX");
        staText = new Text("STA");
        scaleForeground = new Rectangle();
        gapGroup = new Group();
        etaGroup = new Group();
        etaLines = new HashMap<>();
        etaLabels = new HashMap<>();
        gapRectangles = new ArrayList<>();
        firstGapRectangle = new Rectangle();
        //
        clip = new Rectangle();
        //
        slotEtaTags = new HashMap<>();
        planeEtaTags = new HashMap<>();
        //
        initArrivalSequenceViewer();
        SimulationManager.addPropertyChangeListener(this::handleSimulationChanged);
    }

	/*
     * INIT
	 */

    private void initArrivalSequenceViewer() {
        leftVLine.setStroke(DEFAULT_LINE_COLOR);
        rightVLine.setStroke(DEFAULT_LINE_COLOR);
        upperHorizontalLine.setStroke(DEFAULT_LINE_COLOR);
        lowerHorizontalLine.setStroke(DEFAULT_LINE_COLOR);
        etaText.setStroke(DEFAULT_LINE_COLOR);
        airportText.setStroke(DEFAULT_AIRPORT_COLOR);
        staText.setStroke(DEFAULT_LINE_COLOR);
        //
        for (int i = 0; i < MAX_ARRIVAL_TIME; i++) {
            if ((i % 5) != 0 && i != 0) {
                Line line = new Line();
                line.setStroke(DEFAULT_LINE_COLOR);
                etaLines.put(i, line);
            } else {
                final Label etaLabel = new Label(Integer.toString(i));
                etaLabel.setTextFill(DEFAULT_LINE_COLOR);
                etaLabel.setPrefWidth(MAJOR_LINE_SPACING);
                etaLabel.setMinWidth(MAJOR_LINE_SPACING);
                etaLabel.setAlignment(Pos.CENTER);
                etaLabel.setFont(DEFAULT_FONT);
                etaLabels.put(i, etaLabel);
            }
        }
        //
        firstGapRectangle.setVisible(false);
        //
        etaText.setFontSmoothingType(FontSmoothingType.LCD);
        airportText.setFontSmoothingType(FontSmoothingType.LCD);
        staText.setFontSmoothingType(FontSmoothingType.LCD);
        //
        scaleForeground.setFill(Color.PINK);
        scaleForeground.setOpacity(0.0);
        scaleForeground.setWidth(MAJOR_LINE_SPACING);
        //
        initInteractivity();
        //
        addToMainNode();
        //
        planeEtaTags.forEach((plane, tag) -> tag.getNode().setVisible(false));
        slotEtaTags.forEach((slot, tag) -> tag.getNode().setVisible(false));
        mainNode.setClip(clip);
    }

    private void initInteractivity() {
        scaleForeground.setOnScroll(this::handleZoomEvent);
        scaleForeground.setOnMouseEntered(this::handleTimeScaleMouseEntered);
        scaleForeground.setOnMouseExited(this::handleTimeScaleMouseExited);
    }

    private void addToMainNode() {
        mainNode.getChildren().add(leftVLine);
        mainNode.getChildren().add(rightVLine);
        mainNode.getChildren().add(upperHorizontalLine);
        mainNode.getChildren().add(lowerHorizontalLine);
        mainNode.getChildren().add(etaText);
        mainNode.getChildren().add(airportText);
        mainNode.getChildren().add(staText);
        etaLabels.forEach((index, label) -> mainNode.getChildren().add(label));
        etaLines.forEach((index, line) -> mainNode.getChildren().add(line));
        mainNode.getChildren().add(scaleForeground);
        gapGroup.getChildren().add(firstGapRectangle);
        mainNode.getChildren().add(gapGroup);
        mainNode.getChildren().add(etaGroup);
    }

	/*
	 * END INIT
	 */

    public Node getNode() {
        return mainNode;
    }

    protected void updateSize(double newWidth, double newHeight) {
        width = newWidth;
        height = newHeight;
        columnWidth = (width - MAJOR_LINE_SPACING - 2.0 * PADDING) / 2.0;
        clip.setWidth(width);
        clip.setHeight(height);
        updateGraphicsPosition();
        updateTimes(simulationTime);
    }

    private void updateGraphicsPosition() {
        // update footer,
        // to do first as it is a reference for other graphical primitives
        updateFooter();
        updateArrivalHorizontalLines();
        // vertical lines
        updateVerticalLines();
        // calculate radio pixel distance
        calculateNmToPixelsRatio();
        // update the distance labels
        updateDistanceLabels();
        updateDistanceLines();
        // update ETAs
        slotEtaTags.forEach((slot, tag) -> tag.setWidth(columnWidth));
        planeEtaTags.forEach((plane, tag) -> tag.setWidth(columnWidth));
        //
        scaleForeground.setX(leftVLine.getStartX());
        scaleForeground.setY(leftVLine.getStartY());
        scaleForeground.setHeight(leftVLine.getEndY() - leftVLine.getStartY());
    }

    private void updateFooter() {
        footerTextWidth = staText.getLayoutBounds().getWidth();
        footerTextHeight = staText.getLayoutBounds().getHeight();
        // X
        etaText.setTranslateX((width - MAJOR_LINE_SPACING) / 4.0 - footerTextWidth / 2.0 + PADDING);
        airportText.setTranslateX((width - MAJOR_LINE_SPACING - footerTextWidth) / 2.0 + PADDING);
        staText.setTranslateX(3.0 * (width - MAJOR_LINE_SPACING) / 4.0 - footerTextWidth / 2.0 + PADDING);
        // Y
        double y = height - PADDING;
        etaText.setTranslateY(y);
        airportText.setTranslateY(y);
        staText.setTranslateY(y);
        //
    }

    private void calculateNmToPixelsRatio() {
        double nbPixels = leftVLine.getEndY() - leftVLine.getStartY();
        secondsToPixelRatio = nbPixels / currentMaxTime;

    }

    private void updateArrivalHorizontalLines() {
        double y = height - PADDING - footerTextHeight;
        // start
        upperHorizontalLine.setStartX(PADDING);
        upperHorizontalLine.setStartY(y - MAJOR_LINE_SPACING);
        lowerHorizontalLine.setStartX(PADDING);
        lowerHorizontalLine.setStartY(y);
        // end
        upperHorizontalLine.setEndX(width - PADDING);
        upperHorizontalLine.setEndY(y - MAJOR_LINE_SPACING);
        lowerHorizontalLine.setEndX(width - PADDING);
        lowerHorizontalLine.setEndY(y);

    }

    private void updateVerticalLines() {
        // start
        leftVLine.setStartX((width - MAJOR_LINE_SPACING - 2.0 * PADDING) / 2.0 + PADDING);
        leftVLine.setStartY(PADDING);
        rightVLine.setStartX((width + MAJOR_LINE_SPACING - 2.0 * PADDING) / 2.0 + PADDING);
        rightVLine.setStartY(PADDING);
        // end
        leftVLine.setEndX((width - MAJOR_LINE_SPACING - 2.0 * PADDING) / 2.0 + PADDING);
        leftVLine.setEndY(upperHorizontalLine.getStartY());
        rightVLine.setEndX((width + MAJOR_LINE_SPACING - 2.0 * PADDING) / 2.0 + PADDING);
        rightVLine.setEndY(upperHorizontalLine.getStartY());
    }

    private void updateDistanceLabels() {
        // 1 is for custom padding: not nice
        double x = (width - MAJOR_LINE_SPACING) / 2.0; // - 1
        etaLabels.forEach((distance, label) -> {
            label.setTranslateX(x);
            if (distance == 0) {
                label.setTranslateY(upperHorizontalLine.getStartY());
            } else {
                label.setTranslateY(PADDING + (currentMaxTime - distance) * secondsToPixelRatio - label.getHeight() / 2.0);
            }
        });
    }

    private void updateDistanceLines() {
        etaLines.forEach((index, line) -> {
            line.setStartX((width - MAJOR_LINE_SPACING + PADDING) / 2.0);
            line.setStartY(PADDING + (currentMaxTime - index) * secondsToPixelRatio);
            line.setEndX((width + MAJOR_LINE_SPACING - PADDING) / 2.0);
            line.setEndY(PADDING + (currentMaxTime - index) * secondsToPixelRatio);
        });
    }

    public void setDataModel(DataModel dataModel) {
        clear();
        allGaps = dataModel.getArrivalGaps();
        runLater(() -> {
            dataModel.getSlots().forEach(slot -> {
                final SlotEtaTag etaTag = new SlotEtaTag(slot, dataModel.getCorrespondingPlane(slot));
                etaGroup.getChildren().add(etaTag.getNode());
                slotEtaTags.put(slot, etaTag);
            });
            dataModel.getAllPlanes().forEach(plane -> {
                //TODO
                System.err.println("TODO in arrival seq viewer");
//                if (!plane.isDeparture()) {
//                    final AircraftEtaTag etaTag = new AircraftEtaTag(plane);
//                    etaGroup.getChildren().add(etaTag.getNode());
//                    planeEtaTags.put(plane, etaTag);
//                }
            });
        });
        //
        runLater(() -> updateSize(width, height));

    }

    /**
     * Updates the slots and aircraft tags in the arrival sequence
     *
     * @param simTime the current simulation time
     */
    public void updateTimes(int simTime) {
        simulationTime = simTime;
        runLater(() -> {
            updateTags();
            updateGaps(simTime);
        });
    }

    protected void setArrivalGapsVisibility(boolean visibility) {
        gapGroup.setVisible(visibility);
    }

    private void updateTags() {
        slotEtaTags.forEach((slot, tag) -> {
            if (slot.getStartTime() > simulationTime || slot.getEta() <= 0) {
                tag.getNode().setVisible(false);
            } else {
                tag.getNode().setVisible(true);
                tag.getNode().setTranslateX(rightVLine.getStartX());
                tag.getNode().setTranslateY(getAircraftETATranslateY(slot.getEta()));
                tag.update();
            }
        });
        planeEtaTags.forEach((plane, tag) -> {
            if (plane.getStartTime() > simulationTime || plane.getEta() <= 0) {
                tag.getNode().setVisible(false);
            } else {
                tag.getNode().setVisible(true);
                tag.getNode().setTranslateX(PADDING);
                tag.getNode().setTranslateY(getAircraftETATranslateY(plane.getEta()));
                tag.updateController(plane.getController());
            }
        });
    }

    private void updateGaps(int simTime) {
        updateFirstGap();
        if (allGaps == null) {
            return;
        }
        List<NamedArrivalGap> gaps = allGaps.get(simTime);
        if (gaps == null) {
            gapRectangles.forEach(rect -> rect.setVisible(false));
            return;
        }

        NamedArrivalGap gap;
        Rectangle r;
        for (int i = 0; i < gaps.size(); i++) {
            gap = gaps.get(i);
            if (gapRectangles.size() <= i) {
                r = new Rectangle();
                gapRectangles.add(r);
                gapGroup.getChildren().add(r);
            } else {
                r = gapRectangles.get(i);
                if (!r.isVisible()) {
                    r.setVisible(true);
                }
            }
            updateGapDrawing(r, gap);
        }
        for (int i = gaps.size(); i < gapRectangles.size(); i++) {
            gapRectangles.get(i).setVisible(false);
        }
    }

    private void updateFirstGap() {
        if (!slotEtaTags.isEmpty()) {
            List<NewSlot> slots = slotEtaTags.keySet().stream().filter(slot -> slot.getEta() > 0).sorted(CoreComparator.SLOT_ETA_COMPARATOR).collect(Collectors.toList());
            if (!slots.isEmpty()) {
                double eta = slots.get(0).getEta();
                updateGapDrawing(firstGapRectangle, 0, eta, GapUtils.calculateArrivalType((int) eta));
                firstGapRectangle.setVisible(true);
            } else {
                firstGapRectangle.setVisible(false);
            }
        } else {
            firstGapRectangle.setVisible(false);
        }
    }

    private void updateGapDrawing(Rectangle r, NamedArrivalGap gap) {
        updateGapDrawing(r, gap.getFirstETA(), gap.getLastETA(), gap.getGapType());
    }

    private void updateGapDrawing(Rectangle r, double etafirst, double etaSecond, ArrivalGapType gapType) {
        r.setX(rightVLine.getEndX() + 2 * PADDING);
        double lowY = getAircraftETATranslateY(etafirst);
        double upY = getAircraftETATranslateY(etaSecond);
        r.setY(upY);
        r.setHeight(lowY - upY);
        switch (gapType) {
            case SINGLE:
                r.setWidth(4.0);
                r.setFill(Color.LIGHTGREEN);
                break;
            case DOUBLE:
                r.setWidth(8.0);
                r.setFill(Color.LIGHTGREEN);
                break;
            case TRIPLE:
                r.setWidth(12.0);
                r.setFill(Color.LIGHTGREEN);
                break;
            case B757:
            case LARGER:
                r.setWidth(16.0);
                r.setFill(Color.CORNFLOWERBLUE);
                break;
            default:
                r.setWidth(2.0);
                r.setFill(Color.RED);
                break;
        }
    }

    private double getAircraftETATranslateY(double eta) {
        // ( - simulationTime)
        double remainingTime = eta / 60.0;
        return PADDING + (currentMaxTime - remainingTime) * secondsToPixelRatio;
    }

    private void clear() {
//        allGaps.clear();
        etaGroup.getChildren().clear();
        //
        slotEtaTags.clear();
        planeEtaTags.clear();
        //
    }

    private void handleZoomEvent(ScrollEvent event) {
        if (event.getDeltaY() < 0) {
            currentMaxTime = Math.min(MAX_ARRIVAL_TIME, currentMaxTime + SCROLL_AMOUNT_ARRIVAL_TIME);
        } else {
            currentMaxTime = Math.max(MIN_ARRIVAL_TIME, currentMaxTime - SCROLL_AMOUNT_ARRIVAL_TIME);
        }
        updateGraphicsPosition();
        updateTimes(simulationTime);
    }

    private void handleTimeScaleMouseEntered(MouseEvent event) {
        LOG.log(Level.FINE, "handleTimeScaleMouseEntered on event {0}", event);
        leftVLine.setStroke(INTERACTIVE_LINE_COLOR);
        rightVLine.setStroke(INTERACTIVE_LINE_COLOR);
        etaLines.forEach((eta, line) -> line.setStroke(INTERACTIVE_LINE_COLOR));
        etaLabels.forEach((eta, text) -> text.setTextFill(INTERACTIVE_LINE_COLOR));
    }

    private void handleTimeScaleMouseExited(MouseEvent event) {
        LOG.log(Level.FINE, "handleTimeScaleMouseExited on event {0}", event);
        leftVLine.setStroke(DEFAULT_LINE_COLOR);
        rightVLine.setStroke(DEFAULT_LINE_COLOR);
        etaLines.forEach((eta, line) -> line.setStroke(DEFAULT_LINE_COLOR));
        etaLabels.forEach((eta, text) -> text.setTextFill(DEFAULT_LINE_COLOR));
    }

    private void handleSimulationChanged(PropertyChangeEvent event) {
        if (SimulationManager.ATC_GEOGRAPHY_CHANGED.equals(event.getPropertyName())) {
            ATCGeography geo = (ATCGeography) event.getNewValue();
            if (!geo.getAirports().isEmpty()) {
                runLater(() -> airportText.setText(geo.getAirports().get(0).getName()));
            }
        }
    }

}
