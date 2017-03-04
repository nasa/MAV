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

import gov.nasa.arc.brahms.model.expression.Value;

import java.util.ArrayList;
import java.util.List;

public class Parameter implements Cloneable {

	protected String name;  //name of parame
	protected String type;  //int, string, bool, double, sgl...
	protected List<Value> values;
	protected boolean isVar;
	protected String varQuantifier;
	

	public Parameter(String type, String name) {
		this.type = type;
		this.name = name;
		this.values = new ArrayList<Value>();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
	
	public List<Value> getVals() {
		return values;
	}
	
	public void setVals(List<Value> vs) {
		values = vs;
	}
	
	public void addVal(Value v) {
		values.add(v);
	}
	
	public boolean isVar() {
		return isVar;
	}
	
	public void setIsVar(boolean var) {
		isVar = var;
	}
	
	public boolean valsEmpty() {
		return values.isEmpty();
	}
	
	public String toString(){ 
		return "(" + type + ") " + name + " = " + values + ";\n";
	}
	
	public Object clone() {
		try {
			Parameter par = ((Parameter) super.clone());
			
			par.name = new String(name);
			par.type = new String(type);
			
			return par; 
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}
	}
	

}
