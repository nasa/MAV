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
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import gov.nasa.arc.atc.scenariogen.core.ArrivalControllerConf;
import gov.nasa.arc.atc.scenariogen.core.RichFlightPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.geography.Waypoint;

/**
 * 
 * @author ahamon
 *
 */
public class FlightPlanDataLoader {

	private FlightPlanDataLoader() {
		// private utility constructor
	}

	public static void main(String[] args) {
		File destFile = new File("/Desktop/FlightDataWeek2Run6.xml");
		FlightPlanDataSet dataSet = parseXML(destFile);
		System.err.println("=> " + dataSet);
	}

	/**
	 * 
	 * @param dataFile the file to parse
	 * @return the corresponding {@link FlightPlanDataSet}
	 */
	public static FlightPlanDataSet parseXML(File dataFile) {
		if (dataFile != null) {
			FlightPlanDataSet dataSet = new FlightPlanDataSet();
			//
			Document document;
			DocumentBuilderFactory builderFactory;
			builderFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				InputSource source = new InputSource(dataFile.getAbsolutePath());
				document = builder.parse(source);
				Element e = document.getDocumentElement();
				//
				parseWaypoints(e, dataSet);
				//
				parseControllersGroup(e, dataSet);
				//
				parseRichFlightPlans(e, dataSet);
				//
				return dataSet;
			} catch (IOException | SAXException | ParserConfigurationException ex) {
				Logger.getGlobal().log(Level.SEVERE, " {0}", ex);
			}

		}
		return null;
	}

	/*
	 * WAYPOINTS
	 */

	private static void parseWaypoints(Element e, FlightPlanDataSet dataSet) {
		NodeList nodeList = e.getElementsByTagName(FlighPlanDataExporter.WAYPOINT_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			ATCNode node = parseSingleWaypoint(newE);
			dataSet.addNode(node);
		}
	}

	private static ATCNode parseSingleWaypoint(Element e) {
		final String wptName = e.getAttribute(FlighPlanDataExporter.NAME_ATTRIBUTE);
		final double latitude = Double.parseDouble(e.getAttribute(FlighPlanDataExporter.LATITUDE_ATTRIBUTE));
		final double longitude = Double.parseDouble(e.getAttribute(FlighPlanDataExporter.LONGITUDE_ATTRIBUTE));
		final boolean isMeterFix = e.hasAttribute(FlighPlanDataExporter.IS_METER_FIX_ATRIBUTE) && Boolean.parseBoolean(e.getAttribute(FlighPlanDataExporter.IS_METER_FIX_ATRIBUTE));
		// TODO analyze in change in API is needed
		return new Waypoint(wptName, latitude, longitude, isMeterFix);
	}
	
	/*
	 * CONTROLLERS
	 */
	private static void parseControllersGroup(Element e, FlightPlanDataSet dataSet){
		NodeList controllerList = e.getElementsByTagName(FlighPlanDataExporter.CONTROLLER_ELEMENT);
		for(int i= 0 ; i<controllerList.getLength();i++){
			final gov.nasa.arc.atc.scenariogen.core.ArrivalControllerConf controller = createController( (Element)controllerList.item(i));
			dataSet.addController(controller);
		}
	}

	private static ArrivalControllerConf createController(Element e){
		String controllerName = e.getAttribute(FlighPlanDataExporter.NAME_ATTRIBUTE);
		String type = e.getAttribute(FlighPlanDataExporter.TYPE_ATTRIBUTE);
		String handOffController = e.getAttribute(FlighPlanDataExporter.HAND_OFF_CONTROLLER_ATTRIBUTE);
		String handOffWaypoint = e.getAttribute(FlighPlanDataExporter.HAND_OFF_WPT_ATTRIBUTE);
		List<String> nodes = new LinkedList<>();
		NodeList nodeList = e.getElementsByTagName(FlighPlanDataExporter.CONTROLLED_NODE_ELEMENT);
		for(int i = 0;i<nodeList.getLength();i++){
			nodes.add(((Element)nodeList.item(i)).getAttribute(FlighPlanDataExporter.NAME_ATTRIBUTE));
		}
		return new ArrivalControllerConf(controllerName, type, nodes, handOffWaypoint, handOffController);
	}
	

	/*
	 * FLIGHT PLANS
	 */

	private static void parseRichFlightPlans(Element e, FlightPlanDataSet dataSet) {
		NodeList nodeList = e.getElementsByTagName(FlighPlanDataExporter.FLIGHTPLAN_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			RichFlightPlan fpl = parseSingleFlightPlan(newE, dataSet);
			dataSet.addFlightPlan(fpl);
		}
	}

	private static RichFlightPlan parseSingleFlightPlan(Element e, FlightPlanDataSet dataSet) {
		String scenario = e.getAttribute(FlighPlanDataExporter.SCENARIO_NAME_ATTRIBUTE);
		String fplName = e.getAttribute(FlighPlanDataExporter.NAME_ATTRIBUTE);
		double initLat = Double.parseDouble(e.getAttribute(FlighPlanDataExporter.INIT_LATITUDE_ATTRIBUTE));
		double initLng = Double.parseDouble(e.getAttribute(FlighPlanDataExporter.INIT_LONGITUDE_ATTRIBUTE));
		double initAlt = Double.parseDouble(e.getAttribute(FlighPlanDataExporter.INIT_ALTITUDE_ATTRIBUTE));
		double initSpeed = Double.parseDouble(e.getAttribute(FlighPlanDataExporter.INIT_SPEED_ATTRIBUTE));

		// Flight segments
		NodeList sList = e.getElementsByTagName(FlighPlanDataExporter.SEGMENT_ELEMENT);
		FlightSegment[] segments = new FlightSegment[sList.getLength()];
		for (int i = 0; i < sList.getLength(); i++) {
			Element sElement = (Element) sList.item(i);
			final double endAltitude = Double.parseDouble(sElement.getAttribute(FlighPlanDataExporter.END_ALTITUDE_ATTRIBUTE));
			final double endSpeed = Double.parseDouble(sElement.getAttribute(FlighPlanDataExporter.END_SPEED_ATTRIBUTE));
			final int index = Integer.parseInt(sElement.getAttribute(FlighPlanDataExporter.INDEX_ATTRIBUTE));
			final String from = sElement.getAttribute(FlighPlanDataExporter.FROM_WAYPOINT_ATTRIBUTE);
			final String to = sElement.getAttribute(FlighPlanDataExporter.TO_WAYPOINT_ATTRIBUTE);
			segments[index] = new FlightSegment(from + "_" + to, fplName, dataSet.getNode(from), dataSet.getNode(to), endAltitude, endSpeed);
		}

		// Times
		Map<String, Integer> times = new HashMap<>();
		NodeList tList = e.getElementsByTagName(FlighPlanDataExporter.CROSSING_TIME_ELEMENT);
		for (int i = 0; i < tList.getLength(); i++) {
			Element tElement = (Element) tList.item(i);
			final String nodeName = tElement.getAttribute(FlighPlanDataExporter.NODE_ATTRIBUTE);
			final Integer time = Integer.parseInt(tElement.getAttribute(FlighPlanDataExporter.TIME_ATTRIBUTE));
			times.put(nodeName, time);
		}

		return new RichFlightPlan(scenario, fplName, segments, times, new Position(new Coordinates(initLat, initLng), initAlt), initSpeed);
	}

}
