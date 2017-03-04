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
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.util.Pair;

/**
 * @author ahamon
 */
public class DSASConcept implements DSAS {

    private static final Logger LOG = Logger.getGlobal();

    public static final int NONE_TO_MID_SINGLE = Constants.DEP_DEP_MIN + (Constants.SINGLE - Constants.DEP_DEP_MIN) / 2;
    public static final int SINGLE_TO_MID_DOUBLE = Constants.SINGLE + (Constants.DOUBLE - Constants.SINGLE) / 2;
    public static final int DOUBLE_TO_MID_TRIPLE = Constants.DOUBLE + (Constants.TRIPLE - Constants.DOUBLE) / 2;
    public static final int TRIPLE_TO_MID_B757 = Constants.TRIPLE + (Constants.B757 - Constants.TRIPLE) / 2;

    private final List<SimulatedTrajectory> scheduledDepartures;

    private SimulationContext context;
    private ATCNode departureNode;
    private ATCNode arrivalNode;
    private DepartureSequence departureSequence;
    private ArrivalSequence arrivalSequence;

    private final boolean logActivated = false;

    public DSASConcept() {
        scheduledDepartures = new ArrayList<>();
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
        if (logActivated) {
            LOG.log(Level.FINE, "hasDepartureSequence: {0}", hasDepartureSequence);
            LOG.log(Level.FINE, "hasArrivalSequence: {0}", hasArrivalSequence);
        }
        assert hasDepartureSequence;
        assert hasArrivalSequence;
    }

    @Override
    public boolean execute(int simulationTime) {
        if (logActivated) {
            LOG.log(Level.INFO, "T = {0} :: >> Executing DSAS2...", simulationTime);
        }
        // for (int i = 0; i < params.length; i++) {
        // if (logActivated)
        // LOG.log(Level.INFO, "T = {0} :: >> with parameter...", new Object[] { simulationTime, params[i] });
        // }
        //
        departureSequence = context.getDepartureSequences().get(departureNode);
        arrivalSequence = context.getArrivalSequences().get(arrivalNode);
        System.err.println(" ----- t= " + simulationTime);
        arrivalSequence.getSimulatedTrajectories().forEach(a -> System.err.println(" > " + a.getSlotMarker().getName() + " :: " + a.getArrivalTime()));

        System.err.println("--------------");

        // log departure and arrival sequences IN
        // AlgoUtils.logSequencesInDSAS(context, departureNode, arrivalNode, simulationTime);
        // DSASReporter.logTakeOffsLandings(simulationTime, Collections.unmodifiableList(context.getTakeOffs().get(departureNode.getName())), Collections.unmodifiableList(context.getLandings().get(arrivalNode.getName())));
        //
        scheduledDepartures.clear();
        //

        // find last landing
        int lastLandingTime = -1;
        // System.err.println(" ### DSAS Concept 2 :: execute >> context = "+context);
        // System.err.println(" ### DSAS Concept 2 :: execute >> landing nodes = "+context.getLandings().keySet());
        for (Pair<SimulatedTrajectory, Integer> landing : context.getLandings().get(arrivalNode.getName())) {
            lastLandingTime = Math.max(lastLandingTime, landing.getValue());
        }

        // find last take off
        int lastTakeOffTime = -1;
        for (Pair<SimulatedTrajectory, Integer> takeOff : context.getTakeOffs().get(departureNode.getName())) {
            lastTakeOffTime = Math.max(lastTakeOffTime, takeOff.getValue());
        }
        // report last take off and last landing
        // DSASReporter.logLastTakeOffAndLanding(simulationTime, lastTakeOffTime, lastLandingTime);

        // sequence departures
        //// only ensures that departures are correctly spaced
        sequenceDepartures(simulationTime, lastTakeOffTime);

        if (logActivated)
            LOG.log(Level.FINE, "Last take off time = {0}", lastTakeOffTime);

        // build gaps sequence for arrival to fit in
        List<ArrivalGap> gaps = new ArrayList<>();

        // we do NOT create gap before first arrival

        for (int i = 0; i < arrivalSequence.getSimulatedTrajectories().size() - 1; i++) {
            gaps.add(new ArrivalGap(arrivalSequence.getAtIndex(i), arrivalSequence.getAtIndex(i + 1)));
        }

        // analyze gaps
        for (int i = 0; i < gaps.size() - 1; i++) {
            analyzeGap(simulationTime, gaps.get(i), gaps.get(i + 1));
        }

        //// analyse last gap
        if (!gaps.isEmpty()) {
            ArrivalGap lastGap = gaps.get(gaps.size() - 1);
            int newDuration = getNewGapDuration(lastGap);
            int lastGapDelay = lastGap.getStartTime() + newDuration - lastGap.getEndTime();
            // ArrivalGapType newType = getNewGapType(lastGap);
            // int lastGapDelay = lastGap.getStartTime() + newType.getGapDuration() - lastGap.getEndTime();
            if (lastGapDelay > 0) {
                lastGap.getSecondArrival().recalculateTrajectoryWithDelayAt(simulationTime, lastGapDelay);
                lastGap.updateGapAttributes();
            }
        }

        // schedule departures to fit in calculated gaps
        //// schedule before first arrival
        for (int i = 0; i < departureSequence.getNbDepartures(); i++) {
            Pair<Boolean, Integer> result = analyzeTakeOffBeforeNextArrival(departureSequence.getAtIndex(i), simulationTime, lastLandingTime, lastTakeOffTime);
            if (result.getKey()) {
                // if scheduled
                lastTakeOffTime = result.getValue();
                scheduledDepartures.add(departureSequence.getAtIndex(i));
            } else {
                break;
            }
        }

        // analyze gaps for departures
        lastTakeOffTime = scheduleDeparturesInGaps(gaps, lastTakeOffTime);

        // schedule remaining departures
        lastTakeOffTime = scheduleRemainingDepartures(gaps, simulationTime, lastTakeOffTime);
        if (logActivated)
            LOG.log(Level.FINE, "Last take off time = {0}", lastTakeOffTime);

        // log departure and arrival sequences OUT
        // AlgoUtils.logSequencesOutDSAS(context, departureNode, arrivalNode, simulationTime);



        System.err.println(" _-_-_-_ t= " + simulationTime);
        arrivalSequence.getSimulatedTrajectories().forEach(a -> System.err.println(" > " + a.getSlotMarker().getName() + " :: " + a.getArrivalTime()));

        System.err.println("__________________");

        return true;
    }

    @Override
    public String getDisplayName() {
        return "DSAS 2";
    }

    private void setContext(SimulationContext simulationContext) {
        if (simulationContext == null) {
            throw new IllegalArgumentException("ATC Geography cannot be null");
        }
        context = simulationContext;
    }

    private void sequenceDepartures(final int simulationTime, final int lastTakeOffTime) {
//		System.err.println(" sequence departures ");
        int oldDepartureTime;
        if (departureSequence.getNbDepartures() > 0) {
            oldDepartureTime = analyzeFirstDeparture(departureSequence.getAtIndex(0), simulationTime, lastTakeOffTime);
        } else {
            return;
        }
        int nextDepartureAvailable = oldDepartureTime + Constants.DEP_DEP_MIN;
        for (int i = 1; i < departureSequence.getNbDepartures(); i++) {
            SimulatedTrajectory departure = departureSequence.getAtIndex(i);
            oldDepartureTime = departure.getSlotMarker().getDepartureTime();
            if (oldDepartureTime < nextDepartureAvailable) {
                int delay = nextDepartureAvailable - oldDepartureTime;
//                System.err.println(" => recalculateTrajectoryWithDelayAt t="+simulationTime+" delay = "+delay);
                departure.recalculateTrajectoryWithDelayAt(simulationTime, delay);
                nextDepartureAvailable += Constants.DEP_DEP_MIN;
            } else {
                nextDepartureAvailable += Constants.DEP_DEP_MIN;
            }
        }
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
        return Math.max(lastTakeOffTime, departure.getSlotMarker().getStartTime());
    }

    private void analyzeGap(final int simulationTime, ArrivalGap gap, ArrivalGap nextGap) {
        // NOTE: in this implementation, cannot speed up arrivals
        int newDuration = getNewGapDuration(gap);
        int delay = gap.getStartTime() + newDuration - gap.getEndTime();
        if (delay > 0) {
            gap.getSecondArrival().recalculateTrajectoryWithDelayAt(simulationTime, delay);
            gap.updateGapAttributes();
            nextGap.updateGapAttributes();
        }
    }

    // private ArrivalGapType getNewGapType(Gap gap) {
    // if (gap.getGapDuration() < NONE_TO_MID_SINGLE) {
    // // the gap is too small
    // return ArrivalGapType.NO_SPACE;
    // } else if (gap.getGapDuration() < SINGLE_TO_MID_DOUBLE) {
    // return ArrivalGapType.SINGLE;
    // } else if (gap.getGapDuration() < DOUBLE_TO_MID_TRIPLE) {
    // return ArrivalGapType.DOUBLE;
    // } else if (gap.getGapDuration() < TRIPLE_TO_MID_B757) {
    // return ArrivalGapType.TRIPLE;
    // } else {
    // return ArrivalGapType.B757;
    // }
    // }

    private int getNewGapDuration(Gap gap) {
        if (gap.getGapDuration() < NONE_TO_MID_SINGLE) {
            // the gap is too small
            return gap.getGapDuration();
        } else if (gap.getGapDuration() < SINGLE_TO_MID_DOUBLE) {
            return Constants.SINGLE;
        } else if (gap.getGapDuration() < DOUBLE_TO_MID_TRIPLE) {
            return Constants.DOUBLE;
        } else if (gap.getGapDuration() < TRIPLE_TO_MID_B757) {
            return Constants.TRIPLE;
        } else if (gap.getGapDuration() == TRIPLE_TO_MID_B757) {
            return Constants.B757;
        } else {
            return gap.getGapDuration();
        }
    }

    // compare with last take off and last landing
    private Pair<Boolean, Integer> analyzeTakeOffBeforeNextArrival(SimulatedTrajectory departure, final int simulationTime, final int lastLandingTime, final int lastTakeOffTime) {
        int currentDepartureTime = departure.getSlotMarker().getDepartureTime();
        // if departures starts after next arrival, return
        if (!arrivalSequence.getSimulatedTrajectories().isEmpty() && arrivalSequence.getAtIndex(0).getArrivalTime() <= currentDepartureTime) {
            return new Pair<>(false, lastTakeOffTime);
        }
        // TODO: optimize
        if (currentDepartureTime < simulationTime) {
            departure.recalculateTrajectoryWithDelayAt(simulationTime, simulationTime - currentDepartureTime);
            currentDepartureTime = departure.getSlotMarker().getDepartureTime();
        }
        boolean isScheduled;
        // separate with previous landing
        if (lastLandingTime > 0) {
            int timeSeparation = currentDepartureTime - lastLandingTime;
            if (timeSeparation < Constants.ARR_DEP_MIN) {
                departure.recalculateTrajectoryWithDelayAt(simulationTime, Constants.ARR_DEP_MIN - timeSeparation);
            }
        } else {
            // no previous landing
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

        // see if can take off before next arrival
        if (!arrivalSequence.getSimulatedTrajectories().isEmpty()) {
            final int nextArrivalTime = arrivalSequence.getAtIndex(0).getArrivalTime();
            if (departure.getSlotMarker().getDepartureTime() >= nextArrivalTime) {
                isScheduled = false;
            } else
                isScheduled = nextArrivalTime - departure.getSlotMarker().getDepartureTime() >= Constants.DEP_ARR_MIN;
        } else {
            // no arrival in sequence
            isScheduled = true;
        }
        if (isScheduled) {
            return new Pair<>(isScheduled, departure.getSlotMarker().getDepartureTime());
        } else {
            return new Pair<>(false, lastTakeOffTime);
        }
    }

    private int scheduleDeparturesInGaps(final List<ArrivalGap> allArrivalGaps, int lastTakeOffTime) {
        ArrivalGap gap;
        int newLastTakeOffTime = lastTakeOffTime;
        for (int gapIndex = 0; gapIndex < allArrivalGaps.size(); gapIndex++) {
            gap = allArrivalGaps.get(gapIndex);
            // newLastTakeOffTime = Math.max(lastTakeOffTime, scheduleSeveralDepartures(gap));
            switch (gap.getNbDeparturesPossible()) {
                // do nothing
                case 0:
                    break;
                case 1:
                    newLastTakeOffTime = Math.max(lastTakeOffTime, scheduleOneDeparture(gap));
                    break;
                case 2:
                    newLastTakeOffTime = Math.max(lastTakeOffTime, scheduleTwoDepartures(gap));
                    break;
                default:
                    newLastTakeOffTime = Math.max(lastTakeOffTime, scheduleSeveralDepartures(gap));
                    break;
            }
        }
        return newLastTakeOffTime;
    }

    private int scheduleOneDeparture(ArrivalGap gap) {
        // schedule departure
        final int newEarliestDepartureTime = gap.getStartTime() + Constants.ARR_DEP_MIN;
        final int newLatestDepartureTime = gap.getEndTime() - Constants.DEP_ARR_MIN;
        int oldDepartureTime;
        for (int i = 0; i < departureSequence.getNbDepartures(); i++) {
            SimulatedTrajectory departure = departureSequence.getAtIndex(i);
            oldDepartureTime = departure.getSlotMarker().getDepartureTime();
            if (!scheduledDepartures.contains(departure)) {
                boolean canDepart = oldDepartureTime <= newLatestDepartureTime;
                if (canDepart) {
                    int newDepartTime = Math.max(oldDepartureTime, newEarliestDepartureTime);
                    departure.getSlotMarker().setDepartureTime(newDepartTime);
                    scheduledDepartures.add(departure);
                    return newDepartTime;
                } else {
                    // TODO: log unused gap
                }
                break;
            } else {
                // nothing, plane is scheduled
            }
        }
        return -1;
    }

    private int scheduleTwoDepartures(ArrivalGap gap) {
        // schedule first departure
        final int gapEarliestDepartureTime = gap.getStartTime() + Constants.ARR_DEP_MIN;
        final int gapLatestDepartureTime = gap.getEndTime() - Constants.DEP_ARR_MIN;
        SimulatedTrajectory departure;
        int oldDepartureTime;
        int firstDepartureIndex = -1;
        int lastDepartureTime = -1;
        boolean unusableGap = false;
        // schedule first departure in gap
        for (int i = 0; i < departureSequence.getNbDepartures(); i++) {
            departure = departureSequence.getAtIndex(i);
            oldDepartureTime = departure.getSlotMarker().getDepartureTime();
            if (!scheduledDepartures.contains(departure)) {
                boolean canDepart = oldDepartureTime <= gapLatestDepartureTime;
                if (canDepart) {
                    int newDepartTime = Math.max(oldDepartureTime, gapEarliestDepartureTime);
                    departure.getSlotMarker().setDepartureTime(newDepartTime);
                    scheduledDepartures.add(departure);
                    firstDepartureIndex = i;
                    lastDepartureTime = newDepartTime;
                } else {
                    unusableGap = true;
                    // TODO: log unused gap
                }
                break;
            } else {
                // nothing, plane is scheduled
            }
        }
        // schedule second departure
        if (!unusableGap && (lastDepartureTime <= gapLatestDepartureTime - Constants.DEP_DEP_MIN)) {
            for (int i = firstDepartureIndex + 1; i < departureSequence.getNbDepartures(); i++) {
                departure = departureSequence.getAtIndex(i);
                oldDepartureTime = departure.getSlotMarker().getDepartureTime();
                if (!scheduledDepartures.contains(departure)) {
                    boolean canDepart = oldDepartureTime <= gapLatestDepartureTime;
                    if (canDepart) {
                        int newDepartTime = lastDepartureTime + Constants.DEP_DEP_MIN;
                        departure.getSlotMarker().setDepartureTime(newDepartTime);
                        scheduledDepartures.add(departure);
                        lastDepartureTime = newDepartTime;
                    } else {
                        // TODO: log unused gap
                    }
                    break;
                } else {
                    // nothing, plane is scheduled
                }
            }
        }
        return lastDepartureTime;
    }

    private int scheduleSeveralDepartures(ArrivalGap gap) {
        // schedule first departure
        final int gapEarliestDepartureTime = gap.getStartTime() + Constants.ARR_DEP_MIN;
        final int gapLatestDepartureTime = gap.getEndTime() - Constants.DEP_ARR_MIN;
        SimulatedTrajectory departure;
        int oldDepartureTime;
        int lastDepartureTime = gap.getStartTime();
        boolean unusableGap = false;

        int firstDepartureIndex = -1;

        // schedule first departure in gap
        for (int i = 0; i < departureSequence.getNbDepartures(); i++) {
            departure = departureSequence.getAtIndex(i);
            oldDepartureTime = departure.getSlotMarker().getDepartureTime();
            if (!scheduledDepartures.contains(departure)) {
                boolean canDepart = oldDepartureTime <= gapLatestDepartureTime;
                if (canDepart) {
                    int newDepartTime = Math.max(oldDepartureTime, gapEarliestDepartureTime);
                    departure.getSlotMarker().setDepartureTime(newDepartTime);
                    scheduledDepartures.add(departure);
                    firstDepartureIndex = i;
                    lastDepartureTime = newDepartTime;
                } else {
                    unusableGap = true;
                    // TODO: log unused gap
                }
                break;
            } else {
                // nothing, plane is scheduled
            }
        }
        while (firstDepartureIndex < departureSequence.getNbDepartures() && !unusableGap) {
            firstDepartureIndex++;
            for (int i = firstDepartureIndex; i < departureSequence.getNbDepartures(); i++) {
                departure = departureSequence.getAtIndex(i);
                oldDepartureTime = departure.getSlotMarker().getDepartureTime();
                if (!scheduledDepartures.contains(departure)) {
                    boolean canDepart = oldDepartureTime <= gapLatestDepartureTime;
                    if (canDepart) {
                        int newDepartTime = Math.max(oldDepartureTime, lastDepartureTime + Constants.DEP_DEP_MIN);
                        departure.getSlotMarker().setDepartureTime(newDepartTime);
                        scheduledDepartures.add(departure);
                        firstDepartureIndex = i;
                        lastDepartureTime = newDepartTime;
                    } else {
                        unusableGap = true;
                        // TODO: log unused gap
                    }
                    break;
                } else {
                    // nothing, plane is scheduled
                }
            }
        }
        return lastDepartureTime;
    }

    private int scheduleRemainingDepartures(final List<ArrivalGap> gaps, final int simulationTime, int lastTakeOffTime) {
        int oldDepartureTime;
        int lastDeparture = Math.max(lastTakeOffTime, simulationTime - Constants.DEP_DEP_MIN);
        int lastGapEnd;
        if (gaps.isEmpty()) {
            lastGapEnd = simulationTime - Constants.ARR_DEP_MIN;
        } else {
            lastGapEnd = gaps.get(gaps.size() - 1).getEndTime();
        }
        int nextDepartureAvailable = Math.max(lastDeparture + Constants.DEP_DEP_MIN, lastGapEnd + Constants.ARR_DEP_MIN);
        if (!arrivalSequence.getSimulatedTrajectories().isEmpty()) {
            nextDepartureAvailable = Math.max(nextDepartureAvailable, arrivalSequence.getSimulatedTrajectories().get(arrivalSequence.getSimulatedTrajectories().size() - 1).getArrivalTime() + Constants.ARR_DEP_MIN);
        }
        // i=1??
        for (int i = 0; i < departureSequence.getNbDepartures(); i++) {
            SimulatedTrajectory departure = departureSequence.getAtIndex(i);
            if (!scheduledDepartures.contains(departure)) {
                oldDepartureTime = departure.getSlotMarker().getDepartureTime();
                if (oldDepartureTime < nextDepartureAvailable) {
                    departure.getSlotMarker().setDepartureTime(nextDepartureAvailable);
                }
                scheduledDepartures.add(departure);
                nextDepartureAvailable += Constants.DEP_DEP_MIN;
            } else {
                lastDeparture = departure.getSlotMarker().getStartTime();
                nextDepartureAvailable = Math.max(lastDeparture + Constants.DEP_DEP_MIN, nextDepartureAvailable);
            }
        }
        return nextDepartureAvailable;
    }

}
