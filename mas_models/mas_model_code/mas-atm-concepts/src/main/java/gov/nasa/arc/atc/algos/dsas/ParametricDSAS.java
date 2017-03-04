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
import gov.nasa.arc.atc.functions.CandidateFunction;
import gov.nasa.arc.atc.functions.GapSchedulingFunction;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.reports.dsas.DSASReportInputs;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.Comparators;
import gov.nasa.arc.atc.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.util.Pair;

/**
 * 
 * @author ahamon
 *
 */
public class ParametricDSAS implements DSAS {

	private static final Logger LOG = Logger.getGlobal();

	private static final boolean LOG_ACTIVATED = false;

	private final List<SimulatedTrajectory> scheduledDepartures;
	private final CandidateFunction slotCandidateFunction;
	private final CandidateFunction departureCandidateFunction;
	private final GapSchedulingFunction gapFunction;

	// for report
	private final DSASReportInputs reportInputs;

	private SimulationContext context;
	private ATCNode departureNode;
	private ATCNode arrivalNode;
	private DepartureSequence departureSequence;
	private ArrivalSequence arrivalSequence;

	public ParametricDSAS(CandidateFunction slotCandidateF, CandidateFunction departureCandidateF, GapSchedulingFunction gapSchedulingFunction) {
		scheduledDepartures = new ArrayList<>();
		slotCandidateFunction = slotCandidateF;
		departureCandidateFunction = departureCandidateF;
		gapFunction = gapSchedulingFunction;
		//
		reportInputs = new DSASReportInputs();
	}

	@Override
	public void setSimulationConfiguration(String scenarioName, int simulationDuration, int stepDuration) {
		reportInputs.setScenarioName(scenarioName);
		reportInputs.setSimulationDuration(simulationDuration);
		reportInputs.setStepDuration(stepDuration);
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
		if (LOG_ACTIVATED) {
			LOG.log(Level.FINE, "hasDepartureSequence: {0}", hasDepartureSequence);
		}
		if (LOG_ACTIVATED) {
			LOG.log(Level.FINE, "hasArrivalSequence: {0}", hasArrivalSequence);
		}
		assert hasDepartureSequence;
		assert hasArrivalSequence;
	}

	@Override
	public boolean execute(int simulationTime) {
		if (LOG_ACTIVATED) {
			LOG.log(Level.INFO, "T = {0} :: >> Executing DSAS2...", simulationTime);
		}

		departureSequence = context.getDepartureSequences().get(departureNode);
		arrivalSequence = context.getArrivalSequences().get(arrivalNode);

		// Log report
		reportInputs.logSequencesInDSAS(context, departureNode, arrivalNode, simulationTime);

		// reportInputs.logTakeOffsLandings(simulationTime, Collections.unmodifiableList(context.getTakeOffs().get(departureNode.getName())), Collections.unmodifiableList(context.getLandings().get(arrivalNode.getName())));
		//
		scheduledDepartures.clear();
		//

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
		// report last take off and last landing
		reportInputs.logLastTakeOffAndLanding(simulationTime, lastTakeOffTime, lastLandingTime);

		// sequence departures
		//// only ensures that departures are correctly spaced
		sequenceDepartures(simulationTime, lastTakeOffTime);

		if (LOG_ACTIVATED)
			LOG.log(Level.FINE, "Last take off time = {0}", lastTakeOffTime);

		// build gaps sequence for arrival to fit in
		List<ArrivalGap> gaps = new ArrayList<>();

		// we do NOT create gap before first arrival

		for (int i = 0; i < arrivalSequence.getSimulatedTrajectories().size() - 1; i++) {
			if (slotCandidateFunction.isCandidate(arrivalSequence.getAtIndex(i + 1), simulationTime)) {
				gaps.add(new ArrivalGap(arrivalSequence.getAtIndex(i), arrivalSequence.getAtIndex(i + 1)));
			} else {
				// if trailing aircraft is not a candidate for the algorithm, there is no need to actually keep creating and analysing gaps
				break;
			}
		}

		// analyze gaps
		for (int i = 0; i < gaps.size() - 1; i++) {
			if (!analyzeGap(simulationTime, gaps.get(i), gaps.get(i + 1))) {
				break;
			}
		}

		//// analyse last gap
		if (!gaps.isEmpty()) {
			ArrivalGap lastGap = gaps.get(gaps.size() - 1);
			int newDuration = gapFunction.getNewGapDuration(lastGap);
			int lastGapDelay = lastGap.getStartTime() + newDuration - lastGap.getEndTime();
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
		if (LOG_ACTIVATED) {
			LOG.log(Level.FINE, "Last take off time = {0}", lastTakeOffTime);
		}

		reportInputs.logSequencesOutDSAS(context, departureNode, arrivalNode, simulationTime);
		return true;
	}

	@Override
	public String getDisplayName() {
		return "DSAS 2";
	}

	private void setContext(SimulationContext simulationContext) {
		if (simulationContext == null) {
			throw new IllegalArgumentException("SimulationContext cannot be null");
		}
		context = simulationContext;
	}

	private void sequenceDepartures(final int simulationTime, final int lastTakeOffTime) {
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
				//
				departure.recalculateTrajectoryWithDelayAt(simulationTime, delay);
				nextDepartureAvailable += Constants.DEP_DEP_MIN;
			} else {
				nextDepartureAvailable += Constants.DEP_DEP_MIN;
			}
		}
	}

	// compare with last take off
	private int analyzeFirstDeparture(SimulatedTrajectory departure, final int simulationTime, final int lastTakeOffTime) {

		if (!departureCandidateFunction.isCandidate(departure, simulationTime)) {
			return lastTakeOffTime;
		}
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

	private boolean analyzeGap(final int simulationTime, ArrivalGap gap, ArrivalGap nextGap) {
		// NOTE: in this implementation, cannot speed up arrivals
		if (!slotCandidateFunction.isCandidate(gap.getSecondArrival(), simulationTime)) {
			return false;
		}
		int newDuration = gapFunction.getNewGapDuration(gap);
		int delay = gap.getStartTime() + newDuration - gap.getEndTime();
		if (delay > 0) {
			gap.getSecondArrival().recalculateTrajectoryWithDelayAt(simulationTime, delay);
			gap.updateGapAttributes();
			nextGap.updateGapAttributes();
		}
		return true;
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
					// TODO: log unsued gap
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
					// TODO: log unsued gap
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
						// TODO: log unsued gap
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
					// TODO: log unsued gap
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
						// TODO: log unsued gap
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
		//TODO: explain why this block is mandatory or if optimization can be made
		List<SimulatedTrajectory> candidateTrajectories = arrivalSequence.getSimulatedTrajectories().stream().filter(t->slotCandidateFunction.isCandidate(t, simulationTime)).sorted(Comparators.SIMULATED_TRAJECTORY_ORIGINAL_ARRIVAL_TIME_COMPARATOR).collect(Collectors.toList());

		if (!candidateTrajectories.isEmpty()) {
			nextDepartureAvailable = Math.max(nextDepartureAvailable, candidateTrajectories.get(candidateTrajectories.size() - 1).getArrivalTime() + Constants.ARR_DEP_MIN);
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
				lastDeparture = departure.getSlotMarker().getDepartureTime();
				nextDepartureAvailable = Math.max(lastDeparture + Constants.DEP_DEP_MIN, nextDepartureAvailable);
			}
		}
		return nextDepartureAvailable;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DSAS:  Arrivals @");
		sb.append(arrivalNode.getName());
		sb.append(" departures @").append(departureNode.getName());
		sb.append("  with [").append(slotCandidateFunction);
		sb.append(",  ").append(departureCandidateFunction);
		sb.append(",  ").append(gapFunction);
		sb.append("]");
		return sb.toString();
	}

}
