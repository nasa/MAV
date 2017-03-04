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

package gov.nasa.jpf.mas.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent {
	
	double id;
	String name;
	// indexed on time
	Map<Integer, List<Event>> events;
	
	public Agent(double id, String name) {
		this.id = id;
		this.name = name;
		events = new HashMap<Integer, List<Event>>();
	}
	
	public void addEvent(int id, Event e ) {
		List<Event> allEvents;
		if(events.containsKey(id)) {
			allEvents = events.get(id);
		} else {
			allEvents = new ArrayList<Event>();
		}
		allEvents.add(e);
		events.put(id, allEvents);
	}
	
	public Map<Integer, List<Event>> getEvents() {
		return events;
	}
	
	public List<Event> getEventsAtTime(int time) {
		if(events.containsKey(time))
			return events.get(time);
		else 
			return new ArrayList<Event>();
	}
	
	public double getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
}
