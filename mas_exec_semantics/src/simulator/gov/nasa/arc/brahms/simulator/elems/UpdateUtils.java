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

package gov.nasa.arc.brahms.simulator.elems;

import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Attribute;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.comparison.EvalValCompExpOpExp;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.IntegerValue;
import gov.nasa.arc.brahms.model.expression.MapKey;
import gov.nasa.arc.brahms.model.expression.MapKeyValPair;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.Value;

public class UpdateUtils {
	
	public static List<Value> getValuesOfExpression(Basic b, 
			EvalValCompExpOpExp eval, Map<String, Value> varCtx,
			boolean getLeftExpression) {
		
		Expression exp;
		MapKey mapIndex; 
		Value keyVar; 
		
		if(getLeftExpression) {
			exp = eval.getLhsExp();
			mapIndex = eval.getLMapKey();
			keyVar = eval.getLKeyVar();
			
		} else {
			exp = eval.getRhsExp();
			mapIndex = eval.getRMapKey();
			keyVar = eval.getRKeyVar();
		}
			
		String attributeName = UpdateUtils.getAttributeNameString(eval, getLeftExpression);
		
		eval.setBasicAndFrame(b, null);	
		return eval.getValueOfExp(exp, attributeName, mapIndex, keyVar, varCtx);
			
	}
	
	public static boolean assigningMap(Basic b, String attrName) {
		boolean isMap = false;
		System.out.println("attrName " + attrName);
		for(Attribute attr : b.getAttributes()) {
			System.out.println(attr.getName() + " name" );
			if(attr.getName().equals(attrName) &&
					attr.getType().equals("map"))
				return true;
		}
		
		return isMap;
	}
	
	
	public static String getAttributeNameString(Expression exp, boolean getLeftExpression) {
		String attributeName;
		if(getLeftExpression) {
			attributeName = exp.getLAtt();
		} else 
			attributeName = exp.getRAtt();
		
		if (attributeName == null || attributeName.equals("")) {
			if(getLeftExpression)
				attributeName = exp.getLhsExp().getAttributeString();
			else attributeName = exp.getRhsExp().getAttributeString();
		}
		return attributeName;
	}
	
	public static boolean isMapExpression(MapKey mapKey, Value keyVar) {
		if(mapKey != null | keyVar != null) return true;
		return false;
	}
	
	public static MapKeyValPair createNewMapExpression(Basic b, Basic lhsConcept,
			Expression lhsExp, String lAttributeName,
			MapKey lMapIndex, Value lKeyVar, List<Value> rVals, 
									Map<String, Value> varCtx) {
		MapKeyValPair mapKeyValPair;
		if(lMapIndex != null) {
			System.out.println("lMapIndex" + lMapIndex.toString());
			mapKeyValPair = new MapKeyValPair(lMapIndex, rVals.get(0));
		
		} else  {
			assert (lKeyVar != null);
			Value contextVal = varCtx.get(lKeyVar.toString());
		//	System.out.println("context Val :" + contextVal.toString());
			MapKey mapKey;
			if(contextVal instanceof IntegerValue) {
				mapKey = new MapKey(((IntegerValue) contextVal).getIntValue());
			} else if(contextVal instanceof StringValue) {
				mapKey = new MapKey(((StringValue) contextVal).getStringValue());
			} else {
				throw new RuntimeException("unsupported map key");
			}
			mapKeyValPair = new MapKeyValPair(mapKey, rVals.get(0));
		}
			
		return mapKeyValPair;
	}
	
}
