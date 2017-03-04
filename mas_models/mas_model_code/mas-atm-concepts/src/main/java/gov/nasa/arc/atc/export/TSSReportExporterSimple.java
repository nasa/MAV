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

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.arc.atc.reports.TSSReportItem;
import gov.nasa.arc.atc.reports.TSSReport;

/**
 * 
 * @author ahamon
 *
 */
public class TSSReportExporterSimple {

	// hum...
	public static final String TSS = "TSS";

	public static final String REPORT_ELEMENT = "REPORT";
	public static final String INVOCATION_ELEMENT = "INVOCATION";
	public static final String ITEM_ELEMENT = "ITEM";

	public static final String ALGO_ATTRIBUTE = "algorithm";
	public static final String SCENARIO_ATTRIBUTE = "scenario";
	public static final String NB_STEPS_ATTRIBUTE = "nbSteps";
	public static final String STEP_DURATION_ATTRIBUTE = "stepDuration";
	public static final String TIME_ATTRIBUTE = "time";
	public static final String DELAY_ATTRIBUTE = "delay";
	public static final String LEAD_ATTRIBUTE = "lead";
	public static final String TRAIL_ATTRIBUTE = "trail";
	public static final String NODE_ATTRIBUTE = "node";
	public static final String REASON_ATTRIBUTE = "reason";
	public static final String MESSAGE_ATTRIBUTE = "message";

	private static final Logger LOG = Logger.getGlobal();

	private TSSReportExporterSimple() {
		// private utility constructor
	}

	public static boolean exportReport(TSSReport report, File file) {
		if (report == null || file == null) {
			return false;
		}
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(REPORT_ELEMENT);
			rootElement.setAttribute(ALGO_ATTRIBUTE, TSS);
			rootElement.setAttribute(SCENARIO_ATTRIBUTE, report.getScenarioName());
			rootElement.setAttribute(NB_STEPS_ATTRIBUTE, Integer.toString(report.getSimulationDuration()));
			rootElement.setAttribute(STEP_DURATION_ATTRIBUTE, Integer.toString(report.getStepDuration()));
			doc.appendChild(rootElement);
			//
			createAllItems(doc, rootElement, report);
			//
			rootElement.normalize();
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result;
			if (file.getName().endsWith(".xml")) {
				result = new StreamResult(file);
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append(file.getAbsolutePath());
				sb.append(".xml");
				File correctFile = new File(sb.toString());
				result = new StreamResult(correctFile);
			}
			transformer.transform(source, result);
		} catch (ParserConfigurationException | TransformerException ex) {
			LOG.log(Level.SEVERE, " Exception while exporting tss report {0} :: {1}", new Object[] { report.getScenarioName(), ex });
			return false;
		}
		return true;
	}

	public static Element exportTSSReport(Document doc, TSSReport report) {
		Element rootElement = doc.createElement(REPORT_ELEMENT);
		rootElement.setAttribute(ALGO_ATTRIBUTE, TSS);
		rootElement.setAttribute(SCENARIO_ATTRIBUTE, report.getScenarioName());
		rootElement.setAttribute(NB_STEPS_ATTRIBUTE, Integer.toString(report.getSimulationDuration()));
		rootElement.setAttribute(STEP_DURATION_ATTRIBUTE, Integer.toString(report.getStepDuration()));
		//
		createAllItems(doc, rootElement, report);
		return rootElement;
	}

	private static void createAllItems(Document doc, Element rootElement, TSSReport report) {
		report.getReportItems().forEach((time, list) -> createItemsAtGivenTime(doc, rootElement, time, list));
	}

	private static void createItemsAtGivenTime(Document doc, Element rootElement, int time, List<TSSReportItem> items) {
		Element invocationElement = doc.createElement(INVOCATION_ELEMENT);
		invocationElement.setAttribute(TIME_ATTRIBUTE, Integer.toString(time));
		items.forEach(item -> createItem(doc, invocationElement, item, time));
		rootElement.appendChild(invocationElement);
	}

	private static void createItem(Document doc, Element rootElement, TSSReportItem item, int time) {
		boolean timeMatches = time == item.getSimulationTime();
		assert timeMatches;
		if (timeMatches) {
			Element itemElement = doc.createElement(ITEM_ELEMENT);
			itemElement.setAttribute(DELAY_ATTRIBUTE, Integer.toString(item.getDelay()));
			itemElement.setAttribute(LEAD_ATTRIBUTE, item.getLead());
			itemElement.setAttribute(TRAIL_ATTRIBUTE, item.getTrail());
			itemElement.setAttribute(NODE_ATTRIBUTE, item.getNodeName());
			itemElement.setAttribute(REASON_ATTRIBUTE, item.getReason());
			rootElement.appendChild(itemElement);
		} else {
			LOG.log(Level.SEVERE, "Time ( {0} do not match for item {1} ", new Object[] { time, item });
		}
	}

	// Map<Integer, List<ReportItem>> items, int simuDuration,int timeStep

}
