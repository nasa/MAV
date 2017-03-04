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

import java.util.List;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Constants;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.IntegerValue;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.scheduler.DQEvent;

public class ActivityInstance extends DQEvent {
	Activity currActivity;
	int duration;
	int startDuration;
	
	public ActivityInstance(Basic b, Frame f,
					Activity act, List<Value> eventParams) {
		super();
		currActivity = act;
		List<Parameter> activityParams = currActivity.getParams();
		
		duration = act.getDefaultMaxDuration();
		if(duration == Constants.PARAMETER_VAL) {
			ParameterValue paramMaxDuration =
						act.getDefaultMaxDurationParamVal();
				for (int paramIndex = 0; paramIndex < eventParams.size(); paramIndex++) {
				Value val = eventParams.get(paramIndex);
				if(!(val instanceof ParameterValue)) continue;
				ParameterValue paramVal = (ParameterValue) val;
				Parameter actParam = activityParams.get(paramIndex);

				if(!actParam.getName().equals(paramMaxDuration.getName())) continue;
				
				if(f.getVarInstances() != null &&
							f.variableExists(paramVal.getName())) {
					Value valueDuration = 
							f.getVarInstances().get(0).get(paramVal.getName()).get(0);
					assert (valueDuration instanceof IntegerValue);
					IntegerValue intValueDuration = (IntegerValue) valueDuration;
					duration = intValueDuration.getIntValue();
				}
			}
		}
		startDuration = duration;
		super.setDuration(duration);
	}
	
	public ActivityInstance(Basic b, Activity act) {
		super();
		currActivity = act;
		/*if(!act.isRandom(b))
			duration = act.getMaxDuration(b);
		else if(act.getMinDuration(b) == act.getMaxDuration(b)) {
			duration = act.getMaxDuration(b);
		} else {
			duration = Utils.getActualDuration(currActivity.getMinDuration(b), 
						currActivity.getMaxDuration(b));
		}*/
	
	}
	
	public Activity getActivity() {
		return currActivity;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int t) {
		duration = t;
	}
	
	public int getStartDuration() {
		return startDuration;
	}
	
	public String getCurrActivityName(){
		return currActivity.getName();
	}
	
	public String toString() {
		String retVal = "";
		retVal += "activityInstance " + currActivity.getName() + " with " +
		duration + " time remaining \n";
		return retVal;
	}
	
}
