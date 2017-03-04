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

package gov.nasa.arc.atc.brahms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.arc.atc.geography.FlightSegment;

/**
 * 
 * @author ahamon
 *
 */
public class BrahmsFlightPlan {
	
	private final Map<Integer, FlightSegment> plan;
	private final String planName;
	private final String afoName;
	
	public BrahmsFlightPlan(String name, String theAFOName){
		plan = new HashMap<>();
		planName = name;
		afoName = theAFOName;
	}
	
	
	public String getAfoName() {
		return afoName;
	}

	public String getName() {
		return planName;
	}
	
	public Map<Integer, FlightSegment> getSegmentIDs() {
		return Collections.unmodifiableMap(plan);
	}
	
	public void addSegment(int id, FlightSegment segment){
		// test if corresponds to the right afo
		if(!afoName.equals(segment.getAfoName())){
			throw new IllegalArgumentException("Flight segment "+segment+" not belonging to: "+afoName);
		}
		// test if the index is good
		////TODO
		// add
		plan.put(id, segment);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BrahmsFlightPlan: ");
		sb.append(planName);
		return sb.toString();
	}
	
}
