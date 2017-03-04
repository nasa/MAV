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

package gov.nasa.arc.atc.metrics;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.ControllerInitBlock;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.charts.*;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Sector;
import gov.nasa.arc.atc.utils.SectorUtilsTEMP;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds a map [planeCalculationsMap] which holds all planes and their corresponding metric calculations
 * includes overall metrics values, and multiple time plots
 *
 * @author Kelsey
 * @author ahamon
 */
public class SimulationCalculations {

    private final Map<String, PlaneCalculation> planeCalculationsMap = new HashMap<>();
    // hum
    private List<PlaneCalculation> planeCalculations;

    private Map<String, SectorCalculation> sectorCalculationsMap = new HashMap<>();
    //
//    private Map<String, Map<Integer, AfoUpdate>> allDataUpdates; //updates from data model
    private Map<String, Map<Integer, AfoUpdate>> allPlaneUpdates = new HashMap<>();
//    private Map<String, Map<Integer, AfoUpdate>> allSlotUpdates = new HashMap<>();
//    private Map<String, Map<Integer, AfoUpdate>> allControllerUpdates = new HashMap<>();


    private final DataModel dataModel;


    private final Map<String, String> controllerToSectorMap = new HashMap<>();
    private final Map<String, String> waypointToControllerMap = new HashMap<>();

    private final Map<String, List<ATCNode>> sectorNodes = new HashMap<>();

    private int[] nonCumulativeDelays;
    private int[] cumulativeDelays;


    /**
     * for each plane, plots get added to delayInfo, then stored in planeCalculationsMap
     */
    private Map<Integer, Integer> overallDelayPlot = new HashMap<>();
    private Map<Integer, Integer> overallCumDelayPlot = new HashMap<>();
    private Map<Integer, Integer> overallMeterPlot = new HashMap<>();
    private Map<Integer, Integer> overallCumulativeMeterPlot = new HashMap<>();
    private Map<Integer, Integer> aircraftCountMap; // time->aircraft count

    private ArgmaxInfo argmaxDelay = new ArgmaxInfo();
    private ArgmaxInfo argmaxMeterCount = new ArgmaxInfo();
    private int totalDelay;
    private int totalMeterCount;
    private double avgDelay;
    private double avgMeterCount;
    private int maxDelay;
    private int maxMeterCount;


    /**
     * @param aDataModel the dataInputs in the dataModel to analyze
     */
    public SimulationCalculations(DataModel aDataModel) {

        dataModel = aDataModel;

        // creates a map that links waypoints to the sector they belong to
        createSectorMap();
        // calculates each plane calculation
        processPlanes();

        calculateDelayAndMeterMetrics();
        //load metrics format
        setSimulationMetricBoxInfo(); // load boxes
        loadMetricCategories(); // load charts
        //
        //TODO when we have more info
        calculateMultipleDepartureMetrics();
        calculateSectorInfo();
        //
    }


    //note : this only works when a scenario that has controller init blocks is loaded
    private void createSectorMap() {
        System.err.println("1 - SimulationCalculations 0. createSectorMap");

        ATCGeography geography = SimulationManager.getATCGeography();
        List<ATCNode> controlledNodes = new LinkedList<>();
        System.err.println("1 - SimulationCalculations 0.1 controlledNodes ::"+controlledNodes);

        for (ControllerInitBlock block : dataModel.getInputs().getControllerInitializationBlocks()) {
            String controller = block.getControllerName();
            System.err.println("1 - SimulationCalculations 1. Controller ::"+controller);
            controlledNodes.addAll(block.getWaypoints().stream().map(wpt -> geography.getNodeByName(wpt)).collect(Collectors.toList()));
            block.getWaypoints().forEach(wpt -> waypointToControllerMap.put(wpt, controller));
        }

        for (ATCNode waypoint : geography.getWaypoints()) {
            if (waypointToControllerMap.get(waypoint.getName()) != null) {
                Sector sector = SectorUtilsTEMP.calculateSectorFromCoordinate(waypoint.getLatitude(), waypoint.getLongitude(), geography.getSectors());
                String sectorName;
                if (sector == null) {
                    sectorName = "outside of sectors";
                } else {
                    sectorName = sector.getName();
                    // populate sector nodes
                    if (!sectorNodes.containsKey(sectorName)) {
                        sectorNodes.put(sectorName, new LinkedList<>());
                    }
                    sectorNodes.get(sectorName).add(waypoint);
                }
                //
                controllerToSectorMap.put(waypointToControllerMap.get(waypoint.getName()), sectorName);
            }
        }
    }

    private void setSimulationMetricBoxInfo() {
        // create box 1
        BoxInfo delayBox = new BoxInfo(MetricsUtil.DELAY);
        delayBox.setAllValues(totalDelay, avgDelay, maxDelay);
        delayBox.setArgmax(argmaxDelay);
        MetricModel.addBox(delayBox);
        // create box 2
        BoxInfo meterBox = new BoxInfo(MetricsUtil.METER);
        meterBox.setAllValues(totalMeterCount, avgMeterCount, maxMeterCount);
        meterBox.setArgmax(argmaxMeterCount);
        MetricModel.addBox(meterBox);
    }

    private void loadMetricCategories() {
        // metric chart 1 :: category : delay
        MetricCategory delay = new MetricCategory();
        delay.setCategoryName(MetricsUtil.DELAY);
        delay.setSingleSeriesChartInfo(new ChartInfo(this.getOverallCumDelays(), "Overall Delay"));
        delay.addMultipleSeriesChart(MetricModelUtil.getAllPlaneSeriesFromMap(MetricsUtil.DELAY, MetricsUtil.CUMULATIVE, getAllPlanesCalculatedInfo()));
        delay.addMultipleSeriesChart(MetricModelUtil.getAllPlaneSeriesFromMap(MetricsUtil.DELAY, MetricsUtil.NON_CUMULATIVE, getAllPlanesCalculatedInfo()));
        MetricModel.addMetricCategory(delay);
        // metric chart 2 :: category : metering
        MetricCategory meter = new MetricCategory();
        meter.setCategoryName(MetricsUtil.METER);
        meter.setSingleSeriesChartInfo(new ChartInfo(this.getOverallCumMeters(), "Overall MeterCount"));
        meter.addMultipleSeriesChart(MetricModelUtil.getAllPlaneSeriesFromMap(MetricsUtil.METER, MetricsUtil.CUMULATIVE, getAllPlanesCalculatedInfo()));
        meter.addMultipleSeriesChart(MetricModelUtil.getAllPlaneSeriesFromMap(MetricsUtil.METER, MetricsUtil.NON_CUMULATIVE, getAllPlanesCalculatedInfo()));
        MetricModel.addMetricCategory(meter);
        // metric chart 3
//        MetricCategory newMetric = new MetricCategory();
//        newMetric.setCategoryName("practice");
//        newMetric.setSingleSeriesChartInfo(new ChartInfo(this.getOverallCumMeters(), "practice"));
//        newMetric.addMultipleSeriesChart(new ChartInfo());
//        MetricModel.getMetricCategories().add(newMetric);
    }

    private void calculateMultipleDepartureMetrics() {
        //TODO
    }

    private void calculateSectorInfo() {
        for (Map.Entry<String, List<ATCNode>> entry : sectorNodes.entrySet()) {
            String sectorName = entry.getKey();
            final SectorCalculation sectorInfo = new SectorCalculation(sectorName, entry.getValue(), this);
            sectorCalculationsMap.put(sectorInfo.getSectorName(), sectorInfo);
        }
    }


    private void calculateDelayAndMeterMetrics() {
        //todo not start at 0
        // non cumulative delay
//        nonCumulativeDelays = new int[dataModel.getMaxSimTime()];
//        for (int i = 0; i < dataModel.getMaxSimTime(); i++) {
//            int delay = 0;
//            for (int j = 0; j < planeCalculations.size(); j++) {
//                delay += planeCalculations.get(j).getDelayAt(i);
//            }
//            nonCumulativeDelays[i] = delay;
//        }
//
//        // cumulative delay
//        cumulativeDelays = new int[dataModel.getMaxSimTime()];
//        cumulativeDelays[0] = nonCumulativeDelays[0];
//        for (int i = 1; i < dataModel.getMaxSimTime(); i++) {
//            cumulativeDelays[i] = nonCumulativeDelays[i] + cumulativeDelays[i - 1];
//        }

        setOverallTimePlots();
        calculateTotals();
        calculateAverages();
        findMaximums();

    }

    private void setOverallTimePlots() {
        for (Map.Entry<String, PlaneCalculation> entry : planeCalculationsMap.entrySet()) {
//            System.err.println(" ok");
            PlaneCalculation planeCalc = entry.getValue();
            // for each plane add time plots
            for (int time : planeCalc.getDelayTimesMap().keySet()) {

                int currDelayValue = (overallDelayPlot.get(time) == null ? 0 : overallDelayPlot.get(time));
                overallDelayPlot.put(time, currDelayValue + planeCalc.getDelayTimesMap().get(time)); //

                int currMeterValue = (overallMeterPlot.get(time) == null ? 0 : overallMeterPlot.get(time));
                overallMeterPlot.put(time, currMeterValue + planeCalc.getMeterPlotsMap().get(time));
            }
        }
        //cumulative
        for (int t : overallDelayPlot.keySet()) {
            //delay
            int prevCumDelayValue = (overallCumDelayPlot.get(t - 1) == null ? 0 : overallCumDelayPlot.get(t - 1));
            overallCumDelayPlot.put(t, prevCumDelayValue + overallDelayPlot.get(t));
            // meter
            int prevCumMeterValue = (overallCumulativeMeterPlot.get(t - 1) == null ? 0 : overallCumulativeMeterPlot.get(t - 1));
            overallCumulativeMeterPlot.put(t, prevCumMeterValue + overallMeterPlot.get(t));
        }
    }

    private void calculateTotals() {
        totalDelay = 0;
        totalMeterCount = 0;
        for (Map.Entry<String, PlaneCalculation> entry : planeCalculationsMap.entrySet()) {
            PlaneCalculation planeCalc = entry.getValue();
            totalDelay += planeCalc.getDelayTime();
            totalMeterCount += planeCalc.getMeterCount();
        }
    }

    private void calculateAverages() {
        if (!planeCalculationsMap.isEmpty()) {
            avgDelay = (double) totalDelay / planeCalculationsMap.size();
            avgMeterCount = (double) totalMeterCount / planeCalculationsMap.size();
        } else {
            avgDelay = 0;
            avgMeterCount = 0;
        }
    }

    private void findMaximums() {
        Map<String, Integer> planeDelays = new HashMap<>(); // plane -> delay
        Map<String, Integer> planeMeterCounts = new HashMap<>();
        Map<String, Integer> planeMeterTimes = new HashMap<>();
        for (Map.Entry<String, PlaneCalculation> entry : planeCalculationsMap.entrySet()) {
            String plane = entry.getKey();
            PlaneCalculation value = entry.getValue();
            planeDelays.put(plane, value.getDelayTime());
            planeMeterCounts.put(plane, value.getMeterCount());
            planeMeterTimes.put(plane, value.getMeterTime());
        }
        if (!planeDelays.isEmpty()) {
            maxDelay = Collections.max(planeDelays.values());
            maxMeterCount = Collections.max(planeMeterCounts.values());
            // find argmaxes
            calculateDelayArgmax(planeDelays);
            calculateMeterCountArgmax(planeMeterCounts);
        }

    }

    private void calculateMeterCountArgmax(Map<String, Integer> planeMeterCounts) {
        List<String> storeMeterArgmaxSectors = new ArrayList<>();
        List<String> storeMeterArgmaxControllers = new ArrayList<>();
        List<String> storeMeterArgmaxPlanes = new ArrayList<>();
        List<String> storeMeterArgmaxTimes = new ArrayList<>();
        List<String> storeMeterArgmaxWaypoints = new ArrayList<>();
//TODO
//        for (Map.Entry<String, Integer> entry : planeMeterCounts.entrySet()) {
//            String curr = entry.getKey();
//            int meterCount = entry.getValue();
//            if (meterCount == maxMeterCount) {
//                //argmax plane
//                storeMeterArgmaxPlanes.add(curr);
//                int time = 0;
//                for (int t : planeCalculationsMap.get(curr).getCumMeterPlotsMap().keySet()) {
//                    //argmax time
//                    if ((int) planeCalculationsMap.get(curr).getCumMeterPlotsMap().get(t) == maxMeterCount) {
//                        storeMeterArgmaxTimes.add(Integer.toString(t));
//                        time = t;
//                        break;
//                    }
//                }
//                //other argmax
//                if (time != 0) {
//                    AfoUpdate updateInfo = allPlaneUpdates.get(curr).get(time);
//                    //data
//                    String controller = updateInfo.getController();
//                    String sector = (MetricsUtil.findSector(controller) == null ? "waypoint/sector info missing for this controller" : MetricsUtil.findSector(controller));
//                    storeMeterArgmaxSectors.add(sector);
//                    storeMeterArgmaxControllers.add(updateInfo.getController());
//                    storeMeterArgmaxWaypoints.add(updateInfo.getToWaypoint());
//                }
//            }
//        }
//        argmaxMeterCount.setSector(storeMeterArgmaxSectors.get(0));
//        argmaxMeterCount.setController(storeMeterArgmaxControllers.get(0));
//        argmaxMeterCount.setAirplane(storeMeterArgmaxPlanes.get(0));
//        argmaxMeterCount.setTime(storeMeterArgmaxTimes.get(0));
//        argmaxMeterCount.setWaypoint(storeMeterArgmaxWaypoints.get(0));
    }

    private void calculateDelayArgmax(Map<String, Integer> planeDelays) {
        //TODO
        List<String> storeDelayArgmaxSectors = new ArrayList<>();
        List<String> storeDelayArgmaxControllers = new ArrayList<>();
        List<String> storeDelayArgmaxWaypoints = new ArrayList<>();
        List<String> storeDelayArgmaxPlanes = new ArrayList<>();
        List<String> storeDelayArgmaxTimes = new ArrayList<>();
//        for (Map.Entry<String, Integer> entry : planeDelays.entrySet()) {
//            String key = entry.getKey();
//            int delay = entry.getValue();
//            if ((int) delay == (int) maxDelay) {
//                storeDelayArgmaxPlanes.add(key); //argmax plane
//                int time = 0;
//                for (int t : planeCalculationsMap.get(key).getDelayTimesMap().keySet()) {
//                    if ((int) planeCalculationsMap.get(key).getCumDelayTimesMap().get(t) == maxDelay) {
//                        storeDelayArgmaxTimes.add(Integer.toString(t)); //argmax time
//                        time = t;
//                        break;
//                    }
//                }
//                //argmax sector, controller, waypoint
//                if (time != 0) {
//                    AfoUpdate updateInfo = allPlaneUpdates.get(key).get(time);
//                    String controller = updateInfo.getController();
//                    String sector = (MetricsUtil.findSector(controller) == null ? "waypoint/sector info missing for this controller" : MetricsUtil.findSector(controller));
//                    storeDelayArgmaxSectors.add(sector);
//                    storeDelayArgmaxControllers.add(updateInfo.getController());
//                    storeDelayArgmaxWaypoints.add(updateInfo.getToWaypoint());
//                }
//            }
//        }
        //TODO
//        argmaxDelay.setSector(storeDelayArgmaxSectors.get(0));
//        argmaxDelay.setController(storeDelayArgmaxControllers.get(0));
//        argmaxDelay.setAirplane(storeDelayArgmaxPlanes.get(0));
//        argmaxDelay.setTime(storeDelayArgmaxTimes.get(0));
//        argmaxDelay.setWaypoint(storeDelayArgmaxWaypoints.get(0));

    }

    private void processPlanes() {
        dataModel.getAllPlanes().forEach(plane -> {
            PlaneCalculation planeCalculation = new PlaneCalculation(plane);
            planeCalculationsMap.put(plane.getSimpleName(), planeCalculation);
        });
        planeCalculations = planeCalculationsMap.values().stream().collect(Collectors.toList());
    }


    /*
     * -----------------
     * Getters
     * -------------------
     */

    /**
     *
     * @return the {@link DataModel} containing the scenario data the calculations are based on
     */
    public DataModel getDataModel() {
        return dataModel;
    }

    public Map<String, PlaneCalculation> getAllPlanesCalculatedInfo() {
        return Collections.unmodifiableMap(planeCalculationsMap);
    }

    public Map<String, SectorCalculation> getSectorCalculations() {
        return Collections.unmodifiableMap(sectorCalculationsMap);
    }

    public Map<String, String> getWaypointToControllerMap() {
        return Collections.unmodifiableMap(waypointToControllerMap);
    }

    public Map<String, String> getControllerToSectorMap() {
        return Collections.unmodifiableMap(controllerToSectorMap);
    }

    public Map<String, List<ATCNode>> getSectorNodes() {
        return Collections.unmodifiableMap(sectorNodes);
    }

    public Map<Integer, Integer> getOverallDelays() {
        return Collections.unmodifiableMap(overallDelayPlot);
    }

    public Map<Integer, Integer> getOverallCumDelays() {
        return Collections.unmodifiableMap(overallCumDelayPlot);
    }

    public Map<Integer, Integer> getOverallMeters() {
        return Collections.unmodifiableMap(overallMeterPlot);
    }

    public Map<Integer, Integer> getOverallCumMeters() {
        return Collections.unmodifiableMap(overallCumulativeMeterPlot);
    }

    public String findSector(String controller){
		return controllerToSectorMap.get(controller);
    }

    public double getTotalDelay() {
        return totalDelay;
    }

    public double getAvgDelay() {
        return avgDelay;
    }

    public double getMaxDelay() {
        return maxDelay;
    }

    public double getTotalMeterCount() {
        return totalMeterCount;
    }

    public double getMaxMeterCount() {
        return maxMeterCount;
    }

    public double getAvgMeterCount() {
        return avgMeterCount;
    }

    public ArgmaxInfo getArgmaxDelay() {
        return argmaxDelay;
    }

    public ArgmaxInfo getArgmaxMeterCount() {
        return argmaxMeterCount;
    }

    public ArgmaxInfo getArgmaxTEMP() {
        //TODO
        ArgmaxInfo temp = this.argmaxDelay;
        return temp;
    }

    public String findControllerFromSector(String sector) {
        for (Map.Entry<String, String> entry : controllerToSectorMap.entrySet()) {
            if (entry.getValue().equals(sector)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
