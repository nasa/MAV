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
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.reports.DSASReport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author ahamon
 *
 */
public class DSASReportViewController implements Initializable {

	private static final Logger LOG = Logger.getGlobal();

	@FXML
	private SplitPane splitPane;

//	private SequenceEvolutionController sequenceEvolutionController;

//	private DSASReport report;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOG.log(Level.INFO, "initialize URL={0} ResourceBundle={1}", new Object[] { location, resources });
//		initSequenceEvolutionView();
	}

	public void displayReport(DSASReport dsasReport) {
//		report = dsasReport;
		LOG.log(Level.INFO, "displaying dsas report {0}", dsasReport);
		//
//		clearPreviousData();
//		createSequenceDisplay();
		
		try {
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("SequenceEvolutionViewer.fxml"));
			fXMLLoader.load();
			AnchorPane arrivalsEvolutionPane = fXMLLoader.getRoot();
			splitPane.getItems().addAll(arrivalsEvolutionPane);
			SequenceEvolutionController sequenceEvolutionController = fXMLLoader.getController();sequenceEvolutionController.displaySequences(dsasReport.getLastInfoTime(), dsasReport.getAllDeparturesIN(), dsasReport.getAllDeparturesOUT(), dsasReport.getAllArrivalsIN(), dsasReport.getAllArrivalsOUT());
			
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Error while creating TSSReportViewer : {0}", ex);
		}
		
		
	}

//	private void initSequenceEvolutionView() {
//		try {
//			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("SequenceEvolutionViewer.fxml"));
//			fXMLLoader.load();
//			AnchorPane arrivalsEvolutionPane = fXMLLoader.getRoot();
//			AnchorPane empltyPane = new AnchorPane();
//			splitPane.getItems().addAll(arrivalsEvolutionPane, empltyPane);
//			splitPane.setDividerPositions(0.75f, 1.0f);
//			sequenceEvolutionController = fXMLLoader.getController();
//		} catch (IOException ex) {
//			LOG.log(Level.SEVERE, "Error while creating TSSReportViewer : {0}", ex);
//		}
//	}
//
//	private void clearPreviousData() {
//		// TODO
//	}
//
//	private void createSequenceDisplay() {
//		sequenceEvolutionController.displaySequences(report.getLastInfoTime(), report.getAllDeparturesIN(), report.getAllDeparturesOUT(), report.getAllArrivalsIN(), report.getAllArrivalsOUT());
//	}

}
