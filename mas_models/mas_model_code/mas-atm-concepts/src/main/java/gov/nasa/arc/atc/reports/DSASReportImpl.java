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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.atc.reports.dsas.DSASReportInputs;

/**
 * 
 * @author ahamon
 *
 */
public class DSASReportImpl implements DSASReport {

	private final String name;
	private final int simulationDuration;
	private final int stepDuration;

	private final Map<Integer, List<DepartureInfo>> allDeparturesIN;
	private final Map<Integer, List<DepartureInfo>> allDeparturesOUT;
	private final Map<Integer, List<ArrivalInfo>> allArrivalsIN;
	private final Map<Integer, List<ArrivalInfo>> allArrivalsOUT;
	private final Map<Integer, Integer> allLastTakeOffs;
	private final Map<Integer, Integer> allLastLandings;
	private final Map<Integer, List<TSSReportItem>> tssItems;
	
	private int lastInfoTime=-1;

	/**
	 * 
	 * @param scenario
	 * @param simuDuration
	 * @param timeStep
	 * @param maps in order: allDeparturesIN, allDeparturesOUT, allArrivalsIN, allArrivalsOUT, allLastTakeOffs, allLastLandings, tssItems
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public DSASReportImpl(String scenario, int simuDuration, int timeStep, Map<Integer, ? extends Object>... maps) {
		name = scenario;
		simulationDuration = simuDuration;
		stepDuration = timeStep;
		int nbMaps = maps.length;
		assert nbMaps == 7;
		// TODO: make it safe
		allDeparturesIN = (Map<Integer, List<DepartureInfo>>) maps[0];
		allDeparturesOUT = (Map<Integer, List<DepartureInfo>>) maps[1];
		allArrivalsIN = (Map<Integer, List<ArrivalInfo>>) maps[2];
		allArrivalsOUT = (Map<Integer, List<ArrivalInfo>>) maps[3];
		allLastTakeOffs = (Map<Integer, Integer>) maps[4];
		allLastLandings = (Map<Integer, Integer>) maps[5];
		tssItems = (Map<Integer, List<TSSReportItem>>) maps[6];
		//
		analyzeData();

	}
	
	public DSASReportImpl(DSASReportInputs reportInputs) {
		name = reportInputs.getScenarioName();
		simulationDuration = reportInputs.getSimulationDuration();
		stepDuration = reportInputs.getStepDuration();
		allDeparturesIN = reportInputs.getAllDeparturesIN();
		allDeparturesOUT = reportInputs.getAllDeparturesOUT();
		allArrivalsIN = reportInputs.getAllArrivalsIN();
		allArrivalsOUT = reportInputs.getAllArrivalsOUT();
		allLastTakeOffs = reportInputs.getAllTakeOffs();
		allLastLandings = reportInputs.getAllLandings();
		//TEMP: TODO
		tssItems = new HashMap<>();
		//
		analyzeData();
	}

	@Override
	public String getScenarioName() {
		return name;
	}

	@Override
	public int getSimulationDuration() {
		return simulationDuration;
	}

	@Override
	public int getStepDuration() {
		return stepDuration;
	}

	@Override
	public Map<Integer, List<ArrivalInfo>> getAllArrivalsIN() {
		return Collections.unmodifiableMap(allArrivalsIN);
	}

	@Override
	public Map<Integer, List<ArrivalInfo>> getAllArrivalsOUT() {
		return Collections.unmodifiableMap(allArrivalsOUT);
	}

	@Override
	public Map<Integer, List<DepartureInfo>> getAllDeparturesIN() {
		return Collections.unmodifiableMap(allDeparturesIN);
	}
	
	@Override
	public Map<Integer, List<DepartureInfo>> getAllDeparturesOUT() {
		return Collections.unmodifiableMap(allDeparturesOUT);
	}

	@Override
	public Map<Integer, Integer> getAllLastLandings() {
		return Collections.unmodifiableMap(allLastLandings);
	}

	@Override
	public Map<Integer, Integer> getAllLastTakeOffs() {
		return Collections.unmodifiableMap(allLastTakeOffs);
	}

	@Override
	public Map<Integer, List<TSSReportItem>> getTssItems() {
		return Collections.unmodifiableMap(tssItems);
	}
	
	@Override
	public int getLastInfoTime() {
		return lastInfoTime;
	}

	private void analyzeData() {
		// how to optimize this ??
		allArrivalsIN.forEach((simTime,infos)->infos.forEach(info->lastInfoTime = Math.max(lastInfoTime, info.getArrivalTime())));
		allArrivalsOUT.forEach((simTime,infos)->infos.forEach(info->lastInfoTime = Math.max(lastInfoTime, info.getArrivalTime())));
		allDeparturesIN.forEach((simTime,infos)->infos.forEach(info->lastInfoTime = Math.max(lastInfoTime, info.getCurrentDepartureTime())));
		allDeparturesOUT.forEach((simTime,infos)->infos.forEach(info->lastInfoTime = Math.max(lastInfoTime, info.getCurrentDepartureTime())));
	}

}
