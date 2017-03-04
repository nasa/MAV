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

package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.airborne.SlotMarker;
import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.simulation.TimedFlightParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ahamon
 */
public class SlotUpdateUtils {

    private static final double PRECISION = 0.2;

    private SlotUpdateUtils() {
        // private utility constructor
    }


    public static List<TimedFlightParameters> calculateReferenceTrajectory(ATCGeography geography, SlotMarker slotMaker, FlightPlan modifiedFlightPlan, int simulationDuration) {


        FlightSegment firstInitSeg = modifiedFlightPlan.getInitialSegment();
        // !! only applied to arrivals
        final List<TimedFlightParameters> refTraj = new ArrayList<>();

        // get initial slot state
        final FlightParameters initialParam = slotMaker.getInitialParameters();

        final String firstWPT = firstInitSeg.getFromWaypoint().getName();
        final String initToWpt = firstInitSeg.getToWaypoint().getName();

        boolean takesOffInSim = geography.isRunwayOrAirport(firstWPT);

        // set state to origin
        FlightSegment origSeg = modifiedFlightPlan.getPath().get(0);
        Position oriP = new Position(origSeg.getFromWaypoint().getLatitude(), origSeg.getFromWaypoint().getLongitude(), takesOffInSim ? firstInitSeg.getFromWaypoint().getAltitude() : origSeg.getdEndAltitude());
        double toWptHeading = CalculationTools.calculateBearing(origSeg.getFromWaypoint().getLatitude(), origSeg.getFromWaypoint().getLongitude(), origSeg.getToWaypoint().getLatitude(), origSeg.getToWaypoint().getLongitude());
        FlightParameters originParam = new FlightParameters(oriP, origSeg.getEndSpeed(), 0, toWptHeading, Constants.IS_FLYING);
        modifiedFlightPlan.setInitialSegment(origSeg);

        // creating a reference slot to keep the slot input untouched
        SlotMarker refSlotMarker = new SimulatedSlotMarker("ref_"+slotMaker.getName(),oriP,origSeg.getEndSpeed(), 0, toWptHeading,0,0, Constants.IS_FLYING);
        refSlotMarker.setDepartureTime(0);
        refSlotMarker.setFlightPlan(modifiedFlightPlan);

        // calculate reference trajectory
        boolean isCompleted = false;
        // loop while has not reached departure position in simulation
        refTraj.add(new TimedFlightParameters(0, originParam, initToWpt));
        int simTime = 1;

        boolean hasReachedInitialPositionInSim = false;

        if(!takesOffInSim) {
            // only if the plane is already flying
            while (!hasReachedInitialPositionInSim) {
                final FlightParameters p = refSlotMarker.updatePosition();
                final String toWName = refSlotMarker.getFlightPlan().getCurrentSegment().getToWaypoint().getName();
                refTraj.add(new TimedFlightParameters(simTime, p, toWName));
                final double distance = AfoUtils.getHorizontalDistance(initialParam.getLatitude(), initialParam.getLongitude(), p.getLatitude(), p.getLongitude());
                hasReachedInitialPositionInSim = distance < PRECISION;
                simTime++;
            }
        }
        //
        int nbSteps = 0;
        while (nbSteps <= simulationDuration && !isCompleted) {
            final FlightParameters p = refSlotMarker.updatePosition();
            final String toWName = refSlotMarker.getFlightPlan().getCurrentSegment().getToWaypoint().getName();
            isCompleted = refSlotMarker.getFlightPlan().isCompleted();
            if (!isCompleted) {
                refTraj.add(new TimedFlightParameters(simTime, p, toWName));
            } else {
                refTraj.add(new TimedFlightParameters(simTime, p.getPosition(), p.getAirSpeed(), p.getVerticalSpeed(), p.getHeading(), Constants.FINISHED, toWName));
            }
            simTime++;
            nbSteps++;
        }
        //
        return refTraj;
    }



    public static int getFirstTimeAtCoordinates(double latitude, double longitude, List<TimedFlightParameters> parameters) {
        double distance;
        for (TimedFlightParameters param : parameters) {
            distance = AfoUtils.getHorizontalDistance(latitude, longitude, param.getLatitude(), param.getLongitude());
            if (distance < PRECISION) {
                return param.getTimeStamp();
            }
        }
        return -1;
    }

    public static int getLastTimeAtCoordinates(double latitude, double longitude, List<TimedFlightParameters> parameters) {
        double distance;
        int time = -1;
        for (TimedFlightParameters param : parameters) {
            distance = AfoUtils.getHorizontalDistance(latitude, longitude, param.getLatitude(), param.getLongitude());
            if (distance < PRECISION) {
                time = param.getTimeStamp();
            } else if (time >= 0) {
                return time;
            }
        }
        return -1;
    }

}
