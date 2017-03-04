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

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import gov.nasa.arc.atc.utils.MathUtils;
import gov.nasa.arc.atc.utils.SimulationProperties;

// class to be merged with slot in atclib
public class Slot {

	private final PropertyChangeSupport propertyChangeSupport;

	private final Agent agent;
	private String latitude;
	private String longitude;
	private String speed;
	private String isMetering;
	private String altitude;
	private boolean flying;

	private int appearTime;
	private int disappearTime;
	private double eta;
	private long radius;
	private int simTime =0;
	
	// to put in slotJ2D
	private Point2D.Double screenCoord;
	
	public Slot(Agent ag, String lat, String lon, String fly, String spd, String metering,String alt) {
		System.err.println(" !! creating SLOT with "+ag+ " lat="+lat+" lon="+lon+" fly="+fly+" speed="+spd+" metering="+metering+" alt="+alt);
		propertyChangeSupport = new PropertyChangeSupport(Slot.this);
		agent = ag;
		if(!lat.equals("") && lat != null)
			latitude = lat;
		else
			latitude = "0.0";
		if(!lon.equals("") && lon != null)
			longitude = lon;
		else
			longitude = "0.0";
		speed = spd;
		flying = new Boolean(fly);
		eta = 0.0;
		radius = 0;
		altitude=alt;
		isMetering = metering;
		appearTime = Integer.MAX_VALUE;
		disappearTime = 0;
		Set<Integer> timePoints = agent.getBeliefs().keySet();
		Iterator<Integer> it = timePoints.iterator();
		if(it.hasNext())
			it.next();
		while(it.hasNext()) {
			int time = it.next();
			if(disappearTime < time)
				disappearTime = time;
			if(appearTime > time)
				appearTime = time;
		}
		
		screenCoord = new Point2D.Double(0, 0);
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

	public double getTanLatitude() {
		return MathUtils.tanLatitude(Double.parseDouble(latitude));
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

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String newSpeed) {
		speed = newSpeed;
		propertyChangeSupport.firePropertyChange(SimulationProperties.SPEED_PPTY, null, speed);
	}

	public boolean isFlying() {
		return flying;
	}

	public void setFlying(boolean newFlying) {
		flying = newFlying;
		propertyChangeSupport.firePropertyChange(SimulationProperties.FLYING_PPTY, null, flying);
	}

	public int getAppearTime() {
		return appearTime;
	}

	public int getDisappearTime() {
		return disappearTime;
	}

	public void setETA(String newETA) {
		try {
			eta = Double.parseDouble(newETA);
		} catch(NumberFormatException e) {
			eta = Double.POSITIVE_INFINITY;
		}
		propertyChangeSupport.firePropertyChange(SimulationProperties.ETA_PPTY, null, eta);
	}
	
	public double getETA() {
		return eta;
	}
	
	public String getName() {
		return agent.getName();
	}

	public String isMetering() {
		return isMetering;
	}

	public void setMetering(String val) {
		isMetering = val;
		propertyChangeSupport.firePropertyChange(SimulationProperties.METERING_PPTY, null, isMetering);
	}
	
	public Point2D.Double getScreenCoord() {
		return screenCoord;
	}

	public void setScreenCoord(double x, double y) {
		screenCoord = new Point2D.Double(x, y);
	}
	
	public void setRadius(long newRadius) {
		radius = newRadius;
	}
	
	public long getRadius() {
		return radius;
	}

	public boolean updateBelief(final int time) {
		simTime = time;
		boolean update = false;
		String newLatitude = "";
		String newLongitude = "";
		String newSpeed = "";
		String newFlying = "";
		String newEta = "";
		String newMetering = "";
		List<BeliefUpdate> belUp = agent.getBeliefs().get(simTime);

		// use elseif
		if (belUp != null) {
			for (BeliefUpdate bu : belUp) {
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LATITUDE_PPTY)))
					newLatitude = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LONGITUDE_PPTY)))
					newLongitude = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.SPEED_PPTY)))
					newSpeed = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.FLYING_PPTY)))
					newFlying = bu.getValue();
				if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.ETA_PPTY)))
					newEta = bu.getValue();
				if(bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.METERING_PPTY)))
					newMetering = bu.getValue();
				if(bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.ALTITUDE_PPTY)))
					altitude = bu.getValue();
			}
			if (!newLatitude.equals("") && !newLatitude.equals(latitude)) {
				latitude = newLatitude;
				update = true;
			}
			if (!newLongitude.equals("") && !newLongitude.equals(longitude)) {
				longitude = newLongitude;
				update = true;
			}
			if (!newSpeed.equals("") && !newSpeed.equals(speed)) {
				speed = newSpeed;
				update = true;
			}
			if (!newFlying.equals("")) {
				setFlying(!flying);
				update = true;
			}
			if (!newEta.equals("")) {
				try {
					double arrival = Double.parseDouble(newEta);
					if(arrival != eta) {
						eta = arrival;
						update = true;
					}
				} catch (NumberFormatException e) {
				}
			}
			if (!newMetering.equals("") && !newMetering.equals(isMetering)) {
				isMetering = newMetering;
				update = true;
			}
			if (appearTime <= simTime && disappearTime > simTime) {
				flying = true;
				update = true;
			}
		} else {
			if (disappearTime < simTime && flying) {
				flying = false;
				update = true;
			} else if (appearTime > simTime && flying) {
				flying = false;
				update = true;
			}
		}
		if (update) {

			propertyChangeSupport.firePropertyChange(SimulationProperties.SLOT_UPDATED, null, null);
		}
		
		return update;
	}

	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

}
