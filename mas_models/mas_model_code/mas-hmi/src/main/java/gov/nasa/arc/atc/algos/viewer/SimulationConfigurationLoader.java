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

package gov.nasa.arc.atc.algos.viewer;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.Configuration;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.brahms.BrahmsFlightPlan;
import gov.nasa.arc.atc.brahms.parsers.AirportBrahmsParser;
import gov.nasa.arc.atc.brahms.parsers.DepartureQueueParser;
import gov.nasa.arc.atc.brahms.parsers.FlightPlanBrahmsParser;
import gov.nasa.arc.atc.brahms.parsers.SegmentBrahmsParser;
import gov.nasa.arc.atc.brahms.parsers.SimulatedSlotBrahmsParser;
import gov.nasa.arc.atc.brahms.parsers.WaypointParser;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.simulation.DepartureQueue;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.SimulationProperties;

/**
 * @author ahamon
 */
public class SimulationConfigurationLoader {


    private static final InputStream CONF_STREAM = SimulationConfigurationLauncher.class.getResourceAsStream("config.properties");

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final Logger LOG = Logger.getGlobal();

    private static File scenarioConfigurationFile;
    private static File directory;
    private static File arrControllersFile;
    private static File airportsFile;
    private static File waypointsFile;
    private static File flightSegmentsFile;
    private static File flightPlanFile;
    private static File scenarioFile;
    private static File departureQueueFile;
    //
    private static Map<String, Airport> airports;
    //    private static Map<String, Waypoint> waypoints;
    private static Map<String, FlightSegment> flightSegments;
    private static Map<String, BrahmsFlightPlan> brahmsFlightPlans;
    private static List<SimulatedSlotMarker> allFlights;
    private static DepartureQueue departureQueue;
    //
    private static Properties properties;
    private static ATCGeography geography;
    private static SimulationContext simulationContext;

    private SimulationConfigurationLoader() {
        // private utility constructor
    }

    /**
     * @param scenarioConfigFile loads the configuration file and populate the SimulationConfigurationLoader
     */
    public static void loadConfiguration(File scenarioConfigFile) {
        //
        parseBrahmsConfiguration();
        //
        scenarioConfigurationFile = scenarioConfigFile;
        properties = Configuration.readConfigurationFile(scenarioConfigurationFile);
        //
        directory = scenarioConfigFile.getParentFile();

        // retrieve arrival controllers file
        loadArrivalControllers();
        // retrieve airport file
        loadAirportFile();
        // create ATCGeography
        createGeography();
        // retrieve waypoints file
        loadWaypointFile();
        // retrieve flight plans file
        createFlightPlans();

        // retrieve afo file
        scenarioFile = getFile(directory, properties.getProperty(Configuration.AIRCRAFTS_PPTY));
        allFlights = SimulatedSlotBrahmsParser.parseSlotAgents(geography, brahmsFlightPlans, flightSegments, scenarioFile);
        final boolean allFlightsNotNull = !allFlights.isEmpty();
        LOG.log(Level.FINE, "allFlightsNotNull {0}", allFlightsNotNull);
        assert allFlightsNotNull;

        // retrieve departure sequence
        createDepartureSequence();

        // allFlights.stream().forEach(slot -> {
        // System.err.println("FLIGHT ::"+slot.getName()+" "+slot.getFlightPlan());
        // });
        //
        // check flight plans integrity
        // TODO
        // allFlightPlans.forEach((name, fp) -> {
        // if (!fp.isValid()) {
        // throw new IllegalStateException("Flight plan " + name + " is not
        // valid");
        // }
        // fp.getWayPoints().forEach(wp -> {
        // if (!waypoints.containsKey(wp.getName())) {
        // throw new IllegalStateException("Found no Waypoint :" + wp);
        // }
        // });
        // });
        // check that each slot has a flight plan
        // TODO
        // allFlights.stream().forEach(slot -> {
        // if (!allFlightPlans.containsKey(slot.getName())) {
        // throw new IllegalStateException("Slot " + slot.getName() + " has not
        // associated flight plan");
        // }
        // });
        //
        createSimulationContext();
        if (isSimulationConfigurationValid()) {
            LOG.log(Level.INFO, "Simulation configuration load status is OK ");
        } else {
            LOG.log(Level.WARNING, "Simulation configuration load status is NOT OK !");
        }
    }

	/*
     * Loading methods
	 */

    private static void loadArrivalControllers() {
        // TODO put the key in the property file
        arrControllersFile = getFile(directory, "allArrivalControllers.b");
        if (arrControllersFile.exists()) {
            LOG.log(Level.INFO, "arrival controllers file exists: {0}", airportsFile);
            Object arrControllers = 0;
            final boolean arrControllersNotNull = arrControllers != null;
            LOG.log(Level.FINE, "airportsNotNull {0}", arrControllersNotNull);
            assert arrControllersNotNull;
        } else {
            airportsFile = null;
            LOG.log(Level.SEVERE, "arrival controllers file does NOT exist: {0}", airportsFile);
        }

        LOG.log(Level.SEVERE, "arrival controllers NOT parsed yet");
    }

    private static void loadAirportFile() {
        airportsFile = getFile(directory, properties.getProperty(Configuration.AIRPORTS_PPTY));
        if (airportsFile.exists()) {
            LOG.log(Level.INFO, "airport file exists: {0}", airportsFile);
            airports = AirportBrahmsParser.parseAirports(airportsFile);
            final boolean airportsNotEmpty = !airports.isEmpty();
            LOG.log(Level.FINE, "airportsNotNull {0}", airportsNotEmpty);
            assert airportsNotEmpty;
            LOG.log(Level.INFO, "airports found are: {0}", airports.keySet());
        } else {
            LOG.log(Level.WARNING, "airport file does NOT exist: {0}", airportsFile);
            airportsFile = null;
        }
    }

    private static void loadWaypointFile() {
        waypointsFile = getFile(directory, properties.getProperty(Configuration.WAYPOINTS_PPTY));
        if (waypointsFile.exists()) {
            LOG.log(Level.INFO, "waypoint file exists: {0}", waypointsFile);
            WaypointParser.parseWaypoints(waypointsFile, geography);
            final boolean waypointsNotEmpty = !geography.getWaypoints().isEmpty();
            LOG.log(Level.FINE, "waypointsNotNull {0}", waypointsNotEmpty);
            assert waypointsNotEmpty;
            LOG.log(Level.INFO, "waypoints found are: {0}", geography.getWaypoints());
        } else {
            LOG.log(Level.WARNING, "waypoint file does NOT exist: {0}", waypointsFile);
            waypointsFile = null;
        }
    }

    private static void createFlightPlans() {
        flightSegmentsFile = getFile(directory, properties.getProperty(Configuration.FLIGHT_SEGMENTS_PPTY));
        if (flightSegmentsFile.exists()) {
            LOG.log(Level.INFO, "flightSegmentsFile file exists: {0}", flightSegmentsFile);
            flightSegments = SegmentBrahmsParser.parseSegments(flightSegmentsFile, geography);
            final boolean flightSegmentsNotEmpty = !flightSegments.isEmpty();
            LOG.log(Level.FINE, "flightSegmentsNotNull {0}", flightSegmentsNotEmpty);
            assert flightSegmentsNotEmpty;
            LOG.log(Level.INFO, "flight segments found : {0}", flightSegments.size());
            // update geography
            flightSegments.forEach((name, segment) -> geography.addSegment(segment));

        } else {
            LOG.log(Level.WARNING, "flight segment file does NOT exist: {0}", flightSegmentsFile);
            flightSegmentsFile = null;
        }

        flightPlanFile = getFile(directory, properties.getProperty(Configuration.FLIGHT_PLANS_PPTY));
        // Retrieve Brahms flight plan file
        if (flightPlanFile.exists()) {
            LOG.log(Level.INFO, "flightPlanFile file exists: {0}", flightPlanFile);
            brahmsFlightPlans = FlightPlanBrahmsParser.parseBrahmsFPLs(flightPlanFile, flightSegments);
            LOG.log(Level.INFO, "flight plans found : {0}", brahmsFlightPlans.size());
            final boolean brahmsFlightPlansNotNull = brahmsFlightPlans != null;
            LOG.log(Level.FINE, "brahmsFlightPlansNotNull {0}", brahmsFlightPlansNotNull);
            assert brahmsFlightPlansNotNull;
            LOG.log(Level.FINE, "flight plans found are: {0}", brahmsFlightPlans.keySet());
        } else {
            LOG.log(Level.WARNING, "flight segment file does NOT exist: {0}", flightPlanFile);
        }
    }

    private static void createDepartureSequence() {
        // TODO: what if runway change and several departure queues?
        departureQueueFile = getFile(directory, properties.getProperty(Configuration.DEPARTURE_QUEUE_PPTY));
        if (departureQueueFile.exists()) {
            LOG.log(Level.INFO, "departureQueueFile file exists: {0}", departureQueueFile);
            departureQueue = DepartureQueueParser.parseDepartureQueue(departureQueueFile, geography, allFlights);
            LOG.log(Level.INFO, "found {0} departures", departureQueue.getDepartures().size());
        } else {
            LOG.log(Level.WARNING, "departure Queue file does NOT exist: {0}", departureQueueFile);
            departureQueueFile = null;
        }
    }

    /**
     * Method to get the file properties
     */
    private static void parseBrahmsConfiguration() {
        SimulationProperties.parseProperties(CONF_STREAM);
    }

    private static File getFile(File directory, String property) {
        return new File(directory.getPath() + FILE_SEPARATOR + property);
    }

    private static void createGeography() {
        if (isAirportsFileValid()) {
            geography = new ATCGeography("No Name");
            // airports
            airports.values().forEach(geography::addAirport);
        } else {
            LOG.log(Level.WARNING, "cannot createGeography: simulation configuration is not valid");
        }
    }

    private static void createSimulationContext() {
        if (isSimulationConfigurationValid()) {
            // create context with geography
            simulationContext = new SimulationContext(geography);
            // populate the context with the slots
            simulationContext.addSlots(allFlights);
        }
    }

    public static File getConfigurationFile() {
        return scenarioConfigurationFile;
    }

    public static File getScenarioFile() {
        return scenarioFile;
    }

    public static List<SimulatedSlotMarker> getAllFlights() {
        //todo check name
        return Collections.unmodifiableList(allFlights);
    }

    public static File getDepartureQueueFile() {
        return departureQueueFile;
    }

    public static File getAirportsFile() {
        return airportsFile;
    }

    public static File getDirectory() {
        return directory;
    }

    public static File getFlightSegmentsFile() {
        return flightSegmentsFile;
    }

    public static File getWaypointsFile() {
        return waypointsFile;
    }

    public static DepartureQueue getDepartureQueue() {
        return departureQueue;
    }

    public static boolean isConfigurationFileValid() {
        return airportsFile != null && waypointsFile != null && flightSegmentsFile != null && scenarioFile != null;
    }

    public static boolean isAirportsFileValid() {
        // TODO:
        return true;
    }

    public static boolean isWaypointsFileValid() {
        // TODO:
        return true;
    }

    public static boolean isScenarioFileValid() {
        // TODO:
        return true;
    }

    public static boolean isDeparturesFileValid() {
        // TODO:
        return true;
    }

    public static boolean isFlightSegmentFileValid() {
        // TODO:
        return true;
    }

    public static boolean isSimulationConfigurationValid() {
        boolean isValid = isConfigurationFileValid() && isAirportsFileValid();
        isValid = isValid && isWaypointsFileValid() && isScenarioFileValid() && isFlightSegmentFileValid();
        return isValid;
    }

    public static SimulationContext getSimulationContext() {
        return simulationContext;
    }

    public static void displayPrevious(ATCNode node, ATCGeography geography, String padding) {
        final List<ATCNode> previousNodes = geography.getPreviousNodes(node);
        LOG.log(Level.INFO, " {0} Previous node of {1} are {2}", new Object[]{padding, node.getName(), previousNodes});
        for (ATCNode pNode : previousNodes) {
            displayPrevious(pNode, geography, padding + "-");
        }
    }

	/*
     * TO BE MODIFIED FOR MORE GENERICITY
	 * 
	 */

    public static String getDepartureRunway() {
        return properties.getProperty(Configuration.DEPARTURE_RUNWAY);
    }

    public static String getArrivalRunway() {
        return properties.getProperty(Configuration.ARRIVAL_RUNWAY);
    }


}
