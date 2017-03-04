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
import gov.nasa.arc.atc.scenariogen.base.FlightPlanDataLoader;
import gov.nasa.arc.atc.scenariogen.base.FlightPlanDataSet;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class FlightPlanFilter {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {
        File destFile = new File("/Desktop/FlightData.xml");
        FlightPlanDataSet dataSet = FlightPlanDataLoader.parseXML(destFile);
        System.out.println("=> " + dataSet);
        //dataSet.getControllers().forEach(System.out::println);

        ATCNode arrivalRunway = dataSet.getNode("LGA22");
        ATCNode departureRunway = dataSet.getNode("LGA31");

        String westIdentifierPoint = "MARRC";
        String northIdentifierPoint = "IGN";
        String southIdentifierPoint = "SKIPY";


        ATCNode westDeliveryPoint = dataSet.getNode("OMAAR");
        ATCNode northDeliveryPoint = dataSet.getNode("OMAAR");
        ATCNode southDeliveryPoint = dataSet.getNode("OMAAR");


        // filter those which only start before key waypoints
        List<RichFlightPlan> arrivalFPLs = dataSet.getFlightPlans().stream()
                .filter(fpl -> (fpl.crosses(westIdentifierPoint) || fpl.crosses(northIdentifierPoint) || fpl.crosses(southIdentifierPoint)) && fpl.arrivesAt(arrivalRunway.getName()))
                .collect(Collectors.toList());
        System.out.println(" relevant: " + arrivalFPLs.size());

        List<RichFlightPlan> northPool = arrivalFPLs.stream().filter(fpl -> fpl.crosses(northIdentifierPoint)).collect(Collectors.toList());
        List<RichFlightPlan> westPool = arrivalFPLs.stream().filter(fpl -> fpl.crosses(westIdentifierPoint)).collect(Collectors.toList());
        List<RichFlightPlan> southPool = arrivalFPLs.stream().filter(fpl -> fpl.crosses(southIdentifierPoint)).collect(Collectors.toList());
        //
        List<RichFlightPlan> departurePool = dataSet.getFlightPlans().stream().filter(fpl -> fpl.departsFrom(departureRunway.getName())).collect(Collectors.toList());




        double northPercentage = (double) northPool.size() / arrivalFPLs.size();
        double westPercentage = (double) westPool.size() / arrivalFPLs.size();
        double southPercentage = (double) southPool.size() / arrivalFPLs.size();


        // not yet using 15 min intervals
        int hourlyRate = 45;
        double northHourlyRate = hourlyRate * northPercentage;
        double westHourlyRate = hourlyRate * westPercentage;
        double southHourlyRate = hourlyRate * southPercentage;

        double nbHours = 1.0;
        // duration in seconds
        int simulationDuration = (int) (nbHours * 60 * 60);
        System.out.println(ANSI_PURPLE + " ! simulation duration: " + simulationDuration + "s" + ANSI_RESET);

        int northTimeOffset = 0;
        int westTimeOffset = 0;
        int southTimeOffset = 0;

        //
        int northInterval = (int) ((60.0 * 60.0) / northHourlyRate);
        int westInterval = (int) ((60.0 * 60.0) / westHourlyRate);
        int southInterval = (int) ((60.0 * 60.0) / southHourlyRate);
        //
        int nbAircraft = simulationDuration / northInterval + simulationDuration / westInterval + simulationDuration / southInterval;

        double actualRate = (double) nbAircraft / nbHours;


        System.out.println(" - north: " + northPool.size() + " -> " + northPercentage + "%  interval=" + northInterval + "s");
        System.out.println(" - west: " + westPool.size() + " -> " + westPercentage + "%  interval=" + westInterval + "s");
        System.out.println(" - south: " + southPool.size() + " -> " + southPercentage + "%  interval=" + southInterval + "s");
        System.out.println("");


        double rateError = (hourlyRate - actualRate) / hourlyRate;


        System.out.println("actualRate = " + actualRate);
        System.out.println(ANSI_RED + "rateError = " + rateError + ANSI_RESET);
        System.out.println("");

        northInterval -= northInterval * rateError;
        westInterval -= westInterval * rateError;
        southInterval -= southInterval * rateError;


        nbAircraft = simulationDuration / northInterval + simulationDuration / westInterval + simulationDuration / southInterval;
        actualRate = (double) nbAircraft / nbHours;

        System.out.println(" - north: " + northPool.size() + " -> " + northPercentage + "%  interval=" + northInterval + "s");
        System.out.println(" - west: " + westPool.size() + " -> " + westPercentage + "%  interval=" + westInterval + "s");
        System.out.println(" - south: " + southPool.size() + " -> " + southPercentage + "%  interval=" + southInterval + "s");
        System.out.println(ANSI_GREEN + "actualRate = " + actualRate + ANSI_RESET);
        System.out.println("");

        // departure rate and intervals
        double departureRate = 15.0;
        int departureInterval = (int) ((60.0 * 60.0) / departureRate);
        int departureTimeOffset = 0;

        System.out.println(ANSI_BLUE + "  Start generating schedule--- " + ANSI_RESET);

        // generating arrival flow
        List<ScheduledFlightPlan> scheduledArrivalFlightPlans = new LinkedList<>();
        generateFlow(northPool, "NORTH-", northInterval, northTimeOffset, simulationDuration, northDeliveryPoint, scheduledArrivalFlightPlans);
        generateFlow(westPool, "WEST-", westInterval, westTimeOffset, simulationDuration, westDeliveryPoint, scheduledArrivalFlightPlans);
        generateFlow(southPool, "SOUTH-", southInterval, southTimeOffset, simulationDuration, southDeliveryPoint, scheduledArrivalFlightPlans);


        // generating departure flow
        List<ScheduledFlightPlan> scheduledDeparturelFlightPlans = new LinkedList<>();
        generateDepartureFlow(departurePool, "DEP-", departureInterval, departureTimeOffset, simulationDuration, departureRunway, scheduledDeparturelFlightPlans);
        //


        System.out.println(" -> done generating\n");

        System.out.println("=> " + scheduledArrivalFlightPlans.size() + " scheduled flight plans");

        System.out.println("\n--- Starting scheduled flight creation ---\n");

        List<ScheduledAFO> scheduledArrivals = scheduledArrivalFlightPlans.stream().map(scheduledFlightPlan -> scheduledFlightPlan.generateAFO(0, dataSet.getControllers())).collect(Collectors.toList());

        List<ScheduledAFO> scheduledDepartures = scheduledDeparturelFlightPlans.stream().map(scheduledFlightPlan -> scheduledFlightPlan.generateAFO(0, dataSet.getControllers())).collect(Collectors.toList());


        System.out.println("=> " + scheduledArrivals.size() + " scheduled arrival flights");
        System.out.println("=> " + scheduledDepartures.size() + " scheduled departure flights");


        System.out.println("\n--- Starting brahms files creation ---\n");

        scheduledDepartures.forEach(dep-> System.err.println("dep "+dep.getInitUpdate().getController()));

        ScenarioGeneratorUtils.generateScenarioFiles(dataSet.getNodes(), dataSet.getControllers(), scheduledArrivals, scheduledDepartures);


    }

    private static void generateFlow(List<RichFlightPlan> fluxPool, String prefix, int interval, int timeOffset, int simulationDuration, ATCNode deliveryPoint, List<ScheduledFlightPlan> scheduledFlightPlans) {
        int afoId = 0;
        int poolSize = fluxPool.size();
        Random northRandom = new Random();
        int next = timeOffset + interval;
        while (next < simulationDuration) {
            final RichFlightPlan fpl = fluxPool.get(northRandom.nextInt(poolSize));
            ScheduledFlightPlan scheduledFlightPlan = new ScheduledFlightPlan(prefix + afoId, fpl, next, deliveryPoint);
            scheduledFlightPlans.add(scheduledFlightPlan);
            afoId++;
            next += interval;
        }
    }


    private static void generateDepartureFlow(List<RichFlightPlan> fluxPool, String prefix, int interval, int timeOffset, int simulationDuration, ATCNode deliveryPoint, List<ScheduledFlightPlan> scheduledFlightPlans) {
        int afoId = 0;
        int poolSize = fluxPool.size();
        Random northRandom = new Random();
        int next = timeOffset + interval;
        while (next < simulationDuration) {
            final RichFlightPlan fpl = fluxPool.get(northRandom.nextInt(poolSize));
            ScheduledFlightPlan scheduledFlightPlan = new ScheduledFlightPlan(prefix + afoId, fpl, next, deliveryPoint);
            scheduledFlightPlans.add(scheduledFlightPlan);
            afoId++;
            next += interval;
        }
    }


//    long total = 2305;
//    long startTime = System.currentTimeMillis();
//
//        for (int i = 1; i <= total; i = i + 3) {
//        try {
//            Thread.sleep(50);
//            printProgress(startTime, total, i);
//        } catch (InterruptedException e) {
//        }
//    }

    private static void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 :
                (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string
                .append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }


}
