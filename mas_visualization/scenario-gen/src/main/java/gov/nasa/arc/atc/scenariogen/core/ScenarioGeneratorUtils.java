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

package gov.nasa.arc.atc.scenariogen.core;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightSegment;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * @author ahamon
 */
public final class ScenarioGeneratorUtils {

    private static final boolean EXPORT = true;

    private ScenarioGeneratorUtils() {
        // private utility constructor
    }


    public static void generateScenarioFiles(List<ATCNode> nodes, List<ArrivalControllerConf> controllers, List<ScheduledAFO> arrivals, List<ScheduledAFO> departures) {

        // nodes
        StringBuilder waypointsBuilder = new StringBuilder();
        //
        waypointsBuilder.append("package gov.nasa.arc.atm.atmmodel.scenarios.generated.objects;\n");
        waypointsBuilder.append("\n");
        nodes.stream().distinct().forEach(n -> appendWayPoint(waypointsBuilder, n));
//        System.out.println(FlightPlanFilter.ANSI_CYAN + waypointsBuilder.toString() + FlightPlanFilter.ANSI_RESET);

        // allArrivalControllers.b
        StringBuilder controllersBuilder = new StringBuilder();
        controllersBuilder.append("\n");
        controllersBuilder.append("package gov.nasa.arc.atm.atmmodel.scenarios.generated.agents;\n");
        controllersBuilder.append("\n");
        controllers.forEach(controller -> appendController(controllersBuilder, controller));
//        System.out.println(FlightPlanFilter.ANSI_BLUE + controllersBuilder.toString() + FlightPlanFilter.ANSI_RESET);

        StringBuilder zny118Builder = new StringBuilder();
        Optional<ArrivalControllerConf> zny118 = controllers.stream().filter(c -> c.getName().equals("ZNY_118")).findFirst();
        if (zny118.isPresent()) {
//            appendZNY118(zny118Builder, zny118.get(), departures);
            appendZNY118(controllersBuilder, zny118.get(), departures);
        } else {
            throw new IllegalArgumentException("Could not find ZNY_118");
        }


        // allAirplanes.b
        StringBuilder airplanesBuilder = new StringBuilder();
        //
        airplanesBuilder.append("\n");
        airplanesBuilder.append("package gov.nasa.arc.atm.atmmodel.scenarios.generated.agents;\n");
        airplanesBuilder.append("\n");
        arrivals.forEach(afo -> appendArrivalFlight(airplanesBuilder, afo));
        departures.forEach(afo -> appendArrivalFlight(airplanesBuilder, afo));
//        System.out.println(FlightPlanFilter.ANSI_PURPLE + airplanesBuilder.toString() + FlightPlanFilter.ANSI_RESET);


        // flight segments and flight plans
        StringBuilder segmentsBuilder = new StringBuilder();
        StringBuilder fplBuilder = new StringBuilder();
        segmentsBuilder.append("\n");
        segmentsBuilder.append("package gov.nasa.arc.atm.atmmodel.scenarios.generated.objects;\n");
        segmentsBuilder.append("\n");
        fplBuilder.append("\n");
        fplBuilder.append("package gov.nasa.arc.atm.atmmodel.scenarios.generated.objects;\n");
        fplBuilder.append("\n");
        arrivals.forEach(afo -> appendFlightPlanInfo(fplBuilder, segmentsBuilder, afo));
        departures.forEach(afo -> appendFlightPlanInfo(fplBuilder, segmentsBuilder, afo));

//        System.out.println(FlightPlanFilter.ANSI_GREEN + segmentsBuilder.toString() + FlightPlanFilter.ANSI_RESET);
//        System.out.println(FlightPlanFilter.ANSI_RED + fplBuilder.toString() + FlightPlanFilter.ANSI_RESET);


        // slots
        StringBuilder slotsBuilder = new StringBuilder();
        //
        slotsBuilder.append("\n");
        slotsBuilder.append("package gov.nasa.arc.atm.atmmodel.scenarios.generated.agents;\n");
        slotsBuilder.append("\n");
        arrivals.forEach(afo -> appendSlot(slotsBuilder, afo));
//        System.out.println(FlightPlanFilter.ANSI_YELLOW + slotsBuilder.toString() + FlightPlanFilter.ANSI_RESET);


        String generatedScenarioPath = "/Documents/Code/mas_models/exampleModels/DSAS/brahms-src/gov/nasa/arc/atm/atmmodel/scenarios/generated/";
        // exportScenarioConfiguration to files
        String airplanesPath = generatedScenarioPath + "agents/allAirplanes.b";
        String zny118Path = generatedScenarioPath + "agents/ZNY_118.b";
        String controllersPath = generatedScenarioPath + "agents/allArrivalControllers.b";
        String flightPlansPath = generatedScenarioPath + "objects/allFlightPlans.b";
        String segmentsPath = generatedScenarioPath + "objects/allFlightSegments.b";
        String waypointsPath = generatedScenarioPath + "objects/allwaypoints.b";
        String slotsPath = generatedScenarioPath + "agents/allSlotMarkers.b";
        //
        if (EXPORT) {
            printFile(airplanesPath, airplanesBuilder);
//            printFile(zny118Path, zny118Builder);
            printFile(airplanesPath, airplanesBuilder);
            printFile(controllersPath, controllersBuilder);
            printFile(flightPlansPath, fplBuilder);
            printFile(segmentsPath, segmentsBuilder);
            printFile(slotsPath, slotsBuilder);
            printFile(waypointsPath, waypointsBuilder);
        }
    }

    // =========== NEW METHOD for exporting

    public static void generateScenarioFiles(List<ATCNode> nodes, List<ArrivalControllerConf> controllers, List<ScheduledAFO> allAFOs) {
        // TODO
        System.err.println("generateScenarioFiles :: TODO");

        // todo introduce constants
        // for the tower controllers
        System.err.println("=====");
        controllers.forEach(controller -> System.err.println("C:: " + controller));
        System.err.println("-----");
        controllers.stream().filter(controller -> controller.getType().equals("Tower")).forEach(controller -> {
            System.err.println("C Tower:: " + controller);
            // no optimized
            allAFOs.stream().filter(afo->afo.getInitUpdate().getController().equals(controller.getName())).forEach(afo-> System.err.println(" -> "+afo));
        });


    }


    // =====================================
    //
    // =========== PRIVATE METHODS =========
    //
    // =====================================

    // exportScenarioConfiguration nodes
    private static void appendWayPoint(StringBuilder nodeBuilder, ATCNode node) {
        appendObjectStart(nodeBuilder, node.getName(), "waypoint");
        appendInitialBeliefsLine(nodeBuilder);
        appendBelief(nodeBuilder, "name", " \"" + node.getName() + "\"");
        appendBelief(nodeBuilder, "latitude", Double.toString(node.getLatitude()));
        appendBelief(nodeBuilder, "longitude", Double.toString(node.getLongitude()));
        appendInitialFactsLine(nodeBuilder);
        appendBelief(nodeBuilder, "name", " \"" + node.getName() + "\"");
        appendBelief(nodeBuilder, "latitude", Double.toString(node.getLatitude()));
        appendBelief(nodeBuilder, "longitude", Double.toString(node.getLongitude()));
        appendEnd(nodeBuilder);

    }

    // exportScenarioConfiguration controller
    private static void appendController(StringBuilder stringBuilder, ArrivalControllerConf controller) {
        if (!controller.getName().equals("ZNY_118")) {
            appendAgentStart(stringBuilder, controller.getName(), controller.getType());
            appendInitialBeliefsLine(stringBuilder);
            appendControllerBeliefs(stringBuilder, controller);
            appendInitialFactsLine(stringBuilder);
            appendControllerBeliefs(stringBuilder, controller);
            appendEnd(stringBuilder);
        }
    }

    // temp
    private static void appendZNY118(StringBuilder stringBuilder, ArrivalControllerConf controller, List<ScheduledAFO> departures) {
//        stringBuilder.append("package gov.nasa.arc.atm.atmmodel.scenarios.generated.agents;\n");
        stringBuilder.append("\n");
        appendAgentStart(stringBuilder, controller.getName(), controller.getType());
        appendInitialBeliefsLine(stringBuilder);
        appendDeparturesBeliefs(stringBuilder, controller, departures);
        appendControllerBeliefs(stringBuilder, controller);
        appendInitialFactsLine(stringBuilder);
        appendDeparturesBeliefs(stringBuilder, controller, departures);
        appendControllerBeliefs(stringBuilder, controller);
        appendEnd(stringBuilder);
    }

    private static void appendControllerBeliefs(StringBuilder stringBuilder, ArrivalControllerConf controller) {
        //    (current.handoffWaypoint = ARRYA);
        //    (current.handoffTo = ZNY_112);
        controller.getNodes().forEach(node -> appendRelation(stringBuilder, "hasWaypoint", node));
        appendBelief(stringBuilder, "handoffWaypoint", controller.getHandOffWaypoint());
        appendBelief(stringBuilder, "handoffTo", controller.getHandOffController());
    }

    private static void appendDeparturesBeliefs(StringBuilder stringBuilder, ArrivalControllerConf controller, List<ScheduledAFO> departures) {
        //    (laGuardiaDepts.departure_queue(1) = plane_ASQ5573 );
        departures.sort((d1, d2) -> Integer.compare(d1.getInitUpdate().getStartTime(), d2.getInitUpdate().getStartTime()));
        for (int i = 0; i < departures.size(); i++) {
            stringBuilder.append("(laGuardiaDepts.departure_queue(").append(i + 1).append(") = plane_").append(departures.get(i).getInitUpdate().getAfoName()).append(" ); \n");
        }
    }


    // exportScenarioConfiguration afo
    private static void appendArrivalFlight(StringBuilder afoStringBuilder, ScheduledAFO afo) {
        appendAgentStart(afoStringBuilder, "plane_" + afo.getInitUpdate().getAfoName(), "Airplane");
        appendInitialBeliefsLine(afoStringBuilder);
        appendAirplaneBeliefs(afoStringBuilder, afo);
        appendInitialFactsLine(afoStringBuilder);
        appendAirplaneBeliefs(afoStringBuilder, afo);
        appendEnd(afoStringBuilder);
    }

    private static void appendAirplaneBeliefs(StringBuilder stringBuilder, ScheduledAFO afo) {
        appendBelief(stringBuilder, "Name", afo.getInitUpdate().getAfoName());
        appendBelief(stringBuilder, "m_dLatitude", Double.toString(afo.getInitUpdate().getPosition().getLatitude()));
        appendBelief(stringBuilder, "m_dLongitude", Double.toString(afo.getInitUpdate().getPosition().getLongitude()));
        appendBelief(stringBuilder, "landed", Boolean.toString(false)); // check
        appendBelief(stringBuilder, "flightPlan", "flightPlan_" + afo.getInitUpdate().getAfoName());
        appendBelief(stringBuilder, "iStatus", Integer.toString(afo.getInitUpdate().getStatus()));
        appendBelief(stringBuilder, "controller", afo.getInitUpdate().getController());
        appendBelief(stringBuilder, "startTime", Integer.toString(afo.getInitUpdate().getStartTime()));
        appendBelief(stringBuilder, "iCurrentSegment", Integer.toString(afo.getInitUpdate().getCurrentSegment()));
        appendBelief(stringBuilder, "m_iAirSpeed", Integer.toString((int) afo.getInitUpdate().getAirSpeed()));
        appendBelief(stringBuilder, "m_dAltitude", Double.toString(afo.getInitUpdate().getPosition().getAltitude()));
//        appendBelief(stringBuilder, "is_departure", Boolean.toString(afo.getInitUpdate().isDeparture()));
    }

    private static void appendFlightPlanInfo(StringBuilder fplBuilder, StringBuilder segmentsBuilder, ScheduledAFO afo) {
        final FlightSegment[] segments = afo.getFlightPlan();
        appendObjectStart(fplBuilder, "flightPlan_" + afo.getInitUpdate().getAfoName(), "flightPlan");
        appendInitialBeliefsLine(fplBuilder);
        appendFlightPlanBeliefs(fplBuilder, segments, afo.getInitUpdate().getAfoName());
        appendInitialFactsLine(fplBuilder);
        appendFlightPlanBeliefs(fplBuilder, segments, afo.getInitUpdate().getAfoName());
        appendEnd(fplBuilder);
        //
        appendFlightSegmentsBeliefs(segmentsBuilder, segments, afo.getInitUpdate().getAfoName());
    }

    private static void appendFlightPlanBeliefs(StringBuilder fplBuilder, FlightSegment[] segments, String afoName) {
        FlightSegment s;
        for (int i = 0; i < segments.length; i++) {
            s = segments[i];
            appendBelief(fplBuilder, "flightPlanMap(" + i + ")", s.getFromWaypoint().getName() + "_TO_" + s.getToWaypoint().getName() + "_" + afoName);
        }
    }

    // exportScenarioConfiguration segment
    private static void appendFlightSegmentsBeliefs(StringBuilder segmentsBuilder, FlightSegment[] segments, String afoName) {
        for (FlightSegment s : segments) {
            final String fromWPT = s.getFromWaypoint().getName();
            final String toWPT = s.getToWaypoint().getName();
            appendObjectStart(segmentsBuilder, fromWPT + "_TO_" + toWPT + "_" + afoName, "flightSegment");
            appendInitialBeliefsLine(segmentsBuilder);
            appendBelief(segmentsBuilder, "fromWaypoint", fromWPT);
            appendBelief(segmentsBuilder, "toWaypoint", toWPT);
            appendBelief(segmentsBuilder, "end_altitude", Double.toString(s.getdEndAltitude()));
            appendBelief(segmentsBuilder, "end_speed", Double.toString(s.getEndSpeed()));
            appendInitialFactsLine(segmentsBuilder);
            appendBelief(segmentsBuilder, "fromWaypoint", fromWPT);
            appendBelief(segmentsBuilder, "toWaypoint", toWPT);
            appendBelief(segmentsBuilder, "end_altitude", Double.toString(s.getdEndAltitude()));
            appendBelief(segmentsBuilder, "end_speed", Double.toString(s.getEndSpeed()));
            appendEnd(segmentsBuilder);
        }
    }

    // exportScenarioConfiguration slot
    private static void appendSlot(StringBuilder slotStringBuilder, ScheduledAFO afo) {
        final String afoName = afo.getInitUpdate().getAfoName();
        appendAgentStart(slotStringBuilder, "slot_" + afoName, "slotMarker");
        appendInitialBeliefsLine(slotStringBuilder);
        appendBelief(slotStringBuilder, "Name", afoName);
        appendBelief(slotStringBuilder, "flightPlan", "flightPlan_" + afoName);
        appendInitialFactsLine(slotStringBuilder);
        appendBelief(slotStringBuilder, "Name", afoName);
        appendBelief(slotStringBuilder, "flightPlan", "flightPlan_" + afoName);
        appendEnd(slotStringBuilder);
    }


    // generic

    private static void appendAgentStart(StringBuilder stringBuilder, String name, String type) {
        // agent ZNY_110 memberof Tracon {
        stringBuilder.append("agent ").append(name).append(" memberof ").append(type).append(" { \n");
    }

    private static void appendObjectStart(StringBuilder stringBuilder, String name, String type) {
        // agent ZNY_110 memberof Tracon {
        stringBuilder.append("object ").append(name).append(" instanceof ").append(type).append(" { \n");
    }

    private static void appendBelief(StringBuilder stringBuilder, String attribute, String value) {
        //    (current.handoffWaypoint = ARRYA);
        //    (current.handoffTo = ZNY_112);
        stringBuilder.append("(current.").append(attribute).append(" = ").append(value).append("); \n");
    }

    private static void appendRelation(StringBuilder stringBuilder, String type, String value) {
        //    (current hasWaypoint FODAK);
        stringBuilder.append("     (current ").append(type).append(" ").append(value).append("); \n");
    }

    private static void appendInitialBeliefsLine(StringBuilder stringBuilder) {
        stringBuilder.append(" initial_beliefs: \n");
    }


    private static void appendInitialFactsLine(StringBuilder stringBuilder) {
        stringBuilder.append(" initial_facts: \n");
    }

    private static void appendEnd(StringBuilder stringBuilder) {
        stringBuilder.append("} \n\n");
    }

    // print in file

    private static void printFile(String path, StringBuilder content) {
        Writer out;
        FileOutputStream fileOutputStream =null;
        try {
            fileOutputStream = new FileOutputStream(path);
            out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            out.write(content.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(fileOutputStream!=null) {
                fileOutputStream.close();
            }
        }catch (IOException e) {
            //fine
        }

    }


}
