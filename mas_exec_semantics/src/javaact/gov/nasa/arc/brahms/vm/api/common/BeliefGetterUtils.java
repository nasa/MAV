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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Belief;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.Utils;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;

public class BeliefGetterUtils {
	
	public static Map<Object, Object> getBeliefAttributeMap(
			IConcept currConcept, Basic currBasic,
			IConcept lhsConcept,
			String attributeName, boolean includeUnknown,
			IContext ctx) throws InvalidTypeException, ExternalException {
		String beliefName = CommonUtils.constructBeliefName(currConcept, lhsConcept, attributeName);
		System.out.println("beliefName" + beliefName);
		System.out.println(currBasic.getBeliefs().toString() + "these are all the beliefs");
		List<Belief> beliefs = currBasic.getBeliefs().get(beliefName);
		System.out.println(beliefs.toString());
		System.out.println(beliefs.get(0).getClass().getTypeName() + "this is the className");
		System.out.println(currConcept.getName() + " this is the name of currConcept");
		System.out.println(lhsConcept.getName() + " this is the name of the lhsConcept");
		Map<Object, Object> higherLevelMap = new HashMap<Object, Object>();
		for (Belief b : beliefs) {
			
			if(!(b.getBelief() instanceof MapExpression)) 
				System.exit(1);
			
			MapExpression mapExp = (MapExpression) b.getBelief();
			Map<String, Value> map = mapExp.getMap();
			for (String str : map.keySet()) {
				// TODO: change types of "Val" to either primitive
				// or the ICONCEPT
				Value valStore = map.get(str);

				if (valStore instanceof SglObjRef) {
					SglObjRef sglObjRef = (SglObjRef) valStore;
					IConcept iConcept = CommonUtils.findConcept(Utils.getMas(), sglObjRef.getObjRefName());
					if (CommonExpUtils.isInteger(str)) {
						higherLevelMap.put(Integer.valueOf(str), iConcept);
					} else {
						higherLevelMap.put(str, iConcept);
					}
				} else if (valStore instanceof TplObjRef) {
					TplObjRef tplObjRef = (TplObjRef) valStore;
					IConcept iConcept = CommonUtils.findConcept(Utils.getMas(), tplObjRef.getObjRefName());
					if (CommonExpUtils.isInteger(str)) {
						higherLevelMap.put(Integer.valueOf(str), iConcept);
					} else {
						higherLevelMap.put(str, iConcept);
					}
				} else {
					higherLevelMap.put(str, map.get(str));
				}
			}
		}

		return higherLevelMap;
	}

	
}
