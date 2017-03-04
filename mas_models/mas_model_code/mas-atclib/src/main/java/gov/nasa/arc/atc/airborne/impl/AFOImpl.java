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
********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.airborne.impl;

import gov.nasa.arc.atc.BearingIndicator;
import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.FlightPlanUtils;
import gov.nasa.arc.atc.utils.SimulationProperties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hamon
 * @author krantz
 */
public class AFOImpl implements AFO {

    private static final Logger LOGGER = Logger.getLogger(AFOImpl.class.getName());

    private final PropertyChangeSupport propertyChangeSupport;


    /*
     * Attributes
     */
    // Name of object
    private final String name;
    //
    // kts - nautical miles per hour
    private double airSpeed;
    // feet per second of ascent or descent
    private double verticalSpeed;
    // decimal degrees - positive if North, negative if South
    private double latitude;
    // decimal degrees - positive if East, negative if West
    private double longitude;
    // in feet
    private double altitude;
    // in decimal degrees
    private double bearing;

    //TODO remove since redundant, use a static method in utils
    // North, South, South-West, etc.
    private BearingIndicator headingEnum;

    private FlightParameters initialParameters;
    //make final ??
    private FlightPlan afoFlightPlan;
    private final int startTime;
    //
    private int simuTime;

    // Is the airplane flying or on ground
    private int afoStatus;

    //synchronization fields for Brahms

    private double eTA;
    private Object controller;
    private int metering;
    private final int timeIncrement;

    //TODO modify constructors
    private int departureTime = -1;

    /*
     * Constructors
     */

    /**
     * Constructor, creates a new AircraftImpl initialized with the
     * specified values. Use for calculating vertical speeds.
     *
     * @param afoName the afo name
     * @param speed   is air speed in nautical miles per hour
     * @param alt     is altitude
     */
    public AFOImpl(String afoName, int speed, double alt) {
        this(afoName, speed, 0, 0.0, 0.0, alt, 0.0);
    }

    /**
     * Constructor, creates a new AircraftImpl initialized with the
     * specified values.
     *
     * @param afoName the afo name
     * @param speed   is air speed in nautical miles per hour
     * @param lat     is latitude position
     * @param lng     is longitude position
     * @param bearing is bearing in decimal degrees
     */
    public AFOImpl(String afoName, double speed, double lat, double lng, double bearing) {
        this(afoName, speed, 0, lat, lng, 0.0, bearing);
    }

    /**
     * Constructor, creates a new AircraftImpl initialized with the
     * specified values.
     *
     * @param afoName   the afo name
     * @param speed     is air speed in nautical miles per hour
     * @param vertSpeed is vertical speed in feet per second
     * @param lat       is latitude position
     * @param lng       is longitude position
     * @param alt       is altitude
     * @param bearing   is bearing in decimal degrees
     */
    @Deprecated
    public AFOImpl(String afoName, double speed, double vertSpeed, double lat, double lng, double alt, double bearing) {
        this(afoName, speed, vertSpeed, lat, lng, alt, bearing, 0);
    }

    public AFOImpl(String afoName, double speed, double vertSpeed, double lat, double lng, double alt, double bearing, int startTime) {
        propertyChangeSupport = new PropertyChangeSupport(AFOImpl.this);
        initialParameters = new FlightParameters(new Position(lat, lng, alt), speed, vertSpeed, bearing, -1);
        name = afoName;
        airSpeed = speed;
        setVerticalSpeed(vertSpeed);
        longitude = lng;
        latitude = lat;
        altitude = alt;
        setBearing(bearing);
        simuTime = 0;
        LOGGER.log(Level.FINE, "creating AFO {0}", AFOImpl.this);
        timeIncrement = Constants.TIME_INCREMENT;
        this.startTime = startTime;

        //
//        if(departureTime<=startTime && ){
//
//        }
    }


    /**
     * @param afoName          the afo name
     * @param speed            the afo air speed in [kt]
     * @param vertSpeed        the afo vertical speed in [ft/s]
     * @param lat              the afo latitude in [degree]
     * @param lng              the afo longitude in [degree]
     * @param alt              the afo altitude in [ft]
     * @param bearing          the afo bearing in [degree]
     * @param headingEnum      the heading enum corresponding to te heading
     * @param flightPlan       the afo flight plan
     * @param currentSegment   the afo current segment flown in its flight plan
     * @param status           the afo status
     * @param timeStamp        the creation time stamp ?? // todo check
     * @param startTime        the afo start time in the simulation
     * @param eTA              the afo ETA
     * @param controller       the controller controlling the afo
     * @param is_Metering      the afo metering status
     * @param simTimeIncrement the simulation time increment
     * @param flightParams     the afo initial flight parameters
     */
    public AFOImpl(String afoName,
                   double speed,
                   double vertSpeed,
                   double lat,
                   double lng,
                   double alt,
                   double bearing,
                   int headingEnum,
                   FlightPlan flightPlan,
                   int currentSegment,
                   int status,
                   int timeStamp,
                   int startTime,
                   double eTA,
                   Object controller,
                   int is_Metering,
                   int simTimeIncrement,
                   FlightParameters flightParams) {
        propertyChangeSupport = new PropertyChangeSupport(AFOImpl.this);
        name = afoName;
        airSpeed = speed;
        setVerticalSpeed(vertSpeed);
        longitude = lng;
        latitude = lat;
        altitude = alt;
        setBearing(bearing); //this should take care of the heading enum? 
        BearingIndicator.fromInt(headingEnum);
        setFlightPlan(flightPlan);
        FlightSegment fs = getFlightPlan().getSegment(currentSegment);
        getFlightPlan().setCurrentSegment(fs);
        setStatus(status);
        setSimulationTime(timeStamp);
        this.startTime = startTime;
        setETA(eTA);
        setController(controller);
        setMetering(is_Metering);
        this.timeIncrement = simTimeIncrement;
        if (flightParams == null) {
            initialParameters = new FlightParameters(new Position(lat, lng, alt),
                    speed, vertSpeed, bearing, status);
        } else {
            initialParameters = new FlightParameters(
                    new Position(flightParams.getPosition().getLatitude(),
                            flightParams.getPosition().getLongitude(),
                            flightParams.getPosition().getAltitude()),
                    flightParams.getAirSpeed(),
                    flightParams.getVerticalSpeed(),
                    flightParams.getHeading(),
                    flightParams.getStatus());
        }
    }// AircraftImpl
		

    /*
     * Methods
     */

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get nautical miles per hour aircraft is traveling.
     *
     * @return integer is air speed
     */
    @Override
    public double getAirSpeed() {
        if (airSpeed < 0) {
            throw new IllegalArgumentException("Air Speed is negative " + airSpeed);
        }
        return airSpeed;
    }// getAirSpeed

    /**
     * Store nautical miles per hour aircraft is traveling.
     *
     * @param speed is air speed
     */
    @Override
    public void setAirSpeed(double speed) {
        airSpeed = speed;
        propertyChangeSupport.firePropertyChange(SimulationProperties.SPEED_PPTY, simuTime, airSpeed);
    }// setVerticalSpeed

    /**
     * Get feet per second aircraft is climbing or descending.
     *
     * @return integer is vertical speed
     */
    @Override
    public double getVerticalSpeed() {
        return verticalSpeed;
    }// getVerticalSpeed

    /**
     * Store feet per second aircraft is climbing or descending.
     *
     * @param speed is vertical feet per second
     */
    @Override
    public final void setVerticalSpeed(double speed) {
        verticalSpeed = speed;
        propertyChangeSupport.firePropertyChange(SimulationProperties.VERTICAL_SPEED_PPTY, simuTime, verticalSpeed);
    }// setVerticalSpeed

    /**
     * Get stored latitude position
     *
     * @return latitude position
     */
    @Override
    public double getLatitude() {
        return latitude;
    }// getLatitude

    /**
     * Set latitude position
     *
     * @param lat the latitude position to set
     */
    @Override
    public void setLatitude(double lat) {
        latitude = lat;
        propertyChangeSupport.firePropertyChange(SimulationProperties.LATITUDE_PPTY, simuTime, latitude);
    }// setLatitude

    /**
     * Get stored longitude position
     *
     * @return longitude position
     */
    @Override
    public double getLongitude() {
        return longitude;
    }// getLongitude

    /**
     * Set longitude position
     *
     * @param lng is longitude position
     */
    @Override
    public void setLongitude(double lng) {
        longitude = lng;
        propertyChangeSupport.firePropertyChange(SimulationProperties.LONGITUDE_PPTY, null, longitude);
    }// setLongitude

    /**
     * Get feet above sea level aircraft is traveling.
     *
     * @return double is altitude in feet
     */
    @Override
    public double getAltitude() {
        return altitude;
    }// getAltitude

    /**
     * Set the altitude, feet above sea level, of aircraft.
     * Note: if input is less than zero, altitude will be set to zero.
     *
     * @param alt is altitude
     */
    @Override
    public void setAltitude(double alt) {
        if (alt < 0) {
            altitude = 0;
        } else {
            altitude = alt;
        }//end if
        propertyChangeSupport.firePropertyChange(SimulationProperties.ALTITUDE_PPTY, simuTime, altitude);
    }// setAltitude

    /**
     * Get heading, in decimal degrees, of aircraft
     *
     * @return double is bearing in decimal degrees
     */
    @Override
    public double getBearing() {
        return bearing;
    }// getBearing

    /**
     * Set heading, in decimal degrees, of aircraft
     *
     * @param heading is bearing in decimal degrees
     */
    @Override
    public final void setBearing(double heading) {
        if (heading < 0) {
            bearing = 360 + heading;
        } else {
            bearing = heading;
        }//end if
        headingEnum = BearingIndicator.getBearingIndicator(bearing);
        propertyChangeSupport.firePropertyChange(SimulationProperties.BEARING_PPTY, simuTime, headingEnum);
    }// setBearing

    /**
     * Get heading of aircraft
     *
     * @return BearingIndicator for heading of aircraft
     */
    @Override
    public BearingIndicator getBearingIndicator() {
        return headingEnum;
    }// getHeading

    /**
     * Get heading of aircraft like North, South, South-West, etc.
     *
     * @return String representation of heading
     */
    @Override
    public String getHeadingForDisplay() {
        return headingEnum.toDisplay();
    }// getHeading


    /**
     * Check whether other plane is approaching and crossing paths.
     * To check, new coordinates are determined after a nautical mile
     * of travel with its heading. If planes are separating, then
     * traveling away. If planes getting closer, then crossing.
     *
     * @param plane      to check if approaching and crossing paths
     * @param separation distance previously calculated between planes
     * @param speed      of other plane in knots
     * @return true, if cross paths and getting closer, otherwise, false
     */
    @Override
    @Deprecated
    public boolean isCrossing(AFO plane, double separation, int speed) {
        if (!isSameHeading(plane)) {
            throw new UnsupportedOperationException("Recode");
//            AFO oPlane = 
//                new AircraftImpl(getAirSpeed(), 
//                                 getLatitude(), 
//                                 getLongitude(), 
//                                 getBearing()); 
//            oPlane.updatePosition(1.0); // traveling 1 nautical mile
//            IAircraft oPlaneOther = 
//                new AircraftImpl(plane.getAirSpeed(), 
//                                 plane.getLatitude(), 
//                                 plane.getLongitude(), 
//                                 plane.getBearing());
//            oPlaneOther.updatePosition(1.0);
//            double dNewSeparation = oPlane.calculateLateralSeparation(oPlaneOther);
//        //LOG    LOGGER.debug("isCrossing - separation before: " + separation + 
//                    " after: " + dNewSeparation);
//            if (dNewSeparation < separation){
//                return true;
//            }//end if
        } else { // heading same so compare speeds to separation
            double dClosingSpeed;
            if (getAirSpeed() < speed) {
                dClosingSpeed = speed - getAirSpeed();
            } else {
                dClosingSpeed = getAirSpeed() - speed;
            }// end if
            //LOG     LOGGER.debug("isCrossing - closing speed: " + dClosingSpeed + " kts");
            if (dClosingSpeed > 0.0) { // could be same speed
                dClosingSpeed = dClosingSpeed / 60; // per minute
                if (dClosingSpeed > separation) {
                    return true;
                }//end if
            } // end if
        }//end if not same heading
        return false;
    }// isCrossing

    /**
     * Check if other plane is heading, approximately, in same direction.
     *
     * @param plane is other plane with bearing/heading set
     * @return true, if both planes heading same direction, otherwise, false
     */
    @Override
    public boolean isSameHeading(AFO plane) {
        return getBearingIndicator() == plane.getBearingIndicator();
    }// isSameHeading


    /**
     * Return string representation of aircraft position, i.e.,
     * latitude, longitude and altitude.
     *
     * @return string representation of aircraft position
     */
    @Override
    public String printPosition() {
        return "AircraftImpl - latitude:" +
                String.format("%.3f", getLatitude()) + ", longitude:" +
                String.format("%.3f", getLongitude()) + ", altitude:" +
                String.format("%.3f", getAltitude()) + " feet";
    }// printPosition

    /**
     * Return string representation of object.
     *
     * @return String representation of object
     */
    @Override
    public String toString() {
        return "AircraftImpl - " + airSpeed + " kts, vertical rate " +
                +verticalSpeed + " feet/sec at latitude:" +
                String.format("%.3f", latitude) + ", longitude:" +
                String.format("%.3f", longitude) + ", altitude:" +
                String.format("%.3f", altitude) + " feet heading " +
                String.format("%.3f", bearing) + " degrees " +
                getHeadingForDisplay();
    }// toString


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public int getStartTime() {
        return startTime;
    }


    @Override
    public int getDepartureTime() {
        return departureTime;
    }

    @Override
    public void setDepartureTime(int depTime) {
        departureTime = depTime;
    }

    @Override
    public int getSimulationTime() {
        return simuTime;
    }


    @Override
    public final void setFlightPlan(FlightPlan newFlightPlan) {
        afoFlightPlan = newFlightPlan;
        //System.out.println(getName()+" original init segment = "+newFlightPlan.getInitialSegment());
        //System.out.println(getName()+" init segment = "+afoFlightPlan.getInitialSegment());
        //TODO: fire
    }

    @Override
    public final FlightPlan getFlightPlan() {
        return afoFlightPlan;
    }


//	@Override
//	public void setSimTime(int time) {
//		//TODO: remove at some point
//		simuTime=time;
//	}

    @Override
    public void start() {
        //TODO: check if legal
        simuTime = startTime;
        if (afoFlightPlan != null) {
            afoFlightPlan.reset();
        }
        latitude = initialParameters.getLatitude();
        longitude = initialParameters.getLongitude();
        altitude = initialParameters.getAltitude();
        airSpeed = (int) initialParameters.getAirSpeed();
        verticalSpeed = (int) initialParameters.getVerticalSpeed();
        bearing = initialParameters.getHeading();
    }

    @Override
    public void reset() {
        simuTime = 0;
        afoFlightPlan.reset();
        latitude = initialParameters.getLatitude();
        longitude = initialParameters.getLongitude();
        altitude = initialParameters.getAltitude();
        airSpeed = (int) initialParameters.getAirSpeed();
        verticalSpeed = (int) initialParameters.getVerticalSpeed();
        bearing = initialParameters.getHeading();
        //TODO: investigate set status
    }


    /**
     * updatePosition. Method for updating the position of the aircraft given the constant time increment.
     *
     * @return the afo current flight parameters in the simulation
     */
    @Override
    public FlightParameters updatePosition() {
        FlightParameters flightParameters = AfoUtils.calculateNextStepParameters(this);
        Position position = flightParameters.getPosition();
        simuTime += timeIncrement;
        setBearing(flightParameters.getHeading());
        setLatitude(position.getLatitude());
        setLongitude(position.getLongitude());
        setAltitude(position.getAltitude());
        setVerticalSpeed((int) flightParameters.getVerticalSpeed());
        setAirSpeed((int) flightParameters.getAirSpeed());
        setStatus(flightParameters.getStatus());

        // These are required for the Brahms interface: ETA must be in sync with update position
        updateETA();
//		updateDTA();

        return flightParameters;
    }

    @Override
    public FlightParameters getInitialParameters() {
        return initialParameters;
    }

    @Override
    public int getStatus() {
        return afoStatus;
    }

    @Override
    public final void setStatus(int status) {
        afoStatus = status;
        //TODO: fire ppty change
    }

    @Override
    public boolean advanceToNextSegment() {
        return afoFlightPlan.advanceToNextSegment();
    }

    @Override
    public FlightParameters getParameters() {
        Position p = new Position(latitude, longitude, altitude);
        return new FlightParameters(p, airSpeed, verticalSpeed, bearing, afoStatus);
    }

    @Override
    public void setInitialParameters(FlightParameters initParam, String toWaypoint) {
        initialParameters = initParam;
        afoFlightPlan.setInitialSegment(afoFlightPlan.getSegmentEndingAt(toWaypoint));
    }


    //TO USE WITH CAUTION
    //TMP ???
    protected final void setSimulationTime(int time) {
        simuTime = time;
    }

    @Override
    public void setParameters(FlightParameters parameters) {
        setLatitude(parameters.getLatitude());
        setLongitude(parameters.getLongitude());
        setAirSpeed(parameters.getAirSpeed());
        setAltitude(parameters.getAltitude());
        setBearing(parameters.getHeading());
        setVerticalSpeed(parameters.getVerticalSpeed());
        setStatus(parameters.getStatus());
    }

    @Override
    public double getETA() {
        return eTA;
    }

    @Override
    public Object getController() {
        return controller;
    }

    @Override
    public final void setETA(double newETA) {
        eTA = newETA;
    }

    @Override
    public final void setController(Object controller) {
        this.controller = controller;
    }

    @Override
    public int isMetering() {
        return this.metering;
    }

    @Override
    public final void setMetering(int metering) {
        this.metering = metering;
    }

    //	@Override
//    private void updateBearing() {
//        ATCNode toNode = afoFlightPlan.getCurrentSegment().getToWaypoint();
//        bearing = AfoUtils.updateBearing(latitude, longitude, toNode.getLatitude(), toNode.getLongitude());
//
//    }

    //	@Override
    private void updateETA() {
        String lastWPTName = afoFlightPlan.getLastWaypoint().getName();
        eTA = FlightPlanUtils.calculateETATo(this, lastWPTName);
    }

    @Override
    public int getTimeIncrement() {
        return this.timeIncrement;
    }


}
