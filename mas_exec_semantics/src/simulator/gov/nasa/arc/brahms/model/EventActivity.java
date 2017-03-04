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

package gov.nasa.arc.brahms.model;

import gov.nasa.arc.brahms.model.activity.ActivityInstance;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.scheduler.EventSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class EventActivity extends Event
						   implements Cloneable {
	
	String name;
	//parameters
	List<Value> params;
	
	public EventActivity(String name) {
		this.name = name;
		this.params = new ArrayList<Value>();
	}
	
	public void addParameter(Value exp) {
		params.add(exp);
	}
	
	public String getName() {
		return name;
	}
	
	public List<Value> getParams() {
		return params;
	}
	
	
	public String toString(){
		StringBuilder retVal = new StringBuilder();
		
		retVal.append(name);
		if(params != null && params.size() > 0) {
			retVal.append("(" + params.toString() + ")\n");
		} else {
			retVal.append("( )\n");
		}
		return retVal.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		EventActivity ea = ((EventActivity) super.clone());
		ea.name = new String(name);
		
		Object prms = ((ArrayList<Value>) params).clone();
		ea.params = ((List<Value>) prms);
		
		return ea;
	}

	@Override
	public void createDeltaQueueEvent(EventSequence es,
									Basic b, Frame f) {
		Set<Activity> activities = b.getActivities();
		for (Activity act : activities) {
			if (act.getName().equals(this.getName())) {
				Activity newAct = null;
				try {
					newAct = (Activity) act.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
				newAct.setParams(this.getParams(), b, f);
				ActivityInstance ai = new ActivityInstance(b, f, newAct,
											this.getParams());
				// TODO think about the case when duration is passed as a
				// parameter also about the case when it is a move activity and the
				// distance traversed in the move is the duration of the activity
				ai.setDuration(ai.getDuration());
				es.addDQEvent(ai);
				return;
			}
		}
		throw new RuntimeException("could not find the activity to create delta queue");
		
	}
	
}
