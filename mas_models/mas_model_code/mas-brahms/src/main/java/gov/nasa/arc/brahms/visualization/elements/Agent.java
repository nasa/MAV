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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent {

	private String name;
	private Map<Integer, List<BeliefUpdate>> beliefs;
	private Map<Integer, List<Event>> activities;

	public Agent(String agentName, Map<Integer, List<BeliefUpdate>> agentBeliefs, Map<Integer, List<Event>> activities) {
		name = agentName;
		beliefs = agentBeliefs;
		this.activities = activities;
	}

	public Agent(String agentName) {
		this(agentName, new HashMap<>(), new HashMap<>());
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public Map<Integer, List<BeliefUpdate>> getBeliefs() {
		return beliefs;
	}

	public Map<Integer, List<Event>> getActivities() {
		return activities;
	}

	public void updateBeliefs(Integer time, List<BeliefUpdate> beliefList) {
		if (beliefs.containsKey(time)) {
			List<BeliefUpdate> old = beliefs.get(time);
			for (int i = 0; i < beliefList.size(); i++) {
				old.add(beliefList.get(i));
			}
			beliefs.put(time, old);
		} else
			beliefs.put(time, beliefList);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(": ");
		sb.append(name);
		return sb.toString();
	}
}
