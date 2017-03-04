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

package gov.nasa.arc.atc.viewer.proto;

import gov.nasa.arc.atc.algos.dsas.ArrivalGapType;
import gov.nasa.arc.atc.algos.dsas.NamedArrivalGap;
import gov.nasa.arc.atc.core.CoreComparator;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.DataModelUtils;
import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.core.NewSlot;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.utils.GapUtils;
import gov.nasa.arc.atc.viewer.AircraftEtaTag;
import gov.nasa.arc.atc.viewer.SlotEtaTag;
import gov.nasa.arc.atc.viewer.proto.MapZoom.VizState;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static javafx.application.Platform.runLater;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * 
 * @author ahamon
 *
 */
public class SimpleSeqViewer {

	private static final Logger LOG = Logger.getGlobal();

	/**
	 * Max arrival time represented in [MIN]
	 */
	private static final double MAX_ARRIVAL_TIME = 60;
	private static final double MIN_ARRIVAL_TIME = 10;
	private static final double SCROLL_AMOUNT_ARRIVAL_TIME = 5;

	private static final double MAJOR_LINE_SPACING = 20;
	private static final double PADDING = 8;

	private final PropertyChangeSupport support = new PropertyChangeSupport(SimpleSeqViewer.this);

	private final Map<NewSlot, SlotEtaTag> slotEtaTags;
	private final Map<NewPlane, AircraftEtaTag> planeEtaTags;
	private Map<Integer, List<NamedArrivalGap>> allGaps;
	private final List<Rectangle> gapRectangles;
	private final Group gapGroup;
	private final Rectangle firstGapRectangle;

	private final AnchorPane mainNode;
	private final Canvas canvas;
	private final Rectangle clipRectangle;
	private final Button button;

	private VizState state;
	private double width = 300;
	private double height = 800;

	//
	private double scaleStartX = 0;
	private double scaleEndX = 0;
	private double leftVLineEndY = 0;
	private double rightLineX;
	private double secondsToPixelRatio;
	private double columnWidth = 1;

	private double currentMaxTime = MAX_ARRIVAL_TIME;
	private int simulationTime = 0;

	private boolean overScale = false;

	private ATCNode runway;
	private List<String> aircraftNames;

	/**
	 * @param theRunway the runway the sequence arrives at
	 */
	public SimpleSeqViewer(ATCNode theRunway) {
		runway = theRunway;
		slotEtaTags = new HashMap<>();
		planeEtaTags = new HashMap<>();
		allGaps = new HashMap<>();
		mainNode = new AnchorPane();
		gapGroup = new Group();
		canvas = new Canvas(width, height);
		clipRectangle= new Rectangle(width,height);
		mainNode.setClip(clipRectangle);
		button = new Button("Split");
		button.setTranslateX(10);
		button.setTranslateY(10);
		gapRectangles = new ArrayList<>();
		firstGapRectangle = new Rectangle();
		mainNode.getChildren().add(canvas);
		//TEMP modification until proper mechanism is found
//		mainNode.getChildren().add(button);
		gapGroup.getChildren().add(firstGapRectangle);
		mainNode.getChildren().add(gapGroup);
		//
		state = VizState.INTEGRATED;
		button.setOnAction(this::handleAction);
		setSize(width, height);
		initInteractivity();
	}

	public SimpleSeqViewer(){
		this(new Runway("XX",new Airport("XXX",0,0),0));
	}

	void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	void setPosition(double x, double y) {
		mainNode.setTranslateX(x);
		mainNode.setTranslateY(y);
	}

	/**
	 * 
	 * @param newWidth new width
	 * @param newHeight new height
	 */
	public final void setSize(double newWidth, double newHeight) {
		width = newWidth;
		height = newHeight;
		clipRectangle.setWidth(width);
		clipRectangle.setHeight(height);
		columnWidth = (width - MAJOR_LINE_SPACING - 2.0 * PADDING) / 2.0;
		drawCanvas();
		slotEtaTags.forEach((slot, tag) -> tag.setWidth(columnWidth));
		planeEtaTags.forEach((plane, tag) -> tag.setWidth(columnWidth));
		updateTimes(simulationTime);
	}

	Point2D getPosition() {
		return new Point2D(mainNode.getTranslateX(), mainNode.getTranslateY());
	}

	public void setRunway(ATCNode r){
		runway=r;
		drawCanvas();
		//TODO update
		//setDataModel(dataModel);
	}

	public Node getNode() {
		return mainNode;
	}

	private void handleAction(ActionEvent event) {
		LOG.log(Level.FINE, "handleAction {0}", event);
		switch (state) {
		case INTEGRATED:
			state = VizState.SPLIT;
			button.setText("integrate");
			break;
		case SPLIT:
			state = VizState.INTEGRATED;
			button.setText("split");
			break;
		default:
			break;
		}
		support.firePropertyChange("change", null, state);
	}

	private void drawCanvas() {
		// resize
		canvas.setWidth(width);
		canvas.setHeight(height);
		canvas.setTranslateX(0);
		canvas.setTranslateY(0);

		// variables
		double footerTextHeight = 25;
		GraphicsContext context = canvas.getGraphicsContext2D();

		// clear previous drawings

		// font attribute
		context.setFont(new Font(10));

		// background
		context.setFill(Color.BLACK);
		context.fillRect(0, 0, width, height);

		context.setFill(Color.WHITESMOKE);
		context.setStroke(Color.WHITESMOKE);
		// footer
		double footerY = height - PADDING;
		double footerTextWidth = 30; // hum...
		context.fillText("Est. Time", (width - MAJOR_LINE_SPACING) / 4.0 - footerTextWidth / 2.0 + PADDING, footerY);
		context.fillText(runway.getName(), (width - MAJOR_LINE_SPACING - footerTextWidth) / 2.0 + PADDING, footerY);
		context.fillText("Sch. Time", 3.0 * (width - MAJOR_LINE_SPACING) / 4.0 - footerTextWidth / 2.0 + PADDING, footerY);

		// arrival horizontal lines
		double y = height - PADDING - footerTextHeight;
		double leftLineX = (width - MAJOR_LINE_SPACING - 2.0 * PADDING) / 2.0 + PADDING;
		rightLineX = (width + MAJOR_LINE_SPACING - 2.0 * PADDING) / 2.0 + PADDING;
		double leftVLineStartY = PADDING;
		leftVLineEndY = y - MAJOR_LINE_SPACING;

		double nbPixels = leftVLineEndY - leftVLineStartY;
		secondsToPixelRatio = nbPixels / currentMaxTime;

		//// upper line
		context.beginPath();
		context.moveTo(PADDING, leftVLineEndY);
		context.lineTo(leftLineX, leftVLineEndY);
		context.moveTo(rightLineX, leftVLineEndY);
		context.lineTo(width - PADDING, leftVLineEndY);
		//// lower lines
		context.moveTo(PADDING, y);
		context.lineTo(width - PADDING, y);

		if (overScale) {
			context.setStroke(Color.CHARTREUSE);
		}

		// vertical lines
		//// left
		context.moveTo(leftLineX, leftVLineStartY);
		context.lineTo(leftLineX, leftVLineEndY);
		//// right
		context.moveTo(rightLineX, PADDING);
		context.lineTo(rightLineX, leftVLineEndY);

		// distance lines and labels
		scaleStartX = (width - MAJOR_LINE_SPACING + PADDING) / 2.0;
		scaleEndX = (width + MAJOR_LINE_SPACING - PADDING) / 2.0;
		double labelX = (width - MAJOR_LINE_SPACING + PADDING) / 2.0; // - 1
		double dY;
		for (int i = 0; i < currentMaxTime + 1; i++) {
			if ((i % 5) != 0 && i != 0) {
				dY = PADDING + (currentMaxTime - i) * secondsToPixelRatio;
				context.moveTo(scaleStartX, dY);
				context.lineTo(scaleEndX, dY);
			} else {
				dY = PADDING + (currentMaxTime - i) * secondsToPixelRatio + 5; // !!
				context.fillText(Integer.toString(i), labelX, dY);
			}
		}

		// closing
		context.stroke();
		context.closePath();
	}

	private void initInteractivity() {
		canvas.setOnMouseMoved(this::handleMouseMove);
		canvas.setOnScroll(this::handleScroll);
	}

	private void handleMouseMove(MouseEvent event) {
		double x = event.getX();
		double y = event.getY();
        // test inside scale
        overScale = x >= scaleStartX && x <= scaleEndX && y >= PADDING && y <= leftVLineEndY;
		drawCanvas();
	}

	private void handleScroll(ScrollEvent event) {
		if (overScale) {
			if (event.getDeltaY() < 0) {
				currentMaxTime = Math.min(MAX_ARRIVAL_TIME, currentMaxTime + SCROLL_AMOUNT_ARRIVAL_TIME);
			} else {
				currentMaxTime = Math.max(MIN_ARRIVAL_TIME, currentMaxTime - SCROLL_AMOUNT_ARRIVAL_TIME);
			}
			drawCanvas();
			updateTags();
			updateGaps(simulationTime);
		}
	}

	public void setDataModel(DataModel dataModel) {
		aircraftNames = new LinkedList<>();
		// TODO: optimize? (possible in all scenarios?)
		dataModel.getAllPlanes().forEach(plane -> {
			if (DataModelUtils.fliesToFromRunway(dataModel, runway, plane.getSimpleName())) {
				// plane.getDestination().equals(runway.getName())
				final AircraftEtaTag etaTag = new AircraftEtaTag(plane);
				mainNode.getChildren().add(etaTag.getNode());
				planeEtaTags.put(plane, etaTag);
				aircraftNames.add(plane.getSimpleName());
			}
			// TODO: the same for departures
		});
		dataModel.getSlots().forEach(slot -> {
			if (DataModelUtils.fliesToFromRunway(dataModel, runway, slot.getSimpleName())) {
				final SlotEtaTag etaTag = new SlotEtaTag(slot, dataModel.getCorrespondingPlane(slot));
				mainNode.getChildren().add(etaTag.getNode());
				slotEtaTags.put(slot, etaTag);
			}
		});
		//
		allGaps = new HashMap<>();
		dataModel.getArrivalGaps().forEach((time, list) -> allGaps.put(time, list.stream().filter(this::isGapForRunway).collect(Collectors.toList())));

		// update display
		planeEtaTags.forEach((plane, tag) -> {
			tag.setWidth(columnWidth);
			tag.getNode().setVisible(false);
		});
		slotEtaTags.forEach((slot, tag) -> {
			tag.getNode().setVisible(false);
			tag.setWidth(columnWidth);
		});
		updateTags();
	}

	private boolean isGapForRunway(NamedArrivalGap gap) {
		// hum...
		String firstArrivalFulName = gap.getFirstArrivalName();
		String lastArrivalFulName = gap.getLastArrivalName();
        return aircraftNames.stream().anyMatch(name -> firstArrivalFulName.contains(name) || lastArrivalFulName.contains(name));
	}

	/**
	 * Updates the slots and aircrafts tags in the arrival sequence
	 * 
	 * @param simTime the current simulation time
	 */
	public void updateTimes(int simTime) {
		simulationTime = simTime;
		runLater(() -> {
			updateTags();
			updateGaps(simulationTime);
		});
	}

	/**
	 * 
	 * @param showGaps gaps visibility
	 */
	public void showGaps(boolean showGaps) {
		gapGroup.setVisible(showGaps);
	}

	private void updateTags() {
		slotEtaTags.forEach((slot, tag) -> {
			if (slot.getStartTime() > simulationTime || slot.getEta() <= 0) {
				tag.getNode().setVisible(false);
			} else {
				tag.getNode().setVisible(true);
				tag.getNode().setTranslateX(rightLineX);
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

	private double getAircraftETATranslateY(double eta) {
		double remainingTime = eta / 60.0;
		return PADDING + (currentMaxTime - remainingTime) * secondsToPixelRatio;
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
		r.setX(rightLineX + 2 * PADDING);
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

}
