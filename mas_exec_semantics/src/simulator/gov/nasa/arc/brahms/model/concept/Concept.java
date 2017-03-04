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

import java.util.Set;
import java.util.HashSet;

/**
 * 
 * @author jhunter
 * Basic Syntax for Concepts:
 * conceptType ::= concept concept-name 
 * {
 * {display : ID.literal-string;}
 * {icon 	: ID.literal-string;}
 * {GRP.attributes}
 * {GRP.relations}
 * }
 * 
 * concept-name ::= ID.name
 * 
 * This is a the basic parts that make up a concept.  The exception is
 * Path, which will only use name and display from this class
 */
public abstract class Concept {
	//variables
	protected String name;
	protected String display;
	protected String icon = null;
	protected Set<Attribute> attributes = null;
	protected Set<Relation> relations = null;
	
	/**
	 * Concept Constructor, initializes vars
	 * @param n - name of the concept
	 * @param d - display for the concept
	 * @param i - icon is optional
	 */
	protected Concept(String n, String d) {
		this.name = n;
		this.display = d;
		this.attributes = new HashSet<Attribute>();
		this.relations = new HashSet<Relation>();
	}
	
	protected Concept(String n, String d, String i) {
		this.name = n;
		this.display = d;
		this.icon = i;
		
		this.attributes = new HashSet<Attribute>();
		this.relations = new HashSet<Relation>();
	}
		
	public String getName() {
		return name;
	}

	public String getDisplay() {
		return display;
	}

	public String getIcon() {
		return icon;
	}
	
	/** Instantiated in each specific model**/
	protected void addAttributes() {}
	/** Instantiated in each specific model**/
	protected void addRelations() {}
	
	public Set<Attribute> getAttributes() {
		return attributes;
	}

	public Set<Relation> getRelations() {
		return relations;
	}
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(name + "\n");
		if (attributes.size() > 0)
			retVal.append("attributes: " + attributes.toString() + "\n");
		if (relations.size() > 0)
			retVal.append("relations: " + relations.toString() + "\n");
		return retVal.toString();
	}
}
