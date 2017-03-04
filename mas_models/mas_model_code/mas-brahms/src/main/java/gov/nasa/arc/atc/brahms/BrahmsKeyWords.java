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

/**
 * 
 * @author ahamon
 *
 */
public class BrahmsKeyWords {

	public static final String INIT_BELIEFS = "initial_beliefs";
	public static final String INIT_FACTS = "initial_facts";
	public static final String HAS_WAYPOINT = "hasWaypoint";
	public static final String DEPARTURE_QUEUE = "departure_queue";

	// Is it the best way ? instead of having to check for agent / object / {...
	public static final String START_OBJECT_PATTERN = "instanceof";
	public static final String START_AGENT_PATTERN = "memberof";

	public static final String END_PATTERN = "}";
	private BrahmsKeyWords() {
		// private utility constructor
	}

}
