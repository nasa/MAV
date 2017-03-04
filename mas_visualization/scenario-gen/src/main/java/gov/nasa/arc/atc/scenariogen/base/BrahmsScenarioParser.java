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

import gov.nasa.arc.atc.MainResources;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLoader;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.parsing.xml.queue.ATCGeographyQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.scenariogen.core.ArrivalControllerConf;
import gov.nasa.arc.atc.scenariogen.core.RichFlightPlan;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run1.Week1Run1;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run2.Week1Run2;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run3.Week1Run3;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run4.Week1Run4;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run5.Week1Run5;
import gov.nasa.arc.atc.scenarios.dsas.lga.week1.run6.Week1Run6;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run2.Week2Run2;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run3.Week2Run3;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run4.Week2Run4;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run5.Week2Run5;
import gov.nasa.arc.atc.scenarios.dsas.lga.week2.run6.Week2Run6;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.simulation.SlotTrajectory;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import gov.nasa.arc.atc.utils.FlightPlanUtils;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class BrahmsScenarioParser {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        long startT = System.currentTimeMillis();

//        String destination = "LGA22";

        // static for now
        File geographyFile = new File(MainResources.class.getResource("Geography_Week2.xml").getPath());
        ATCGeography geography = (ATCGeography) XMLMaster.requestParsing(geographyFile, new ATCGeographyQueueParser(), geographyFile);
        SimulationManager.setATCGeography(geography);

        // static for now : TODO
        List<gov.nasa.arc.atc.scenariogen.core.ArrivalControllerConf> controllers = createControllers();


        ConsoleUtils.setLoggingLevel(Level.SEVERE);

        Map<String, List<FlightPlan>> allFlightPlans = new HashMap<>();

        BrahmsWaypointAggregator.aggregate();
        Map<String, ATCNode> nodes = new HashMap<>(BrahmsWaypointAggregator.getNODES());

        List<RichFlightPlan> richFlightPlans = new LinkedList<>();


        List<Class> classes = new LinkedList<>();
        // week 1
//		classes.add(Week1Run1.class);
//		classes.add(Week1Run2.class);
//		classes.add(Week1Run3.class);
//		classes.add(Week1Run4.class);
//		classes.add(Week1Run5.class);
//		classes.add(Week1Run6.class);
        // week 2
//		classes.add(Week2Run2.class);
//		classes.add(Week2Run3.class);
//		classes.add(Week2Run4.class);
//		classes.add(Week2Run5.class);
        classes.add(Week2Run6.class);


        for (Class c : classes) {

            System.err.println(" Parsing " + c.getSimpleName());

            String path = c.getResource("scenario.properties").getPath();
            File file = new File(path);
            SimulationConfigurationLoader.loadConfiguration(file);

            // retrieve flight plans
            //final List<FlightPlan> arrivalFlightPlans = SimulationConfigurationLoader.getAllFlights().stream().filter(f -> !f.isDeparture()).map(SimulatedSlotMarker::getFlightPlan).filter(fpl -> fpl.getLastWaypoint().getName().contains(destination)).collect(Collectors.toList());
            final List<FlightPlan> flightPlans = SimulationConfigurationLoader.getAllFlights().stream().map(SimulatedSlotMarker::getFlightPlan).collect(Collectors.toList());

            // populate the nodes
            flightPlans.forEach(f -> f.getPath().forEach(s -> {
                // from
                if (!nodes.containsKey(s.getFromWaypoint().getName())) {
                    nodes.put(s.getFromWaypoint().getName(), s.getFromWaypoint());
                }
                // to
                if (!nodes.containsKey(s.getToWaypoint().getName())) {
                    nodes.put(s.getToWaypoint().getName(), s.getToWaypoint());
                }

            }));

            System.err.println(" found flightPlans :: " + flightPlans.size());
//            flightPlans.forEach(fpl-> System.err.println(" "+fpl));

            // filter them so only remain identical one or the largest

//            flightPlans.stream().
            List<FlightPlan> distinctFPL = distinct(flightPlans);
            System.err.println(" found DISTINCT flightPlans :: " + distinctFPL.size());

            System.err.println(" -------------- ");
            distinctFPL.stream().sorted((f0, f1) -> {
                if (f0.getLenght() == f1.getLenght()) {
                    return f0.getSegment(0).getFromWaypoint().getName().compareTo(f1.getSegment(0).getFromWaypoint().getName());
                }
                return Integer.compare(f0.getLenght(),f1.getLenght());
            })
                    .forEach(distinct -> System.err.println(toCSV(distinct)));
            System.err.println(" -------------- ");

            allFlightPlans.put(c.getSimpleName(), flightPlans);


            System.err.println(" > " + flightPlans.size() + " flight plans");
            System.err.println(" ... done\n");

        }


        System.err.println("\n >  generating rich flight plans");
        System.err.println(" ... done\n");
        //

        allFlightPlans.forEach((scenario, list) -> list.forEach(fpl -> {
            String name = scenario + "_" + list.indexOf(fpl);
            System.err.println(" - " + name);
            // TODO check if ok fpl.setInitialSegment(0);
            final FlightSegment s = fpl.getPath().get(0);
            fpl.setInitialSegment(0);
//			double lat = (s.getToWaypoint().getLatitude() + s.getFromWaypoint().getLatitude()) / 2.0;
//			double lng = (s.getToWaypoint().getLongitude() + s.getFromWaypoint().getLongitude()) / 2.0;
            double lat = s.getFromWaypoint().getLatitude();
            double lng = s.getFromWaypoint().getLongitude();
            Position initPosition = new Position(new Coordinates(lat, lng), s.getdEndAltitude());
            double initSpeed = s.getEndSpeed();
            // create the slot S
            SimulatedSlotMarker sMarker = new SimulatedSlotMarker(name,new Position(initPosition.getLatitude(), initPosition.getLongitude(), initPosition.getAltitude()), initSpeed, 0, 0, 0,0,0);
            sMarker.setFlightPlan(fpl);
            SlotTrajectory trajectory = new SlotTrajectory(geography,sMarker, Integer.MAX_VALUE);
            RichFlightPlan richFlightPlan = new RichFlightPlan(scenario, name, fpl, trajectory, initPosition, initSpeed);
            richFlightPlans.add(richFlightPlan);

        }));

        // Export
        File destFile = new File("/Desktop/ScenarioGen/FlightData.xml");
        FlighPlanDataExporter.exportFlightPlanDataToXML(nodes.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()), controllers, richFlightPlans, destFile);

        // Time debug
        long endT = System.currentTimeMillis();
        System.err.println("  time => " + (endT - startT));
    }

    private static List<FlightPlan> distinct(List<FlightPlan> inList) {
        if (inList.isEmpty()) {
            return Collections.emptyList();
        }
        if (inList.size() == 1) {
            return Collections.unmodifiableList(inList);
        }
        // inList is size 2 minimum
        List<FlightPlan> result = new LinkedList<>();
        result.add(inList.get(0));
        //

        for (int i = 1; i < inList.size(); i++) {
            FlightPlan fpl = inList.get(i);
            boolean isContained = result.stream().filter(candidate -> isContained(fpl, candidate)).findFirst().isPresent();
            if (!isContained) {
                result.removeAll(result.stream().filter(candidate -> isContained(candidate, fpl)).collect(Collectors.toList()));
                result.add(fpl);
            }
        }
        return Collections.unmodifiableList(result);
    }

    //todo put in flight plan utils afterwards

    public static boolean isContained(FlightPlan fpl, FlightPlan containerCandidate) {
        int fplLength = fpl.getLenght();
        int containerLength = containerCandidate.getLenght();
        if (fplLength > containerLength) {
            return false;
        }
        if (fplLength == 0) {
            return true;
        }
        FlightSegment s0 = fpl.getSegment(0);
        int c0 = 0;
        FlightSegment sC;

        // find the segment matching the first one from fpl
        for (int j = 0; j < containerLength; j++) {
            sC = containerCandidate.getSegment(j);
            if (sC.getToWaypoint().getName().equals(s0.getToWaypoint().getName())) {
                if (!segmentEquals(s0, sC)) {
                    return false;
                }
                c0 = j;
                break;
            }
        }
        for (int i = 1; i < fplLength; i++) {
            if (i + c0 >= containerLength || !segmentEquals(fpl.getSegment(i), containerCandidate.getSegment(i + c0))) {
                return false;
            }
        }

        return true;
    }

    public static String toCSV(FlightPlan fpl){
        return fpl.getPath().stream().map(seg->seg.getFromWaypoint().getName()+"-"+seg.getToWaypoint().getName()+" "+seg.getEndSpeed()+" "+seg.getdEndAltitude()).collect(Collectors.joining(";"));
    }

    public static boolean segmentEquals(FlightSegment s0, FlightSegment s1) {
        return s0.getFromWaypoint().getName().equals(s1.getFromWaypoint().getName()) &&
                s0.getToWaypoint().getName().equals(s1.getToWaypoint().getName()) &&
                Math.abs(s0.getEndSpeed() - s1.getEndSpeed()) < 1 &&
                Math.abs(s0.getdEndAltitude() - s1.getdEndAltitude()) < 1;
    }


    // END


    private static List<ArrivalControllerConf> createControllers() {
        List<gov.nasa.arc.atc.scenariogen.core.ArrivalControllerConf> result = new LinkedList<>();

        // center south
        ArrivalControllerConf zny29 = new ArrivalControllerConf("ZNY_29", "Center", Arrays.asList("SPEAK", "RAISN", "PALEO", "GVE", "FUREE", "PXT", "COLIN", "GARED", "AGARD", "ENO", "SKIPY", "BESSI", "EDJER", "DAVYS", "HOLEY", "RIDGY", "PALEO", "KUBEY", "FATIR", "LAZIR", "COVTO", "BEBLE", "ADAXE", "FIROP", "DCA01"), "HOLEY", "ZNY_114");

        // center west
        ArrivalControllerConf zny28 = new ArrivalControllerConf("ZNY_28", "Center", Arrays.asList("PSB", "SFK", "MIP", "MARRC", "BILEY", "VIBES", "FJC", "FINSI", "ETG"), "FINSI", "ZNY_114");

        // center north
        ArrivalControllerConf zny27 = new ArrivalControllerConf("ZNY_27", "Center", Arrays.asList("BOS27", "WIBAS", "BOSOX", "HADAS", "FABEN", "HNK", "LOXXE", "IGN", "VALRE", "BASYE", "BDL"), "BASYE", "ZNY_110");

        // Empyr Arrival sector
        ArrivalControllerConf zny114 = new ArrivalControllerConf("ZNY_114", "Tracon", Arrays.asList("BRAND", "KORRY", "RBV", "SHVAL", "TYKES", "MINKS", "RABBA", "RENUE", "CONIL", "VASTI", "ARRYA", "AWARE", "FOLAM", "HAYER", "COSTL"), "ARRYA", "ZNY_112");

        // Haarp Arrival sector
        ArrivalControllerConf zny110 = new ArrivalControllerConf("ZNY_110", "Tracon", Arrays.asList("FODAK", "DOSWL", "ELZEE", "HAARP", "FAMMA"), "FAMMA", "ZNY_112");

        // Final sector
        ArrivalControllerConf zny112 = new ArrivalControllerConf("ZNY_112", "Tracon", Arrays.asList("EVANZ", "MIRRA", "CARAA", "KYLIE", "GREKO", "OMAAR", "YOMAN"), "KYLIE", "ZNY_118");

        // Tower
        ArrivalControllerConf zny118 = new ArrivalControllerConf("ZNY_118", "Tower", Arrays.asList("LGA22", "LGA31", "KUTMI", "RAMUP", "BEGNZ", "KWANN", "THRON"), "LGA22", "ZNY_118");

        //
        result.add(zny29);
        result.add(zny28);
        result.add(zny27);
        result.add(zny114);
        result.add(zny110);
        result.add(zny112);
        result.add(zny118);

        return Collections.unmodifiableList(result);
    }

//    private static List<FlightPlan> createDepartureFlightPlans(Map<String,ATCNode> nodes) {
//        List<FlightPlan> results = new LinkedList<>();
//        results.add(createDepartureKUTMI(nodes));
//        results.add(createDepartureTHRON(nodes));
//        return Collections.unmodifiableList(results);
//    }
//
//    private static FlightPlan createDepartureTHRON(Map<String,ATCNode> nodes) {
////		(current.flightPlanMap(0) = LGA31_TO_KWANN_TCF5584);
////		(current.flightPlanMap(1) = KWANN_TO_THRON_TCF5584);
//        FlightSegment s0 = new FlightSegment("LGA31_TO_KWANN", , , 2000, 240);
//        object LGA31_TO_KWANN_GJS6195 instanceof flightSegment {
//            initial_beliefs:
//            (current.fromWaypoint = LGA31);
//            (current.toWaypoint = KWANN);
//            (current.end_altitude = 2137.46);
//            (current.end_speed = 240.0);
//            initial_facts:
//            (current.fromWaypoint = LGA31);
//            (current.toWaypoint = KWANN);
//            (current.end_altitude = 2137.46);
//            (current.end_speed = 240.0);
//        }
//        object KWANN_TO_THRON_GJS6195 instanceof flightSegment {
//            initial_beliefs:
//            (current.fromWaypoint = KWANN);
//            (current.toWaypoint = THRON);
//            (current.end_altitude = 5855.97);
//            (current.end_speed = 240.0);
//            initial_facts:
//            (current.fromWaypoint = KWANN);
//            (current.toWaypoint = THRON);
//            (current.end_altitude = 5855.97);
//            (current.end_speed = 240.0);
//        }
//
//
//    }
//
//    private static FlightPlan createDepartureKUTMI(Map<String,ATCNode> nodes) {
////		(current.flightPlanMap(0) = LGA31_TO_KWANN_TCF5474);
////		(current.flightPlanMap(1) = KWANN_TO_KUTMI_TCF5474);
//
//        object LGA31_TO_KWANN_TCF5474 instanceof flightSegment {
//            initial_beliefs:
//            (current.fromWaypoint = LGA31);
//            (current.toWaypoint = KWANN);
//            (current.end_altitude = 3436.7);
//            (current.end_speed = 240.0);
//            initial_facts:
//            (current.fromWaypoint = LGA31);
//            (current.toWaypoint = KWANN);
//            (current.end_altitude = 3436.7);
//            (current.end_speed = 240.0);
//        }
//        object KWANN_TO_KUTMI_TCF5474 instanceof flightSegment {
//            initial_beliefs:
//            (current.fromWaypoint = KWANN);
//            (current.toWaypoint = KUTMI);
//            (current.end_altitude = 9907.59);
//            (current.end_speed = 240.0);
//            initial_facts:
//            (current.fromWaypoint = KWANN);
//            (current.toWaypoint = KUTMI);
//            (current.end_altitude = 9907.59);
//            (current.end_speed = 240.0);
//
//        }

}
