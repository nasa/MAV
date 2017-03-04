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

package gov.nasa.arc.brahms.visualization.elements;

import gov.nasa.arc.atc.utils.MathUtils;
import gov.nasa.arc.atc.utils.SimulationProperties;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class Plane implements Comparable<Plane> {
	
	public static final String METERING_CST = "1";

	private final PropertyChangeSupport propertyChangeSupport;
	private Agent agent;
	private String altitude;
	private String latitude;
	private String longitude;
	private String speed;
	private String controller;
	private boolean departure;
	private boolean landed;
	private boolean flying;
	private double eta;
	private int landedSimTime;
	private int startTime;
	private double separationDist; // distance plane is from plane directly in
									// front of it
	private String isMetering;
	private int meterCount;
	private String planeInFront;
	private int flash;
	private Point2D.Double screenCoord;
	
	private int simTime = 0;

	public Plane(Agent ag, String alt, boolean depart, String sp, String cont, String lat, String lon, String fly, String stTime, String metering) {
		System.err.println(" !! creating PLANE with "+ag+ " lat="+lat+" lon="+lon+" fly="+fly+" speed="+sp+" metering="+metering+" alt="+alt+" controller="+cont);
		propertyChangeSupport = new PropertyChangeSupport(Plane.this);
		agent = ag;
		altitude = alt;
		speed = sp;
		controller = cont;
		landed = false;
		latitude = lat;
		longitude = lon;
		flying = new Boolean(fly);
		eta = 0.0;
		departure = depart;
		separationDist = 0.0;
		planeInFront = "";
		flash = 0;
		screenCoord = new Point2D.Double(0, 0);
		isMetering = metering;
		meterCount = isMetering.equals(METERING_CST) ? 1 : 0;
		
		try {
			startTime = Integer.parseInt(stTime);
		} catch (NumberFormatException e) {
			if (stTime.contains(".")) {
				int endInt = stTime.indexOf('.');
				startTime = Integer.parseInt(stTime.substring(0, endInt));
			}
		}

		landedSimTime = 0;
		Set<Integer> timePoints = agent.getBeliefs().keySet();
		Iterator<Integer> it = timePoints.iterator();
		while (it.hasNext()) {
			int time = it.next();
			if (landedSimTime < time)
				landedSimTime = time;
		}
		// this is to ensure departing planes that never take off stay on departure queue
		if(landedSimTime == 0 && departure)
			landedSimTime = Integer.MAX_VALUE;
	}

	public Agent getAgent() {
		return agent;
	}
	
	public int getSimTime() {
		return simTime;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String newLatitude) {
		latitude = newLatitude;
		propertyChangeSupport.firePropertyChange(SimulationProperties.LATITUDE_PPTY, null, latitude);
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String newLongitude) {
		longitude = newLongitude;
		propertyChangeSupport.firePropertyChange(SimulationProperties.LONGITUDE_PPTY, null, longitude);
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String newAltitude) {
		altitude = newAltitude;
		propertyChangeSupport.firePropertyChange(SimulationProperties.ALTITUDE_PPTY, null, altitude);
	}

	public String getDestination() {
		if (!departure)
			return "LGA";
		else
			return "";
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String newSpeed) {
		speed = newSpeed;
		propertyChangeSupport.firePropertyChange(SimulationProperties.SPEED_PPTY, null, speed);
	}

	public String getController() {
		return controller;
	}

	public void setController(String newController) {
		controller = newController;
		propertyChangeSupport.firePropertyChange(SimulationProperties.CONTROLLER_PPTY, null, controller);
	}

	public boolean isLanded() {
		return landed;
	}

	public void setLanded(boolean isLanded) {
		landed = isLanded;
		propertyChangeSupport.firePropertyChange(SimulationProperties.LANDED_PPTY, !landed, landed);
	}

	public double getEta() {
		return eta;
	}

	public void setEta(String newEta) {
		try {
			eta = Double.parseDouble(newEta);
		} catch (NumberFormatException e) {
			eta = Double.POSITIVE_INFINITY;
		}
		propertyChangeSupport.firePropertyChange(SimulationProperties.ETA_PPTY, null, eta);
	}

	public boolean isFlying() {
		return flying;
	}

	public void setFlying(boolean isFlying) {
		flying = isFlying;
		propertyChangeSupport.firePropertyChange(SimulationProperties.FLYING_PPTY, null, flying);
	}

	public int getLandedSimTime() {
		return landedSimTime;
	}

	public boolean isDeparture() {
		return departure;
	}

	public String getStartTime() {
		return String.valueOf(startTime);
	}

	public String getName() {
		return agent.getName().substring(6);
	}

	public void setSeparationDist(String dist) {
		separationDist = Double.parseDouble(dist);
		propertyChangeSupport.firePropertyChange(SimulationProperties.SEP_DIST_PPTY, null, separationDist);
	}

	public double getSeparationDist() {
		return separationDist;
	}

	public String getPlaneInFront() {
		return planeInFront;
	}

	public void setPlaneInFront(String newPlaneInFront) {
		planeInFront = newPlaneInFront;
		propertyChangeSupport.firePropertyChange(SimulationProperties.PLANE_FRONT_PPTY, null, planeInFront);
	}

	public boolean isFlashing() {
		flash++;
		if (flash > 2) {
			flash = 0;
			return true;
		} else
			return false;
	}

	public Point2D.Double getScreenCoord() {
		return screenCoord;
	}

	public void setScreenCoord(double x, double y) {
		this.screenCoord = new Point2D.Double(x, y);
	}
	
	public String isMetering() {
		return isMetering.trim();
	}
	
	public void setMetering(String val) {
		if (!val.equals(isMetering) && val.equals(METERING_CST))
			meterCount++;
		isMetering = val;
		propertyChangeSupport.firePropertyChange(SimulationProperties.METERING_PPTY, null, isMetering);
	}
	
	public int getMeterCount() {
		return meterCount;
	}

	public boolean equals(Plane p) {
		if (!p.getName().equals(this.getName()))
			return false;

		return true;
	}

	
	
	@Override
	public int compareTo(Plane p) {
		return (int) (this.getEta() - p.getEta());
	}

	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	public boolean updateBeliefs(int time, SortedSet<Plane> etaList) {
		simTime = time;
		boolean update = false;
		List<BeliefUpdate> belUp = agent.getBeliefs().get(simTime);
		if (belUp != null) {
			String lat = "";
			String lon = "";
			String alt = "";
			String airSpeed = "";
			String cont = "";
			String arriveTime = "";
			boolean land = false;
			String fly = "";
			String sepDist = "";
			String planeFront = "";
			String metering = "";

			for (BeliefUpdate bu : belUp) {
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LATITUDE_PPTY)))
					lat = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LONGITUDE_PPTY)))
					lon = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.ALTITUDE_PPTY)))
					alt = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.SPEED_PPTY)))
					airSpeed = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.CONTROLLER_PPTY)))
					cont = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LANDED_PPTY)))
					land = new Boolean(bu.getValue());
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.FLYING_PPTY)))
					fly = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.ETA_PPTY)))
					arriveTime = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.SEP_DIST_PPTY)))
					sepDist = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.PLANE_FRONT_PPTY)))
					planeFront = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.METERING_PPTY)))
					metering = bu.getValue();
			}

			if (!lat.equals(latitude) && !lat.equals("")) {
				latitude = lat;
				update = true;
			}
			if (!lon.equals(longitude) && !lon.equals("")) {
				longitude = lon;
				update = true;
			}
			if (!alt.equals("") && !alt.equals(altitude)) {
				altitude = alt;
				update = true;
			}
			if (!airSpeed.equals("") && !airSpeed.equals(speed)) {
				speed = airSpeed;
				update = true;
			}
			if (!cont.equals("") && !cont.equals(controller)) {
				controller = cont;
				update = true;
			}
			if (land)
				landed = land;
			if (!fly.equals("") && (new Boolean(fly) != flying)) {
				flying = !flying;
				update = true;
			}
			if (!arriveTime.equals("")) {
				try{
					double newETA = Double.parseDouble(arriveTime);
					if (newETA != eta) {
						if (!departure) {
							etaList.remove(this);
							etaList.add(this);
						}
						eta = newETA;
						update = true;
					}
				} catch(NumberFormatException e) {
				}
			}
			if (!sepDist.equals("")) {
				try {
					double newSepDist = Double.parseDouble(sepDist);
					if(newSepDist != separationDist) {
						separationDist = newSepDist;
						update = true;
					}
				} catch(NumberFormatException e) {
				}
			}
			if (!planeFront.equals("") && !planeFront.equals(planeInFront)) {
				planeInFront = planeFront;
				update = true;
			}
			if(!metering.equals("") && !metering.equals(isMetering)) {
				isMetering = metering;
				if (metering.equals("1"))
					meterCount++;
				update = true;
			}
			if (startTime <= simTime && landedSimTime > simTime) {
				flying = true;
				landed = false;
				update = true;
			}
		} else {
			// this prevents drawing a plane when fast forwarding simulation
			// to after the plane has already landed
			if (landedSimTime < simTime) {
				landed =true;
				flying = false;
				update = true;
			}
		}
		return update;
	}

	public double getTanLatitude() {
		return MathUtils.tanLatitude(Double.parseDouble(latitude));
	}
}
