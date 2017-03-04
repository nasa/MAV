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

import gov.nasa.arc.brahms.model.comparison.ResultComparison;

/**
 * 
 * @author josie
 *
 *detectable 	::= 	detectable detectable-name {
 *{ when ( [ whenever | ID.unsigned ] ) }
 *detect ( ( resultcomparison ) { , detect-certainty } )
 *{ then detectable-action } ;
 *}
 */
public class Detectable {
	protected String name;
	protected String when; //whenever | unsigned
	protected ResultComparison resultComparison; //EvalValCompExpOpExp or RelComp
	protected int certainty;
	protected DetectableAction action; //enum
	//Workload fields
	public double perceptionWeight;
	public double decisionWeight;
	
	public String sensoryType = "AUDIO";
	
	public Detectable(String name, String when, ResultComparison resultComparison, 
			int certainty, DetectableAction action) {
		this.name = name;
		this.when = when;
		this.resultComparison = resultComparison;
		this.certainty = certainty;
		this.action = action;
	}
	
	public Detectable(String name, String when, ResultComparison resultComparison, 
			int certainty, DetectableAction action, double weight, String sensoryType) {
		this.name = name;
		this.when = when;
		this.resultComparison = resultComparison;
		this.certainty = certainty;
		this.action = action;
		this.sensoryType = sensoryType;
		
	}
	
	
	
	public String getWhen() {
		return when;
	}
	
	public ResultComparison getResultComparison() {
		return resultComparison;
	}
	
	public int getCertainty() {
		return certainty;
	}
	
	public DetectableAction getAction() {
		return action;
	}
	
	public String getSensoryType(){
		return sensoryType;
	}
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("detectable " + name + " { \n");
		retVal.append("\t when(" + when + ") \n");
		retVal.append("\t detect((" + resultComparison + "), dc: " + certainty
				+ ") \n");
		retVal.append("\t then " + action + "\n" + "} \n");
		return retVal.toString();
	}
	
	public Object clone(){
		ResultComparison newResultComp = new ResultComparison(resultComparison.getCmp());
		Detectable det = new Detectable(name, when, newResultComp, certainty, action);
		return det;
	}
	
}
