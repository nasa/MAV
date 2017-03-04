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

import gov.nasa.arc.atc.utils.MathUtils;
import atcGUI.model.SimulationDataModel;
import gov.nasa.arc.brahms.visualization.elements.Waypoint;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class WaypointFileParser {

	private List<Waypoint> points;
	private Map<String, String[]> sectors;
	private int CORNERDIFF = 824;

	private static Logger logger = Logger.getLogger("GUIManager");

	public WaypointFileParser() {
		points = new ArrayList<>();
		sectors = new HashMap<>();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        if (width <= (CORNERDIFF + 200) || height <= (CORNERDIFF + 200)) {
        	CORNERDIFF = height - 312;
        }
	}

	/**
	 * Use this method call from the GUI for Trac waypoint files
	 * @param pointTextFilePath file path of the waypoint file to be parsed
	 * @param data from the gui
	 */
	public void parseTracWayFile(String pointTextFilePath, SimulationDataModel data) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(pointTextFilePath));
			String line;			
			while (!(line = br.readLine()).equals("SECTORS")) {
				String[] params = line.split("\\s+");
				String name = "";
				String latitude = "";
				String longitude = "";
				String altRest = "";

				if (params.length == 5) {
					name = params[1];
					latitude = params[2];
					longitude = params[3];
					Waypoint p = new Waypoint(name, latitude, longitude, altRest);
					points.add(p);

				}
			}

			while (!(line = br.readLine()).equals("SEGMENTS")) {
				String sectorName = line.substring(0, line.indexOf(":"));
				String[] sectorPoints = line.substring(line.indexOf(":") + 1).split(",");
				sectors.put(sectorName, sectorPoints);
				String vertices = br.readLine().trim();
				String edges = br.readLine().trim();
				createSectorBoundaries(vertices, edges, data);
			}
			
			List<String[]> segmentPoints = new ArrayList<>();
			while((line = br.readLine()) != null)
				segmentPoints.add(line.trim().split(","));

			data.addSegmentLines(segmentPoints);
			br.close();
		} catch (IOException e) {
			String error = pointTextFilePath + " doesn't exist";
			JOptionPane.showMessageDialog(null, error + "\nYou must restart GUI");
			data.addError(error);
			logger.log(Level.SEVERE, error);
		}
		convertLatLongTrc(data);
	}
	
	private void createSectorBoundaries(String vertices, String edges, SimulationDataModel data) {
		String[] verts = vertices.split(",");
		String[] edgeConnections = edges.split(",");
		List<String[]> vertPoints = new ArrayList<>();
		List<String[]> sectorLines = new ArrayList<>();
		for (int i = 0; i < verts.length; i++) {
			String vert = verts[i];
			int split = vert.indexOf('-');
			String latitude = vert.substring(1, split);
			String longitude = vert.substring(split + 1, vert.length() - 1);
			vertPoints.add(new String[]{longitude, latitude});
		}
		
		for (int i = 0; i < edgeConnections.length; i++) {
			String line = edgeConnections[i];
			int split = line.indexOf('-');
			int p1 = Integer.parseInt(line.substring(1, split));
			int p2 = Integer.parseInt(line.substring(split + 1, line.length() - 1));
			String[] end1 = vertPoints.get(p1);
			String[] end2 = vertPoints.get(p2);
			sectorLines.add(new String[] {end1[1], end1[0], end2[1], end2[0]});
		}
		
		data.addSectorBoundary(sectorLines);
	}

	/**
	 * Called for the GUI method on Trac waypoint files
	 * @param data
	 */
	private void convertLatLongTrc(SimulationDataModel data) {
		double maxLat = 0.0;
    	double maxLong = 0.0;
    	double minLat = Double.MAX_VALUE;
    	double minLong = Double.MAX_VALUE;
    	double maxTanLat = 0.0;

    	for (int i = 0; i < points.size(); i++) {
    		// finding bounding box of waypoints
    		String lat = points.get(i).getLatitude();
    		String lon = points.get(i).getLongitude();
	        double latit = 0;
    		double longit = 0;
    		try {
	    		latit = Double.valueOf(lat.substring(0,2)) + Double.valueOf(lat.substring(2,4)) / 60.0 + Double.valueOf(lat.substring(4)) / 3600.0;
	    		longit = Double.valueOf(lon.substring(0,2)) + Double.valueOf(lon.substring(2,4)) / 60.0 + Double.valueOf(lon.substring(4)) / 3600.0;
    		} catch (NumberFormatException e) {
    			String error = "Error converting from string to double. Make sure all these values " +
    					"from waypoint.txt are numbers and not letters: " + lat.substring(0,2) + " " + lat.substring(2,4) + " " + 
    					lat.substring(4) + "; " + lon.substring(0,2) + " " + lon.substring(2,4) + " " + lon.substring(4);
    			data.addError(error);
    			JOptionPane.showMessageDialog(null, error + "\nYou must restart GUI");
    			logger.log(Level.SEVERE, error);
    		}

    		if (latit > maxLat) 
    			maxLat = latit;
    		if (latit < minLat) 
    			minLat = latit;
    		if (longit > maxLong) 
    			maxLong = longit;
    		if (longit < minLong) 
    			minLong = longit;

    		double tanLatit = MathUtils.tanLatitude(latit);
    		if (tanLatit > maxTanLat)
    			maxTanLat = tanLatit;
    	}

    	data.setMaxWaypointLatitude(maxLat);
    	data.setMaxWaypointLongitude(maxLong);
    	data.setMinWaypointLatitude(minLat);
    	data.setMinWaypointLongitude(minLong);
    	data.setMaxWaypointTanLatitude(maxTanLat);

    	// (maxLong, maxLat) ==(maps to)==> (100,100) x/y coords
    	double totalDiffLat = maxLat - minLat;
    	double totalDiffLon = maxLong - minLong;

    	for (int i = 0; i < points.size(); i++) {
    		// converting lat/long coordinates to decimal form
    		String lat = points.get(i).getLatitude();
    		String lon = points.get(i).getLongitude();
    		double latit = 0;
    		double longit = 0;
    		try {
    			latit = Double.valueOf(lat.substring(0,2)) + Double.valueOf(lat.substring(2,4)) / 60.0 + Double.valueOf(lat.substring(4)) / 3600.0;
	    		longit = Double.valueOf(lon.substring(0,2)) + Double.valueOf(lon.substring(2,4)) / 60.0 + Double.valueOf(lon.substring(4)) / 3600.0;
    		} catch (NumberFormatException e) {
    			String error = "Error converting from string to double. Make sure all these values " +
    					"from waypoint.txt are numbers and not letters: " + lat.substring(0,2) + " " + lat.substring(2,4) + " " + 
    					lat.substring(4) + "; " + lon.substring(0,2) + " " + lon.substring(2,4) + " " + lon.substring(4);
    			data.addError(error);
    			JOptionPane.showMessageDialog(null, error + "\nYou must restart GUI");
    			logger.log(Level.SEVERE, error);
    		}

	        // converting decimal form to x/y coordinates
    		double latDiff = maxLat - latit;
    		double lonDiff = maxLong - longit;

    		double percentageLat = latDiff / totalDiffLat;
    		double percentageLon = lonDiff / totalDiffLon;

    		double Y = (CORNERDIFF * percentageLat) + 100;
    		double X = (CORNERDIFF * percentageLon) + 100;
    		points.get(i).setCenter(new Point2D.Double(X, Y));
    	}

    	if (data != null) {
    		data.setWaypoints(points);
    		data.setSectorPoints(sectors);
    	}
	}

	/**
	 * Use this method call from the GUI for .txt files
	 * @param pointTextFilePath file path of the waypoint text file to be parsed
	 * @param data from the gui
	 */
	public void parseTxtWayFile(String pointTextFilePath, SimulationDataModel data) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(pointTextFilePath));
			String line;			
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\s+", "");
				String name = "";
				String latitude = "";
				String longitude = "";
				String altRest = "";

				if (line.contains("name")) {
					name = line.substring(line.indexOf(":") + 2, line.length() - 2);
					line = br.readLine().replaceAll("\\s+", "");
				}
				if (line.contains("latitude")) {
					latitude = line.substring(line.indexOf(":") + 2, line.length() - 2);
					line = br.readLine().replaceAll("\\s+", "");
				}
				if (line.contains("longitude")) {
					longitude = line.substring(line.indexOf(":") + 2, line.length() - 2);
					line = br.readLine().replaceAll("\\s+", "");
				}
				if (line.contains("altitude_restriction")) {
					altRest = line.substring(line.indexOf(":") + 2, line.length() - 2) + " ft";
					line = br.readLine().replaceAll("\\s+", "");
					Waypoint p = new Waypoint(name, latitude, longitude, altRest);
					points.add(p);
				}
			}
			br.close();
		} catch (IOException e) {
			String error = pointTextFilePath + " doesn't exist";
			data.addError(error);
			JOptionPane.showMessageDialog(null, error + "\nYou must restart GUI");
			logger.log(Level.SEVERE, error);
		}
		convertLatLongTxt(data);
	}

	/**
	 * Called for the GUI method on .txt waypoint files
	 * @param data
	 */
	private void convertLatLongTxt(SimulationDataModel data) {
		double maxLat = 0.0;
    	double maxLong = 0.0;
    	double minLat = Double.MAX_VALUE;
    	double minLong = Double.MAX_VALUE;

    	for (int i = 0; i < points.size(); i++) {
    		// finding bounding box of waypoints
    		String[] lat = points.get(i).getLatitude().replace("'", "").substring(1).split("\\.");
    		String[] lon = points.get(i).getLongitude().replace("'", "").substring(1).split("\\.");
	        double latit = 0;
    		double longit = 0;
    		try {
	    		latit = Double.valueOf(lat[0]) + Double.valueOf(lat[1] + "." + lat[2])/60.0;
	    		longit = Double.valueOf(lon[0]) + Double.valueOf(lon[1] + "." + lon[2])/60.0;
    		} catch (NumberFormatException e) {
    			String error = "Error converting from string to double. Make sure all these values " +
    					"from waypoint.txt are numbers and not letters: " + lat[0] + " " + lat[1] + " " + 
    					lat[2] + "; " + lon[0] + " " + lon[1] + " " + lon[2];
    			data.addError(error);
    			JOptionPane.showMessageDialog(null, error + "\nYou must restart GUI");
    			logger.log(Level.SEVERE, error);
    		}

    		if (points.get(i).getLatitude().substring(0,1).equalsIgnoreCase("e"))
    			latit *= -1.0;
    		if (points.get(i).getLongitude().substring(0,1).equalsIgnoreCase("s"))
    			longit *= -1.0;

    		if (latit > maxLat) 
    			maxLat = latit;
    		if (latit < minLat) 
    			minLat = latit;
    		if (longit > maxLong) 
    			maxLong = longit;
    		if (longit < minLong) 
    			minLong = longit;
    	}

    	// (maxLong, maxLat) ==(maps to)==> (100,100) x/y coords
    	double totalDiffLat = maxLat - minLat;
    	double totalDiffLon = maxLong - minLong;

    	for (int i = 0; i < points.size(); i++) {
    		// converting lat/long coordinates to decimal form
    		String[] lat = points.get(i).getLatitude().replace("'", "").substring(1).split("\\.");
    		String[] lon = points.get(i).getLongitude().replace("'", "").substring(1).split("\\.");
    		double latit = 0;
    		double longit = 0;
    		try {
	    		latit = Double.valueOf(lat[0]) + Double.valueOf(lat[1] + "." + lat[2])/60.0;
	    		longit = Double.valueOf(lon[0]) + Double.valueOf(lon[1] + "." + lon[2])/60.0;
    		} catch (NumberFormatException e) {
    			String error = "Error converting from string to double. Make sure all these values " +
    					"from waypoint.txt are numbers and not letters: " + lat[0] + " " + lat[1] + " " + 
    					lat[2] + "; " + lon[0] + " " + lon[1] + " " + lon[2];
    			data.addError(error);
    			JOptionPane.showMessageDialog(null, error + "\nYou must restart GUI");
    			logger.log(Level.SEVERE, error);
    		}

	        // converting decimal form to x/y coordinates
    		double latDiff = maxLat - latit;
    		double lonDiff = maxLong - longit;
    		
    		double percentageLat = latDiff / totalDiffLat;
    		double percentageLon = lonDiff / totalDiffLon;
    		
    		double Y = (CORNERDIFF * percentageLat) + 100;
    		double X = (CORNERDIFF * percentageLon) + 100;
    		points.get(i).setCenter(new Point2D.Double(X, Y));
    	}

    	if (data != null) {
    		for (Waypoint p : points) {
    			String latit = p.getLatitude();
    			String longit = p.getLongitude();
    			latit = latit.substring(0,3) + "\u00b0" + " " + latit.substring(4);
				longit = longit.substring(0,3) + "\u00b0" + " " + longit.substring(4);
				p.setLatitude(latit);
				p.setLongitude(longit);
    		}
    		data.setWaypoints(points);
    		data.setSectorPoints(sectors);
    	}
	}
}
