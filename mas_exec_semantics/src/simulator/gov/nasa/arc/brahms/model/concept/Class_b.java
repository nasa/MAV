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

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Attribute;
import gov.nasa.arc.brahms.model.Belief;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Fact;
import gov.nasa.arc.brahms.model.Relation;
import gov.nasa.arc.brahms.model.ThoughtFrame;
import gov.nasa.arc.brahms.model.WorkFrame;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.MapKey;
import gov.nasa.arc.brahms.model.expression.MapKeyValPair;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.simulator.Simulator;
import gov.nasa.arc.brahms.simulator.world.FactSet;
import gov.nasa.arc.brahms.vm.api.common.IClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nrungta & jhunter
 *
 *class 	::= 	class class-name { class-inheritance }
{
{ display : ID.literal-string ; }	//inherited from Concept
{ cost : ID.number ; }				//inherited from Basic
{ time_unit : ID.number ; }			//inherited from Basic
{ resource : ID.truth-value ; }		
{ icon : ID.literal-string ; }		//inherited from Concept
{ GRP.attributes }					//inherited from Concept
{ GRP.relations }					//inherited from Concept
{ GRP.initial-beliefs }				//inherited from Basic
{ GRP.initial-facts }				//inherited from Basic
{ GRP.activities }					//inherited from Basic
{ GRP.workframes }					//inherited from Basic
{ GRP.thoughtframes }				//inherited from Basic
}
class-name 	::= 	ID.name
class-inheritance 	::= 	extends class-name [ , class-name ]*
 */

public class Class_b extends Basic implements Cloneable, IClass{

	protected boolean resource;
	protected Set<String> classesExtended;
	private boolean inherited = false;

	/**
	 * Class Constructor
	 * @param name of class
	 * @param display for class
	 * @param cost for class
	 * @param time_unit for class
	 * @param resource for class
	 */
	public Class_b(String name, String display, double cost,
			int time_unit, boolean resource) {
		super(name, display, cost, time_unit);
		this.resource = resource;
		this.classesExtended = new HashSet<String>();
	}
	
	public Class_b(String name, String display, double cost,
			int time_unit) {
		super(name, display, cost, time_unit);
		this.classesExtended = new HashSet<String>();
	}
	
	public Class_b(String name, String display, String icon, double cost,
			int time_unit, boolean resource) {
		super(name, display, icon, cost, time_unit);
		this.resource = resource;
		this.classesExtended = new HashSet<String>();
	}
	
	public void addElements(){ 
		super.addElements();
		addclassExtended();
	}
	
	/**Instantiated in each specific model**/
	protected void addclassExtended() {}
	
	public Set<String> getClassExtends() {
		return this.classesExtended;
	}
	
	public void setResource(boolean r) {
		this.resource = r;
	}
	
	public boolean getResource() {
		return resource;
	}
	
	/**
	 * Called from the driver of each model
	 * Inherits attributes, relations, beliefs, activities, wfs, tfs from class
	 * @param classes - all possible classes, checks if each is a parent
	 */
	public void inheritFromClasses(Map<String, Class_b> classes) {
		if (!inherited) {
			inherited = true;
			Iterator<String> parentItr = classesExtended.iterator();
			while (parentItr.hasNext()) {
				String parentStr = parentItr.next();
				Class_b parentClass = classes.get(parentStr);
				parentClass.inheritFromClasses(classes);// Parent inherits from
														// its parents first
				inheritAttributes(parentClass);
				inheritRelations(parentClass);
				inheritBeliefs(parentClass);
				inheritFacts(parentClass);
				try {
					inheritActivities(parentClass);
					inheritWorkframes(parentClass);
					inheritThoughtframes(parentClass);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}
	
	/**
	 * adds all attributes from parent class
	 * @param: memberClass the class to be inherited from
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
			if (!duplicate){
				Attribute newAtt = new Attribute(nextClsAtt.getName(), 
						nextClsAtt.getPrivacy(), nextClsAtt.getType());
				attributes.add(newAtt);
			}
		}
	}
	
	/**
	 * adds all relations from parent classes
	 * @param: the class to be inherited from
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
			if (!duplicate) {
				if (!(this instanceof Object_b) && 
						(nextClsRel.getPrivacy().equals("private"))) {
					if (Simulator.DEBUG)
						System.out.println("in class - NOT inheriting private rel" 
								+ nextClsRel.getName());
				} else {
					//add relation
					Relation newRel = new Relation(nextClsRel.getName(), 
							nextClsRel.getPrivacy(), nextClsRel.getType());
					relations.add(newRel);
				}
			}
			else
				throw new RuntimeException("***ERROR: cannot redefine "
						+ "inherited relations: " + nextClsRel.getName());
		}
		if (Simulator.DEBUG)
			System.out.println("RELATIONS: " + relations.toString());
	}
	
	/**
	 * inherits beliefs, won't overwrite beliefs, just ignores duplicates
	 * the belief closest to 'this' takes precedence
	 * @param memberClass
	 */
	protected void inheritBeliefs(Class_b memberClass) {
		for (Map.Entry<String, List<Belief>> parentBeliefs : memberClass.getBeliefs().entrySet())
		{
			if (parentBeliefs.getKey().contains(".")) { //don't overwrite, if exists
				List<Belief> oldBels = parentBeliefs.getValue();
				if (oldBels.get(0) != null && oldBels.get(0).getBelief() instanceof ValueExpression) {
					if (!(this.getBeliefs().containsKey(parentBeliefs.getKey()))) {
						Belief oldBelief = parentBeliefs.getValue().get(0); //only one belief for atts
						int index = parentBeliefs.getKey().indexOf(".");
						String obj = parentBeliefs.getKey().substring(0, (index));
						String att = parentBeliefs.getKey().substring((index+1));
						if (oldBelief.getBelief() instanceof ValueExpression)
							this.updateBeliefs(new Term(obj, att), (ValueExpression) oldBelief.getBelief());
						else { //is map
						}
					}
				} else if (oldBels.get(0) != null) { //map
					int index = parentBeliefs.getKey().indexOf(".");
					String obj = parentBeliefs.getKey().substring(0, (index));
					String att = parentBeliefs.getKey().substring((index+1));
					MapExpression mapexp = (MapExpression) oldBels.get(0).getBelief();
					Map<String, Value> map = mapexp.getMap();
					if (this.beliefExists(obj, att)) {
						Belief bel = this.getBelief(obj, att);
						MapExpression currmapexp = (MapExpression) bel.getBelief();
						Map<String, Value> currmap = currmapexp.getMap();
						//check if key exists already
						for (Map.Entry<String, Value> entry : map.entrySet()) {
							String key = entry.getKey();
							if (!(currmap.containsKey(key))) {
								 MapKey mapKey;
								    if (key.startsWith("\"")) 
								    	mapKey = new MapKey(key.substring(1, key.length()-2));
								    else
								    	mapKey = new MapKey(Integer.parseInt(key));
								    MapKeyValPair pair = new MapKeyValPair(mapKey, entry.getValue());
								    this.updateBeliefs(new Term(obj, att),  pair);
							}
						}
					} else {
						for (Map.Entry<String, Value> entry : map.entrySet()) {
						    String key = entry.getKey();
						    MapKey mapKey;
						    if (key.startsWith("\"")) 
						    	mapKey = new MapKey(key.substring(1, key.length()-2));
						    else
						    	mapKey = new MapKey(Integer.parseInt(key));
						    MapKeyValPair pair = new MapKeyValPair(mapKey, entry.getValue());
						    this.updateBeliefs(new Term(obj, att), pair);
						}
					}
				}
			} else { //relations, add all
	    		//add belief(s)
	    		List<Belief> oldList = parentBeliefs.getValue();
	    		for (int i = 0; i < oldList.size(); i++) {
	    			Expression newBel = oldList.get(i).getBelief();
    				this.updateBeliefs(parentBeliefs.getKey(), (RelationalExpression) newBel);
	    		}
			}
		}
		if (Simulator.DEBUG)
			System.out.println("BELIEFS: " + beliefs.toString());
	}
	
	/**
	 * inherit facts
	 */
	protected void inheritFacts(Class_b memberClass) {
		if (Simulator.DEBUG)
			System.out.println("FACTS: " + memberClass.getName());
		Map<String, List<Fact>> temp = new HashMap<String, List<Fact>>();
		for (Map.Entry<String, List<Fact>> entry : FactSet.getFacts().entrySet()) {
			if (entry.getKey().startsWith(memberClass.getName() + ".")) { //valExp
				Fact f = entry.getValue().get(0);//will only be one value for valexp
				ValueExpression oldValExp = (ValueExpression) f.getFact();
				ValueExpression newValExp = new ValueExpression(this.getName(),
						oldValExp.getAttName(), oldValExp.getEvalOp(), 
						oldValExp.getFactValue(memberClass, null).get(0));
				Fact newFact = new Fact(newValExp);
				String newKey = this.getName() + "." + oldValExp.getAttName();
				List<Fact> newList = new ArrayList<Fact>();
				newList.add(newFact);
				temp.put(newKey, newList);
			}
			else if (entry.getKey().startsWith(memberClass.getName() + " ")) { //relation
				List<Fact> fList = entry.getValue();
				String newKey = entry.getKey();
				newKey = newKey.replace(memberClass.getName(), this.getName());
				List<Fact> newList = new ArrayList<Fact>();
				try{
					newList.addAll(FactSet.getFact(newKey));
				}
				catch (Exception e){
					newList = new ArrayList<Fact>();
				}
				for (int i = 0; i < fList.size(); i++) {
					Fact f = fList.get(i);
					RelationalExpression oldRelExp = (RelationalExpression) f.getFact();
					RelationalExpression newRelExp = new RelationalExpression(this.getName(), 
							oldRelExp.getRelationName(), oldRelExp.getRhsObjRef());
					Fact newFact = new Fact(newRelExp);
					newList.add(newFact);
					temp.put(newKey, newList);
				}
			}
		}
		for (Map.Entry<String, List<Fact>> entry : temp.entrySet()) {
			List<Fact> fList = entry.getValue();
			for (int i = 0; i < fList.size(); i++) {
				Fact f = fList.get(i);
				if (f.getFact() instanceof RelationalExpression)
					FactSet.addFact(entry.getKey(), (RelationalExpression) f.getFact());
				else {
					String key = entry.getKey();
					int inx = key.indexOf(".");
					String obj = key.substring(0, inx);
					String att = key.substring(inx+1);
					FactSet.addFact(new Term(obj, att), f.getFact());
				}
			}
		}
	}
	
	
	/**
	 * inherits all activities, won't overwrite activities closest to 'this'
	 * @param memberClass
	 * @throws CloneNotSupportedException 
	 */
	protected void inheritActivities(Class_b memberClass) throws CloneNotSupportedException {
		Iterator<Activity> clsactItr = memberClass.getActivities().iterator();
		while(clsactItr.hasNext()) { //iterate through group activities
			Activity nextClsAct = clsactItr.next();
			boolean duplicate = false;
			Iterator<Activity> actItr = activities.iterator();
			while(actItr.hasNext()) { //check each inherited activity against
				Activity nextAct = actItr.next(); //current activity list
				if (nextAct.getName().equals(nextClsAct.getName()))
					duplicate = true;
			}
			if (!duplicate){
				Activity act = (Activity) nextClsAct.clone();
				activities.add(act);
			}
		}
	}
	
	/**
	 * inherits all wfs, won't overwrite wfs closest to 'this'
	 * @param memberClass
	 * @throws CloneNotSupportedException 
	 */
	protected void inheritWorkframes(Class_b memberClass) throws CloneNotSupportedException {
		Iterator<WorkFrame> clswfItr = memberClass.getWorkFrames().iterator();
		while(clswfItr.hasNext()) { //iterate through group workframes
			WorkFrame nextClsWf = clswfItr.next();
			boolean duplicate = false;
			Iterator<WorkFrame> wfItr = workFrames.iterator();
			while(wfItr.hasNext()) { //check each inherited wf against
				WorkFrame nextWf = wfItr.next(); //current wf list
				if (nextWf.getName().equals(nextClsWf.getName()))
					duplicate = true;
			}
			if (!duplicate) {
				Object newWF = nextClsWf.clone();
				workFrames.add((WorkFrame) newWF);
			} 
		}
	}
	
	/**
	 * inherits all tfs, won't overwrite tfs closest to 'this'
	 * @param memberClass
	 * @throws CloneNotSupportedException 
	 */
	protected void inheritThoughtframes(Class_b memberClass) throws CloneNotSupportedException {
		Iterator<ThoughtFrame> clstfItr = memberClass.getThoughtFrames().iterator();
		while(clstfItr.hasNext()) { //iterate through group workframes
			ThoughtFrame nextClsTf = clstfItr.next();
			boolean duplicate = false;
			Iterator<ThoughtFrame> tfItr = thoughtFrames.iterator();
			while(tfItr.hasNext()) { //check each inherited wf against
				ThoughtFrame nextTf = tfItr.next(); //current wf list
				if (nextTf.getName().equals(nextClsTf.getName()))
					duplicate = true;
			}
			if (!duplicate)
				thoughtFrames.add((ThoughtFrame) nextClsTf.clone());
		}
	}
	
	public String toString(){ 
		String retVal = "Class " + super.toString();
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			Class_b cb = (Class_b) super.clone();
			
			cb.name = new String(name);
			cb.display = new String(display);
		
			Object attr = ((HashSet<Attribute>) this.attributes).clone();
			cb.attributes = ((Set<Attribute>) attr);
			
			Object rel = ((HashSet<Relation>) this.relations).clone();
			cb.relations = ((Set<Relation>) rel);
			
			Object initBeliefs = ((HashMap<String, List<Belief>>) this.beliefs).clone();
			cb.beliefs = ((Map<String, List<Belief>>) initBeliefs);
			
			Object acts = ((HashSet<Activity>) this.activities).clone();
			cb.activities = ((Set<Activity>) acts);
			
			Object wfs = ((HashSet<WorkFrame>) this.workFrames).clone();
			cb.workFrames = ((Set<WorkFrame>) wfs);
			
			Object tfs = ((HashSet<ThoughtFrame>) this.thoughtFrames).clone();
			cb.thoughtFrames = ((Set<ThoughtFrame>) tfs);
			
			return cb;
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}
	}
}
