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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nasa.arc.atc.reports.DSASReport;
import gov.nasa.arc.atc.reports.TSSReport;

/**
 * 
 * @author ahamon
 *
 */
public class ReportXMLParser {

	private static TSSReport tssReport = null;
	private static DSASReport dsasReport = null;

	private static int uniqueID = 0;

	private ReportXMLParser() {
		// private utility constructor
	}

	public static void parseReportFile(File reportFile) {
		if (reportFile != null) {
			//
			Document document;
			DocumentBuilderFactory builderFactory;
			builderFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				InputSource source = new InputSource(reportFile.getAbsolutePath());
				document = builder.parse(source);
				Element e = document.getDocumentElement();
				NodeList reportList = e.getElementsByTagName(ReportXMLExport.REPORT_ELEMENT);
				for (int i = 0; i < reportList.getLength(); i++) {
					Element reportElement = (Element) reportList.item(i);
					parseReport(reportElement);
				}
				//
			} catch (IOException | SAXException | ParserConfigurationException ex) {
				Logger.getGlobal().log(Level.SEVERE, " {0}", ex);
			}

		}

	}

	public static TSSReport getTssReport() {
		return tssReport;
	}

	public static DSASReport getDsasReport() {
		return dsasReport;
	}

	public static int getNextUniqueID() {
		uniqueID++;
		return uniqueID;
	}

	private static void parseReport(Element reportElement) {
		String reportType = reportElement.getAttribute(ReportXMLExport.ALGO_ATTRIBUTE);
		switch (reportType) {
		case ReportXMLExport.TSS:
			tssReport = TSSReportParser.parseReport(reportElement);
			break;
		case ReportXMLExport.DSAS:
			dsasReport = DSASReportParser.parseReport(reportElement);
			break;
		default:
			throw new UnsupportedOperationException("Cannot parse report for algorithm: " + reportType);
		}
	}

}
