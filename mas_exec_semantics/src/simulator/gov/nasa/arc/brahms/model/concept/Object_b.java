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

package gov.nasa.arc.brahms.model.concept;

import gov.nasa.arc.brahms.simulator.Utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Object_b extends Class_b
					  implements Cloneable{
	
	protected String location;
	String isAnInstanceOf;
	//TODO: conceptual object membership
	protected Set<String> partof; //conceptual objects
	
	public Object_b(String name, String display, double cost, int time_unit,
			String location) {
		super(name, display, cost, time_unit);
		this.addclassExtended();
		this.partof = new HashSet<String>();
		this.location = location;
	}
	
	public Object_b(String name, String display, double cost, int time_unit,
			String location, Class_b theClassInstance) {
		super(name, display, cost, time_unit, theClassInstance.getResource());
		this.isAnInstanceOf = theClassInstance.getName();
		this.addclassExtended();
		this.partof = new HashSet<String>();
		this.location = location;
	}
	
	public String getClassInstance() {
		return this.isAnInstanceOf;
	}

	public void inheritFromClasses(Map<String, Class_b> classes) {
		Class_b parentClass = classes.get(isAnInstanceOf);
		inheritAttributes(parentClass);
		inheritRelations(parentClass);
		inheritBeliefs(parentClass);
		inheritFacts(parentClass);
		try {
			inheritActivities(parentClass);
			inheritWorkframes(parentClass);
			inheritThoughtframes(parentClass);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		inheritFromConcepts();
	}
	/**Instantiated in each specific model**/
	protected void addPartOf() {}
	
	/**Instantiated in each specific model**/
	protected void addclassExtended() {}
	
	
	
	public String getLocation(){ 
		return location;
	}
	
	public void setLocation(String s) {
		this.location = s;
	}
	
	public Set<String> getPartOfs() {
		return partof;
	}
	
	public void setPartOfs(Set<String> concepts) {
		partof = concepts;
	}
	
	public void inheritFromConcepts() {
		Iterator<String> itr = partof.iterator();
		while (itr.hasNext()) {
			String s = itr.next();
			Concept co = Utils.getConcept(s);
			inheritAttributes(co);
			inheritRelations(co);
		}
	}
	
	public String toString(){ 
		String retVal = super.toString();
		retVal = retVal.replaceFirst("Class ", "Object ");
		return retVal;
	}
	
	public Object clone() {
		Object_b obj = ((Object_b) super.clone());
		obj.location = new String(location);
		obj.isAnInstanceOf = new String(isAnInstanceOf);
		return obj;
	}

}
