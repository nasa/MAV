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

package atcGUI;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import gov.nasa.arc.atc.MainResources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import atcGUI.components.ATCVisFrame;
import atcGUI.components.GUIFunctions;
import gov.nasa.arc.atc.utils.SimulationProperties;
import gov.nasa.arc.brahms.visualization.elements.Agent;
import gov.nasa.arc.brahms.visualization.elements.BeliefUpdate;
import gov.nasa.arc.brahms.visualization.elements.CommunicateActivity;
import gov.nasa.arc.brahms.visualization.elements.Event;
import gov.nasa.arc.brahms.visualization.elements.FactUpdate;
import atcGUI.model.SimulationDataModel;

public class ATCParser {

	public static final String BRAHMSSIM = "brahmsSim";
	public static final String BRAHMSTRANSLATE = "brahmsTranslate";
	public static final String FNF = "File not Found: ";
	
	public static final String CANCELED = "canceled";
	public static final String GUIFAILED = "guiFailed";
	public static final String FILEFAILED = "fileIntegrityFailed";
	public static final String FAILED = "fileFormatFailed";
	public static final String UNKNOWNFAIL = "unknownFailure";

	private static Logger logger = Logger.getLogger("GUIManager");
	private static int result = -100;

	public static String chooseFile(SimulationDataModel data, String simFile, String formatFlag) {
		try {
			SimulationProperties.parseProperties(new FileInputStream(MainResources.class.getResource("config.properties").getPath()));
		} catch (FileNotFoundException e) {
			data.addError("config.properties file can't be found");
			GUIFunctions.updateErrorList(data.getErrorOutput());
			logger.log(Level.SEVERE, "ATCVisViewer:loadSimulationData - config.properties file can't be found");
		}
		final JFileChooser chooser = new JFileChooser();

		if (simFile.equals("SELECT")) {
			try {
				EventQueue.invokeAndWait(new Runnable() {
				    @Override
				    public void run() {
				        chooser.setDialogTitle("Select a Simulation Output File to Run");
				        result = chooser.showOpenDialog(null);
				    }
				});
			} catch (InvocationTargetException | InterruptedException e) {
				logger.log(Level.SEVERE, e.toString());
				return GUIFAILED;
			}
		}
		// checks if simFile already contains file path to sim output
		if (result == JFileChooser.APPROVE_OPTION || !simFile.equals("SELECT")) {
            File file;
            if (simFile.equals("SELECT"))
            	file = chooser.getSelectedFile();
            else
            	file = new File(simFile);
            switch (formatFlag) {
            	case ATCVisFrame.XML_OUTPUT_FLAG:
		        	Document doc = null;
		        	try {
		    			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    			doc = builder.parse(file);
		    			TreeSet<Integer> timePoints = new TreeSet<Integer>();
		        		parseDepartureQueue(doc, data);
		        		ArrayList<Agent> agents = parseAgents(doc, timePoints);
		        		data.setAgents(agents);
		        		data.setTimePoints(timePoints);
		        		if (checkIntegrityOfFile(data)) 
		        			return file.getName();
		        		else {
		        			return FILEFAILED;
		        		}
		    		} catch (IOException e) {
		    			data.addError("WARNING: Can't find the chosen file");
		    			GUIFunctions.updateErrorList(data.getErrorOutput());
		    			logger.log(Level.WARNING, "ATCParser:chooseFile - Can't find the file: " + simFile);
		    		} catch (Exception e) {
		    			data.addError("ERROR: Unable to parse " + file.getPath());
		    			GUIFunctions.updateErrorList(data.getErrorOutput());
		    			logger.log(Level.SEVERE, "ATCParser:chooseFile - Error in  parsing the file: " + simFile);
		    			return FAILED;
		    		}
            	case ATCVisFrame.BRAHMS_OUTPUT_FLAG:
            		return parseBrahmsLogFile(data, file, BRAHMSSIM);
            	case ATCVisFrame.BRAHMS_TRANSLATE_OUTPUT_FLAG:
            		return parseBrahmsLogFile(data, file, BRAHMSTRANSLATE);
            	case ATCVisFrame.TRX_OUTPUT_FLAG:
            		return parseAFCSLogFile(data, file);
            } 
		} else if (result == JFileChooser.CANCEL_OPTION)
			return CANCELED;
		else if (result == JFileChooser.ERROR_OPTION)
			return GUIFAILED;
		return FAILED;
	}

	public static Agent getAgent(List<Agent> agents, String planeName) {
		for (Agent ag : agents) {
			if (ag.getName().equals(planeName))
				return ag;
		}
		return null;
	}

	private static void parseDepartureQueue(Document doc, SimulationDataModel data) {
		NodeList departureList = doc.getElementsByTagName("DEPARTURE");
		String[] departQueue = new String[departureList.getLength()];

		for (int i = 0; i < departureList.getLength(); i++) {
			Element planeDeparture = (Element) departureList.item(i);
			String order = planeDeparture.getAttribute("order");
			String name = planeDeparture.getAttribute("plane");
			int ord = Integer.parseInt(order) - 1;
			departQueue[ord] = name;
		}
		data.addDepartureQueue(new ArrayList<String>(Arrays.asList(departQueue)));		
	}

	private static ArrayList<Agent> parseAgents(Document doc, TreeSet<Integer> timePoints) {
    	ArrayList<Agent> agents = new ArrayList<>();
    	NodeList agentsList = doc.getElementsByTagName("AGENT");
    	for (int i = 0; i < agentsList.getLength(); i++) {
    		Element agentElement = (Element) agentsList.item(i);
    		String agentNameElem = agentElement.getAttribute("name");
    		Map<Integer,List<BeliefUpdate>> beliefs = parseBeliefs(agentElement, timePoints);
    		Map<Integer,List<Event>> activities = parseActivities(agentElement, timePoints);
    		Agent agent = new Agent(agentNameElem, beliefs, activities);
    		agents.add(agent);
    	}
    	return agents;
    }

	private static Map<Integer,List<BeliefUpdate>> parseBeliefs(Element doc, TreeSet<Integer> timePoints) {
		Map<Integer, List<BeliefUpdate>> beliefMap = new HashMap<>();
    	List<BeliefUpdate> beliefs = new ArrayList<>();
    	NodeList beliefList = doc.getElementsByTagName("BELIEF_UPDATE");

    	int simTime = 0;
    	int beliefTime;
    	for (int i = 0; i < beliefList.getLength(); i++) {
    		Element beliefUpdate = (Element) beliefList.item(i);
    		beliefTime = Integer.parseInt(beliefUpdate.getAttribute("time").trim());
    		timePoints.add(beliefTime);
    		if (simTime == beliefTime) {
				String beliefRefElem = beliefUpdate.getAttribute("reference");
				String beliefAttrElem = beliefUpdate.getAttribute("attribute");
				String beliefValElem = beliefUpdate.getAttribute("value");
				BeliefUpdate belief = new BeliefUpdate(beliefTime, beliefRefElem, beliefAttrElem, beliefValElem);
				beliefs.add(belief);
    		} else {
    			beliefMap.put(simTime, beliefs);
    			simTime = beliefTime;
    			beliefs = new ArrayList<>();
    			String beliefRefElem = beliefUpdate.getAttribute("reference");
				String beliefAttrElem = beliefUpdate.getAttribute("attribute");
				String beliefValElem = beliefUpdate.getAttribute("value");
				BeliefUpdate belief = new BeliefUpdate(beliefTime, beliefRefElem, beliefAttrElem, beliefValElem);
				beliefs.add(belief);
    		}
    	}
    	beliefMap.put(simTime, beliefs);
    	return beliefMap;
    }

 	private static Map<Integer,List<Event>> parseActivities(Element doc, TreeSet<Integer> timePoints) {
		Map<Integer,List<Event>> activitiesMap = new HashMap<>();
		List<Event> activities;
		NodeList activityList = doc.getElementsByTagName("EVENT");

		for (int i = 0; i < activityList.getLength(); i++) {
			Element activity = (Element) activityList.item(i);
			String startTime = activity.getAttribute("startTime");
			String endTime = activity.getAttribute("endTime");

			Element wf = (Element) activity.getElementsByTagName("WORKFRAME").item(0);
			Element wf2 = (Element) wf.getElementsByTagName("WORKFRAME").item(0);
			if(wf2 != null) {
				String wfName = wf2.getAttribute("name");
				Element act = (Element) wf2.getElementsByTagName("ACTIVITY").item(0);
				String actName = act.getAttribute("name");
				String actType = act.getAttribute("type");
				String duration = act.getAttribute("duration");

				Event ev;
				if (actType.equals("CommunicateActivity")) {
					List<CommunicateActivity> comms = parseComms(act);
					ev = new Event(startTime, endTime, wfName, actName, actType, duration, comms);
				} else 
					ev = new Event(startTime, endTime, wfName, actName, actType, duration);
				
				int time = Integer.parseInt(startTime);
				if (activitiesMap.containsKey(time)) {
					activities = activitiesMap.get(time);
					activities.add(ev);
				} else {
					activities = new ArrayList<>();
					activities.add(ev);
				}
				activitiesMap.put(time, activities);
			}

			String wfName = wf.getAttribute("name");
			Element act = (Element) activity.getElementsByTagName("ACTIVITY").item(0);
			String actName = act.getAttribute("name");
			String actType = act.getAttribute("type");
			String duration = act.getAttribute("duration");

			Event ev;
			if (actType.equals("CommunicateActivity")) {
				List<CommunicateActivity> comms = parseComms(act);
				ev = new Event(startTime, endTime, wfName, actName, actType, duration, comms);
			} else 
				ev = new Event(startTime, endTime, wfName, actName, actType, duration);
			
			int time = Integer.parseInt(startTime.trim());
			timePoints.add(time);
			if (activitiesMap.containsKey(time)) {
				activities = activitiesMap.get(time);
				activities.add(ev);
			} else {
				activities = new ArrayList<Event>();
				activities.add(ev);
			}
			activitiesMap.put(time, activities);
		}
		return activitiesMap;
    }

 	private static List<CommunicateActivity> parseComms(Element doc) {
		List<CommunicateActivity> comms = new ArrayList<>();
		NodeList acts = doc.getElementsByTagName("MESSAGE");

		for (int i = 0; i < acts.getLength(); i++) {
			Element node = (Element) acts.item(i);
			String with = node.getAttribute("with");
			String type = node.getAttribute("type");
			String attr = node.getAttribute("objRefName") + "." + node.getAttribute("attribute");
			String val = node.getAttribute("value");
			comms.add(new CommunicateActivity(type, with, attr, val));
		}
		return comms;
	}
 	
 	private static boolean checkIntegrityOfFile(SimulationDataModel data) {
 		List<Agent> planes = data.getAgents();
 		List<String> departures = data.getDepartureQueue();
 
 		for (int i = 0; i < departures.size(); i++) {
 			String checkPlane = departures.get(i);
 			boolean planeMissing = true;
 			for (int j = 0; j < planes.size(); j++) {
 				if (checkPlane.equals(planes.get(j).getName())) {
 					planeMissing = false;
 					break;
 				}
 			}
 			if (planeMissing) {
 				String error = "Error: at least 1 plane in departure list doesn't exist in XML file";
 				data.addError(error);
 				JOptionPane.showMessageDialog(null, error);
 				logger.log(Level.SEVERE, "ATCParser:checkIntegrityOfFile - at least 1 plane in departure list doesn't exist in XML file");
 				return false;
 			}
 		}
 		return true;
 	}
	
	public static String parseBrahmsLogFile(SimulationDataModel data, File file, String logOutputFormat) {
		Map<String, Agent> agentMap = new HashMap<>();
    	List<Agent> agents = new ArrayList<>();
    	Map<Integer, List<String[]>> separationViolators = new HashMap<>();
    	try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String curLine;
			boolean start = false;
			TreeSet<Integer> timePoints = new TreeSet<>(); // only add time points when a plane or slot gets updated
			Integer curSimTime = 0;
			boolean failedParseTime = false;
			while ((curLine = br.readLine()) != null) {
				if (curLine.contains("Starting virtual machine") ||
						curLine.contains("**!**!**!**ATC**!**!**!**")) {
					start = true; 
					continue;
				}
				if (curLine.contains("Stopping virtual machine") ||
						curLine.contains("**!**!**!**ATCSimulation Compete"))
					break;
				if (start) {
					switch (logOutputFormat) {
						case BRAHMSSIM:
							curSimTime = parseBrahmsSim(br, curLine, curSimTime, agentMap, data, timePoints, separationViolators);
							break;
						case BRAHMSTRANSLATE:
							curSimTime = parseBrahmsTranslate(br, curLine, curSimTime, agentMap, data, timePoints, separationViolators, failedParseTime);
							break;
					}
				}
			}
			
			Iterator<Entry<String, Agent>> itr = agentMap.entrySet().iterator();			
			if (BRAHMSSIM.equals(logOutputFormat)) {
				while (itr.hasNext()) {
					Agent agent = itr.next().getValue();
					String name = agent.getName();
					int pos = name.lastIndexOf('.');
					name = name.substring(pos + 1); // remove package structure from name before adding it to list of agents
					agent.setName(name);
					agents.add(agent); // add agents from agentMap to agents list
				}
			} else if (BRAHMSTRANSLATE.equals(logOutputFormat)) {
				while (itr.hasNext())
					agents.add(itr.next().getValue()); // add agents from agentMap to agents list
			}

			data.setAgents(agents);
			data.setTimePoints(timePoints);
			data.setSeparationViolators(separationViolators);
			data.addDepartureQueue(new ArrayList<String>());
			return file.getName();
		} catch (FileNotFoundException e) {
			String error = FNF + file.getAbsolutePath();
			data.addError(error);
			JOptionPane.showMessageDialog(null, error);
			logger.log(Level.WARNING, "ATCParser:parseLogFile - File not found: " + file.getAbsolutePath());
		} catch (IOException e) {
			String error = "Error reading in file: " + file.getAbsolutePath();
			data.addError(error);
			JOptionPane.showMessageDialog(null, error);
			logger.log(Level.SEVERE, "ATCParser:parseLogFile - Error reading in file: " + file.getAbsolutePath());
		}
		return "FAILED";
	}
	
	public static Integer parseBrahmsSim(BufferedReader br, String curLine, Integer curSimTime, Map<String, Agent> agentMap, SimulationDataModel data,
			SortedSet<Integer> timePoints, Map<Integer, List<String[]>> separationViolators) throws IOException {
		String[] line = curLine.split("\\s+");
		// initialize all agents here
		if (curLine.contains("Starting engine for")) {
			if (curLine.contains(".agents.ZNY_") || curLine.contains(".agents.plane_") || curLine.contains(".agents.slot_")) {
				int pos = curLine.indexOf("'");
				String agentName = curLine.substring(pos + 1, curLine.length() - 1);
				Agent agent = new Agent(agentName);
				List<BeliefUpdate> bu = new ArrayList<>();
				if (curLine.contains(".agents.ZNY_")) {
					// give belief time 0 for minimum separation distance
					String attribute = "";
					try {
						Properties prop = new Properties();
						prop.load(new FileInputStream(data.getCurDir() + "config.properties"));
						attribute = prop.getProperty("minimumSeparation");
					} catch (FileNotFoundException e) {
						attribute = "minimum_separation";
					}
					bu.add(new BeliefUpdate(0, "current", attribute, "8"));
				}
				agent.updateBeliefs(0, bu);
				agentMap.put(agentName, agent);
			}
		}
		// update simulation time variable here
		else if (curLine.contains("gov.nasa.arc.atm.atmmodel.objects.globalClock.time = ")) {
			String simTime = line[7].substring(0, line[7].length() - 1);
			curSimTime = Integer.parseInt(simTime);
		}
		// update agents here; only care to update planes and slots
		else if (curLine.contains("     [exec] --gov.nasa.arc.atm.atmmodel.scenarios.")) {
			int pos = line[2].indexOf('(');
			String agentName = line[2].substring(2, pos);

			curLine = br.readLine();
			line = curLine.split("\\s+");
			String afoValue = line[3];
			if (!agentName.contains(afoValue))
				JOptionPane.showMessageDialog(null, "Mismatch between agent name and AFO value at time " + curSimTime);
			
			Agent agent = agentMap.get(agentName);
			List<BeliefUpdate> beliefs = new ArrayList<>();
			parseBeliefs(br, curLine, line, curSimTime, beliefs, false, agentMap, "");						
			
			try {
				agent.updateBeliefs(curSimTime, beliefs);
				agentMap.put(agentName, agent);
				timePoints.add(curSimTime);
				if (agent.getName().contains("plane")) {
					initializeDepartureAttributes(beliefs, agent);
				}
			} catch (Exception e) {
				String error = "Error updating agent belief from log file for agent " + agentName + " at simulation time " + curSimTime + "\nThe agent's name may be misspelled\n";
				error += "Parsing is continuing without that belief update for the agent";
				JOptionPane.showMessageDialog(null, error);
			}
		}
		// parse separation violator info
		else if (curLine.contains("SeparationViolation")) {
			int time = parseSeparationViolators(br, separationViolators);
			timePoints.add(time);
		}
		// update slot marker eta (due to TSS scheduler)
		else if (curLine.contains(".topArrivalETA = "))
			parseTSS(br, curSimTime, agentMap, timePoints);
		return curSimTime;
	}
	
	public static Integer parseBrahmsTranslate(BufferedReader br, String curLine, Integer curSimTime, Map<String, Agent> agentMap, SimulationDataModel data,
			SortedSet<Integer> timePoints, Map<Integer, List<String[]>> separationViolators, boolean failedParseTime) throws IOException {
		String[] line = curLine.split("\\s+");
		// update simulation time variable here
		if (curLine.contains("globalClock : ( current.time  = ")) {
			try {
				curSimTime = Integer.parseInt(line[12]) + 1; // adding 1 because of the order of updates being printed
			} catch (NumberFormatException e) {
				failedParseTime = true;
				String error = "Misprinted line in output file:\n" + curLine;
				JOptionPane.showMessageDialog(null, error);
			}
		}

		// create and update agents here; only care to update planes and slots
		else if (curLine.contains("     [java] --plane_") || curLine.contains("     [java] --slot_")) {
			int pos = line[2].indexOf('(');
			String agentName = line[2].substring(2, pos);
			
			if (failedParseTime) {
				failedParseTime = false;
				int index = line[2].indexOf(')');
				curSimTime = Integer.parseInt(line[2].substring(pos + 1, index));
			}

			curLine = br.readLine();
			line = curLine.split("\\s+");
			if (!agentName.contains(line[3]))
				JOptionPane.showMessageDialog(null, "Mismatch between agent name and AFO value at time " + curSimTime);
			
			Agent agent;
			if(agentMap.containsKey(agentName))
				agent = agentMap.get(agentName);
			else {
				List<BeliefUpdate> bu = new ArrayList<>();
				agent = new Agent(agentName);
				agent.updateBeliefs(0, bu);
			}

			List<BeliefUpdate> beliefs = new ArrayList<>();
			String filePath = data.getCurDir() + "config.properties";
			parseBeliefs(br, curLine, line, curSimTime, beliefs, true, agentMap, filePath);
			
			try {
				agent.updateBeliefs(curSimTime, beliefs);
				agentMap.put(agentName, agent);
				timePoints.add(curSimTime);
				if (agent.getName().contains("plane")) {
					initializeDepartureAttributes(beliefs, agent);
				}
			} catch (Exception e) {
				String error = "Error updating agent belief from log file for agent " + agentName + " at simulation time " + curSimTime + "\nThe agent's name may be misspelled\n";
				error += "Parsing is continuing without that belief update for the agent";
				JOptionPane.showMessageDialog(null, error);
			}
		}
		// parse separation violator info
		else if (curLine.contains("SeparationViolation")) {
			int time = parseSeparationViolators(br, separationViolators);
			timePoints.add(time);
		}
		// update slot marker eta (due to TSS scheduler)
		else if (curLine.contains(".topArrivalETA = ")) 
			parseTSS(br, curSimTime, agentMap, timePoints);
		
		return curSimTime;
	}
	
	private static void initializeDepartureAttributes(List<BeliefUpdate> beliefs, Agent agent) {
		List<BeliefUpdate> initialBeliefs = agent.getBeliefs().get(0);
		boolean startTime = false;
		boolean departure = false;
		for (int i = 0; i < initialBeliefs.size(); i++) {
			BeliefUpdate bu = initialBeliefs.get(i);
			if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.DEPART_PPTY)))
				departure = true;
			else if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.STARTED_TIME_PPTY)))
				startTime = true;
		}
		if (startTime && departure)
			return;
		for (int i = 0; i < beliefs.size(); i++) {
			BeliefUpdate bu = beliefs.get(i);
			if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.DEPART_PPTY)) && bu.getValue().equals("true")) {
				ArrayList<BeliefUpdate> bl = new ArrayList<>();
				bl.add(new BeliefUpdate(0, bu.getReference(), bu.getAttribute(), bu.getValue()));
				agent.updateBeliefs(0, bl);
			} else if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.STARTED_TIME_PPTY))) {
				ArrayList<BeliefUpdate> bl = new ArrayList<>();
				bl.add(new BeliefUpdate(0, bu.getReference(), bu.getAttribute(), bu.getValue()));
				agent.updateBeliefs(0, bl);
			}									
		}
	}
 	
 	public static void parseBeliefs(BufferedReader br, String curLine, String[] line, int curSimTime, 
 			List<BeliefUpdate> beliefs, boolean brahmsTranslate, Map<String, Agent> agentMap, String filePath) throws IOException {
 		String reference = "current";
 		Agent agent;

 		// Attribute order: m_iAirspeed, m_iVerticalSpeed, m_dLatitude, m_dLongitude, m_dAltitude, m_dBearing, m_headingEnum
 		List<String> attributes = Arrays.asList("m_iAirSpeed", "m_iVerticalSpeed", "m_dLatitude", "m_dLongitude", "m_dAltitude", "m_dBearing", "m_headingEnum");
		for (int i = 0; i < 7; i++) {
			curLine = br.readLine();
			line = curLine.split("\\s+");
			String attribute = "";
			String value = "";
			try {
				attribute = line[2];
				if (!attributes.contains(attribute))
					throw new Exception();
				value = line[3];
			} catch (Exception e) {
				String error = "Misprinted line in output file while parsing Agent's belief update at simulation time " + 
									curSimTime + ":\n" + curLine + "\nThis belief update will be skipped";
				JOptionPane.showMessageDialog(null, error);
				return;
			}
			beliefs.add(new BeliefUpdate(curSimTime, reference, attribute, value));
		}

		// Attributes skipping: flightPlan, iCurrentSegment, toWaypoint, iStatus, m_iTimeStamp
		for (int i = 0; i < 5; i++)
			curLine = br.readLine();

		// Attribute order: startTime, is_departure, ETA, controller, is_Metering
		attributes = Arrays.asList("startTime", "is_departure", "ETA", "controller", "is_Metering");
		for (int i = 0; i < 5; i++) {
			curLine = br.readLine();
			line = curLine.split("\\s+");
			String attribute = "";
			String value = "";
			try {
				attribute = line[2];
				if (!attributes.contains(attribute))
					throw new Exception();
				value = line[3];
			} catch (Exception e) {
				String error = "Misprinted line in output file while parsing Agent's belief update at simulation time " +
									curSimTime + ":\n" + curLine + "\nThis belief update will be skipped";
				JOptionPane.showMessageDialog(null, error);
				return;
			}
			if (i == 3) {
				if (brahmsTranslate) {
					if (!agentMap.containsKey(value)) { // create controller agent
						String minSep = "";
						try {
							Properties prop = new Properties();
							prop.load(new FileInputStream(filePath));
							minSep = prop.getProperty(SimulationProperties.MINIMUM_SEPARATION_PPTY);
						} catch (FileNotFoundException e) {
							minSep = "minimum_separation";
						}
						agent = new Agent(value);
						List<BeliefUpdate> bu = new ArrayList<>();
						bu.add(new BeliefUpdate(0, reference, minSep, "8"));
						agent.updateBeliefs(0, bu);
						agentMap.put(value, agent);
					}
				} else {
					int pos = line[3].lastIndexOf(".");
					value = line[3].substring(pos + 1);
				}
			}
			beliefs.add(new BeliefUpdate(curSimTime, reference, attribute, value));
		}
 	}
 	
 	public static void parseTSS(BufferedReader br, int curSimTime, Map<String, Agent> agentMap, SortedSet<Integer> timePoints) throws IOException {
 		String curLine = br.readLine();
 		String[] line = curLine.split("\\s+");
		if (line.length != 8) {
			curLine = br.readLine();
			line = curLine.split("\\s+");
			if (line.length != 8)
				return;
		}
		try {
			line[5] = line[5].substring(0, line[5].length() - 4); // removing .ETA from end of string
			int pos = line[5].lastIndexOf(".");
			String agentName = line[5].substring(pos + 1);
			Agent agent = agentMap.get(agentName);
			List<BeliefUpdate> beliefs = new ArrayList<>();
			String reference = "current";
			String attribute = "ETA";
			String value = line[7].substring(0, line[7].length() - 1);
			
			beliefs.add(new BeliefUpdate(curSimTime, reference, attribute, value));
			agent.updateBeliefs(curSimTime, beliefs);
			agentMap.put(agentName, agent);
			timePoints.add(curSimTime);
		} catch (Exception e) {
			return;
		}
 	}

 	public static Integer parseSeparationViolators(BufferedReader br, Map<Integer, List<String[]>> allSepViolators) throws IOException {
 		String[] retList = new String[4];
 		for (int i = 0; i < 4; i++) {
 			String[] line = br.readLine().trim().split("\\s+");
 			retList[i] = line[2];
 		}
 		
 		int time = Integer.parseInt(retList[2]);
 		List<String[]> violators = allSepViolators.get(time);
 		if (violators != null) {
 			violators.add(retList);
 			allSepViolators.put(time, violators);
 		} else {
 			violators = new ArrayList<>();
 			violators.add(retList);
 			allSepViolators.put(time, violators);
 		}
 		
 		return time;
 	}
 	
 	@SuppressWarnings("resource")
	public static String parseAFCSLogFile(SimulationDataModel data, File file) {
    	List<Agent> agents = new ArrayList<>();
    	try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String curLine;
			boolean start = false;
//			boolean justOnce = true;
//			String prevTimePoint = "";
			int prevTimePoint = 0;
			TreeSet<Integer> timePoints = new TreeSet<>();
			timePoints.add(0);
			while ((curLine = br.readLine()) != null) {
				String[] line = curLine.split("\\s+");
				if(curLine.contains("Virtual machine started")) {
					start = true; 
					continue;
				}
				if (curLine.contains("Stopping virtual machine")) {
					timePoints.add(prevTimePoint);
					break;
				}
				if (start) {
//					if(justOnce) {
//						timePoints.add(line[0]);	// adds initial time point of simulation running
//						justOnce = false;
//					}
					// create agents here; update them here
					if (curLine.contains("Airspeed") || curLine.contains("Altitude") || curLine.contains("Latitude") || curLine.contains("Runway Takeoff")) {
						int time = Integer.parseInt(line[0]);
						ArrayList<BeliefUpdate> beliefUpdates = new ArrayList<>();
						String ref = "current";
						switch (line[8]) {
							case "Airspeed:":
								beliefUpdates.add(new BeliefUpdate(time, ref, "speed", line[9].substring(0, line[9].length() - 1)));
								break;
							case "Altitude:":
								beliefUpdates.add(new BeliefUpdate(time, ref, "altitude", line[9]));
								break;
							case "Latitude:":
								beliefUpdates.add(new BeliefUpdate(time, ref, "latitude", line[9].substring(0, line[9].length() - 1)));
								beliefUpdates.add(new BeliefUpdate(time, ref, "longitude", line[11]));
								break;
							default: // runway takeoff
								beliefUpdates.add(new BeliefUpdate(time, ref, "flying", "true"));
								beliefUpdates.add(new BeliefUpdate(time, ref, "startTime", line[0]));
								beliefUpdates.add(new BeliefUpdate(time, ref, "currentController", "ZNY_default"));
								break;
						}
						String planeName = line[2].substring(59, line[2].length() - 1);
						Agent ag = getAgent(agents, "plane_" + planeName);
						if (ag != null) {
							if(ag.getBeliefs().get(0).size() < 7)
								ag.updateBeliefs(0, beliefUpdates);
							else
								ag.updateBeliefs(time, beliefUpdates);
						} else {
							Map<Integer,List<BeliefUpdate>> beliefs = new HashMap<>();
							beliefs.put(0, beliefUpdates);
							ag = new Agent("plane_" + planeName, beliefs, null);
							agents.add(ag);
						}
					}
				}
				prevTimePoint = Integer.parseInt(line[0].trim());
			}
			Integer beg = timePoints.first();
			Integer end = timePoints.last();
			timePoints.remove(1);
			for(Integer i = beg + 1; i <= end; i++) 
				timePoints.add(i);
			data.setTimePoints(timePoints);

			Agent contAg = new Agent("ZNY_default", null, null);	// creating single default controller that all planes belong to
			agents.add(contAg);

			data.setAgents(agents);
			data.addDepartureQueue(new ArrayList<String>());
			return file.getName();
		} catch (FileNotFoundException e) {
			String error = FNF + file.getAbsolutePath();
			data.addError(error);
			JOptionPane.showMessageDialog(null, error);
			logger.log(Level.WARNING, "ATCParser:parseLogFile - File not found: " + file.getAbsolutePath());
		} catch (IOException e) {
			String error = "Error reading in file: " + file.getAbsolutePath();
			data.addError(error);
			JOptionPane.showMessageDialog(null, error);
			logger.log(Level.SEVERE, "ATCParser:parseLogFile - Error reading in file: " + file.getAbsolutePath());
		}
    	return "FAILED";
 	}

 	@SuppressWarnings("unused")
	private static Map<Integer, List<FactUpdate>> parseFacts(Document doc) {
		Map<Integer, List<FactUpdate>> factMap = new HashMap<>();
    	List<FactUpdate> facts = new ArrayList<>();
    	NodeList factsList = doc.getElementsByTagName("FACT_UPDATE");

//    	HashMap<String, FlightSegment> flightSegs = new HashMap<String, FlightSegment>();
//    	String fromWaypoint = "";
//    	String toWaypoint = "";
//    	double distBtwnPoints = 0.0;
//    	String name = "";
//    	for(int i = 0; i < factsList.getLength(); i++) {
//    		Element factElement = (Element) factsList.item(i);
//    		
//    		int factTime = Integer.parseInt(factElement.getAttribute("time"));
//    		if(factTime != 0)
//    			break;
//    		
//    		if(factElement.getAttribute("attribute").equals("fromWaypoint")) {
//    			name = factElement.getAttribute("reference");
//    			fromWaypoint = factElement.getAttribute("value");
//    		}
//    		if(factElement.getAttribute("attribute").equals("toWaypoint"))
//    			toWaypoint = factElement.getAttribute("value");
//    		if(factElement.getAttribute("attribute").equals("dist_btwn_waypoints"))
//    			distBtwnPoints = Double.parseDouble(factElement.getAttribute("value"));
//    		if(!fromWaypoint.equals("") && !toWaypoint.equals("") && !name.equals("") && distBtwnPoints != 0.0) {
//    			flightSegs.put(name, new FlightSegment(name, fromWaypoint, toWaypoint, distBtwnPoints));
//    		}
//    	}

    	int simTime = 0;
    	int factTime;
    	int index = 0;
    	for (int i = 0; i < factsList.getLength(); i++) {
    		Element factElement = (Element) factsList.item(i);

    		factTime = Integer.parseInt(factElement.getAttribute("time"));
    		if (simTime == factTime) {
        		String factRefElem = factElement.getAttribute("reference");
        		String factAttrElem = factElement.getAttribute("attribute");
        		String factValElem = factElement.getAttribute("value");

        		FactUpdate fact = new FactUpdate(factTime, factRefElem, factAttrElem, factValElem);
        		facts.add(fact);
    		} else {
    			factMap.put(index, facts);
    			index++;
    			simTime = factTime;
    			facts = new ArrayList<>();

    			String beliefRefElem = factElement.getAttribute("reference");
				String beliefAttrElem = factElement.getAttribute("attribute");
				String beliefValElem = factElement.getAttribute("value");
				FactUpdate belief = new FactUpdate(factTime, beliefRefElem, beliefAttrElem, beliefValElem);
				facts.add(belief);
    		}
    	}
    	factMap.put(index, facts);
    	return factMap;
    }

 	@SuppressWarnings("unused")
	private static boolean parseTimePoints(Document doc, SimulationDataModel data) {
		NodeList beliefList = doc.getElementsByTagName("TIME_STEPS");
		Element beliefUpdate = (Element) beliefList.item(0);
		String[] timePoints = beliefUpdate.getAttribute("steps").split(",\\s");
		if(timePoints.length == 1)	// time_steps might not have spaces in between entries
			timePoints = beliefUpdate.getAttribute("steps").split(",");
		data.setTimePoints(new ArrayList<String>(Arrays.asList(timePoints)));
		if(timePoints.length <= 1) {
			data.addError("Error parsing TIME_STEPS in XML file: no time points listed");
			GUIFunctions.updateErrorList(data.getErrorOutput());
			logger.log(Level.SEVERE, "ATCParser:parseTimePoints - Error in parsing TIME_STEPS in XML file: no time points listed");
			return false;
		}
		return true;
	}
}
