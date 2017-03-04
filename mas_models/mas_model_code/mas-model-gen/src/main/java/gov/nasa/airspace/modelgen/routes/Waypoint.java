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
import java.util.Map;
import java.util.StringTokenizer;

public class Waypoint { 
	
	Map<String, Double> latMap;
	Map<String, Double> longMap;
	ArrayList<String> allWayPoints;
	
	public Waypoint() {
		latMap = new HashMap<String, Double>();
		longMap = new HashMap<String, Double>();
		allWayPoints = new ArrayList<String>();
	}
	
	public boolean containsWayPoint(String wayPoint) {
		if(allWayPoints.contains(wayPoint)) return true;
		return false;
	}
	

	
	public void addWayPoint(String wayPoint, Double lat, Double lon) {
		if(allWayPoints.contains(wayPoint)) return;
		allWayPoints.add(wayPoint);
		latMap.put(wayPoint, lat);
		longMap.put(wayPoint, lon);
	}
	

	
	//object AGARD instanceof waypoint{
	//	initial_facts:
	//		(current.latitude = 39.04361111111111);
	//		(current.longitude = -76.06972222222223);
	//}
	
	public void genBrahmsFile(String path) {
		
		String fileName = path + "allwaypoints" + ".b";
		File file = FileManagerUtils.createFile(fileName);
		
		String retStr = "";
		for(int wIndex = 0; wIndex < allWayPoints.size(); wIndex++) {
			String wayPointName = allWayPoints.get(wIndex);
			retStr = retStr + "object " + wayPointName +
						" instanceof waypoint {\n";
			retStr = retStr + "  initial_beliefs:\n";
			retStr = retStr + " \t(current.name = \"" +
					wayPointName + ""
							+ "\");\n";
			retStr = retStr + " \t(current.latitude = " +
						this.latMap.get(wayPointName) + ");\n";
			retStr = retStr + " \t(current.longitude = " +
						this.longMap.get(wayPointName) + ");\n";
			retStr = retStr + "  initial_facts:\n";
			retStr = retStr + " \t(current.name = \"" +
					wayPointName + ""
							+ "\");\n";
			retStr = retStr + " \t(current.latitude = " +
						this.latMap.get(wayPointName) + ");\n";
			retStr = retStr + " \t(current.longitude = " +
						this.longMap.get(wayPointName) + ");\n";
			retStr = retStr + "}\n\n";

		}
		
		FileManagerUtils.writeToFile(file, retStr, false);
	}
	
	public void generateVisualizationWPFile(String path) {
		
		String fileName = path + "waypoints";
		File file = FileManagerUtils.createFile(fileName);
		
		String retStr = "";
		for(int wIndex = 0; wIndex < allWayPoints.size(); wIndex++) {
			String wayPointName = allWayPoints.get(wIndex);
			retStr = retStr + "1\t" + wayPointName;
			retStr  = retStr + "\t" + transformWayPoint(latMap.get(wayPointName));
			retStr = retStr + "\t" + transformWayPoint(longMap.get(wayPointName));
			retStr = retStr + "\t" + "0\n";
		}
		
		FileManagerUtils.writeToFile(file, retStr, false);
	}
	
	
public void generateTRACWPFile(String path) {
		
		File file = FileManagerUtils.createFile(path + "tracwy");
		
		String retStr = "";
		for(int wIndex = 0; wIndex < allWayPoints.size(); wIndex++) {
			String wayPointName = allWayPoints.get(wIndex);
			retStr = retStr + "1\t" + wayPointName+"Q";
			retStr  = retStr + "\t" + transformWayPoint(latMap.get(wayPointName));
			retStr = retStr + "\t" + transformWayPoint(longMap.get(wayPointName));
			retStr = retStr + "\t" + "0\n";
		}
		
		FileManagerUtils.writeToFile(file, retStr, false);
	}

	
	
	private String transformWayPoint(Double val) {
		
		String dblVal = String.valueOf(val);
		String[] firstElements = getElements(dblVal);
		String hours = firstElements[0];
		
		
		double mins = Double.valueOf("0."+firstElements[1]) * 60.0;
		String minDblVal = String.valueOf(mins);
		String[] secondElements = getElements(minDblVal);
		String minutes = secondElements[0];
		
		double secs = Double.valueOf("0."+secondElements[1]) * 60.0;
		String secsDblVal = String.valueOf(secs);
		String[] thirdElements = getElements(secsDblVal);
		String seconds = thirdElements[0];
		
		while(hours.length() < 2) {
			hours = "0" + hours; 
		}
		while(minutes.length() < 2) {
			minutes = "0" + minutes;
		}
		while(seconds.length() < 2) {
			seconds = "0" + seconds;
		}
		
		return hours+minutes+seconds;
	}
	
	private String[] getElements(String dblVal) {
		StringTokenizer strTok = new StringTokenizer(dblVal, ".");
		String[] elements = new String[2];
		int counter = 0;
		while(strTok.hasMoreElements()) {
			elements[counter] = strTok.nextElement().toString();
			counter++;
		}
		return elements;
		
	}
}
