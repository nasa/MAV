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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a PCTL model: it contains a list of agents (list of strings),
 * a list of of states, and a list of transitions.
 * We assume probability are 0..100 (could be easily changed using MAXPROB).
 *
 * TODO: check that probabilities add to 1
 * TODO: here assuming that states have id 0..N (and no gaps), check (or modify)
 * 
 */
public class LTSModel {
    private List<String> agents;
    private List<LTSState> states;
    private List<LTSTransition> transitions;
    private int initState;
    private int MAXPROB=100;
        
    public LTSModel() {
        agents = new ArrayList<String>();
        states = new ArrayList<LTSState>();
        transitions = new ArrayList<LTSTransition>();
    }
    
    public void setInitState(int sid) {
        initState = sid;
    }
    
    public void addAgent(String agentID) {
        if (!agents.contains(agentID)) {
            agents.add(agentID);
        }
    }
    
    public void addState(LTSState s) {
        	states.add(s); 
    }
    
    /**
     * Add a state without checking that there exists another state
     * with the same set of beliefs/facts
     * 
     * TODO: should check that the ID of the state is unique...
     * 
     */
    public int addStateNoCheck(LTSState s) {
    	states.add(s); 
        return 0;
    }
    
    public int contains(LTSState st) {
    	int ret = -1;

    	for (LTSState currSt : states) {
    		Map<String, List<String>> agBeliefs = currSt.getBeliefs();
    		Map<String, List<String>> agFacts = currSt.getFacts();
    		

    		boolean found = true;
    		int tmpId = currSt.getID();
    		
    		Iterator<String> agItr = agBeliefs.keySet().iterator();
    		while(agItr.hasNext()) {
    			String agId = agItr.next();
    			
    			List<String> beliefs = agBeliefs.get(agId);
    			List<String> otherBeliefs = st.getBeliefs().get(agId);
    			
    			for(int bIndex = 0; bIndex < beliefs.size(); bIndex++) {
    				if(!(otherBeliefs.contains(beliefs.get(bIndex)))) {
    					found = false;
    				}
    			}
    			List<String> facts = agFacts.get(agId);
    			for(int fIndex = 0; fIndex < facts.size(); fIndex++) {
    					if(!st.getFacts().get(agId).contains(facts.get(fIndex))) {
    					found = false;
    				}
    			}
    		}
    		
    		if(found) {
    			return tmpId;
    		}
    	}
    	return ret;
    }
    
    public void addTransition(LTSTransition t) {
    	boolean found = false;
    	for(int tIndex = 0; tIndex < transitions.size(); tIndex++) {
    		if(transitions.get(tIndex).getStart() == t.getStart() &&
    				transitions.get(tIndex).getEnd() == t.getEnd() &&
    				transitions.get(tIndex).getProb() == t.getProb()) {
    			found =  true;
    			break;
    		}
         }
    	if(!found) {
    		transitions.add(t);
    	}
    }
    
    public LTSState getState(int stateID) {
        for (LTSState s: states) {
            if (s.getID() == stateID ) {
                return s;
            }
        }
        return null;
    }
    
    public List<LTSState> getStates() {
        return states;    
    }
    
    public List<LTSTransition> getTransitions() {
        return transitions;
    }
    
    public List<String> getAgents() {
        return agents;
    }
    
    /*
     * Generate a PRISM model
     * TODO: a few simplifying assumptions here, to be improved.
     */
    public String generatePRISM() {
        String result = "dtmc\n\n" + "module automaticallyGenerated\n\n";
        
        result += "  state : [0.."+(states.size()-1)+"] init "+initState+";\n\n";

        for (int i=0; i<states.size(); i++) {
            result += "  [] (state="+i+") -> ";
            boolean first=true;
            for (LTSTransition t: transitions) {
                if (t.getStart()==i) {
                    if (!first) {
                        result += " + ";
                    }
                    if (t.getProb() < MAXPROB ) {
                        result += ((double)t.getProb()/MAXPROB)+": (state'="+t.getEnd()+")";
                    } else {
                        result += "(state'="+t.getEnd()+")";
                    }
                    first = false;
                }
            }
            result += ";\n";
        }
        result += "\n" + "endmodule\n\n";

        // Now generating the labels. We generate a label for each fact that
        // we have in _env (see PCTLState).
        
        HashMap<String,List<String>> facts = buildFactLabels();
        Set<String> factSet = facts.keySet();
        for (String fact: factSet) {
            List<String> stateSet = facts.get(fact);
          // We need to massage a bit the facts:
          // - we remove spaces
          // - they must start with a letter: we add l_ at the beginning
          // - we substitute . with _
          // - we substitute = with "is"
          fact = fact.replace(".", "_");
          fact = fact.replace("=","_is_");
          fact = fact.replace(" ","");
          result += "label \""+fact+"\" = ";
          
          boolean first=true;
          for (String state: stateSet ) {
            if (!first) {
              result += " | ";
            }
            result += "state="+state;
            first=false;
          }
          result += ";\n";
        }
        
        result += "\n";
        

        // getting all the labels
        HashMap<String,List<String>> labels = buildLabels();
        
        // Now we need to print the labels in a nice way
        Set<String> labelSet = labels.keySet();
        
        for (String lab: labelSet) {
          
          List<String> stateSet = labels.get(lab);
          // We need to massage a bit the labels:
          // - we remove spaces
          // - they must start with a letter: we add l_ at the beginning
          // - we substitute . with _
          // - we substitute = with "is"
          lab = lab.replace(".", "_");
          lab = lab.replace("=","_is_");
          lab = lab.replace(" ","");
          lab = "l_"+lab;
          result += "label \""+lab+"\" = ";
          
          boolean first=true;
          for (String state: stateSet ) {
            if (!first) {
              result += " | ";
            }
            result += "state="+state;
            first=false;
          }
          result += ";\n";
          // state="+s.getID()+";\n"          
        }
        
        return result;  
    }
    
  public String generatePROMELA() {
    /* This is used to generate a PROMELA model for SPIN
     * We use just one variable (state) and we proceed similarly to
     * the PRISM case above.
     * 
     */
    String result = "int state=" + initState + ";\n\n";

    // We define the various prositions by means of #define constructs.

    HashMap<String, List<String>> labels = buildLabels();

    Set<String> labelSet = labels.keySet();

    for (String lab : labelSet) {
      List<String> stateSet = labels.get(lab);
      // We need to massage a bit the labels:
      // - we remove spaces
      // - they must start with a letter: we add l_ at the beginning
      // - we substitute . with _
      // - we substitute = with "is"
      lab = lab.replace(".", "_");
      lab = lab.replace("=", "_is_");
      lab = lab.replace(" ", "");
      lab = "l_" + lab;
      result += "#define " + lab + " ( ";

      boolean first = true;
      for (String state : stateSet) {
        if (!first) {
          result += " || ";
        }
        result += "state==" + state;
        first = false;
      }
      result += ")\n";     
    }
    
    result += "\n";
    
    // We now generate the propositions corresponding to facts (again using
    // a #define statement). Remember that the assumption is that fact are 
    // stored in the special agent _env

    HashMap<String,List<String>> facts = buildFactLabels();
    Set<String> factSet = facts.keySet();

    for (String fact : factSet) {
      List<String> stateSet = facts.get(fact);
      // We need to massage a bit the facts:
      // - we remove spaces
      // - we substitute . with _
      // - we substitute = with "is"
      fact = fact.replace(".", "_");
      fact = fact.replace("=", "_is_");
      fact = fact.replace(" ", "");
      result += "#define " + fact + " ( ";

      boolean first = true;
      for (String state : stateSet) {
        if (!first) {
          result += " || ";
        }
        result += "state==" + state;
        first = false;
      }
      result += ")\n";     
    }
    
    result += "\n";
    
    
    // We generate a proctype to encode transitions
    result += "active proctype automaticallyGenerated() {\n";
    result += "  do\n";
    
    for (LTSTransition t : transitions) {
      result += "    :: (state==" + t.getStart() + ") -> "
              + "{state=" + t.getEnd() + ";}\n";
    }
    result += "  od\n";
    result += "}\n";

    return result;
  }

  /* A method to generate NuSMV code (see above the generation of Prism code
   * for comments, this method is very similar)
   */
  public String generateSMV() {
    String result = "MODULE main\n\n";

    result += "  VAR s : 0.." + (states.size() - 1) + ";\n\n";
    result += "  ASSIGN\n";
    result += "    init(s) := " + initState + ";\n";

    result += "    next(s) := case\n";
    for (int i = 0; i < states.size(); i++) {
      result += "       s=" + i + " : {";
      boolean first = true;
      for (LTSTransition t : transitions) {
        if (t.getStart() == i) {
          if (!first) {
            result += ", ";
          }
          result += t.getEnd();
          first = false;
        }
      }
      result += "};\n";
    }
    result += "    esac;\n\n";

    // Now generating the labels. We generate a label for each fact that
    // we have in _env (see PCTLState).

    HashMap<String, List<String>> facts = buildFactLabels();
    Set<String> factSet = facts.keySet();
    for (String fact : factSet) {
      List<String> stateSet = facts.get(fact);
      // We need to massage a bit the facts:
      // - we remove spaces
      // - they must start with a letter: we add l_ at the beginning
      // - we substitute . with _
      // - we substitute = with "is"
      fact = fact.replace(".", "_");
      fact = fact.replace("=", "_is_");
      fact = fact.replace(" ", "");
      result += "DEFINE " + fact + " := (";

      boolean first = true;
      for (String state : stateSet) {
        if (!first) {
          result += " | ";
        }
        result += "s=" + state;
        first = false;
      }
      result += ");\n";
    }

    result += "\n";

    // getting all the labels
    HashMap<String, List<String>> labels = buildLabels();

    // Now we need to print the labels in a nice way
    Set<String> labelSet = labels.keySet();

    for (String lab : labelSet) {

      List<String> stateSet = labels.get(lab);
      // We need to massage a bit the labels:
      // - we remove spaces
      // - they must start with a letter: we add l_ at the beginning
      // - we substitute . with _
      // - we substitute = with "is"
      lab = lab.replace(".", "_");
      lab = lab.replace("=", "_is_");
      lab = lab.replace(" ", "");
      lab = "l_" + lab;
      result += "DEFINE " + lab + " := (";

      boolean first = true;
      for (String state : stateSet) {
        if (!first) {
          result += " | ";
        }
        result += "s=" + state;
        first = false;
      }
      result += ");\n";
      // state="+s.getID()+";\n"          
    }

    return result;
  }

  private HashMap<String, List<String>> buildLabels() {
    // We now generate a label for each agent's belief.
    // We start by building a map "agentname.belief" -> {s0,s1,..}
    // i.e. a map from the belief with agent name in front to a set of
    // states where the belief is true.
    // We store each belief as as a string of the form agentname.belief
    HashMap<String, List<String>> labels = new HashMap<String, List<String>>();
    for (LTSState s : states) {
      Set<String> agNames = s.getBeliefs().keySet();
      for (String agtName : agNames) {
        if (!agtName.equals("_env")) {
          List<String> beliefs = s.getStateForAgent(agtName);
          for (String belief : beliefs) {
            String beliefString = agtName + "." + belief;
            if (labels.containsKey(beliefString)) {
              // We add stateID to the list of states where belief is true
              labels.get(beliefString).add(String.valueOf(s.getID()));
            } else {
              // We create a new entry
              ArrayList<String> tmpList = new ArrayList<String>();
              tmpList.add(String.valueOf(s.getID()));
              labels.put(beliefString, tmpList);
            }
          }
        }
      }
    } // end for over states
    return labels;
  }
  
    private HashMap<String, List<String>> buildFactLabels() {
    // We now generate a label for each fact.
    // We start by building a map "fact" -> {s0,s1,..}
    // i.e. a map from the fact to a set of
    // states where the fact is true.
    // We store each fact as as a string of the form fact.fact
    HashMap<String, List<String>> labels = new HashMap<String, List<String>>();
    for (LTSState s : states) {
      Set<String> agNames = s.getBeliefs().keySet();
      for (String agtName : agNames) {
        if (agtName.equals("_env")) {
          List<String> beliefs = s.getStateForAgent(agtName);
          for (String belief : beliefs) {
            String beliefString = "fact" + "." + belief;
            if (labels.containsKey(beliefString)) {
              // We add stateID to the list of states where belief is true
              labels.get(beliefString).add(String.valueOf(s.getID()));
            } else {
              // We create a new entry
              ArrayList<String> tmpList = new ArrayList<String>();
              tmpList.add(String.valueOf(s.getID()));
              labels.put(beliefString, tmpList);
            }
          }
        }
      }
    } // end for over states
    return labels;
  }
}
