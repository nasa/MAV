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

package atcGUI.control_view;

import java.awt.geom.Point2D;

import atcGUI.components.GUIFunctions;
import gov.nasa.arc.brahms.visualization.elements.Plane;
import atcGUI.model.SimulationDataModel;
import gov.nasa.arc.brahms.visualization.elements.Slot;

public class DrawUtils {

	private static int CORNERDIFF;

	private static double maxWaypointLatitude;
	private static double minWaypointLatitude;
	private static double maxWaypointLongitude;
	private static double minWaypointLongitude;
	private double maxWaypointTanLatitude;
	private static double totalDiffLat;
	private static double totalDiffLon;

	private static DrawUtils sConverter;
	
	private DrawUtils(SimulationDataModel data) {
		maxWaypointLatitude = data.getMaxWaypointLatitude();
		maxWaypointLongitude = data.getMaxWaypointLongitude();
		minWaypointLatitude = data.getMinWaypointLatitude();
		minWaypointLongitude = data.getMinWaypointLongitude();
		maxWaypointTanLatitude = data.getMaxWaypointTanLatitude();
		CORNERDIFF = GUIFunctions.getCanvasSize() - 200;
		totalDiffLat = maxWaypointLatitude - minWaypointLatitude;
		totalDiffLon = maxWaypointLongitude - minWaypointLongitude;
	}
	
	public static void initialize(SimulationDataModel data) {
		if (sConverter == null)
			sConverter = new DrawUtils(data);
	}
	
	public static Plane convertLatLongToScreenPoints(Plane p) {
		// (maxLong, maxLat) ==(maps to)==> (100,100) x/y coords
		double pLat = Double.parseDouble(p.getLatitude());
		double pLon = Math.abs(Double.parseDouble(p.getLongitude())); // if longitude is negative, drop neg sign

		// converting decimal form to x/y coordinates
		double latDiff = maxWaypointLatitude - pLat;
		double lonDiff = maxWaypointLongitude - pLon;
		double percentageLat = latDiff / totalDiffLat;
		double percentageLon = lonDiff / totalDiffLon;
		double Y = (CORNERDIFF * percentageLat) + 100;
		double X = (CORNERDIFF * percentageLon) + 100;

		p.setScreenCoord(X, Y);
		return p;
	}

	public static Slot convertLatLongToScreenPoints(Slot s) {
		// (maxLong, maxLat) ==(maps to)==> (100,100) x/y coords
		double sLat = Double.parseDouble(s.getLatitude());
		double sLon = Double.parseDouble(s.getLongitude());

		// converting decimal form to x/y coordinates
		double latDiff = maxWaypointLatitude - sLat;
		double lonDiff = maxWaypointLongitude - sLon;
		double percentageLat = latDiff / totalDiffLat;
		double percentageLon = lonDiff / totalDiffLon;
		double centerY = (CORNERDIFF * percentageLat) + 100;
		double centerX = (CORNERDIFF * percentageLon) + 100;

		s.setScreenCoord(centerX, centerY);

		/////////////////////////////
		// determine slot radius here
		/////////////////////////////
		double earthRadius = 3960.0;
		double mph = Double.parseDouble(s.getSpeed()) * 1.15078; // convert knots to mph
		double mpMin = mph / 60.0; // convert mph to miles per min

		// computing distance traveled in 1 min in degrees
		double latChange = Math.toDegrees(mpMin / earthRadius);
		double r = earthRadius * Math.cos(Math.toRadians(sLat));
		double lonChange = Math.toDegrees(mpMin / r);

		sLat += latChange;
		sLon += lonChange;
		latDiff = maxWaypointLatitude - sLat;
		lonDiff = maxWaypointLongitude - sLon;
		percentageLat = latDiff / totalDiffLat;
		percentageLon = lonDiff / totalDiffLon;

		double lineY = (CORNERDIFF * percentageLat) + 100;
		double lineX = (CORNERDIFF * percentageLon) + 100;
		double diffY = Math.abs(lineY - centerY);
		double diffX = Math.abs(lineX - centerX);
		long radius = Math.round((diffY + diffX) / 2.0);

		s.setRadius(radius);
		/////////////////////////////
		/////////////////////////////
		return s;
	}

	public static double convertSectorPointLatitudeDDMMSS(String lat) {
		if (lat.length() != 6)
			return 100;
		double lati = Double.valueOf(lat.substring(0,2)) + Double.valueOf(lat.substring(2,4)) / 60.0 + Double.valueOf(lat.substring(4)) / 3600.0;
		double latDiff = maxWaypointLatitude - lati;
		double percentageLat = latDiff / totalDiffLat;
		return CORNERDIFF * percentageLat + 100.0;
	}
	
	public static double convertSectorPointLongitudeDDMMSS(String lon) {
		if (lon.length() != 6)
			return 100;
		double longi = Double.valueOf(lon.substring(0,2)) + Double.valueOf(lon.substring(2,4)) / 60.0 + Double.valueOf(lon.substring(4)) / 3600.0;
		double lonDiff = maxWaypointLongitude - longi;
		double percentageLon = lonDiff / totalDiffLon;
		return CORNERDIFF * percentageLon + 100.0;
	}
	
	public static double convertSectorPointLatitudeDouble(String lat) {
		double lati = Double.parseDouble(lat);
		double latDiff = maxWaypointLatitude - lati;
		double percentageLat = latDiff / totalDiffLat;
		return CORNERDIFF * percentageLat + 100.0;
	}
	
	public static double convertSectorPointLongitudeDouble(String lon) {
		double longi = Double.parseDouble(lon);
		double lonDiff = maxWaypointLongitude - longi;
		double percentageLon = lonDiff / totalDiffLon;
		return CORNERDIFF * percentageLon + 100.0;
	}
	
	public static Plane convertFromScreen(Plane p) {
		Point2D.Double sPoint = p.getScreenCoord();
		double xToLon = sPoint.getX() - 100;
		double yToLat = sPoint.getY() - 100;

		xToLon /= CORNERDIFF;
		yToLat /= CORNERDIFF;
		
		xToLon *= totalDiffLon;
		yToLat *= totalDiffLat;
		
		double lat = maxWaypointLatitude - yToLat;
		double lon = maxWaypointLongitude - xToLon;

		
		
		return p;
	}
}
