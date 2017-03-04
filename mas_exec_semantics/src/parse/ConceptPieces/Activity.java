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

import java.util.List;
import java.util.ArrayList;
/**
 * 
 * @author jhunter
 *
 */

//enum ActType {PRIMITIVE, MOVE, COMMUNICATE, BROADCAST, COMPOSITE, CREATEAGENT,
//	CREATEOBJECT, CREATEAREA, GESTURE, GET, JAVA, PUT};

public class Activity {
	String name;
	String disp;
	String maxDur;
	String minDur;
	String prior;
	String rand;
	Parameter[] params;
	boolean hasParams = false;
	String type; //acttype: prim, move, comm, broad, comp, get, put, java
				 //crArea, crAgent, crObject, gest, 
	
	String loc; //move
	List<String> with; //comm (or 'to' for broad)
	String when; //comm braod start/end
	//String dir; //comm braod send/receive
	List<Guard> about; //comm broad (transferdefs)
	List<String> items; //get/put items
	String sourceDest; //get source or put destination
	String className; //java
	String endCondition; //composite
	String action; //create obj
	String source; //create obj
	String dest; //create obj
	String destName; //create obj
	
	String memberof; //create agj
	
	public Activity(String t, String name, String disp, String max,
			String min, String prior, String rand) {
		this.name = name;
		this.disp = disp;
		this.maxDur = max;
		this.minDur = min;
		this.prior = prior;
		this.rand = rand;
		this.type = t;
		with = new ArrayList<String>();
		items = new ArrayList<String>();
	}
	
	public Parameter[] getParams(){
		return params;
	}
	
	public void setParams(Parameter[] p){
		params = p;
		hasParams = true;
	}
	
	public boolean hasParams(){
		return hasParams;
	}
	
	public void setLoc(String loc) {
		this.loc = loc;
	}
	
	public void setWith(List<String> with) {
		this.with = with;
	}
	
	public void setWhen(String when) {
		this.when = when;
	}
	
	public void setAbout(List<Guard> about) {
		this.about = about;
	}
	
	public void setItems(List<String> its) {
		items = its;
	}
	
	public void setSourceDest(String sd) {
		sourceDest = sd;
	}
	
	public void setMemberOf(String memberOf) {
		this.memberof = memberOf;
	}
	
	public void setClassName(String s) {
		//int tempInd = s.lastIndexOf(".") + 1;
		//className = s.substring(tempInd);
		className = s;
	}
	
	public void setEndCondition(String s) {
		endCondition = s;
	}
	
	public void setAction(String a) {
		action = a;
	}
	
	public void setSource(String s) {
		
		source = s;
	}
	
	public void setDest(String s) {
		dest = s;
	}
	
	public void setDestName(String n) {
		destName = n;
	}
		
	/*public void setTo(String to) {
		this.to = to;
	}*/
	
	public String getName() {
		return name;
	}
	
	public String getDisp() {
		return disp;
	}
	
	public String getMax() {
		return maxDur;
	}
	
	public String getMin() {
		return minDur;
	}
	
	public String getPrior() {
		return prior;
	}
	
	public String getRand() {
		return rand;
	}
	
	public String getType() {
		return type;
	}
	
	public String getLoc() {
		return loc;
	}
	
	public List<String> getWith() {
		return with;
	}
	
	public String getWhen() {
		return when;
	}
	
	public List<Guard> getAbout() {
		return about;
	}
	
	public List<String> getItems() {
		return items;
	}
	
	public String getSourceDest() {
		return sourceDest;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getEndCondition() {
		return endCondition;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getDest() {
		return dest;
	}
	
	public String getDestName() {
		return destName;
	}
	
	public String getMemberOf() {
		return this.memberof;
	}
		
	/*public String getTo() {
		return to;
	}*/
}
