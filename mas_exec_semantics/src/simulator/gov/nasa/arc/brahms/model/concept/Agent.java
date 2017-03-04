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

package gov.nasa.arc.brahms.model.concept;


import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.simulator.world.FactSet;


public class Agent extends Group {
	
	protected String location;

	public Agent (String name, String display, double cost, int time_unit,
			String location) {
		super(name, display, cost, time_unit);
		initializeLocation(location);
	}
	
	public Agent (String name, String display, String icon, double cost,
			int time_unit, String location) {
		super(name, display, icon, cost, time_unit);
		initializeLocation(location);
	}
	
	protected void initializeLocation(String location) {
		this.location = location;
		ValueExpression valExp0 = new ValueExpression("current", "location",
				EvaluationOperator.EQ, new SglObjRef(location));
		this.addBelief(new Term("current", "location"), valExp0);
		FactSet.addFact(new Term(name, "location"), valExp0);
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
		ValueExpression valExp1 = new ValueExpression(name, "location",
				EvaluationOperator.EQ, new SglObjRef(location));
		FactSet.updateFacts(new Term(name, "location"), valExp1);
	}
	
	public String toString(){
		String retVal = super.toString();
		retVal = retVal.replaceFirst("Group ", "Agent ");
		StringBuilder retVal1 = new StringBuilder();
		retVal1.append(retVal);
		retVal1.append("location: " + location + "\n");
		return retVal1.toString();
		
	}
}
