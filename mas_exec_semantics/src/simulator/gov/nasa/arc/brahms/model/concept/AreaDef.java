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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author nrungta & jhunter
 * 
 * areadef 	::= 	areadef areadef-name { areadef-inheritance }
{
{ display : ID.literal-string ; }	//inherited from Concept
{ icon : ID.literal-string ; }		//inherited from Concept
{ GRP.attributes }					//inherited from Concept
{ GRP.relations }					//inherited from Concept
{ GRP.initial-facts }
}
areadef-name 	::= 	ID.name
 * 
 *
 */

public class AreaDef extends Concept{
	Set<String> areasExtended; //areadef inheritance
	
	public AreaDef(String name, String display) {
		super(name, display);
		this.areasExtended = new HashSet<String>();
	}
	
	//includes icon
	public AreaDef(String name, String display, String icon) {
		super(name, display, icon);
		this.areasExtended = new HashSet<String>();
	}
	
	//includes inherited areadefs
	public AreaDef(String name, String display,
			Set<String> areasExtended) {
		super(name, display);
		this.areasExtended = areasExtended;
	}
	
	//includes attributes
	public AreaDef(String name, String display,
			Set<String> areasExtended, Set<Attribute> attributes) {
		super(name, display);
		this.areasExtended = areasExtended;
		this.attributes = attributes;
	}
	
	//inclues attributes & relations
	public AreaDef(String name, String display,
			Set<String> areasExtended, Set<Attribute> attributes,
			Set<Relation> relations) {
		super(name, display);
		this.areasExtended = areasExtended;
		this.attributes = attributes;
		this.relations = relations;
	}
	
	//includes attributes, relations & facts
	public AreaDef(String name, String display,
			Set<String> areasExtended, Set<Attribute> attributes,
			Set<Relation> relations, Map<String, Fact> facts) {
		super(name, display);
		this.areasExtended = areasExtended;
		this.attributes = attributes;
		this.relations = relations;
		for (Map.Entry<String, Fact> entry : facts.entrySet()) {
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
		addclassExtended();
	}
	
	/**Instantiated in each specific model**/
	protected void addclassExtended() {}
	
	public Set<String> getAreasExtended(){ 
		return areasExtended;
	}
	
	public void inheritFromAreaDefs(Set<AreaDef> areaDefs) {
		Iterator<String> parentItr = areasExtended.iterator();
		while (parentItr.hasNext()) {
			String parentStr = parentItr.next();
			Iterator<AreaDef> areadefItr = areaDefs.iterator();
			while (areadefItr.hasNext()) {
				AreaDef ad = areadefItr.next();
				String name = ad.getName();
				if (parentStr.equals(name)) {
					inheritAttributes(ad);
				}
			}
		}
	}
	
	/**
	 * adds all attributes from parent areadefs
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
	
	/**
	 * adds all attributes from parent areadefs
	 * @param: ad the areadef to be inherited from
	 * @exception: attributes from 'this' cannot be overwritten
	 **/
	protected void inheritRelations(AreaDef ad) {
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
		retVal.append("areaDef " + super.getName() + " extends "); 
		Iterator<String> extItr = areasExtended.iterator();
		while(extItr.hasNext()) {
			retVal.append(extItr.next());
			if(extItr.hasNext()) {
				retVal.append(",  ");
			}
		}
		retVal.append("\n");
		return retVal.toString();
	}
}
