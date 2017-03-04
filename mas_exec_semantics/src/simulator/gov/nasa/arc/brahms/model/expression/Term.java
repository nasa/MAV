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

import gov.nasa.arc.brahms.model.Belief;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Fact;
import gov.nasa.arc.brahms.model.WorkFrame;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.simulator.world.FactSet;
import gov.nasa.arc.brahms.model.Frame;

public class Term extends Expression {
	
	String objRefName;
	String attrName;
	EvaluationOperator evalOp;
	
	public String getAttributeString() {
		return attrName;
	}
	
	public List<Basic> getPossibleBindings(Basic b, Frame f) {
		List<Basic> bindings = new ArrayList<Basic>();
		Basic objRef;
		if (objRefName.equals("current"))
			objRef = b;
		else
			objRef = CommonExpUtils.findConcept(objRefName);
		bindings.add(objRef);
		return bindings;
	}
	
	public Term(String objRefName, String attrName) {
		this.objRefName = objRefName;
		this.attrName = attrName;
	}
	
	public String getObjRefName() {
		return objRefName;
	}
	
	public void setObjRefName(String objRef) {
		this.objRefName = objRef;
	}
	
	public String getAttrName() {
		return attrName;
	}
	
	public String toString() {
		return this.objRefName + "." + this.attrName;
	}
	
	public Object clone() {
		Term exp = ((Term) super.clone());
		return exp;
	}
	@Override
	public Expression getLhs(){
		return this;
	}
	@Override
	public Expression getRhs(){
		return this;
	}

	
	@Override
	public List<Value> getValue(Basic obj, Frame f) {
		List<Value> value = new ArrayList<Value>();
		String identifier = CommonExpUtils.getBeliefOrFactAttribute(f, obj,
				this.objRefName, this.attrName);
		if (!f.operatesOnFacts() && obj.getBeliefs().containsKey(identifier)) {
			// nsr: in the refactor need to deal with the "0" element;
			Belief b = obj.getBelief(identifier).get(0);
			if (b.getBelief() instanceof ValueExpression) {
				// Set evalOp in this Term as the one from the fact
				evalOp = ((ValueExpression) b.getBelief()).getEvalOp();
			}
			List<Value> vals = b.getBelief().getValue(obj, f);
			return vals;
		}
		else if(f.operatesOnFacts() && FactSet.factExists(identifier)) {
			Fact fact = FactSet.getFact(identifier).get(0);
			//EvaluationOperator temp = evalOp;
			if(fact.getFact() instanceof ValueExpression){
				//Set evalOp in this Term as the one from the fact
				evalOp = ((ValueExpression) fact.getFact()).getEvalOp();
			}
			List<Value> vals = fact.getFact().getFactValue(obj, f);
			return vals;
			
		}
		else if(attrName.equals("location")) {
			if (obj instanceof Agent) {
				value.add(new StringValue(((Agent) obj).getLocation()));
				return value;
			} else {
				value.add(new StringValue(((Object_b) obj).getLocation()));
				return value;
			}
		} else if(obj instanceof Object_b && FactSet.factExists(identifier)){ //TODO: nsr: what does this do? 
			//If the value is for a communication and the object doesn't have a belief then the fact value is sent
			try{
				WorkFrame wf = (WorkFrame) f;
				wf.setType("factframe");
				List <Value> vals2 = getValue(obj, f);
				return vals2;
			}
			catch (Exception e){ 
				e.printStackTrace();
				return null;}
		}
		else { //no belief or fact about 'this' exists, check if variable
			List<Value> varVals = f.getVarInstanceVal(objRefName);
			try{
				for (int i = 0; i < varVals.size(); i++) {
					Term tmp;
					if (varVals.get(i) instanceof SglObjRef) {
						SglObjRef newObj = (SglObjRef) varVals.get(i);
						tmp = new Term(newObj.getObjRefName(), attrName);
					} else {
						TplObjRef newObj = (TplObjRef) varVals.get(i);
						tmp = new Term(newObj.getObjRefName(), attrName);
					}
					List<Value> tmpVals = tmp.getValue(obj, f);
					for (int j = 0; j < tmpVals.size(); j++) {
						value.add(tmpVals.get(j));
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
			return value;
		}
	}
	
	public List<Value> getFactValue(Basic obj, Frame fr) {
		List<Value> value = new ArrayList<Value>();
		if (this.objRefName.equals("current"))
				this.objRefName = obj.getName();
		if (FactSet.getFacts().containsKey(this.toString())) {
			Fact f = FactSet.getFact(this.toString()).get(0);
			return f.getFact().getFactValue(obj, fr);
		} else if(attrName.equals("location")) {
			if (obj instanceof Agent) {
				value.add(new StringValue(((Agent) obj).getLocation()));
				return value;
			} else {
				value.add(new StringValue(((Object_b) obj).getLocation()));
				return value;
			}
		} else {
			if (fr.variableExists(objRefName)) {
				List<Value> varVals = fr.getVarInstanceVal(objRefName);
				try{
					for (int i = 0; i < varVals.size(); i++) {
						Term tmp;
						if (varVals.get(i) instanceof SglObjRef) {
							SglObjRef newObj = (SglObjRef) varVals.get(i);
							if (newObj.getObjRefName().equals("current"))
								tmp = new Term(obj.getName(), attrName);
							else
								tmp = new Term(newObj.getObjRefName(), attrName);
						} else {
							TplObjRef newObj = (TplObjRef) varVals.get(i);
							if (newObj.getObjRefName().equals("current"))
								tmp = new Term(obj.getName(), attrName);
							else
								tmp = new Term(newObj.getObjRefName(), attrName);
						}
						List<Value> tmpVals = tmp.getFactValue(obj, fr);
						for (int j = 0; j < tmpVals.size(); j++) {
							value.add(tmpVals.get(j));
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			return value;
		}
	}
	
	public EvaluationOperator getEvalOp(){
		return evalOp;
	}
}
