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

package gov.nasa.arc.brahms.model;


import gov.nasa.arc.brahms.model.comparison.EvalValCompExpOpExp;
import gov.nasa.arc.brahms.model.comparison.RelComp;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.simulator.scheduler.EventSequence;

/**
 * 
 * @author josie
 *
 *consequence	::=	conclude ( ( resultcomparison ) 
 *{ , fact-certainty } 
 *{ , belief-certainty } ) ;
 *
 *
 *resultcomparison	::=	[ result-val-comp | PRE.rel-comp ]
 *result-val-comp	::=	 BEL.obj-attr BEL.equality-operator PRE.expression |
 *BEL.obj-attr BEL.equality-operator ID.literal-symbol |
 *BEL.obj-attr BEL.equality-operator ID.literal-string |
 *BEL.obj-attr BEL.equality-operator BEL.sgl-object-ref |
 *BEL.tuple-object-ref BEL.equality-operator BEL.sgl-object-ref
 *fact-certainty	::=	fc : ID.unsigned
 *belief-certainty	::=	bc : ID.unsigned
 *
 *
 *Please note: in workframes only, fact certainties default to 100, not 0.  So if there is no
 *fc given in the Brahms model, set it to 100, or use the constructor
 *with only the bc as a parameter
 *thoughtframes have no fc, and so those should be set to 0
 */
public class Conclude extends Event
				      implements Cloneable{
	
	Expression resultComparison; //evalValCompExpOpExp or relcomp
	int bc = 100; //belief certainty 
	int fc = 100; // fact certainty - must default to 100
	
	double weight = 1;
	String workloadID = "";
	boolean attachedToActivity = false;
	
	@SuppressWarnings("unused")
	public Conclude(Expression resultComparison, int bc, int fc) {
		this.resultComparison = resultComparison;
		if ((!(resultComparison instanceof RelComp)) && 
				(!(resultComparison instanceof EvalValCompExpOpExp)))
			throw new RuntimeException("Conclude: expression must be assignExp or RelComp");
		if (resultComparison instanceof EvalValCompExpOpExp) {
			
			EvalValCompExpOpExp exp = (EvalValCompExpOpExp) resultComparison;
		}
		this.bc = bc;
		this.fc = fc;
	}
	
	@SuppressWarnings("unused")
	public Conclude(Expression resultComparison, int bc, int fc, String ID) {
		this.resultComparison = resultComparison;
		if ((!(resultComparison instanceof RelComp)) && 
				(!(resultComparison instanceof EvalValCompExpOpExp)))
			throw new RuntimeException("Conclude: expression must be assignExp or RelComp");
		if (resultComparison instanceof EvalValCompExpOpExp) {
			EvalValCompExpOpExp exp = (EvalValCompExpOpExp) resultComparison;
		}
		this.bc = bc;
		this.fc = fc;
		
	}
	
	public boolean eval(Basic b, Frame f){
		return true;
	}
	
	public Expression getResult() {
		return resultComparison;
	}
	
	public int getBc() {
		return bc;
	}
	
	public int getFc() {
		return fc;
	}
	
	public String toString() {
		String retVal = "conclude(" +
				resultComparison.toString() + ", " +
				"bc: " + Integer.toString(bc) + ", " +
				"fc: " + Integer.toString(fc) + ")" + "\n";
		return retVal;
	}
	
	public Object clone() {
		Conclude con = ((Conclude) super.clone());
		con.resultComparison = ((Expression) resultComparison.clone());
		return con;
	}
	
	
	
	public boolean getAttachedToActivity(){
		return attachedToActivity;
	}

	@Override
	public void createDeltaQueueEvent(EventSequence es, Basic b, Frame f) {
		this.setDuration(0);
		es.addDQEvent(this);		
	}

}
