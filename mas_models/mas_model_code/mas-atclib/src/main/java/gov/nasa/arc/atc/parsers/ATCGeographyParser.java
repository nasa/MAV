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

package gov.nasa.arc.atc.parsers;

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

import gov.nasa.arc.atc.export.ATCGeographyExporter;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.geography.Segment;
import gov.nasa.arc.atc.geography.Waypoint;

/**
 * @author ahamon
 */
public class ATCGeographyParser extends Parser {

    // TODO: remove duplication with ATCGeography loader

    private static final Logger LOG = Logger.getGlobal();

    private ATCGeography geography;

    private Document document;
    private DocumentBuilderFactory builderFactory;
    //
    private NodeList waypointsList;
    private NodeList airportsList;
    private NodeList arrivalsList;
    private NodeList departuresList;
    private NodeList segmentsList;
    // using double for percentage calculations
    private double nbNodes;
    private double nbItemParsed;

    public ATCGeographyParser(File file2Parse) {
        super(file2Parse);
    }

    @Override
    public Object parse() {
        if (getFile() == null) {
            return null;
        }
        boolean structureOK = instanciateDocument();
        if (!structureOK) {
            return null;
        }
        geography = new ATCGeography(Long.toString(System.currentTimeMillis()));
        // so it's considered a double
        nbNodes = 0.0 + waypointsList.getLength() + airportsList.getLength() + arrivalsList.getLength() + departuresList.getLength() + segmentsList.getLength();
        nbItemParsed = 0;
        //
        parseAirports(geography);
        parseWaypoints(geography);
        parseArrivals(geography);
        parseDepartures(geography);
        parseSegments(geography);
        //
        updateProgress(1.0);
        setCompletedSuccessfully(true);
        //
        return geography;
    }

    public ATCGeography getResult() {
        return geography;
    }

    private boolean instanciateDocument() {
        builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            InputSource source = new InputSource(getFile().getAbsolutePath());
            document = builder.parse(source);
            builder.reset();
            Element e = document.getDocumentElement();
            // get waypoints
            waypointsList = e.getElementsByTagName(ATCGeographyExporter.WAYPOINT_ELEMENT);
            // get airports
            airportsList = e.getElementsByTagName(ATCGeographyExporter.AIRPORT_ELEMENT);
            // get arrival nodes
            arrivalsList = e.getElementsByTagName(ATCGeographyExporter.ARRIVAL_ELEMENT);
            // get departure nodes
            departuresList = e.getElementsByTagName(ATCGeographyExporter.DEPARTURE_ELEMENT);
            // get segments
            segmentsList = e.getElementsByTagName(ATCGeographyExporter.SEGMENT_ELEMENT);
            // get sectors

        } catch (IOException | SAXException | ParserConfigurationException ex) {
            LOG.log(Level.SEVERE, " {0}", ex);
            return false;
        }
        return true;
    }

	/*
     * WAYPOINTS
	 */

    private void parseWaypoints(ATCGeography geography) {
        for (int i = 0; i < waypointsList.getLength(); i++) {
            Element newE = (Element) waypointsList.item(i);
            Waypoint waypoint = parseSingleWaypoint(newE);
            geography.addWaypoint(waypoint);
            incrProgress();
        }
    }

    private Waypoint parseSingleWaypoint(Element e) {
        final String wptName = e.getAttribute(ATCGeographyExporter.NAME_ATTRIBUTE);
        final double latitude = Double.parseDouble(e.getAttribute(ATCGeographyExporter.LATITUDE_ATTRIBUTE));
        final double longitude = Double.parseDouble(e.getAttribute(ATCGeographyExporter.LONGITUDE_ATTRIBUTE));
        return new Waypoint(wptName, latitude, longitude);
    }

	/*
	 * AIRPORTS
	 */

    private void parseAirports(ATCGeography geography) {
        for (int i = 0; i < airportsList.getLength(); i++) {
            Element newE = (Element) airportsList.item(i);
            Airport airport = parseSingleAirport(newE);
            geography.addAirport(airport);
            incrProgress();
        }
    }

    private Airport parseSingleAirport(Element airportElement) {
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

    private Runway parseSingleRunway(Element runwayElement, Airport airport) {
        final String runwayName = runwayElement.getAttribute(ATCGeographyExporter.NAME_ATTRIBUTE);
        final int qfu = Integer.parseInt(runwayElement.getAttribute(ATCGeographyExporter.QFU_ATTRIBUTE));
        return new Runway(runwayName, airport, qfu);
    }

    /*
     * ARRIVAL
     */
    private void parseArrivals(ATCGeography geography) {
        for (int i = 0; i < arrivalsList.getLength(); i++) {
            Element newE = (Element) arrivalsList.item(i);
            parseSingleArrival(newE, geography);
            incrProgress();
        }
    }

    private void parseSingleArrival(Element arrivalElement, ATCGeography geography) {
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
    private void parseDepartures(ATCGeography geography) {
        for (int i = 0; i < departuresList.getLength(); i++) {
            Element newE = (Element) departuresList.item(i);
            parseSingleDeparture(newE, geography);
            incrProgress();
        }
    }

    private void parseSingleDeparture(Element departureElement, ATCGeography geography) {
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
    private void parseSegments(ATCGeography geography) {
        for (int i = 0; i < segmentsList.getLength(); i++) {
            Element newE = (Element) segmentsList.item(i);
            Segment segment = parseSingleSegment(newE, geography);
            geography.addSegment(segment);
            incrProgress();
        }
    }

    private Segment parseSingleSegment(Element runwayElement, ATCGeography geography) {
        final String fromWPTName = runwayElement.getAttribute(ATCGeographyExporter.FROM_WAYPOINT_ATTRIBUTE);
        final String toWPTName = runwayElement.getAttribute(ATCGeographyExporter.TO_WAYPOINT_ATTRIBUTE);
        ATCNode fromNode = geography.getNodeByName(fromWPTName);
        ATCNode toNode = geography.getNodeByName(toWPTName);
        return new Segment(fromNode, toNode);
    }

	/*
	 * PROGRESS
	 */

    private void incrProgress() {
        nbItemParsed++;
        updateProgress(nbItemParsed / nbNodes);
    }

}
