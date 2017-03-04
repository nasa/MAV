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

package gov.nasa.arc.brahms.model.comparison;

import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Variable;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.Value;

public class EvalValComp implements Comparison {
	String type;
	Expression exp; //evalvalcompexpopexp or relcomp

	public EvalValComp(String type, Expression inExp) {
		this.type = type;
		if ((!(type.equals("knownval"))) && (!(type.equals("not"))))
			throw new RuntimeException("EvalValComp must be 'knownval' or 'not'");
		if ((!(inExp instanceof EvalValCompExpOpExp)) && (!(inExp instanceof RelComp)))
			throw new RuntimeException("EvalValComp can only have " +
					"evalvalcompexpopexp or relcomp");
		this.exp = inExp;
	}
	
	public Expression getExp() {
		return exp;
	}
	
	public String getType(){
		return type;
	}
	
	
	public boolean compareEvaluatesTrue(Basic b, Frame f) {
		boolean tmp = false;
		tmp = beliefTrue(b, f);
		return tmp;
	}
	
	public boolean beliefTrue(Basic b, Frame f) {
		//if it is not a relation
		if (exp instanceof EvalValCompExpOpExp) {
			EvalValCompExpOpExp newExp = (EvalValCompExpOpExp) exp;
			newExp.setType(type);
			return newExp.beliefIsTrue(b, f);
		} else { //relcomp		
			RelComp newExp = (RelComp) exp;
			newExp.setType(type);
			return newExp.beliefIsTrue(b, f);
		}
	}
	
	/**
	 * Returns value of belief if knownval & "is true" or if not & "is false"
	 * Otherwise negates the value of the belief
	 */
	public boolean factEvaluatesTrue(Basic b, Frame f) {//, int idx) {
		f.setOperateOnFacts();
		return factTrue(b, f);
			
	}
	
	public boolean factTrue(Basic b, Frame f) {
		if (exp instanceof EvalValCompExpOpExp) {
		EvalValCompExpOpExp newExp = (EvalValCompExpOpExp) exp;
			newExp.setType(type);
			return newExp.factIsTrue(b, f);
		} else { //relcomp
			RelComp newExp = (RelComp) exp;
			newExp.setType(type);
			return newExp.factIsTrue(b, f);
		}
	}
	
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("(" + type + "(" + exp.toString() + "))");
		return retVal.toString();
	}
	
	@Override
	public Object clone(){
		EvalValComp dvc = new EvalValComp(type, exp);
		return dvc;
		
	}
	
	
	public boolean eval(Basic b, Frame f, Map<String, Value> varCtx) {
		if (exp instanceof EvalValCompExpOpExp) {
			EvalValCompExpOpExp newExp = (EvalValCompExpOpExp) exp;
			newExp.setType(type);
			return newExp.eval(b, f, varCtx);
		} else { //relcomp		
			RelComp newExp = (RelComp) exp;
			newExp.setType(type);
			return newExp.eval(b, f, varCtx);
		}
	}
	
	public Variable getNextFreeVariable(Frame f, Map<String, Value> varCtx) {
		if(exp instanceof RelComp) 
			return ((RelComp) exp).getNextFreeVariable(f, varCtx);
		return ((EvalValCompExpOpExp) exp).getNextFreeVariable(f, varCtx);
	}
	
	public List<Value> getVariableBindings(Basic b, Frame f, Variable var,
			Map<String, Value> varCtx) {
		if(exp instanceof RelComp)
			return ((RelComp) exp).getVariableBindings(b, f, var, varCtx);
		return ((EvalValCompExpOpExp) exp).getVariableBindings(b, f, var, varCtx);
	}
	
}
