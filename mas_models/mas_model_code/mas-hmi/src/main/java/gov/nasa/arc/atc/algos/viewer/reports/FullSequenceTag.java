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

package gov.nasa.arc.atc.algos.viewer.reports;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.airborne.TrafficType;
import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.DepartureInfo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

/**
 * 
 * @author ahamon
 *
 */
public class FullSequenceTag implements Tag {

	public static final double TAG_MARGIN = 8.0;

	private static final Logger LOG = Logger.getGlobal();

	private static final Color DEBUG_COLOR = Color.DEEPPINK;
	private static final Color ORIGINAL_LINE_COLOR = Color.ORANGERED;

	private static final double DEFAULT_LABEL_WIDTH = 40;
	private static final double DEFAULT_LABEL_HEIGHT = 10;

	private final int maxTime;
	private final int timeIn;
	private final int timeOut;
	private final int originalTimeOut;
	//
	private final int timeInDiff;
	private final int timeOutDiff;
	private final int originalTimeOutDiff;
	private final TrafficType type;

	private final Group mainNode;
	private final Line inLine;
	private final Line outLine;
	private final Line originalOutLine;
	private final Line leftOriginalOutLine;
	private final Line rightOriginalOutLine;
	private final Line connectionLine;
	private final Label leftTagName;
	private final Label rightTagName;

	private final boolean delayed;

	private double leftY = 0.0;
	private double rightY = 0.0;
	private double originalY = 0.0;
	private double halfWidth = 0.0;

	private final Background tagBackground = new Background(new BackgroundFill(Color.BLACK, new CornerRadii(1), new Insets(0)));

	public FullSequenceTag(final int maxSimTime, final String name, final int timeIN, final int timeOUT, final int originalTimeOUT, final TrafficType trafficType) {
		maxTime = maxSimTime;
		timeIn = timeIN;
		timeOut = timeOUT;
		originalTimeOut = originalTimeOUT;
		type = trafficType;
		timeInDiff = maxTime - timeIn;
		timeOutDiff = maxTime - timeOut;
		originalTimeOutDiff = maxTime - originalTimeOut;
		delayed = timeIn != timeOut;
		//
		mainNode = new Group();
		inLine = new Line();
		outLine = new Line();
		originalOutLine = new Line();
		leftOriginalOutLine = new Line();
		rightOriginalOutLine = new Line();
		connectionLine = new Line();
		leftTagName = new Label(name);
		rightTagName = new Label(name);
		initTag();
		initTagInteractivity();
	}

	public FullSequenceTag(final int maxSimTime, DepartureInfo departureInfoIn, DepartureInfo departureInfoOut) {
		this(maxSimTime, departureInfoIn.getName(), departureInfoIn.getCurrentDepartureTime(), departureInfoOut.getCurrentDepartureTime(), departureInfoOut.getOriginalDepartureTime(), TrafficType.DEPARTURE);
		assert departureInfoIn.getName().equals(departureInfoOut.getName());
	}

	public FullSequenceTag(final int maxSimTime, ArrivalInfo arrivalInfoIn, ArrivalInfo arrivalInfoOut) {
		this(maxSimTime, arrivalInfoIn.getName(), arrivalInfoIn.getArrivalTime(), arrivalInfoOut.getArrivalTime(), -1, TrafficType.ARRIVAL); // TODO propage in XML file original arrival Time
		assert arrivalInfoIn.getName().equals(arrivalInfoOut.getName());
	}

	@Override
	public Node getNode() {
		return mainNode;
	}

	@Override
	public void setY(double newSecondsPixelRatio) {
		leftY = timeInDiff * newSecondsPixelRatio;
		rightY = timeOutDiff * newSecondsPixelRatio;
		originalY = originalTimeOutDiff * newSecondsPixelRatio;
		// left
		inLine.setStartY(leftY);
		inLine.setEndY(leftY);
		leftTagName.setTranslateY(leftY - DEFAULT_LABEL_HEIGHT / 2);
		// right
		outLine.setStartY(rightY);
		outLine.setEndY(rightY);
		rightTagName.setTranslateY(rightY - DEFAULT_LABEL_HEIGHT / 2);
		//
		connectionLine.setStartY(leftY);
		connectionLine.setEndY(rightY);
		//
		originalOutLine.setStartY(originalY);
		originalOutLine.setEndY(originalY);
		leftOriginalOutLine.setStartY(originalY);
		leftOriginalOutLine.setEndY(leftY);
		rightOriginalOutLine.setStartY(originalY);
		rightOriginalOutLine.setEndY(rightY);
	}

	@Override
	public void setWidth(double newWidth, double centerWidth) {
		halfWidth = (newWidth - centerWidth) / 2.0;
		// left
		leftTagName.setTranslateX(TAG_MARGIN);
		inLine.setStartX(TAG_MARGIN + DEFAULT_LABEL_WIDTH);
		inLine.setEndX(halfWidth);
		// right
		rightTagName.setTranslateX(newWidth - TAG_MARGIN - DEFAULT_LABEL_WIDTH);
		outLine.setStartX(halfWidth + centerWidth);
		outLine.setEndX(rightTagName.getTranslateX());
		//
		connectionLine.setStartX(halfWidth);
		connectionLine.setEndX(halfWidth + centerWidth);
		//
		originalOutLine.setStartX(TAG_MARGIN + DEFAULT_LABEL_WIDTH);
		originalOutLine.setEndX(newWidth - TAG_MARGIN - DEFAULT_LABEL_WIDTH);
		leftOriginalOutLine.setStartX(halfWidth);
		leftOriginalOutLine.setEndX(halfWidth);
		rightOriginalOutLine.setStartX(halfWidth + centerWidth);
		rightOriginalOutLine.setEndX(halfWidth + centerWidth);
	}

	private void initTag() {
		setCurrentTagColor();
		//
		originalOutLine.setStroke(ORIGINAL_LINE_COLOR);
		leftOriginalOutLine.setStroke(ORIGINAL_LINE_COLOR);
		rightOriginalOutLine.setStroke(ORIGINAL_LINE_COLOR);
		// TODO : use static vars
		if (!delayed) {
			connectionLine.getStrokeDashArray().addAll(2d, 3d);
			//
			leftTagName.setFont(new Font(8.5));
			rightTagName.setFont(new Font(8.5));
		} else {
			inLine.setStrokeWidth(1.5);
			connectionLine.setStrokeWidth(1.5);
			outLine.setStrokeWidth(1.5);
			//
			leftTagName.setFont(new Font(9));
			rightTagName.setFont(new Font(9));
		}
		//
		leftTagName.setBackground(tagBackground);
		leftTagName.setPrefWidth(DEFAULT_LABEL_WIDTH);
		leftTagName.setPrefHeight(DEFAULT_LABEL_HEIGHT);
		leftTagName.setAlignment(Pos.CENTER);
		//
		rightTagName.setBackground(tagBackground);
		rightTagName.setPrefWidth(DEFAULT_LABEL_WIDTH);
		rightTagName.setPrefHeight(DEFAULT_LABEL_HEIGHT);
		rightTagName.setAlignment(Pos.CENTER);
		//
		originalOutLine.setVisible(false);
		leftOriginalOutLine.setVisible(false);
		rightOriginalOutLine.setVisible(false);
		originalOutLine.setStrokeWidth(3.0);
		leftOriginalOutLine.setStrokeWidth(3.0);
		rightOriginalOutLine.setStrokeWidth(3.0);
		// TEMP ACK for departure only
		if (originalTimeOut > 0) {
			mainNode.getChildren().add(originalOutLine);
			mainNode.getChildren().add(leftOriginalOutLine);
			mainNode.getChildren().add(rightOriginalOutLine);
		}
		//
		mainNode.getChildren().add(inLine);
		mainNode.getChildren().add(connectionLine);
		mainNode.getChildren().add(outLine);
		mainNode.getChildren().add(leftTagName);
		mainNode.getChildren().add(rightTagName);
	}

	private void initTagInteractivity() {
		leftTagName.setOnMouseEntered(this::handleMouseEntered);
		leftTagName.setOnMouseExited(this::handleMouseExited);
		rightTagName.setOnMouseEntered(this::handleMouseEntered);
		rightTagName.setOnMouseExited(this::handleMouseExited);
	}

	private void setCurrentTagColor() {
		switch (type) {
		case ARRIVAL:
			displayAsArrival();
			break;
		case DEPARTURE:
			displayAsDeparture();
			break;
		default:
			setColor(DEBUG_COLOR);
			break;
		}
	}

	private void displayAsDeparture() {
		if (delayed) {
			setColor(AbstractTag.DEPARTURE_DELAYED_COLOR);
		} else {
			setColor(AbstractTag.DEPARTURE_COLOR);
		}
		leftTagName.setVisible(false);
	}

	private void displayAsArrival() {
		if (delayed) {
			setColor(AbstractTag.ARRIVAL_DELAYED_COLOR);
		} else {
			setColor(AbstractTag.ARRIVAL_COLOR);
		}
		rightTagName.setVisible(false);
	}

	private void displayOver() {
		if (delayed) {
			setColor(AbstractTag.OVER_DELAYED_COLOR);
		} else {
			setColor(AbstractTag.OVER_COLOR);
		}
	}

	private void setColor(Color color) {
		inLine.setStroke(color);
		connectionLine.setStroke(color);
		outLine.setStroke(color);
		leftTagName.setTextFill(color);
		rightTagName.setTextFill(color);
	}

	private void handleMouseEntered(MouseEvent event) {
		LOG.log(Level.FINE, "handleMouseEntered : event = {0}", event);
		originalOutLine.setVisible(true);
		leftOriginalOutLine.setVisible(true);
		rightOriginalOutLine.setVisible(true);
		displayOver();
	}

	private void handleMouseExited(MouseEvent event) {
		LOG.log(Level.FINE, "handleMouseExited : event = {0}", event);
		originalOutLine.setVisible(false);
		leftOriginalOutLine.setVisible(false);
		rightOriginalOutLine.setVisible(false);
		setCurrentTagColor();
	}

}
