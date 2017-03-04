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
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Fact;
import gov.nasa.arc.brahms.model.Relation;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.simulator.world.FactSet;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;

/**
 * 
 * @author nrungta & jhunter
 *
 *area 	::= 	area area-name
instanceof ADF.areadef-name
{ partof area-name }
{
{ display : ID.literal-string ; }	//inherited from Concept
{ icon : ID.literal-string ; }		//inherited from Concept
{ GRP.attributes }					//inherited from Concept
{ GRP.relations }					//inherited from Concept
{ GRP.initial-facts }
}
area-name 	::= 	ID.name

 */

public class Area extends Concept {
	
	String instanceOfAreaDef = "";
	String partOfArea = "";
	int id;
	
	public Area(String name, String display, 
			String instanceOfAreaDef) {
		super(name, display);
		this.instanceOfAreaDef = instanceOfAreaDef;
	}
	
	//includes partof
	public Area(String name, String display, 
			String instanceOfAreaDef, String partOfArea) {
		super(name, display);
		this.instanceOfAreaDef = instanceOfAreaDef;
		this.partOfArea = partOfArea;
	}
	
	//includes partof, attributes, relations, facts
	public Area(String name, String display, String instanceOfAreaDef,
			String partOfArea, Set<Attribute> attributes, Set<Relation> relations,
			Map<String, Fact> initialFacts) {
		super(name, display);
		this.instanceOfAreaDef = instanceOfAreaDef;
		this.partOfArea = partOfArea;
		this.attributes = attributes;
		this.relations = relations;
		for (Map.Entry<String, Fact> entry : initialFacts.entrySet()) {
			Expression exp = entry.getValue().getFact();
			if (exp instanceof RelationalExpression)
				FactSet.addFact(entry.getKey(), (RelationalExpression) exp);
			else {
				String key = entry.getKey();
				int idx = key.indexOf(".");
				FactSet.addFact(new Term(entry.getKey().substring(0, idx), 
						entry.getKey().substring(idx+1)), exp);
			}
		}
	}
	
	public void addElements() {
		addAttributes();
		addRelations();
	}	
	
	public String getInstanceOfAreaDef() {
		return instanceOfAreaDef;
	}
	
	public String getPartOfArea() {
		return partOfArea;
	}
	
	public void setID(int num) {
		this.id = num;
	}
	
	public int getID() {
		return id;
	}
	
	
	public void inheritFromInst(Set<AreaDef> areaDefs) {
		Iterator<AreaDef> parentItr = areaDefs.iterator();
		while (parentItr.hasNext()) {
			AreaDef parent = parentItr.next();
			if (parent.getName().equals(instanceOfAreaDef)) {
				inheritAttributes(parent);
			}
		}
	}
	
	/**
	 * adds all attributes from parent areadef
	 * @param: ad the areadef to be inherited from
	 * @exception: attributes from 'this' cannot be overwritten
	 **/
	protected void inheritAttributes(AreaDef ad) {
		Iterator<Attribute> adattItr = ad.getAttributes().iterator();
		while(adattItr.hasNext()) { //iterate through group attributes
			boolean duplicate = false;
			Attribute nextADAtt = adattItr.next();
			Iterator<Attribute> attItr = attributes.iterator();
			while(attItr.hasNext()) { //check each inherited attribute against
				Attribute nextAtt = attItr.next(); //current attribute list
				if (nextAtt.getName().equals(nextADAtt.getName()))
					duplicate = true;
			}
			if (!duplicate)
				attributes.add(nextADAtt);
			else
				throw new RuntimeException("***ERROR: cannot redefine "
						+ "inherited attributes: " + nextADAtt.getName());
		}
	}
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("area " + super.getName());
		if(!this.instanceOfAreaDef.equals("")) 
			retVal.append(" instanceof " + this.instanceOfAreaDef + " ");
		if(!this.partOfArea.equals(""))
			retVal.append(" partof " + this.partOfArea + "\n");
		return retVal.toString();
	}
}
