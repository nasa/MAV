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

package external;

import java.util.Map;

import org.apache.log4j.Logger;

import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.common.IObject;
import gov.nasa.arc.brahms.vm.api.common.IParameter;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

//import gov.nasa.arc.brahms.jac.AbstractExternalActivity;
//import gov.nasa.arc.brahms.jac.ExternalException;

public class Read_IntBoxBox_Map extends AbstractExternalActivity {

	public final static Logger LOGGER = Logger.getLogger(Read_IntBoxBox_Map.class);

	@Override
	public void doActivity() throws ExternalException {
		try {

			IParameter iboxParameter = this.getParameter("iboxbox");
			IObject iboxbox = null;

			switch (iboxParameter.getContentType()) {
			case IParameter.VARIABLE: {
				iboxbox = (IObject) iboxParameter.getVariableValue().getConceptValue();
				break;
			} // VARIABLE
			case IParameter.CONCEPT: {
				iboxbox = (IObject) iboxParameter.getConceptValue();
				break;
			} // CONCEPT
			default: {
				throw new ExternalException(
						"Invalid parameter value for 'icoord' parameter, must be a variable of type 'Coordinate'");
			} // default
			} // end switch

			IContext ctx = this.getContext();
			
			Map<Object,Object> imp = null;
			if (iboxbox.hasBeliefAttributeAnyValue(iboxbox, "mp", false, ctx)){
			      imp = (Map<Object,Object>)iboxbox.getBeliefAttributeMap(iboxbox, "mp", false, ctx);	
			};
			
			LOGGER.info(imp.toString());
			iboxbox.setBeliefAttributeJava(iboxbox, "mp", imp.size()+1, imp.size()+1, ctx);
			
		} catch (Exception e) {
			e.printStackTrace();
		} // end try
	}
}
