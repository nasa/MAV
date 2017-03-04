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

package gov.nasa.arc.atc.export;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.DSASReport;
import gov.nasa.arc.atc.reports.DSASReportImpl;
import gov.nasa.arc.atc.reports.DepartureInfo;

/**
 * 
 * @author ahamon
 *
 */
public class DSASReportParser {
	
	private DSASReportParser(){
		// private utility constructor
	}

	public static DSASReport parseReport(Element reportElement) {
		Map<Integer, List<DepartureInfo>> departuresInInfo = new HashMap<>();
		Map<Integer, List<DepartureInfo>> departuresOutInfo = new HashMap<>();
		Map<Integer, List<ArrivalInfo>> arrivalsInInfo = new HashMap<>();
		Map<Integer, List<ArrivalInfo>> arrivalsOutInfo = new HashMap<>();
		//
		String scenarioName = reportElement.getAttribute(ReportXMLExport.SCENARIO_ATTRIBUTE);
		int nbSteps = Integer.parseInt(reportElement.getAttribute(ReportXMLExport.NB_STEPS_ATTRIBUTE));
		int stepDuration = Integer.parseInt(reportElement.getAttribute(ReportXMLExport.STEP_DURATION_ATTRIBUTE));
		//
		NodeList departuresINList = reportElement.getElementsByTagName(SequencesXMLUtils.DEPARTURES_IN_AT_GROUP);
		for (int i = 0; i < departuresINList.getLength(); i++) {
			Element departuresInAt = (Element) departuresINList.item(i);
			SequencesXMLUtils.parseDeparturesAt(departuresInAt, departuresInInfo);
		}  
		//    
		NodeList departuresOUTList = reportElement.getElementsByTagName(SequencesXMLUtils.DEPARTURES_OUT_AT_GROUP);
		for (int i = 0; i < departuresOUTList.getLength(); i++) {
			Element departuresOutAt = (Element) departuresOUTList.item(i);
			SequencesXMLUtils.parseDeparturesAt(departuresOutAt, departuresOutInfo);
		}
		//
		NodeList arrivalsINList = reportElement.getElementsByTagName(SequencesXMLUtils.ARRIVAL_IN_AT_GROUP);
		for (int i = 0; i < arrivalsINList.getLength(); i++) {
			Element arrivalsInAt = (Element) arrivalsINList.item(i);
			SequencesXMLUtils.parseArrivalsAt(arrivalsInAt, arrivalsInInfo);
		}
		//
		NodeList arrivalsOUTList = reportElement.getElementsByTagName(SequencesXMLUtils.ARRIVAL_OUT_AT_GROUP);
		for (int i = 0; i < arrivalsOUTList.getLength(); i++) {
			Element arrivalsOutAt = (Element) arrivalsOUTList.item(i);
			SequencesXMLUtils.parseArrivalsAt(arrivalsOutAt, arrivalsOutInfo);
		}
		//TODO add missing ones
		return new DSASReportImpl(scenarioName, stepDuration, nbSteps, departuresInInfo, departuresOutInfo, arrivalsInInfo, arrivalsOutInfo, new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	

}
