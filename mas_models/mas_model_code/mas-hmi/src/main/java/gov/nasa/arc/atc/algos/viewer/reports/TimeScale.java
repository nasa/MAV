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

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * 
 * @author ahamon
 *
 */
public class TimeScale {

	private static final Logger LOG = Logger.getGlobal();

	private static final double TIME_SCALE_CENTER_WIDTH_PERCENTAGE = 0.5;

	private static final Color DEFAUL_INTERACTIVE_COLOR = Color.CHARTREUSE;
	private static final double DEFAULT_LABEL_WIDTH = 25;
	private static final double DEFAULT_LABEL_HEIGHT = 25;

	private final Group mainNode;
	private final Rectangle background;
	private final Group labelGroup;
	private final Line currentTimeLine;
	private final Label currentTimeLabel;
	private final Rectangle foreground;

	private final Parent myParent;

	private Scene scene;

	private int maxTime = 1;

	private double backgroundWidth = 1;

	public TimeScale(Parent parent) {
		mainNode = new Group();
		labelGroup = new Group();
		background = new Rectangle();
		foreground = new Rectangle();
		currentTimeLabel = new Label();
		currentTimeLine = new Line();
		myParent = parent;
		initTimeScale();
		initInteractivity();
	}

	public void setWidth(double newWidth) {
		backgroundWidth = newWidth * TIME_SCALE_CENTER_WIDTH_PERCENTAGE;
		double backgroundX = (newWidth - backgroundWidth) / 2.0;
		//
		background.setWidth(backgroundWidth);
		background.setX(backgroundX);
		//
		currentTimeLine.setStartX(backgroundX);
		currentTimeLine.setEndX(backgroundX + newWidth / 2.0);
		//
		currentTimeLabel.setTranslateX(backgroundX + (backgroundWidth - DEFAULT_LABEL_WIDTH) / 2.0);
		//
		foreground.setWidth(newWidth);
	}

	public void setHeight(double newHeight) {
		background.setHeight(newHeight);
		foreground.setHeight(newHeight);
	}

	public void setX(double newX) {
		mainNode.setTranslateX(newX);
	}

	public Node getNode() {
		return mainNode;
	}

	public void setMaxTime(int newMaxTime) {
		maxTime = newMaxTime;
		LOG.log(Level.FINE, "maxTime = {0}", maxTime);
	}

	private void initTimeScale() {
		foreground.setOpacity(0.0);
		foreground.setFill(Color.RED);
		currentTimeLine.setStroke(DEFAUL_INTERACTIVE_COLOR);
		currentTimeLabel.setFont(new Font(9));
		currentTimeLabel.setAlignment(Pos.CENTER);
		currentTimeLabel.setPrefSize(DEFAULT_LABEL_WIDTH, DEFAULT_LABEL_HEIGHT);
		currentTimeLabel.setTextFill(DEFAUL_INTERACTIVE_COLOR);
		labelGroup.getChildren().add(currentTimeLine);
		labelGroup.getChildren().add(currentTimeLabel);
		mainNode.getChildren().add(background);
		mainNode.getChildren().add(labelGroup);
		mainNode.getChildren().add(foreground);
		//
		background.setStroke(null);
		background.setVisible(false);
		labelGroup.setVisible(false);
	}

	private void initInteractivity() {
		foreground.setOnMouseEntered(this::handleMouseEntered);
		foreground.setOnMouseExited(this::handleMouseExited);
		foreground.setOnMouseMoved(this::handleMouseMoved);
	}

	private void handleMouseEntered(MouseEvent event) {
		LOG.log(Level.FINE, "handleMouseEntered : event = {0}", event);
		background.setStroke(DEFAUL_INTERACTIVE_COLOR);
		background.setVisible(true);
		labelGroup.setVisible(true);
		// hide cursor
		scene = myParent.getScene();
		scene.setCursor(Cursor.NONE);
		//
		displayTime(event.getY());
	}

	private void handleMouseExited(MouseEvent event) {
		LOG.log(Level.FINE, "handleMouseExited : event = {0}", event);
		background.setStroke(null);
		background.setVisible(false);
		labelGroup.setVisible(false);
		// show cursor
		scene = myParent.getScene();
		scene.setCursor(Cursor.DEFAULT);
	}

	private void handleMouseMoved(MouseEvent event) {
		LOG.log(Level.FINE, "handleMouseMoved : event = {0}", event);
		displayTime(event.getY());
	}

	private void displayTime(double y) {
		currentTimeLine.setStartY(y);
		currentTimeLine.setEndY(y);
		int time = maxTime - (int) (y / background.getHeight() * maxTime);
		currentTimeLabel.setText(Integer.toString(time));
	}

}
