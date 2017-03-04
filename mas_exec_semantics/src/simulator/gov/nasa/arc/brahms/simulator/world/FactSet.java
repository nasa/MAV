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

package gov.nasa.arc.brahms.simulator.world;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Fact;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.MapKeyValPair;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.ValueExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactSet {
	static StringBuilder factUpdates = new StringBuilder();
	protected static Map<String, List<Fact>> facts = new HashMap<String, List<Fact>>();
	
	

	public static StringBuilder getFactUpdates(){
		return factUpdates;
	}
	
	public static void addFact(Term lhs, Expression exp) {	
		if (exp instanceof ValueExpression)
			updateFacts(lhs, (ValueExpression) exp);
		else
			updateFacts(lhs, (MapKeyValPair) exp);
	}
	
	public static void addFact(String key, RelationalExpression rel) {
		updateFacts(key, rel);
	}
	
	public static void updateFacts(Term lhs, ValueExpression exp) {
		boolean createNew = false;
		String lhsString = lhs.getObjRefName() + "." + lhs.getAttrName();
		if (facts.get(lhsString) != null) {//fact has been initialized
			List<Fact> newFct = facts.get(lhsString);
			if (newFct.isEmpty())
				createNew = true;
			else {
				Fact fct = newFct.get(0); //only one element
				fct.setFact(exp);
				facts.put(lhsString, newFct);
				//logFactUpdates(newFct, null, null);
			}
		} else 
			createNew = true;
		if (createNew) {//fact has not yet been initialized
			List<Fact> newFct = new ArrayList<Fact>();
			newFct.add(new Fact(exp));
			facts.put(lhsString, newFct);
			//logFactUpdates(newFct, null, null);
		}
	}
	
	public static void updateFacts(Term lhs, MapKeyValPair exp) {
		boolean createNewMap = false;
		String lhsString = lhs.getObjRefName() + "." + lhs.getAttrName();
		if (facts.get(lhsString) != null) {//fact has been initialized
			List<Fact> newFct = facts.get(lhsString);
			if (newFct.isEmpty())
				createNewMap = true;
			else {
				Fact fct = newFct.get(0);
				if (fct.getFact() instanceof MapExpression) {
					MapExpression map = (MapExpression) fct.getFact();
					map.add(exp);
					//logFactUpdates(newFct, lhs, exp);
				} else {
					createNewMap = true;
					newFct.clear();
				}
			}
		} else
			createNewMap = true;
		if (createNewMap) {//fact has not yet been initialized
			List<Fact> newFct = new ArrayList<Fact>();
			MapExpression map = new MapExpression(lhs.getAttrName()); //create new map
			newFct.add(new Fact(map));
			map.add(exp);
			facts.put(lhsString, newFct);
			//logFactUpdates(newFct, null, null);
		}
	}
	
	public static void updateFacts(String key, RelationalExpression rel) {
		if (facts.get(key) != null) { //fact for key exists
			List<Fact> fcts = facts.get(key);
			boolean newFact = true; //don't add same fact twice
			for (int i = 0; i < fcts.size(); i++) {
				Fact oldFct = fcts.get(i);
				RelationalExpression oldExp = (RelationalExpression) oldFct.getFact();
				if (oldExp.getRhsObjRef().equals(rel.getRhsObjRef())) {
					newFact = false;
					if (oldExp.getTruthVal() != rel.getTruthVal())
						oldExp.setTruthVal(rel.getTruthVal());
				}
			}
			if (newFact) {
				fcts.add(new Fact(rel));
				facts.put(key, fcts);
				//logFactUpdates(fcts, null, null);
			}
		} else { //no fact for key
			List<Fact> newFct = new ArrayList<Fact>();
			newFct.add(new Fact(rel));
			facts.put(key, newFct);
			//logFactUpdates(newFct, null, null);
		}
	}
	
	/**
	 * Returns the set of facts
	 * @return Map<String, Fact>
	 */
	public static Map<String, List<Fact>> getFacts() {
		return facts;
	}
	
	/**
	 * Returns true if the given key is contained in the set of facts
	 * @param key
	 * @return
	 */
	public static boolean factExists(String key) {
		//System.out.println(facts);
		if (facts.containsKey(key))
			return true;
		return false;
	}
	
	/**
	 * Returns true if the given key is contained in the set of facts
	 * @param key
	 * @return
	 */
	public static boolean factExists(String objRefName, String attrName) {
		if (facts.containsKey(objRefName + "." + attrName))
			return true;
		return false;
	}
	
	/**
	 * Returns the fact associated with the given key
	 * or null if none exists
	 * @param key
	 * @return
	 */
	public static List<Fact> getFact(String key) {
		if (factExists(key))
			return facts.get(key);
		return null;
	}
	
	/**
	 * Returns the fact associated with the given key
	 * or null if none exists
	 * @param key
	 * @return
	 */
	public static Fact getFact(String objRefName, String attrName) {
		if (factExists(objRefName + "." + attrName))
			return facts.get(objRefName + "." + attrName).get(0);
		return null;
	}
	
	/**
	 * Updates facts
	 * @param Basic who is updating facts b
	 * @param key the key for the map: key, value pair
	 * @param exp the value for the map: key, value pair
	 */
	public static void updateFacts(Basic b, String key, Expression exp) {
		//Facts can't use current, so find out which agent/object the fact is about
		if (key.contains("current.") || key.contains("current "))
			key = key.replaceFirst("current", b.getName());
		if (exp instanceof ValueExpression) {
			ValueExpression oldExp = (ValueExpression) exp;
			Frame frame = null;
			try {
				frame = (Frame) b.getCurrentWorkFrame().get(0);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			ValueExpression newExp = new ValueExpression(b.getName(),
					oldExp.getAttName(), EvaluationOperator.EQ,
					oldExp.getValue(b, frame).get(0));
			int index = key.indexOf(".");
			updateFacts(new Term(key.substring(0, index), 
					key.substring(index+1)), newExp);
		} else if (exp instanceof RelationalExpression) {
			RelationalExpression newExp = (RelationalExpression) exp;
			if (newExp.getLhsObjRef().equals("current"))
				newExp.setLHSObjRef(b.getName());
			updateFacts(key, newExp);
		} else { //exp instanceof Map
			MapKeyValPair oldExp = (MapKeyValPair) exp;
			int index = key.indexOf(".");
			updateFacts(new Term(key.substring(0, index), 
					key.substring(index+1)), oldExp);
		}
	
	}
	
	public static void clearMapFacts(Basic b, Term lhs) {
		
		String obj = lhs.getObjRefName();
		obj = obj.replaceFirst("current", b.getName());
		if (facts.get(obj + "." + lhs.getAttrName()) != null) {
			List<Fact> mapFcts = facts.get(obj + "." + lhs.getAttrName());
			/*Neha, I could probably work around this if necessary, but this 
			 * makes for much less complicated checking in several areas (is 
			 * list empty VS is list empty? is 1st element map? is map empty?*/
			mapFcts.clear();
		}
		ValueExpression valExp = new ValueExpression(obj,
				lhs.getAttrName(), EvaluationOperator.EQ, new 
				SglObjRef("unknown"));
		updateFacts(new Term(obj, lhs.getAttrName()), valExp);
		
	}
	
	public static void retractFact(Basic b, String key, RelationalExpression rel) {
		RelationalExpression newExp = rel;
		if (key.startsWith("current")) {
			newExp = new RelationalExpression(b.getName(),
				rel.getRelationName(), rel.getRhsObjRef());
			key = key.replace("current", b.getName());
		}
		if (facts.get(key) != null) { //fact for key exists
			List<Fact> fcts = facts.get(key);
			int indexOfRemoved = -1;
			for (int i = 0; i < fcts.size(); i++) {
				Fact oldFct = fcts.get(i);
				RelationalExpression oldExp = (RelationalExpression) oldFct.getFact();
				if (oldExp.getRhsObjRef().equals(newExp.getRhsObjRef()))
					indexOfRemoved = i;
			}
			if (indexOfRemoved > -1) {
				fcts.remove(indexOfRemoved);
			}
		}
	}
	
	public static void removeFact(String key) {
		facts.remove(key);
	}
	
	public static void printFacts() {
		String retVal = "[";
		for(Map.Entry<String, List<Fact>> entry: facts.entrySet()) {
			List<Fact> fts = entry.getValue();
			for (int i = 0; i < fts.size(); i++) {
				retVal += entry.getKey() + "/";
				Expression exp = fts.get(i).getFact();
				if (exp instanceof ValueExpression)
					retVal += ((ValueExpression) exp).toString();
				if (exp instanceof RelationalExpression)
					retVal += ((RelationalExpression) exp).toString();
				if (exp instanceof MapExpression)
					retVal += ((MapExpression) exp).toString();
			}
		}
		retVal += "]";
		System.out.println(retVal);
	}

}
