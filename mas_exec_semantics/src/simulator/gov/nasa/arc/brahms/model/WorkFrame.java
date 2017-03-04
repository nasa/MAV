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

package gov.nasa.arc.brahms.model;


/**
 * 
 * @author nrungta & jhunter
 *
 *workframe 	::= 	workframe workframe-name
{
{ display : ID.literal-string ; }
{ type : factframe | dataframe ; }
{ repeat : ID.truth-value ; }
{ priority : ID.unsigned ; }
{ variable-decl }
{ detectable-decl }
{ [ precondition-decl workframe-body-decl ] |
workframe-body-decl }
}
workframe-name 	::= 	ID.name
variable-decl 	::= 	variables : [ VAR.variable ]*
detectable-decl 	::= 	detectables : [ DET.detectable ]*
precondition-decl 	::= 	when ( { [ PRE.precondition ] [ and PRE.precondition ]* } )
workframe-body-decl 	::= 	do { [ workframe-body-element ]* }
workframe-body-element 	::= 	[ PAC.activity-ref | CON.consequence | DEL.delete-operation ]

 
 */
public class WorkFrame extends Frame 
					   implements Cloneable {

	protected String type; //factframe | data frame;
	protected int index;
	protected int level; //set to true if at 'level 0', false if w/in composite
	double temporalWeight = 1;
	double decisionWeight = 1;
	/**
	 * WorkFrame Constructor
	 * @param type is factframe or dataframe (agents only have dataframes)
	 */
	public WorkFrame(String name, String display, String type, String repeat, 
			int priority){
		super(name, display, priority, repeat);
		this.type = type;	
		this.index = 0;
		level = 0;		
	}
	
	/**
	 * Automatically sets type to dataframe since all agents use dataframes
	 * @param name of wf
	 * @param display for wf
	 * @param repeat is workframe repeatable? t/f
	 * @param priority for wf
	 */
	public WorkFrame(String name, String display, String repeat, int priority){
		super(name, display, priority, repeat);
		this.type = "dataframe";	
		this.index = 0;
		level = 0;

	}
	

	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int i) {
		level = i;
	}
	
	public void setPriority(int i) {
		priority = i;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void incIndex(){
		index++;
	}
	
	public void resetIndex() {
		this.index = 0;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String t) {
		type = t;
	}
	
	
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("workframe " + name + "\n");
		retVal.append(super.toString());
		return retVal.toString();
	}
	
	public Object clone() throws CloneNotSupportedException {
		WorkFrame wf = ((WorkFrame) super.clone());
		wf.type = new String(type);
		return wf;
	}

	@Override
	public boolean operatesOnFacts() {
		if(super.operateOnFacts) return true;
		
		if(type.equals("factframe"))
			return true;
		return false;
	}
}
