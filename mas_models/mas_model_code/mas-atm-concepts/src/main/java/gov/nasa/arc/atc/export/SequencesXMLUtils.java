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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.DepartureInfo;

/**
 * 
 * @author ahamon
 *
 */
public class SequencesXMLUtils {

	public static final String ARRIVAL_IN_GROUP = "ARRIVALS_IN";
	public static final String ARRIVAL_IN_AT_GROUP = "ARRIVALS_IN_AT";
	public static final String ARRIVAL_OUT_GROUP = "ARRIVALS_OUT";
	public static final String ARRIVAL_OUT_AT_GROUP = "ARRIVALS_OUT_AT";

	public static final String DEPARTURES_IN_GROUP = "DEPARTURES_IN";
	public static final String DEPARTURES_IN_AT_GROUP = "DEPARTURES_IN_AT";
	public static final String DEPARTURES_OUT_GROUP = "DEPARTURES_OUT";
	public static final String DEPARTURES_OUT_AT_GROUP = "DEPARTURES_OUT_AT";

	public static final String ARRIVAL_ELEMENT = "ARRIVAL";
	public static final String DEPARTURE_ELEMENT = "DEPARTURE";

	public static final String ARRIVAL_TYPE_ATTRIBUTE = "arrivalType";
	public static final String TIME_ATTRIBUTE = "time";
	public static final String NAME_ATTRIBUTE = "arrivalName";
	public static final String RUNWAY_ATTRIBUTE = "arrivalRunway";
	public static final String ARRIVAL_TIME_ATTRIBUTE = "arrivalTime";
	public static final String CURRENT_DEPARTURE_TIME_ATTRIBUTE = "currentDepartureTime";
	public static final String ORIGINAL_DEPARTURE_TIME_ATTRIBUTE = "originalDepartureTime";

	public static final String IN = "in";
	public static final String OUT = "out";

	private SequencesXMLUtils() {
		// private utility constructor
	}

	public static void createDeparturesIN(Document doc, Element reportElement, Map<Integer, List<DepartureInfo>> allDeparturesIN) {
		Element departureInElement = doc.createElement(DEPARTURES_IN_GROUP);
		allDeparturesIN.forEach((time, list) -> createDeparturesINAtTime(doc, departureInElement, time, list));
		reportElement.appendChild(departureInElement);
	}

	public static void createDeparturesOUT(Document doc, Element reportElement, Map<Integer, List<DepartureInfo>> allDeparturesOUT) {
		Element departureOutElement = doc.createElement(DEPARTURES_OUT_GROUP);
		allDeparturesOUT.forEach((time, list) -> createDeparturesOUTAtTime(doc, departureOutElement, time, list));
		reportElement.appendChild(departureOutElement);
	}

	public static void createArrivalsIN(Document doc, Element parentElement, Map<Integer, List<ArrivalInfo>> allArrivalsIN) {
		Element arrivalInElement = doc.createElement(ARRIVAL_IN_GROUP);
		allArrivalsIN.forEach((time, list) -> createArrivalsINAtTime(doc, arrivalInElement, time, list));
		parentElement.appendChild(arrivalInElement);
	}

	public static void createArrivalsOUT(Document doc, Element parentElement, Map<Integer, List<ArrivalInfo>> allArrivalsOUT) {
		Element arrivalOUTElement = doc.createElement(ARRIVAL_OUT_GROUP);
		allArrivalsOUT.forEach((time, list) -> createArrivalsOUTAtTime(doc, arrivalOUTElement, time, list));
		parentElement.appendChild(arrivalOUTElement);
	}

	public static void createArrivalsINAtTime(Document doc, Element parentElement, int time, List<ArrivalInfo> infos) {
		Element arrivalsAtElement = doc.createElement(ARRIVAL_IN_AT_GROUP);
		arrivalsAtElement.setAttribute(TIME_ATTRIBUTE, Integer.toString(time));
		infos.forEach(info -> createArrivalInfoElement(doc, arrivalsAtElement, info));
		parentElement.appendChild(arrivalsAtElement);
	}

	public static void createArrivalsOUTAtTime(Document doc, Element parentElement, int time, List<ArrivalInfo> infos) {
		Element arrivalsAtElement = doc.createElement(ARRIVAL_OUT_AT_GROUP);
		arrivalsAtElement.setAttribute(TIME_ATTRIBUTE, Integer.toString(time));
		infos.forEach(info -> createArrivalInfoElement(doc, arrivalsAtElement, info));
		parentElement.appendChild(arrivalsAtElement);
	}

	public static void createArrivalInfoElement(Document doc, Element parentElement, ArrivalInfo info) {
		Element arrivalElement = doc.createElement(ARRIVAL_ELEMENT);
		arrivalElement.setAttribute(NAME_ATTRIBUTE, info.getName());
		arrivalElement.setAttribute(RUNWAY_ATTRIBUTE, info.getArrivalRunway());
		arrivalElement.setAttribute(ARRIVAL_TIME_ATTRIBUTE, Integer.toString(info.getArrivalTime()));
		parentElement.appendChild(arrivalElement);
	}

	public static void createDeparturesINAtTime(Document doc, Element parentElement, int time, List<DepartureInfo> infos) {
		Element departuresAtElement = doc.createElement(DEPARTURES_IN_AT_GROUP);
		departuresAtElement.setAttribute(TIME_ATTRIBUTE, Integer.toString(time));
		infos.forEach(info -> createDepartureInfoElement(doc, departuresAtElement, info));
		parentElement.appendChild(departuresAtElement);
	}

	public static void createDeparturesOUTAtTime(Document doc, Element parentElement, int time, List<DepartureInfo> infos) {
		Element departuresAtElement = doc.createElement(DEPARTURES_OUT_AT_GROUP);
		departuresAtElement.setAttribute(TIME_ATTRIBUTE, Integer.toString(time));
		infos.forEach(info -> createDepartureInfoElement(doc, departuresAtElement, info));
		parentElement.appendChild(departuresAtElement);
	}

	public static void createDepartureInfoElement(Document doc, Element parentElement, DepartureInfo info) {
		Element arrivalElement = doc.createElement(DEPARTURE_ELEMENT);
		arrivalElement.setAttribute(NAME_ATTRIBUTE, info.getName());
		arrivalElement.setAttribute(RUNWAY_ATTRIBUTE, info.getName());
		arrivalElement.setAttribute(CURRENT_DEPARTURE_TIME_ATTRIBUTE, Integer.toString(info.getCurrentDepartureTime()));
		arrivalElement.setAttribute(ORIGINAL_DEPARTURE_TIME_ATTRIBUTE, Integer.toString(info.getOriginalDepartureTime()));
		parentElement.appendChild(arrivalElement);
	}

	public static void parseDeparturesAt(Element element, Map<Integer, List<DepartureInfo>> departuresInfo) {
		final int time = Integer.parseInt(element.getAttribute(TIME_ATTRIBUTE));
		List<DepartureInfo> infos = new ArrayList<>();
		//
		NodeList departuresList = element.getElementsByTagName(DEPARTURE_ELEMENT);
		for (int i = 0; i < departuresList.getLength(); i++) {
			final Element departureElement = (Element) departuresList.item(i);
			parseSingleDepartureAt(departureElement, infos);
		}
		departuresInfo.put(time, Collections.unmodifiableList(infos));
	}

	public static void parseSingleDepartureAt(Element departureElement, List<DepartureInfo> infos) {
		String name = departureElement.getAttribute(NAME_ATTRIBUTE);
		String runwayName = departureElement.getAttribute(RUNWAY_ATTRIBUTE);
		int currentArrival = Integer.parseInt(departureElement.getAttribute(CURRENT_DEPARTURE_TIME_ATTRIBUTE));
		int originalArrival = Integer.parseInt(departureElement.getAttribute(ORIGINAL_DEPARTURE_TIME_ATTRIBUTE));
		DepartureInfo info = new DepartureInfo(name, originalArrival, currentArrival, runwayName);
		infos.add(info);
	}

	public static void parseArrivalsAt(Element element, Map<Integer, List<ArrivalInfo>> arrivalsInfo) {
		final int time = Integer.parseInt(element.getAttribute(TIME_ATTRIBUTE));
		List<ArrivalInfo> infos = new ArrayList<>();
		//
		NodeList arrivalsList = element.getElementsByTagName(ARRIVAL_ELEMENT);
		for (int i = 0; i < arrivalsList.getLength(); i++) {
			final Element arrivalElement = (Element) arrivalsList.item(i);
			parseSingleArrivalAt(arrivalElement, infos);
		}
		arrivalsInfo.put(time, Collections.unmodifiableList(infos));
	}

	public static void parseSingleArrivalAt(Element arrivalElement, List<ArrivalInfo> infos) {
		String name = arrivalElement.getAttribute(NAME_ATTRIBUTE);
		String runwayName = arrivalElement.getAttribute(RUNWAY_ATTRIBUTE);
		int arrival = Integer.parseInt(arrivalElement.getAttribute(ARRIVAL_TIME_ATTRIBUTE));
		ArrivalInfo info = new ArrivalInfo(name, arrival, runwayName);
		infos.add(info);
	}
}
