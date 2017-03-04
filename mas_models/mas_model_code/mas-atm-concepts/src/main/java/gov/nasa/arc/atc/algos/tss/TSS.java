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

package gov.nasa.arc.atc.algos.tss;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.algos.ArrivalAlgorithm;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.functions.CandidateFunction;
import gov.nasa.arc.atc.functions.SeparationFunction;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.reports.ReportItemProperties;
import gov.nasa.arc.atc.reports.tss.TSSReportInputs;
import gov.nasa.arc.atc.simulation.SlotTrajectory;
import gov.nasa.arc.atc.simulation.ReverseSimulationUtils;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.AlgoUtils;
import gov.nasa.arc.atc.utils.CalculationTools;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahamon
 *
 */
public class TSS implements ArrivalAlgorithm {

	private static final String WITH_LOG = "withLog";

	private static final Logger LOG = Logger.getGlobal();
	//
	private static final String FOR_SLOT = " for slot ";
	private static final Level FINE = Level.OFF;
	private static final Level INFO = Level.OFF;

	private static final int EXTRA_DELAY = 10;

	private final CandidateFunction slotCandidateFunction;
	private final SeparationFunction slotSeparationFunction;

	// for report
	private final TSSReportInputs reportInputs;

	private SimulationContext context;
	private ATCNode arrivalNode;
	private ArrivalSequence arrivalSequence;

	private boolean withLog = false;
	private boolean logWithDSAS = false;

	/**
	 *
	 * @param configProperties the properties used for configuring TSS
	 * @param candidateFunction the function determining the candidate slots
	 * @param separationFunction the function separating two consecutive slots
	 */
	public TSS(Map<String, Object> configProperties, CandidateFunction candidateFunction, SeparationFunction separationFunction) {
		slotCandidateFunction = candidateFunction;
		slotSeparationFunction = separationFunction;
		//
		if (configProperties.containsKey(AlgoUtils.WITH_DSAS_PPTY)) {
			logWithDSAS = (boolean) configProperties.get(AlgoUtils.WITH_DSAS_PPTY);
		}
		LOG.log(Level.WARNING, "tss via dsas ? :: {0}", logWithDSAS);
		if (configProperties.containsKey(WITH_LOG)) {
			withLog = (boolean) configProperties.get(WITH_LOG);
		}
		//
		reportInputs = new TSSReportInputs(!logWithDSAS);
	}

	/**
	 * creates an instance of TSS with basic configuration by default all slots markers are eligible for TSS processing
	 *
	 * @param separationFunction the function separating two consecutive slots
	 */
	public TSS(SeparationFunction separationFunction) {
		this(Collections.emptyMap(), (trajectory, time) -> true, separationFunction);
	}

	@Override
	public void setSimulationConfiguration(String scenarioName, int simulationDuration, int stepDuration) {
		reportInputs.setScenarioName(scenarioName);
		reportInputs.setSimulationDuration(simulationDuration);
		reportInputs.setStepDuration(stepDuration);
	}

	@Override
	public void initializeData(SimulationContext simulationContext, ATCNode aNode) {
		setContext(simulationContext);
		arrivalNode = aNode;
		arrivalSequence = context.getArrivalSequences().get(arrivalNode);
		boolean hasArrivalSequence = arrivalSequence != null;

		log(FINE, "hasArrivalSequence: {0}", hasArrivalSequence);

		assert hasArrivalSequence;
	}

	@Override
	public boolean execute(final int simulationTime) {
		final long nanoIn = System.currentTimeMillis();

		log(INFO, "T = {0} :: >> Executing TSS...", simulationTime);

		// log for report: sequence before TSS is applied
		reportInputs.logSequencesInTSS(context, arrivalNode, simulationTime);

		// refresh arrival ?? bug fix ??
		arrivalSequence = context.getArrivalSequences().get(arrivalNode);

		meterArrivals(arrivalNode, arrivalSequence, simulationTime);

		// log for report: sequence after TSS is applied
		reportInputs.logSequencesOutTSS(context, arrivalNode, simulationTime);

		final long nanoOut = System.currentTimeMillis();
		final long ms = nanoOut - nanoIn;
		reportInputs.logInvocationDuration(simulationTime, ms);
		return true;
	}

	private void setContext(SimulationContext simulationContext) {
		if (simulationContext == null) {
			throw new IllegalArgumentException("ATC Geography cannot be null");
		}
		context = simulationContext;
	}

	private void meterArrivals(ATCNode node, ArrivalSequence sequence, final int time) {
		log(INFO, "T={0} Metering at node :: {1} nb slots :: {2}", new Object[] { time, node.getName(), sequence.getSimulatedTrajectories().size() });

		// for each pair of two consecutive slots
		for (int i = 0; i < sequence.getSimulatedTrajectories().size() - 1; i++) {
			// retrieve leading and trailing slots
			SlotTrajectory lead = (SlotTrajectory) sequence.getAtIndex(i);
			SlotTrajectory trail = (SlotTrajectory) sequence.getAtIndex(i + 1);
			meterConsecutiveSlots(time, node, lead, trail);
		}
		log(INFO, "!! >> DONE meterArrivals @node: {0}, starting recursion", node.getName());

		// recursively do the same for each previous node
		context.getGeography().getPreviousNodes(node.getName()).forEach(previousNode -> meterArrivals(previousNode, new ArrivalSequence(previousNode, sequence.getSimulatedTrajectories()), time));
	}

	/*
	 *********** Private methods to compute the metering
	 */

	private void meterConsecutiveSlots(final int time, ATCNode node, SlotTrajectory lead, SlotTrajectory trail) {

		// // ensures that the slots are valid candidates
		if (!slotCandidateFunction.isCandidate(lead, time) || !slotCandidateFunction.isCandidate(trail, time)) {
			return;
		}
		final int leadETA = lead.getArrivalTimeAtNode(node.getName());
		final int trailETA = trail.getArrivalTimeAtNode(node.getName());

		// log(INFO, "!! ** at time={0}, leadETA={1}", new Object[] { time, leadETA });
		final FlightParameters leadInParam = FlightParameters.clone(lead.getParametersAtSimulationTime(time));
		final FlightParameters leadInParamNext;
		if (lead.getParametersAtSimulationTime(time + 1) != null) {
			leadInParamNext = FlightParameters.clone(lead.getParametersAtSimulationTime(time + 1));
		} else {
			leadInParamNext = null;
		}
		// log(FINE, "*** >> Node={3} lead: {0} arriving at: {1} ... originally at {2}", new Object[] { lead.getSlotMarker().getName(), lead.getArrivalTimeAtNode(node.getName()), lead.getOriginalArrivalTimeAtNode(node.getName()), node.getName() });
		// log(FINE, "*** >> Node={3} trail: {0} arriving at: {1} ... originally at {2}", new Object[] { trail.getSlotMarker().getName(), trail.getArrivalTimeAtNode(node.getName()), trail.getOriginalArrivalTimeAtNode(node.getName()), node.getName() });

		// test if lead slot is arrived
		if (trailETA >= lead.getArrivalTime()) {
			log(FINE, " trailTimeAtNode >= lead.getArrivalTime()");
			return;
		}

		if (trailETA < leadETA) {
			delayEarlyTrail(time, node, lead, trail, leadETA, trailETA);
		}

		// creating the IN values for the report item
		final Map<String, Object> reportValues = initReportItemValues(time, node, lead, trail, leadETA);

		int conflictDelay;
		// meter the slots
		if (lead.getSlotMarker().getFlightPlan().getSegmentEndingAt(node.getName()).getEndSpeed() > trail.getSlotMarker().getFlightPlan().getSegmentEndingAt(node.getName()).getEndSpeed()) {
			log(FINE, "*** >> meterConsecutiveSlots Lead AtWPT @node: {0}", node.getName());
			conflictDelay = meterConsecutiveSlotsLeadAtWPT(node, time, lead, trail, reportValues);
		} else {
			log(FINE, "*** >> meterConsecutiveSlots Trail AtWPT @node: {0}", node.getName());
			conflictDelay = meterConsecutiveSlotsTrailAtWPT(node, time, lead, trail, reportValues);
		}

		log(FINE, "*** >> reportValues {0}", reportValues);
		log(FINE, "*** >> metering DONE delayed: {0}s", conflictDelay);

		// compare both trajectories to verify correct separation along the route
		final int leadETAEnd = lead.getArrivalTimeAtNode(node.getName());
		final FlightParameters leadEndParam = FlightParameters.clone(lead.getParametersAtSimulationTime(time));

		if (leadETA != leadETAEnd) {
			throw new IllegalStateException("Lead ETA changed from " + leadETA + " to " + leadETAEnd + FOR_SLOT + lead.getSlotMarker().getName());
		}
		if (!leadInParam.equals(leadEndParam)) {
			throw new IllegalStateException("Lead Parameters changed from " + leadInParam + " to " + leadEndParam + FOR_SLOT + lead.getSlotMarker().getName());
		}

		final FlightParameters leadOutParamNext;
		if (lead.getParametersAtSimulationTime(time + 1) != null) {
			leadOutParamNext = FlightParameters.clone(lead.getParametersAtSimulationTime(time + 1));
		} else {
			leadOutParamNext = null;
		}
		if (leadInParamNext != null && !leadInParamNext.equals(leadOutParamNext)) {
			throw new IllegalStateException("Lead Next Parameters changed from " + leadInParam + " to " + leadEndParam + FOR_SLOT + lead.getSlotMarker().getName());
		}
	}

	private int meterConsecutiveSlotsTrailAtWPT(ATCNode node, final int simulationTime, SlotTrajectory lead, SlotTrajectory trail, Map<String, Object> reportValues) {
		System.err.println("TSS :: meterConsecutiveSlotsTrailAtWPT @t="+simulationTime);
//		System.err.println("    >  lead = "+lead.getSlotMarker().getName()+" // FPL = "+lead.getSlotMarker().getFlightPlan());
//		System.err.println("    > trail = "+trail.getSlotMarker().getName()+" // FPL = "+trail.getSlotMarker().getFlightPlan());
		System.err.println("    >  lead = "+lead.getSlotMarker().getName() + "  arrives at "+lead.getArrivalTime());
		System.err.println("    > trail = "+trail.getSlotMarker().getName() + "  arrives at "+trail.getArrivalTime());
		// calculate arrival time at node for the trail aircraft
		int trailTimeAtNode = trail.getArrivalTimeAtNode(node.getName());

		if (trailTimeAtNode >= lead.getArrivalTime()) {
			return 0;
		}

		int delayNeeded = slotSeparationFunction.calculateDelayNeeded(trailTimeAtNode, lead, trail);

		if (delayNeeded > 0) {
			System.err.println("TSS ::  Delay needed = "+delayNeeded);
			ReverseSimulationUtils.introduceDelay(context, simulationTime, trail, delayNeeded);
			logScheduleConflictDetected(simulationTime, node, lead, trail, reportValues, trailTimeAtNode, delayNeeded);
		}

		return delayNeeded;
	}

	private int meterConsecutiveSlotsLeadAtWPT(ATCNode node, final int simulationTime, SlotTrajectory lead, SlotTrajectory trail, Map<String, Object> reportValues) {

		// calculate arrival time at the node for the lead aircraft
		int etaLead = lead.getArrivalTimeAtNode(node.getName());
		// int etaTrail = trail.getArrivalTimeAtNode(node.getName());
		// if (withLog) {
		// LOG.log(FINE, " ~~~ etaLead at node {0} :: {1}", new Object[] { node.getName(), etaLead });
		// LOG.log(FINE, " ~~~ etaTrail at node {0} :: {1}", new Object[] { node.getName(), etaTrail });
		// }

		//

		int delayNeeded = slotSeparationFunction.calculateDelayNeeded(etaLead, lead, trail);

		if (delayNeeded > 0) {
			ReverseSimulationUtils.introduceDelay(context, simulationTime, trail, delayNeeded);
			logScheduleConflictDetected(simulationTime, node, lead, trail, reportValues, etaLead, delayNeeded);
		}

		return delayNeeded;
	}

	//
	/**
	 * New occurring trail can have a smaller ETA than a lead which is already metered. This method delays the trailing slot so it arrives after the lead slot
	 * 
	 * @param leadETA the lead slot ETA
	 * @param trailETA the trailing slot ETA
	 */
	private void delayEarlyTrail(final int simulationTime, ATCNode node, SlotTrajectory lead, SlotTrajectory trail, final int leadETA, final int trailETA) {
		int additionalDelay;
		additionalDelay = leadETA - trailETA + EXTRA_DELAY;
		// if (withLog) {
		// LOG.log(FINE, " ~~~ Additional :: {0} ", additionalDelay);
		// }

		reportInputs.logNewSlotETAisBefore(simulationTime, node, lead, trail, additionalDelay);

		ReverseSimulationUtils.introduceDelay(context, simulationTime, trail, additionalDelay);

		// etaTrail = trail.getArrivalTimeAtNode(node.getName());
		// if (withLog) {
		// LOG.log(FINE, " ~~~ NEW etaTrail at node {0} :: {1}", new Object[] { node.getName(), etaTrail });
		// }
		reportInputs.logNewSlotETAisBefore(simulationTime, node, lead, trail, additionalDelay);
	}

	@Override
	public String toString() {
		return "TSS:  @"+arrivalNode.getName()+"  with ["+slotCandidateFunction+",  "+slotSeparationFunction+"]";
	}

	/*
	 * Reporting methods
	 */
	// the use of this methods does not yet optimize performances

	private Map<String, Object> initReportItemValues(final int simulationTime, ATCNode node, SlotTrajectory lead, SlotTrajectory trail, final int leadETA) {
		// map containing the values needed for the report
		Map<String, Object> reportValues = new HashMap<>();
		reportValues.put(ReportItemProperties.SIMULATION_TIME, simulationTime);
		reportValues.put(ReportItemProperties.NODE, node);
		reportValues.put(ReportItemProperties.LEAD, lead);
		reportValues.put(ReportItemProperties.TRAIL, trail);
		//
		FlightParameters leadParameters = lead.getParametersAtSimulationTime(leadETA);
		FlightParameters trailParameters = trail.getParametersAtSimulationTime(leadETA);
		String leadToWaypoint = lead.getWaypointAimedAtSimulationTime(simulationTime);
		reportValues.put(ReportItemProperties.LEAD_PARAMETERS, leadParameters);
		reportValues.put(ReportItemProperties.LEAD_SEGMENT, lead.getSlotMarker().getFlightPlan().getSegmentEndingAt(leadToWaypoint));
		String oldTrailToWaypoint = trail.getWaypointAimedAtSimulationTime(simulationTime);
		reportValues.put(ReportItemProperties.TRAIL_OLD_PARAMETERS, FlightParameters.clone(trailParameters));
		reportValues.put(ReportItemProperties.TRAIL_OLD_SEGMENT, trail.getSlotMarker().getFlightPlan().getSegmentEndingAt(oldTrailToWaypoint));
		//
		return reportValues;
	}
	// TODO reformat using unsatisfied criteria

	private void logScheduleConflictDetected(final int simulationTime, ATCNode node, SlotTrajectory lead, SlotTrajectory trail, Map<String, Object> reportValues, final int eta, final int delay) {
		FlightParameters leadParameters = lead.getParametersAtSimulationTime(eta);
		FlightParameters trailParameters = trail.getParametersAtSimulationTime(eta);

		// calculate the distance of the further slot to the airport
		// -> in some cases, the trailer can be closer to the airport
		double leadDistanceToAirport = AfoUtils.getHorizontalDistance(arrivalNode.getLatitude(), arrivalNode.getLongitude(), leadParameters.getLatitude(), leadParameters.getLongitude());
		double trailDistanceToAirport = AfoUtils.getHorizontalDistance(arrivalNode.getLatitude(), arrivalNode.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());

		// deduce the minimum separation needed: it is the maximum separation
		// needed at each slot's position
		double minSeparationReq = Math.max(CalculationTools.minumumRequiredSeparation(leadDistanceToAirport), CalculationTools.minumumRequiredSeparation(trailDistanceToAirport));
		// calculate the separation distance between the slots' centers
		final double initialHDistance = AfoUtils.getHorizontalDistance(leadParameters.getLatitude(), leadParameters.getLongitude(), trailParameters.getLatitude(), trailParameters.getLongitude());
		reportValues.put(ReportItemProperties.MIN_H_SEPARATION_REQUIRED, minSeparationReq);
		reportValues.put(ReportItemProperties.INITIAL_H_SEPARATION, initialHDistance);
		reportValues.put(ReportItemProperties.DELAY_NEEDED, delay);
		String newTrailToWaypoint = trail.getWaypointAimedAtSimulationTime(simulationTime);
		reportValues.put(ReportItemProperties.TRAIL_NEW_PARAMETERS, FlightParameters.clone(trailParameters));
		reportValues.put(ReportItemProperties.TRAIL_NEW_SEGMENT, trail.getSlotMarker().getFlightPlan().getSegmentEndingAt(newTrailToWaypoint));
		reportInputs.logScheduleConflictDetected(simulationTime, reportValues);
	}

	/*
	 * Log methods
	 */

	private void log(Level level, String message, Object[] params) {
		if (withLog) {
			LOG.log(level, message, params);
		}
	}

	private void log(Level level, String message, Object param) {
		if (withLog) {
			LOG.log(level, message, param);
		}
	}

	private void log(Level level, String message) {
		if (withLog) {
			LOG.log(level, message);
		}
	}
}
