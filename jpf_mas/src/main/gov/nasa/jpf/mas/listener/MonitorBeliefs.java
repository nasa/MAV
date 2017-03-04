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


import java.util.Stack;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.mas.extract.ExtractAgent;
import gov.nasa.jpf.mas.extract.ExtractRelExpression;
import gov.nasa.jpf.mas.extract.ExtractTerm;
import gov.nasa.jpf.mas.extract.ExtractValueExpression;
import gov.nasa.jpf.mas.extract.ExtractValues;
import gov.nasa.jpf.mas.lts.LTSModel;
import gov.nasa.jpf.mas.lts.LTSState;
import gov.nasa.jpf.mas.lts.LTSTransition;
import gov.nasa.jpf.mas.output.PrintToDotMas;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class MonitorBeliefs extends ListenerAdapter {
	
	Stack<LTSState> currStates;
	
	boolean beforeSimulation = true;
	
	LTSModel model;
	static int counter = 0;
	
	 private final String updateBeliefsValExp = "gov.nasa.arc.brahms.model.concept.Basic"
	 		+ ".updateBeliefs(Lgov/nasa/arc/brahms/model/expression/Term;"
	 		+ "Lgov/nasa/arc/brahms/model/expression/ValueExpression;)V"; 
	 
	 private final String updateBeliefsRelExp = "gov.nasa.arc.brahms.model.concept.Basic."
	 		+ "updateBeliefs(Ljava/lang/String;"
	 		+ "Lgov/nasa/arc/brahms/model/expression/RelationalExpression;)V";
	 
	 
	 private final String updateFactsValExp = "gov.nasa.arc.brahms.simulator.world.FactSet."
	 		+ "updateFacts(Lgov/nasa/arc/brahms/model/expression/Term;"
	 		+ "Lgov/nasa/arc/brahms/model/expression/ValueExpression;)V";
	 
	 private final String updateFactsRelExp = "gov.nasa.arc.brahms.simulator.world.FactSet."
	 		+ "updateFacts(Ljava/lang/String;"
	 		+ "Lgov/nasa/arc/brahms/model/expression/RelationalExpression;)V";



	  @Override
	  public void methodEntered (VM vm, ThreadInfo currentThread,
			  MethodInfo enteredMethod) {
		  String methodName = enteredMethod.getFullName();
		  if(methodName.equals(updateBeliefsValExp)) {
			  StackFrame sf = currentThread.getModifiableTopFrame();
			 
			  int firstArg = sf.getTopPos() - 1;
			  int secondArg = sf.getTopPos() - 2;

			  generateValues(vm, sf, sf, firstArg, 
					  				secondArg, true);
			  
		  } else if (methodName.equals(updateBeliefsRelExp)) {
			  
			  StackFrame sf = currentThread.getModifiableTopFrame();
			  String agentId = generateAgentId(vm, sf);
			  int secondArg = sf.getTopPos() - 2;
			  String val = ExtractRelExpression.getValue(vm, sf, secondArg);
			  addToState(agentId, val, true);
			  
		  }  else if(methodName.contains("updateBeliefs") &&
				  	enteredMethod.getClassName().contains("Basic")) {
			  System.err.println(methodName);
			  System.exit(1);
		  } else if(methodName.equals(updateFactsValExp)) {
			  
			  StackFrame sf = currentThread.getModifiableTopFrame();
			  StackFrame prev = extractRightStackFrame(vm, sf);

			  int firstArg = sf.getTopPos();
			  int secondArg = sf.getTopPos() - 1;
			  
			  generateValues(vm, prev, sf, firstArg, secondArg, false);
		  } else if(methodName.equals(updateFactsRelExp)) {
			  
			  StackFrame sf = currentThread.getModifiableTopFrame();
			  StackFrame prev = extractRightStackFrame(vm, sf);
			  
			  String agentId = generateAgentId(vm, prev);
			  int secondArg = sf.getTopPos() - 1;
			  
			  String val = ExtractRelExpression.getValue(vm, sf, secondArg);
			  addToState(agentId, val, false);
			 
		  }
	  }
	  
	  protected StackFrame extractRightStackFrame(VM vm, StackFrame sf) {
		  StackFrame prev = sf; 
		  while(prev.getMethodInfo().getClassName().contains("FactSet")) {
			  prev = prev.getPrevious();
		  }
		  return prev;
	  }
	  
	  protected void generateValues(VM vm, StackFrame sfAgentId, 
			  StackFrame sfParams, int firstArg, int secondArg,
			  boolean isBelief) {
		  
		  String agentId = generateAgentId(vm, sfAgentId);
			  
		  String val = ExtractTerm.getTerm(vm, sfParams, firstArg);
		  ElementInfo ei2Val = ExtractValueExpression.
				  getValueExpressionRef(vm, sfParams, secondArg);
		 
		  val = ExtractValues.getValue(val, ei2Val);
		  addToState(agentId, val, isBelief);
		  
	  }
	  
	  protected String generateAgentId(VM vm, StackFrame sf) {
		  String agentId = ExtractAgent.getAgentId(vm, sf,
		  				sf.getTopPos());
		  model.addAgent(agentId);
		  return agentId;
	  }
	  
	  protected void addToState(String agentId, String val, boolean isBelief) {
		  LTSState state = currStates.peek();
		  if(!isBelief) {
			  state.setFactsForAgent(agentId, val);
		  } else {
			  state.setBeliefsForAgent(agentId, val);
		  }
		  System.out.println( agentId + "----> " + val);
	  }
	  
	  @Override
	  public void searchStarted(Search search) {
		  currStates = new Stack<LTSState>();
		  beforeSimulation = true;
		  model = new LTSModel();
		  LTSState initState = new LTSState(counter);
		  model.addState(initState);
		  currStates.push(initState);
		 
	  }
	  
	  @Override
	  public void searchFinished(Search search) {
		 PrintToDotMas.generateDotFile(model);
	  }
	  
	  @Override
	  public void stateAdvanced(Search search) {
		  counter = counter+1;
		  LTSState state = new LTSState(counter);
		  int prevStateId = currStates.peek().getID();
		  int currStateId = state.getID();
		  LTSTransition transition = new LTSTransition(prevStateId, currStateId);
		  model.addState(state);
		  model.addTransition(transition);
		  currStates.push(state);
	  }
	
	  
	  @Override
	  public void stateBacktracked(Search search) {
		  currStates.pop();
	  }
	  
	  
	
}
