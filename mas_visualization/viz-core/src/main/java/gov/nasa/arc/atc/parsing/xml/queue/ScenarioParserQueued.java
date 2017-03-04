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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.arc.atc.parsing.xml.queue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;

import gov.nasa.arc.atc.utils.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.AfoUpdateFactory;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.parsing.log.ThreadedAlternateLogParser;
import gov.nasa.arc.atc.utils.SimulationProperties;

/**
 *
 * @author hamon
 */
public final class ScenarioParserQueued implements XMLParserQueued {

	private static final Logger LOG = Logger.getGlobal();

	@Override
	public DataModel parseFile(File scenarioFile, DocumentBuilder docBuilder) {
		// TODO: put it at the right place
		SimulationProperties.parseProperties(ThreadedAlternateLogParser.class.getResourceAsStream("config.properties"));
		//
		DataModel model = parseXMLScenarioDocument(scenarioFile, docBuilder);
		//
		LOG.log(Level.INFO, "XML standalone scenario parsed : {0}", model);
		return model;
	}

	private DataModel parseXMLScenarioDocument(File scenarioFile, DocumentBuilder docBuilder) {
		try {
			// DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Document doc = builder.parse(scenarioFile);
			Document doc = docBuilder.parse(scenarioFile);
			// List<SimulatedElement> elements = new ArrayList<>();
			// SortedSet<Integer> timePoints = new TreeSet<>();
			// final Map<String, Map<Integer, AfoUpdate>> allAgentUpdates = new HashMap<>();

			DataModelInput inputs = new DataModelInput();

			parseAgents(doc, inputs);
			DataModel dataModel = new DataModel(inputs);
			// TODO: integrity check
			// if (checkIntegrityOfFile(dataModel)) {
			// // return true;
			// } else {
			// // return false;
			// throw new IllegalStateException("File is NOT correct");
			// }
			return dataModel;
		} catch (SAXException | IOException ex) {// ParserConfigurationException |
			Logger.getLogger(ScenarioParserQueued.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private void parseAgents(Document doc, DataModelInput inputs) {
		NodeList agentsList = doc.getElementsByTagName(XMLConstants.AGENT_ELEMENT);
		for (int i = 0; i < agentsList.getLength(); i++) {
			final Element agentElement = (Element) agentsList.item(i);
			parseSingleAgent(agentElement, inputs);
		}
	}

	private void parseSingleAgent(Element agentElement, DataModelInput inputs) {
		final String agentNameElem = agentElement.getAttribute(XMLConstants.NAME_ATTRIBUTE);
		final Map<Integer, AfoUpdate> agentUpdates = new HashMap<>();
		NodeList updateList = agentElement.getElementsByTagName(AfoUpdateFactory.AFO_UPDATE_ELEMENT);
		for (int i = 0; i < updateList.getLength(); i++) {
			final Element updateElement = (Element) updateList.item(i);
			AfoUpdate update = AfoUpdateFactory.parseElement(updateElement);
			agentUpdates.put(update.getTimeStamp(), update);
		}
		inputs.addAgentUpdates(agentNameElem, agentUpdates);
//		if (agentNameElem.contains(XMLConstants.DEPARTURE_PREFIX)) {
//			SimulatedElement element = new NewPlane(agentNameElem, agentNameElem.substring(XMLConstants.DEPARTURE_PREFIX.length()), agentUpdates);
//			inputs.addSimulatedElement(element);
//		} else if (agentNameElem.contains(XMLConstants.SLOT_PREFIX)) {
//			SimulatedElement element = new NewSlot(agentNameElem, agentNameElem.substring(XMLConstants.SLOT_PREFIX.length()), agentUpdates);
//			inputs.addSimulatedElement(element);
//		} else {
			throw new UnsupportedOperationException(" cannot parse agent : " + agentNameElem);
//		}
	}

	// private List<Agent> parseAgents(Document doc) {
	// final List<Agent> agents = new ArrayList<>();
	// NodeList agentsList = doc.getElementsByTagName(XMLConstants.AGENT_ELEMENT);
	// for (int i = 0; i < agentsList.getLength(); i++) {
	// final Element agentElement = (Element) agentsList.item(i);
	// final String agentNameElem = agentElement.getAttribute(XMLConstants.NAME_ATTRIBUTE);
	// final Map<Integer, List<BeliefUpdate>> beliefs = parseBeliefs(agentElement);
	// final Map<Integer, List<Event>> activities = parseActivities(agentElement);
	// final Agent agent = new Agent(agentNameElem, beliefs, activities);
	// agents.add(agent);
	// }
	// return agents;
	// }
	//
	// private Map<Integer, List<BeliefUpdate>> parseBeliefs(Element doc) {
	// final Map<Integer, List<BeliefUpdate>> beliefMap = new HashMap<>();
	// final NodeList beliefList = doc.getElementsByTagName(XMLConstants.BELIEF_UPDATE_ELEMENT);
	// for (int i = 0; i < beliefList.getLength(); i++) {
	// Element beliefUpdate = (Element) beliefList.item(i);
	// final int beliefTime = Integer.parseInt(beliefUpdate.getAttribute(XMLConstants.TIME_ATTRIBUTE));
	// String beliefRefElem = beliefUpdate.getAttribute(XMLConstants.REFERENCE_ATTRIBUTE);
	// String beliefAttrElem = beliefUpdate.getAttribute(XMLConstants.ATTRIBUTE_ATTRIBUTE);
	// String beliefValElem = beliefUpdate.getAttribute(XMLConstants.VALUE_ATTRIBUTE);
	// BeliefUpdate belief = new BeliefUpdate(beliefTime, beliefRefElem, beliefAttrElem, beliefValElem);
	//
	// if (!beliefMap.containsKey(beliefTime)) {
	// beliefMap.put(beliefTime, new ArrayList<>());
	// }
	// beliefMap.get(beliefTime).add(belief);
	// }
	// return beliefMap;
	// }
	//
	// private Map<Integer, List<Event>> parseActivities(Element doc) {
	// Map<Integer, List<Event>> activitiesMap = new HashMap<>();
	// // = new ArrayList<>()
	// List<Event> activities;
	// NodeList activityList = doc.getElementsByTagName("EVENT");
	//
	// for (int i = 0; i < activityList.getLength(); i++) {
	// Element activity = (Element) activityList.item(i);
	// String startTime = activity.getAttribute("startTime");
	// String endTime = activity.getAttribute("endTime");
	//
	// Element wf = (Element) activity.getElementsByTagName("WORKFRAME").item(0);
	// Element wf2 = (Element) wf.getElementsByTagName("WORKFRAME").item(0);
	// if (wf2 != null) {
	// String wfName = wf2.getAttribute("name");
	// Element act = (Element) wf2.getElementsByTagName("ACTIVITY").item(0);
	// String actName = act.getAttribute("name");
	// String actType = act.getAttribute("type");
	// String duration = act.getAttribute("duration");
	//
	// Event ev;
	// if (actType.equals("CommunicateActivity")) {
	// List<CommunicateActivity> comms = parseComms(act);
	// ev = new Event(startTime, endTime, wfName, actName, actType, duration, comms);
	// } else {
	// ev = new Event(startTime, endTime, wfName, actName, actType, duration);
	// }
	//
	// int time = Integer.parseInt(startTime);
	// if (activitiesMap.containsKey(time)) {
	// activities = activitiesMap.get(time);
	// activities.add(ev);
	// } else {
	// activities = new ArrayList<>();
	// activities.add(ev);
	// }
	// activitiesMap.put(time, activities);
	// }
	//
	// String wfName = wf.getAttribute("name");
	// Element act = (Element) activity.getElementsByTagName("ACTIVITY").item(0);
	// String actName = act.getAttribute("name");
	// String actType = act.getAttribute("type");
	// String duration = act.getAttribute("duration");
	//
	// Event ev;
	// if (actType.equals("CommunicateActivity")) {
	// List<CommunicateActivity> comms = parseComms(act);
	// ev = new Event(startTime, endTime, wfName, actName, actType, duration, comms);
	// } else {
	// ev = new Event(startTime, endTime, wfName, actName, actType, duration);
	// }
	//
	// int time = Integer.parseInt(startTime);
	// if (activitiesMap.containsKey(time)) {
	// activities = activitiesMap.get(time);
	// activities.add(ev);
	// } else {
	// activities = new ArrayList<>();
	// activities.add(ev);
	// }
	// activitiesMap.put(time, activities);
	// }
	//
	// return activitiesMap;
	// }
	//
	// private List<CommunicateActivity> parseComms(Element doc) {
	// List<CommunicateActivity> comms = new ArrayList<>();
	// NodeList acts = doc.getElementsByTagName("MESSAGE");
	//
	// for (int i = 0; i < acts.getLength(); i++) {
	// Element node = (Element) acts.item(i);
	// String with = node.getAttribute("with");
	// String type = node.getAttribute("type");
	// String attr = node.getAttribute("objRefName") + "." + node.getAttribute("attribute");
	// String val = node.getAttribute("value");
	// comms.add(new CommunicateActivity(type, with, attr, val));
	// }
	// return comms;
	// }
	//
	// private boolean checkIntegrityOfFile(DataModel data) {
	// // List<Agent> planes = data.getAgents();
	// List<String> departures = data.getDepartureQueue();
	//
	// for (int i = 0; i < departures.size(); i++) {
	// String checkPlane = departures.get(i);
	// boolean planeMissing = true;
	// // for (int j = 0; j < planes.size(); j++) {
	// // if (checkPlane.equals(planes.get(j).getName())) {
	// // planeMissing = false;
	// // break;
	// // }
	// // }
	// if (planeMissing) {
	// // data.addError("Error: at least 1 plane in departure list doesn't exist in XML file");
	// // GUIFunctions.updateErrorList(data.getErrorOutput());
	// LOG.log(Level.SEVERE, "ATCParser:checkIntegrityOfFile - at least 1 plane in departure list doesn't exist in XML file");
	// return false;
	// }
	// }
	// return true;
	// }

}
