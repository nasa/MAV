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
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.ArrivalSequenceUtils;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.GapUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 * 
 * @author ahamon
 *
 */
public class StandardDepartureAlgo implements DepartureArrivalAlgorithm {

	private static final Logger LOG = Logger.getGlobal();

	private final List<SimulatedTrajectory> scheduledDepartures;

	private SimulationContext context;
	private ATCNode departureNode;
	private ATCNode arrivalNode;
	private DepartureSequence departureSequence;
	private ArrivalSequence arrivalSequence;

	public StandardDepartureAlgo() {
		scheduledDepartures = new ArrayList<>();
	}

	@Override
	public void setSimulationConfiguration(String scenarioName, int simulationDuration, int stepDuration) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initializeData(SimulationContext simulationContext, ATCNode dNode, ATCNode aNode) {
		// boolean nbNodesOK = nodes.length == 2;
		// LOG.log(Level.FINE, "ATCNodes ok: {0}", nbNodesOK);
		// assert nbNodesOK;
		setContext(simulationContext);
		departureNode = dNode;
		arrivalNode = aNode;
		setContext(simulationContext);
		departureSequence = context.getDepartureSequences().get(departureNode);
		arrivalSequence = context.getArrivalSequences().get(arrivalNode);
		boolean hasDepartureSequence = departureSequence != null;
		boolean hasArrivalSequence = arrivalSequence != null;
		LOG.log(Level.FINE, "hasDepartureSequence: {0}", hasDepartureSequence);
		LOG.log(Level.FINE, "hasArrivalSequence: {0}", hasArrivalSequence);
		assert hasDepartureSequence;
		assert hasArrivalSequence;

	}

	@Override
	public boolean execute(final int simulationTime) {
		LOG.log(Level.INFO, "T = {0} :: >> Executing Standard Departure...", simulationTime);
		// for (int i = 0; i < params.length; i++) {
		// LOG.log(Level.INFO, "T = {0} :: >> with parameter...", new Object[] { simulationTime, params[i] });
		// }
		// HUM
		departureSequence = context.getDepartureSequences().get(departureNode);
		arrivalSequence = context.getArrivalSequences().get(arrivalNode);
		//
		scheduledDepartures.clear();

		// find last landing
		int lastLandingTime = -1;
		for (Pair<SimulatedTrajectory, Integer> landing : context.getLandings().get(arrivalNode.getName())) {
			lastLandingTime = Math.max(lastLandingTime, landing.getValue());
		}
		// TODO: check landing time < sim Time, and equivalent or take of time

		// find last take off
		int lastTakeOffTime = -1;
		for (Pair<SimulatedTrajectory, Integer> takeOff : context.getTakeOffs().get(departureNode.getName())) {
			lastTakeOffTime = Math.max(lastTakeOffTime, takeOff.getValue());
		}

		List<SimpleArrivalGap> allArrivalGaps;
		allArrivalGaps = new ArrayList<>(ArrivalSequenceUtils.calculateGaps(arrivalSequence));
		// overkill?
		Collections.sort(allArrivalGaps, GapUtils.START_TIME_GAP_COMPARATOR);

		// try to schedule before first arrival
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
		lastTakeOffTime = analyzeGapsForDepartures(allArrivalGaps, simulationTime, lastTakeOffTime);

		// schedule remaining departures
		lastTakeOffTime = scheduleRemainingDepartures(simulationTime, lastTakeOffTime);
		LOG.log(Level.FINE, "Last take off time = {0}", lastTakeOffTime);
		return true;
	}

	private void setContext(SimulationContext simulationContext) {
		if (simulationContext == null) {
			throw new IllegalArgumentException("ATC Geography cannot be null");
		}
		context = simulationContext;
	}

	// compare with last take off and last landing
	private Pair<Boolean, Integer> analyzeTakeOffBeforeNextArrival(SimulatedTrajectory departure, final int simulationTime, final int lastLandingTime, final int lastTakeOffTime) {
		int currentDepartureTime = departure.getSlotMarker().getStartTime();
		// if departures starts after next arrival, return
		if (!arrivalSequence.getSimulatedTrajectories().isEmpty() && arrivalSequence.getAtIndex(0).getArrivalTime() <= currentDepartureTime) {
			return new Pair<>(false, lastTakeOffTime);
		}
		// TODO: optimize
		if (currentDepartureTime < simulationTime) {
			departure.recalculateTrajectoryWithDelayAt(simulationTime, simulationTime - currentDepartureTime);
			currentDepartureTime = departure.getSlotMarker().getStartTime();
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
			if (departure.getSlotMarker().getStartTime() >= nextArrivalTime) {
				isScheduled = false;
            } else
                isScheduled = nextArrivalTime - departure.getSlotMarker().getStartTime() >= Constants.DEP_ARR_MIN;
		} else {
			// no arrival in sequence
			isScheduled = true;
		}
		if (isScheduled) {
			return new Pair<>(isScheduled, departure.getSlotMarker().getStartTime());
		} else {
			return new Pair<>(false, lastTakeOffTime);
		}
	}

	private int analyzeGapsForDepartures(final List<SimpleArrivalGap> allArrivalGaps, final int simulationTime, int lastTakeOffTime) {
		SimpleArrivalGap gap;
		int newLastTakeOffTime = lastTakeOffTime;
		for (int gapIndex = 0; gapIndex < allArrivalGaps.size(); gapIndex++) {
			gap = allArrivalGaps.get(gapIndex);
			switch (gap.getGapType()) {
			case NO_SPACE:
				// not enought space
				break;
			case SINGLE:
				newLastTakeOffTime = Math.max(lastTakeOffTime, scheduleOneDeparture(gap, simulationTime));
				break;
			case DOUBLE:
			case B757:
				newLastTakeOffTime = Math.max(lastTakeOffTime, scheduleTwoDepartures(gap, simulationTime));
				break;

			default:
				break;
			}
		}
		return newLastTakeOffTime;
	}

	private int scheduleOneDeparture(SimpleArrivalGap gap, final int simulationTime) {
		// schedule departure
		final int newEarliestDepartureTime = gap.getStartTime() + Constants.ARR_DEP_MIN;
		final int newLatestDepartureTime = gap.getEndTime() - Constants.DEP_ARR_MIN;
		int oldDepartureTime;
		for (int i = 1; i < departureSequence.getNbDepartures(); i++) {
			SimulatedTrajectory departure = departureSequence.getAtIndex(i);
			oldDepartureTime = departure.getSlotMarker().getStartTime();
			if (!scheduledDepartures.contains(departure)) {
				boolean canDepart = oldDepartureTime <= newLatestDepartureTime;
				if (canDepart) {
					int newDepartTime = Math.max(oldDepartureTime, newEarliestDepartureTime);
					departure.recalculateTrajectoryWithDelayAt(simulationTime, newDepartTime - oldDepartureTime);
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

	private int scheduleTwoDepartures(SimpleArrivalGap gap, int simulationTime) {
		// schedule first departure
		final int newEarliestDepartureTime = gap.getStartTime() + Constants.ARR_DEP_MIN;
		final int newLatestDepartureTime = gap.getEndTime() - Constants.DEP_ARR_MIN;
		SimulatedTrajectory departure;
		int oldDepartureTime;
		int firstDepartureIndex = -1;
		int lastDepartureTime = -1;
		boolean unusableGap = false;
		for (int i = 1; i < departureSequence.getNbDepartures(); i++) {
			departure = departureSequence.getAtIndex(i);
			oldDepartureTime = departure.getSlotMarker().getStartTime();
			if (!scheduledDepartures.contains(departure)) {
				boolean canDepart = oldDepartureTime <= newLatestDepartureTime;
				if (canDepart) {
					int newDepartTime = Math.max(oldDepartureTime, newEarliestDepartureTime);
					departure.recalculateTrajectoryWithDelayAt(simulationTime, newDepartTime - oldDepartureTime);
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
		if (!unusableGap) {
			for (int i = firstDepartureIndex + 1; i < departureSequence.getNbDepartures(); i++) {
				departure = departureSequence.getAtIndex(i);
				oldDepartureTime = departure.getSlotMarker().getStartTime();
				if (!scheduledDepartures.contains(departure)) {
					boolean canDepart = oldDepartureTime <= newLatestDepartureTime;
					if (canDepart) {
						int newDepartTime = Math.max(oldDepartureTime, newEarliestDepartureTime);
						departure.recalculateTrajectoryWithDelayAt(simulationTime, newDepartTime - oldDepartureTime);
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

	private int scheduleRemainingDepartures(final int simulationTime, int lastTakeOffTime) {
		int oldDepartureTime;
		int nextDepartureAvailable = lastTakeOffTime + Constants.DEP_DEP_MIN;
		// i=1??
		for (int i = 1; i < departureSequence.getNbDepartures(); i++) {
			SimulatedTrajectory departure = departureSequence.getAtIndex(i);
			if (!scheduledDepartures.contains(departure)) {
				oldDepartureTime = departure.getSlotMarker().getStartTime();
				if (oldDepartureTime < nextDepartureAvailable) {
					int delay = nextDepartureAvailable - oldDepartureTime;
					departure.recalculateTrajectoryWithDelayAt(simulationTime, delay);
					nextDepartureAvailable += Constants.DEP_DEP_MIN;
					scheduledDepartures.add(departure);
				}
			}
		}
		return nextDepartureAvailable;
	}

	// @Override
	public String getDisplayName() {
		return "Standard departures";
	}

}
