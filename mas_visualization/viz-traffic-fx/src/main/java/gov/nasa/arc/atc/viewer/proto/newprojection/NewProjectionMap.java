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

package gov.nasa.arc.atc.viewer.proto.newprojection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * 
 * @author ahamon
 *
 */
public class NewProjectionMap extends Application{

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		stage.setTitle("New Projection / canvas proto");
		
		AnchorPane root = new AnchorPane();
		root.setStyle("-fx-background-color: red;");
		
		Canvas canvas = new Canvas();
		
		root.getChildren().add(canvas);

		root.widthProperty().addListener((obs,old,newV)->redrawCanvas(canvas, root));
		root.heightProperty().addListener((obs,old,newV)->redrawCanvas(canvas, root));
		
		Scene scene = new Scene(root,800,600);
		stage.setScene(scene);
		stage.show();
		
		redrawCanvas(canvas, root);
		
		
	}
	
	private void redrawCanvas(Canvas canvas, AnchorPane pane){
		// resize
		canvas.setWidth(pane.getWidth());
		canvas.setHeight(pane.getHeight());
//		canvas.setTranslateX(0);
//		canvas.setTranslateY(0);
		
		
		//
		GraphicsContext context = canvas.getGraphicsContext2D();
		
		//background
		context.beginPath();
		context.setFill(Color.BLACK);
		context.fillRect(0, 0, pane.getWidth(), pane.getHeight());
		context.closePath();
		
	}
}
