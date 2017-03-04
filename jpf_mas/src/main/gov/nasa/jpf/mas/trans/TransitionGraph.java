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

package gov.nasa.jpf.mas.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransitionGraph {
	
	Set<BrahmsState> states;
	static int counter = 0;
	Map<Integer, List<Integer>> transitions;
	
	public TransitionGraph() {
		states = new HashSet<BrahmsState>();
		transitions = new HashMap<Integer, List<Integer>>();
	}
	
	public boolean ifExists(BrahmsState st) {
		Iterator<BrahmsState> stItr = states.iterator();
		while(stItr.hasNext()) {
			BrahmsState inState = stItr.next();
			Map<String, Integer> beliefs = inState.intBeliefs;
			Iterator<String> bItr = beliefs.keySet().iterator();
			boolean found = true;
			while(bItr.hasNext()) {
				String bStr = bItr.next();
				assert(beliefs.containsKey(bStr));
				if(st.intBeliefs.get(bStr).intValue() != beliefs.get(bStr).intValue())
					found = false;
			}
			if(found) return true;
		}
		
		return false;
	}
	
	public void addTransition(int parent, int child) {
		List<Integer> children;
		if(transitions.containsKey(parent)) {
			children = transitions.get(parent);
		} else {
			children = new ArrayList<Integer>();
		}
		children.add(child);
		transitions.put(parent, children);
	}
	
	public boolean addState(BrahmsState st) {
		st.setId(counter);
		counter++;
		return (states.add(st));
	}
	
	public Map<Integer, List<Integer>> getTransitions() {
		return transitions;
	}
	
}
