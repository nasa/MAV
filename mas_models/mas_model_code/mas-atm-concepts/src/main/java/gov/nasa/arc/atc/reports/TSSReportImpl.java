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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.atc.reports.tss.TSSReportInputs;
import javafx.util.Pair;

/**
 * 
 * @author ahamon
 *
 */
public class TSSReportImpl implements TSSReport {

	private final String scenarioName;
	private final Map<Integer, List<TSSReportItem>> reportItems;
	private final Map<Integer, List<ArrivalInfo>> arrivalsIN;
	private final Map<Integer, List<ArrivalInfo>> arrivalsOUT;
	//
	// afoName, simulationTime, delay
	private final Map<String, List<Pair<Integer, Integer>>> slotDelays;
	private final Map<Integer, Integer> delayDistribution;
	// afoName, total delay in simulation
	private final Map<String, Integer> slotDelay;
	private final int[] delays;
	private final int[] cumulativeDelays;
	private final int simulationDuration;
	private final int stepDuration;
	private int totalDelay = 0;
	private int lastInfoTime = -1;

	public TSSReportImpl(String scenario, Map<Integer, List<TSSReportItem>> items, int simuDuration, int timeStep, Map<Integer, List<ArrivalInfo>> allArrivalsIN, Map<Integer, List<ArrivalInfo>> allArrivalsOUT) {
		scenarioName = scenario;
		reportItems = Collections.unmodifiableMap(items);
		arrivalsIN = allArrivalsIN;
		arrivalsOUT = allArrivalsOUT;
		slotDelays = new HashMap<>();
		slotDelay = new HashMap<>();
		delayDistribution = new HashMap<>();
		simulationDuration = simuDuration;
		assert simulationDuration > 0;
		stepDuration = timeStep;
		delays = new int[simulationDuration + 1];
		cumulativeDelays = new int[simulationDuration + 1];
		//
		//// TODO : check integrity
		//
		initDelays();
		//
		buildReport();
	}

	public TSSReportImpl(TSSReportInputs inputs) {
		this(inputs.getScenarioName(), inputs.getItems(), inputs.getSimulationDuration(), inputs.getStepDuration(), inputs.getAllArrivalsIN(), inputs.getAllArrivalsOUT());
	}

	@Override
	public String getScenarioName() {
		return scenarioName;
	}

	@Override
	public int getNumberInvocations() {
		return reportItems.size();
	}

	@Override
	public int getTotalDelayAdded() {
		return totalDelay;
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
	public int getSlotInitialETA(String slotName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalDelayAddedToSlot(String slotName) {
		return slotDelay.get(slotName);
	}

	@Override
	public List<Pair<Integer, Integer>> getDelays(String slotName) {
		return Collections.unmodifiableList(slotDelays.get(slotName));
	}

	@Override
	public int getNbSlotInSimulation() {
		return -1;
	}

	@Override
	public Map<String, Integer> getSlotDelays() {
		return Collections.unmodifiableMap(slotDelay);
	}

	@Override
	public int getNbSlotDelayed() {
		return slotDelay.size();
	}

	@Override
	public int[] getCumulativeDelays() {
		// return Arrays.copyOf(cumulativeDelays, cumulativeDelays.length);
		return cumulativeDelays;
	}

	@Override
	public int[] getDelays() {
		return Arrays.copyOf(delays, delays.length);
	}

	@Override
	public Map<Integer, Integer> getDelayDistribution() {
		return Collections.unmodifiableMap(delayDistribution);
	}

	@Override
	public Map<Integer, List<TSSReportItem>> getReportItems() {
		return Collections.unmodifiableMap(reportItems);
	}

	@Override
	public Map<Integer, List<ArrivalInfo>> getArrivalsIN() {
		return Collections.unmodifiableMap(arrivalsIN);
	}

	@Override
	public Map<Integer, List<ArrivalInfo>> getArrivalsOUT() {
		return Collections.unmodifiableMap(arrivalsOUT);
	}

	@Override
	public int getLastInfoTime() {
		return lastInfoTime;
	}

	private void initDelays() {
		for (int i = 0; i < delays.length; i++) {
			delays[i] = 0;
			cumulativeDelays[i] = 0;
		}
	}

	private void buildReport() {
		reportItems.values().forEach(this::processItemList);
		calculateCumulativeDelays();
		calculateDelayDistribution();
		analyzeData();
	}

	private void processItemList(List<TSSReportItem> list) {
		list.forEach(item -> {
			 System.err.println("-----------");
			 System.err.println( " @@ processing TSS item : "+item);
			// increase slot delay
			increaseSlotDelay(item);
			// add delay to list of delays for slot
			addToSlotDelaysList(item);
			// increase total simulation delay
			totalDelay += item.getDelay();
			 System.err.println( " @@ totalDelay : "+totalDelay);
			 System.err.println( " @@@ item.getSimulationTime() : "+item.getSimulationTime());
			 System.err.println( " @@@ delays.length : "+ delays.length);
			delays[item.getSimulationTime()] = delays[item.getSimulationTime()] + item.getDelay();
			 System.err.println("-----------");
		});
	}

	private void increaseSlotDelay(TSSReportItem item) {
		if (!slotDelay.containsKey(item.getTrail())) {
			slotDelay.put(item.getTrail(), item.getDelay());
		} else {
			final int previousDelay = slotDelay.get(item.getTrail());
			slotDelay.put(item.getTrail(), item.getDelay() + previousDelay);
		}
	}

	private void addToSlotDelaysList(TSSReportItem item) {
		if (!slotDelays.containsKey(item.getTrail())) {
			slotDelays.put(item.getTrail(), new ArrayList<>());
		}
		slotDelays.get(item.getTrail()).add(new Pair<>(item.getSimulationTime(), item.getDelay()));
	}

	private void calculateCumulativeDelays() {
		cumulativeDelays[0] = delays[0];
		for (int i = 1; i < delays.length; i++) {
			cumulativeDelays[i] = delays[i] + cumulativeDelays[i - 1];
		}
	}

	private void calculateDelayDistribution() {
		slotDelay.forEach((name, delay) -> {
			if (delayDistribution.containsKey(delay)) {
				final int prevNumber = delayDistribution.get(delay);
				delayDistribution.put(delay, prevNumber + 1);
			} else {
				delayDistribution.put(delay, 1);
			}
		});
	}

	private void analyzeData() {
		// how to optimize this ??
		arrivalsIN.forEach((simTime, infos) -> infos.forEach(info -> lastInfoTime = Math.max(lastInfoTime, info.getArrivalTime())));
		arrivalsOUT.forEach((simTime, infos) -> infos.forEach(info -> lastInfoTime = Math.max(lastInfoTime, info.getArrivalTime())));
	}

}
