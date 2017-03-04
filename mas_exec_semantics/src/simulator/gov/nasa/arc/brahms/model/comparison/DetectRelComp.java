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
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.world.FactSet;

/**
 * 
 * @author jhunter
 * 
 * detect-rel-comp 	::= detectable-object REL.relation-name |
 * 						detectable-object REL.relation-name sgl-object-ref 
 * 							{ is ID.truth-value }
 *
 */
public class DetectRelComp implements Comparison {
	Value lhs;
	String relation;
	SglObjRef rhs;
	boolean truthVal;

	/**
	 * Detect obj rel
	 * @param objRef
	 * @param rel
	 */
	public DetectRelComp(TplObjRef objRef, String rel) {
		this.lhs = objRef;
		this.relation = rel;
		this.rhs = null;
		
	}
	
	public DetectRelComp(Value objRef, String rel, SglObjRef objRef2) {
		this.lhs = objRef;
		this.relation = rel;
		this.rhs = objRef2;
		this.truthVal = true;
	}
	
	public DetectRelComp(Value objRef, String rel, SglObjRef objRef2, 
			boolean tv) {
		this.lhs = objRef;
		this.relation = rel;
		this.rhs = objRef2;
		this.truthVal = tv;
		
	}
	
	public Value getLhs() {
		return lhs;
	}
	
	public String getRelation() {
		return relation;
	}
	
	public SglObjRef getRhs() {
		return rhs;
	}
	
	public boolean getTruthVal() {
		return truthVal;
	}
	
	public String toString() {
		return lhs.toString() + " " + relation + " " + rhs + " is " + truthVal;
	}
	
	public boolean compareEvaluatesTrue(Basic b, Frame f) {
		if (rhs == null)
			return b.beliefExists(lhs.toString() + " " + relation);
		RelComp newExp = new RelComp(lhs, relation, rhs, truthVal);
		try {
			return newExp.beliefIsTrue(b, f);//, 0);
		} catch (NullPointerException npe) {
			return false;
		}
	}
	
	public boolean factEvaluatesTrue(Basic b, Frame f) {
		if (rhs == null)
			return FactSet.factExists(lhs.toString() + " " + relation);
		RelComp newExp = new RelComp(lhs, relation, rhs, truthVal);
		try {
			return newExp.factIsTrue(b, f);//, 0);
		} catch (NullPointerException npe) {
			return false;
		}
	}
	
	@Override
	public Object clone(){
		DetectRelComp drc = new DetectRelComp(lhs, relation, rhs, truthVal);
		return drc;
		
	}

	
	public Expression getExp() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean eval(Basic b, Frame f, Map<String, Value> varCtx) {
		if(f.operatesOnFacts())
			return factEvaluatesTrue(b, f);
		return compareEvaluatesTrue(b, f);
	}

	
	public Variable getNextFreeVariable(Frame f, Map<String, Value> varCtx) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<Value> getVariableBindings(Basic b, Frame f, Variable var,
			Map<String, Value> varCtx) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
