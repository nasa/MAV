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

package atcGUI.model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import atcGUI.components.GUIFunctions;
import atcGUI.control_view.DrawUtils;
import gov.nasa.arc.brahms.visualization.elements.Agent;
import gov.nasa.arc.brahms.visualization.elements.Plane;
import gov.nasa.arc.brahms.visualization.elements.Slot;
import gov.nasa.arc.brahms.visualization.elements.Waypoint;

public class SimulationDataModel extends Observable {

	private List<Waypoint> waypoints;
	private List<Agent> agents;
	private List<Plane> departingPlanes;
	private List<String> departureQueue;
	private Map<String, Plane> departureMap;
	private Map<String, List<Waypoint>> sectorPoints;
	private Map<Integer, List<String[]>> separationViolators;
	private String visibleSector;
	private List<String[]> sectorBoundaries;
	private List<String[]> segmentLines;
	
	private List<Plane> allPlanes;
	private List<Slot> slots;
	private List<String> controllers;
	private List<Plane> arrivingPlanes;
	private Map<String, Slot> arrivingSlots;
	private Map<String, String> planeETA;
	private Map<String, String> planeDepartTime;

	private TreeSet<Integer> timePoints;
	private int maxSimTime;
	private int timePointsIndex; // current index of timePoint list to set the
									// simTime to
	private Map<String, String> controllersDist;

	private List<String> errorOutput; // list of all errors to be output to window
	private List<String> illegalSlotAccessList;	// list of slot markers that don't exist for a plane

	private static Logger logger;
	private String logFile;

	private String atmDirPath; // file path of the ATM project
	private String curDirPath; // file path of where the user is running the vis tool from
	private String guiImagePath; // file path of where the guiImages directory is located

	private double maxWaypointLatitude;
	private double maxWaypointLongitude;
	private double minWaypointLatitude;
	private double minWaypointLongitude;
	private double maxWaypointTanLatitude;

	public SimulationDataModel(String filePath) {
		waypoints = new ArrayList<>();
		sectorPoints = new HashMap<>();
		visibleSector = "ALL";
		departingPlanes = new ArrayList<>();
		planeDepartTime = new HashMap<>();
		arrivingPlanes = new ArrayList<>();
		planeETA = new HashMap<>();
		arrivingSlots = new HashMap<>();
		departureMap = new HashMap<>();
		allPlanes = new ArrayList<>();
		slots = new ArrayList<>();
		timePointsIndex = 0;
		errorOutput = new ArrayList<>();
		illegalSlotAccessList = new ArrayList<>();
		sectorBoundaries = new ArrayList<>();
		segmentLines = new ArrayList<>();
		separationViolators = new HashMap<>();

		maxWaypointLatitude = 0.0;
		maxWaypointLongitude = 0.0;
		minWaypointLatitude = 0.0;
		minWaypointLongitude = 0.0;
		maxWaypointTanLatitude = 0.0;

		String src = File.separator + "src" + File.separator;
		String brahms = File.separator + "BrahmsModels" + File.separator;

		curDirPath = filePath + File.separator;

		if (filePath.contains(src)) {
			atmDirPath = filePath.substring(0, filePath.lastIndexOf(src));
			curDirPath = atmDirPath;
		} else if (filePath.contains(brahms))
			atmDirPath = filePath.substring(0, filePath.lastIndexOf(brahms));
		else
			atmDirPath = filePath;

		guiImagePath = atmDirPath + File.separator + "src" + File.separator + "guiImages" + File.separator;
	}

	public void createLogger() {
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH) + 1;
		String day = month + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.YEAR);
		String time = String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + "_" + String.format("%02d", c.get(Calendar.MINUTE)) + "_" + String.format("%02d", c.get(Calendar.SECOND));
		String logDir = atmDirPath + File.separator + "LogFiles";
		File logDirectory = new File(logDir);
		if (!logDirectory.exists())
			logDirectory.mkdir();

		logger = Logger.getLogger("GUIManager");
		FileHandler fileHandler;
		try {
			logFile = logDir + File.separator + "LogFile_" + day + "_" + time + ".txt";
			fileHandler = new FileHandler(logFile, false);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Waypoint> getWaypoints() {
		return waypoints;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public Integer addWaypoint(Waypoint w) {
		waypoints.add(w);
		setChanged();
		notifyObservers();
		clearChanged();
		return waypoints.size() - 1;
	}

	public void setWaypoints(List<Waypoint> points) {
		waypoints = points;
	}
	
	public void setSectorPoints(Map<String, String[]> sectors) {
		Iterator<Entry<String, String[]>> itr = sectors.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String[]> pair = itr.next();
			String[] points = pair.getValue();
			ArrayList<Waypoint> wPoints = new ArrayList<>();
			for (int i = 0; i < points.length; i++)
				wPoints.add(getWaypoint(points[i].trim()));
			sectorPoints.put(pair.getKey(), wPoints);
		}
		sectorPoints.put("ALL", waypoints);
	}

	public void setAgents(List<Agent> ag) {
		agents = ag;
	}

	public Waypoint getWaypoint(int i) {
		return waypoints.get(i);
	}
	
	public Waypoint getWaypoint(String name) {
		for (Waypoint w : waypoints) {
			if (w.getName().equals(name))
				return w;
		}
		return null;
	}
	
	public List<Waypoint> getVisibleWaypoints() {
		return sectorPoints.get(visibleSector);
	}
	
	public void updateVisibleSector(String sector) {
		visibleSector = sector;
		
		// TODO finish updating this method
		
		setChanged();
		notifyObservers();
		clearChanged();
	}
	
	public void addSectorBoundary(List<String[]> boundaries) {
		sectorBoundaries.addAll(boundaries);
	}
	
	public void addSegmentLines(List<String[]> segments) {
		segmentLines.addAll(segments);
	}
	
	public List<Line2D> getSectorBoundaries() {
		List<Line2D> boundaryLines = new ArrayList<>();
		for (int i = 0; i < sectorBoundaries.size(); i++) {
			String[] line = sectorBoundaries.get(i);
//			double x1 = DrawUtils.convertSectorPointLongitudeDDMMSS(line[1]);
//			double y1 = DrawUtils.convertSectorPointLatitudeDDMMSS(line[0]);
//			double x2 = DrawUtils.convertSectorPointLongitudeDDMMSS(line[3]);
//			double y2 = DrawUtils.convertSectorPointLatitudeDDMMSS(line[2]);
			double x1 = DrawUtils.convertSectorPointLongitudeDouble(line[1]);
			double y1 = DrawUtils.convertSectorPointLatitudeDouble(line[0]);
			double x2 = DrawUtils.convertSectorPointLongitudeDouble(line[3]);
			double y2 = DrawUtils.convertSectorPointLatitudeDouble(line[2]);
			boundaryLines.add(new Line2D.Double(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2)));
		}
		return boundaryLines;
	}
	
	public List<Line2D> getSegmentLines() {
		List<Line2D> segments = new ArrayList<>();
		for (int i = 0; i < segmentLines.size(); i++) {
			String[] list = segmentLines.get(i);
			for (int j = 0; j < list.length - 1; j++) {
				Waypoint end1 = getWaypoint(list[j]);
				Waypoint end2 = getWaypoint(list[j+1]);
				double x1 = DrawUtils.convertSectorPointLongitudeDDMMSS(end1.getLongitude());
				double y1 = DrawUtils.convertSectorPointLatitudeDDMMSS(end1.getLatitude());
				double x2 = DrawUtils.convertSectorPointLongitudeDDMMSS(end2.getLongitude());
				double y2 = DrawUtils.convertSectorPointLatitudeDDMMSS(end2.getLatitude());
				segments.add(new Line2D.Double(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2)));
			}
		}
		return segments;
	}

	public void addDepartureQueue(List<String> queue) {
		departureQueue = queue;
	}

	public void addDepartingPlane(Plane plane, String time) {
		int index = 0;
		for (int i = 0; i < departingPlanes.size(); i++) {
			Plane p = departingPlanes.get(i);
			if (Integer.parseInt(plane.getStartTime()) <= Integer.parseInt(p.getStartTime())) {
				index = i;
				break;
			} else
				index = i + 1;
		}

		departureMap.put(plane.getName(), plane);

		departingPlanes.add(index, plane);
		planeDepartTime.put(plane.getAgent().getName(), time);
	}

	public String updateDepartureSequence(int simTime) {
		for (int i = 0; i < departingPlanes.size(); i++) {
			Plane p = departingPlanes.get(i);
			Integer time = Integer.parseInt(p.getStartTime()) - simTime;
			String fly = Boolean.toString(p.isFlying());
			Plane newP = new Plane(p.getAgent(), p.getAltitude(), p.isDeparture(), p.getSpeed(), p.getController(), p.getLatitude(), p.getLongitude(), fly, p.getStartTime(), p.isMetering());
			departingPlanes.remove(i);
			addDepartingPlane(newP, time.toString());
		}
		return getDepartureSequence();
	}

	public String getDepartureSequence() {
		StringBuilder depart = new StringBuilder();
		int qNum = 0;
		if (!departureMap.isEmpty()) {
			for (int i = 0; i < departingPlanes.size(); i++) {
				Plane p = departureMap.get(departingPlanes.get(i).getName());
				if (p != null && Integer.parseInt(p.getStartTime()) > getSimTime() || (!p.isFlying() && getSimTime() < p.getLandedSimTime())) {
					qNum += 1;
					if (i <= 8)
						depart.append(qNum + ")   " + p.getName() + "\n");
					else
						depart.append(qNum + ") " + p.getName() + "\n");
				}
			}
		}
		return depart.toString();
	}

	public List<String> getDepartureQueue() {
		return departureQueue;
	}

	public void addArrivingPlane(Plane plane) {
		int index = 0;
		for (int i = 0; i < arrivingPlanes.size(); i++) {
			Plane p = arrivingPlanes.get(i);
			if (plane.getEta() <= p.getEta()) {
				index = i;
				break;
			} else
				index = i + 1;
		}
		arrivingPlanes.add(index, plane);
		planeETA.put(plane.getName(), Double.toString(plane.getEta()));
	}

	public void updateArrivalSequence(Plane plane) {
		for (int i = 0; i < arrivingPlanes.size(); i++) {
			Plane p = arrivingPlanes.get(i);
			if (plane.getName().equals(p.getName())) {
				arrivingPlanes.remove(i);
				addArrivingPlane(plane);
				break;
			}
		}
	}

	public String getArrivalSequence(int simTime) {
		StringBuilder arrive = new StringBuilder();
		arrive.append("      Plane\t" + "ETA\t" + "STA - ETA\n");
		int qNum = 0;
		for (int i = 0; i < arrivingPlanes.size(); i++) {
			Plane p = arrivingPlanes.get(i);
			if (p.getLandedSimTime() >= simTime && Integer.parseInt(p.getStartTime()) <= simTime) {
				qNum += 1;
				int eta = (int) Math.round(p.getEta() / 60.0);
				Slot s = arrivingSlots.get("slot_" + p.getName());
				if (s != null) {
					int diff = (int) (s.getETA() - p.getEta());
					if (i <= 8)
						arrive.append(qNum + ")   " + p.getName().substring(6) + "\t" + eta + " minutes\t" + diff + " seconds\n");
					else
						arrive.append(qNum + ") " + p.getName().substring(6) + "\t" + eta + " minutes\t" + diff + " seconds\n");
				} else {
					String error = "Trying to access slot marker slot_" + p.getName().substring(6) + " which doesn't exist"; 
					if (!illegalSlotAccessList.contains(p.getName().substring(6))) {
						illegalSlotAccessList.add(p.getName().substring(6));
						addError(error);
						GUIFunctions.updateErrorList(getErrorOutput());
						// this check ensures this error only gets printed to log file once rather than every time the slot is trying to be accessed
						if (!errorOutput.contains(error))
							logger.log(Level.INFO, "SimulationDataModel:getArrivalSequence - " + error);
					}
				}
			}
		}
		return arrive.toString();
	}

	public void resetPlaneSequences() {
		departingPlanes.clear();
		planeDepartTime.clear();
		arrivingPlanes.clear();
		planeETA.clear();
		arrivingSlots.clear();
		departureMap.clear();
		allPlanes.clear();
		slots.clear();
		timePointsIndex = 0;
	}

	public Integer hitWaypointTest(double d, double e) {
		for (int i = waypoints.size() - 1; i >= 0; i--) {
			boolean contains = waypoints.get(i).contains(d, e);
			if (contains)
				return i;
		}
		return -1;
	}

	public List<Plane> getAllPlanes() {
		// TODO: use Collections.unmodifiable and test
		return allPlanes;
	}
	
	public List<Plane> getArrivingPlanes() {
		return arrivingPlanes;
	}

	public void addPlane(Plane p) {
		allPlanes.add(p);
	}

	public List<Slot> getSlots() {
		return slots;
	}

	public Slot getSlot(String name) {
		return arrivingSlots.get(name);
	}

	public void addSlot(Slot s) {
		arrivingSlots.put(s.getName(), s);
		slots.add(s);
	}

	public int getSimTimeIndex() {
		return timePointsIndex;
	}

	public void setTimePoints(SortedSet<Integer> points) {
		timePoints = (TreeSet<Integer>) points;
		maxSimTime = points.size() - 1;
	}

	public int getSimulationDuration() {
		return timePoints.last();
	}

	// this is for Arnaud's ScenarioParser.java:parseTimePoints()
	// delete this once that has been updated
	public void setTimePoints(List<String> points) {
		timePoints = new TreeSet<>();
		for (String time : points)
			timePoints.add(Integer.parseInt(time.trim()));
		maxSimTime = points.size() - 1;
	}

	public int getSimTime() {
		if (timePointsIndex < maxSimTime)
			return (int) timePoints.toArray()[timePointsIndex];
		else
			return timePoints.last();
	}

	public void updateSimTime() {
		timePointsIndex++;
	}

	public void setSimTime(int index) {
		timePointsIndex = index;
	}
	
	public void setSeparationViolators(Map<Integer, List<String[]>> violators) {
		separationViolators = violators;
	}
	
	public String getSeparationViolation() {
		StringBuilder violators = new StringBuilder();
		List<String[]> violList = separationViolators.get(getSimTime());
		if (violList == null)
			violList = new ArrayList<>();
		for (int i = 0; i < violList.size(); i++) {
			String[] v = violList.get(i);
			violators.append(v[1]).append(" is ");
			Double separationDistance = Double.parseDouble(v[3]);
			violators.append(String.format("%.5g", separationDistance)).append(" miles in trail of ");
			violators.append(v[0]).append("\n");
		}
		if (violList.size() > 0 && timePointsIndex <= maxSimTime) {
			logger.log(Level.INFO, "SimulationDataModel:getSeparationViolation - TIME: " + timePoints.toArray()[timePointsIndex] + " : " + violators.toString());
		}
		return violators.toString();
	}

	public void setControllers(Map<String, String> controllers) {
		controllersDist = controllers;
	}

	public void setControllerList(List<String> cont) {
		// TODO: create new list
		controllers = cont;
	}

	public List<String> getControllerNames() {
		// TODO: use Collections.unmodifiable and test
		return controllers;
	}

	public Plane getPlane(String plane) {
		for (Plane p : arrivingPlanes) {
			if (p.getName().equals(plane))
				return p;
		}
		return null;
	}

	public String getErrorOutput() {
		StringBuilder output = new StringBuilder();
		for (String err : errorOutput)
			output.append(err + "\n");
		return output.toString();
	}

	public void addError(String error) {
		if (!errorOutput.contains(error))
			errorOutput.add(error);
	}

	public void clearErrors() { 
		errorOutput.clear();
	}

	public String getLogFileName() {
		return logFile;
	}

	public String getAtmDir() {
		return atmDirPath;
	}

	public String getCurDir() {
		return curDirPath;
	}

	public void setMaxWaypointLatitude(double maxLat) {
		maxWaypointLatitude = maxLat;
	}

	public double getMaxWaypointLatitude() {
		return maxWaypointLatitude;
	}

	public void setMinWaypointLatitude(double minLat) {
		minWaypointLatitude = minLat;
	}

	public double getMinWaypointLatitude() {
		return minWaypointLatitude;
	}

	public void setMaxWaypointLongitude(double maxLon) {
		maxWaypointLongitude = maxLon;
	}

	public double getMaxWaypointLongitude() {
		return maxWaypointLongitude;
	}

	public void setMinWaypointLongitude(double minLon) {
		minWaypointLongitude = minLon;
	}

	public double getMinWaypointLongitude() {
		return minWaypointLongitude;
	}

	public void setMaxWaypointTanLatitude(double maxTan) {
		maxWaypointTanLatitude = maxTan;
	}

	public double getMaxWaypointTanLatitude() {
		return maxWaypointTanLatitude;
	}

	public String getGuiImageDirectoryPath() {
		return guiImagePath;
	}

	public int getMaxSimTime() {
		return maxSimTime;
	}

	public boolean endOfSim() {
		if (timePointsIndex >= maxSimTime)
			return true;
		return false;
	}

	public void update() {
		setChanged();
		notifyObservers();
		clearChanged();
	}
}
