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

import gov.nasa.arc.atc.airborne.TrafficType;
import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.DepartureInfo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public abstract class AbstractTag implements Tag {

	public static final double TAG_MARGIN = 8.0;

	public static final Color ARRIVAL_COLOR = Color.LIGHTBLUE;
	public static final Color ARRIVAL_DELAYED_COLOR = Color.DEEPSKYBLUE;
	public static final Color DEPARTURE_COLOR = Color.DARKGOLDENROD;
	public static final Color DEPARTURE_DELAYED_COLOR = Color.GOLD;
	public static final Color OVER_COLOR = Color.CHARTREUSE;
	public static final Color OVER_DELAYED_COLOR = Color.FORESTGREEN;
	
	private static final Color DEBUG_COLOR = Color.DEEPPINK;
	
	private static final double DEFAULT_LABEL_HEIGHT = 10;

	// to remove?
	private final int time;
	private final int simDuration;
	//
	private final int timeDiff;
	private final TrafficType type;

	private final Group mainNode;
	private final Line line;
	private final Label tagName;
	
	private final Background tagBackground = new Background(new BackgroundFill(Color.BLACK, new CornerRadii(1), new Insets(0)));

	public AbstractTag(final int simulationDuration, final int simulationTime, final String name, final TrafficType trafficType) {
		time = simulationTime;
		simDuration = simulationDuration;
		type = trafficType;
		timeDiff = simDuration - time;
		//
		mainNode = new Group();
		line = new Line();
		tagName = new Label(name);
		initTag();
	}

	public AbstractTag(final int simulationDuration, DepartureInfo departureInfo) {
		this(simulationDuration, departureInfo.getCurrentDepartureTime(), departureInfo.getName(), TrafficType.DEPARTURE);
	}

	public AbstractTag(final int simulationDuration, ArrivalInfo arrivalInfo) {
		this(simulationDuration, arrivalInfo.getArrivalTime(), arrivalInfo.getName(), TrafficType.ARRIVAL);
	}

	@Override
	public Node getNode() {
		return mainNode;
	}

	@Override
	public void setY(double newSecondsPixelRatio) {
		mainNode.setTranslateY(timeDiff * newSecondsPixelRatio);
	}

	protected final void setLabelX(double newX) {
		tagName.setTranslateX(newX);
	}

	protected final double getLabelStartX() {
		return tagName.getTranslateX();
	}
	
	protected final double getLabelWidth() {
		return tagName.getWidth();
	}

	protected final double getLabelEndX() {
		return tagName.getTranslateX() + tagName.getWidth();
	}

	protected final void setLineXCoordinates(double startX, double endX) {
		line.setStartX(startX);
		line.setEndX(endX);
	}

	private void initTag() {
		switch (type) {
		case ARRIVAL:
			setColor(ARRIVAL_COLOR);
			break;
		case DEPARTURE:
			setColor(DEPARTURE_COLOR);
			break;
		default:
			setColor(DEBUG_COLOR);
			break;
		}
		//
		tagName.setFont(new Font(8.5));
		tagName.setBackground(tagBackground);
		tagName.setPrefHeight(DEFAULT_LABEL_HEIGHT);
		tagName.setAlignment(Pos.CENTER);
		//
		mainNode.getChildren().add(line);
		mainNode.getChildren().add(tagName);
		//
		tagName.setTranslateY(-DEFAULT_LABEL_HEIGHT/2);
	}

	private void setColor(Color color) {
		line.setStroke(color);
		tagName.setTextFill(color);
	}

}
