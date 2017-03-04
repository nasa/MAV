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

import org.junit.Test;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.utils.Aerodynamics;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.CalculationTools;
import gov.nasa.arc.atc.utils.Constants;

/**
 * 
 * @author ahamon
 *
 */
public class ReverseGhostFlightTest {

	// @Test
	public void testSimpleReverseFlight() {
		Position initPosition = new Position(40, -70, 10000);
		FlightParameters initParameters = new FlightParameters(initPosition, 250, 0, 135, 1);
		System.err.println("> initParameters    " + initParameters);
		System.err.println("");
		//
		int delay = 250;
		FlightParameters delayedStartParamsAPPROX = ReverseSimulationUtils.reverseToGhostPosition(initParameters, delay).getKey();
		FlightParameters delayedStartParamsTRUE = reverseToTRUEGhostPosition(initParameters, 1, delay);

		double distanceApprox = AfoUtils.getHorizontalDistance(initParameters.getPosition().getLatitude(), initParameters.getPosition().getLongitude(), delayedStartParamsAPPROX.getPosition().getLatitude(), delayedStartParamsAPPROX.getPosition().getLongitude());
		double distanceTrue = AfoUtils.getHorizontalDistance(initParameters.getPosition().getLatitude(), initParameters.getPosition().getLongitude(), delayedStartParamsTRUE.getPosition().getLatitude(), delayedStartParamsTRUE.getPosition().getLongitude());
		System.err.println("> delayedStartParamsAPPROX    " + delayedStartParamsAPPROX);
		System.err.println("> delayedStartParamsTRUE      " + delayedStartParamsTRUE);
		System.err.println(">> distanceApprox " + distanceApprox);
		System.err.println(">> distanceTrue   " + distanceTrue);

		System.err.println("");

		FlightParameters delayedStartParamsApproxFLY = flightAtHeading(delayedStartParamsAPPROX, delay);
		FlightParameters delayedStartParamsTrueFLY = flightAtHeading(delayedStartParamsTRUE, delay);
		double distanceApproxFLY = AfoUtils.getHorizontalDistance(initParameters.getPosition().getLatitude(), initParameters.getPosition().getLongitude(), delayedStartParamsApproxFLY.getPosition().getLatitude(), delayedStartParamsApproxFLY.getPosition().getLongitude());
		double distanceTrueFly = AfoUtils.getHorizontalDistance(initParameters.getPosition().getLatitude(), initParameters.getPosition().getLongitude(), delayedStartParamsTrueFLY.getPosition().getLatitude(), delayedStartParamsTrueFLY.getPosition().getLongitude());

		System.err.println("> delayedStartParamsApproxFLY " + delayedStartParamsApproxFLY);
		System.err.println("> delayedStartParamsTrueFLY   " + delayedStartParamsTrueFLY);
		System.err.println(">> distanceApproxFLY " + distanceApproxFLY);
		System.err.println(">> distanceTrueFly   " + distanceTrueFly);

		System.err.println("");

		FlightParameters delayedStartParamsApproxFLYForward = forwardToTRUEGhostPosition(delayedStartParamsAPPROX, 1, delay);
		FlightParameters delayedStartParamsTrueFLYForward = forwardToTRUEGhostPosition(delayedStartParamsTRUE, 1, delay);
		double distanceApproxFLYForward = AfoUtils.getHorizontalDistance(initParameters.getPosition().getLatitude(), initParameters.getPosition().getLongitude(), delayedStartParamsApproxFLYForward.getPosition().getLatitude(), delayedStartParamsApproxFLYForward.getPosition().getLongitude());
		double distanceTrueFlyForward = AfoUtils.getHorizontalDistance(initParameters.getPosition().getLatitude(), initParameters.getPosition().getLongitude(), delayedStartParamsTrueFLYForward.getPosition().getLatitude(), delayedStartParamsTrueFLYForward.getPosition().getLongitude());

		System.err.println("> delayedStartParamsApproxFLYForward " + delayedStartParamsApproxFLYForward);
		System.err.println("> delayedStartParamsTrueFLYForward   " + delayedStartParamsTrueFLYForward);
		System.err.println(">> distanceApproxFLYForward " + distanceApproxFLYForward);
		System.err.println(">> distanceTrueFlyForward   " + distanceTrueFlyForward);
	}

	@Test
	public void testSimpleReverseFlightLoop() {
		Position initPosition = new Position(40, -70, 10000);
		FlightParameters initParameters = new FlightParameters(initPosition, 250, 0, 135, 1);
		System.err.println("> initParameters    " + initParameters);
		System.err.println("");
		//
		for (int i = 0; i < 150; i++) {
			int delay = 10 * i;
			System.err.println(" ! delay= " + delay);
			FlightParameters delayedStartParamsAPPROX = ReverseSimulationUtils.reverseToGhostPosition(initParameters, delay).getKey();
			FlightParameters delayedStartParamsTRUE = reverseToTRUEGhostPosition(initParameters, 1, delay);
			FlightParameters delayedStartParamsApproxFLYForward = forwardToTRUEGhostPosition(delayedStartParamsAPPROX, 1, delay);
			FlightParameters delayedStartParamsTrueFLYForward = forwardToTRUEGhostPosition(delayedStartParamsTRUE, 1, delay);
			double distanceApproxFLY = AfoUtils.getHorizontalDistance(initParameters.getPosition().getLatitude(), initParameters.getPosition().getLongitude(), delayedStartParamsApproxFLYForward.getPosition().getLatitude(), delayedStartParamsApproxFLYForward.getPosition().getLongitude());
			double distanceTrueFly = AfoUtils.getHorizontalDistance(initParameters.getPosition().getLatitude(), initParameters.getPosition().getLongitude(), delayedStartParamsTrueFLYForward.getPosition().getLatitude(), delayedStartParamsTrueFLYForward.getPosition().getLongitude());

			System.err.println(" " + i + " >> distanceApproxFLYForward " + distanceApproxFLY);
			System.err.println(" " + i + " >> distanceTrueFlyForward   " + distanceTrueFly);
		}

	}

	public static FlightParameters reverseToTRUEGhostPosition(FlightParameters lastParameters, int step, int flightTime) {
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
			dDistanceToTravel = trueAirSpeed * step * Constants.SEC2HOURS;

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

		return result;
	}

	public static FlightParameters forwardToTRUEGhostPosition(FlightParameters lastParameters, int step, int flightTime) {
		FlightParameters result;
		FlightParameters previousStep;
		Position newPosition;
		//
		double trueAirSpeed;
		double dDistanceToTravel;
		double slotHeading;
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
			dDistanceToTravel = trueAirSpeed * step * Constants.SEC2HOURS;

			slotHeading = previousStep.getHeading();

			// Angular Distance in radiant
			dAngDist = CalculationTools.calculateAngularDistance(dDistanceToTravel, previousStep.getAltitude());
			// New latitude in degrees
			lat2 = CalculationTools.newLatitude(previousStep.getLatitude(), dAngDist, slotHeading);
			// New long. in degrees
			long2 = CalculationTools.newLongitude(previousStep.getLatitude(), previousStep.getLongitude(), lat2, dAngDist, slotHeading);

			newPosition = new Position(lat2, long2, previousStep.getAltitude());
			result = new FlightParameters(newPosition, previousStep.getAirSpeed(), previousStep.getVerticalSpeed(), slotHeading, previousStep.getStatus());
			previousStep = result;
		}

		return result;
	}

	public static FlightParameters flightAtHeading(FlightParameters startParameters, int fightTime) {
		FlightParameters result;
		FlightParameters previousStep;
		Position newPosition;
		//
		double trueAirSpeed;
		double dDistanceToTravel;
		double dAngDist;
		double lat2;
		double long2;
		//
		result = startParameters;
		previousStep = startParameters;

		// LOOP
		for (int i = 0; i < fightTime; i++) {

			/* Calculating distance and setting speed and bearing */
			trueAirSpeed = Aerodynamics.trueAirSpeedISA(startParameters.getAltitude() * Constants.FT2METER, startParameters.getAirSpeed() * Constants.KTS2MS);
			trueAirSpeed = trueAirSpeed / Constants.KTS2MS;

			// Distance in NM
			dDistanceToTravel = trueAirSpeed * Constants.TIME_INCREMENT * Constants.SEC2HOURS;

			double dAltitude = previousStep.getAltitude();
			// Bearing in degrees
			double dBearing = previousStep.getHeading();
			// Latitude in degrees
			double lat1 = previousStep.getLatitude();
			// Long. in degrees
			double long1 = previousStep.getLongitude();
			// Angular Distance in radiant
			dAngDist = CalculationTools.calculateAngularDistance(dDistanceToTravel, dAltitude);
			// New latitude in degrees
			lat2 = CalculationTools.newLatitude(lat1, dAngDist, dBearing);
			// New long. in degrees
			long2 = CalculationTools.newLongitude(lat1, long1, lat2, dAngDist, dBearing);

			newPosition = new Position(lat2, long2, dAltitude + 0);
			result = new FlightParameters(newPosition, previousStep.getAirSpeed(), previousStep.getVerticalSpeed(), previousStep.getHeading(), 1);
			previousStep = result;
		}
		return result;
	}

}
