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

/**
 * author jhunter
 * conceptual-object 	::= 	conceptual_object conceptual-object-name
 * instanceof COC.conceptual-class-name
 * { conceptual-object-membership }
 * {
 * { display : ID.literal-string ; }
 * { icon : ID.literal-string ; }
 * { GRP.attributes }
 * { GRP.relations }
 * }
**/

public class Conceptual_Object extends Conceptual_Class {
	
	protected String isAnInstanceOf;
	protected Set<String> partof; //conceptual objects
	
	public Conceptual_Object(String name, String display, 
			Conceptual_Class theClassInstance) {
		super(name, display);
		this.isAnInstanceOf = theClassInstance.getName();
		this.partof = new HashSet<String>();
	}
	
	//includes icon
	public Conceptual_Object(String name, String display, String icon, 
			Conceptual_Class theClassInstance) {
		super(name, display, icon);
		this.isAnInstanceOf = theClassInstance.getName();
		this.partof = new HashSet<String>();
	}
	
	public String getClassInstance() {
		return this.isAnInstanceOf;
	}
	
	public void inheritFromClasses(Map <String, Conceptual_Class> classes) {
		Conceptual_Class parentClass = classes.get(isAnInstanceOf);
		inheritAttributes(parentClass);
		inheritRelations(parentClass);
		inheritFromConcepts();
	}
	
	/**Instantiated in each specific model**/
	protected void addPartOf() {}
	
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
	
	public String toString() {
		String retVal = super.toString();
		retVal = retVal.replaceFirst("Conceptual Class ", "Conceptual Object");
		return retVal;
	}
	
}
