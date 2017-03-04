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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.utils.ConsoleUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author ahamon
 *
 */
public class ViewerConfigApp extends Application {

	private static final Logger LOG = Logger.getGlobal();
	private static final Object BARRIER = new Object();
	//

	@Override
	public void start(final Stage primaryStage) throws Exception {
		synchronized (BARRIER) {
			BARRIER.notify();
			try {
				FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("ViewerConfigurator.fxml"));
				fXMLLoader.load();
				Parent configContent = fXMLLoader.getRoot();
				ViewerConfigController configController = fXMLLoader.getController();
				LOG.log(Level.FINE, "ViewerConfigController : {0}", configController);
				primaryStage.setScene(new Scene(configContent));
				primaryStage.setTitle("Configuration window");
				configController.setStage(primaryStage);
				primaryStage.show();
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, "Exception is {0}", ex);
				LOG.log(Level.SEVERE, "Error while creating ViewerConfigController: {0}", ex.getMessage());
			}
			
		}
	}

	private static void initialize() throws InterruptedException {
		Thread t = new Thread("JavaFX Init Thread") {
			@Override
			public void run() {
				Application.launch(ViewerConfigApp.class, new String[0]);
			}
		};
		t.setDaemon(true);
		t.start();
		synchronized (BARRIER) {
			boolean waited = false;
			while (!waited) {
				BARRIER.wait();
				waited = true;
			}
		}
	}

	/**
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ConsoleUtils.setLoggingLevel(Level.WARNING);
		ViewerConfigApp.initialize();
	}

}
