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

import gov.nasa.arc.atc.atc2dviz.HighlightManager;
import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.core.NewSlot;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
public class SlotEtaTag {

	private static final Color DEFAULT_ETA_COLOR = Color.WHITESMOKE;
	private static final Color METERED_COLOR = Color.DARKORANGE;
	private static final Font DEFAULT_FONT = new Font(10);

	private final Group mainNode;

	private final Line line;
	private final Label nameLabel;

	private final NewSlot slot;
	private final NewPlane plane;

	private boolean isMetered = false;

	private double width = 0;

	private Color fillColor = DEFAULT_ETA_COLOR;

	/**
	 * 
	 * @param slot the slot to represent in the {@link ArrivalSequenceViewer}
	 * @param plane the plane corresponding to the slot
	 */
	public SlotEtaTag(NewSlot slot, NewPlane plane) {
		this.slot = slot;
		this.plane = plane;
		mainNode = new Group();
		line = new Line(5.0, 0.0, 75.0, 0.0);
		nameLabel = new Label(this.slot.getSimpleName());
		initEtaTag();
	}

	private void initEtaTag() {
		setColor(DEFAULT_ETA_COLOR);
		nameLabel.setFont(DEFAULT_FONT);
		nameLabel.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
		nameLabel.setTranslateX(ArrivalSequenceViewer.PADDING);
		//
		createInteractivity();
		//
		mainNode.getChildren().add(line);
		mainNode.getChildren().add(nameLabel);
	}

	public Node getNode() {
		return mainNode;
	}

	public void setWidth(double newWidth) {
		width = newWidth;
		double labelWidth = nameLabel.getWidth();
		double labelHeight = nameLabel.getHeight();
		if (Double.doubleToRawLongBits(labelWidth) == 0) {
			nameLabel.applyCss();
			labelWidth = nameLabel.prefWidth(-1);
			labelHeight = nameLabel.prefHeight(-1);
		}
		nameLabel.setTranslateX(width - labelWidth - ArrivalSequenceViewer.PADDING);
		line.setEndX(nameLabel.getTranslateX() - ArrivalSequenceViewer.LABEL_PADDING);
		nameLabel.setTranslateY(-labelHeight / 2.0);
	}

	/**
	 * update the tag if plane is been metered
	 */
	public void update() {
		if (plane != null && plane.isMetering() != isMetered) {
			isMetered = plane.isMetering();
			if (isMetered) {
				setColor(METERED_COLOR);
			} else {
				setColor(DEFAULT_ETA_COLOR);
			}
		}
	}

	private void createInteractivity() {
		nameLabel.setOnMouseEntered(event -> {
			line.setStroke(HighlightManager.HIGHLIGHT_COLOR);
			nameLabel.setTextFill(HighlightManager.HIGHLIGHT_COLOR);
			HighlightManager.highlightSlot(slot.getFullName(), true);
		});
		nameLabel.setOnMouseExited(event -> {
			setColor(fillColor);
			HighlightManager.highlightSlot(slot.getFullName(), false);
		});
	}

	private void setColor(Color c) {
		fillColor = c;
		line.setStroke(fillColor);
		nameLabel.setTextFill(fillColor);
	}

}
