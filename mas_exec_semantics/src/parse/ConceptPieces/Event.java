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

public class Event {
	String type; //activity or consequence
	
	//activity
	String nameOfAct;
	Parameter[] params;
	
	//consequence
	//LHS
	String lhsExpType; //OA, V
	String lhsObj;
	String lhsAtt;
	String relation; //eq
	String lhsObjType = "";
	String lhsCollectionType;
	String lhsCollectionIndex;
	boolean isRelation = false; //flag to identify if the consequence is of type relation
	boolean truthValue = true;
	
	//RHS
	String rhsExpType; //OA, V
	String rhsObj;
	String rhsAtt;
	String valType; //int, bool, string, double
	String value;
	String rhsOperator; //+, -, *, /
	String rhsObjType = "";
	
	//RHS2
	String rhsExpType2; //OA, V
	String rhsObj2;
	String rhsAtt2;
	String valType2;
	String value2;
	String rhsObjType2 = "";
	
	String bc;
	String fc;
	
	//Consequence
	public Event(String type, String op, String lObj, String lAtt) {
		this.type = type;
		this.lhsObj = lObj;
		this.lhsAtt = lAtt;
		this.relation = op;
		rhsObj = "";
		rhsAtt = "";
		valType = "";
		value = "";
		rhsOperator = "";
		rhsObj2 = "";
		rhsAtt2 = "";
		valType2 = "";
		value2 = "";
		bc = "100";
		fc = "100";	
		
		if(this.relation.contains(".")){
			int index = this.relation.lastIndexOf(".") + 1;
			this.relation = this.relation.substring(index);
		}
	}
	public Parameter[] getParams(){
		return params;
	}
	
	public void setParams(Parameter[] p){
		params = p;
	}
	//Activity
	public void setIsRelation(boolean b){
		isRelation = b;
		
	}
	public boolean getIsRelation(){
		return isRelation;
	}
	
	public Event(String type, String name) {
		this.type = type;
		this.nameOfAct = name;
	}
	
	public String getActName() {
		return nameOfAct;
	}
	
	public String getType() {
		return type;
	}
	
	public String getRelation() {
		return relation;
	}
	
	public String getLhsObj() {
		return lhsObj;
	}
	
	public String getLhsAtt() {
		return lhsAtt;
	}
	
	public void setLHSObjType(String type){
		lhsObjType = type;
	}

	public void setRHSObjType(String type){
		rhsObjType = type;
	}

	public void setRHSObj2Type(String type){
		rhsObjType2 = type;
	}
	
	public void setRhsOp(String op) {
		this.rhsOperator = op;
	}
	
	
	public void setRhsObj1(String ob) {
		this.rhsObj = ob;
	}
	
	public void setRhsAtt1(String att) {
		this.rhsAtt = att;
	}
	
	public void setRhsObj2(String ob) {
		this.rhsObj2 = ob;
	}
	
	public void setRhsAtt2(String att) {
		this.rhsAtt2 = att;
	}
	
	public void setRhsValType1(String valt) {
		this.valType = valt;
	}
	
	public void setRhsVal1(String val) {
		this.value = val;
	}
	
	public void setRhsValType2(String valt) {
		this.valType2 = valt;

	}
	
	public void setRhsVal2(String val) {
		this.value2 = val;
	}

	public void setLhsExpType(String val) {
		this.lhsExpType = val;
	}
	
	public void setRhsExpType(String val) {
		this.rhsExpType = val;
	}
	
	public void setRhsExpType2(String val) {
		this.rhsExpType2 = val;
	}
	
	public String getLhsExpType() {
		return lhsExpType;
	}
	
	public String getRhsExpType() {
		return rhsExpType;
	}
	
	public String getRhsExpType2() {
		return rhsExpType2;
	}	
	
	public void setBC(String bc) {
		this.bc = bc;
	}
	
	public void setFC(String fc) {
		this.fc = fc;
	}
	public void setLhsCollectionType(String lct) {
		this.lhsCollectionType = lct;
	}
	
	public void setLhsCollectionIndex(String lci) {
		this.lhsCollectionIndex = lci;
	}
	
	public void setTruthVal(boolean t) {
		this.truthValue = t;
	}

	public boolean getTruthVal() {
		return this.truthValue;
	}
	
	public boolean setTruthVal() {
		return truthValue;
	}
	
	public String getRhsOp() {
		return rhsOperator;
	}
	
	
	public String getRhsObj1() {
		return rhsObj;
	}
	
	public String getRhsAtt1() {
		return rhsAtt;
	}
	
	public String getRhsObj2() {
		return rhsObj2;
	}
	
	public String getRhsAtt2() {
		return rhsAtt2;
	}
	
	public String getRhsValType1() {
		return valType;
	}
	
	public String getRhsVal1() {
		return value;
	}
	
	public String getRhsValType2() {
		return valType2;
	}
	
	public String getRhsVal2() {
		return value2;
	}
	
	public String getBC() {
		return bc;
	}
	
	public String getFC() {
		return fc;
	}
	
	public String getLhsObjType() {
		return lhsObjType;
	}
	public String getRhsObjType() {
		return rhsObjType;
	}
	public String getRhsObjType2() {
		return rhsObjType2;
	}
	public String getLhsCollectionType() {
		return this.lhsCollectionType;
	}
	
	public String getLhsCollectionIndex() {
		return this.lhsCollectionIndex;
	}
}
