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

/**
 * 
 * @author nrungta & josie
 * path 	::= 	path path-name		//inherited from Concept
 * {
 * { display : ID.literal-string ; }	//inherited from Concept
 * area1 : ARE.area-name ;
 * area2 : ARE.area-name ;
 * { distance : ID.unsigned ; }
 * }
 *
 *
 * Note: Path inherits from Concept, which means it has icon,
 * attributes and relations, which Path shouldn't have
 * Related methods throw exceptions
 */
public class Path extends Concept{
	
	String area1;
	String area2;
	
	int distance;
	
	public Path(String name, String display, 
			String area1, String area2, int distance) {
		super(name, display);
		
		this.area1 = area1;
		this.area2 = area2;
		
		this.distance = distance;
	}
	
	public String getArea1(){ 
		return area1;
	}
	
	public String getArea2() {
		return area2;
	}
	
	public int getDistance(){ 
		return distance;
	}
	
	public String getIcon() {
		throw new RuntimeException("path's can't have icons");
	}
	protected void addAttributes() {
		throw new RuntimeException("path's can't have attributes");
	}
	protected void addRelations() {
		throw new RuntimeException("path's can't have relations");
	}
	public Set<Attribute> getAttributes() {
		throw new RuntimeException("path's can't have attributes");
	}
	public Set<Relation> getRelations() {
		throw new RuntimeException("path's can't have relations");
	}
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("path " + super.getName() + " {\n");
		retVal.append("area1: " + this.area1 +";\n");
		retVal.append("area2: " + this.area2 + ";\n");
		retVal.append("distance: " + this.distance + "\n");
		retVal.append("} \n");
		return retVal.toString();
	}
}
