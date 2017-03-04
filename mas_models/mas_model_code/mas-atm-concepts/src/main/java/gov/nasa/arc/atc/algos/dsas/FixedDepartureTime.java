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

package gov.nasa.arc.atc.algos.dsas;

import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.airborne.DepartureSequence;
import gov.nasa.arc.atc.algos.DepartureArrivalAlgorithm;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.algos.tss.TSS;
import gov.nasa.arc.atc.functions.SeparationFunction;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.GapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.util.Pair;

/**
 * Implementation in which departure schedule is fixed
 *
 * @author ahamon
 */
public class FixedDepartureTime implements DepartureArrivalAlgorithm {

    private static final Logger LOG = Logger.getGlobal();

    private final List<SimulatedTrajectory> scheduledArrivals;
    private final Pair<String, Boolean> tssDSASppty;

    private SimulationContext context;
    private ATCNode departureNode;
    private ATCNode arrivalNode;
    private DepartureSequence departureSequence;
    private ArrivalSequence arrivalSequence;

    private final TSS tss;

    public FixedDepartureTime(SeparationFunction separationFunction) {
        scheduledArrivals = new ArrayList<>();
        tssDSASppty = new Pair<>(DSAS.DSAS_PPTY, true);
        Map<String, Object> tssConfig = new HashMap<>();
        tssConfig.put(tssDSASppty.getKey(), tssDSASppty.getValue());
        tss = new TSS(tssConfig, (traj, time) -> true, separationFunction);
    }

    @Override
    public void setSimulationConfiguration(String scenarioName, int simulationDuration, int stepDuration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initializeData(SimulationContext simulationContext, ATCNode dNode, ATCNode aNode) {
        setContext(simulationContext);
        departureNode = dNode;
        arrivalNode = aNode;
        departureSequence = context.getDepartureSequences().get(departureNode);
        arrivalSequence = context.getArrivalSequences().get(arrivalNode);
        boolean hasDepartureSequence = departureSequence != null;
        boolean hasArrivalSequence = arrivalSequence != null;
        LOG.log(Level.FINE, "hasDepartureSequence: {0}", hasDepartureSequence);
        LOG.log(Level.FINE, "hasArrivalSequence: {0}", hasArrivalSequence);
        assert hasDepartureSequence;
        assert hasArrivalSequence;
        tss.initializeData(context, arrivalNode);
    }

    @Override
    public boolean execute(int simulationTime) {
        LOG.log(Level.INFO, "T = {0} :: >> Executing DSAS1...", simulationTime);
        // log departure and arrival sequences IN
        // AlgoUtils.logSequencesInDSAS(context, departureNode, arrivalNode, simulationTime);
        //
        scheduledArrivals.clear();

        // find last landing
        int lastLandingTime = -1;
        for (Pair<SimulatedTrajectory, Integer> landing : context.getLandings().get(arrivalNode.getName())) {
            lastLandingTime = Math.max(lastLandingTime, landing.getValue());
        }
        // find last take off
        int lastTakeOffTime = -1;
        for (Pair<SimulatedTrajectory, Integer> takeOff : context.getTakeOffs().get(departureNode.getName())) {
            lastTakeOffTime = Math.max(lastTakeOffTime, takeOff.getValue());
        }
        //
        // sequence departures
        //// only ensures that departures are correctly spaced
        sequenceDepartures(simulationTime, lastTakeOffTime);
        LOG.log(Level.FINE, "Last take off time = {0}", lastTakeOffTime);

        // build gaps sequence for arrival to fit in
        List<SimpleDepartureGap> gaps = new ArrayList<>();
        for (int i = 0; i < departureSequence.getNbDepartures() - 1; i++) {
            gaps.add(new SimpleDepartureGap(departureSequence.getAtIndex(i).getSlotMarker().getDepartureTime(), departureSequence.getAtIndex(i + 1).getSlotMarker().getDepartureTime()));
        }
        // overkill?
        Collections.sort(gaps, GapUtils.START_TIME_GAP_COMPARATOR);

        // try to schedule before next departure
        for (int i = 0; i < arrivalSequence.getSimulatedTrajectories().size(); i++) {
            Pair<Boolean, Integer> result = analyzeArrivalBeforeNextTakeOff(arrivalSequence.getAtIndex(i), simulationTime, lastLandingTime, lastTakeOffTime);
            if (result.getKey()) {
                // if scheduled
                lastLandingTime = result.getValue();
                scheduledArrivals.add(arrivalSequence.getAtIndex(i));
            } else {
                break;
            }
        }

        // meter arrival to insert in departure schedule

        analyzeGapsForArrivals(simulationTime, gaps, lastLandingTime);
        //
        int nextLandingAvailable = calculatedNextLandingAvailable(lastTakeOffTime, lastLandingTime);

        for (int i = 0; i < arrivalSequence.getSimulatedTrajectories().size(); i++) {
            SimulatedTrajectory arrival = arrivalSequence.getSimulatedTrajectories().get(i);
            if (!scheduledArrivals.contains(arrival)) {
                int oldArrivalTime = arrival.getArrivalTime();
                int delay = nextLandingAvailable - oldArrivalTime;
                if (delay > 0) {
                    arrival.recalculateTrajectoryWithDelayAt(simulationTime, delay);
                }
            }
        }

        // schedule remaining arrivals
        tss.execute(simulationTime); // tssConfig !!!
        LOG.log(Level.FINE, "Last landing time = {0}", lastLandingTime);

        // log departure and arrival sequences OUT
        // AlgoUtils.logSequencesOutDSAS(context, departureNode, arrivalNode, simulationTime);
        //
        return true;
    }

    private void setContext(SimulationContext simulationContext) {
        if (simulationContext == null) {
            throw new IllegalArgumentException("ATC Geography cannot be null");
        }
        context = simulationContext;
    }

    private int calculatedNextLandingAvailable(final int lastTakeOffTime, final int lastLandingTime) {
        int lastTakeOffInQueue = lastTakeOffTime;
        if (departureSequence.getNbDepartures() > 0) {
            lastTakeOffInQueue = Math.max(lastTakeOffInQueue, departureSequence.getAtIndex(departureSequence.getNbDepartures() - 1).getSlotMarker().getDepartureTime());
        }
        // not optimized
        int lastlandingInQueue = lastLandingTime;
        // TODO: loop is not using loop element:: FIX
        for (int i = 0; i < scheduledArrivals.size(); i++) {
            lastlandingInQueue = Math.max(lastlandingInQueue, scheduledArrivals.get(scheduledArrivals.size() - 1).getArrivalTime());
        }
        //
        return Math.max(lastTakeOffInQueue + Constants.DEP_ARR_MIN, lastlandingInQueue + Constants.ARR_ARR_MIN);
    }

    private Pair<Boolean, Integer> analyzeArrivalBeforeNextTakeOff(SimulatedTrajectory arrival, int simulationTime, int lastLandingTime, int lastTakeOffTime) {
        System.err.println("analyzeArrivalBeforeNextTakeOff " + arrival);
        int currentArrivalTime = arrival.getArrivalTime();
        System.err.println("> arrival time "+currentArrivalTime);
        // if arrival arrives after next departure min req separation, return
        if ((!departureSequence.getSimulatedTrajectories().isEmpty()) && currentArrivalTime > departureSequence.getAtIndex(0).getSlotMarker().getDepartureTime() - Constants.ARR_DEP_MIN) {
            System.err.println("> r1");
            return new Pair<>(false, lastLandingTime);
        }
        // test if can land after last landing and before next departure
        int nextAvailableLanding = calculateNextAvailableLanding(simulationTime, lastTakeOffTime, lastLandingTime);
        //
        // if no more departures
        if (departureSequence.getSimulatedTrajectories().isEmpty()) {
            // either was scheduled to before available landing
            if (currentArrivalTime < nextAvailableLanding) {
                arrival.recalculateTrajectoryWithDelayAt(simulationTime, nextAvailableLanding - currentArrivalTime);
                scheduledArrivals.add(arrival);
                System.err.println("> r2");
                return new Pair<>(true, arrival.getArrivalTime());
            } else {
                // or do nothing because landing is OK
                scheduledArrivals.add(arrival);
                System.err.println("> r3");
                return new Pair<>(true, arrival.getArrivalTime());
            }
        }
        // if there still are departures
        else {
            int maxLandingTime = departureSequence.getAtIndex(0).getSlotMarker().getDepartureTime();
            // can land before next departure
            boolean canLand = nextAvailableLanding < maxLandingTime && currentArrivalTime <= maxLandingTime;
            int delay = nextAvailableLanding - currentArrivalTime;
            if (canLand && delay > 0) {
                arrival.recalculateTrajectoryWithDelayAt(simulationTime, delay);
                scheduledArrivals.add(arrival);
                // DSASReporter.logArrivalDelay(simulationTime, delay, departureSequence.getAtIndex(0), arrival);
                return new Pair<>(true, arrival.getArrivalTime());
            } else if (canLand) {
                scheduledArrivals.add(arrival);
                return new Pair<>(true, arrival.getArrivalTime());
            } else {
                return new Pair<>(false, lastLandingTime);
            }
        }
    }

    private int calculateNextAvailableLanding(final int simulationTime, final int lastTakeOffTime, final int lastLandingTime) {
        int nextTimeAfterTakeOff;
        if (lastTakeOffTime < 0) {
            nextTimeAfterTakeOff = simulationTime;
        } else {
            nextTimeAfterTakeOff = lastTakeOffTime + Constants.DEP_ARR_MIN;
        }
        int nextTimeAfterLanding;
        if (lastLandingTime < 0) {
            nextTimeAfterLanding = simulationTime;
        } else {
            nextTimeAfterLanding = lastLandingTime + Constants.ARR_ARR_MIN;
        }
        //
        return Math.max(nextTimeAfterTakeOff, nextTimeAfterLanding);
    }

    private int sequenceDepartures(final int simulationTime, final int lastTakeOffTime) {
        int oldDepartureTime = lastTakeOffTime;
        if (departureSequence.getNbDepartures() > 0) {
            oldDepartureTime = analyzeFirstDeparture(departureSequence.getAtIndex(0), simulationTime, lastTakeOffTime);
        }
        int nextDepartureAvailable = oldDepartureTime + Constants.DEP_DEP_MIN;
        for (int i = 1; i < departureSequence.getNbDepartures(); i++) {
            SimulatedTrajectory departure = departureSequence.getAtIndex(i);
            oldDepartureTime = departure.getSlotMarker().getDepartureTime();
            if (oldDepartureTime < nextDepartureAvailable) {
                int delay = nextDepartureAvailable - oldDepartureTime;
                departure.recalculateTrajectoryWithDelayAt(simulationTime, delay);
                // DSASReporter.logSpaceDeparture(simulationTime, delay, departure);
                nextDepartureAvailable += Constants.DEP_DEP_MIN;
            } else {
                nextDepartureAvailable += Constants.DEP_DEP_MIN;
            }
        }
        return nextDepartureAvailable - Constants.DEP_DEP_MIN;
    }

    // compare with last take off
    private int analyzeFirstDeparture(SimulatedTrajectory departure, final int simulationTime, final int lastTakeOffTime) {
        int currentDepartureTime = departure.getSlotMarker().getDepartureTime();
        // TODO: optimize
        if (currentDepartureTime < simulationTime) {
            departure.recalculateTrajectoryWithDelayAt(simulationTime, simulationTime - currentDepartureTime);
            currentDepartureTime = departure.getSlotMarker().getDepartureTime();
        }
        // separate with previous take off
        if (lastTakeOffTime > 0) {
            int timeSeparation = currentDepartureTime - lastTakeOffTime;
            if (timeSeparation < Constants.DEP_DEP_MIN) {
                departure.recalculateTrajectoryWithDelayAt(simulationTime, Constants.DEP_DEP_MIN - timeSeparation);
            }
        } else {
            // no previous take off
        }

        return Math.max(lastTakeOffTime, departure.getSlotMarker().getDepartureTime());
    }

    private int analyzeGapsForArrivals(final int simulationTime, final List<SimpleDepartureGap> gaps, final int lastLandingTime) {
        if (arrivalSequence.getSimulatedTrajectories().isEmpty()) {
            // no arrival
            return lastLandingTime;
        }
        // find index of last scheduled arrival
        int lastScheduledArrival = -1;
        if (!scheduledArrivals.isEmpty()) {
            lastScheduledArrival = arrivalSequence.getSimulatedTrajectories().indexOf(scheduledArrivals.get(scheduledArrivals.size() - 1));
        }

        int newLastTakeOffTime = lastLandingTime;
        int earliestLanding;
        int latestLanding;
        int lastLandingInGap;
        int remainingPossibleArrivals;
        SimulatedTrajectory arrival;
        boolean canLand;
        SimpleDepartureGap gap;
        for (int gapIndex = 0; gapIndex < gaps.size(); gapIndex++) {
            gap = gaps.get(gapIndex);
            remainingPossibleArrivals = gap.getNbArrivalsPossible();
            lastLandingInGap = -1;
            // for each gap
            for (int i = 0; i < remainingPossibleArrivals; i++) {
                if (lastScheduledArrival == (arrivalSequence.getSimulatedTrajectories().size() - 1)) {
                    return newLastTakeOffTime;
                }
                // try to fitt a landing in the gap
                earliestLanding = Math.max(lastLandingTime, gap.getEarliestTimeAtNumber(i + 1));
                latestLanding = gap.getEndTime() - Constants.ARR_DEP_MIN;
                // not optimized

                arrival = arrivalSequence.getAtIndex(lastScheduledArrival + 1);
                canLand = (!scheduledArrivals.contains(arrival)) && arrival.getArrivalTime() <= latestLanding;
                if (canLand) {
                    int newLandingTime = Math.max(earliestLanding, lastLandingInGap + Constants.ARR_ARR_MIN);
                    newLandingTime = Math.min(earliestLanding, newLandingTime);
                    arrival.recalculateTrajectoryWithDelayAt(simulationTime, newLandingTime - arrival.getArrivalTime());
                    lastLandingInGap = arrival.getArrivalTime();
                    lastScheduledArrival = arrivalSequence.getSimulatedTrajectories().indexOf(arrival);
                    scheduledArrivals.add(arrival);
                    if (lastLandingInGap > latestLanding) {
                        // gap no longer usable
                        break;
                    }
                } else {
                    // log gap is not usable for landing
                    // proceed to next gap
                    break;
                }

            }
        }
        return newLastTakeOffTime;
    }

    // @Override
    public String getDisplayName() {
        return "DSAS 1";
    }

}
