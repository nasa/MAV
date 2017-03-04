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

package gov.nasa.arc.brahms.model.activity;

import java.util.ArrayList;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.expression.Value;

public class CreateAgentActivity extends CreateConceptActivity {

	public CreateAgentActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
	}
	
	public CreateAgentActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
	}
	
	public String toString(){	
		return "CreateAgent Acitivity:" +
						super.toString() +
						"-------\n";
	}
}
