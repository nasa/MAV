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

package gov.nasa.arc.atc.metrics.comparison;


import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.MainResources;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.metrics.SimulationCalculations;
import gov.nasa.arc.atc.parsing.log.ThreadedAlternateLogParser;
import gov.nasa.arc.atc.parsing.xml.queue.ATCGeographyQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import gov.nasa.arc.brahms.atmjava.activities.Constants;
import javafx.embed.swing.JFXPanel;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SectorDelayCalculatorApp {

    private static final File GEOGRAPHY_FILE = new File(MainResources.class.getResource("Geography_Week2.xml").getPath());

    private static final int SIM_DUR = 8000;

    private static final int EFFECTIVE_START = 500;

    private static final File CONFIG_FILE = new File(MainResources.class.getResource("config.properties").getPath());


    // for parametric only
    private static final File SCENARIO_1_FILE = new File("/Desktop/Comparison/log_conf3-1200_t" + SIM_DUR + ".log");
    private static final File SCENARIO_2_FILE = new File("/Desktop/Comparison/log_conf3-900_t" + SIM_DUR + ".log");
    private static final File SCENARIO_3_FILE = new File("/Desktop/Comparison/log_conf3-600_t" + SIM_DUR + ".log");
    private static final File SCENARIO_4_FILE = new File("/Desktop/Comparison/log_conf3-10_t" + SIM_DUR + ".log");


    private ThreadedAlternateLogParser threadedAlternateLogParser1;
    private ThreadedAlternateLogParser threadedAlternateLogParser2;
    private ThreadedAlternateLogParser threadedAlternateLogParser3;
    private ThreadedAlternateLogParser threadedAlternateLogParser4;


    public static void main(String[] args) {
        ConsoleUtils.setLoggingLevel(Level.SEVERE);
        JFXPanel jfxPanel = new JFXPanel();
        System.err.println("JavaFX initialized with " + jfxPanel);
        SectorDelayCalculatorApp app = new SectorDelayCalculatorApp();
        app.generate();
    }


    private void generate() {
        System.err.println(" Starting parsing AtcGeography");
        ATCGeography geography = (ATCGeography) XMLMaster.requestParsing(GEOGRAPHY_FILE, new ATCGeographyQueueParser(), this);
        SimulationManager.setATCGeography(geography);

        System.err.println("Starting scenario 1 parsing ... ");
        threadedAlternateLogParser1 = new ThreadedAlternateLogParser(SCENARIO_1_FILE, CONFIG_FILE, this::handleSimu1Parsed);
        threadedAlternateLogParser1.start();
    }

    private void handleSimu1Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 1 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 1 parsed!");
                //
                System.err.println("Starting scenario 2 parsing ... ");
                threadedAlternateLogParser2 = new ThreadedAlternateLogParser(SCENARIO_2_FILE, CONFIG_FILE, this::handleSimu2Parsed);
                threadedAlternateLogParser2.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu2Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 2 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 2 parsed!");
                System.err.println("Starting scenario 3 parsing ... ");
                threadedAlternateLogParser3 = new ThreadedAlternateLogParser(SCENARIO_3_FILE, CONFIG_FILE, this::handleSimu3Parsed);
                threadedAlternateLogParser3.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu3Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 3 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 3 parsed!");
                System.err.println("Starting scenario 4 parsing ... ");
                threadedAlternateLogParser4 = new ThreadedAlternateLogParser(SCENARIO_4_FILE, CONFIG_FILE, this::handleSimu4Parsed);
                threadedAlternateLogParser4.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu4Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 4 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 4 parsed!");
                System.err.println(" PARSING COMPLETED");
                calculate();
                break;
            default:
                break;
        }
    }

    private void calculate() {
        //
        System.err.println("Creating simulation calculations ...");
        System.err.println(" > 1");
        SimulationCalculations simCal1 = new SimulationCalculations(threadedAlternateLogParser1.getDataModel());
        System.err.println(" > 1 done");
        System.err.println(" > 2");
        SimulationCalculations simCal2 = new SimulationCalculations(threadedAlternateLogParser2.getDataModel());
        System.err.println(" > 2 done");
        System.err.println(" > 3");
        SimulationCalculations simCal3 = new SimulationCalculations(threadedAlternateLogParser3.getDataModel());
        System.err.println(" > 3 done");
        System.err.println(" > 4");
        SimulationCalculations simCal4 = new SimulationCalculations(threadedAlternateLogParser4.getDataModel());
        System.err.println(" > 4 done");
        //
//        calculateAllDelaysInSimulation(simCal1, 1);
//        calculateAllDelaysInSimulation(simCal2, 2);
//        calculateAllDelaysInSimulation(simCal3, 3);
//        calculateAllDelaysInSimulation(simCal4, 4);
//        //
//        calculateDelayPerArrival(simCal1, 1);
//        calculateDelayPerArrival(simCal2, 2);
//        calculateDelayPerArrival(simCal3, 3);
//        calculateDelayPerArrival(simCal4, 4);
//        //
//        calculateLandedDelaysInSimulation(simCal1, 1);
//        calculateLandedDelaysInSimulation(simCal2, 2);
//        calculateLandedDelaysInSimulation(simCal3, 3);
//        calculateLandedDelaysInSimulation(simCal4, 4);
        //
        calculateLandedAlternateDelaysInSimulation(simCal1, 1);
        calculateLandedAlternateDelaysInSimulation(simCal2, 2);
        calculateLandedAlternateDelaysInSimulation(simCal3, 3);
        calculateLandedAlternateDelaysInSimulation(simCal4, 4);


    }

    private void calculateAllDelaysInSimulation(SimulationCalculations simCal, int nb) {
        System.err.println("> calculateDelaysInSimulation... " + nb);
        Map<String, ControllerDelays> controllerDelaysMap = new HashMap<>();
        simCal.getControllerToSectorMap().forEach((name, sector) -> {
            final ControllerDelays controllerDelays = new ControllerDelays(name, sector);
            controllerDelaysMap.put(name, controllerDelays);
        });
        // do we have to?
        synchronized (controllerDelaysMap) {
            //brute force...
            simCal.getAllPlanesCalculatedInfo().forEach((name, planeCal) -> {

                Map<Integer, AfoUpdate> planeUpdates = planeCal.getPlane().getUpdates();
                for (int i = 0; i < SIM_DUR; i++) {
                    AfoUpdate update = planeUpdates.get(i);
                    if (update != null) {
                        final int addedDelayAt = planeCal.getDelayAt(i) - planeCal.getDelayAt(i - 1);
                        // temp have to change the controller API
                        final String[] splitControllerName = update.getController().split("\\.");
                        final String controllerName = splitControllerName[splitControllerName.length - 1];

                        controllerDelaysMap.get(controllerName).addDelay(addedDelayAt);
                    }

                }

            });
        }
//        System.err.println(" ----------------");
//        controllerDelaysMap.forEach((name,controllerDelays)-> System.err.println(" Controller "+name+" for sector "+controllerDelays.getSectorName()+" added "+controllerDelays.getDelay()+"s delay"));
//        System.err.println("__________________");
    }

    private void calculateDelayPerArrival(SimulationCalculations simCal, int nb) {
        System.err.println("> calculateDelaysInSimulation... " + nb);

        List<NewPlane> arrivingPlanes = simCal.getDataModel().getArrivingPlanes();
        List<NewPlane> landedPlanes = arrivingPlanes.stream().filter(this::landsDuringSimulation).collect(Collectors.toList());
//        System.err.println("  --> nb landed :: " + landedPlanes.size());

        int delay = 0;
        for (NewPlane plane : landedPlanes) {
            delay += calculateDelayWhenLanded(plane);
        }
        System.err.println("Delay for sim " + nb + " => " + delay);
    }

    private boolean landsDuringSimulation(NewPlane plane) {
        return !plane.getUpdates().isEmpty() && plane.getUpdates().get(plane.getLastUpdateTime()).getStatus() == Constants.FINISHED;
    }

    private int calculateDelayWhenLanded(NewPlane plane) {
        //test is landed
        int startTime = plane.getStartTime();
        int originalArrivalTime = Math.max(0, startTime + (int) plane.getUpdates().get(startTime).getEta());
        int landedTime = plane.getLastUpdateTime();
        return landedTime - originalArrivalTime;
    }


    private void calculateLandedDelaysInSimulation(SimulationCalculations simCal, int nb) {
        System.err.println("> calculateLandedDelaysInSimulation... " + nb);
        Map<String, ControllerDelays> controllerDelaysMap = new HashMap<>();

        simCal.getControllerToSectorMap().forEach((name, sector) -> {
            final ControllerDelays controllerDelays = new ControllerDelays(name, sector);
            controllerDelaysMap.put(name, controllerDelays);
        });

        List<NewPlane> arrivingPlanes = simCal.getDataModel().getArrivingPlanes();
        List<String> landedPlanes = arrivingPlanes.stream().filter(this::landsDuringSimulation).map(NewPlane::getSimpleName).collect(Collectors.toList());
        // do we have to?
        synchronized (controllerDelaysMap) {
            //brute force...
            simCal.getAllPlanesCalculatedInfo().forEach((name, planeCal) -> {

                if (landedPlanes.contains(name)) {
                    Map<Integer, AfoUpdate> planeUpdates = planeCal.getPlane().getUpdates();
                    for (int i = EFFECTIVE_START; i < SIM_DUR; i++) {
                        AfoUpdate update = planeUpdates.get(i);
                        if (update != null) {
                            final int addedDelayAt = planeCal.getDelayAt(i) - planeCal.getDelayAt(i - 1);
                            // temp have to change the controller API
                            final String[] splitControllerName = update.getController().split("\\.");
                            final String controllerName = splitControllerName[splitControllerName.length - 1];
                            controllerDelaysMap.get(controllerName).addDelay(addedDelayAt);
                        }

                    }
                }

            });
        }
//        System.err.println(" ----------------");
//        controllerDelaysMap.forEach((name, controllerDelays) -> System.err.println(" Controller " + name + " for sector " + controllerDelays.getSectorName() + " added " + controllerDelays.getDelay() + "s delay"));
//        System.err.println("__________________");
    }


    private void calculateLandedAlternateDelaysInSimulation(SimulationCalculations simCal, int nb) {
        System.err.println("> calculateLandedDelaysInSimulation... " + nb);
        Map<String, ControllerDelays> controllerDelaysMap = new HashMap<>();

        simCal.getControllerToSectorMap().forEach((name, sector) -> {
            final ControllerDelays controllerDelays = new ControllerDelays(name, sector);
            controllerDelaysMap.put(name, controllerDelays);
        });

        List<NewPlane> arrivingPlanes = simCal.getDataModel().getArrivingPlanes();
        List<String> landedPlanes = arrivingPlanes.stream().filter(this::landsDuringSimulation).map(NewPlane::getSimpleName).collect(Collectors.toList());
        // do we have to?
        synchronized (controllerDelaysMap) {
            // for each plane
            simCal.getAllPlanesCalculatedInfo().forEach((name, planeCal) -> {
                AfoUpdate firstControllerUpdate = null;
                AfoUpdate lastControllerUpdate = null;
                if (landedPlanes.contains(name)) {
                    Map<Integer, AfoUpdate> planeUpdates = planeCal.getPlane().getUpdates();
                    for (int i = EFFECTIVE_START; i < SIM_DUR; i++) {
                        final AfoUpdate update = planeUpdates.get(i);
                        if (update != null) {
                            // core
                            if (firstControllerUpdate == null) {

                                // new controller
                                firstControllerUpdate = update;
                                lastControllerUpdate = update;
                            } else if (update.getController().equals(firstControllerUpdate.getController())) {
                                //still with the same controller
                                lastControllerUpdate = update;
                            } else {
                                // change of controller
                                //// temp have to change the controller API
                                final String[] splitControllerName = firstControllerUpdate.getController().split("\\.");
                                final String controllerName = splitControllerName[splitControllerName.length - 1];
                                //// calculate delay introduced by last controller
                                final int entryArrivalTime = firstControllerUpdate.getTimeStamp() + (int) firstControllerUpdate.getEta();
                                final int exitArrivalTime = lastControllerUpdate.getTimeStamp() + (int) lastControllerUpdate.getEta();
                                final int delay = Math.max(0, exitArrivalTime - entryArrivalTime);
                                //
                                controllerDelaysMap.get(controllerName).addDelay(delay);
                                firstControllerUpdate = update;
                                lastControllerUpdate = update;
                                //
                            }
                            // end of delay calculation update
                        }

                    }
                }
                // end calculation for plane
            });

        }
        System.err.println(" ----------------");
        controllerDelaysMap.forEach((name, controllerDelays) -> System.err.println(" Controller " + name + " for sector " + controllerDelays.getSectorName() + " added " + controllerDelays.getDelay() + "s delay"));
        System.err.println("__________________");
    }


}
