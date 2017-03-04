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

package gov.nasa.arc.atc.factories;

import gov.nasa.arc.atc.geography.FlightPlan;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ahamon
 *
 */
public class FlightPlanFactory {

	private static final Logger LOG = Logger.getGlobal();

	private FlightPlanFactory() {
		// private utility constructor
	}

	public static FlightPlan duplicateFPL(FlightPlan originalFPL) {
		LOG.log(Level.FINE," duplicating FPL: {0}", originalFPL);
		// TODO testing this
		// TODO override equals method
		FlightPlan clone = new FlightPlan(originalFPL.getAfoName());
		// add segments
		originalFPL.getSegments().forEach(seg -> clone.addSegment(seg));
		// add arrival airport and runway if exists
		if (originalFPL.getArrivalRunway() != null) {
			clone.setArrivalRunway(originalFPL.getArrivalRunway());
		}
		// add departure airport and runway if exists
		if (originalFPL.getDepartureRunway() != null) {
			clone.setDepartureRunway(originalFPL.getDepartureRunway());
		}
		clone.setInitialSegment(originalFPL.getInitialSegment());
		return clone;
	}

}
