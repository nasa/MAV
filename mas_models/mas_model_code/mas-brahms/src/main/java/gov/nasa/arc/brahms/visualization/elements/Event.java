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

package gov.nasa.arc.brahms.visualization.elements;

import java.util.List;

public class Event {

	private String startTime;		// time the activity starts
	private String endTime;			// time the activity ends
	private String workframe;		// the name of the workframe the activity occurs in
	private String activityName;	// name of the activity itself
	private String activityType;	// either CommunicateActivity or PrimitiveActivity
	private String duration;		// how long the activity lasts (endTime - startTime) = duration
	private List<CommunicateActivity> commsList;	// list of all commActivities if it is a communicate activity
	
	public Event(String start, String end, String wf, String actName, String actType, 
			String duration, List<CommunicateActivity> commsList) {
		this.startTime = start;
		this.endTime = end;
		this.workframe = wf;
		this.activityName = actName;
		this.activityType = actType;
		this.duration = duration;
		this.commsList = commsList;
	}
	
	public Event(String start, String end, String wf, String actName, String actType, 
			String duration) {
		this.startTime = start;
		this.endTime = end;
		this.workframe = wf;
		this.activityName = actName;
		this.activityType = actType;
		this.duration = duration;
		this.commsList = null;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getWorkframe() {
		return workframe;
	}

	public String getActivityName() {
		return activityName;
	}

	public String getActivityType() {
		return activityType;
	}

	public String getDuration() {
		return duration;
	}

	public List<CommunicateActivity> getCommsList() {
		return commsList;
	}
}
