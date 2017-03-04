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

package gov.nasa.jpf.mas.output;

import gov.nasa.jpf.mas.lts.LTSModel;
import gov.nasa.jpf.mas.lts.LTSState;
import gov.nasa.jpf.mas.lts.LTSTransition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PrintToDotMas {

	public static void generateDotFile(LTSModel model) {

		Writer output = null;
		File file = new File("test.dot");
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			System.err.println("error while creating the file to write");
			e.printStackTrace();
		}
		try {
			output.write("digraph \"\" { \n");
			List<LTSState> states = model.getStates();
			for(int stateIndex = 0; stateIndex < states.size(); stateIndex++) {
				LTSState state = states.get(stateIndex);
				HashMap<String, List<String>> agBeliefs = state.getBeliefs();
				Iterator<String> agItr = agBeliefs.keySet().iterator();
				output.write(Integer.toString(state.getID()) + "[ label=\""); 
				output.write(Integer.toString(state.getID()) + "\\n");
				while(agItr.hasNext()) {
					String key = agItr.next();
					List<String> beliefs = agBeliefs.get(key);
					for(int bIndex = 0; bIndex < beliefs.size(); bIndex++) {
						String b = beliefs.get(bIndex);
						b = b.replace(".", "_");
						//System.out.println(b);
						output.write("b_"+b+"\\n");
					}
					if(state.getFacts().containsKey(key)) {
						List<String> facts = state.getFacts().get(key);
						for(int fIndex = 0; fIndex < facts.size(); fIndex++) {
							String f = facts.get(fIndex);
							f = f.replace(".", "_");
							output.write("f_"+f+"\\n");
						}
					}
				}

				output.write("\"];\n");
			}	
			List<LTSTransition> pctlTransitions = model.getTransitions();
			for(int transitionIndex = 0; transitionIndex < pctlTransitions.size(); transitionIndex++) {
				LTSTransition t = pctlTransitions.get(transitionIndex);
				output.write(Integer.toString(t.getStart())+ " -> " + 
						Integer.toString(t.getEnd())+ "[label=\"" + 
						Integer.toString(t.getProb()) + "\\n" +
						t.getLabel() + "\"];\n");
			}
			output.write("}");
		} catch (IOException e) {
			System.err.println("Error while writing to the XML file");
			e.printStackTrace();
		}

		try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
