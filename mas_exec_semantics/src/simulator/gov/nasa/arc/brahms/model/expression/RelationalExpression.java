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

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Frame;
/**
 * 
 * @author nrungta & jhunter
 *	::= 	tuple-object-ref REL.relation-name sgl-object-ref { is ID.truth-value }
 */
public class RelationalExpression extends Expression {
	
	String lhsObjRef; //tupleObjRef
	String relationName;
	String rhsObjRef; //sglObjRef, can be unknown, obj.att
	boolean truthVal; //is true, is false
	
	public RelationalExpression(String lhsObjRef, 
			String relationName, String rhsObjRef) {
		this.lhsObjRef = lhsObjRef;
		this.relationName = relationName;
		this.rhsObjRef = rhsObjRef;
		this.truthVal = true;
	}

	public RelationalExpression(String lhsObjRef, 
			String relationName, String rhsObjRef, boolean tv) {
		this.lhsObjRef = lhsObjRef;
		this.relationName = relationName;
		this.rhsObjRef = rhsObjRef;
		this.truthVal = tv;
	}
	
	public String getLhsObjRef() {
		return this.lhsObjRef;
	}
	
	public String getRelationName() {
		return this.relationName;
	}
	
	public String getRhsObjRef() {
		return this.rhsObjRef;
	}
	
	public boolean getTruthVal() {
		return this.truthVal;
	}
	
	public void setLHSObjRef(String s) {
		this.lhsObjRef = s;
	}
	
	public void setTruthVal(boolean tf) {
		this.truthVal = tf;
	}
	
	@Override
	public String toString(){ 
		return "( " + lhsObjRef + " " + relationName
				+ " " + rhsObjRef + " is " + truthVal + ")\n";
	}
	
	public Object clone() {
		RelationalExpression exp = ((RelationalExpression) super.clone());
		return exp;
	}

	@Override
	public List<Value> getValue(Basic b, Frame f) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Value> getFactValue(Basic b, Frame f) {
		// TODO Auto-generated method stub
		return null;
	}
}
