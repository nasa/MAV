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

package gov.nasa.arc.atc.metrics;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
/**
 * Links the SimulationMetrics to the simulation replay when "View Simulation Info" is clicked
 * @author Kelsey
 *
 */
public class SimulationLinkApp {
	private static final Logger LOG = Logger.getGlobal();
	public static final String APP_FXML_FILE = "SimulationLinkDialog.fxml";
	public static final String APP_TITLE = "Link to Simulation Run?";
	public static final int SCENE_WIDTH = 500;
	public static final int SCENE_HEIGHT = 150;
	private Stage stage = new Stage();
	private Scene scene;
	private SimulationLinkController slController;

	/**--------------------------------*/
	public SimulationLinkApp() {
		Platform.runLater(() -> buildApp());
	}

	private void buildApp() {
		Parent root = new Pane();
		try {
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(APP_FXML_FILE));
			fXMLLoader.load();
			root = fXMLLoader.getRoot();
			slController = fXMLLoader.getController();
			//TODO fix
			int tempTime = 0;
			slController.setSimulationTime(tempTime);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Error while creating SimulationLinkApp : {0}", ex);
		}
		
		scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		stage.setScene(scene);
		stage.setTitle(APP_TITLE);
	}

	public void openSimulationDialog(int time, ArgmaxInfo argmax){
		//TODO fix
		if( slController == null){
			System.out.println("RETRY - controller is null");
		}else{
			slController.setSimulationTime(time); // controller is null on the first click
			slController.setOtherSimulationInfo(argmax);
		}
		stage.show();
	}
	
	public void closeApp(){
		stage.hide();
	}
}
