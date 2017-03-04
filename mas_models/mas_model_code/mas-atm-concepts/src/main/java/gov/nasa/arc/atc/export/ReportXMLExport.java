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

import gov.nasa.arc.atc.reports.DSASReporter;
import gov.nasa.arc.atc.reports.TSSReporter;

/**
 * 
 * @author ahamon
 *
 */
public class ReportXMLExport {
	// hum...
	public static final String TSS = "TSS";
	public static final String DSAS = "DSAS";

	public static final String REPORT_GROUP_ELEMENT = "REPORTS";
	public static final String REPORT_ELEMENT = "REPORT";
	public static final String INVOCATION_ELEMENT = "INVOCATION";

	public static final String ITEM_ELEMENT = "ITEM";
	public static final String LEAD_ELEMENT = "LEAD";
	public static final String TRAIL_ELEMENT = "TRAIL";

	public static final String ALGO_ATTRIBUTE = "algorithm";
	public static final String SCENARIO_ATTRIBUTE = "scenario";
	public static final String NB_STEPS_ATTRIBUTE = "nbSteps";
	public static final String STEP_DURATION_ATTRIBUTE = "stepDuration";
	public static final String TIME_ATTRIBUTE = "time";

	private static final Logger LOG = Logger.getGlobal();

	private ReportXMLExport() {
		// private utility class
	}

	/**
	 * Export all the TSS and DSAS report inputs that are registered
	 * 
	 * @param file the file to write into
	 * @return if the export is successful
	 */
	public static boolean exportReports(File file) {
		if ((TSSReporter.getReportInputs().isEmpty() && DSASReporter.getReportInputs().isEmpty()) || file == null) {
			return false;
		}
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(REPORT_GROUP_ELEMENT);
			doc.appendChild(rootElement);

			// actual export
			TSSReportExporter.exportTSSReports(doc, rootElement);
			DSASReportExporter.exportDSASReports(doc, rootElement);
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
			LOG.log(Level.SEVERE, " Exception while exporting tss report {0} :: {1}", new Object[] { file.getName(), ex });
			return false;
		}
		return true;
	}

}
