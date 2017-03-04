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

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {
	BorderPane bp = new BorderPane();

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		stage.setTitle("Slide out YouTube demo");

		// create a WebView to show to the right of the SideBar.
		bp.setStyle("-fx-background-color: #2f4f4f;");
		bp.setPrefSize(800, 600);

		// create a sidebar with some content in it.
		final Pane lyricPane = createSidebarContent();
		SideBar sidebar = new SideBar(250, lyricPane);
		VBox.setVgrow(lyricPane, Priority.ALWAYS);

		// layout the scene.
		final BorderPane layout = new BorderPane();

		StackPane st = new StackPane();
		st.getChildren().addAll(bp, sidebar.getControlButton());
		st.setAlignment(Pos.TOP_LEFT);

		VBox vb = new VBox(10);
		vb.getChildren().addAll(st);

		layout.setBottom(sidebar);
		layout.setCenter(vb);

		// show the scene.
		Scene scene = new Scene(layout);
		// scene.getStylesheets().add(getClass().getResource("/styles/slideout.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

	private BorderPane createSidebarContent() {
		// create some content to put in the sidebar.
		final Button changeLyric = new Button("New Song");
		changeLyric.getStyleClass().add("change-lyric");
		changeLyric.setMaxWidth(Double.MAX_VALUE);
        changeLyric.setOnAction((ActionEvent actionEvent) -> {
            System.out.println("Some action");
        });
		changeLyric.fire();
		final BorderPane lyricPane = new BorderPane();
		lyricPane.setTop(changeLyric);
		return lyricPane;
	}

	/**
	 * Animates a node on and off screen to the left.
	 */
	class SideBar extends VBox {
		/**
		 * @return a control button to hide and show the sidebar
		 */
		public Button getControlButton() {
			return controlButton;
		}

		private final Button controlButton;

		/**
		 * creates a sidebar containing a vertical alignment of the given nodes
		 */
		SideBar(final double expandedWidth, Node... nodes) {
			getStyleClass().add("sidebar");
			this.setPrefWidth(expandedWidth);
			this.setMinWidth(0);

			// create a bar to hide and show.
			setAlignment(Pos.CENTER);
			getChildren().addAll(nodes);

			// create a button to hide and show the sidebar.
			controlButton = new Button("Collapse");
			controlButton.getStyleClass().add("hide-left");

			// apply the animations when the button is pressed.
			controlButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					// create an animation to hide sidebar.
					final Animation hideSidebar = new Transition() {
						{
							setCycleDuration(Duration.millis(250));
						}

						@Override
						protected void interpolate(double frac) {
							final double curWidth = expandedWidth * (1.0 - frac);
							setPrefHeight(curWidth);
							setTranslateY(-expandedWidth + curWidth);
						}
					};
					hideSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent actionEvent) {
							setVisible(false);
							controlButton.setText("Show");
							controlButton.getStyleClass().remove("hide-left");
							controlButton.getStyleClass().add("show-right");
						}
					});
					// create an animation to show a sidebar.
					final Animation showSidebar = new Transition() {
						{
							setCycleDuration(Duration.millis(250));
						}

						@Override
						protected void interpolate(double frac) {
							final double curWidth = expandedWidth * frac;
							setPrefHeight(curWidth);
							setTranslateY(-expandedWidth + curWidth);
						}
					};
					showSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent actionEvent) {
							controlButton.setText("Collapse");
							controlButton.getStyleClass().add("hide-left");
							controlButton.getStyleClass().remove("show-right");
						}
					});
					if (showSidebar.statusProperty().get() == Animation.Status.STOPPED && hideSidebar.statusProperty().get() == Animation.Status.STOPPED) {
						if (isVisible()) {
							hideSidebar.play();
						} else {
							setVisible(true);
							showSidebar.play();
						}
					}
				}
			});
		}
	}
}
