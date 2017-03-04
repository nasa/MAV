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
* *******************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.airborne;

import java.beans.PropertyChangeListener;
import gov.nasa.arc.atc.BearingIndicator;
import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.FlightPlan;

/**
 * Based on the code from IAircraft of Chin Seah
 * 
 * @author hamon
 *
 */
public interface AFO {

	/**
	 * 
	 * @return the tag name of the instance
	 */
	String getName();

	/**
	 * Get nautical miles per hour aircraft is traveling.
	 * 
	 * @return integer is air speed
	 */
	double getAirSpeed();

	/**
	 * Store nautical miles per hour aircraft is traveling.
	 * 
	 * @param speed integer is air speed
	 */
	void setAirSpeed(double speed);

	/**
	 * Get feet per second aircraft is climbing or descending.
	 * 
	 * @return integer is vertical speed
	 */
	double getVerticalSpeed();

	/**
	 * Store feet per second aircraft is climbing or descending.
	 * 
	 * @param speed is vertical feet per second
	 */
	void setVerticalSpeed(double speed);

	/**
	 * Get stored latitude position
	 * 
	 * @return latitude position
	 */
	double getLatitude();

	/**
	 * Set latitude position
	 * 
	 * @param lat the latitude position to set
	 */
	void setLatitude(double lat);

	/**
	 * Get stored longitude position
	 * 
	 * @return longitude position
	 */
	double getLongitude();

	/**
	 * Set longitude position
	 * 
	 * @param lng is longitude position
	 */
	void setLongitude(double lng);

	/**
	 * Get feet above sea level aircraft is traveling.
	 * 
	 * @return double is altitude in feet
	 */
	double getAltitude();

	/**
	 * Set the altitude, feet above sea level, of aircraft.
	 * 
	 * @param alt is altitude
	 */
	void setAltitude(double alt);

	/**
	 * Get heading, in decimal degrees, of aircraft
	 * 
	 * @return double is bearing in decimal degrees
	 */
	double getBearing();

	/**
	 * Set heading, in decimal degrees, of aircraft
	 * 
	 * @param heading is bearing in decimal degrees
	 */
	void setBearing(double heading);

	/**
	 * Get heading of aircraft
	 *
	 * @return BearingIndicator for heading of aircraft
	 */
	BearingIndicator getBearingIndicator();

	/**
	 * Get heading of aircraft like North, South, South-West, etc.
	 *
	 * @return String representation of heading
	 */
	String getHeadingForDisplay();

	// adding APIs for synchornization with BrahmsAFO objects
	// this is used in the Utils.java of the dsas-java-src

	double getETA();

//	double getDTA();

	Object getController();

	int isMetering();

	void setETA(double eTA);

	void setController(Object controller);

	void setMetering(int metering);

	// these two methods are needed for getting planes initialized
	// correctly in the Brahms world.

	// it should use the current lat/long and the endwaypoint
	// of the current flight segment to compute the bearing
	// and the heading.
//	void updateBearing();

	// it should use the current position to compute
	// the ETA to the last point in the flight plan using SOP
//	void updateETA();

	// Distance to the airport
//	void updateDTA();

	// /**
	// * Calculate range tau which is the time, in seconds, until closest point of approach (CPA) to another aircraft. Latitude and longitude positions and air speeds (kts) of both aircrafts are required.
	// *
	// * Range tau = slant range (nm) / closing speed (kts) * 3600
	// *
	// * @param afo is other aircraft to determine CPA
	// * @return time, in seconds, until closest point of approach
	// */
	// int calculateRangeTau(AFO afo);

	// /**
	// * Calculate vertical speed to reach new altitude using the descent rule of thumb used to determine to descend in terms of the number of miles prior to arrive at new altitude. This is accomplished by dividing the altitude needed to be lost by 300, etc.
	// *
	// * @param toAltitude is altitude, in feet, to climb or descend to
	// */
	// void calculateVerticalSpeed(double toAltitude);
	//
	// /**
	// * Calculate vertical tau which is time, in seconds, until closest point of approach (CPA) to another aircraft. Altitudes, in feet, and vertical speeds (feet per second) of both aircrafts are needed.
	// *
	// * Vertical tau = altitude separation (feet) / vertical closing speed (feet per second)
	// *
	// * @param afo is other aircraft with altitude and vertical speed
	// * @return time, in seconds, until vertical closest point of approach
	// */
	// int calculateVerticalTau(AFO afo);

	/**
	 * Check whether other plane is approaching and crossing paths. To check, new coordinates are determined after a nautical mile of travel with its heading. If planes are separating, then traveling away. If planes getting closer, then crossing.
	 * 
	 * @param afo: to check if approaching and crossing paths
	 * @param separation distance previously calculated between planes
	 * @param speed of other plane in knots
	 * @return true, if cross paths and getting closer, otherwise, false
	 */
	@Deprecated
	boolean isCrossing(AFO afo, double separation, int speed);

	/**
	 * Check if other plane is heading, approximately, in same direction.
	 *
	 * @param afo is other plane with bearing/heading set
	 * @return true, if both planes heading same direction, otherwise, false
	 */
	boolean isSameHeading(AFO afo);

	/**
	 * Return string representation of aircraft position, i.e., latitude, longitude and altitude.
	 * 
	 * @return string representation of aircraft position
	 */
	String printPosition();

	/**
	 * 
	 * @param listener needing to subscribe to the aircraft updates
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * 
	 * @param listener needing to remove subscription to the aircraft updates
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

//	TrafficType getTrafficType();

//	void setTrafficType(TrafficType type);

	void setFlightPlan(FlightPlan flightPlan);

	FlightPlan getFlightPlan();

	/*

	 */

//	void setStartTime(int newStartTime);

	int getStartTime();

	void setDepartureTime(int departureTime);

	int getDepartureTime();


	/*

	 */

	// keep it here or in tss simulatedSlotMarker only?
	// void setSimTime(int time);

	FlightParameters getInitialParameters();

	/**
	 * This method sets the initial parameters to the new values and updates the flight plan accordingly
	 * 
	 * @param initParam new initial parameters
	 * @param toWaypoint the new waypoint the afo is flying towards
	 */
	void setInitialParameters(FlightParameters initParam, String toWaypoint);

	FlightParameters getParameters();

	FlightParameters updatePosition();

	void start();

	void reset();

	int getSimulationTime();

	int getStatus();

	void setStatus(int status);

	boolean advanceToNextSegment();

//	void setDeparture(boolean isDeparture);

//	boolean isDeparture();

	void setParameters(FlightParameters parameters);

	int getTimeIncrement();

}
