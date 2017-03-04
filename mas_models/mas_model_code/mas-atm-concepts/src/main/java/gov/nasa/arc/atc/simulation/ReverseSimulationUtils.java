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

package gov.nasa.arc.atc.simulation;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.utils.Aerodynamics;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.CalculationTools;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.MathUtils;
import javafx.util.Pair;

public class ReverseSimulationUtils {
	
	private static final Logger LOG = Logger.getGlobal();

	private ReverseSimulationUtils() {
		// private utility constructor
	}
	
	public static void introduceDelay(final SimulationContext context, final int simulationTime, SlotTrajectory trail, int delayNeeded) {

		trail.recalculateTrajectoryWithDelayAt(simulationTime, delayNeeded);
//		LOG.log(Level.FINE, " $$ introduceDelay : simulationTime={0} SimulatedSlotTrajectory={1}  delayNeeded={2}", new Object[] { simulationTime, trail.getAFO().getName(), delayNeeded });
//		LOG.log(Level.FINE, " $$$ slot starts at ->  -> {0} ", trail.getAFO().getStartTime());
//		LOG.log(Level.FINE, " $$$ (simulationTime - delayNeeded) ->  -> {0} ", (simulationTime - delayNeeded));
//		
//		// 2 cases depending on whether the slot is flying at  t=(etaLead - delayNeed)
//		if (trail.getAFO().getStartTime() < (simulationTime - delayNeeded)) {
//			LOG.log(Level.FINE, " $ the slot started before (simulationTime - delayNeeded) ");
//			// the slot started before
//			// recalculate the trailing slot trajectory
//			LOG.log(Level.FINE, " $ !!! trail.getAFO().getStartTime() < (simulationTime - delayNeeded)");
//			trail.recalculateTrajectoryWithDelayAt(simulationTime, delayNeeded);
//		} else {
//			// the slot is not yet started in the simulation
//			//// if the slot departs in the simulations
//			if (context.getGeography().isADepartureNode(trail.getAFO().getFlightPlan().getInitialSegment().getFromWaypoint().getName())) {
//				LOG.log(Level.FINE, " $ !!! delay start ->  -> {0} ", trail.getAFO().getName());
//				trail.getAFO().setStartTime(trail.getAFO().getStartTime() + delayNeeded);
//			} else {
//				LOG.log(Level.FINE, " $ slot does not depart in the simulation ");
//				LOG.log(Level.FINE, " $ !!! ReverseSimulationUtils ->  -> {0} ", trail.getAFO().getName());
//				Pair<FlightParameters, String> initSetUp = ReverseSimulationUtils.modifyInitialParameters(context, trail.getSlotMarker(), delayNeeded);
//				LOG.log(Level.FINE, " $ !!! FlightParameters ->  -> {0} ", initSetUp.getKey());
//				LOG.log(Level.FINE, " $ !!! String ->  -> {0} ", initSetUp.getValue());
//				trail.getAFO().setInitialParameters(initSetUp.getKey(), initSetUp.getValue());
//			}
//			// recalculate from start since no part of the previous
//			// simulated trajectory is OK
//			trail.recalculateTrajectoryWithDelayAt(0, 0);
//		}
	}
	
	/// TODO be careful of duplicates in the algo

	public static Pair<FlightParameters, String> modifyInitialParameters(SimulationContext context, SimulatedSlotMarker slot, int delayToSimulate) {
		LOG.log(Level.FINE, " modifyInitialParameters of {0}, adding delay={1}", new Object[]{slot.getName(),delayToSimulate});

		
		slot.reset();
		LOG.log(Level.FINE, " ���� --> on segment: {0}", slot.getFlightPlan().getCurrentSegment().getSegmentName());
		//
		// variable to store the parameters
		Pair<FlightParameters, Double> calculatedParamters;
		boolean stepCompleted;

		// number of steps backwards calculated
		int nbStepsCalculated = 0;

		// retrieve the initial flight parameters
		FlightParameters initParameters = slot.getInitialParameters();
		calculatedParamters = new Pair<>(initParameters, 0.0);
		LOG.log(Level.FINE, " ���� --> initPosition: {0}", initParameters.getPosition());

		// get the first segment flown
		FlightSegment firstSegment = slot.getFlightPlan().getInitialSegment();
		LOG.log(Level.FINE, " ���� --> first segment flown: {0}", firstSegment.getSegmentName());

		// get the from waypoint of the segment, the one, the slot will fly backwards to
		ATCNode fromWPT = firstSegment.getFromWaypoint();
		LOG.log(Level.FINE, " ���� --> so arriving from: {0}", fromWPT.getName());

		// for the amount of delay needed
		// ... and as long as the from waypoint is not met
		// fly backwards
		for (int i = 0; i < delayToSimulate; i++) {
			//LOG.log(Level.INFO, " ���� --> Go back to: {0} ... loop:{1}", new Object[]{fromWPT.getName(),i});
			nbStepsCalculated++;
			calculatedParamters = reverseUpdatePosition(calculatedParamters.getKey(), 1, firstSegment);
			if (Double.doubleToRawLongBits(calculatedParamters.getValue()) != 0) {
				//System.err.println(" - *** loop: " + i + " BREAK");
				LOG.log(Level.FINE, " ���� --> Go back to: {0} ... loop:{1} BREAK", new Object[]{fromWPT.getName(),i});
				break;
			}
		}

		// if the step is complete
		stepCompleted = calculatedParamters.getValue() < MathUtils.EPSILON;
		LOG.log(Level.FINE, " ���� --> stepCompleted: {0}", stepCompleted);

		// calculate the remaining steps
		// TODO: !! we major the remaining time not flown on the last segment and set it to one second -> check if OK
		int nbRemainingSteps;
		if (stepCompleted) {
			nbRemainingSteps = delayToSimulate - nbStepsCalculated;
		} else {
			nbRemainingSteps = delayToSimulate - nbStepsCalculated + 1;
		}
		LOG.log(Level.FINE, " ���� --> nbRemainingSteps: {0}", nbRemainingSteps);

		// if the number of steps calculated corresponds to the delay, return
		// the slot's new initial parameters
		if (nbStepsCalculated == delayToSimulate && stepCompleted) {
			LOG.log(Level.FINE, " ���� --> DONE: ");
			return new Pair<>(calculatedParamters.getKey(), slot.getFlightPlan().getCurrentSegment().getToWaypoint().getName());
		}

		// otherwise calculate the remaining steps to fly backwards

		//// 1st case: there is a previous segment in the flight plan
		// !! set up the flight plan
		// -> recursive calculation
		boolean isThesePreviousSeg = slot.getFlightPlan().rewindToPreviousSegment();
//		LOG.log(Level.FINE, " ���� --> isThesePreviousSeg:: {0}", isThesePreviousSeg);

		if (isThesePreviousSeg) {
			FlightSegment seg = slot.getFlightPlan().getCurrentSegment();
			slot.getFlightPlan().setInitialSegment(seg);
			// TODO: check simulation time in AFO for -1 !!
			return modifyInitialParameters(context, slot, nbRemainingSteps);
		}

		//// 2nd case: the last waypoint is an airport
		// -> delay the slot at the airport
		if (context.getGeography().isADepartureNode(fromWPT.getName())) {
			if (stepCompleted) {
				slot.setDepartureTime(slot.getDepartureTime() + nbRemainingSteps);
			} else {
				slot.setDepartureTime(slot.getDepartureTime() + nbRemainingSteps+1);//?? +1
			}
			return new Pair<>(slot.getInitialParameters(), slot.getFlightPlan().getInitialSegment().getToWaypoint().getName());
		}
		//// 3rd case: no previous segment and last waypoint not an airport
		// -> keep last IAS, heading and fly backwards
		// Vertical speed is set to 0 to have the plane fly horizontally

		LOG.log(Level.FINE, "--> Last FPL paramters {0}", calculatedParamters);
		for (int i = 0; i < nbRemainingSteps; i++) {
			LOG.log(Level.FINE, "--> flying backwards {0} -> calculatedParamters:: {1}", new Object[]{i,calculatedParamters});
			calculatedParamters = reverseToGhostPosition(calculatedParamters.getKey(), 1);
		}
		// update the initial slot position
		LOG.log(Level.WARNING, "-->fromWPT {0} ", fromWPT);
		LOG.log(Level.WARNING, "-->fromWPT.getName() {0} ", fromWPT.getName());
		slot.setInitialParameters(calculatedParamters.getKey(),fromWPT.getName());

		return new Pair<>(calculatedParamters.getKey(), slot.getFlightPlan().getCurrentSegment().getToWaypoint().getName());
	}

	private static Pair<FlightParameters, Double> reverseUpdatePosition(FlightParameters lastParameters, int timeDelta, FlightSegment segment) {
		LOG.log(Level.FINE, "--> reverseUpdatePosition parameters {0}", lastParameters);
		/* Calculating distance and setting speed and bearing */
		double newAltitude = lastParameters.getAltitude() + lastParameters.getVerticalSpeed() * timeDelta;

		// true air speed
		double trueAirSpeed = Aerodynamics.trueAirSpeedISA(newAltitude * Constants.FT2METER, segment.getEndSpeed() * Constants.KTS2MS);
		trueAirSpeed = trueAirSpeed / Constants.KTS2MS;

		// Distance in NM
		double dDistanceToTravel = trueAirSpeed * timeDelta * Constants.SEC2HOURS;

		ATCNode previousWpt = segment.getFromWaypoint();
		double dDistToPreviousWpt = AfoUtils.getHorizontalDistance(lastParameters.getLatitude(), lastParameters.getLongitude(), previousWpt.getLatitude(), previousWpt.getLongitude());

		// test if overshoot the waypoint
		if (dDistanceToTravel > dDistToPreviousWpt) {
			// calculate time flown on the last segment
			double dLastSegTime = dDistToPreviousWpt / trueAirSpeed;
			// calculate time remaining to flight on the next segment
			double dTimeOnNewtSeg = timeDelta - dLastSegTime;
			FlightParameters finalPosition = new FlightParameters(new Position(previousWpt.getLatitude(), previousWpt.getLongitude(), segment.getdEndAltitude()), lastParameters.getAirSpeed(), lastParameters.getVerticalSpeed(), lastParameters.getHeading(), Constants.FINISHED);

			LOG.log(Level.FINE, " ���� --> @ previousWpt {0}", previousWpt);
			LOG.log(Level.FINE, " ���� --> @ waypoint with position {0}", finalPosition);
			return new Pair<>(finalPosition, dTimeOnNewtSeg);
		}

		double headingBack = AfoUtils.updateBearing(lastParameters.getLatitude(), lastParameters.getLongitude(), segment.getFromWaypoint().getLatitude(), segment.getFromWaypoint().getLongitude());
		// TODO de-comment previous
		double planeHeading = AfoUtils.updateBearing(lastParameters.getLatitude(), lastParameters.getLongitude(), segment.getToWaypoint().getLatitude(), segment.getToWaypoint().getLongitude());
		// System.err.println(" $$ -- >>> heading=" + heading + " reverseH=" + reverseHeading + " flying towards " + segment.getToWaypoint().getName() + " ... my lat is: " + lastParameters.getLatitude());
		double airSpeed = segment.getEndSpeed();

		// Angular Distance in radiant
		double dAngDist = CalculationTools.calculateAngularDistance(dDistanceToTravel, newAltitude);
		// New latitude in degrees
		double lat2 = CalculationTools.newLatitude(lastParameters.getLatitude(), dAngDist, headingBack);
		// New long. in degrees
		double long2 = CalculationTools.newLongitude(lastParameters.getLatitude(), lastParameters.getLongitude(), lat2, dAngDist, headingBack);

		Position newPosition = new Position(lat2, long2, newAltitude);
		Pair<FlightParameters, Double> result = new Pair<>(new FlightParameters(newPosition, airSpeed, lastParameters.getVerticalSpeed(), planeHeading, lastParameters.getStatus()), 0.0);
		LOG.log(Level.FINE, " ���� --> reverseUpdatePosition result {0}", lastParameters);
		return result;
	}

	public static Pair<FlightParameters, Double> reverseToGhostPosition(FlightParameters lastParameters, int flightTime) {
		FlightParameters result;
		FlightParameters previousStep;
		Position newPosition;
		//
		double trueAirSpeed;
		double dDistanceToTravel;
		double slotHeading;
		double headingBack;
		double dAngDist;
		double lat2;
		double long2;
		//
		result = lastParameters;
		previousStep = lastParameters;

		// LOOP
		for (int i = 0; i < flightTime; i++) {

			// true air speed
			trueAirSpeed = Aerodynamics.trueAirSpeedISA(previousStep.getAltitude() * Constants.FT2METER, previousStep.getAirSpeed() * Constants.KTS2MS);
			trueAirSpeed = trueAirSpeed / Constants.KTS2MS;

			// Distance in NM
			dDistanceToTravel = trueAirSpeed * Constants.TIME_INCREMENT * Constants.SEC2HOURS;

			slotHeading = previousStep.getHeading();
			headingBack = (slotHeading + 180.0) % 360.0;

			// Angular Distance in radiant
			dAngDist = CalculationTools.calculateAngularDistance(dDistanceToTravel, previousStep.getAltitude());
			// New latitude in degrees
			lat2 = CalculationTools.newLatitude(previousStep.getLatitude(), dAngDist, headingBack);
			// New long. in degrees
			long2 = CalculationTools.newLongitude(previousStep.getLatitude(), previousStep.getLongitude(), lat2, dAngDist, headingBack);

			newPosition = new Position(lat2, long2, previousStep.getAltitude());
			result = new FlightParameters(newPosition, previousStep.getAirSpeed(), previousStep.getVerticalSpeed(), slotHeading, previousStep.getStatus());
			previousStep = result;
		}
		return new Pair<>(result, 0.0);
	}

}
