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

package gov.nasa.jpf.mas.listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.NEW;
import gov.nasa.jpf.mas.cg.ConcludeChoiceGenerator;
import gov.nasa.jpf.mas.cg.PriorityChoiceGenerator;
import gov.nasa.jpf.mas.lts.LTSModel;
import gov.nasa.jpf.mas.lts.LTSState;
import gov.nasa.jpf.mas.lts.LTSTransition;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.search.Search;


public class StateGen extends ListenerAdapter {
	
	Map<Integer, Set<Integer>> agentToBeliefs; 
	Map<Integer, Set<Integer>> agentToFacts;
	Map<Integer, Integer> beliefToExpression;
	boolean beforeSimulation = true;
	int currId = -1;
	
	Stack<Integer> completeStack;
	LTSModel completeModel;
	static int completeCounter = -1;
	
	final static String ValExpClass = "Lgov/nasa/arc/brahms/model/expression/ValueExpression;";
	final static String IntValClass = "Lgov/nasa/arc/brahms/model/expression/IntegerValue;";
	final static String BoolValClass = "Lgov/nasa/arc/brahms/model/expression/BooleanValue;";
	final static String StringValClass = "Lgov/nasa/arc/brahms/model/expression/StringValue;";
	
  private boolean generatePrism = false;
  private boolean generatePromela = false;
  private boolean generateSMV = false;
  
  
	public StateGen(Config conf, JPF jpf) {
		jpf.addPublisherExtension(ConsolePublisher.class, this);
		completeStack = new Stack<Integer>();
		completeModel = new LTSModel();

    String prism = jpf.getVM().getConfig().getProperty("generatePrism");
    if ( prism != null ) {
      if ( prism.equals("true")) {
        generatePrism = true;
      }
    }

    String promela = jpf.getVM().getConfig().getProperty("generatePromela");
    if ( promela != null ) {
      if ( promela.equals("true")) {
        generatePromela = true;
      }
    }
    
    String smv = jpf.getVM().getConfig().getProperty("generateSMV");
    if ( smv != null ) {
      if ( smv.equals("true")) {
        generateSMV = true;
      }
    }
    
    
  }
	
	private void generateDotFile(LTSModel model) {
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
					List<String> facts = state.getFacts().get(key);
					for(int fIndex = 0; fIndex < facts.size(); fIndex++) {
						String f = facts.get(fIndex);
						f = f.replace(".", "_");
						output.write("f_"+f+"\\n");
					}
				}
			
				output.write("\"];\n");
			}	
			List<LTSTransition> pctlTransitions = model.getTransitions();
			for(int transitionIndex = 0; transitionIndex < pctlTransitions.size(); transitionIndex++) {
				LTSTransition t = pctlTransitions.get(transitionIndex);
				output.write(Integer.toString(t.getStart())+ " -> " + 
								Integer.toString(t.getEnd())+ "[label=\"" + 
						Integer.toString(t.getProb()) + "\"];\n");
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
	
	@Override
	public void searchStarted(Search search) {
		agentToBeliefs = new HashMap<Integer, Set<Integer>>();
		agentToFacts = new HashMap<Integer, Set<Integer>>();
		beliefToExpression = new HashMap<Integer, Integer>();
		//stack = new Stack<Integer>();
	}
	
	@Override
	public void searchFinished(Search search) {
    generateDotFile(completeModel);
    
    if (generatePrism) {
      writeToFile("prism.pm", completeModel.generatePRISM());
    }
    if (generatePromela) {
      writeToFile("promela.pml", completeModel.generatePROMELA());
    }
    if (generateSMV) {
      writeToFile("smv.smv", completeModel.generateSMV());
    }
    
    System.out.println("IM States:" + completeModel.getStates().size());
    System.out.println("IM Transition: " + completeModel.getTransitions().size());
  }
	
	@Override
	public void stateBacktracked(Search search) {
		if(beforeSimulation) return;
		completeStack.pop();
	}
	
	
	@Override
	public void stateAdvanced(Search search) {
		if(beforeSimulation) return;
		int cStateId = genNewState(search.getVM().getSystemState());
		ChoiceGenerator<?> cg = search.getVM().getChoiceGenerator();
		
		if(cg instanceof ConcludeChoiceGenerator) { 
			genTransitionForCCG(cg, cStateId);
		} else if(cg instanceof PriorityChoiceGenerator) {
			if(completeStack.peek() != cStateId) 
				genTransitionForPCG(cg, cStateId);
		} else {
			generateTransition(completeStack.peek(), cStateId, 100, completeModel);
		}
	
		completeStack.push(cStateId);
		
	}
	
	private void genTransitionForCCG(ChoiceGenerator<?> cg, int id ) {
		int prob = getCCGProbability(cg);
		generateTransition(completeStack.peek(), id, prob, completeModel);
		if(prob < 100) {
			generateTransition(completeStack.peek(), completeStack.peek(),
							100-prob, completeModel);
		}
	}
	
	private void genTransitionForPCG(ChoiceGenerator<?> cg, int id) {
		int prob = gePCGProbability(cg);
		generateTransition(completeStack.peek(), id, prob, completeModel);
	}
	
	private void generateTransition(int parent, int child, int prob, LTSModel model) {
		LTSTransition transition = new LTSTransition(parent, child, prob);
		model.addTransition(transition);
	}
	
	private int getCCGProbability(ChoiceGenerator<?> cg) {
		if(cg instanceof ConcludeChoiceGenerator) {
			ConcludeChoiceGenerator ccg = ((ConcludeChoiceGenerator) cg);
			if(ccg.getTotalNumberOfChoices() > 1 && ccg.getProcessedNumberOfChoices() == 1){
				return 100 - ccg.getProbability();
			} else {
				return ccg.getProbability();
			}
		} 
		throw new RuntimeException("should never get here");
	}

	
	private int gePCGProbability(ChoiceGenerator<?> cg) {
		if(cg instanceof PriorityChoiceGenerator) {
			PriorityChoiceGenerator pcg = ((PriorityChoiceGenerator) cg);
			return pcg.getProbability();
		} 
		throw new RuntimeException("should never get here");
	}

	
	  public void instructionExecuted(VM vm, ThreadInfo currentThread,
			  Instruction nextInstruction, Instruction executedInstruction) {
		Instruction insn = executedInstruction;
		if(insn instanceof NEW) {
			NEW newInsn = ((NEW) insn);
			int objRef = newInsn.getNewObjectRef();
			//TODO: add support for _Object
			if(objRef != -1 && newInsn.getClassName().endsWith("_Agent")) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + objRef + newInsn.toString());
				Set<Integer> agFacts = new HashSet<Integer>();
				//should never come here after the simulation has started
				assert (beforeSimulation);
				//this.agentToBeliefs.put(objRef, agBeliefs);
				this.agentToFacts.put(objRef, agFacts);
			}
			if(newInsn.getClassName().contains("Belief") && currId != -1) {
				if(this.beforeSimulation) { 			
					System.out.println("currId :" + currId);
					if(agentToBeliefs.containsKey(currId)) {
						agentToBeliefs.get(currId).add(newInsn.getNewObjectRef());
					} else {
						throw new RuntimeException("not found belief");
					}
				}
			} else if (newInsn.getClassName().contains("Fact") && currId != -1) {
				if(this.beforeSimulation) {
					if(agentToFacts.containsKey(currId)) {
						agentToFacts.get(currId).add(newInsn.getNewObjectRef());			
					} else {
						throw new RuntimeException("not found fact");
					}
				}
			}
		}
		if(insn instanceof JVMInvokeInstruction) {
			if(!beforeSimulation) return;
			JVMInvokeInstruction ivk = ((JVMInvokeInstruction) insn);
			if(ivk.getInvokedMethodName().contains("addBeliefs")) {
				System.out.println(ivk.getInvokedMethodName() + ivk.getLastObjRef());
			}
			if(ivk.getInvokedMethod().getName().contains("addBeliefs")) {
				currId = ivk.getLastObjRef();		
				Set<Integer> agBeliefs = new HashSet<Integer>();
				this.agentToBeliefs.put(currId, agBeliefs);
			}
			
			if(ivk.getInvokedMethod().getName().contains("startSim")) {
				beforeSimulation = false;
				int cStateId = genNewState(vm.getSystemState());
				completeModel.setInitState(cStateId);
				completeStack.push(cStateId);
			}
		}
	}    
	
	private int genNewState(SystemState ss) {
		LTSState pcState = new LTSState();
		pcState = extractState(ss, pcState);
		completeModel.addState(pcState);
		/*if(exists < 0) {
			completeCounter = completeCounter + 1;
			pcState.setID(completeCounter);
			return completeCounter;
		} else {
			return exists;
		}*/
		return pcState.getID();
		
	}
	
	private LTSState extractState(SystemState ss, LTSState pcState) {
		generateValues(this.agentToBeliefs, ss, true, pcState);
		generateValues(this.agentToFacts, ss, false, pcState);
		return pcState;
	}
	
	private void generateValues(Map<Integer, Set<Integer>> agentToBOrF, 
			SystemState ss, boolean setBeliefs, LTSState pcState) {
		
		Iterator<Integer> agItr = agentToBOrF.keySet().iterator();	
		while(agItr.hasNext()) {
			Integer agId = agItr.next();
			Set<Integer> bOrFId = agentToBOrF.get(agId);
			Iterator<Integer> borFItr = bOrFId.iterator();
			List<String> allTheBsOrFs = new ArrayList<String>();
			while(borFItr.hasNext()) {
				int objRef = borFItr.next();
				generateBOrFValues(ss, objRef, allTheBsOrFs);
				
			} 
			if(setBeliefs)
				pcState.setBeliefsForAgent(Integer.toString(agId), allTheBsOrFs);
			else 
				pcState.setFactsForAgent(Integer.toString(agId), allTheBsOrFs);
		}
		
	}
	
	private void generateBOrFValues(SystemState ss, int objRef, List<String> allTheBsOrFs) {
		ElementInfo bEI = ss.ks.heap.get(objRef);
		assert(bEI != null);
		int expRef = bEI.getReferenceField("exp");
		ElementInfo exEI = ss.ks.heap.get(expRef);
		if(exEI.getType().equals(ValExpClass)) {
			String belief = exEI.getStringField("objRefName") +
						   "."+exEI.getStringField("attrName");
			int valRef = exEI.getReferenceField("val");
			ElementInfo vaEI = ss.ks.heap.get(valRef);
			String value;
			if(vaEI.getType().equals(IntValClass)) {
				value = Integer.toString(vaEI.getIntField("val"));
			} else if(vaEI.getType().equals(BoolValClass)) {
				value = Boolean.toString(vaEI.getBooleanField("val"));
			} else if(vaEI.getType().equals(StringValClass)) {
				value = vaEI.getStringField("val");
			} else {
				throw new RuntimeException("need to handle the Value :" + vaEI.getType());
			}
			allTheBsOrFs.add(belief+"="+value);
		} else {
			throw new RuntimeException("need to hand expression : " + exEI.getType());
		}
	}
  
  private void writeToFile(String fname, String content) {
      Writer output = null;
      File file = new File(fname);
      try {
        output = new BufferedWriter(new FileWriter(file));
      } catch (IOException e) {
        System.err.println("error while creating the file "+fname);
        e.printStackTrace();
      }
      try {
        output.write(content);
      } catch (IOException e) {
        System.err.println("Error while writing to the Prism file");
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
