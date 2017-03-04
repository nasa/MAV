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

import java.beans.PropertyChangeEvent;

import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Runway;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * 
 * @author ahamon
 *
 */
public class MapZoom extends Application {

	private HBox hBox;

	private SimpleSeqViewer viz;
	private SimpleCustomStage cStage;
	private Point2D oldVizPosition;

	@Override
	public void start(Stage primaryStage) throws Exception {
		hBox = new HBox();
		Scene scene = new Scene(hBox, 800, 1000);
		hBox.getChildren().add(new Rectangle(300, 300, Color.BLUE));
		//
		viz = new SimpleSeqViewer(new Runway("A_RUN", new Airport("Airport", 40, -70), 12));
		// !! pos creates bug
		viz.setPosition(0, 0);
		viz.addPropertyChangeListener(this::handleSimpleVizChange);
		hBox.getChildren().add(0, viz.getNode());
		//
		cStage = new SimpleCustomStage();
		//
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void handleSimpleVizChange(PropertyChangeEvent event) {
		VizState vState = (VizState) event.getNewValue();
		switch (vState) {
		case INTEGRATED:

			cStage.clearContent();
			viz.setPosition(oldVizPosition.getX(), oldVizPosition.getY());
			hBox.getChildren().add(0, viz.getNode());
			break;
		case SPLIT:
			oldVizPosition = viz.getPosition();
			viz.setPosition(0, 0);
			// if refresh problem when remoing node, use .toBack before removing it
			// https://bugs.openjdk.java.net/browse/JDK-8087752
			hBox.getChildren().remove(viz.getNode());
			cStage.setContent(viz);
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	public enum VizState {
		SPLIT, INTEGRATED
	}

	public static class SimpleCustomStage {

		private final Stage stage;
		private final Scene scene;
		private final Group root;

		private SimpleSeqViewer myContent;

		public SimpleCustomStage() {
			root = new Group();
			scene = new Scene(root);
			stage = new Stage();
			stage.setScene(scene);
			stage.hide();
			scene.widthProperty().addListener((obs, old, newValue) -> {
				if (myContent != null) {
					myContent.setSize(scene.getWidth(), scene.getHeight());
				}
			});
			scene.heightProperty().addListener((obs, old, newValue) -> {
				if (myContent != null) {
					myContent.setSize(scene.getWidth(), scene.getHeight());
				}
			});

		}

		void setContent(SimpleSeqViewer content) {
			myContent = content;
			root.getChildren().setAll(content.getNode());
			stage.show();
		}

		void clearContent() {
			myContent = null;
			root.getChildren().clear();
			stage.hide();
		}

	}

}
