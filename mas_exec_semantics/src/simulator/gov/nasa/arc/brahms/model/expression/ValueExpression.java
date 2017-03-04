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

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Frame;

public class ValueExpression extends Expression 
							 implements Cloneable {
	
	//TODO: change this to the actual objref
	String objRefName;
	
	String attrName;
	EvaluationOperator evalOp;
	Value val; //term, int, bool, string, symbol, double, or sglobjref
	
	public ValueExpression(String objRefName, String attrName,
			EvaluationOperator evalOp, Value val) {
		this.objRefName = objRefName;
		this.attrName = attrName;
		this.evalOp = evalOp;
		this.val = val;
	}
	
	public ValueExpression(Term t,
			EvaluationOperator evalOp, Value val) {
		this.objRefName = t.getObjRefName();
		this.attrName = t.getAttrName();
		this.evalOp = evalOp;
		this.val = val;
	}
	
	public String toString() {
		return "( "+this.objRefName+"."+attrName + " " +
				evalOp.toString() + " " + val.toString() +" )\n";
	}
	
	public Object clone() {
		ValueExpression valExp = ((ValueExpression) super.clone());
		valExp.attrName = new String(this.attrName);
		valExp.objRefName = new String(this.objRefName);
		valExp.val = ((Value) val.clone());
		return valExp;	
	}
	
	public String getObjRefName() {
		return objRefName;
	}
	
	public String getAttName() {
		return attrName;
	}
	
	public EvaluationOperator getEvalOp() {
		return evalOp;
	}
	
	public Value getValue() {
		return val;
	}
	
	@Override
	public List<Value> getValue(Basic b, Frame f) {
		List<Value> value = new ArrayList<Value>();
		value.add(val);
		return value;
	}

	@Override
	public List<Value> getFactValue(Basic b, Frame f) {
		List<Value> value = new ArrayList<Value>();
		value.add(val);
		return value;
	}
}
