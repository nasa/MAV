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

package gov.nasa.jpf.mas.lts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */

/* 
 * This is a state in the model. 
 * We assume that each state has a unique integer id.
 * Each state is a map of the form:
 * <AgentID> -> [Belief1,Belief2,...,BeliefN]
 * (i.e. a map<String,List<String>)
 * We assume that there is a special agent "environment" (with id=_env) where
 * we store all "facts".
 */

public class LTSState {
    protected int id;
    private HashMap<String,List<String>> beliefs;
    private HashMap<String, List<String>> facts;
    
    public LTSState(int id) {
        this.id=id;
        beliefs = new HashMap<String,List<String>>();
        facts = new HashMap<String, List<String>>();
    }
    
    public LTSState() {
    	beliefs = new HashMap<String, List<String>>();
    	facts = new HashMap<String, List<String>>();
    }
    
    public String toString() {
    	String retVal;
    	retVal = "id: = " + id;
    	retVal = retVal + beliefs.toString();
    	return retVal;
    }
    
    public int getID() {
        return this.id;        
    }

    public void setID(int id) {
    	this.id = id;
    }
    
    public HashMap<String,List<String>> getBeliefs() {
        return this.beliefs;
    }
    
    public HashMap<String, List<String>> getFacts() {
    	return this.facts;
    }
    
    public List<String> getStateForAgent(String agentID) {
        if (this.beliefs.containsKey(agentID)) {
            return this.beliefs.get(agentID);
        } else {
            return null;
        }
    }
    
    /* 
     * Set the list of beliefs for agentID in this state
     */
    public void setBeliefsForAgent(String agentID, List<String> beliefs) {
       this.beliefs.put(agentID, beliefs);
    }
    
    public void setFactsForAgent(String agentID, List<String> facts) {
    	this.facts.put(agentID, facts);
    }

    /* 
     * Set the list of beliefs for agentID in this state
     */
    public void setBeliefsForAgent(String agentID, String beliefs) {
       List<String> beliefsOfAgent;
       if(this.beliefs.containsKey(agentID)) {
    	   beliefsOfAgent = this.beliefs.get(agentID);
       } else {
    	   beliefsOfAgent = new ArrayList<String>();
       }
       beliefsOfAgent.add(beliefs);
       this.beliefs.put(agentID, beliefsOfAgent);
    }
    
    public void setFactsForAgent(String agentId, String fact) {
    	List<String> factsOfAgent;
    	System.out.println(this.toString());
    	if(this.facts.containsKey(agentId)) {
    		factsOfAgent = this.facts.get(agentId);
    	} else {
    		factsOfAgent = new ArrayList<String>();
    	}
    	factsOfAgent.add(fact);
    	this.facts.put(agentId, factsOfAgent);
    }
    
    /*
     * Check if an agent has a belief
     * TODO: this is simply a check for membership of a string,
     * see equals() below for further comments.
     */
    public boolean agentHasBelief(String agentID, String belief) {

        // If the agent is not present, it's false.
        if (!beliefs.containsKey(agentID)) {
            return false;
        }

        if (!beliefs.get(agentID).contains(belief)) {
            return false;
        }
        
        return true;
    }
    
    public boolean agentHasFact(String agentID, String fact) {
    	if(!this.facts.containsKey(agentID)) return false;
    	
    	if(!this.facts.get(agentID).contains(fact)) return false;
    	
    	return true;
    }

    
    /*
     * We may need this one: compare two states.
     * TODO: This is probably not very efficient. We could choose an
     * implementation of List that implements equals(..) efficiently,
     * sort the list (of beliefs), and compare with equals.
     */
   /**** @Override
    public boolean equals(Object s) {

        // If they are not the same type, they are different
        if (!(s instanceof PCTLState)) {
            return false;
        } 
        PCTLState otherstate = (PCTLState) s;
        
        // If their id is different, they are different
        if (otherstate.getID() != this.id ) {
            return false;
        }

        // If they contain a different number of agents, they are different
        if (map.size() != otherstate.getMap().size() ) { 
            return false;
        }

        // Now let's iterate over all agents
        for (String agentName: map.keySet() ) {
            if ( !otherstate.getMap().containsKey(agentName)) {
                // If an agent is not present, they are different
                return false;
            }
            for (String belief: map.get(agentName)) {
                if ( otherstate.getStateForAgent(agentName).contains(belief)) {
                    // If a state does not contain the same beliefs, it is
                    // different.
                    // TODO: this is a *syntactic-only* check! A comparison
                    // between x>0 and !(x<=0) would return false!
                    return false;
                }
            }
        }        
        // All checks passed: they are probably the same state :-).
        return true;
    } ***/
}
