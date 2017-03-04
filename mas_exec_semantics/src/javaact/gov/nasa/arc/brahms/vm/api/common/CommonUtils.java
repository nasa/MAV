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

package gov.nasa.arc.brahms.vm.api.common;

import gov.nasa.arc.brahms.model.MultiAgentSystem;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class CommonUtils {
	
	public static String getLhsIdentifier(IConcept currConcept,
			IConcept lhsConcept) {
		if (currConcept.getName().equals(lhsConcept.getName())) {
			return "current";
		} else {
			return lhsConcept.getName();
		}
	}
	
	public static String constructBeliefName(IConcept currConcept,
			IConcept lhsConcept, String attributeName) {
		return getLhsIdentifier(currConcept, lhsConcept) + "." +
													attributeName;
	}

	public static String constructRelationName(IConcept currConcept,
			IConcept lhsConcept, String relation) {
		return getLhsIdentifier(currConcept, lhsConcept) + " " +
														relation;
	}
	
	public static IConcept findConcept(MultiAgentSystem MAS, String conceptName) {
		if (AbstractExternalActivity.allConcepts.containsKey(conceptName))
			return AbstractExternalActivity.allConcepts.get(conceptName);

		IObject obj = findObject(MAS, conceptName);
		if (obj != null)
			return obj;
		IAgent agt = findAgent(MAS, conceptName);
		if (agt != null)
			return agt;
		throw new RuntimeException("could not find concept: " + conceptName);
	}

	public static IObject findObject(MultiAgentSystem MAS, String objName) {

		if(!MultiAgentSystem.objNameToObjectMap.containsKey(objName))
			return null;
		
		Object_b obj = MultiAgentSystem.objNameToObjectMap.get(objName);
		IObject iObj = new JObject(obj);
		AbstractExternalActivity.allConcepts.put(obj.getName(), iObj);
		return iObj;
	}

	public static IAgent findAgent(MultiAgentSystem MAS, String agName) {
		
		if(!MultiAgentSystem.agNameToAgentMap.containsKey(agName))
			return null;
		
		Agent ag = MultiAgentSystem.agNameToAgentMap.get(agName);
		IAgent iAgt = new JAgent(ag);
		AbstractExternalActivity.allConcepts.put(ag.getName(), iAgt);
		return iAgt;
		
	}
	
	public static Term initializeNewTerm(IConcept currConcept, 
							IConcept lhsConcept, String beliefName) {
		if(lhsConcept.getName().equals(currConcept.getName()))
			return new Term("current", beliefName);
		else
			return new Term(lhsConcept.getName(), beliefName);
	}

}
