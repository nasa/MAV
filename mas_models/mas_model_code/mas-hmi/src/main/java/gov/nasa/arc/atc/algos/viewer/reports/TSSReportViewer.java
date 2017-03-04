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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.reports.TSSReport;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author ahamon
 *
 */
public class TSSReportViewer {

	private static final Logger LOG = Logger.getGlobal();
	//
	private AnchorPane pane;
	private TSSReportViewerController viewController;

	public TSSReportViewer() {
		// generate layout
		try {
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("TSSReportViewer.fxml"));
			fXMLLoader.load();
			pane = fXMLLoader.getRoot();
			viewController = fXMLLoader.getController();
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Error while creating TSSReportViewer : {0}", ex);
		}
	}

	public void displayReport(TSSReport report) {
		// populate graphs
		viewController.displayReport(report);
	}

	public Parent getNode() {
		return pane;
	}

}
