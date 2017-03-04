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

import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Frame;



public abstract class Expression implements Cloneable{
	
	double weight = 1;
	
	public String getAttributeString() {
		return "";
	}
	
	public boolean isVariableValue() {
		return false; 
	}
	
	public List<Basic> getPossibleBindings(Basic b, Frame f) {
		return null;
	}

	public Object clone() {
		try {
			Expression exp = ((Expression) super.clone());
			return exp;
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}
	}
	
	public double getWeight(){
		return this.weight;
	}

	public void setWeight(double weight){
		this.weight = weight;
	}
	
	public abstract List<Value> getValue(Basic b, Frame f);
	public abstract List<Value> getFactValue(Basic b, Frame f);

	public Expression getLhsExp() {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression getRhsExp() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Value> getLhsValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Value> getRhsValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public Value getKeyVarL() {
		// TODO Auto-generated method stub
		return null;
	}

	public Value getKeyVarR() {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression getLhs() {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression getRhs() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Value> getLhsValues(Basic b, Frame f) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Value> getRhsValues(Basic b, Frame f) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<Value>> getVarValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Value> getVarValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLAtt() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRAtt() {
		// TODO Auto-generated method stub
		return null;
	}

	public void resetVarValues() {
		// TODO Auto-generated method stub
		
	}
	
	public String getVal() {
		// TODO Auto-generated method stub
		return null;
	}

}
