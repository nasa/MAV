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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import gov.nasa.arc.atc.reports.TSSReportItem;
import gov.nasa.arc.atc.reports.tss.FullInitializationConflictDetection;
import gov.nasa.arc.atc.reports.tss.InitializationConflictDetection;
import gov.nasa.arc.atc.reports.tss.ScheduleConflictResolution;
import gov.nasa.arc.atc.reports.tss.SimpleInitializationConflictDetection;
import gov.nasa.arc.atc.reports.tss.SimpleScheduleConflictResolution;
import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.ReportItemProperties;
import gov.nasa.arc.atc.reports.TSSReport;
import gov.nasa.arc.atc.reports.TSSReportImpl;

/**
 * 
 * @author ahamon
 *
 */
public class TSSReportParser {

	private static final String NULL_DEBUG = "NULL";

	private TSSReportParser() {
		// private utility constructor
	}

	public static final TSSReport parseReport(File file) {
		if (file != null) {
			//
			Document document;
			DocumentBuilderFactory builderFactory;
			builderFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				InputSource source = new InputSource(file.getAbsolutePath());
				document = builder.parse(source);
				Element e = document.getDocumentElement();
				return parseReport(e);
				//
			} catch (IOException | SAXException | ParserConfigurationException ex) {
				Logger.getGlobal().log(Level.SEVERE, " {0}", ex);
			}
		}
		return null;
	}

	public static final TSSReport parseReport(Element reportElement) {
		Map<Integer, List<TSSReportItem>> items = new HashMap<>();
		Map<Integer, List<ArrivalInfo>> arrivalsInInfo = new HashMap<>();
		Map<Integer, List<ArrivalInfo>> arrivalsOutInfo = new HashMap<>();
		//
		String scenarioName = reportElement.getAttribute(ReportXMLExport.SCENARIO_ATTRIBUTE);
		int nbSteps = Integer.parseInt(reportElement.getAttribute(ReportXMLExport.NB_STEPS_ATTRIBUTE));
		int stepDuration = Integer.parseInt(reportElement.getAttribute(ReportXMLExport.STEP_DURATION_ATTRIBUTE));
		//
		NodeList invocationList = reportElement.getElementsByTagName(ReportXMLExport.INVOCATION_ELEMENT);
		for (int i = 0; i < invocationList.getLength(); i++) {
			Element invocation = (Element) invocationList.item(i);
			parseTSSInvocation(invocation, items);
		}
		//
		NodeList arrivalsINList = reportElement.getElementsByTagName(SequencesXMLUtils.ARRIVAL_IN_AT_GROUP);
		for (int i = 0; i < arrivalsINList.getLength(); i++) {
			Element arrivalsInAt = (Element) arrivalsINList.item(i);
			SequencesXMLUtils.parseArrivalsAt(arrivalsInAt, arrivalsInInfo);
		}
		//
		NodeList arrivalsOUTList = reportElement.getElementsByTagName(SequencesXMLUtils.ARRIVAL_OUT_AT_GROUP);
		for (int i = 0; i < arrivalsOUTList.getLength(); i++) {
			Element arrivalsOutAt = (Element) arrivalsOUTList.item(i);
			SequencesXMLUtils.parseArrivalsAt(arrivalsOutAt, arrivalsOutInfo);
		}
		//
		return new TSSReportImpl(scenarioName, items, nbSteps, stepDuration, arrivalsInInfo, arrivalsOutInfo);
	}

	// TODO move elsewhere
	public static FlightParameters parseFlightParameter(Element parameterElement) {
		double latitude = Double.parseDouble(parameterElement.getAttribute(TSSReportItemXMLVisitor.LATITUDE_ATTRIBUTE));
		double longitude = Double.parseDouble(parameterElement.getAttribute(TSSReportItemXMLVisitor.LONGITUDE_ATTRIBUTE));
		double altitude = Double.parseDouble(parameterElement.getAttribute(TSSReportItemXMLVisitor.ALTITUDE_ATTRIBUTE));
		double heading = Double.parseDouble(parameterElement.getAttribute(TSSReportItemXMLVisitor.HEADING_ATTRIBUTE));
		double airSpeed = Double.parseDouble(parameterElement.getAttribute(TSSReportItemXMLVisitor.AIRSPEED_ATTRIBUTE));
		double vSpeed = Double.parseDouble(parameterElement.getAttribute(TSSReportItemXMLVisitor.VSPEED_ATTRIBUTE));
		int status = Integer.parseInt(parameterElement.getAttribute(TSSReportItemXMLVisitor.STATUS_ATTRIBUTE));
		//
		return new FlightParameters(new Position(latitude, longitude, altitude), airSpeed, vSpeed, heading, status);
	}

	private static void parseTSSInvocation(Element invocation, Map<Integer, List<TSSReportItem>> allItems) {
		List<TSSReportItem> items = new ArrayList<>();
		//
		int invocationTime = Integer.parseInt(invocation.getAttribute(ReportXMLExport.TIME_ATTRIBUTE));
		//
		NodeList itemElements = invocation.getElementsByTagName(ReportXMLExport.ITEM_ELEMENT);
		for (int i = 0; i < itemElements.getLength(); i++) {
			Element itemElement = (Element) itemElements.item(i);
			parseItemElement(itemElement, items);
		}
		//
		allItems.put(invocationTime, Collections.unmodifiableList(items));
	}

	private static void parseItemElement(Element itemElement, List<TSSReportItem> items) {
		//TODO fix with proper cases
		String itemType = itemElement.getAttribute(TSSReportItemXMLVisitor.ITEM_TYPE_ATTRIBUTE);
		if (ScheduleConflictResolution.class.getSimpleName().equals(itemType)) {
			parseScheduleConflicResolution(itemElement, items);
		} else if (InitializationConflictDetection.class.getSimpleName().equals(itemType)||FullInitializationConflictDetection.class.getSimpleName().equals(itemType)) {
			parseInitializationConflictDetection(itemElement, items);
		} else {
			throw new UnsupportedOperationException(" FAIL PARSING :: " + itemType);
		}

	}

	private static void parseScheduleConflicResolution(Element itemElement, List<TSSReportItem> items) {
		//
		// <ITEM id="6" type="ScheduleConflictResolution">
		// <leadSegment fromWPT="RBV" toWPT="SHVAL"/>
		// <initialHSep>1.5214333422724822</initialHSep>
		// <minHSepReq>2.5</minHSepReq>
		// <trailNewParameters airSpeed="160.0" altitude="954.9199922796491" heading="211.9153754555445" latitude="40.82088023268911" longitude="-73.8412374048435" status="1" vSpeed="-13.55981657497258"/>
		// <lead name="DAL1816"/>
		// <trailNewSegment/>
		// <node name="LGA22"/>
		// <trail name="CHQ6249"/>
		// <delayNeeded>25</delayNeeded>
		// <leadParameters airSpeed="125.0" altitude="0.0" heading="0.0" latitude="40.78528" longitude="-73.87055" status="3" time="1410" vSpeed="0.0"/>
		// <trailOldSegment/>
		// <simulationTime>323</simulationTime>
		// <trailOldParameters airSpeed="125.0" altitude="615.2184616634" heading="211.9439266219387" latitude="40.80679875437206" longitude="-73.85283101563932" status="1" vSpeed="-13.614680318386492"/>
		// </ITEM>
		//
		int id = Integer.parseInt(itemElement.getAttribute(TSSReportItemXMLVisitor.ID_ATTRIBUTE));
		// TODO FIX
		Map<String, Object> reportValues = new HashMap<>();
		//
		reportValues.put(ReportItemProperties.LEAD_SEGMENT, NULL_DEBUG);
		reportValues.put(ReportItemProperties.TRAIL_NEW_SEGMENT, NULL_DEBUG);
		reportValues.put(ReportItemProperties.TRAIL_OLD_SEGMENT, NULL_DEBUG);
		//
		addInitialHSeparation(itemElement, reportValues);
		addMiniHSeparation(itemElement, reportValues);
		addLead(itemElement, reportValues);
		addTrail(itemElement, reportValues);
		addNode(itemElement, reportValues);
		addLeadParameter(itemElement, reportValues);
		addTrailNewParameter(itemElement, reportValues);
		addTrailOldParameter(itemElement, reportValues);
		addDelayNeeded(itemElement, reportValues);
		addSimTime(itemElement, reportValues);
		//
		ScheduleConflictResolution item = new SimpleScheduleConflictResolution(id, reportValues);
		items.add(item);
	}

	private static void addSimTime(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.SIMULATION_TIME);
		Element simTimePptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.SIMULATION_TIME, Integer.parseInt(simTimePptyElement.getTextContent()));
	}

	private static void addTrail(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.TRAIL);
		Element trailPptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.TRAIL, trailPptyElement.getAttribute(TSSReportItemXMLVisitor.NAME_ATTRIBUTE));
	}

	private static void addLead(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.LEAD);
		Element leadPptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.LEAD, leadPptyElement.getAttribute(TSSReportItemXMLVisitor.NAME_ATTRIBUTE));
	}

	private static void addMiniHSeparation(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.MIN_H_SEPARATION_REQUIRED);
		Element miniSepPptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.MIN_H_SEPARATION_REQUIRED, Double.parseDouble(miniSepPptyElement.getTextContent()));
	}

	private static void addTrailNewParameter(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.TRAIL_NEW_PARAMETERS);
		Element newTrailPptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.TRAIL_NEW_PARAMETERS, parseFlightParameter(newTrailPptyElement));
	}

	private static void addTrailOldParameter(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.TRAIL_OLD_PARAMETERS);
		Element oldTrailPptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.TRAIL_OLD_PARAMETERS, parseFlightParameter(oldTrailPptyElement));
	}

	private static void addDelayNeeded(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.DELAY_NEEDED);
		Element delayPptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.DELAY_NEEDED, Integer.parseInt(delayPptyElement.getTextContent()));
	}

	private static void addLeadParameter(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.LEAD_PARAMETERS);
		Element leadPptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.LEAD_PARAMETERS, parseFlightParameter(leadPptyElement));

	}

	private static void addNode(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.NODE);
		Element nodePptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.NODE, nodePptyElement.getAttribute(ReportItemProperties.NODE));
	}

	private static void addInitialHSeparation(Element itemElement, Map<String, Object> reportValues) {
		NodeList propertyList = itemElement.getElementsByTagName(ReportItemProperties.INITIAL_H_SEPARATION);
		Element initHSepPptyElement = (Element) propertyList.item(0);
		reportValues.put(ReportItemProperties.INITIAL_H_SEPARATION, Double.parseDouble(initHSepPptyElement.getTextContent()));
	}

	private static void parseInitializationConflictDetection(Element itemElement, List<TSSReportItem> items) {
		// <ITEM id="12" delay="445" lead="JBU316" node="LGA22" reason="InitializationConflictDetection" trail="DAL944" type="InitializationConflictDetection"/>
		int id = Integer.parseInt(itemElement.getAttribute(TSSReportItemXMLVisitor.ID_ATTRIBUTE));
		int simTime = Integer.parseInt(itemElement.getAttribute(TSSReportItemXMLVisitor.TIME_ATTRIBUTE));
		int delay = Integer.parseInt(itemElement.getAttribute(TSSReportItemXMLVisitor.DELAY_ATTRIBUTE));
		String node = itemElement.getAttribute(TSSReportItemXMLVisitor.NODE_ATTRIBUTE);
		String trail = itemElement.getAttribute(TSSReportItemXMLVisitor.TRAIL_ATTRIBUTE);
		String lead = itemElement.getAttribute(TSSReportItemXMLVisitor.LEAD_ATTRIBUTE);
		//
		InitializationConflictDetection item = new SimpleInitializationConflictDetection(id, simTime, node, lead, trail, delay);
		items.add(item);
	}

}
