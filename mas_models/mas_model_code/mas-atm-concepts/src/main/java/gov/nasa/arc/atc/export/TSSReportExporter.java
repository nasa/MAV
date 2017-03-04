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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.arc.atc.reports.TSSReportItem;
import gov.nasa.arc.atc.reports.TSSReporter;
import gov.nasa.arc.atc.reports.tss.TSSReportInputs;

/**
 * 
 * @author ahamon
 *
 */
public class TSSReportExporter {

	private static final Logger LOG = Logger.getGlobal();

	private TSSReportExporter() {
		// private utility constructor
	}

	/**
	 * 
	 * @param doc the xml document all the exports shall be written in
	 * @param rootElement the xml parent node element for the report element
	 */
	public static void exportTSSReports(Document doc, Element rootElement) {
		TSSReporter.getReportInputs().stream().forEach(reportInput -> exportTSSReportInput(doc, rootElement, reportInput));
	}

	/**
	 * 
	 * @param doc the xml document the export shall be written in
	 * @param rootElement the xml parent node element for the report element
	 * @param reportInput the report input to export
	 */
	public static void exportTSSReportInput(Document doc, Element rootElement, TSSReportInputs reportInput) {
		Element reportElement = doc.createElement(ReportXMLExport.REPORT_ELEMENT);
		reportElement.setAttribute(ReportXMLExport.ALGO_ATTRIBUTE, ReportXMLExport.TSS);
		reportElement.setAttribute(ReportXMLExport.SCENARIO_ATTRIBUTE, reportInput.getScenarioName());
		reportElement.setAttribute(ReportXMLExport.NB_STEPS_ATTRIBUTE, Integer.toString(reportInput.getSimulationDuration()));
		reportElement.setAttribute(ReportXMLExport.STEP_DURATION_ATTRIBUTE, Integer.toString(reportInput.getStepDuration()));
		reportInput.getItems().forEach((time, list) -> createItemsAtGivenTime(doc, reportElement, time, list));
		SequencesXMLUtils.createArrivalsIN(doc, reportElement, reportInput.getAllArrivalsIN());
		SequencesXMLUtils.createArrivalsOUT(doc, reportElement, reportInput.getAllArrivalsOUT());
		rootElement.appendChild(reportElement);
	}

	/*
	 * private methods
	 */

	private static void createItemsAtGivenTime(Document doc, Element rootElement, int time, List<TSSReportItem> items) {
		Element invocationElement = doc.createElement(ReportXMLExport.INVOCATION_ELEMENT);
		invocationElement.setAttribute(ReportXMLExport.TIME_ATTRIBUTE, Integer.toString(time));
		items.forEach(item -> createItem(doc, invocationElement, item, time));
		rootElement.appendChild(invocationElement);
	}

	private static void createItem(Document doc, Element rootElement, TSSReportItem item, int time) {
		boolean timeMatches = time == item.getSimulationTime();
		assert timeMatches;
		if (timeMatches) {
			rootElement.appendChild(TSSReportItemXMLVisitor.exportItem(doc, item));
		} else {
			LOG.log(Level.SEVERE, "Time ( {0} do not match for item {1} ", new Object[] { time, item });
		}
	}

}
