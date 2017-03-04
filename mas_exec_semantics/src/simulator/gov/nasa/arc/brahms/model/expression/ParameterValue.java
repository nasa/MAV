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

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.simulator.Utils;

public class ParameterValue extends Value {
	
	protected String name;
	
	public ParameterValue(String val) {
		this.name = val;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return this.name;
	}
	
	public List<Basic> getPossibleBindings(Basic b, Frame f) {
		System.out
				.println("coming to the get possible binding of parameter value");
		List<Basic> bindings = new ArrayList<Basic>();
		List<Value> values = getValue(b, f);
		for (Value v : values) {
			System.out.println(v.toString());
			if (v.toString().equals("current"))
				bindings.add(b);
			else {
				Basic possibleObj = CommonExpUtils.findConcept(v.toString());
				if (possibleObj != null) {
					bindings.add(possibleObj);
				}
			}
		}
		return bindings;
	}

	public Object clone() {
		ParameterValue exp = ((ParameterValue) super.clone());
		return exp;
	}

	@Override
	public List<Value> getValue(Basic obj, Frame frame) {
		Parameter param = Utils.findParameter(obj, frame, this);
		//If the value is a double or an int then return it as so
		if(param.getType().equals("int")){
			List<Value> vals = new ArrayList<Value>();
			for(int i = 0; i < param.getVals().size(); i++){
				Value v = param.getVals().get(i);
				try{
					int x = Integer.parseInt(v.toString());
					IntegerValue iv = new IntegerValue(x);
					vals.add(iv);
				}
				catch(Exception e){
					
				}
			}
			return vals;
		}
		if(param.getType().equals("double")){
			List<Value> vals = new ArrayList<Value>();
			for(int i = 0; i < param.getVals().size(); i++){
				Value v = param.getVals().get(i);
				try{
					double x = Double.parseDouble(v.toString());
					DoubleValue iv = new DoubleValue(x);
					vals.add(iv);
				}
				catch(Exception e){
					
				}
			}
			return vals;
		}
		return param.getVals();
	}
	
	@Override
	public List<Value> getFactValue(Basic obj, Frame frame) {
		Parameter param = Utils.findParameter(obj, frame, this);
		return param.getVals();
	}

	@Override
	protected Value addValues(Value val) {
		// TODO Auto-generated method stub
		throw new RuntimeException("parameterValue: trying to add");
	}

	@Override
	protected Value subtractValues(Value val) {
		// TODO Auto-generated method stub
		throw new RuntimeException("parameterValue: trying to subtract");

	}

	@Override
	protected Value multiplyValues(Value val) {
		// TODO Auto-generated method stub
		throw new RuntimeException("parameterValue: trying to multiply");

	}

	@Override
	protected Value divideValues(Value val) {
		// TODO Auto-generated method stub
		throw new RuntimeException("parameterValue: trying to divide");

	}

	@Override
	public boolean compareValues(Value val, EvaluationOperator eq) {
		// TODO Auto-generated method stub
		throw new RuntimeException("parameterValue: trying to compare");
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
