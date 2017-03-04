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

package gov.nasa.arc.atc.reports.dsas;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.airborne.DepartureSequence;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.DSASReporter;
import gov.nasa.arc.atc.reports.DepartureInfo;

/**
 * 
 * @author ahamon
 *
 */
public class DSASReportInputs {

	private static final Map<Integer, List<DepartureInfo>> allDeparturesIN = new HashMap<>();
	private static final Map<Integer, List<DepartureInfo>> allDeparturesOUT = new HashMap<>();
	private static final Map<Integer, List<ArrivalInfo>> allArrivalsIN = new HashMap<>();
	private static final Map<Integer, List<ArrivalInfo>> allArrivalsOUT = new HashMap<>();
	private final Map<Integer, Integer> allTakeOffs;
	private final Map<Integer, Integer> allLandings;

	// scenario configuration
	private String scenarioName;
	private int simulationDuration;
	private int stepDuration;

	/**
	 * Instanciates an empty {@link DSASReportInputs}
	 */
	public DSASReportInputs() {
		allTakeOffs = new HashMap<>();
		allLandings = new HashMap<>();
		DSASReporter.registerReportInputs(DSASReportInputs.this);
	}

	/*
	 * LOG Methods
	 */

	public void logSequencesInDSAS(SimulationContext context, ATCNode departureNode, ATCNode arrivalNode, final int simulationTime) {
		DepartureSequence departureSequence = context.getDepartureSequences().get(departureNode);
		ArrivalSequence arrivalSequence = context.getArrivalSequences().get(arrivalNode);
		// log departure and arrival sequences IN
		List<DepartureInfo> departureInfos = new LinkedList<>();
		departureSequence.getSimulatedTrajectories().forEach(departure -> departureInfos.add(new DepartureInfo(departure)));
		List<ArrivalInfo> arrivalInfos = new LinkedList<>();
		arrivalSequence.getSimulatedTrajectories().forEach(arrival -> arrivalInfos.add(new ArrivalInfo(arrival)));
		// DSASReporter.logSequencesIn(simulationTime, Collections.unmodifiableList(departureInfos), Collections.unmodifiableList(arrivalInfos));
		allDeparturesIN.put(simulationTime, departureInfos);
		allArrivalsIN.put(simulationTime, arrivalInfos);
	}

	public void logSequencesOutDSAS(SimulationContext context, ATCNode departureNode, ATCNode arrivalNode, final int simulationTime) {
		DepartureSequence departureSequence = context.getDepartureSequences().get(departureNode);
		ArrivalSequence arrivalSequence = context.getArrivalSequences().get(arrivalNode);
		// log departure and arrival sequences OUT
		List<DepartureInfo> departureInfos = new LinkedList<>();
		departureSequence.getSimulatedTrajectories().forEach(departure -> departureInfos.add(new DepartureInfo(departure)));
		List<ArrivalInfo> arrivalInfos = new LinkedList<>();
		arrivalSequence.getSimulatedTrajectories().forEach(arrival -> arrivalInfos.add(new ArrivalInfo(arrival)));
		// DSASReporter.logSequencesOut(simulationTime, Collections.unmodifiableList(departureInfos), Collections.unmodifiableList(arrivalInfos));
		allDeparturesOUT.put(simulationTime, departureInfos);
		allArrivalsOUT.put(simulationTime, arrivalInfos);
	}

	public void logLastTakeOffAndLanding(final int simulationTime, final int lastTakeOffTime, final int lastLandingTime) {
		allTakeOffs.put(simulationTime, lastTakeOffTime);
		allLandings.put(simulationTime, lastLandingTime);
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

	public Map<Integer, List<DepartureInfo>> getAllDeparturesIN() {
		return Collections.unmodifiableMap(allDeparturesIN);
	}

	public Map<Integer, List<DepartureInfo>> getAllDeparturesOUT() {
		return Collections.unmodifiableMap(allDeparturesOUT);
	}

	public Map<Integer, Integer> getAllTakeOffs() {
		return Collections.unmodifiableMap(allTakeOffs);
	}

	public Map<Integer, Integer> getAllLandings() {
		return Collections.unmodifiableMap(allLandings);
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

}
