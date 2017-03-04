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

package gov.nasa.arc.atc;

import java.util.Collections;
import java.util.List;

import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.simulation.SimulationContext;

/**
 * 
 * @author ahamon
 *
 */
public final class SimulationResultsHolder {

	// figure out what to do with this class
	
	
	public static  void holdSimulationContext(SimulationContext context){
		
		
		
		
		DataModelInput dataInputs = new DataModelInput();
		
		DataModel model = new DataModel(dataInputs);
		
	}
	
	public static List<?> getContexts(){
		return Collections.emptyList();
	}
	
}
