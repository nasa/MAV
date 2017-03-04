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

public class Guard {
	String modifier; //knownval/not
	String compare; //eq, neq, gt, lt, gte, lte
	String mathOp; //if rhs is: term + int, etc
	boolean isRelation = false;
	
	//LHS
	String exp1; //OA or V
	String exp1Obj; //Object
	String exp1Att; //Attribute
	String obj1Type; //If it is a variable
	String collectionIndexType1;
	String collectionIndex1;
	
	//RHS1
	String exp2; //OA or V
	String exp2Type; //int, double, string... (exp1Type will be term)
	String exp2Val; //Value, if not a term
	String exp2Obj; //Object, if a term
	String exp2Att; //Attribute, if a term
	String obj2Type; //If it is a variable
	String collectionIndexType2;
	String collectionIndex2;
	
	//RHS2
	String exp3; //OA or V, if exists
	String exp3Type; //int, double, string...
	String exp3Val; //Value, if not a term
	String exp3Obj; //Object, if term
	String exp3Att; //Attribute, if term
	String obj3Type; //If it is a variable
	String collectionIndexType3;
	String collectionIndex3;
	
	//Addition field used if it is in a communicaiton
	String sendReceive;
	

	
	
	public Guard(String mod, String comp, String op, String e1, String e2,
			String e2Type, String e1Obj, String e1Att, String e2Obj,
			String e2Att, String e2Val, String oT1, String oT2,
			String cit1, String ci1, String cit2, String ci2) {
		modifier = mod;
		compare = comp;
		if(compare.contains(".")){
			int index = compare.lastIndexOf(".");
			compare = compare.substring(index+1);
		}
			
		mathOp = op;
		exp1 = e1;
		exp1Obj = e1Obj;
		exp1Att = e1Att;
		obj1Type = oT1;
		
		exp2 = e2;
		exp2Type = e2Type;
		exp2Obj = e2Obj;
		exp2Att = e2Att;
		exp2Val = e2Val;
		obj2Type = oT2;
		
		exp3 = "";
		exp3Type = "";
		exp3Obj = "";
		exp3Att = "";
		exp3Val = "";
		
		collectionIndexType1 = cit1;
		collectionIndex1 = ci1;
		
		collectionIndexType2 = cit2;
		collectionIndex2 = ci2;

	}
	
	public void addExp3(String e3, String e3Type, String e3Obj, String e3Att,
			String e3Val, String oT3) {
		exp3 = e3;
		exp3Type = e3Type;
		exp3Obj = e3Obj;
		exp3Att = e3Att;
		exp3Val = e3Val;
		obj3Type = oT3;
	}
	
	public void setIsRelationTrue(){
		isRelation = true;
	}
	
	public String getModifier() {
		return modifier;
	}
	
	public String getCompare() {
		return compare;
	}
	
	public String getMathOp() {
		return mathOp;
	}
	
	public String getExp1() {
		return exp1;
	}
	
	public String getExp2() {
		return exp2;
	}
	
	public String getExp3() {
		return exp3;
	}
	
	public String getExp2Type() {
		return exp2Type;
	}
	
	public String getExp3Type() {
		return exp3Type;
	}
	
	public String getExp1Obj() {
		return exp1Obj;
	}
	
	public String getExp1Att() {
		return exp1Att;
	}
	
	public String getExp2Obj() {
		return exp2Obj;
	}
	
	public String getExp2Att() {
		return exp2Att;
	}
	
	public String getExp2Val() {
		return exp2Val;
	}	
	
	public String getExp3Obj() {
		return exp3Obj;
	}	
	
	public String getExp3Att() {
		return exp3Att;
	}	
	
	public String getExp3Val() {
		return exp3Val;
	}

	public String getObj1Type(){
		return obj1Type;
	}	
	
	public String getObj2Type(){
		return obj2Type;
	}	
	
	public String getObj3Type(){
		return obj3Type;
	}
	
	public boolean getIsRelation(){
		return isRelation;
	}
	
	public void setSendReceive(String sr){
		sendReceive = sr.toLowerCase();
	}
	public String getSendReceive(){
		return sendReceive;
	}

	public String getCollectionIndexType1(){
		return collectionIndexType1;
	}
	public String getCollectionIndex1(){
		return collectionIndex1;
	}
	
	public String getCollectionIndexType2(){
		return collectionIndexType2;
	}
	public String getCollectionIndex2(){
		return collectionIndex2;
	}
	public String getCollectionIndexType3(){
		return collectionIndexType3;
	}
	public String getCollectionIndex3(){
		return collectionIndex3;
	}
}
