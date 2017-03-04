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

package gov.nasa.arc.atc.reports;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.reports.tss.TSSReportInputs;

/**
 * 
 * @author ahamon
 *
 */
public class TSSReporter {

	public static final String TSS_REPORT_PREFIX = "## TSS:: ";

	private static final Logger LOG = Logger.getGlobal();

	// private static boolean withBrahms = false;
	// private static boolean debug = false;

	// private static String scenarioName;
	// private static int simuDuration;
	// private static int stepDuration;
	//
	// private static int itemUniqueID=1;

	private static final List<TSSReportInputs> REPORT_INPUTS = new LinkedList<>();

	private TSSReporter() {
		// private utility constructor
	}

	public static void registerReportInputs(TSSReportInputs reportInputs) {
		LOG.log(Level.WARNING, "adding reportInputs: {0}", reportInputs);
		REPORT_INPUTS.add(reportInputs);
	}

	public static List<TSSReportInputs> getReportInputs() {
		return Collections.unmodifiableList(REPORT_INPUTS);
	}

	@Deprecated
	public static void logSimulationDuration(final int simulationDuration) {
		// simuDuration = simulationDuration;
	}

	@Deprecated
	public static void logSimulationTimeIncrement(final int timeIncrement) {
		// stepDuration = timeIncrement;
	}

	@Deprecated
	public static void logScenarioName(final String name) {
		// scenarioName = name;
	}

	@Deprecated
	public static void logNewAFO(final AFO afo, final int simulationTime) {

    }

	// public static void logAlgoInvokation(final int simulationTime) {
	// // TODO change when repo merge
	// InvocationItem item = new InvocationItem(itemUniqueID,"TSS", simulationTime);
	// itemUniqueID++;
	// addItem(simulationTime, item);
	// logMessage(" start invocation at t=" + simulationTime);
	// }
	//
	// public static void logNewSlotETAisBefore(int simulationTime, ATCNode node, SlotTrajectory leadSlot, SlotTrajectory trailSlot, int delay) {
	// InitializationConflictDetection c = new FullInitializationConflictDetection(itemUniqueID,simulationTime, node, leadSlot, trailSlot, delay);
	// itemUniqueID++;
	// addItem(simulationTime, c);
	// logMessage(c.toString());
	// }
	//
	//
	// public static void logScheduleConflictDetected(int simulationTime, Map<String, Object> values) {
	// ScheduleConflictResolution c = new FullScheduleConflictResolution(itemUniqueID,values);
	// itemUniqueID++;
	// addItem(simulationTime, c);
	// logMessage(c.toString());
	// }

	// public static void setWithBrahms(boolean withBrahms) {
	// TSSReporter.withBrahms = withBrahms;
	// }

	// public static void setLogFile(File file) {
	// logFile = file;
	// }
	//
	// public static TSSReport exportReport() {
	// return new TSSReportImpl(scenarioName, items, simuDuration, stepDuration,allArrivalsIN,allArrivalsOUT);
	// }
	//
	// public static void clearReports() {
	// items.clear();
	// allArrivalsIN.clear();
	// allArrivalsOUT.clear();
	// }

	// public static void logSequencesIn(final int simulationTime, final List<ArrivalInfo> arrivalsIN) {
	// allArrivalsIN.put(simulationTime, arrivalsIN);
	// }

	// public static void logSequencesOut(final int simulationTime, final List<ArrivalInfo> arrivalsOUT) {
	// allArrivalsOUT.put(simulationTime, arrivalsOUT);
	// }

	// public static Map<Integer, List<TSSReportItem>> getReportItems() {
	// return Collections.unmodifiableMap(items);
	// }

	// private static void addItem(int time, TSSReportItem item) {
	// if (!items.containsKey(time)) {
	// items.put(time, new ArrayList<>());
	// }
	// items.get(time).add(item);
	// }

	// private static void logMessage(String message) {
	// String outMessage = TSS_REPORT_PREFIX + message;
	// if (debug) {
	// System.out.println(outMessage);
	// }
	// if (withBrahms) {
	// // TODO
	// } else if (logFile != null) {
	//
	// }
	// }

}
