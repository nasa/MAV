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

package gov.nasa.arc.atc.export;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.arc.atc.reports.DSASReporter;
import gov.nasa.arc.atc.reports.dsas.DSASReportInputs;

/**
 * 
 * @author ahamon
 *
 */
public class DSASReportExporter {

	private DSASReportExporter() {
		// private utility constructor
	}

	/**
	 * 
	 * @param doc the xml document all the exports shall be written in
	 * @param rootElement the xml parent node element for the report element
	 */
	public static void exportDSASReports(Document doc, Element rootElement) {
		DSASReporter.getReportInputs().stream().forEach(reportInput -> exportDSASReport(doc, rootElement, reportInput));
	}

	/**
	 * 
	 * @param doc the xml document the export shall be written in
	 * @param rootElement the xml parent node element for the report element
	 * @param reportInputs the report input to export
	 */
	public static void exportDSASReport(Document doc, Element rootElement, DSASReportInputs reportInputs) {
		Element reportElement = doc.createElement(ReportXMLExport.REPORT_ELEMENT);
		// TODO give dsas version
		reportElement.setAttribute(ReportXMLExport.ALGO_ATTRIBUTE, ReportXMLExport.DSAS);
		reportElement.setAttribute(ReportXMLExport.SCENARIO_ATTRIBUTE, reportInputs.getScenarioName());
		reportElement.setAttribute(ReportXMLExport.NB_STEPS_ATTRIBUTE, Integer.toString(reportInputs.getSimulationDuration()));
		reportElement.setAttribute(ReportXMLExport.STEP_DURATION_ATTRIBUTE, Integer.toString(reportInputs.getStepDuration()));
		//
		SequencesXMLUtils.createArrivalsIN(doc, reportElement, reportInputs.getAllArrivalsIN());
		SequencesXMLUtils.createArrivalsOUT(doc, reportElement, reportInputs.getAllArrivalsOUT());

		SequencesXMLUtils.createDeparturesIN(doc, reportElement, reportInputs.getAllDeparturesIN());
		SequencesXMLUtils.createDeparturesOUT(doc, reportElement, reportInputs.getAllDeparturesOUT());
		rootElement.appendChild(reportElement);
	}

}
