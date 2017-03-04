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

package gov.nasa.arc.atc.scenariogen.base;

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

import gov.nasa.arc.atc.scenariogen.core.ArrivalControllerConf;
import gov.nasa.arc.atc.scenariogen.core.RichFlightPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightSegment;

/**
 * 
 * @author ahamon
 *
 */
public class FlighPlanDataExporter {

	// ROOT
	public static final String ROOT_ELEMENT = "DATA";
	
	// GROUPS
	public static final String WAYPOINT_GROUP_ELEMENT = "WAYPOINTS";
	public static final String CONTROLLER_GROUP_ELEMENT = "CONTROLLERS";
	public static final String FLIGHTPLAN_GROUP_ELEMENT = "FLIGHTPLANS";
	public static final String SEGMENT_GROUP_ELEMENT = "SEGMENTS";
	public static final String TIMES_GROUP_ELEMENT = "TIMES";
	
	// ELEMENTS
	public static final String WAYPOINT_ELEMENT = "waypoint";
	public static final String CONTROLLER_ELEMENT = "controller";
	public static final String CONTROLLED_NODE_ELEMENT = "cNode";
	public static final String FLIGHTPLAN_ELEMENT = "flightplan";
	public static final String SEGMENT_ELEMENT = "segment";
	public static final String CROSSING_TIME_ELEMENT = "crossingTime";
	
	// ATTRIBUTES
	public static final String END_ALTITUDE_ATTRIBUTE = "endAltitude";
	public static final String END_SPEED_ATTRIBUTE = "endSpeed";
	public static final String FROM_WAYPOINT_ATTRIBUTE = "fromWaypoint";
	public static final String INDEX_ATTRIBUTE = "index";
	public static final String INIT_ALTITUDE_ATTRIBUTE = "initAlt";
	public static final String INIT_LATITUDE_ATTRIBUTE = "initLat";
	public static final String INIT_LONGITUDE_ATTRIBUTE = "initLng";
	public static final String INIT_SPEED_ATTRIBUTE = "initSpeed";
	
	public static final String IS_METER_FIX_ATRIBUTE = "isMeterFix";
	public static final String LATITUDE_ATTRIBUTE = "latitude";
	public static final String LONGITUDE_ATTRIBUTE = "longitude";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String NODE_ATTRIBUTE = "node";
	public static final String SCENARIO_NAME_ATTRIBUTE = "scenarioName";
	public static final String TIME_ATTRIBUTE = "time";
	public static final String TO_WAYPOINT_ATTRIBUTE = "toWaypoint";

	public static final String TYPE_ATTRIBUTE = "type";
	public static final String HAND_OFF_CONTROLLER_ATTRIBUTE = "handOffController";
	public static final String HAND_OFF_WPT_ATTRIBUTE = "handOffWaypoint";

	
	private FlighPlanDataExporter() {
		// private utility constructor
	}

	public static boolean exportFlightPlanDataToXML(List<ATCNode> nodes, List<ArrivalControllerConf> controllers, List<RichFlightPlan> flightPlans, File destFile) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(ROOT_ELEMENT);
			doc.appendChild(rootElement);
			// Create Waypoints
			createWaypointsGroup(doc, rootElement, nodes);
			// Create Controllers
			createControllersGroup(doc, rootElement, controllers);
			// Create Rich flight Plans
			createRichFlightPlanGroup(doc, rootElement, flightPlans);

			//
			rootElement.normalize();
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result;
			if (destFile.getName().endsWith(".xml")) {
				result = new StreamResult(destFile);
			} else {
				File correctFile = new File(destFile.getAbsolutePath()+".xml");
				result = new StreamResult(correctFile);
			}
			transformer.transform(source, result);
		} catch (ParserConfigurationException | TransformerException ex) {
			Logger.getGlobal().log(Level.SEVERE, " Exception while exporting atc geography :: {0}", ex);
			return false;
		}
		return true;
	}

	/*
	 * WAYPOINTS
	 */

	private static void createWaypointsGroup(Document doc, Element rootElement, List<ATCNode> nodes) {
		Element waypointsGroupElement = doc.createElement(WAYPOINT_GROUP_ELEMENT);
		nodes.forEach(node -> createWaypoint(doc, waypointsGroupElement, node));
		rootElement.appendChild(waypointsGroupElement);
	}

	private static void createWaypoint(Document doc, Element parentElement, ATCNode waypoint) {
		Element waypointElement = doc.createElement(WAYPOINT_ELEMENT);
		//
		waypointElement.setAttribute(NAME_ATTRIBUTE, waypoint.getName());
		waypointElement.setAttribute(LATITUDE_ATTRIBUTE, Double.toString(waypoint.getLatitude()));
		waypointElement.setAttribute(LONGITUDE_ATTRIBUTE, Double.toString(waypoint.getLongitude()));
		waypointElement.setAttribute(IS_METER_FIX_ATRIBUTE, Boolean.toString(waypoint.isMeterFix()));
		//
		parentElement.appendChild(waypointElement);
	}
	
	/*
	 * CONTROLLERS
	 */
	private static void createControllersGroup(Document doc, Element rootElement, List<ArrivalControllerConf> controllers){
		Element controllersGroupElement = doc.createElement(CONTROLLER_GROUP_ELEMENT);
		controllers.forEach(node -> createController(doc, controllersGroupElement, node));
		rootElement.appendChild(controllersGroupElement);
	}
	
	private static void createController(Document doc, Element parentElement,ArrivalControllerConf controller){
		Element cElement = doc.createElement(CONTROLLER_ELEMENT);
		cElement.setAttribute(NAME_ATTRIBUTE, controller.getName());
		cElement.setAttribute(TYPE_ATTRIBUTE, controller.getType());
		cElement.setAttribute(HAND_OFF_CONTROLLER_ATTRIBUTE, controller.getHandOffController());
		cElement.setAttribute(HAND_OFF_WPT_ATTRIBUTE, controller.getHandOffWaypoint());
		controller.getNodes().forEach(node->{
			final Element nElement = doc.createElement(CONTROLLED_NODE_ELEMENT);
			nElement.setAttribute(NAME_ATTRIBUTE, node);
			cElement.appendChild(nElement);
		});
		parentElement.appendChild(cElement);
	}
	
	
	/*
	 * Flight plans
	 */
	
	private static void createRichFlightPlanGroup(Document doc, Element rootElement, List<RichFlightPlan> flightPlans) {
		Element fplGroupElement = doc.createElement(FLIGHTPLAN_GROUP_ELEMENT);
		flightPlans.forEach(fpl -> createRichFlightPlan(doc, fplGroupElement, fpl));
		rootElement.appendChild(fplGroupElement);
		
	}

	
	public static void createRichFlightPlan(Document doc, Element parentElement, RichFlightPlan fpl) {
		Element flpElement = doc.createElement(FLIGHTPLAN_ELEMENT);
		//
		flpElement.setAttribute(NAME_ATTRIBUTE, fpl.getFplName());
		flpElement.setAttribute(SCENARIO_NAME_ATTRIBUTE, fpl.getScenarioName());
		flpElement.setAttribute(INIT_LATITUDE_ATTRIBUTE, Double.toString(fpl.getInitPosition().getLatitude()));
		flpElement.setAttribute(INIT_LONGITUDE_ATTRIBUTE, Double.toString(fpl.getInitPosition().getLongitude()));
		flpElement.setAttribute(INIT_ALTITUDE_ATTRIBUTE, Double.toString(fpl.getInitPosition().getAltitude()));
		flpElement.setAttribute(INIT_SPEED_ATTRIBUTE, Double.toString(fpl.getInitSpeed()));
		// segments
		Element sGroupElement = doc.createElement(SEGMENT_GROUP_ELEMENT);
		final FlightSegment[] segments = fpl.getSegments();
		for(int i = 0;i<segments.length;i++){
			final Element sElement = doc.createElement(SEGMENT_ELEMENT);
			final FlightSegment s = segments[i];
			sElement.setAttribute(INDEX_ATTRIBUTE, Integer.toString(i));
			sElement.setAttribute(FROM_WAYPOINT_ATTRIBUTE, s.getFromWaypoint().getName());
			sElement.setAttribute(TO_WAYPOINT_ATTRIBUTE, s.getToWaypoint().getName());
			sElement.setAttribute(END_SPEED_ATTRIBUTE, Double.toString(s.getEndSpeed()));
			sElement.setAttribute(END_ALTITUDE_ATTRIBUTE, Double.toString(s.getdEndAltitude()));
			sGroupElement.appendChild(sElement);
		}
		flpElement.appendChild(sGroupElement);
		// times
		Element timesGroupElement = doc.createElement(TIMES_GROUP_ELEMENT);
		fpl.getTimes().forEach((name,time)->{
			final Element tElement = doc.createElement(CROSSING_TIME_ELEMENT);
			tElement.setAttribute(TIME_ATTRIBUTE, Integer.toString(time));
			tElement.setAttribute(NODE_ATTRIBUTE, name);
			timesGroupElement.appendChild(tElement);
		});
		flpElement.appendChild(timesGroupElement);
		//
		parentElement.appendChild(flpElement);
	}

}
