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

package gov.nasa.arc.atc.reports.tss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.InvocationItem;
import gov.nasa.arc.atc.reports.TSSReportItem;
import gov.nasa.arc.atc.reports.TSSReporter;
import gov.nasa.arc.atc.simulation.SlotTrajectory;

/**
 * 
 * @author ahamon
 *
 */
public class TSSReportInputs {

	private final Map<Integer, List<TSSReportItem>> items;
	//
	private final Map<Integer, List<ArrivalInfo>> allArrivalsIN;
	private final Map<Integer, List<ArrivalInfo>> allArrivalsOUT;

	// scenario configuration
	private String scenarioName;
	private int simulationDuration;
	private int stepDuration;

	private int itemUniqueID = 1;

	/**
	 * Instanciates an empty {@link TSSReportInputs}
	 * 
	 * @param standalone if the TSS reported is a standalone one
	 */
	public TSSReportInputs(boolean standalone) {
		items = new HashMap<>();
		allArrivalsIN = new HashMap<>();
		allArrivalsOUT = new HashMap<>();
		if (standalone) {
			TSSReporter.registerReportInputs(TSSReportInputs.this);
		}
	}

	// protected methods??

	public void logSequencesInTSS(SimulationContext context, ATCNode arrivalNode, final int simulationTime) {
		// System.err.println("AlgoUtils :: context " + context);
		// System.err.println("AlgoUtils :: arrivalNode " + arrivalNode);
		// System.err.println("AlgoUtils :: simulationTime " + simulationTime);
		ArrivalSequence arrivalSequence = context.getArrivalSequences().get(arrivalNode);
		// log departure sequence IN
		List<ArrivalInfo> arrivalInfos = new LinkedList<>();
		arrivalSequence.getSimulatedTrajectories().forEach(arrival -> arrivalInfos.add(new ArrivalInfo(arrival)));
		allArrivalsIN.put(simulationTime, Collections.unmodifiableList(arrivalInfos));
	}

	public void logSequencesOutTSS(SimulationContext context, ATCNode arrivalNode, final int simulationTime) {
		ArrivalSequence arrivalSequence = context.getArrivalSequences().get(arrivalNode);
		// log departure sequence OUT
		List<ArrivalInfo> arrivalInfos = new LinkedList<>();
		arrivalSequence.getSimulatedTrajectories().forEach(arrival -> arrivalInfos.add(new ArrivalInfo(arrival)));
		allArrivalsOUT.put(simulationTime, Collections.unmodifiableList(arrivalInfos));
	}

	public void logInvocationDuration(final int simulationTime, final long duration) {
		// if (duration > 4) {
		// System.err.println(" t=" + simulationTime + " TSS dur=" + duration + " ms");
		// }
	}

	public void logAlgoInvokation(final int simulationTime) {
		// TODO change when repo merge
		InvocationItem item = new InvocationItem(itemUniqueID, "TSS", simulationTime);
		itemUniqueID++;
		addItem(simulationTime, item);
		logMessage(" start invocation at t=" + simulationTime);
	}

	public void logNewSlotETAisBefore(int simulationTime, ATCNode node, SlotTrajectory leadSlot, SlotTrajectory trailSlot, int delay) {
		InitializationConflictDetection c = new FullInitializationConflictDetection(itemUniqueID, simulationTime, node, leadSlot, trailSlot, delay);
		itemUniqueID++;
		addItem(simulationTime, c);
		logMessage(c.toString());
	}

	public void logScheduleConflictDetected(int simulationTime, Map<String, Object> values) {
		ScheduleConflictResolution c = new FullScheduleConflictResolution(itemUniqueID, values);
		itemUniqueID++;
		addItem(simulationTime, c);
		logMessage(c.toString());
	}

	/*
	 * GETTERS
	 */

	public Map<Integer, List<ArrivalInfo>> getAllArrivalsIN() {
		return Collections.unmodifiableMap(allArrivalsIN);
	}

	public Map<Integer, List<ArrivalInfo>> getAllArrivalsOUT() {
		return Collections.unmodifiableMap(allArrivalsOUT);
	}

	public Map<Integer, List<TSSReportItem>> getItems() {
		return Collections.unmodifiableMap(items);
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public int getSimulationDuration() {
		return simulationDuration;
	}

	public int getStepDuration() {
		return stepDuration;
	}

	/*
	 * SETTERS
	 */

	public void setScenarioName(String name) {
		scenarioName = name;
	}

	public void setSimulationDuration(int simDur) {
		simulationDuration = simDur;
	}

	public void setStepDuration(int stepDur) {
		stepDuration = stepDur;
	}

	/*
	 * Private methods
	 */

	private void addItem(int time, TSSReportItem item) {
		if (!items.containsKey(time)) {
			items.put(time, new ArrayList<>());
		}
		items.get(time).add(item);
	}

	private static void logMessage(String message) {
		// String outMessage = TSS_REPORT_PREFIX + message;
		// if (debug) {
		// System.out.println(outMessage);
		// }
		// if (withBrahms) {
		// // TODO
		// } else if (logFile != null) {
		//
		// }
	}

}
