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
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.simulator.Simulator;
import gov.nasa.arc.brahms.simulator.world.FactSet;

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
 *group 	::= 	group group-name { group-membership }
{
{ display : ID.literal-string ; }	//inherited from concept
{ cost : ID.number ; }				//inherited from Basic
{ time_unit : ID.number ; }			//inherited from Basic
{ icon : ID.literal-string ; }		//inherited from concept
{ attributes }						//inherited from concept
{ relations }						//inherited from concept
{ initial-beliefs }					//inherited from Basic
{ initial-facts }					//inherited from Basic
{ activities }						//inherited from Basic
{ workframes }						//inherited from Basic
{ thoughtframes }					//inherited from Basic
}

 */

public class Group extends Basic {
	
	private boolean inherited = false;
	
	protected Set<String> memberOf;
	
	/**
	 * Group Constructor
	 * @param name for the group
	 * @param display for the group
	 * @param icon for the group is optional
	 * @param cost for the group
	 * @param timeUnit for the group
	 */
	public Group(String name, String display,  double cost, int timeUnit) {
		super(name, display, cost, timeUnit);
		this.memberOf = new HashSet<String>();	
	}
	
	public Group(String name, String display, String icon, double cost, 
			int timeUnit) {
		super(name, display, icon, cost, timeUnit);
		this.memberOf = new HashSet<String>();	
	}
	
	public void addElements(){ 
		super.addElements();
		addMemberOfs();
	}
	
	/** Instantiated in each specific model**/
	protected void addMemberOfs() {}
	
	public Set<String> getMemberOfs() {
		return memberOf;
	}
	
	/**
	 * Called from the driver of each model
	 * Inherits attributes, relations, beliefs, activities, wfs, tfs from group
	 * @param groups - all possible groups, checks if each is a parent
	 * @throws CloneNotSupportedException 
	 */
	public void inheritFromMembers(Set<Group> groups) {
		// Stop the group from constantly inheriting from its parents
		if (!inherited) {
			inherited = true;
			Iterator<Group> grpItr = groups.iterator();
			while (grpItr.hasNext()) {
				Group memberGroup = grpItr.next();
				if (!memberOf.contains(memberGroup.getName())) {
					continue;
				}
				memberGroup.inheritFromMembers(groups);// Tell parent to inherit
														// from its parents
														// first
				Set<String> parentIsMemberOf = memberGroup.getMemberOfs();
				memberOf.addAll(parentIsMemberOf);
				inheritAttributes(memberGroup);
				inheritRelations(memberGroup);
				inheritBeliefs(memberGroup);
				inheritFacts(memberGroup);
				try {
					inheritActivities(memberGroup);
					inheritWorkframes(memberGroup);
					inheritThoughtframes(memberGroup);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}

			}
		}
	}
	
	/**
	 * adds all attributes from parent groups
	 * @param: the group to be inherited from
	 * @exception: attributes from 'this' cannot be overwritten
	 **/
	public void inheritAttributes(Group memberGroup) {
		Iterator<Attribute> gpattItr = memberGroup.getAttributes().iterator();
		while(gpattItr.hasNext()) { //iterate through group attributes
			boolean duplicate = false;
			Attribute nextGpAtt = gpattItr.next();
			Iterator<Attribute> attItr = attributes.iterator();
			while(attItr.hasNext()) { //check each inherited attribute against
				Attribute nextAtt = attItr.next(); //current attribute list
				if (nextAtt.getName().equals(nextGpAtt.getName()))
					duplicate = true;
			}
			if (!duplicate) {
				if (!(this instanceof Agent) && 
						(nextGpAtt.getPrivacy().equals("private"))) {
					if (Simulator.DEBUG)
						System.out.println("in group - NOT inheriting private att" 
								+ nextGpAtt.getName());
				} else {
					//add attribute
					Attribute newAtt = new Attribute(nextGpAtt.getName(), 
							nextGpAtt.getPrivacy(), nextGpAtt.getType());
					attributes.add(newAtt);
				}
			}
			else{

			}
		}
	}
	
	/**
	 * adds all relations from parent groups
	 * @param: the group to be inherited from
	 * @exception: can't overwrite or redefine relations from parents
	 **/
	public void inheritRelations(Group memberGroup) {
		Iterator<Relation> gpRelItr = memberGroup.getRelations().iterator();
		while(gpRelItr.hasNext()) { //iterate through group attributes
			boolean duplicate = false;
			Relation nextGpRel = gpRelItr.next();
			Iterator<Relation> relItr = relations.iterator();
			while(relItr.hasNext()) { //check each inherited attribute against
				Relation nextRel = relItr.next(); //current attribute list
				if (nextRel.getName().equals(nextGpRel.getName()))
					duplicate = true;
			}
			if (!duplicate) {
				if (!(this instanceof Agent) && 
						(nextGpRel.getPrivacy().equals("private"))) {
					if (Simulator.DEBUG)
						System.out.println("in group - NOT inheriting private rel" 
								+ nextGpRel.getName());
				} else {
					//add relation
					Relation newRel = new Relation(nextGpRel.getName(), 
							nextGpRel.getPrivacy(), nextGpRel.getType());
					relations.add(newRel);
				}
			}
			else{
			}
		}
	}
	
	/**
	 * inherits beliefs, won't overwrite beliefs, just ignores duplicates
	 * the belief closest to 'this' takes precedence
	 * @param memberClass
	 */
	protected void inheritBeliefs(Group memberGroup) {
		for (Map.Entry<String, List<Belief>> parentBeliefs : memberGroup.getBeliefs().entrySet())
		{
			if (parentBeliefs.getKey().contains(".")) { //don't overwrite, if exists
				if (!(this.getBeliefs().containsKey(parentBeliefs.getKey()))) {
					Belief oldBelief = parentBeliefs.getValue().get(0); //only one belief for atts
					int index = parentBeliefs.getKey().indexOf(".");
    				String obj = parentBeliefs.getKey().substring(0, index);
    				String att = parentBeliefs.getKey().substring((index+1));
    				if (oldBelief.getBelief() instanceof ValueExpression)
    					this.updateBeliefs(new Term(obj, att), (ValueExpression) oldBelief.getBelief());
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
	}
		
	/**
	 * inherit facts
	 */
	protected void inheritFacts(Group memberGroup) {
		Map<String, List<Fact>> temp = new HashMap<String, List<Fact>>();
		for (Map.Entry<String, List<Fact>> entry : FactSet.getFacts().entrySet()) {
			if (entry.getKey().contains(memberGroup.getName() + ".")) {
				Fact f = entry.getValue().get(0);//will only be one value for valexp
				if (f.getFact() instanceof ValueExpression) {
					ValueExpression oldValExp = (ValueExpression) f.getFact();
					ValueExpression newValExp = new ValueExpression(this.getName(),
							oldValExp.getAttName(), oldValExp.getEvalOp(), 
							oldValExp.getFactValue(memberGroup, null).get(0));
					Fact newFact = new Fact(newValExp);
					String newKey = this.getName() + "." + oldValExp.getAttName();
					List<Fact> newList = new ArrayList<Fact>();
					newList.add(newFact);
					temp.put(newKey, newList);
				} 
			}
			else if (entry.getKey().contains(memberGroup.getName()+ " ")) {
				List<Fact> fList = entry.getValue();
				String newKey = entry.getKey();
				newKey.replace(memberGroup.getName(), this.getName());
				List<Fact> newList = FactSet.getFact(newKey);
				if (newList == null)
					newList = new ArrayList<Fact>();
				for (int i = 0; i < fList.size(); i++) {
					Fact f = fList.get(i);
					RelationalExpression oldRelExp = 
							(RelationalExpression) f.getFact();
					RelationalExpression newRelExp = new 
							RelationalExpression(this.getName(), 
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
	 * @param memberGroup
	 * @throws CloneNotSupportedException 
	 */
	public void inheritActivities(Group memberGroup) throws CloneNotSupportedException {
		Iterator<Activity> gpactItr = memberGroup.getActivities().iterator();
		while(gpactItr.hasNext()) { //iterate through group activities
			Activity nextGpAct = gpactItr.next();
			boolean duplicate = false;
			Iterator<Activity> actItr = activities.iterator();
			while(actItr.hasNext()) { //check each inherited activity against
				Activity nextAct = actItr.next(); //current activity list
				if (nextAct.getName().equals(nextGpAct.getName()))
					duplicate = true;
			}
			if (!duplicate) {
				activities.add((Activity) nextGpAct.clone());
			}
		}
	}
	
	/**
	 * inherits all wfs, won't overwrite wfs closest to 'this'
	 * @param memberGroup
	 * @throws CloneNotSupportedException 
	 */
	public void inheritWorkframes(Group memberGroup) throws CloneNotSupportedException {
		Iterator<WorkFrame> gpwfItr = memberGroup.getWorkFrames().iterator();
		while(gpwfItr.hasNext()) { //iterate through group workframes
			WorkFrame nextGpWf = gpwfItr.next();
			boolean duplicate = false;
			Iterator<WorkFrame> wfItr = workFrames.iterator();
			while(wfItr.hasNext()) { //check each inherited wf against
				WorkFrame nextWf = wfItr.next(); //current wf list
				if (nextWf.getName().equals(nextGpWf.getName()))
					duplicate = true;
			}
			if (!duplicate) {
				workFrames.add((WorkFrame) nextGpWf.clone());
			}
		}
	}
	
	/**
	 * inherits all tfs, won't overwrite tfs closest to 'this'
	 * @param memberGroup
	 * @throws CloneNotSupportedException 
	 */
	public void inheritThoughtframes(Group memberGroup) throws CloneNotSupportedException {
		Iterator<ThoughtFrame> gptfItr = memberGroup.getThoughtFrames().iterator();
		while(gptfItr.hasNext()) { //iterate through group workframes
			ThoughtFrame nextGpTf = gptfItr.next();
			boolean duplicate = false;
			Iterator<ThoughtFrame> tfItr = thoughtFrames.iterator();
			while(tfItr.hasNext()) { //check each inherited wf against
				ThoughtFrame nextTf = tfItr.next(); //current wf list
				if (nextTf.getName().equals(nextGpTf.getName()))
					duplicate = true;
			}
			if (!duplicate)
				thoughtFrames.add((ThoughtFrame) nextGpTf.clone());
		}
	}
	
	public String toString() {
		String retVal = "Group " + super.toString(); 
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			Group grp = ((Group) super.clone());
			
			grp.name = new String(name);
			grp.display = new String(display);
			
			Object rel = ((HashSet<Relation>) relations).clone();
			grp.relations = ((Set<Relation>) rel);
			
			Object attr = ((HashSet<Attribute>) attributes).clone();
			grp.attributes = ((Set<Attribute>) attr);
			
			Object initBeliefs = ((HashMap<String, List<Belief>>) super.getBeliefs()).clone();
			grp.beliefs = ((Map<String, List<Belief>>) initBeliefs);
						
			Object acts = ((HashSet<Activity>) activities).clone();
			grp.activities = ((Set<Activity>) acts);
			
			Object wfs = ((HashSet<WorkFrame>) workFrames).clone();
			grp.workFrames = ((Set<WorkFrame>) wfs);
			
			Object tfs = ((HashSet<ThoughtFrame>) thoughtFrames).clone();
			grp.thoughtFrames = ((Set<ThoughtFrame>) tfs);
			
			Object mof = ((HashSet<String>) memberOf).clone();
			grp.memberOf = ((Set<String>) mof);
			
			return grp;
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}
	}
}
