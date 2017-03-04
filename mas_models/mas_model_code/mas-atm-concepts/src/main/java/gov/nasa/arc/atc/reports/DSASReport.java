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

import java.util.List;
import java.util.Map;

/**
 * 
 * @author ahamon
 *
 */
public interface DSASReport extends SimulationReport {

	Map<Integer, List<ArrivalInfo>> getAllArrivalsIN();

	Map<Integer, List<ArrivalInfo>> getAllArrivalsOUT();

	Map<Integer, List<DepartureInfo>> getAllDeparturesIN();

	Map<Integer, List<DepartureInfo>> getAllDeparturesOUT();

	Map<Integer, Integer> getAllLastLandings();

	Map<Integer, Integer> getAllLastTakeOffs();

	Map<Integer, List<TSSReportItem>> getTssItems();

	// Map<Integer, List<ReportItem>> getReportItems();
	//
	// int getNumberInvocations();
	//
	// int getTotalDelayAdded();
	//
	//
	// int getSlotInitialETA(String slotName);
	//
	// int getTotalDelayAddedToSlot(String slotName);
	//
	// List<Pair<Integer, Integer>> getDelays(String slotName);
	//
	// int getNbSlotInSimulation();
	//
	// int getNbSlotDelayed();
	//
	// int[] getCumulativeDelays();
	//
	// int[] getDelays();
	//
	// Map<Integer, Integer> getDelayDistribution();

}
