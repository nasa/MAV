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

package gov.nasa.arc.atc.simulation;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.FlightPlanUtils;
import gov.nasa.arc.atc.utils.SlotUpdateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author ahamon
 */
public class SlotTrajectory extends SimulatedTrajectory<SimulatedSlotMarker> {

    // original schedule part
    private final int originalArrivalTime;
    private final Map<String, Integer> originalWPTArrivalTime;
    private final Map<String, Integer> wPTArrivalTime;
    // redundancy with slotMarker?
    private final Map<Integer, Integer> finalETAs;
    //
    private final ATCGeography geography;

    //
    private List<TimedFlightParameters> referenceTrajectory;
    private int departTimeInRef;
    //
    private List<TimedFlightParameters> parameters;
    private int arrivalTime;
    private int cumulativeDelay = 0;
    private int inFlightDelay = 0;
    // the iteration version of this simulated trajectory
    private int version;
    //
    private List<TimedFlightParameters> lastVersion;

    private boolean refTrajectoryTakesOff;


    private final String noWPT = "NO_WPT";
    private String firstWPTName;
    private FlightParameters offParameterNoTime;
    private FlightParameters groundParameterNoTime;

    /**
     * @param atcGeography       the geography needed to determine the arrival and departure nodes
     * @param slotMarker         the slot used for trajectory calculations
     * @param simulationDuration the simulation duration
     */
    public SlotTrajectory(ATCGeography atcGeography, SimulatedSlotMarker slotMarker, int simulationDuration) {
        super(slotMarker, simulationDuration);
        geography = atcGeography;
        originalWPTArrivalTime = new HashMap<>();
        wPTArrivalTime = new HashMap<>();
        finalETAs = new HashMap<>();
        parameters = new ArrayList<>();
        // simulates the trajectory from the beginning
        createReferenceTrajectory();
        initializeTrajectory();
        originalArrivalTime = arrivalTime;
        storeETAs(originalWPTArrivalTime);
        storeETAs(wPTArrivalTime);
        storeFinalETAAtSimTime(0);
    }

    @Override
    public final void recalculateTrajectoryWithDelayAt(int calculationStart, int delay) {
        LOG.log(Level.FINE, " recalculateTrajectoryWithDelayAt calculationStart={0} delay={1}", new Object[]{calculationStart, delay});
        if (delay < 0) {
            LOG.log(Level.SEVERE, "Delay CANNOT be negative : delay={0}", delay);
            throw new IllegalStateException("Delay CANNOT be negative : delay=" + delay);
        }
        cumulativeDelay = cumulativeDelay + delay;
        // store last calculations in history
        storePreviousVersion();
        //
        populateNewTrajectory(calculationStart, delay);
        LOG.log(Level.FINE, "RESULT of recalculation  for slot {0} :: previousArrival={1} delay={2} newArrival={3}", new Object[]{getSlotMarker().getName(), arrivalTime, delay, parameters.size() + 1});
        arrivalTime = parameters.get(parameters.size() - 1).getTimeStamp();
        LOG.log(Level.FINE, "Slot NEW arrivalTime={0}", arrivalTime);
        storeETAs(wPTArrivalTime);
        storeFinalETAAtSimTime(calculationStart);
    }

    @Override
    public TimedFlightParameters getParametersAtSimulationTime(int time) {
        return parameters.get(time);
    }

    @Override
    public String getWaypointAimedAtSimulationTime(int time) {
        return parameters.get(time).getToWPT();
    }

    @Override
    public int getOriginalArrivalTime() {
        return originalArrivalTime;
    }

    @Override
    public int getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public int getArrivalTimeAtNode(String nodeName) {
        return wPTArrivalTime.get(nodeName);
    }

    @Override
    public int getOriginalStartTime() {
        return getSlotMarker().getStartTime();
    }


    // ADDED METHOD FOR CTOP
    @Override
    public int getCrossingTime(ATCNode node, int meterFixDistance) {
        // to be optimized and cached
        int index = parameters.size();
        double nodeLat = node.getLatitude();
        double nodeLng = node.getLongitude();
        TimedFlightParameters param = null;
        double distance = 0;
        while (index > 0 && distance < meterFixDistance) {
            index--;
            param = parameters.get(index);
            distance = AfoUtils.getHorizontalDistance(param.getLatitude(), param.getLongitude(), nodeLat, nodeLng);
        }
        return param != null ? param.getTimeStamp() : -1;
    }


    private void createReferenceTrajectory() {
        FlightPlan modifiedFlightPlan = FlightPlanUtils.createSlotModifiedFlightPlan(geography, getSlotMarker());
        referenceTrajectory = SlotUpdateUtils.calculateReferenceTrajectory(geography, getSlotMarker(), modifiedFlightPlan, getSimulationDuration());
    }

    private void storeETAs(Map<String, Integer> times) {
        LOG.log(Level.FINE, " storeETAs ");
        times.clear();
        for (int i = 1; i < parameters.size(); i++) {
            if (parameters.get(i).getToWPT() == null ? parameters.get(i - 1).getToWPT() != null : !parameters.get(i).getToWPT().equals(parameters.get(i - 1).getToWPT())) {
                // i or i-1 ... what is more safe?
                times.put(parameters.get(i - 1).getToWPT(), i - 1);
            }
        }
        times.put(parameters.get(parameters.size() - 1).getToWPT(), arrivalTime);
    }

    private void storePreviousVersion() {
        LOG.log(Level.FINE, " storePreviousVersion");
        if (version != 0) {
            lastVersion = new ArrayList<>(parameters);
        }
    }

    private void storeFinalETAAtSimTime(int simulationTime) {
        final int eTA = parameters.get(parameters.size() - 1).getTimeStamp();
        finalETAs.put(simulationTime, eTA);
    }

    // TODO factor methods
    private void initializeTrajectory() {
        // update version
        version = 1;
        // clear old parameters
        parameters = new ArrayList<>();
        getSlotMarker().reset();
        // variables
        int startTime = getSlotMarker().getStartTime();
        int departureTime = getSlotMarker().getDepartureTime();
        //
        FlightParameters initParameters = getSlotMarker().getInitialParameters();
        refTrajectoryTakesOff = geography.isRunwayOrAirport(getSlotMarker().getFlightPlan().getFirstWaypoint().getName());
        if (!refTrajectoryTakesOff) {
            departTimeInRef = SlotUpdateUtils.getLastTimeAtCoordinates(initParameters.getLatitude(), initParameters.getLongitude(), referenceTrajectory);
        } else {
            //TODO: use a crashing value?
            departTimeInRef = 0;
        }


        // couple of variables
        FlightSegment origSeg = getSlotMarker().getFlightPlan().getPath().get(0);
        Position oriP = new Position(origSeg.getFromWaypoint().getLatitude(), origSeg.getFromWaypoint().getLongitude(), refTrajectoryTakesOff ? origSeg.getFromWaypoint().getAltitude() : origSeg.getdEndAltitude());
        firstWPTName = origSeg.getFromWaypoint().getName();

        // variable being used when recalculating
        offParameterNoTime = new FlightParameters(oriP, 0, 0, 0, Constants.NO_STARTED);
        if (departureTime > startTime) {
            groundParameterNoTime = new FlightParameters(oriP, 0, 0, 0, Constants.ON_GROUND);
        }

        // heavy loop to fill in while not started in the sim, to get rid of in optimization?
        for (int i = 0; i < Math.min(startTime, getSimulationDuration()); i++) {
            //TODO make sure it is on the ground?
            addToParametersLists(new TimedFlightParameters(i, offParameterNoTime, noWPT), "-1");
        }

        // heavy loop to fill in while on the ground, to get rid of in optimization?
        for (int i = startTime; i < Math.min(departureTime, getSimulationDuration()); i++) {
            addToParametersLists(new TimedFlightParameters(i, groundParameterNoTime, firstWPTName), "0");
        }

        getSlotMarker().start();
        // calculating the rest of the trajectory

        int flyLoopStart = Math.max(departureTime, startTime);
        LOG.log(Level.FINE, " {0} flyLoopStart = {1}", new Object[]{getSlotMarker().getName(), flyLoopStart});
        int simTime = flyLoopStart;

        for (int i = departTimeInRef; i < referenceTrajectory.size(); i++) {
            addToParametersLists(new TimedFlightParameters(simTime, referenceTrajectory.get(i)), "1");
            simTime++;
        }
        arrivalTime = parameters.get(parameters.size() - 1).getTimeStamp();

    }

    private void populateNewTrajectory(int calculationStart, int lastAddedDelay) {
        // test historic
        if (version != 0 && lastVersion == null) {
            LOG.log(Level.SEVERE, "LastVersion is null for version={0}", version);
            throw new IllegalArgumentException("LastVersion is null for version=" + version);
        }
        // update version
        version++;
        // clear old parameters and waypoints
        parameters = new ArrayList<>();
        //

        // variables
        final int startTime = getSlotMarker().getStartTime();
        int departureTime = getSlotMarker().getDepartureTime();

        // NEW ________________

        if (calculationStart <= departureTime) {
            //delay the departure time
            getSlotMarker().setDepartureTime(departureTime + lastAddedDelay);
            departureTime = getSlotMarker().getDepartureTime();

            // heavy loop to fill in while not started in the sim, to get rid of in optimization?
            for (int i = 0; i < Math.min(startTime, getSimulationDuration()); i++) {
                //TODO make sure it is on the ground?
                addToParametersLists(new TimedFlightParameters(i, offParameterNoTime, noWPT), "-1");
            }


            // couple of variables
            FlightSegment origSeg = getSlotMarker().getFlightPlan().getPath().get(0);
            Position oriP = new Position(origSeg.getFromWaypoint().getLatitude(), origSeg.getFromWaypoint().getLongitude(), refTrajectoryTakesOff ? origSeg.getFromWaypoint().getAltitude() : origSeg.getdEndAltitude());
            firstWPTName = origSeg.getFromWaypoint().getName();

            if (departureTime > startTime) {
                groundParameterNoTime = new FlightParameters(oriP, 0, 0, 0, Constants.ON_GROUND);
            }
            // heavy loop to fill in while on the ground, to get rid of in optimization?
            for (int i = startTime; i < Math.min(departureTime, getSimulationDuration()); i++) {
                addToParametersLists(new TimedFlightParameters(i, groundParameterNoTime, firstWPTName), "0");
            }

            // calculating the rest of the trajectory
            int simTime = Math.min(getSimulationDuration(), Math.max(departureTime, startTime));
            for (int i = departTimeInRef; i < referenceTrajectory.size(); i++) {
                addToParametersLists(new TimedFlightParameters(simTime, referenceTrajectory.get(i)), "1");
                simTime++;
            }
            arrivalTime = parameters.get(parameters.size() - 1).getTimeStamp();

        } else {
            //TODO test that delay can be added
            inFlightDelay += lastAddedDelay;
            // calculationStart >= startTime
            for (int i = 0; i < calculationStart; i++) {
                addToParametersLists(new TimedFlightParameters(i, lastVersion.get(i)), "1_");
            }
            if (calculationStart >= lastAddedDelay) {
                for (int i = calculationStart; i < lastVersion.size() + lastAddedDelay; i++) {
                    addToParametersLists(new TimedFlightParameters(i, lastVersion.get(i - lastAddedDelay)), "1_-");
                }
            } else {
                //retrieve index of first position from last version in ref trajectory
                TimedFlightParameters firstParameterLastVersion = lastVersion.get(0);
                int referenceTimeIndex = SlotUpdateUtils.getLastTimeAtCoordinates(firstParameterLastVersion.getLatitude(), firstParameterLastVersion.getLongitude(), referenceTrajectory);

                // if index is negative, it means we are trying to add too much delay => extend ghost segment length in modified flight plan calculations
                if (referenceTimeIndex < 0) {
                    throw new IllegalStateException("tying to add to much delay :: " + lastAddedDelay);
                }
                int indexToLastVersion = lastAddedDelay - calculationStart;

                for (int j = calculationStart; j < calculationStart + indexToLastVersion; j++) {
                    addToParametersLists(new TimedFlightParameters(j, referenceTrajectory.get(j - indexToLastVersion + referenceTimeIndex)), "1_from ref");
                }
                for (int i = 0; i < lastVersion.size(); i++) {
                    addToParametersLists(new TimedFlightParameters(i + calculationStart + indexToLastVersion, lastVersion.get(i)), "1_from lastV");
                }
            }
        }

    }

    private void addToParametersLists(TimedFlightParameters timedFlightParameters, String debug) {
        parameters.add(timedFlightParameters);
        if (timedFlightParameters.getTimeStamp() != parameters.indexOf(timedFlightParameters)) {
            LOG.log(Level.SEVERE, " index mismatch :: timedFlightParameters.getTimeStamp={0} // parameters.indexOf(timedFlightParameters)={1}", new Object[]{timedFlightParameters.getTimeStamp(), parameters.indexOf(timedFlightParameters)});
            throw new IllegalStateException("index mismatch :: timedFlightParameters.getTimeStamp=" + timedFlightParameters.getTimeStamp() + " // parameters.indexOf(timedFlightParameters)=" + parameters.indexOf(timedFlightParameters) + " at debug:" + debug);
        }
    }

    @Override
    public String toString() {
        return "SimulatedTrajectory [" + getSlotMarker().getName() + "]";
    }

    @Override
    public int getOriginalArrivalTimeAtNode(String nodeName) {
        if (!originalWPTArrivalTime.containsKey(nodeName)) {
            LOG.log(Level.FINE, "FPL: {0}  ", getSlotMarker().getFlightPlan());
            LOG.log(Level.FINE, "init seg: {0}  ", getSlotMarker().getFlightPlan().getInitialSegment());
            LOG.log(Level.FINE, "slot:{0}  at node:{1} __originalWPTArrivalTime:{2}", new Object[]{getSlotMarker().getName(), nodeName, originalWPTArrivalTime});
            return -1;
        }
        return originalWPTArrivalTime.get(nodeName);
    }

    @Override
    public int getETAAtSimulationTime(int simulationTime) {
        for (int i = simulationTime; i > -1; i--) {
            if (finalETAs.containsKey(i)) {
                return finalETAs.get(i);
            }
        }
        return -1;
    }

    @Override
    public int getInFlightDelay() {
        return inFlightDelay;
    }

    @Override
    public void printDebug() {
        if (DEBUG) {
            System.err.println(" SIM ARRIVAL TRAJ DEBUG :: " + getSlotMarker().getName());
            for (int i = 0; i < parameters.size(); i++) {
                System.err.println(" T=" + i + "  lat=" + parameters.get(i).getLatitude() + "  long=" + parameters.get(i).getLongitude() + "  alt=" + parameters.get(i).getAltitude() + "  status=" + parameters.get(i).getStatus());
            }
            System.err.println("___________ " + getSlotMarker().getName() + "\n");
        }
    }

    @Override
    public List<TimedFlightParameters> getTimedFlightParameters() {
        return Collections.unmodifiableList(parameters);
    }

}
