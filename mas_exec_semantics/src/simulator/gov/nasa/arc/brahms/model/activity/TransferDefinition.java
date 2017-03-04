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

package gov.nasa.arc.brahms.model.activity;

import gov.nasa.arc.brahms.model.Expression;

/**
 * 
 * @author nrungta
 *
 *
 *=== TDF.transfer-definition === 
transfer-definition 	::= 	transfer-action ( communicative-act |
DET.resultcomparison )
transfer-action 	::= 	send | receive
communicative-act 	::= 	OBJ.object-name | PAC.param-name
 */

public class TransferDefinition implements Cloneable {

	
	String transferAction; // sent or receive
	Expression exp; // comunicative-acti or resultComparison
	
	public TransferDefinition(String transferAction,
			Expression exp) {
		this.transferAction = transferAction;
		this.exp = exp;
	}
	
	public String getTransferAction() {
		return transferAction;
	}
	
	public Expression getExpression() {
		return exp;
	}
	
	public Object clone() {
		return new TransferDefinition(new String(transferAction),
				(Expression) exp.clone());
	}
	
	public String toString() {
		return transferAction + exp.toString() + "\n"; 
	}
	
}
