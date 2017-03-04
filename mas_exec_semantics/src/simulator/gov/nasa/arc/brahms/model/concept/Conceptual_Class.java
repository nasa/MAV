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

import gov.nasa.arc.brahms.model.Attribute;
import gov.nasa.arc.brahms.model.Relation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * author jhunter
 * conceptual-class 	::= 	conceptual_class conceptual-class-name 
 * { conceptual-class-inheritance }
 * {
 * { display : ID.literal-string ; }
 * { icon : ID.literal-string ; }
 * { GRP.attributes }
 * { GRP.relations }
 * }
**/

public class Conceptual_Class extends Concept {
	
	protected Set<String> classesExtended;
	
	public Conceptual_Class(String name, String display) {
		super(name, display);
		this.classesExtended = new HashSet<String>();
	}
	
	public Conceptual_Class(String name, String display, String icon) {
		super(name, display, icon);
		this.classesExtended = new HashSet<String>();
	}
	
	public void addElements() {
		addAttributes();
		addRelations();
		addclassExtended();
	}
	
	/**Instantiated in each specific model**/
	protected void addclassExtended() {}
	
	public Set<String> getClassExtends() {
		return this.classesExtended;
	}
	
	/**
	 * Called from the driver of each model
	 * Inherits attributes & relations from conceptual class
	 * @param classes- all possible conceptual classes, checks if each is a parent
	 */
	public void inheritFromClasses(Map <String, Conceptual_Class> classes) {
		Iterator<String> parentItr = classesExtended.iterator();
		while (parentItr.hasNext()) {
			String parentStr = parentItr.next();
			Conceptual_Class parentClass = classes.get(parentStr);
			inheritAttributes(parentClass);
			//inheritRelations(parentClass);
		}
	}
	
	/**
	 * adds all attributes from parent groups
	 * @param: the conceptual class to be inherited from
	 * @exception: attributes from 'this' cannot be overwritten
	 **/
	protected void inheritAttributes(Concept memberClass) {
		Iterator<Attribute> clsattItr = memberClass.getAttributes().iterator();
		while(clsattItr.hasNext()) { //iterate through group attributes
			boolean duplicate = false;
			Attribute nextClsAtt = clsattItr.next();
			Iterator<Attribute> attItr = attributes.iterator();
			while(attItr.hasNext()) { //check each inherited attribute against
				Attribute nextAtt = attItr.next(); //current attribute list
				if (nextAtt.getName().equals(nextClsAtt.getName()))
					duplicate = true;
			}
			if (!duplicate)
				attributes.add(nextClsAtt);
			else
				throw new RuntimeException("***ERROR: cannot redefine "
						+ "inherited attributes: " + nextClsAtt.getName());
		}
	}
	
	/**
	 * adds all relations from parent classes
	 * @param: the conceptual class to be inherited from
	 * @exception: can't overwrite or redefine attributes from parents
	 **/
	protected void inheritRelations(Concept memberClass) {
		Iterator<Relation> clsRelItr = memberClass.getRelations().iterator();
		while(clsRelItr.hasNext()) { //iterate through group attributes
			boolean duplicate = false;
			Relation nextClsRel = clsRelItr.next();
			Iterator<Relation> relItr = relations.iterator();
			while(relItr.hasNext()) { //check each inherited attribute against
				Relation nextRel = relItr.next(); //current attribute list
				if (nextRel.getName().equals(nextClsRel.getName()))
					duplicate = true;
			}
			if (!duplicate)
				relations.add(nextClsRel);
			else
				throw new RuntimeException("***ERROR: cannot redefine "
						+ "inherited attributes: " + nextClsRel.getName());
		}
	}
	
	public String toString() {
		String retVal = "Conceptual Class " + super.toString();
		return retVal;
	}
	
}
