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

package gov.nasa.arc.brahms.model.expression;

import java.util.List;

import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Variable;

public class VariableValue extends Value {
	
	String varName;
	
	public List<Basic> getPossibleBindings(Basic b, Frame f) {
		for(Variable v : f.getVariables()) {
			String frameVarName = v.getVarName();
			if(!varName.equals(frameVarName)) continue;
			return CommonExpUtils.findConceptsofType(v.getVarType());
		}
		return null;
	}
	
	public boolean isVariableValue() {
		return true; 
	}
	
	public VariableValue(String val) {
		this.varName = val;
	}
	
	public String getVarName() {
		return varName;
	}

	
	public String toString() {
		return this.varName;
	}
	
	public Object clone() {
		VariableValue exp = ((VariableValue) super.clone());
		return exp;
	}

	@Override
	public List<Value> getValue(Basic obj, Frame frame) {
		return frame.getVarInstanceVal(varName);
	}
	
	@Override
	public List<Value> getFactValue(Basic obj, Frame frame) {
		return frame.getVarInstanceVal(varName);
	}
	
	@Override
	protected Value addValues(Value val) {
		// TODO Auto-generated method stub
		throw new RuntimeException("variableValue: trying to add");
	}

	@Override
	protected Value subtractValues(Value val) {
		// TODO Auto-generated method stub
		throw new RuntimeException("variableValue: trying to subtract");

	}

	@Override
	protected Value multiplyValues(Value val) {
		// TODO Auto-generated method stub
		throw new RuntimeException("variableValue: trying to multiply");

	}

	@Override
	protected Value divideValues(Value val) {
		// TODO Auto-generated method stub
		throw new RuntimeException("variableValue: trying to divide");

	}

	@Override
	public boolean compareValues(Value val, EvaluationOperator eq) {
		// TODO Auto-generated method stub
		throw new RuntimeException("variableValue: trying to compare");
	}
	
	@Override
	public Expression getLhs(){
		return this;
	}
	@Override
	public Expression getRhs(){
		return this;
	} 
}
