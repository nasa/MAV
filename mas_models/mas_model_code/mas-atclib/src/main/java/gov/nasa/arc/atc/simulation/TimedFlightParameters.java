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
package gov.nasa.arc.atc.simulation;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.Position;

/**
 * 
 * @author hamon
 *
 */
public class TimedFlightParameters extends FlightParameters {

	private final int timeStamp;

	// TODO: clean one new API stable
	private final String toWPT;

	@Deprecated
	public TimedFlightParameters(int time, Position flightPosition, double flightAirSpeed, double flightVerticalSpeed, double flightHeading, int flightStatus) {
		super(flightPosition, flightAirSpeed, flightVerticalSpeed, flightHeading, flightStatus);
		timeStamp = time;
		toWPT = "XX";
	}

	public TimedFlightParameters(int time, TimedFlightParameters parameter) {
		super(parameter.getPosition(), parameter.getAirSpeed(), parameter.getVerticalSpeed(), parameter.getHeading(), parameter.getStatus());
		timeStamp = time;
		toWPT = parameter.getToWPT();
	}

	@Deprecated
	public TimedFlightParameters(int time, FlightParameters parameter) {
		super(parameter.getPosition(), parameter.getAirSpeed(), parameter.getVerticalSpeed(), parameter.getHeading(), parameter.getStatus());
		timeStamp = time;
		toWPT = "XX";
	}

	public TimedFlightParameters(int time, Position flightPosition, double flightAirSpeed, double flightVerticalSpeed, double flightHeading, int flightStatus, String toWaypoint) {
		super(flightPosition, flightAirSpeed, flightVerticalSpeed, flightHeading, flightStatus);
		timeStamp = time;
		toWPT = toWaypoint;
	}

	public TimedFlightParameters(int time, FlightParameters parameter, String toWaypoint) {
		super(parameter.getPosition(), parameter.getAirSpeed(), parameter.getVerticalSpeed(), parameter.getHeading(), parameter.getStatus());
		timeStamp = time;
		toWPT = toWaypoint;
	}

	/**
	 * 
	 * @return a new instance of TimedFlightParameters with all attributes set to 0
	 */
	public static final FlightParameters emptyParameters() {
		return new FlightParameters(new Position(0, 0, 0), 0, 0, 0, 0);
	}

	/**
	 * 
	 * @return the time the parameters we calculed at
	 */
	public int getTimeStamp() {
		return timeStamp;
	}

	public String getToWPT() {
		return toWPT;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Timed Parameters : t=");
		sb.append(timeStamp);
		sb.append(" ");
		sb.append(getPosition());
		sb.append(" IAS:");
		sb.append(getAirSpeed());
		sb.append(" VS:");
		sb.append((int)getVerticalSpeed());
		sb.append(" heading:");
		sb.append((int)getHeading());
		sb.append(" status:");
		sb.append(getStatus());
		sb.append(" toWPT:");
		sb.append(toWPT);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + timeStamp;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimedFlightParameters other = (TimedFlightParameters) obj;
		if (Double.doubleToLongBits(getAirSpeed()) != Double.doubleToLongBits(other.getAirSpeed()))
			return false;
		if (Double.doubleToLongBits(getHeading()) != Double.doubleToLongBits(other.getHeading()))
			return false;
		if (getPosition() == null) {
			if (other.getPosition() != null)
				return false;
		} else if (!getPosition().equals(other.getPosition()))
			return false;
		if (getStatus() != other.getStatus())
			return false;
		if( Double.doubleToLongBits(getVerticalSpeed()) != Double.doubleToLongBits(other.getVerticalSpeed())){
			return false;
		}
        return timeStamp == other.timeStamp && super.equals(other);
	}

}
