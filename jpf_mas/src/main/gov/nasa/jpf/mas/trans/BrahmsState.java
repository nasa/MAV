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

package gov.nasa.jpf.mas.trans;

import java.util.HashMap;
import java.util.Map;

public class BrahmsState {
	
	int stateId;
	Map<String, Integer> intBeliefs;
	
	public BrahmsState() {
		intBeliefs = new HashMap<String, Integer>();
	}
	
	public void addIntBeliefs(String belief, int value) {
		intBeliefs.put(belief, value);
	}
	
	public void setId(int id) {
		stateId = id;
	}
	
	public int getId() {
		return stateId;
	}
}
