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
package gov.nasa.arc.atc.geography;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 
 * @author hamon
 *
 */
public class FlightSegment extends Segment{

	private static final Logger LOG = Logger.getGlobal();

	private final String name;
	private final String afoName;
	private final double dEndAltitude;
	private final double iEndSpeed;

	public FlightSegment(String segmentName, String afo, ATCNode fromWaypoint, ATCNode toWaypoint, double dNewEndAltitude, double iNewEndSpeed) {
		super(fromWaypoint, toWaypoint);
		name = segmentName;
		afoName = afo;
		dEndAltitude = dNewEndAltitude;
		iEndSpeed = iNewEndSpeed;
		LOG.log(Level.FINE, "Created Flight Segment {0}", FlightSegment.this);
	}

	public String getSegmentName() {
		return name;
	}

	
	public String getAfoName() {
		return afoName;
	}


	public double getdEndAltitude() {
		return dEndAltitude;
	}

	public double getEndSpeed() {
		return iEndSpeed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FlightSegment: ");
		sb.append(getSegmentName());
		sb.append(" from: " );
		sb.append(getFromWaypoint());
		sb.append(" -> To: ");
		sb.append(getToWaypoint());
		return sb.toString();
	}
}
