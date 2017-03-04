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

import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Region;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.geography.Segment;
import gov.nasa.arc.atc.geography.Sector;

/**
 * @author ahamon
 */
public class ATCGeographyExporter {

    public static final String GEO_ROOT_ELEMENT = "GEOGRAPHY";
    public static final String WAYPOINT_GROUP_ELEMENT = "Waypoints";
    public static final String WAYPOINT_ELEMENT = "waypoint";
    public static final String AIRPORT_GROUP_ELEMENT = "Airports";
    public static final String AIRPORT_ELEMENT = "airport";
    public static final String RUNWAY_ELEMENT = "runway";
    public static final String SECTOR_GROUP_ELEMENT = "Sectors";
    public static final String SECTOR_ELEMENT = "sector";
    public static final String SECTOR_EDGE_ELEMENT = "edge";
    public static final String REGION_ELEMENT = "region";
    public static final String ARRIVAL_GROUP_ELEMENT = "Arrivals";
    public static final String ARRIVAL_ELEMENT = "arrival";
    public static final String DEPARTURE_GROUP_ELEMENT = "Departures";
    public static final String DEPARTURE_ELEMENT = "departure";
    public static final String SEGMENT_GROUP_ELEMENT = "Segments";
    public static final String SEGMENT_ELEMENT = "segment";
    //
    public static final String NAME_ATTRIBUTE = "name";
    public static final String LATITUDE_ATTRIBUTE = "latitude";
    public static final String LONGITUDE_ATTRIBUTE = "longitude";
    public static final String QFU_ATTRIBUTE = "qfu";
    public static final String MIN_ALTITUDE_ATTRIBUTE = "minAltitude";
    public static final String MAX_ALTITUDE_ATTRIBUTE = "maxAltitude";
    public static final String FROM_WAYPOINT_ATTRIBUTE = "fromWaypoint";
    public static final String TO_WAYPOINT_ATTRIBUTE = "toWaypoint";
    public static final String ID_ATTRIBUTE = "id";
    public static final String IS_METER_FIX_ATTRIBUTE = "isMeterFix";


    private static final Logger LOG = Logger.getGlobal();

    private ATCGeographyExporter() {
        // private utility constructor
    }

    /**
     * @param geography the {@link ATCGeography} to save
     * @param destFile  the file save into
     * @return true if the export is successful
     */
    public static boolean exportGeographyToXML(ATCGeography geography, File destFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(GEO_ROOT_ELEMENT);
            doc.appendChild(rootElement);
            // Create Waypoints
            createWaypointsGroup(doc, rootElement, geography);
            // Create airports
            createAirportsGroup(doc, rootElement, geography);
            // Create Sectors
            createSectorsGroup(doc, rootElement, geography);
            // Create arrival nodes
            createArrivalsGroup(doc, rootElement, geography);
            // Create departure nodes
            createDeparturesGroup(doc, rootElement, geography);
            // Create Segments
            createSegmentsGroup(doc, rootElement, geography);
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
                String correctFilePath = destFile.getAbsolutePath() + ".xml";
                File correctFile = new File(correctFilePath);
                result = new StreamResult(correctFile);
            }
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            LOG.log(Level.SEVERE, " Exception while exporting atc geography :: {0}", ex);
            return false;
        }
        return true;
    }

	/*
     * WAYPOINTS
	 */

    private static void createWaypointsGroup(Document doc, Element rootElement, ATCGeography geography) {
        Element waypointsGroupElement = doc.createElement(WAYPOINT_GROUP_ELEMENT);
        geography.getWaypoints().forEach(waypoint -> createWaypoint(doc, waypointsGroupElement, waypoint));
        rootElement.appendChild(waypointsGroupElement);
    }

    private static void createWaypoint(Document doc, Element parentElement, ATCNode waypoint) {
        Element waypointElement = doc.createElement(WAYPOINT_ELEMENT);
        //
        waypointElement.setAttribute(NAME_ATTRIBUTE, waypoint.getName());
        waypointElement.setAttribute(LATITUDE_ATTRIBUTE, Double.toString(waypoint.getLatitude()));
        waypointElement.setAttribute(LONGITUDE_ATTRIBUTE, Double.toString(waypoint.getLongitude()));
        waypointElement.setAttribute(IS_METER_FIX_ATTRIBUTE, Boolean.toString(waypoint.isMeterFix()));
        //
        parentElement.appendChild(waypointElement);
    }

	/*
	 * ARRIVALS
	 */

    private static void createArrivalsGroup(Document doc, Element rootElement, ATCGeography geography) {
        Element arrivalsGroupElement = doc.createElement(ARRIVAL_GROUP_ELEMENT);
        geography.getArrivalNodes().forEach(arrival -> createArrival(doc, arrivalsGroupElement, arrival));
        rootElement.appendChild(arrivalsGroupElement);
    }

    private static void createArrival(Document doc, Element parentElement, ATCNode arrival) {
        Element arrivalElement = doc.createElement(ARRIVAL_ELEMENT);
        arrivalElement.setAttribute(NAME_ATTRIBUTE, arrival.getName());
        parentElement.appendChild(arrivalElement);
    }

	/*
	 * DEPARTURES
	 */

    private static void createDeparturesGroup(Document doc, Element rootElement, ATCGeography geography) {
        Element departuresGroupElement = doc.createElement(DEPARTURE_GROUP_ELEMENT);
        geography.getDepartureNodes().forEach(departure -> createDeparture(doc, departuresGroupElement, departure));
        rootElement.appendChild(departuresGroupElement);
    }

    private static void createDeparture(Document doc, Element parentElement, ATCNode departure) {
        Element departureElement = doc.createElement(DEPARTURE_ELEMENT);
        departureElement.setAttribute(NAME_ATTRIBUTE, departure.getName());
        parentElement.appendChild(departureElement);
    }

	/*
	 * AIRPORTS
	 */

    private static void createAirportsGroup(Document doc, Element rootElement, ATCGeography geography) {
        Element airportsGroupElement = doc.createElement(AIRPORT_GROUP_ELEMENT);
        geography.getAirports().forEach(airport -> createAirport(doc, airportsGroupElement, airport));
        rootElement.appendChild(airportsGroupElement);
    }

    private static void createAirport(Document doc, Element parentElement, Airport airport) {
        Element airportElement = doc.createElement(AIRPORT_ELEMENT);
        //
        airportElement.setAttribute(NAME_ATTRIBUTE, airport.getName());
        airportElement.setAttribute(LATITUDE_ATTRIBUTE, Double.toString(airport.getLatitude()));
        airportElement.setAttribute(LONGITUDE_ATTRIBUTE, Double.toString(airport.getLongitude()));
        airport.getRunways().forEach(runway -> createRunway(doc, airportElement, runway));
        //
        parentElement.appendChild(airportElement);
    }

    private static void createRunway(Document doc, Element parentElement, Runway runway) {
        Element runwayElement = doc.createElement(RUNWAY_ELEMENT);
        //
        runwayElement.setAttribute(NAME_ATTRIBUTE, runway.getName());
        runwayElement.setAttribute(QFU_ATTRIBUTE, Integer.toString(runway.getQFU()));
        //
        parentElement.appendChild(runwayElement);
    }

	/*
	 * SECTORS
	 */

    private static void createSectorsGroup(Document doc, Element rootElement, ATCGeography geography) {
        Element sectorsGroupElement = doc.createElement(SECTOR_GROUP_ELEMENT);
        geography.getSectors().forEach(sector -> createSector(doc, sectorsGroupElement, sector));
        rootElement.appendChild(sectorsGroupElement);
    }

    private static void createSector(Document doc, Element parentElement, Sector sector) {
        Element sectorElement = doc.createElement(SECTOR_ELEMENT);
        //
        sectorElement.setAttribute(NAME_ATTRIBUTE, sector.getName());
        sectorElement.setAttribute(ID_ATTRIBUTE, Integer.toString(sector.getID()));
        sector.getRegions().forEach(region -> createRegion(doc, sectorElement, region));
        //
        parentElement.appendChild(sectorElement);
    }

    private static void createRegion(Document doc, Element parentElement, Region region) {
        Element regionElement = doc.createElement(REGION_ELEMENT);
        regionElement.setAttribute(MIN_ALTITUDE_ATTRIBUTE, Integer.toString(region.getMinAltitude()));
        regionElement.setAttribute(MAX_ALTITUDE_ATTRIBUTE, Integer.toString(region.getMaxAltitude()));
        region.getVertices().forEach(vertex -> createRegionEdges(doc, regionElement, vertex));
        parentElement.appendChild(regionElement);
    }

    private static void createRegionEdges(Document doc, Element parentElement, Coordinates vertex) {
        Element edgeElement = doc.createElement(SECTOR_EDGE_ELEMENT);
        //
        edgeElement.setAttribute(LATITUDE_ATTRIBUTE, Double.toString(vertex.getLatitude()));
        edgeElement.setAttribute(LONGITUDE_ATTRIBUTE, Double.toString(vertex.getLongitude()));
        //
        parentElement.appendChild(edgeElement);
    }

	/*
	 * SEGMENTS
	 */

    private static void createSegmentsGroup(Document doc, Element rootElement, ATCGeography geography) {
        Element segmentsGroupElement = doc.createElement(SEGMENT_GROUP_ELEMENT);
        geography.getSegments().forEach(segment -> createSegment(doc, segmentsGroupElement, segment));
        rootElement.appendChild(segmentsGroupElement);
    }

    private static void createSegment(Document doc, Element parentElement, Segment segment) {
        Element segmentElement = doc.createElement(SEGMENT_ELEMENT);
        //
        segmentElement.setAttribute(FROM_WAYPOINT_ATTRIBUTE, segment.getFromWaypoint().getName());
        segmentElement.setAttribute(TO_WAYPOINT_ATTRIBUTE, segment.getToWaypoint().getName());
        //
        parentElement.appendChild(segmentElement);
    }

}
