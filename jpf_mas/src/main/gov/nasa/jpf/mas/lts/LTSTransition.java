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
import java.util.List;

/**
 * This encodes a transition: it requires the id of the start state, id of
 * end state, and probability. We assume that probability is an integer 0..100
 * (in percentage). We also include an optinal label (in the form of a list of 
 * actions, one for each agent.)
 * 
 */
public class LTSTransition {
    private int startID, endID, prob;
    private List<String> label;
    
    public LTSTransition(int s, int e, int p) {
        this.startID = s;
        this.endID = e;
        this.prob = p;
        this.label = new ArrayList<String>();
    }
    
    // A constructor where we pass everything: it calls the other constructor
    // and then sets the label
    public LTSTransition(int s, int e, int p, List<String> label) {
    	this(s,e,p);
    	this.setLabel(label);
    	
    }
    
 // A constructor where we pass everything: it calls the other constructor
    // and then sets the label
    public LTSTransition(int s, int e) {
    	this(s,e,100);
    }
    
    public int getStart() {
        return startID;
    }
    
    public int getEnd() {
        return endID;                
    }
    
    public int getProb() {
        return prob;
    }
    
    public List<String> getLabel() {
    	return label;
    }
    
    public void setLabel(List<String> label) {
    	this.label = label;
    }
    
}
