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

package ConceptPieces;

public class Frame {
	String name;
	String disp;
	String type; //factframe/dataframe
	String repeat;
	String prior;
	
	VariableParse[] vars;
	Detect[] detects;
	Guard[] guards;
	Event[] events;
	
	public Frame(String name, String disp, String type, String repeat,
			String prior) {
		this.name = name;
		this.disp = disp;
		this.type = type;
		this.repeat = repeat;
		this.prior = prior;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisp() {
		return disp;
	}
	
	public String getType() {
		return type;
	}
	
	public String getRepeat() {
		return repeat;
	}
	
	public String getPrior() {
		return prior;
	}
	
	public VariableParse[] getVars() {
		return vars;
	}
	
	public Detect[] getDetects() {
		return detects;
	}
	
	public Guard[] getGuards() {
		return guards;
	}
	
	public Event[] getEvents() {
		return events;
	}
	
	public void setGuardLength(int i) {
		guards = new Guard[i];
	}
	
	public void addGuard(Guard g, int i) {
		guards[i] = g;
	}
	
	public void setEventLength(int i) {
		events = new Event[i];
	}
	
	public void addEvent(Event e, int i) {
		events[i] = e;
	}

/*	public void setDetectLength(int i) {
		detects = new Detect[i];
	}*/
	
	public void addDetects(Detect[] d) {
		detects = d;
	}
	
	public void addVars(VariableParse[] v ){
		vars = v;
	}
}
