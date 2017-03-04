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

package gov.nasa.airspace.modelgen.routes;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Aircraft implements Comparable<Aircraft>{
	
	static final boolean UNIQUE_FL_SEG = false;
	
	Set<String> flightSegmentDuplicates = new HashSet<String>();
	
	static final String NOT_SET = "";
	static final int DEPT_SET_LEN = 3;
	
	String id;
	Map<String, Double> latMap;
	Map<String, Double> longMap;
	Map<String, Double> speedMap;
	Map<String, Double> altMap;
	List<String> wayPoints;
	int numPoints;
	boolean getWaypoint = false;
	String flightPlanId; 
	boolean isDept = false;
	String entryTimeInSecs = NOT_SET;
	int startTime = Integer.MIN_VALUE;
	String startController;
	
	double initLat = 0.0;
	double initLon = 0.0;
	double initAlt = 0.0;
	double initSpeed = 0.0;
	
	int timeSTAFrozen = Integer.MAX_VALUE;
	int STAETADiff = 0; 
	
	
	static final double earthRadius = 3959; //Earth's radis in NM
	
	public Aircraft(String id) {
		this.id = id;
		latMap = new HashMap<String, Double>();
		longMap = new HashMap<String, Double>();
		speedMap = new HashMap<String, Double>();
		altMap = new HashMap<String, Double>();
		wayPoints = new ArrayList<String>();
		flightPlanId = "flightPlan_" + id;
	}
	
	public void setInitialController(String controller) {
		this.startController = controller;
	}
	
	public String getInitialController() {
		return this.startController;
	}
	
	public boolean isSTAFrozen() {
		if(timeSTAFrozen < Integer.MAX_VALUE)
			return true;
		return false;
	}
	
	public void setSTAElements(int timeSTAFrozen, int STAETADiff) {
		this.timeSTAFrozen = timeSTAFrozen;
		this.STAETADiff = STAETADiff;
	}
	
	public int getStartTime() {
		return this.startTime;
	}
	
	
	
	public boolean hasValidStartTime() {
		if(this.startTime >=0) return true;
		return false;
	}
	
	public void setInitialLatLon(double initLat, double initLon) {
		this.initLat = initLat;
		this.initLon = initLon;
	}
	
	public void setInitAlt(double altitude) {
		this.initAlt = altitude;
	}
	
	public void setInitSpeed(double speed) {
		this.initSpeed = speed; 
	}
	public boolean isEntryTimeSet() { 
		if(entryTimeInSecs == NOT_SET) {
			return false;
		}
		return true;
	}
	
	public String getFirstWaypoint() {
		assert(wayPoints.size() > 0);
		return wayPoints.get(0);
	}
	
	public String getSecondWaypoint() { 
		assert(wayPoints.size() > 1);
		return wayPoints.get(1);
	}
	
	public String getId() {
		 return id;
	}
	
	
	public void setIsDeparture(boolean isDeparture ) {
		this.isDept = isDeparture;
	}
	
	public boolean isDepartureAircraft() {
		return this.isDept;
	}
	
	public void setEntryTimeInSeconds(String entryTimeInSecs,
													int offset) {
		this.entryTimeInSecs = entryTimeInSecs; 
		startTime = offset;
	}
	
	public Map<String, Double> getLatMap() {
		return this.latMap;
	}
	
	public Map<String, Double> getLonMap() {
		return this.longMap;
	}
	
	public Map<String, Double> getAltMap() {
		return this.altMap;
	}
	public void setValues(String waypoint, Double lat, Double lon, Double cas,
			Double alt) {
		latMap.put(waypoint, lat);
		longMap.put(waypoint, lon);
		speedMap.put(waypoint, cas);
		altMap.put(waypoint, alt);
		wayPoints.add(waypoint);
	}
	
	public void setNumberofWaypoints(int number_of_points) {
		this.numPoints = number_of_points;
	}
	
	public int getNumberofWaypoints() {
		return numPoints;
	}
	
	public void waypointReset() {
		getWaypoint = true;
	}
	
	public boolean startGettingWaypoints(){
		return getWaypoint;
	}
	
	private int getwpOfInterestNum() {
		int end_counter = wayPoints.size();
		if(this.isDept) {
			end_counter = Aircraft.DEPT_SET_LEN;
		}
		return end_counter;
	}
	
	public void setWaypointOfInterest(Waypoint wp) {
		int end_counter = getwpOfInterestNum();
		for(int wIndex = 0; wIndex < end_counter; wIndex++) {
			String wayPoint = wayPoints.get(wIndex);
			double lat = latMap.get(wayPoint);
			double lon = longMap.get(wayPoint);
			wp.addWayPoint(wayPoint, lat, lon);
		}
	}
	
	
	public void genFlightSegmentBrahmsFile(File file) {
		String flightId = "";
		if(Aircraft.UNIQUE_FL_SEG)
			flightId = "_" + this.getId();
	
		
		
		String fg =  "flightSegment { \n";
		String retStr = "";
		int end_counter = getwpOfInterestNum();
		for(int wIndex = 0; wIndex < end_counter; wIndex++) {
			String obj = "object ";
			String wayPoint = wayPoints.get(wIndex);
			if(wIndex < end_counter - 1) { 
				String nextWayPoint = wayPoints.get(wIndex+1);
				String wp = wayPoint + "_TO_" + nextWayPoint+  flightId + 
									" instanceof "+ fg;
				if(this.flightSegmentDuplicates.contains(wp)) continue;
				flightSegmentDuplicates.add(wp);
				
				String beliefs = "  initial_beliefs:\n";
				String facts = "  initial_facts:\n";
				String vl = "  \t(current.fromWaypoint = " + wayPoint + ");\n";
				vl = vl + "  \t(current.toWaypoint = " + nextWayPoint + ");\n";
				vl = vl + "  \t(current.end_altitude = " + this.altMap.get(nextWayPoint) + ");\n";
				vl = vl + "  \t(current.end_speed = " + this.speedMap.get(nextWayPoint) + ");\n";
				retStr = retStr + obj + wp + beliefs + vl + facts + vl + "}\n";
			}
		
		}
		
		FileManagerUtils.writeToFile(file, retStr, true);
		
	}
	
	public void genFlightPlanBrahmsFile(File file) {

		int end_counter = getwpOfInterestNum();
		String obj = "object " + flightPlanId;
		String val = " instanceof flightPlan {\n";
		String beliefs = "  initial_beliefs:\n";
		String facts = "  initial_facts:\n";
		String str = "";
		
		String flightId = "";
		if(Aircraft.UNIQUE_FL_SEG)
			flightId = "_" + this.getId();
		
		for(int wIndex = 0; wIndex < end_counter; wIndex++) {
			String wayPoint = wayPoints.get(wIndex);
			if(wIndex < (end_counter - 1)) { 
				String nextWayPoint = wayPoints.get(wIndex+1);
				str = str + "\t(current.flightPlanMap(" +
						String.valueOf(wIndex) + ") = " +
						wayPoint + "_TO_" + nextWayPoint + flightId +
						");\n";
			}
		}

		String retStr = obj + val + beliefs + str + facts + str + "}";
		//System.out.println(retStr);

		FileManagerUtils.writeToFile(file, retStr, true);
	}

	
	public void genAircraftBrahmsFiles(File airplaneFile) {

		String obj = "agent plane_" + this.id 
					+ " memberof Airplane {\n";
		
		String beliefs = "  initial_beliefs:\n";
		String facts = "  initial_facts:\n";
		
		String str = "\t(current.Name = " + this.id +");\n";
		str = str +	"\t(current.m_dLatitude = " + this.initLat + ");\n";
		str = str + "\t(current.m_dLongitude = " + this.initLon + ");\n";
		str = str + "\t(current.landed = false);\n";
		str = str + "\t(current.flightPlan = " + this.flightPlanId + ");\n";
	
		if(!isDept) { // if it is not a departure
			if(this.startTime == 0)
				str = str + "\t(current.iStatus = 1);\n";
			else 
				str = str + "\t(current.iStatus = 0);\n";
			
			str = str + "\t(current.controller = " + this.startController + ");\n";
		} else {
			str = str + "\t(current.iStatus = 2);\n";
			str = str + "\t(current.controller = " + "ZNY_118" + ");\n";
		}
		str = str + "\t(current.startTime = " + this.startTime + ");\n";
		str = str + "\t(current.iCurrentSegment = 0);\n";
		
		str = str + "\t(current.m_iAirSpeed = " + (int) this.initSpeed + ");\n";
		str = str + "\t(current.m_dAltitude = " + this.initAlt + ");\n";
		str = str + "\t(current.is_departure = " + String.valueOf(isDept) + ");\n";
		
		String retStr = obj + beliefs + str + facts + str + "}\n\n";
		
		FileManagerUtils.writeToFile(airplaneFile, retStr, true);

		
	}
	
	public void genSlotMarkerBrahmsFiles(File slotMarkerFile) {

		String slotObj = "agent slot_" + this.id 
				+ " memberof slotMarker { \n"
				+ "initial_beliefs:\n"
				+ "\t(current.Name = " + this.id +");\n"
				+ "\t(current.flightPlan = " + this.flightPlanId + ");\n"
				+ "initial_facts:\n"
				+ "\t(current.Name = " + this.id +");\n"
				+ "\t(current.flightPlan = " + this.flightPlanId + ");\n"
				+ "}\n";

		if(!this.isDept) {
			FileManagerUtils.writeToFile(slotMarkerFile, slotObj, true);
		} 

	}
	
	public double computeDistance(double currLat, double currLon, double targetLat,
			double targLon) {
		currLat = currLat * (Math.PI/180);
		currLon = currLon * (Math.PI/180);
		targetLat = targetLat * (Math.PI/180);
		targLon = targLon * (Math.PI/180);
		double a = Math.sin((targetLat - currLat)/2) * Math.sin((targetLat - currLat)/2) +
		           Math.cos(currLat) * Math.cos(targetLat) *
		           Math.sin((targLon - currLon)/2) * Math.sin((targLon - currLon)/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = earthRadius*c;
		return d;
	}
	
	public void print() { 
		int end_counter = getwpOfInterestNum();
		for(int wIndex = 0; wIndex < end_counter; wIndex++) {
			String wayPoint = wayPoints.get(wIndex);
			if(wIndex < end_counter - 1) { 
				String nextWayPoint = wayPoints.get(wIndex+1);
				System.out.println(wayPoint + ", " + nextWayPoint);
			}
			
		}
		System.out.println("*****************");
		System.out.println(latMap.toString());
		System.out.println("*****************");
		System.out.println(longMap.toString());
		System.out.println("*****************");
	}

	

	@Override
	public int compareTo(Aircraft o) {
		return this.startTime - o.getStartTime();
	}
	

}
