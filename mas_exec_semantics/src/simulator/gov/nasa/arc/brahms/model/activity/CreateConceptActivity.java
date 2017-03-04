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
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.Value;

public class CreateConceptActivity extends Activity {

	
	protected String source; //(conceptual) classname, (conceptual) objectname, param
	protected ParameterValue sourceVal = null;
	protected ParameterValue destination;
	protected String destName;
	protected ParameterValue destNameVal = null;
	protected String when; //start or end
	protected ParameterValue whenVal = null;
	protected String location;
	protected ParameterValue locVal;

	
	public CreateConceptActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, priority, random, 
				params);
	}
	
	public CreateConceptActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, priority, random, 
				params);
	}
	
	public void setSource(String s) {
		source = s;
	}
	
	public void setSource(Value s) {
		if (s instanceof StringValue)
			source = ((StringValue) s).getStringValue();
		else if (s instanceof ParameterValue) {
			source = null;
			sourceVal = (ParameterValue) s;
		} else throw new RuntimeException("***ERROR: source needs to be string");

	}

	public void setDest(ParameterValue pv) {
		destination = pv;
	}

	public void setDestName(String s) {
		destName = s;
	}
	
	public void setDestName(Value s) {
		if (s instanceof StringValue)
			destName = ((StringValue) s).getStringValue();
		else if (s instanceof ParameterValue) {
			destName = null;
			destNameVal = (ParameterValue) s;
		} else throw new RuntimeException("***ERROR: dest needs to be string");

	}

	public void setWhen(String s) {
		when = s;
	}
	
	public void setWhen(Value s) {
		if (s instanceof StringValue)
			when = ((StringValue) s).getStringValue();
		else if (s instanceof ParameterValue) {
			when = null;
			whenVal = (ParameterValue) s;
		} else throw new RuntimeException("***ERROR: when needs to be string");

	}

	public void setLocation(String s) {
		location = s;
	}
	
	public void setLocation(Value s) {
		if (s instanceof StringValue)
			location = ((StringValue) s).getStringValue();
		else if (s instanceof ParameterValue)
			locVal = (ParameterValue) s;
		else throw new RuntimeException("***ERROR: loc needs to be string");
	}

	
	

}
