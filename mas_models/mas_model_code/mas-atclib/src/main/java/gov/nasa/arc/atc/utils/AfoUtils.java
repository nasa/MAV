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
package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Position;

/**
 * 
 * @author hamon
 * @author krantz
 *
 */
public class AfoUtils {

    @Deprecated
	public static final String METER_FIX_WAYPOINTS = "KORRY FINSI VALRE";

    @Deprecated
	public static final String NO_METER_FIX_WAYPOINT = "N/A";

	private AfoUtils() {
		// private utility constructor
	}

	/**
	 * @param afo
	 * @param segment
	 * @return the average True Air Speed on a segment
	 */
	public static double calculateAverageSegmentTAS(AFO afo, FlightSegment segment) {		
		// Get the Indicated Air Speed (IAS) for the segment
		double segmentIAS = segment.getEndSpeed();
		// Start Altitude
		double startAlt;
		// End altitude of segment
		double endAlt = segment.getdEndAltitude();
		// If on current segment, start altitude is current altitude
		FlightSegment prevSegment = afo.getFlightPlan().getPreviousSegment(segment);
		if(prevSegment==null||afo.getFlightPlan().isCurrentSegment(segment)){
			startAlt = afo.getAltitude();
		} else {
			// Else it is the end altitude of the previous segment
			startAlt = prevSegment.getdEndAltitude();
		}
		double aveAlt = (startAlt + endAlt) / 2;
		return (Aerodynamics.trueAirSpeedISA(aveAlt * Constants.FT2METER, segmentIAS * Constants.KTS2MS)) / Constants.KTS2MS;
	}
	
	public static long calculateTimeToArrival(AFO afo){
		
		
		
		return -1;
	}
	 
	//
	// FROM AFO July 2016
	//
	
	
    /**
     * Calculate range tau which is the time, in seconds, until
     * closest point of approach (CPA) to another aircraft. Latitude
     * and longitude positions and air speeds (kts) of both aircrafts 
     * are required.
     * 
     * Range tau = slant range (nm) / closing speed (kts) 
     * 
     * @param afo1
     * @param afo2 is other aircraft with position and air speed
     * @return time, in seconds, until lateral closest point of approach
     */
    public static  int calculateRangeTau(AFO afo1,AFO afo2) {
        double dSlantRange = AfoUtils.calculateLateralSeparation(afo1,afo2);
        // check if planes heading towards each other at an angle or not
        double dClosingBearing;
        if (afo1.getBearing() > afo2.getBearing()){
          dClosingBearing = afo1.getBearing() - afo2.getBearing();
        } else {
          dClosingBearing = afo2.getBearing() - afo1.getBearing();
        }//end if
        // convert to radiant
        double dBearingInRadians = Constants.DEG_TO_RAD * dClosingBearing;
        // Closing Speed = SQRT((V1^2 * V2^2) - 2 * V1 * V2 * cosine(A))
        // where V1 & V2 is air speed, A is closing bearing
        double dClosingSpeed = Math.sqrt(
          ((afo1.getAirSpeed()*afo1.getAirSpeed()) + 
           (afo2.getAirSpeed()*afo2.getAirSpeed())) -
           (2 * afo1.getAirSpeed() * afo2.getAirSpeed() * Math.cos(dBearingInRadians))); 
//        if (LOGGER.isDebugEnabled()){
//          DecimalFormat df = new DecimalFormat("#.##");
//          LOGGER.debug("calculateRangeTau - bearing:" + dClosingBearing +
//              " range:" + df.format(dSlantRange) + 
//              " nm speed:" + df.format(dClosingSpeed) + " nm/hr");
//        }//end if debug
        if (dSlantRange > 0.0 && dClosingSpeed > 0.0){
            return (int) ((dSlantRange * 3600)/dClosingSpeed);
        }// end if
        return 0;
    }// calculateRangeTau
	
	
    /**
     * Calculate vertical speed to reach new altitude based on descent rule 
     * of thumb used to determine number of miles needed prior to arrive at 
     * new altitude. This is accomplished by dividing the altitude needed to 
     * be lost by 300, etc.
     * 
     * @param afo
     * @param toAltitude is altitude, in feet, to climb or descend to
     * @return
     */
    public static double calculateVerticalSpeed(AFO afo,double toAltitude) {
        double verticalSpeed;
        double dAltitudeDiff = toAltitude - afo.getAltitude();
       //LOG LOGGER.debug("calculateVerticalSpeed - " + 
        //LOG        String.format("%.3f", dAltitudeDiff) + " feet");
        // do not calculate vertical speed if altitude change is less than 10 feet
        if ((dAltitudeDiff > 0 && dAltitudeDiff > 10) || 
            (dAltitudeDiff < 0 && dAltitudeDiff < -10)){
          double dDistance = dAltitudeDiff / 300; // approx. nautical miles needed 
          if (dDistance < 0.0) {
              dDistance *= -1.0;
          }// end if
          // calculate time needed based on air speed
          //LOG  LOGGER.debug("calculateVerticalSpeed - " + getAirSpeed() + " kts");
          // speed * time = distance 
          double timeInSeconds = (dDistance / afo.getAirSpeed()) * 3600;
          //LOG  LOGGER.debug("calculateVerticalSpeed - " + 
          //LOG          String.format("%.3f", dDistance) + " nm in " + 
          //LOG          String.format("%.2f", timeInSeconds) + " seconds");
          if (timeInSeconds <= 0)
              timeInSeconds = 1;
          verticalSpeed = (int) (dAltitudeDiff / timeInSeconds);
        } else {
          verticalSpeed = 0;
        }// end if
        return verticalSpeed;
    }// calculateVerticalSpeed
    
    
    /**
     * Calculate vertical tau which is time, in seconds, until
     * closest point of approach (CPA) to another aircraft.
     * Altitudes, in feet, and vertical speeds (feet per second)
     * of both aircrafts are needed.
     * 
     * Vertical tau = altitude separation (feet) / 
     *                vertical closing speed (feet per second)
     *  
     * @param plane1
     * @param plane2 is other aircraft with altitude and vertical speed
     * @return time, in seconds, until vertical closest point of approach 
     */
    public int calculateVerticalTau(AFO plane1,AFO plane2){
        // altitude separation
        double dSeparation = AfoUtils.getAltitudeSeparation(plane1,plane2);
        //LOG LOGGER.debug("calculateVerticalTau - separation: " + dSeparation);
        if (dSeparation > 0.0){
            double iClosingVSpeed = getVerticalClosingSpeed(plane1,plane2);
            //LOG    LOGGER.debug("calculateVerticalTau - closing speed: " + iClosingVSpeed);
            if (iClosingVSpeed > 0){
                return (int) (dSeparation / iClosingVSpeed);
            } else { // assume some drift toward each other
              return (int) (dSeparation / 10);
            }//end if
        }//end if
        return 0;
    }// calculateVerticalTau
    
    
    /**
     * Get the closing vertical speed, in feet per second 
     * between aircraft. Closing vertical speed will depend on 
     * whether both aircraft are climbing or descending, 
     * one aircraft is climbing while other descending, etc.
     * 
     * @param afo2 is other aircraft with vertical speed
     * @return closing vertical speed in feet per second
     */
    private static double getVerticalClosingSpeed(AFO afo1,AFO afo2) {
        // either aircraft not climbing or descending
        if (afo1.getVerticalSpeed() == 0){
            return Math.abs(afo2.getVerticalSpeed());
        }// end if
        if (afo2.getVerticalSpeed() == 0){
            return Math.abs(afo1.getVerticalSpeed());
        }//end if
        // both climbing
        if (afo1.getVerticalSpeed() > 0 && afo2.getVerticalSpeed() > 0){
            if (afo1.getVerticalSpeed() > afo2.getVerticalSpeed()){
                return afo1.getVerticalSpeed() - afo2.getVerticalSpeed();
            } else {
                return afo2.getVerticalSpeed() - afo1.getVerticalSpeed();
            }// end if
        }//end if
        // both descending
        if (afo1.getVerticalSpeed() < 0 && afo2.getVerticalSpeed() < 0){
            if (afo1.getVerticalSpeed() < afo2.getVerticalSpeed()){
                return Math.abs(afo1.getVerticalSpeed() - afo2.getVerticalSpeed());
            } else {
                return Math.abs(afo2.getVerticalSpeed() - afo1.getVerticalSpeed());
            }// end if
        }//end if
        // current climbing, other descending
        if (afo1.getVerticalSpeed() > 0 && afo2.getVerticalSpeed() < 0){
            return Math.abs(afo1.getVerticalSpeed() + afo2.getVerticalSpeed());
        }//end if
        // current descending, other climbing
        if (afo1.getVerticalSpeed() < 0 && afo2.getVerticalSpeed() > 0){
            return Math.abs(afo1.getVerticalSpeed() + afo2.getVerticalSpeed());
        }//end if
        return 0;
    }// getVerticalClosingSpeed
    
    //
    // END FROM AFO July 2016
    //
	
//	/**
//	 * calculateSegmentTraverseTime. Time to traverse segment in flightplan.
//	 * If segment specified is current segment, the remaining time will be calculated.
//	 * @param iSeg
//	 * @return double dSegTime. The time to traverse the segment.
//	 */
//	public double calculateSegmentTraverseTime(AFO afo,FlightSegment segment) {
//		double dSegDist = segment.getSegmentDistance();
//		if (iSeg == iCurrentSegment) {
//			double dFromLat = afo.getAircraft().getLatitude();
//			double dFromLong = afo.getAircraft().getLongitude();
//			double dToLat = segment.getToWaypoint().getLatitude();
//			double dToLong = segment.getToWaypoint().getLongitude();
////			dSegDist = getAircraft().getDistance(dToLat, dToLong);
//			dSegDist = CalculationTools.distanceFromTo(dFromLat, dFromLong, dToLat, dToLong);
//		}
//		double dSegSpeed = calculateAverageSegmentTAS(iSeg);
//		return (dSegDist/dSegSpeed)*Constants.HOURS2SEC;
//	}// END calculateSegmentTraverseTime
	
	
    /**
     * Calculates the distance from current position to input
     * latitude and longitude based on Haversine formula (see 
     * http://www.movable-type.co.uk/scripts/latlong.html#cosine-law)
     *
     * @param afo the {@link AFO}
     * @param latitude
     * @param longitude
     * @return distance from current position to new position
     */
    public static final double getDistance(AFO afo,double latitude, double longitude){
        return getHorizontalDistance(afo.getLatitude(), afo.getLongitude(), latitude, longitude);
    }// calculateDistance
	
    /**
     * Calculates the distance from current position to input
     * latitude and longitude based on Haversine formula (see 
     * http://www.movable-type.co.uk/scripts/latlong.html#cosine-law)
     *
     * @param latitude1 current latitude
     * @param longitude1 current longitude
     * @param latitude2 latitude to travel to 
     * @param longitude2 longitude to travel to
     * @return distance from current position to new position
     */
    public static final double getHorizontalDistance(double latitude1, double longitude1,double latitude2, double longitude2){
        double dDistance;
        // convert degrees to radiant
        double dFromLatitudeInRadians = latitude1 * Constants.DEG_TO_RAD;
        double dToLatitudeInRadians = latitude2 * Constants.DEG_TO_RAD;
        double dFromLongitudeInRadians = longitude1* Constants.DEG_TO_RAD;
        double dToLongitudeInRadians = longitude2* Constants.DEG_TO_RAD;
        // ACOS(SIN(Lat1)*SIN(Lat2)+COS(Lat1)*COS(Lat2)*COS(Lng2-Lng1))*3437.7387
        
        double acosArg = Math.sin(dFromLatitudeInRadians) * Math.sin(dToLatitudeInRadians) +
                Math.cos(dFromLatitudeInRadians) * Math.cos(dToLatitudeInRadians) *
                Math.cos(dToLongitudeInRadians - dFromLongitudeInRadians);
        double acosAsgAbs = Math.abs(acosArg);
        
        // some operation end up with rounded values greater than 1 (in absolute)
        if(acosAsgAbs > 1 ){
        	if(acosAsgAbs-CalculationTools.EPSILON < 1){
        		return 0.0;
        	}else{
        		throw new IllegalStateException(" acosArg value is too big for acos function:: " + acosArg);
        	}
        }
        
        dDistance = Math.acos(acosArg);

        // convert back to nautical miles
        dDistance = dDistance * Constants.RAD_TO_NM;
        return dDistance;
    }
    
	
	/**
     * Calculate lateral separation, in nautical miles, to another
     * aircraft given latitude and longitude positions of both planes.
     * 
     * @param afo1
     * @param afo2 is other plane with latitude and longitude position
     * @return lateral separation in nautical miles
     */
    public static final double calculateLateralSeparation(AFO afo1, AFO afo2) {
        double dDistance = getDistance(afo1,afo2.getLatitude(), afo2.getLongitude());
       //LOG  LOGGER.debug("calculateLateralSeparation - " + dDistance + " nm");
        return dDistance;
    }// calculateLateralSeparation

    /**
     * Update heading of aircraft based on latitude and longitude
     * position aircraft will travel to.
     * 
     * @param latitude1
     * @param longitude1
     * @param latitude2 to travel to
     * @param longitude2 to travel to
     * @return
     */
    public static final double updateBearing(double latitude1, double longitude1,double latitude2, double longitude2){
      if (latitude1 != latitude2 || longitude1 != longitude2){
        double dFromLongitudeInRadians = longitude1 * Constants.DEG_TO_RAD;
        double dToLongitudeInRadians = longitude2 * Constants.DEG_TO_RAD;
//        double dLongChange = Math.abs(dFromLongitudeInRadians - dToLongitudeInRadians);
        double dLongChange = dToLongitudeInRadians - dFromLongitudeInRadians;
        double dFromLatitudeInRadians = latitude1 * Constants.DEG_TO_RAD;
        double dToLatitudeInRadians = latitude2 * Constants.DEG_TO_RAD;
        // y = sin(dLon) * cos(lat2)
        // x = (cos(lat1)* sin(lat2)) - (sin(lat1) * cos(lat2) * cos(dLon))
        // bearing = atan2(y, x);
        double dBearing = 
            Math.atan2(Math.sin(dLongChange) * Math.cos(dToLatitudeInRadians), 
            (Math.cos(dFromLatitudeInRadians)* Math.sin(dToLatitudeInRadians)) - 
            (Math.sin(dFromLatitudeInRadians)* Math.cos(dToLatitudeInRadians) 
                * Math.cos(dLongChange)));
        // convert to degrees
        dBearing = dBearing * Constants.RAD_TO_DEG;
        // make sure we don't go the long way
        if (dBearing < 0) {
          dBearing = 360 + dBearing;
        } else { // bearing greater than zero
            if (Math.sin(dToLongitudeInRadians - dFromLongitudeInRadians) < 0.0){
                dBearing = 360 - dBearing;
            }//end if
        }// end if
        //afo.setBearing(dBearing);
        return dBearing;
     //LOG   LOGGER.debug("updateBearing - "+ getBearing() + " " + getHeadingForDisplay());
      }// end if
      return 0;
    }// updateBearing
    
    
    /**
     * Update heading of aircraft based on latitude and longitude
     * position aircraft will travel to.
     * 
     * @param afo
     * @param latitude to travel to 
     * @param longitude to travel to
     * @return
     */
    public static final double updateBearing(AFO afo,double latitude, double longitude){
    	//TOD facto with upper method diff is in return if equals
      if (afo.getLatitude() != latitude || afo.getLongitude() != longitude){
        double dFromLongitudeInRadians = afo.getLongitude() * Constants.DEG_TO_RAD;
        double dToLongitudeInRadians = longitude * Constants.DEG_TO_RAD;
//        double dLongChange = Math.abs(dFromLongitudeInRadians - dToLongitudeInRadians);
        double dLongChange = dToLongitudeInRadians - dFromLongitudeInRadians;
        double dFromLatitudeInRadians = afo.getLatitude() * Constants.DEG_TO_RAD;
        double dToLatitudeInRadians = latitude * Constants.DEG_TO_RAD;
        // y = sin(dLon) * cos(lat2)
        // x = (cos(lat1)* sin(lat2)) - (sin(lat1) * cos(lat2) * cos(dLon))
        // bearing = atan2(y, x);
        double dBearing = 
            Math.atan2(Math.sin(dLongChange) * Math.cos(dToLatitudeInRadians), 
            (Math.cos(dFromLatitudeInRadians)* Math.sin(dToLatitudeInRadians)) - 
            (Math.sin(dFromLatitudeInRadians)* Math.cos(dToLatitudeInRadians) 
                * Math.cos(dLongChange)));
        // convert to degrees
        dBearing = dBearing * Constants.RAD_TO_DEG;
        // make sure we don't go the long way
        if (dBearing < 0) {
          dBearing = 360 + dBearing;
        } else { // bearing greater than zero
            if (Math.sin(dToLongitudeInRadians - dFromLongitudeInRadians) < 0.0){
                dBearing = 360 - dBearing;
            }//end if
        }// end if
        //afo.setBearing(dBearing);
        return dBearing;
     //LOG   LOGGER.debug("updateBearing - "+ getBearing() + " " + getHeadingForDisplay());
      }// end if
      return afo.getBearing();
    }// updateBearing
	
	/**
	 * updatePosition. Method for updating the position of the aircraft given a time increment.
	 * 
	 * @param afo the {@link AFO} to calculate the new parameters for
     * @return
   	 */
	public static FlightParameters calculateNextStepParameters(AFO afo) {
		/* Calculating distance and setting speed and bearing */
		double trueAirSpeed = Aerodynamics.trueAirSpeedISA(afo.getAltitude()*Constants.FT2METER,afo.getAirSpeed()*Constants.KTS2MS);
		trueAirSpeed = trueAirSpeed/Constants.KTS2MS;
		
		// Distance in NM
		double dDistanceToTravel = trueAirSpeed*afo.getTimeIncrement()*Constants.SEC2HOURS;
		
		FlightSegment currentSegment = afo.getFlightPlan().getCurrentSegment();
		ATCNode nextWpt = currentSegment.getToWaypoint();
		double dDistToNextWpt = getDistance(afo,nextWpt.getLatitude(), nextWpt.getLongitude());
		
		double airspeed = afo.getAirSpeed();
		
		// test if overshoot the waypoint
		if(dDistanceToTravel>dDistToNextWpt){
			// calculate time flown on the last segment
			double dLastSegTime = dDistToNextWpt/trueAirSpeed;
			// calculate time remaining to flight on the next segment
			double dTimeOnNewtSeg = afo.getTimeIncrement()-dLastSegTime;
			
			// proceed to next segment in the flightPlan
			boolean toNextWP = afo.advanceToNextSegment();
			// check if afo arrived
			if(!toNextWP){
				//we keep the last speed in order to account for the slot radius.
				//TODO: find a potential way around if it crashed ??
				return new FlightParameters(new Position(nextWpt.getLatitude(), nextWpt.getLongitude(), nextWpt.getAltitude()), afo.getAirSpeed(), 0, 0, Constants.FINISHED);
			}
			
			trueAirSpeed = Aerodynamics.trueAirSpeedISA(afo.getAltitude()*Constants.FT2METER,afo.getAirSpeed()*Constants.KTS2MS);
			trueAirSpeed = trueAirSpeed/Constants.KTS2MS;
			
			// Distance in NM
			dDistanceToTravel = trueAirSpeed*dTimeOnNewtSeg*Constants.SEC2HOURS;
			currentSegment = afo.getFlightPlan().getCurrentSegment();
			nextWpt = currentSegment.getToWaypoint();
			// TODO: may need to revisit for the metering.
			airspeed = currentSegment.getEndSpeed(); 
			if (afo.isMetering() == 1) {
				airspeed -= 40;
				System.err.println("TODO fix this !!!!!! air speed hard coded");
			}
		}
		double heading = updateBearing(afo,nextWpt.getLatitude(), nextWpt.getLongitude());
		//double airSpeed =currentSegment.getEndSpeed();
		

		
		// If plane is not finished, update position
			double dTimeToNextWpt = FlightPlanUtils.calculateETATo(afo,nextWpt.getName());
			double segEndAlt = currentSegment.getdEndAltitude();
			double currentAlt = afo.getAltitude();
			double vSpeed = (segEndAlt-currentAlt)/dTimeToNextWpt;
//			setVerticalSpeed((int) vSpeed);
//			setAltitude(currentAlt + (vSpeed * Constants.TIME_INCREMENT));
			
			/*
			 * Updating position
			 */
			// Altitude in feet
			//TODO: recalculate the correct altitude
			//currentAlt + (vSpeed * Constants.TIME_INCREMENT)
			double dAltitude = afo.getAltitude();
			// Bearing in degrees
			double dBearing =  afo.getBearing();
			// Latitude in degrees
			double lat1 =  afo.getLatitude();
			// Long. in degrees
			double long1 =  afo.getLongitude();
			// Angular Distance in radiant
			double dAngDist = CalculationTools.calculateAngularDistance(dDistanceToTravel, dAltitude);
			// New latitude in degrees
			double lat2 = CalculationTools.newLatitude(lat1, dAngDist, dBearing);
			// New long. in degrees
			double long2 = CalculationTools.newLongitude(lat1, long1, lat2, dAngDist, dBearing);

			Position newPosition = new Position(lat2, long2, dAltitude+vSpeed);
			return new FlightParameters(newPosition, airspeed, vSpeed, heading, afo.getStatus());		
	}

	
    /**
     * Get altitude separation, in feet, between aircraft.
     * 
     * @param afo1 the first aircraft
     * @param afo2 is other aircraft to get altitude in feet
     * @return altitude separation, in feet, between aircraft
     */
    public static double getAltitudeSeparation(AFO afo1, AFO afo2) {
        double dSeparation;
        if (afo1.getAltitude() > afo2.getAltitude()){
            dSeparation = afo1.getAltitude() - afo2.getAltitude();
        } else {
            dSeparation = afo2.getAltitude() - afo1.getAltitude();
        }//end if
        return dSeparation;
    }// getAltitudeSeparation
	


    /**
     * Calculate increment to latitude per increment, rate in seconds, given 
     * distance to next position. Time of travel will be speed / distance.
     * 
     * @param afo the {@link AFO}
     * @param latitude is decimal degrees position to travel to
     * @param travelTime in seconds to destination latitude
     * @param rate is number of seconds per increment
     * @return increment, in decimal degrees, per second to latitude
     */
    public static double calculateLatitudeIncrement(AFO afo, double latitude, int travelTime, int rate) {
      double dLatInc ;
      dLatInc = latitude - afo.getLatitude();
      double dTime = 0.0;
      if (travelTime != 0 && rate != 0){
        dTime = travelTime / rate;
      }//end if
      if (dLatInc != 0.0 && dTime > 0.0){ // latitude could be negative going South
        dLatInc = dLatInc / dTime;
      }//end if
   //LOG   LOGGER.debug("calculateLatitudeIncrement - latitude: " + dLatInc + " per second");
      return dLatInc;
    }// calculateLatitudeIncrement
    
    /**
     * Calculate increment to longitude per second given distance to next
     * position. Time of travel will be speed / distance.
     * 
     * @param afo the {@link AFO}
     * @param longitude is decimal degrees position to travel to
     * @param travelTime in seconds to destination latitude
     * @param rate is number of seconds
     * @return increment, in decimal degrees, per second to longitude
     */
    public static double calculateLongitudeIncrement(AFO afo, double longitude, int travelTime, int rate){
      double dLongInc ;
      // 1st calculate duration of travel
      double dTime = 0.0;
      if (travelTime != 0 && rate != 0){
        dTime = travelTime / rate;
      }//end if
    //LOG  LOGGER.debug("calculateLongitudeIncrement - time " + dTime + " seconds");
      dLongInc = longitude - afo.getLongitude();
      if (dLongInc != 0.0 && dTime > 0.0){ // longitude could be negative going West
        dLongInc = dLongInc / dTime;
      }//end if
    //LOG  LOGGER.debug("calculateLongitudeIncrement - longitude: " + dLongInc + " per second");
      return dLongInc;
    }// calculateLongitudeIncrement




    /*
    NEW on October 2016 (TODO: remove coment)
     */

    public static double getDTA(AFO afo){
        String lastWPTName = afo.getFlightPlan().getLastWaypoint().getName();
        return FlightPlanUtils.calculateDistanceTo(afo, lastWPTName);
    }

	
}
