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

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.choice.IntChoiceFromSet;
import gov.nasa.jpf.jvm.bytecode.IRETURN;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.mas.cg.ConcludeChoiceGenerator;
import gov.nasa.jpf.mas.cg.PriorityChoiceGenerator;

public class GenerateChoices extends ListenerAdapter {
	
	static String CG_ID0 = "Certainty";
	static String CG_ID1 = "Duration";
	static String CG_ID2 = "WorkFramePriority";
	private static boolean debug = true;

	static String UtilsClass = "gov.nasa.arc.brahms.simulator.Utils";
	
	@Override
	public void instructionExecuted(VM vm, ThreadInfo currentThread,
			Instruction nextInstruction, Instruction executedInstruction) {
		ThreadInfo ti = currentThread;
		Instruction insn = executedInstruction;
		MethodInfo mi = currentThread.getTopFrameMethodInfo();
		if(mi == null) return;
		if(!mi.getClassName().equals(UtilsClass)) return;
		if (mi.getName().equals("getCertainty")) {
			certainties(vm, ti, mi, insn);
		} else if (mi.getName().equals("getActualDuration")) {
			generateChoicesForDuration(vm, ti, mi);
		}   
		else if (mi.getName().equals("getHighestPriorityFrameIndex")) {
			priorities(vm, ti, mi, insn); 
		} 
	}    
	
	protected void priorities(VM vm, ThreadInfo ti, MethodInfo mi, Instruction insn) {
		if(!ti.isFirstStepInsn()) {
			if(insn instanceof JVMInvokeInstruction) {
				int size = ti.getTopFrame().getPrevious().peek();
				int[] choices = new int[size];
				for(int choiceId = 0; choiceId < size; choiceId++) {
					choices[choiceId] = choiceId;
				}
				PriorityChoiceGenerator cg = new PriorityChoiceGenerator(CG_ID1, choices);
				if(vm.setNextChoiceGenerator(cg)) return;
			}
		} else {
			PriorityChoiceGenerator cg = vm.getCurrentChoiceGenerator(CG_ID1, PriorityChoiceGenerator.class);
			if (cg != null) {
				int choice = cg.getNextChoice();
				Instruction lastInsn = mi.getLastInsn();
				assert lastInsn instanceof IRETURN : "last instruction not an IRETURN ";
				StackFrame frame = ti.getModifiableTopFrame();
				frame.clearOperandStack();
				frame.push(choice);
				ti.setNextPC(lastInsn);
			} else {
				System.out.println("unexpected CG: " + cg);
			}
		}
	}
	
	protected void certainties(VM vm, ThreadInfo ti, MethodInfo mi, Instruction insn) {
		if (!ti.isFirstStepInsn()) {
			//System.out.println("in top half: " + insn);
			if (insn instanceof JVMInvokeInstruction) { // we just entered the interceptedMethod
				int prob = ti.getTopFrame().getPrevious().peek();
				ConcludeChoiceGenerator cg;
				if(prob == 100)
					cg = new ConcludeChoiceGenerator(CG_ID0, prob, 1);
				else
					cg = new ConcludeChoiceGenerator(CG_ID0, prob, 0, 1);
				if (vm.setNextChoiceGenerator(cg)) {
					return; // we are done here, next insn in this method will skip to return
				}
			}
		} else {
			// note - this is the first insn WITHIN the interceptedMethod
			//System.out.println("in bottom half: " + insn);
			ConcludeChoiceGenerator cg = vm.getCurrentChoiceGenerator(CG_ID0, ConcludeChoiceGenerator.class);
			if (cg != null) {
				int choice = cg.getNextChoice();
				Instruction lastInsn = mi.getLastInsn();
				assert lastInsn instanceof IRETURN : "last instruction not an IRETURN ";
				StackFrame frame = ti.getModifiableTopFrame(); // we are modifying it
				frame.clearOperandStack();
				frame.push(choice);
				ti.setNextPC(lastInsn);
			} else {
				System.out.println("unexpected CG: " + cg);
			}
		}
	}
	

	/*protected void generateChoicesForWorkFrames(VM vm, ThreadInfo ti, MethodInfo mi) {
		if(!ti.isFirstStepInsn()) {
			// generate choices
			StackFrame sf = ti.getTopFrame();
			int size = sf.peek(0);
			int elem[] = new int[size];
			for(int i = 0; i < size; i++) elem[i] = i;
			IntChoiceFromSet cg = new IntChoiceFromSet(CG_ID2, elem);
			if(vm.setNextChoiceGenerator(cg)) {
				ti.skipInstruction(vm.getLastInstruction());
				return;
			}
		} else{
			 IntChoiceFromSet cg = vm.getCurrentChoiceGenerator(CG_ID2, IntChoiceFromSet.class);
		      if (cg != null){
		          int choice = cg.getNextChoice();
		          StackFrame sf = ti.popFrame();
		          ti.removeArguments(mi);
		          ti.push(choice);
		          Instruction nextIns = sf.getPC().getNext();
		  		  vm.getCurrentThread().skipInstruction(nextIns);
		      }
		}
	}*/
	
	protected void generateChoicesForDuration(VM vm, ThreadInfo ti, MethodInfo mi) {
		if(!ti.isFirstStepInsn()) {
			// generate choices
			StackFrame sf = ti.getTopFrame();

			int min = sf.getLocalVariable("min_duration");
			int max = sf.getLocalVariable("max_duration");

			IntChoiceFromSet cg = new IntChoiceFromSet(CG_ID1, min, max);
			if(vm.setNextChoiceGenerator(cg)) {
				ti.skipInstruction(vm.getInstruction());
			}
		} else{
			IntChoiceFromSet cg = vm.getCurrentChoiceGenerator(CG_ID1, IntChoiceFromSet.class);
			if (cg != null){
				int choice = cg.getNextChoice();
				StackFrame sf = ti.popAndGetModifiableTopFrame();
				ti.getTopFrame().removeArguments(mi);
				ti.getTopFrame().push(choice);
				Instruction nextIns = sf.getPC().getNext();
				vm.getCurrentThread().skipInstruction(nextIns);
			}
		}
	}

}
