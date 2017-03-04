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

/**
 * 
 * @author josie & nrungta
 *thoughtframe 	::= 	thoughtframe thoughtframe-name
 *{
 *{ display : ID.literal-string ; }						//inherited from Frame
 *{ repeat : ID.truth-value ; }							//inherited from Frame
 *{ priority : ID.unsigned ; }							//inherited from Frame
 *{ WFR.variable-decl }									//inherited from Frame
 *{ [ WFR.precondition-decl thoughtframe-body-decl ] |	//inherited from Frame
 *thoughtframe-body-decl }								//inherited from Frame
 *}
 */
 public class ThoughtFrame extends Frame {
	 
	/**
	 * ThoughtFrame Constructor
	 */
	public ThoughtFrame(String name, String display, 
			String repeat, int priority){
		super(name, display, priority, repeat);
	}
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("thoughtFrame " + name + "\n");
		retVal.append(super.toString());
		return retVal.toString();
	}
	
	public Object clone() throws CloneNotSupportedException {
		ThoughtFrame tf = ((ThoughtFrame) super.clone());
		return tf;
	}

	//thoughtframes always operate on beliefs
	//from the Brahms documentation
	/**A thoughtframe allows an agent or object to
	deduce new beliefs from existing beliefs **/
	@Override
	public boolean operatesOnFacts() {
		return false;
	}
	

}
