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

/**
 *
 */
public class TestLTSModel {
    public static void main(String argv[]) {
        
        // Example from http://www.prismmodelchecker.org/tutorial/die.php
        // In this simple example we just use state id and transitions
        // (no agents). The possible states are:
        /*
         * 0  -> s=0,d=0
         * 1  -> 1,0
         * 2  -> 2,0
         * 3  -> 3,0
         * 4  -> 4,0
         * 5  -> 5,0
         * 6  -> 6,0
         * 7  -> 7,1
         * 8  -> 7,2
         * 9  -> 7,3
         * 10 -> 7,4
         * 11 -> 7,5
         * 12 -> 7,6

         * These are the transitions from PRISM:
	[] s=0 -> 0.5 : (s'=1) + 0.5 : (s'=2);
	[] s=1 -> 0.5 : (s'=3) + 0.5 : (s'=4);
	[] s=2 -> 0.5 : (s'=5) + 0.5 : (s'=6);
	[] s=3 -> 0.5 : (s'=1) + 0.5 : (s'=7) & (d'=1);
	[] s=4 -> 0.5 : (s'=7) & (d'=2) + 0.5 : (s'=7) & (d'=3);
	[] s=5 -> 0.5 : (s'=7) & (d'=4) + 0.5 : (s'=7) & (d'=5);
	[] s=6 -> 0.5 : (s'=2) + 0.5 : (s'=7) & (d'=6);
	[] s=7 -> (s'=7);
         */
        
        LTSModel diceModel = new LTSModel();
        
        for (int i=0; i<7; i++) {
            LTSState s = new LTSState(i);
            diceModel.addState(s);
        }
        
        for (int i = 7; i < 13; i++) {
            LTSState d = new LTSState(i); // a state in which die is i
            // We add the fact that die is one using the special agent _env 
            // (see PCTLState definition)
            ArrayList<String> facts = new ArrayList<String>();
            if (facts.add("die" + (i-6))) {
                d.setFactsForAgent("_env", facts);
            }
            diceModel.addState(d);
        }

        diceModel.setInitState(0);
        
        diceModel.addTransition(new LTSTransition(0,1,50));
        diceModel.addTransition(new LTSTransition(0,2,50));
        
        diceModel.addTransition(new LTSTransition(1,3,50));
        diceModel.addTransition(new LTSTransition(1,4,50));        

        diceModel.addTransition(new LTSTransition(2,5,50));
        diceModel.addTransition(new LTSTransition(2,6,50));
        
        diceModel.addTransition(new LTSTransition(3,1,50));
        diceModel.addTransition(new LTSTransition(3,7,50));
        
        diceModel.addTransition(new LTSTransition(4,8,50));
        diceModel.addTransition(new LTSTransition(4,9,50));        

        diceModel.addTransition(new LTSTransition(5,10,50));
        diceModel.addTransition(new LTSTransition(5,11,50));    
        
        diceModel.addTransition(new LTSTransition(6,2,50));
        diceModel.addTransition(new LTSTransition(6,12,50));        

        // Final states (self loops)
        diceModel.addTransition(new LTSTransition(7,7,100));
        diceModel.addTransition(new LTSTransition(8,8,100));
        diceModel.addTransition(new LTSTransition(9,9,100));
        diceModel.addTransition(new LTSTransition(10,10,100));
        diceModel.addTransition(new LTSTransition(11,11,100));
        diceModel.addTransition(new LTSTransition(12,12,100));
        
        System.out.println(diceModel.generatePRISM());
    }
}
