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
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
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
import gov.nasa.jpf.mas.output.PrintToDotMas;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.search.Search;

/**
 * A Listener to generate the tree of states explored by JPF during an
 * execution of the Brahms simulator. Notice that some states can be
 * created by the associate listener GenerateChoices.
 * States are stored in a PCTLModel, nothing fancy here. Transitions are
 * labelled with probabilities
 * 
 * TODO: label with workframes as well (we need to change PCTLState adding a 
 * field to store "actions"
 * 
 * 
 * @see GenerateChoices
 */
/**
 *
 */
/**
 *
 */
public class FullTreeGen extends ListenerAdapter {

	// This map associates an agent ID with the list of objRefs for its beliefs
	Map<Integer, Set<Integer>> agentToBeliefs;
	// As above, but for facts
	Map<Integer, Set<Integer>> agentToFacts;
	// This is a list of agent IDs, to keep track of their positions (used
	// when building labels for transitions: we need to know the position of
	// the agent.
	List<Integer> agentList;
	// FIXME: doesn't seem to be used.
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

	public FullTreeGen(Config conf, JPF jpf) {
		jpf.addPublisherExtension(ConsolePublisher.class, this);
		completeStack = new Stack<Integer>();
		completeModel = new LTSModel();

		String prism = jpf.getVM().getConfig().getProperty("generatePrism");
		if (prism != null) {
			if (prism.equals("true")) {
				generatePrism = true;
			}
		}

		String promela = jpf.getVM().getConfig().getProperty("generatePromela");
		if (promela != null) {
			if (promela.equals("true")) {
				generatePromela = true;
			}
		}

		String smv = jpf.getVM().getConfig().getProperty("generateSMV");
		if (smv != null) {
			if (smv.equals("true")) {
				generateSMV = true;
			}
		}

	}
	
	
	
	@Override
	public void searchStarted(Search search) {
		agentToBeliefs = new HashMap<Integer, Set<Integer>>();
		agentToFacts = new HashMap<Integer, Set<Integer>>();
		beliefToExpression = new HashMap<Integer, Integer>();
		//stack = new Stack<Integer>();
		agentList = new ArrayList<Integer>();
	}
	
	@Override
	public void searchFinished(Search search) {
		PrintToDotMas.generateDotFile(completeModel);
    
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

    System.out.println("FRANCO: agentToBeliefs: "+agentToBeliefs);
	System.out.println("FRANCO: agentToFacts: "+agentToFacts);
	System.out.println("FRANCO: agentList: "+agentList);
  }
	
	
	@Override
	public void stateAdvanced(Search search) {
		if(beforeSimulation) return;
	
		// Franco: this is the key difference: the id of the state is
		// the id from JPF.
		int cStateId = search.getVM().getSystemState().getId();
		
		// TODO: maybe add a bit of error checking for the next 4 lines...
		LTSState pcState = new LTSState();
		pcState = extractState(search.getVM().getSystemState(), pcState);
		completeModel.addStateNoCheck(pcState);		
		pcState.setID(cStateId);
				
		ChoiceGenerator<?> cg = search.getVM().getChoiceGenerator();
		if(cg instanceof ConcludeChoiceGenerator) { 
			System.out.println("FRANCO: I found a getCertainty");
			genTransitionForCCG(cg, cStateId);
		} else if(cg instanceof PriorityChoiceGenerator) {
			if (cg.hasMoreChoices()) {
				System.out.println("FRANCO: more choices");
			} else {
				System.out.println("FRANCO: this was the last choice");
			}
			System.out.println("FRANCO: I found a getHighestPriorityFrameIndex");
			if(completeStack.peek() != cStateId) 
				genTransitionForPCG(cg, cStateId);
					
		} else {
			generateTransition(completeStack.peek(), cStateId, 100, new ArrayList<String>(), completeModel);
		}
	
		completeStack.push(cStateId);
		if (search.isEndState()) {
			generateTransition(cStateId, cStateId, 100, new ArrayList<String>(), this.completeModel);				
		}
		
	}
	
	/**
	 * When we backtrack, we remove the state from the stack.
	 */
	@Override
	public void stateBacktracked(Search search) {
		if(beforeSimulation) return;
		completeStack.pop();
	}
	
	private void genTransitionForCCG(ChoiceGenerator<?> cg, int id ) {
		int prob = getCCGProbability(cg);
		
		/* Franco: this is some magic from Neha	
		 * The idea is: method getCertainty is called by
		 * method another getCertainty, which is called by concludeStatement, and the first 
		 * argument of concludeStatement is
		 * the (id of the) agent. So we must get this id
		*/
		
		System.out.println("FRANCO: cg is "+cg.toString());
		System.out.println("FRANCO ti: "+cg.getThreadInfo());
		System.out.println("FRANCO cSf: "+cg.getThreadInfo().getCallerStackFrame());
		List<String> label1 = new ArrayList<String>();
		List<String> label2 = new ArrayList<String>();
		if ( cg.getThreadInfo().getCallerStackFrame() != null ) {
			StackFrame topSF = cg.getThreadInfo().getCallerStackFrame().getPrevious();
			System.out.println("FRANCO: the topframe is: "+topSF.toString());
			MethodInfo enteredMethod = topSF.getMethodInfo();
			int isStatic = enteredMethod.isStatic() ? 0 : 1;
			// Yes we know this is a static method, but it looks fancier this way
			int topPos = topSF.getTopPos() - isStatic;
			int agtRef = topSF.peek(topPos);
			// End Neha's magic

			// Now we need to get the index of the agent with agtRef:
			int agtPosition = agentList.indexOf(agtRef);
		
			// Now we build a List of Strings that are all empty, with the 
			// exception of the one at position agtPosition that contains the index
			// of the wf.
			// We build 2 labels, one for the update and one for the non-update:
			label1 = new ArrayList<String>();
			label2 = new ArrayList<String>();
			for (int i=0; i<agtPosition; i++) {
				label1.add(i,null);
				label2.add(i,null);
			}
			label1.add(agtPosition,"Update");
			label2.add(agtPosition,"NoUpdate");
			for (int i = agtPosition+1; i<agentList.size(); i++) {
				label1.add(i,null);
				label2.add(i,null);
			}
		} else {
			System.out.println("FRANCO: I found an empty caller stack frame");
		}
		
		List<String> label = new ArrayList<String> ();
		
		if (cg.getProcessedNumberOfChoices() == 1) {
			label = label2;
		} else {
			label = label1;
		}
		
		generateTransition(completeStack.peek(), id, prob, label, completeModel);
		System.out.println("FRANCO: Added transition with update");

	}
	
	private void genTransitionForPCG(ChoiceGenerator<?> cg, int id) {
		int prob = gePCGProbability(cg);
		List<String> label = new ArrayList<String>();
		
		/* Franco: this is some magic from Neha	
		 * The idea is: method getHighestPriorityFrameIndex is called by
		 * method Wf_getPrior, and the first argument of Wf_getPrior is
		 * the (id of the) agent. So we must get this id
		*/
		if ( cg.getThreadInfo().getCallerStackFrame() != null ) {
			StackFrame topSF = cg.getThreadInfo().getCallerStackFrame().getPrevious();
			System.out.println("FRANCO: the top frame is: "+topSF.toString());
			MethodInfo enteredMethod = topSF.getMethodInfo();
			int isStatic = enteredMethod.isStatic() ? 0 : 1;
			// Yes we know this is a static method, but it looks fancier this way
			int topPos = topSF.getTopPos() - isStatic;
			int agtRef = topSF.peek(topPos);
			// End Neha's magic

			System.out.println("FRANCO: the ref of the agent is: "+agtRef);

			// Now we need to get the index of the agent with agtRef:
			int agtPosition = agentList.indexOf(agtRef);

			System.out.println("FRANCO: the list of agents is: "+agentList);
			System.out.println("FRANCO: the position of the agent is: "+agtPosition);		

			// Now we build a List of Strings that are all empty, with the 
			// exception of the one at position agtPosition that contains the index
			// of the wf:
			label = new ArrayList<String>();
			for (int i=0; i<agtPosition; i++) {
				label.add(i,null);
			}
			System.out.println("FRANCO: I'm trying to add at position: "+agtPosition + " the value "+cg.getProcessedNumberOfChoices());
			label.add(agtPosition,String.valueOf(cg.getProcessedNumberOfChoices()));
			System.out.println("FRANCO: Done");
			for (int i = agtPosition+1; i<agentList.size(); i++) {
				label.add(i,null);
			}
		}
				
		generateTransition(completeStack.peek(), id, prob, label, completeModel);
	}
	
	private void generateTransition(int parent, int child, int prob, 
			List<String> label, LTSModel model) {
		LTSTransition transition = new LTSTransition(parent, child, prob, label);
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
				// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + objRef + newInsn.toString());
				Set<Integer> agBeliefs = new HashSet<Integer>();
				Set<Integer> agFacts = new HashSet<Integer>();
				//should never come here after the simulation has started
				assert (beforeSimulation);
				this.agentToBeliefs.put(objRef, agBeliefs);
				this.agentToFacts.put(objRef, agFacts);
				if (!agentList.contains(objRef)) {
					agentList.add(objRef);
				}
			}
			if(currId != -1 && newInsn.getClassName().contains("Belief")) {
				if(this.beforeSimulation) { 					
					if(agentToBeliefs.containsKey(currId)) {
						agentToBeliefs.get(currId).add(newInsn.getNewObjectRef());
					}else 
						throw new RuntimeException("no belief");
				}
			} else if (currId != -1 && newInsn.getClassName().contains("Fact")) {
				if(this.beforeSimulation) {
					if(agentToFacts.containsKey(currId)) {
						agentToFacts.get(currId).add(newInsn.getNewObjectRef());			
					} else 
						throw new RuntimeException("no fact");
				}
			}
		}
		if(insn instanceof JVMInvokeInstruction) {
			if(!beforeSimulation) return;
			JVMInvokeInstruction ivk = ((JVMInvokeInstruction) insn);
			if(ivk.getInvokedMethod().getName().contains("addBeliefs")) {
				currId = ivk.getLastObjRef();		
			}
			
			if(ivk.getInvokedMethod().getName().contains("startSim")) {
				beforeSimulation = false;
				System.out.println(agentToBeliefs.toString());
				System.out.println(agentToFacts.toString());
				System.out.println("right when the sim is starting");
				int cStateId = vm.getSystemState().getId();
				LTSState pcState = new LTSState();
				pcState = extractState(vm.getSystemState(), pcState);
				pcState.setID(cStateId);
				completeModel.setInitState(cStateId);
				completeStack.push(cStateId);
			}
		}
	}    
	
	
	/**
	 * This method generate a PCTLState from a system state by extracting
	 * the values of the beliefs (the objRefs of the beliefs/facts for the
	 * various agents are stored in the two maps agentToBeliefs and 
	 * agentToFacts).
	 * 
	 * This method has been copied from StateGen.
	 * 
	 * TODO: question for Neha: why do we pass pcState, if we return it, and
	 * we create a new one when we call it? is the pcState parameter redundant?
	 * 
	 * @author neha
	 * 
	 */
	private LTSState extractState(SystemState ss, LTSState pcState) {
		System.out.println(agentToBeliefs.toString() + " beliefs");
		System.out.println(agentToFacts.toString() + " facts");
		
		
		
		generateValues(this.agentToBeliefs, ss, true, pcState);
		generateValues(this.agentToFacts, ss, false, pcState);
		return pcState;
	}
	
	private void generateValues(Map<Integer, Set<Integer>> agentToBOrF, 
			SystemState ss, boolean setBeliefs, LTSState pcState) {
		
		Iterator<Integer> agItr = agentToBOrF.keySet().iterator();	
		while(agItr.hasNext()) {
			Integer agId = agItr.next();
			System.out.println("the agent id :" + agId);
			ElementInfo agInfo = ss.ks.heap.get(agId);
			assert(agInfo != null);
			int expreF = agInfo.getReferenceField("beliefs");
			if(expreF == -1) throw new RuntimeException("its -1");
			System.out.println(expreF);
			ElementInfo expRef = ss.ks.getHeap().get(expreF);
			Fields f = expRef.getFields();
			System.out.println(f.toString());
			
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
		System.out.println("id : " + ss.getId());
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
		System.out.println(belief.toString()+ "=" +value.toString());
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
