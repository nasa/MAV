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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.SlotMarker;
import gov.nasa.arc.atc.airborne.TrafficType;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Waypoint;

import java.util.logging.Logger;

/**
 * @author krantz
 * @author hamon
 */
public class FlightPlanUtils {

    public static final Logger LOGGER = Logger.getGlobal();

    /**
     * The length of the ghost segment created a the beginning of the flight plan for delay calculations. in [NM]
     */
    public static final double GHOST_SEGMENT_LENGTH = 100;

    public static final String D_TO_END_FLIGHTPLAN = " ";

    private FlightPlanUtils() {
        // private utility constructor
    }

    public static Waypoint getTopOfDescent(FlightPlan flightPlan) {
        return null;
    }

    // //TODO attention !!!
    // /**
    // * @deprecated use instead
    // * @param flightPlan the flight plan to evaluate
    // * @return if is a departure
    // */
    // @Deprecated
    // public static boolean checkIfDepartureFlightPlan(FlightPlan flightPlan){
    // Waypoint start = flightPlan.getFirstWaypoint();
    // Waypoint second = flightPlan.getPath().get(0).getToWaypoint();
    // if(start!=null && second!=null){
    // String sStartpoint = start.getName();
    // // TODO startPoint
    // String sFirstWaypoint = start.getName();
    // if (sStartpoint.equals("LGA31") || sFirstWaypoint.equals("LGA31") ||
    // sStartpoint.equals("LGA22") || sFirstWaypoint.equals("LGA22") ||
    // sStartpoint.equals("LGA04") || sFirstWaypoint.equals("LGA04") ||
    // sStartpoint.equals("LGA13") || sFirstWaypoint.equals("LGA13"))
    // return true;
    // }
    // return false;
    // }

    /**
     * @param flightPlan
     * @param geography
     * @return
     * @deprecated use {@link AirportUtils} instead
     */
    @Deprecated
    public static TrafficType getTrafficType(FlightPlan flightPlan, ATCGeography geography) {
        // check if is departure
        for (Airport airport : geography.getAirports()) {
            if (AirportUtils.departsFrom(flightPlan, airport)) {
                return TrafficType.DEPARTURE;
            }
        }

        // check if arrival
        for (Airport airport : geography.getAirports()) {
            if (AirportUtils.arrivesAt(flightPlan, airport)) {
                return TrafficType.ARRIVAL;
            }
        }
        // otherwise is transit
        return TrafficType.TRANSIT;
    }

    /**
     * findMeterfix. Method for finding out what meterfix belongs to the current flightplan.
     *
     * @param afoFlightPlan
     * @return Name of meterfix as string. Or "N/A" if no meterfix was found.
     */
    public static final String findMeterfix(FlightPlan afoFlightPlan) {
        for (ATCNode waypoint : afoFlightPlan.getWayPoints()) {
            if (AfoUtils.METER_FIX_WAYPOINTS.contains(waypoint.getName())) {
                return waypoint.getName();
            }
        }
        return AfoUtils.NO_METER_FIX_WAYPOINT;
    }

    /**
     * calculateSegmentTraverseTime. Time to traverse segment in flightplan. If segment specified is current segment, the remaining time will be calculated.
     *
     * @param afo
     * @param segment      the segment to traverse
     * @param segmentIndex
     * @return double dSegTime. The time to traverse the segment.
     */
    public static final double calculateSegmentTraverseTime(AFO afo, FlightSegment segment, int segmentIndex) {
        double dSegDist;
        // if the segment is the one currently flown by the afo
        if (afo.getFlightPlan().isCurrentSegment(segment)) {
            double dFromLat = afo.getLatitude();
            double dFromLong = afo.getLongitude();
            double dToLat = segment.getToWaypoint().getLatitude();
            double dToLong = segment.getToWaypoint().getLongitude();
            // dSegDist = getAircraft().getDistance(dToLat, dToLong);
            dSegDist = CalculationTools.distanceFromTo(dFromLat, dFromLong, dToLat, dToLong);
        } else {
            dSegDist = segment.getSegmentDistance();
        }
        double dSegSpeed = calculateAverageSegmentTAS(afo, segment, segmentIndex);
        return (dSegDist / dSegSpeed) * Constants.HOURS2SEC;
    }// END calculateSegmentTraverseTime

    /**
     * calculateSegmentAverageSpeed. Method for calculating the averageSpeed in a specified flight segment. This method assumes true air speed (TAS) as a non-linear function of altitude.
     *
     * @param afo
     * @param segmentIndex TODO
     * @param segment,     number of the segment
     * @return double averageSpeed.
     */
    public static final double calculateAverageSegmentTAS(AFO afo, FlightSegment segment, int segmentIndex) {
        // Get the Indicated Air Speed (IAS) for the segment
        double segmentIAS = segment.getEndSpeed();
        // Start Altitude
        double startAlt;
        // End altitude of segment
        double endAlt = segment.getdEndAltitude();
        int iSeg = segmentIndex;
        // If on current segment, start altitude is current altitude
        if (iSeg == 0 || afo.getFlightPlan().isCurrentSegment(segment)) {
            startAlt = afo.getAltitude();
        } else {
            // Else it is the end altitude of the previous segment
            // if (afo.getName().equals("KLM5822") && (afo.getSimulationTime() > 220)) {
            // LOGGER.info("calculateAverageSegmentTAS(" + segment.getSegmentName() + ")");
            // LOGGER.info("iSeg = " + iSeg);
            // LOGGER.info("prior segment = " + afo.getFlightPlan().getSegment(iSeg - 1));
            // LOGGER.info("ending altitude" + afo.getFlightPlan().getSegment(iSeg - 1).getdEndAltitude());
            // }
            startAlt = afo.getFlightPlan().getSegment(iSeg - 1).getdEndAltitude();
        }
        double aveAlt = (startAlt + endAlt) / 2;
        double rv = (Aerodynamics.trueAirSpeedISA(aveAlt * Constants.FT2METER, segmentIAS * Constants.KTS2MS)) / Constants.KTS2MS;
        // if (afo.getName().equals("KLM5822") && (afo.getSimulationTime() > 220)) {
        // LOGGER.info("calculateAverageSegmentTAS(" + segment.getSegmentName() + ")");
        // LOGGER.info("iSeg = " + iSeg);
        // LOGGER.info("afo.getFlightPlan().isCurrentSegment() = " + afo.getFlightPlan().isCurrentSegment(segment));
        // LOGGER.info("startAlt = " + startAlt);
        // LOGGER.info("endAlt = " + endAlt);
        // LOGGER.info("aveAlt = " + aveAlt);
        // LOGGER.info("segmentIAS = " + segmentIAS);
        // LOGGER.info("trueAirSpeedTSA = " + rv);
        // }
        return rv;
    }// END calculateAverageSegmentTAS

    /**
     * @param afo
     * @param nameOfWpt, the name of the waypoint to which distance should be calculated. If left blank (" ", ie. D_TO_END_FLIGHTPLAN), the remaining distance of the flightplan will be returned.
     * @return double dTotDist. The distance along the track to the specified waypoint.
     */
    public static final double calculateDistanceInTrackTo(AFO afo, String nameOfWpt) {
        double dTotDist = 0.0;
        for (int i = afo.getFlightPlan().getCurrentSegmentIndex(); i < afo.getFlightPlan().getPath().size(); i++) {
            ATCNode nextWpt = afo.getFlightPlan().getSegment(i).getToWaypoint();
            /*
			 * If on current segment, calculate distance from aircraft position to next waypoint
			 */
            if (i == afo.getFlightPlan().getCurrentSegmentIndex()) {
                double dFromLat = afo.getLatitude();
                double dFromLong = afo.getLongitude();
                double dToLat = nextWpt.getLatitude();
                double dToLong = nextWpt.getLongitude();
                dTotDist = CalculationTools.distanceFromTo(dFromLat, dFromLong, dToLat, dToLong);
                // dTotDist = getAircraft().getDistance(dToLat, dToLong);
            } else {
                dTotDist += afo.getFlightPlan().getSegment(i).getSegmentDistance();
            }
            if (nameOfWpt.equals(nextWpt.getName())) {
                break;
            }
        }
        return dTotDist;
    }// calculateDistanceInTrackTo

    /**
     * calculateETATo. Method for calculating the ETA at a specific waypoint.
     *
     * @param afo
     * @param nameOfWpt
     * @return double dETA. ETA in seconds
     */
    public static double calculateETATo(AFO afo, String nameOfWpt) {
        double dETA;
        ATCNode nextWpt;
        // calculate the time to traverse the current waypoint
        nextWpt = afo.getFlightPlan().getCurrentSegment().getToWaypoint();
        dETA = FlightPlanUtils.calculateSegmentTraverseTime(afo, afo.getFlightPlan().getCurrentSegment(), afo.getFlightPlan().getCurrentSegmentIndex());
        // if the end of current segment corresponds to the desired waypoint
        // if (afo.getName().equals("KLM5822") && afo.getSimulationTime() > 220) {
        // LOGGER.info("dETA(" + afo.getFlightPlan().getCurrentSegmentIndex() + ") = " + dETA);
        // }
        if (nameOfWpt.equals(nextWpt.getName())) {
            return dETA;
        }

        // calculate the remaining time for each segment and add the value to the ETA
        for (int i = afo.getFlightPlan().getCurrentSegmentIndex() + 1; i < afo.getFlightPlan().getPath().size(); i++) {
            nextWpt = afo.getFlightPlan().getSegment(i).getToWaypoint();
            dETA += FlightPlanUtils.calculateSegmentTraverseTime(afo, afo.getFlightPlan().getSegment(i), i);
            // if (afo.getName().equals("KLM5822") && afo.getSimulationTime() > 220) {
            // LOGGER.info("dETA(" + i + ")* = " +
            // FlightPlanUtils.calculateSegmentTraverseTime(afo, afo.getFlightPlan().getSegment(i), i));
            // }

            if (nameOfWpt.equals(nextWpt.getName())) {
                return dETA;
            }
        }
        // if the waypoint is not find while moving forward to the end of the path
        return -1;
    }// END calculateETATo

    public static double calculateDistanceTo(AFO afo, String nameOfWpt) {
        FlightSegment segment = afo.getFlightPlan().getCurrentSegment();
        ATCNode nextWpt;
        double dFromLat = afo.getLatitude();
        double dFromLong = afo.getLongitude();
        double dToLat = segment.getToWaypoint().getLatitude();
        double dToLong = segment.getToWaypoint().getLongitude();
        double distance = CalculationTools.distanceFromTo(dFromLat, dFromLong, dToLat, dToLong);

        // calculate the remaining time for each segment and add the value to the ETA
        for (int i = afo.getFlightPlan().getCurrentSegmentIndex() + 1; i < afo.getFlightPlan().getPath().size(); i++) {
            nextWpt = afo.getFlightPlan().getSegment(i).getToWaypoint();
            distance += afo.getFlightPlan().getSegment(i).getSegmentDistance();
            if (nameOfWpt.equals(nextWpt.getName())) {
                return distance;
            }
        }
        // if the waypoint is not find while moving forward to the end of the path
        return -1;
    }

    public enum NodeNavigationType {
        TOWARDS, FROM, NOT_RELATED
    }

    public static NodeNavigationType getInitialNodeNavigationType(ATCNode node, FlightPlan flightPlan) {
        FlightSegment initSegment = flightPlan.getInitialSegment();
        int initSegmentIndex = flightPlan.getSegmentIndex(initSegment);
        // if at the origin of initial segment
        if (initSegment.getFromWaypoint().getName().equals(node.getName())) {
            return NodeNavigationType.FROM;
        }
        // if is from wpt on first path segment
        if (initSegmentIndex > 0 && flightPlan.getSegment(0).getFromWaypoint().getName().equals(node.getName())) {
            return NodeNavigationType.FROM;
        }
        for (int i = 0; i < initSegmentIndex; i++) {
            if (flightPlan.getSegment(i).getToWaypoint().getName().equals(node.getName())) {
                return NodeNavigationType.FROM;
            }
        }
        for (int i = initSegmentIndex; i < flightPlan.getPath().size(); i++) {
            if (flightPlan.getSegment(i).getToWaypoint().getName().equals(node.getName())) {
                return NodeNavigationType.TOWARDS;
            }
        }
        return NodeNavigationType.NOT_RELATED;
    }

    /**
     * findSegmentTopOfDecent. Method for finding the segment prior to Top Of Decent.
     *
     * @return segment number. returns current segment if no TOD found.
     */
    /**
     * public int findSegmentTopOfDecent() { double alt = 0; // Altitude of segment i double maxAlt = 0; // Max found altitude in flightplan for (int i = getiCurrentSegment(); i < flightSegments.size(); i++) { // for all segments between current and last... alt = flightSegments.get(i).getdEndAltitude(); // get altitude
     * of segment i if (alt >= maxAlt) { // save the highest altitude maxAlt = alt; } else if (alt < maxAlt) { // If descending.. return i-1; // return number of previous segment. } } return getiCurrentSegment(); // Else return current segment }
     **/

	/*
	 * 
	 */

    /**
     * Creates a modified version of the original flight plan to ease reverse time calculations in the simulation
     *
     * @param originalFPL
     * @param intialLatitude
     * @param initialLongitude
     * @param initialAltitude
     * @param initialAirSpeed
     * @return the modified flightPlane
     */
    public static FlightPlan createSlotModifiedFlightPlan(FlightPlan originalFPL, final double intialLatitude, final double initialLongitude, final double initialAltitude, final double initialAirSpeed) {
        FlightPlan modifiedFPL = new FlightPlan("modifiedFPL_" + originalFPL.getAfoName());

        originalFPL.getPath().stream().filter(segment -> (segment != originalFPL.getInitialSegment())).forEach(modifiedFPL::addSegment);

        final FlightSegment originalSeg = originalFPL.getInitialSegment();
        // create initial waypoint where the slot is
        final Waypoint initWPT = new Waypoint("INIT", intialLatitude, initialLongitude);
        // create segment before start
        final FlightSegment beforeInitSeg = new FlightSegment("beforeInit", originalFPL.getAfoName(), originalSeg.getFromWaypoint(), initWPT, initialAltitude, initialAirSpeed);
        // create segment after start
        final FlightSegment initSeg = new FlightSegment("init", originalFPL.getAfoName(), initWPT, originalSeg.getToWaypoint(), originalSeg.getdEndAltitude(), originalSeg.getEndSpeed());
        // create the ghost segment
        final FlightSegment ghostSeg = createGhostSegment(originalFPL.getPath().get(0), originalFPL.getAfoName());
        // add segments
        modifiedFPL.addSegment(ghostSeg);
        modifiedFPL.addSegment(beforeInitSeg);
        modifiedFPL.addSegment(initSeg);
        // initialize the flight plan
        modifiedFPL.setInitialSegment(initSeg);
        //
        return modifiedFPL;
    }

    /**
     * Creates a modified version of the original flight plan to ease reverse time calculations in the simulation
     *
     * @param atcGeography the geography used to determine airports and runways
     * @param slotMarker   the slot containing the flight plan
     * @return the modified flightPlane
     */
    public static FlightPlan createSlotModifiedFlightPlan(ATCGeography atcGeography, SlotMarker slotMarker) {
        FlightPlan originalFPL = slotMarker.getFlightPlan();
        FlightPlan modifiedFPL = new FlightPlan("modifiedFPL_" + slotMarker.getName());

        originalFPL.getPath().stream().filter(segment -> segment != originalFPL.getInitialSegment()).forEach(modifiedFPL::addSegment);

        final FlightSegment originalSeg = originalFPL.getInitialSegment();
        final ATCNode firstNode = originalSeg.getFromWaypoint();

        // only modify the first segment of the flight plan if it is not from an airport or runway
        if (atcGeography.isRunwayOrAirport(firstNode.getName())) {
            modifiedFPL.addSegment(originalSeg);
            // initialize the flight plan
            modifiedFPL.setInitialSegment(originalSeg);
        } else {
            // create initial waypoint where the slot is
////            final Waypoint initWPT = new Waypoint("INIT", slotMarker.getInitialParameters().getLatitude(), slotMarker.getInitialParameters().getLongitude());
//
//            double initSegmentHeading = CalculationTools.calculateBearing(originalSeg);
//


            final Waypoint initWPT = new Waypoint("INIT", slotMarker.getInitialParameters().getLatitude(), slotMarker.getInitialParameters().getLongitude());
            // create segment before start
            final FlightSegment beforeInitSeg = new FlightSegment("beforeInit", slotMarker.getName(), originalSeg.getFromWaypoint(), initWPT, slotMarker.getAltitude(), slotMarker.getAirSpeed());
            // create segment after start
            final FlightSegment initSeg = new FlightSegment("init", slotMarker.getName(), initWPT, originalSeg.getToWaypoint(), originalSeg.getdEndAltitude(), originalSeg.getEndSpeed());
            // create the ghost segment
            final FlightSegment ghostSeg = createGhostSegment(originalFPL.getPath().get(0), slotMarker.getName());
            // add segments
            modifiedFPL.addSegment(ghostSeg);
            modifiedFPL.addSegment(beforeInitSeg);
            modifiedFPL.addSegment(initSeg);
            // initialize the flight plan
            modifiedFPL.setInitialSegment(initSeg);
        }
        //
        return modifiedFPL;
    }

    private static FlightSegment createGhostSegment(FlightSegment firstSegment, String slotName) {
        double wpt1Latitude = firstSegment.getFromWaypoint().getLatitude();
        double wpt1Longitude = firstSegment.getFromWaypoint().getLongitude();
        double toWpt1Heading = CalculationTools.calculateBearing(wpt1Latitude, wpt1Longitude, firstSegment.getToWaypoint().getLatitude(), firstSegment.getToWaypoint().getLongitude());
        double headingBack = (toWpt1Heading + 180.0) % 360.0;
        // Angular Distance in radiant
        double dAngDist = CalculationTools.calculateAngularDistance(GHOST_SEGMENT_LENGTH, firstSegment.getdEndAltitude());
        // New latitude in degrees
        double latitudeOrigin = CalculationTools.newLatitude(wpt1Latitude, dAngDist, headingBack);
        // New long. in degrees
        double longitudeOrigin = CalculationTools.newLongitude(wpt1Latitude, wpt1Longitude, latitudeOrigin, dAngDist, headingBack);
        Waypoint ghostOrigin = new Waypoint("WPT_ORIGIN_" + slotName, latitudeOrigin, longitudeOrigin);
        return new FlightSegment("ORIGIN_" + slotName, slotName, ghostOrigin, firstSegment.getFromWaypoint(), firstSegment.getdEndAltitude(), firstSegment.getEndSpeed());
    }

}
