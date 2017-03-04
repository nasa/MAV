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
package gov.nasa.arc.atc;

import gov.nasa.arc.atc.geography.Position;

/**
 * 
 * @author hamon
 *
 */
public class FlightParameters {

	private final Position position;

	private final double airSpeed;
	private final double verticalSpeed;
	private final double heading;
	private final int status;

	/**
	 * 
	 * @param flightPosition afo's position
	 * @param flightAirSpeed afo's air speed in [nautical miles per hour]
	 * @param flightVerticalSpeed afo's vertical speed in [feet per second]
	 * @param flightHeading afo's heading in [decimal degrees]
	 * @param flightStatus afo's status
	 */
	public FlightParameters(Position flightPosition, double flightAirSpeed, double flightVerticalSpeed, double flightHeading, int flightStatus) {
		position = flightPosition;
		airSpeed = flightAirSpeed;
		verticalSpeed = flightVerticalSpeed;
		heading = flightHeading;
		status = flightStatus;
	}

	/**
	 * 
	 * @return the afo position in 3D space
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * 
	 * @return the afo air speed in [nautical miles per hour]
	 */
	public double getAirSpeed() {
		return airSpeed;
	}

	/**
	 * 
	 * @return the afo vertical speed in [feet per second]
	 */
	public double getVerticalSpeed() {
		return verticalSpeed;
	}

	/**
	 * 
	 * @return the afo heading in [decimal degrees]
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * 
	 * @return the afo status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 
	 * @return the position's latitude
	 */
	public double getLatitude() {
		return position.getLatitude();
	}

	/**
	 * 
	 * @return the position's longitude
	 */
	public double getLongitude() {
		return position.getLongitude();
	}

	/**
	 * 
	 * @return the position's altitude in feet
	 */
	public double getAltitude() {
		return position.getAltitude();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FlightParameters : ");
		sb.append(position);
		sb.append(" IAS:");
		sb.append(airSpeed);
		sb.append(" VS:");
		sb.append(verticalSpeed);
		sb.append(" heading:");
		sb.append(heading);
		sb.append(" status:");
		sb.append(status);
		return sb.toString();
	}

	public static FlightParameters clone(FlightParameters p) {
		return new FlightParameters(new Position(p.getLatitude(), p.getLongitude(), p.getAltitude()), p.getAirSpeed(), p.getVerticalSpeed(), p.getHeading(), p.getStatus());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(airSpeed);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(heading);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + status;
		temp = Double.doubleToLongBits(verticalSpeed);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlightParameters other = (FlightParameters) obj;
		if (Double.doubleToLongBits(airSpeed) != Double.doubleToLongBits(other.airSpeed))
			return false;
		if (Double.doubleToLongBits(heading) != Double.doubleToLongBits(other.heading))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (status != other.status)
			return false;
        return Double.doubleToLongBits(verticalSpeed) == Double.doubleToLongBits(other.verticalSpeed);
	}

}
