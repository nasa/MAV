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

package gov.nasa.arc.brahms.atmjava.activities;

import org.apache.log4j.Logger;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

/**
 * 
 * @author ahamon
 *
 */
public class LogAFOFlightPlan extends AbstractExternalActivity {

	public static final String AFO = "afoName";
	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String NUMBER_SEGMENTS = "nbSegments";
	public static final String HAS_SEGMENT = "hasSegment";

	private static final Logger LOGGER = Logger.getLogger(LogAFOFlightPlan.class);

	/**
	 * Executes the java activity action.
	 *
	 * @exception ExternalException if an unrecoverable error occurs.
	 */
	public void doActivity() throws ExternalException {
		try {
			Utils instance = Utils.getInstance();
			synchronized (instance) {
				IAgent iAFO = (IAgent) this.getParameterConcept("iAFO");
				String name = iAFO.getName();
				AFO afoObject = instance.getAFO(name);
				FlightPlan flightPlan = afoObject.getFlightPlan();
				//
				StringBuilder sb = new StringBuilder();
				sb.append("\n--- AFO FlightPlan ---");
				//
				sb.append("\n ").append(AFO).append("   ").append(name);
				//
				sb.append("\n ").append(FROM).append("   ").append(flightPlan.getDepartureRunway());
				//
				sb.append("\n ").append(TO).append("   ").append(flightPlan.getArrivalRunway());
				//
				sb.append("\n ").append(NUMBER_SEGMENTS).append("   ").append(flightPlan.getSegments().size());
				//
				flightPlan.getSegments().stream().forEach(segment -> appendSegment(segment, sb));
				//
				sb.append("\n--- END FlightPlan block ---");
				//
				LOGGER.info(sb.toString());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void appendSegment(FlightSegment segment, StringBuilder sb) {
		sb.append("\n ").append(HAS_SEGMENT).append("   ");
		sb.append(segment.getSegmentName()).append("   ");
		sb.append(segment.getFromWaypoint().getName()).append("   ");
		sb.append(segment.getToWaypoint().getName()).append("   ");
		sb.append(segment.getdEndAltitude()).append("   ");
		sb.append(segment.getEndSpeed()).append("   ");
	}

}
