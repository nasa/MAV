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

package gov.nasa.arc.atc;

import java.util.Collections;
import java.util.List;

import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Runway;

/**
 * 
 * @author ahamon
 *
 */
public class FlightPlanUpdateImpl implements FlightPlanUpdate {

	private final String afoName;
	private final Runway fromRunway;
	private final Runway toRunway;
	private final List<FlightSegment> flightSegments;

	public FlightPlanUpdateImpl(String afoName, Runway fromRunway, Runway toRunway, List<FlightSegment> flightSegments) {
		this.afoName = afoName;
		this.fromRunway = fromRunway;
		this.toRunway = toRunway;
		this.flightSegments = flightSegments;

	}

	@Override
	public String getAFOName() {
		return afoName;
	}

	@Override
	public Runway getFromRunway() {
		return fromRunway;
	}

	@Override
	public Runway getToRunway() {
		return toRunway;
	}

	@Override
	public List<FlightSegment> getFlightSegments() {
		return Collections.unmodifiableList(flightSegments);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" for ").append(afoName);
		sb.append("  from: ").append(fromRunway!=null? fromRunway.getName(): " N/A");
		sb.append("  to: ").append(toRunway!=null? toRunway.getName(): " N/A");
		return sb.toString();
	}

}
