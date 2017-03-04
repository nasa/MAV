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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.export.ReportXMLParser;
import gov.nasa.arc.atc.reports.DSASReport;
import gov.nasa.arc.atc.reports.DSASReportImpl;
import gov.nasa.arc.atc.reports.DSASReporter;
import gov.nasa.arc.atc.reports.TSSReport;
import gov.nasa.arc.atc.reports.TSSReportImpl;
import gov.nasa.arc.atc.reports.TSSReporter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * @author ahamon
 *
 */
public class StandaloneReportViewerController implements Initializable {

	private static final Logger LOG = Logger.getGlobal();

	private final FileChooser fileChooser = new FileChooser();
	private final FileChooser.ExtensionFilter extFilterXML = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");

	@FXML
	private Tab tssTab;
	@FXML
	private Tab dsasTab;

	private TSSReportViewer tssReportViewer;
	private DSASReportViewer dsasReportViewer;

	private Stage stage;

	private File reportFile = null;

	private TSSReport tssReport = null;
	private DSASReport dsasReport = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tssReportViewer = new TSSReportViewer();
		tssTab.setContent(tssReportViewer.getNode());
		dsasReportViewer = new DSASReportViewer();
		dsasTab.setContent(dsasReportViewer.getNode());
	}

	@FXML
	public void onLoadAction(ActionEvent event) {
		LOG.log(Level.INFO, "handle onChangeConfigFileAction on event {0}", event);
		fileChooser.setTitle("Select report file to import");
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(extFilterXML);
		File tmpConfig = fileChooser.showOpenDialog(stage);
		if (tmpConfig != null) {
			reportFile = tmpConfig;
			parseReportFile();
		}
	}

	protected void setOwner(Stage owner) {
		stage = owner;
	}

	protected void setReports(Object... params) {
		if (params.length == 0) {
			//HACK
			// be more generic in this HMI
			if(TSSReporter.getReportInputs().isEmpty()&&DSASReporter.getReportInputs().isEmpty()){
				Platform.runLater(() -> onLoadAction(new ActionEvent()));
				
			}else{
				if(!TSSReporter.getReportInputs().isEmpty()){
					tssReport= new TSSReportImpl(TSSReporter.getReportInputs().get(0));
					displayTSSReport();
				}
				if(!DSASReporter.getReportInputs().isEmpty()){
					dsasReport= new DSASReportImpl(DSASReporter.getReportInputs().get(0));
					displayDSASReport();
				}
			}
		} else if (params.length == 1) {
			loadParam1(params[0]);
		} else if (params.length == 2) {
			loadParams2(params[0], params[1]);
		} else {
			throw new IllegalStateException("Cannot handle more than 2 parameters at the time, nb parameters in: " + params.length);
		}
	}

	private void loadParam1(Object object) {
		if (object instanceof File) {
			reportFile = (File) object;
			parseReportFile();
		} else {
			throw new UnsupportedOperationException("unsupported parameter type");
		}
		// TODO: else
	}

	private void loadParams2(Object object, Object object2) {
		// TODO
	}

	private void parseReportFile() {
		if (reportFile != null) {
			ReportXMLParser.parseReportFile(reportFile);
			tssReport = ReportXMLParser.getTssReport();
			dsasReport = ReportXMLParser.getDsasReport();
			displayTSSReport();
			displayDSASReport();
		}
	}

	private void displayTSSReport() {
		if (tssReport != null) {
			Platform.runLater(() -> tssReportViewer.displayReport(tssReport));
		}
	}

	private void displayDSASReport() {
		if (dsasReport != null) {
			Platform.runLater(() -> dsasReportViewer.displayReport(dsasReport));
		}
	}

}
