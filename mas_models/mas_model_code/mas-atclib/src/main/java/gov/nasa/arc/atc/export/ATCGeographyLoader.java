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

import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Region;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.geography.Sector;
import gov.nasa.arc.atc.geography.Segment;
import gov.nasa.arc.atc.geography.Waypoint;

/**
 * 
 * @author ahamon
 *
 */
public class ATCGeographyLoader {

	private ATCGeographyLoader() {
		// private utility constructor
	}

	/**
	 * 
	 * @param geographyFile the file to parse
	 * @return the corresponding {@link ATCGeography}
	 */
	public static ATCGeography parseXML(File geographyFile) {
		if (geographyFile != null) {
			ATCGeography geography = new ATCGeography("");
			//
			Document document;
			DocumentBuilderFactory builderFactory;
			builderFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				InputSource source = new InputSource(geographyFile.getAbsolutePath());
				document = builder.parse(source);
				Element e = document.getDocumentElement();
				// get airports
				parseAirports(e, geography);
				// get waypoints
				parseWaypoints(e, geography);
				// get arrival nodes
				parseArrivals(e, geography);
				// get departure nodes
				parseDepartures(e, geography);
				// get segments
				parseSegments(e, geography);
				// get sectors
				parseSectors(e, geography);
				//
				return geography;
			} catch (IOException | SAXException | ParserConfigurationException ex) {
				Logger.getGlobal().log(Level.SEVERE, " {0}", ex);
			}

		}
		return null;
	}

	/*
	 * WAYPOINTS
	 */

	private static void parseWaypoints(Element e, ATCGeography geography) {
		NodeList nodeList = e.getElementsByTagName(ATCGeographyExporter.WAYPOINT_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			Waypoint waypoint = parseSingleWaypoint(newE);
			geography.addWaypoint(waypoint);
		}
	}

	private static Waypoint parseSingleWaypoint(Element e) {
		final String wptName = e.getAttribute(ATCGeographyExporter.NAME_ATTRIBUTE);
		final double latitude = Double.parseDouble(e.getAttribute(ATCGeographyExporter.LATITUDE_ATTRIBUTE));
		final double longitude = Double.parseDouble(e.getAttribute(ATCGeographyExporter.LONGITUDE_ATTRIBUTE));
		final boolean isMeterFix = e.hasAttribute(ATCGeographyExporter.IS_METER_FIX_ATTRIBUTE) ? Boolean.parseBoolean(e.getAttribute(ATCGeographyExporter.IS_METER_FIX_ATTRIBUTE)) : false;
		return new Waypoint(wptName, latitude, longitude, isMeterFix);
	}

	/*
	 * AIRPORTS
	 */

	private static void parseAirports(Element e, ATCGeography geography) {
		NodeList nodeList = e.getElementsByTagName(ATCGeographyExporter.AIRPORT_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			Airport airport = parseSingleAirport(newE);
			geography.addAirport(airport);
		}
	}

	private static Airport parseSingleAirport(Element airportElement) {
		final String airportName = airportElement.getAttribute(ATCGeographyExporter.NAME_ATTRIBUTE);
		final double latitude = Double.parseDouble(airportElement.getAttribute(ATCGeographyExporter.LATITUDE_ATTRIBUTE));
		final double longitude = Double.parseDouble(airportElement.getAttribute(ATCGeographyExporter.LONGITUDE_ATTRIBUTE));
		//
		Airport airport = new Airport(airportName, latitude, longitude);
		//
		NodeList runwayList = airportElement.getElementsByTagName(ATCGeographyExporter.RUNWAY_ELEMENT);
		for (int i = 0; i < runwayList.getLength(); i++) {
			Element runwayElement = (Element) runwayList.item(i);
			Runway runway = parseSingleRunway(runwayElement, airport);
			airport.addRunway(runway);
		}
		return airport;
	}

	private static Runway parseSingleRunway(Element runwayElement, Airport airport) {
		final String runwayName = runwayElement.getAttribute(ATCGeographyExporter.NAME_ATTRIBUTE);
		final int qfu = Integer.parseInt(runwayElement.getAttribute(ATCGeographyExporter.QFU_ATTRIBUTE));
		return new Runway(runwayName, airport, qfu);
	}

	/*
	 * ARRIVAL
	 */
	private static void parseArrivals(Element e, ATCGeography geography) {
		NodeList nodeList = e.getElementsByTagName(ATCGeographyExporter.ARRIVAL_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			parseSingleArrival(newE, geography);
		}
	}

	private static void parseSingleArrival(Element arrivalElement, ATCGeography geography) {
		final String arrivalName = arrivalElement.getAttribute(ATCGeographyExporter.NAME_ATTRIBUTE);
		for (ATCNode node : geography.getWaypoints()) {
			if (node.getName().equals(arrivalName)) {
				geography.addArrivalNode(node);
				return;
			}
		}
	}

	/*
	 * DEPARTURE
	 */
	private static void parseDepartures(Element e, ATCGeography geography) {
		NodeList nodeList = e.getElementsByTagName(ATCGeographyExporter.DEPARTURE_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			parseSingleDeparture(newE, geography);
		}
	}

	private static void parseSingleDeparture(Element departureElement, ATCGeography geography) {
		final String departureName = departureElement.getAttribute(ATCGeographyExporter.NAME_ATTRIBUTE);
		for (ATCNode node : geography.getWaypoints()) {
			if (node.getName().equals(departureName)) {
				geography.addDepartureNode(node);
				return;
			}
		}
	}

	/*
	 * SEGMENTS
	 */
	private static void parseSegments(Element e, ATCGeography geography) {
		NodeList nodeList = e.getElementsByTagName(ATCGeographyExporter.SEGMENT_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			Segment segment = parseSingleSegment(newE, geography);
			geography.addSegment(segment);
		}
	}

	private static Segment parseSingleSegment(Element runwayElement, ATCGeography geography) {
		final String fromWPTName = runwayElement.getAttribute(ATCGeographyExporter.FROM_WAYPOINT_ATTRIBUTE);
		final String toWPTName = runwayElement.getAttribute(ATCGeographyExporter.TO_WAYPOINT_ATTRIBUTE);
		ATCNode fromNode = geography.getNodeByName(fromWPTName);
		ATCNode toNode = geography.getNodeByName(toWPTName);
		return new Segment(fromNode, toNode);
	}

	/*
	 * SECTORS
	 */

	private static void parseSectors(Element e, ATCGeography geography) {
		NodeList nodeList = e.getElementsByTagName(ATCGeographyExporter.SECTOR_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			Sector sector = parseSingleSector(newE);
			geography.addSector(sector);
		}
	}

	private static Sector parseSingleSector(Element sectorElement) {
		final String name = sectorElement.getAttribute(ATCGeographyExporter.NAME_ATTRIBUTE);
		final int iD = Integer.parseInt(sectorElement.getAttribute(ATCGeographyExporter.ID_ATTRIBUTE));
		Sector sector = new Sector(name, iD);

		NodeList regionList = sectorElement.getElementsByTagName(ATCGeographyExporter.REGION_ELEMENT);
		for (int i = 0; i < regionList.getLength(); i++) {
			Element newE = (Element) regionList.item(i);
			Region region = parseSingleSectorRegion(newE);
			System.out.println("region: "+region);
			sector.addRegion(region);
		}
		return sector;
	}

	private static Region parseSingleSectorRegion(Element regionElement) {
		final int minAltitude = Integer.parseInt(regionElement.getAttribute(ATCGeographyExporter.MIN_ALTITUDE_ATTRIBUTE));
		final int maxAltitude = Integer.parseInt(regionElement.getAttribute(ATCGeographyExporter.MAX_ALTITUDE_ATTRIBUTE));
		Region region = new Region(minAltitude, maxAltitude);

		NodeList edgesList = regionElement.getElementsByTagName(ATCGeographyExporter.SECTOR_EDGE_ELEMENT);
		for (int i = 0; i < edgesList.getLength(); i++) {
			Element newE = (Element) edgesList.item(i);
			final double latitude = Double.parseDouble(newE.getAttribute(ATCGeographyExporter.LATITUDE_ATTRIBUTE));
			final double longitude = Double.parseDouble(newE.getAttribute(ATCGeographyExporter.LONGITUDE_ATTRIBUTE));
			region.addVertex(new Coordinates(latitude, longitude));
		}

		return region;
	}

}
