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

package ConceptPieces;

public class Belief {
	String conceptName; //lobj in relexp
	String attName;
	String evalOp;
	String value;
	String rel; //relation in relexp
	String rObj; //relexp
	String index; //index for map
	String indexType; //string or int - type for index for map
	String type; //int, string, bool; ag/obj for relation
	String expType; //oav = valexp or mapkeyvalpair, oro = relexp
	String truthVal;
	
	//val exp
	public Belief(String conceptName, String attName, String evalOp, String value, String 
			type, String expType){
		if(expType.equals("oav")){
			this.conceptName = conceptName;
			this.attName = attName;
			this.evalOp = evalOp;
			this.value = value;
			this.type = type;
			this.expType = expType;
			this.index = null;
			this.indexType = null;
			this.truthVal = null;
		}
		else if(expType.equals("oao")){
			//obj, att, evalOp, rObj, type, expType
			this.conceptName = conceptName;
			this.attName = attName;
			this.evalOp = evalOp;
			this.rObj = value;
			this.type = type;
			this.expType = expType;
			this.index = null;
			this.indexType = null;
			this.truthVal = null;
		}
		
		if(this.conceptName.contains(".")){
			int index = this.conceptName.lastIndexOf(".") + 1;
			this.conceptName = this.conceptName.substring(index);
		}
	}
	
	//map key val pair
	public Belief(String conceptName, String attName, String index, String 
			indexType, String evalOp, String value, String 
			type, String expType){
		this.conceptName = conceptName;
		this.attName = attName;
		this.index = index;
		this.indexType = indexType;
		this.evalOp = evalOp;
		if(expType.equals("oao"))
			this.rObj = value;
		else
			this.value = value;
		this.type = type;
		this.expType = expType;
		this.truthVal = null;
		if(this.conceptName.contains(".")){
			int i = this.conceptName.lastIndexOf(".") + 1;
			this.conceptName = this.conceptName.substring(i);
		}
	}
		
	
	//rel exp
	public Belief(String lObj, String rel, String rObj, String 
			type, String expType, String tf, int i){
		this.conceptName = lObj;
		this.rel = rel;
		this.rObj = rObj;
		this.type = type;
		this.expType = expType;
		this.index = null;
		this.indexType = null;
		this.truthVal = tf;
		if(this.conceptName.contains(".")){
			int j = this.conceptName.lastIndexOf(".") + 1;
			this.conceptName = this.conceptName.substring(j);
		}
		if(this.rObj.contains(".")){
			int j = this.rObj.lastIndexOf(".") + 1;
			this.rObj = this.rObj.substring(j);
		}
	}
	
/*	public Belief(String conceptName, String attName, String evalOp, String rObj, String 
			type, String expType){
		this.conceptName = conceptName;
		this.attName = attName;
		this.evalOp = evalOp;
		this.rObj = rObj;
		this.type = type;
		this.expType = expType;
		this.index = null;
		this.indexType = null;
		this.truthVal = null;
	}*/
	
	public String getObj() {
		return conceptName;
	}
	
	public String getAtt() {
		return attName;
	}
	
	public String getVal() {
		return value;
	}
	
	public String getIndex() {
		return index;
	}
	
	public String getIndexType() {
		return indexType;
	}
	
	public String getRel() {
		return rel;
	}
	
	public String getRObj() {
		return rObj;
	}
	
	public String getType() {
		return type;
	}
	
	public String getExpType() {
		return expType;
	}
	
	public String getTruthVal() {
		return truthVal;
	}
	public String getEvalOp() {
		return evalOp;
	}
}
