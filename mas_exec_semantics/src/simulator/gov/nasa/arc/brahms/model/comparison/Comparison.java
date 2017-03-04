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

package gov.nasa.arc.brahms.model.comparison;

import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Variable;

public interface Comparison {
	//String type; // "known", "unknown", "not", "knownval", "" for detect
	
	public boolean eval(Basic b, Frame f, Map<String, Value> varCtx);
	
	public boolean factEvaluatesTrue(Basic b, Frame f);
	
	public String toString();
	
	public Expression getExp();
	
	public Object clone();
	
	public Variable getNextFreeVariable(Frame f, Map<String, Value> varCtx);
	
	public List<Value> getVariableBindings(Basic b, Frame f, 
							Variable var, Map<String, Value> varCtx);
}
