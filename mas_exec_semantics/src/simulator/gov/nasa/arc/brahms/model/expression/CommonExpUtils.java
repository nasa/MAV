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

package gov.nasa.arc.brahms.model.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Knowledge;
import gov.nasa.arc.brahms.model.MultiAgentSystem;
import gov.nasa.arc.brahms.model.Variable;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.simulator.world.FactSet;

public class CommonExpUtils {
	
	public static Basic findConcept(Basic b, String conceptName) {
		if(conceptName.equals("current"))
			return CommonExpUtils.findConcept(b.getName());
		return CommonExpUtils.findConcept(conceptName);
	}
	
	public static Basic findConcept(String conceptName) {
		if(MultiAgentSystem.agNameToAgentMap.containsKey(conceptName))
			return MultiAgentSystem.agNameToAgentMap.get(conceptName);
		
		if(MultiAgentSystem.objNameToObjectMap.containsKey(conceptName))
			return MultiAgentSystem.objNameToObjectMap.get(conceptName);
		
		System.err.println("could not find basic :" + conceptName);
		return null;
	}
	
	public static List<Basic> findConceptsofType(String type) {
		List<Basic> types = new ArrayList<Basic>();
		if(MultiAgentSystem.typeNameToInstancesMap.containsKey(type)) {
			return MultiAgentSystem.typeNameToInstancesMap.get(type);
		}
		return types;
	}
	
	public static boolean hasBeliefsOrFactsFor(Frame f, Basic currBasic, String index) {
		if(f != null && !f.operatesOnFacts())
			return currBasic.getBeliefs().containsKey(index);
		else 
			return FactSet.getFacts().containsKey(index);
	}
	
	
	
	public static List<Knowledge> getBeliefsOrFactsFor(Frame f,
			Basic currBasic, String index) {
		List<Knowledge> elements = new ArrayList<Knowledge>();
		if ((f ==null || !f.operatesOnFacts())
				&& currBasic.getBeliefs().containsKey(index))
			elements.addAll(currBasic.getBeliefs().get(index));
		else if (FactSet.getFacts().containsKey(index))
			elements.addAll(FactSet.getFacts().get(index));
		return elements;
	}
	
	
	public static String getBeliefOrFactAttribute(Frame f, Basic currBasic, Basic lhsBasic,
			String attributeName) {
		//the nullity check is for junit tests, a null frame might be passed.
		if(f!= null && !f.operatesOnFacts()) {
			return getBeliefAttribute(currBasic, lhsBasic, attributeName);
		}
		else {
			return getFactAttribute(lhsBasic, attributeName);
		}
	}
	
	public static String getBeliefOrFactAttribute(Frame f, Basic b, String objName, String attribute) {
		if(f == null || !(f.operatesOnFacts())) {
			if(objName.equals(b.getName()))
				return "current."+attribute;
			else
				return objName+"."+attribute;
		}
		else {
			if(objName.equals("current"))
				return b.getName()+"."+attribute;
			else
				return objName+"."+attribute;
		}
	}
	
	public static String getBeliefAttribute(Basic currBasic, Basic lhsBasic,
			String attributeName) {
		return getBeliefIdentifier(currBasic, lhsBasic)
					+ "." +  attributeName;
	}
	
	public static String getFactAttribute(Basic lhsBasic,
			String attributeName) {
		return getFactIdentifier(lhsBasic)+ "." +  attributeName;
	}
	
	public static String getBeliefRelation(Basic currBasic, Basic lhsBasic,
			String relationName) {
		return getBeliefIdentifier(currBasic, lhsBasic)
				+ " " +  relationName;
	}
	
	public static String getFactRelation(Basic lhsBasic,
			String relationName) {
		return getFactIdentifier(lhsBasic)+ " " +  relationName;
	}
	
	public static String getBeliefOrFactRelation(Frame f, Basic currBasic, Basic lhsBasic,
			String relationName) {
		 if(f == null || !f.operatesOnFacts())
			return getBeliefRelation(currBasic, lhsBasic, relationName);
		else
			return getFactRelation(lhsBasic, relationName);
	}
	

	public static String getFactAttributeRelation(Basic lhsBasic,
			String attribute, String relationName) {
		if(attribute != null && !attribute.equals(""))
			return getFactIdentifier(lhsBasic) + "." + attribute
				+ " " +  relationName;
		else return getFactRelation(lhsBasic, relationName);
	}
	
	public static String getBeliefIdentifier(Basic currBasic, Basic lhsBasic) {
		if(lhsBasic == null)
			return "";
		if(currBasic.getName().equals(lhsBasic.getName())) {
			return "current";
		} else {
			return lhsBasic.getName();
		}
	}
	
	public static String getFactIdentifier(Basic lhsBasic) {
		if(lhsBasic == null)
			return "";
		return lhsBasic.getName();
		
	}
	
	public static Variable getVariable(Expression currExp,
			Map<String, Value> varCtx, Frame f) {
		
		if (currExp != null && currExp instanceof VariableValue) {
			String varName = ((VariableValue) currExp).getVarName();
			if (!varCtx.containsKey(varName)) {
				return f.getVariable(varName);
			}
		}
		return null;
	}
	
	public static Map<Basic, Basic> getTargetsOfAttribute(Frame f, Basic currBasic,
			List<Basic> bindings, String attributeName, MapKey mapIndex) {
		Map<Basic, Basic> bindingToTarget = new HashMap<Basic, Basic>();
		for (Basic lhsBasic : bindings) {
			bindingToTarget.putAll(getTargetsOfAttribute(f, currBasic, lhsBasic,
					attributeName, mapIndex));

		}
		return bindingToTarget;
	}
	
	public static Map<Basic, Basic> getTargetsOfAttribute(Frame f, Basic currBasic,
			Basic lhsBasic, String attributeName, MapKey mapIndex) {
		Map<Basic, Basic> bindingToTarget = new HashMap<Basic, Basic>();
		String index = CommonExpUtils.getBeliefOrFactAttribute(f,
				currBasic, lhsBasic, attributeName);
		if (!CommonExpUtils.hasBeliefsOrFactsFor(f, currBasic, index))
			return new HashMap<Basic, Basic>();
		List<Knowledge> allKnowElements = CommonExpUtils
				.getBeliefsOrFactsFor(f, currBasic, index);

		List<Value> values;
		if (mapIndex != null) {
			Expression exp = allKnowElements.get(0).getBeliefOrFactExp();
			values = ((MapExpression) exp).getValue(mapIndex, currBasic, f);
		} else {
			Expression exp = allKnowElements.get(0).getBeliefOrFactExp();
			values = exp.getValue(currBasic, f);
		}
		Basic oneTarget = CommonExpUtils.findConcept(values.get(0)
				.toString());
		if (oneTarget != null)
			bindingToTarget.put(lhsBasic, oneTarget);
		return bindingToTarget;
	}

	
	public static boolean variableMatchesExpression(Basic b, Frame f, Expression exp,
			Variable var, Map<String, Value> varCtx) {
		if (exp != null && exp.isVariableValue()
				&& ((VariableValue) exp).getVarName().equals(var.getVarName())) {
			return true;
		}
		return false;
	}
	
	public static List<Value> getVariableBindings(Basic b, Frame f, Expression exp,
			Variable var, Map<String, Value> varCtx) {
		if (exp != null && exp.isVariableValue()
				&& ((VariableValue) exp).getVarName().equals(var.getVarName())) {
			List<Value> possibleBound = new ArrayList<Value>();
			for (Basic currBasic : exp.getPossibleBindings(b, f)) {
				possibleBound.add(new TplObjRef(currBasic.getName()));
			}
			return possibleBound;
		}
		return null;
	}
	
	public static List<Basic> getBoundConcepts(Basic b, Frame f, Expression exp,
			Map<String, Value> varCtx) {
		List<Basic> concepts = new ArrayList<Basic>();
		if (exp.isVariableValue() && varCtx.size() > 0
				&& varCtx.containsKey(((VariableValue) exp).getVarName())) {
			Value targetValue = varCtx.get(((VariableValue) exp).getVarName());
			if(!CommonExpUtils.isPrimitiveType(targetValue)) {
				String conceptName = "";
				TplObjRef tplObjRef = (TplObjRef) targetValue;
				if(tplObjRef.getObjRefName().equals("current"))
					conceptName = b.getName();
				else 
					conceptName = tplObjRef.getObjRefName();
				assert(!conceptName.equals(""));
				concepts.add(CommonExpUtils.findConcept(conceptName));
			}
		} else if(varCtx.containsKey(exp.toString())) {
			concepts.add(CommonExpUtils.findConcept(b, varCtx.get(exp.toString()).toString()));
		} else {
			concepts = exp.getPossibleBindings(b, f);
		}
		return concepts;
	}

	public static boolean isPrimitiveType(Expression exp) {
		if(exp instanceof IntegerValue || exp instanceof StringValue ||
				exp instanceof DoubleValue || exp instanceof SymbolValue ||
				exp instanceof BooleanValue)
			return true;
		return false;
	}
	
	public static Basic getSingleBoundConcepts(Basic b, Frame f, Expression exp,
			Map<String, Value> varCtx) {
		List<Basic> concepts = getBoundConcepts(b, f, exp, varCtx);
		if(concepts.size() == 1)
			return concepts.get(0);
		return null;
	}
	
	public static List<Value> getKeyVarValues(Basic b, Frame f, Expression exp,
			String att, Map<String, Value> varCtx) {
		String index;
		if (exp instanceof Term) {
			Term t = (Term) exp;
			index = CommonExpUtils.getBeliefOrFactAttribute(f, b,
					t.getObjRefName(), t.getAttributeString());
		} else if (exp.isVariableValue()) {
			VariableValue varVal = (VariableValue) exp;
			index = CommonExpUtils.getBeliefOrFactAttribute(f, b,
					varCtx.get(varVal.getVarName()).toString(), att);
		} else if (exp instanceof ParameterValue) {
			//ParameterValue param = (ParameterValue) exp;
			throw new RuntimeException("need to handle parameter value");
		} else {
			throw new RuntimeException("need to handle exp");
		}

		//System.out.println("index := " + index);
		List<Value> possibleKeyVars = new ArrayList<Value>();
		for (Knowledge knowledge : CommonExpUtils.getBeliefsOrFactsFor(f, b,
				index)) {
			assert (knowledge.getBeliefOrFactExp() instanceof MapExpression);
			MapExpression mapExp = (MapExpression) knowledge
					.getBeliefOrFactExp();
			for (String str : mapExp.getMap().keySet()) {
				if (CommonExpUtils.isInteger(str))
					possibleKeyVars.add(new IntegerValue(Integer.valueOf(str)));
				else
					possibleKeyVars.add(new StringValue(str));
			}
		}
		return possibleKeyVars;
	}
	
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
}
