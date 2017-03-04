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

package gov.nasa.arc.atc.reports;

/**
 * 
 * @author ahamon
 *
 */
public class ReportItemProperties {

	public static final String SIMULATION_TIME = "simulationTime";
	public static final String NODE = "node";
	public static final String LEAD = "lead";
	public static final String LEAD_PARAMETERS = "leadParameters";
	public static final String LEAD_SEGMENT = "leadSegment";
	public static final String LEAD_ARRIVAL = "leadArrival";
	public static final String TRAIL = "trail";
	public static final String TRAIL_OLD_PARAMETERS = "trailOldParameters";
	public static final String TRAIL_NEW_PARAMETERS = "trailNewParameters";
	public static final String TRAIL_OLD_SEGMENT = "trailOldSegment";
	public static final String TRAIL_NEW_SEGMENT = "trailNewSegment";
	public static final String TRAIL_OLD_ARRIVAL = "trailOldArrival";
	public static final String TRAIL_NEW_ARRIVAL = "trailNewArrival";
	public static final String MIN_H_SEPARATION_REQUIRED = "minHSepReq";
	public static final String INITIAL_H_SEPARATION = "initialHSep";
	public static final String DELAY_NEEDED = "delayNeeded";
	
	
	private ReportItemProperties(){
		// private utility constructor
	}

}
