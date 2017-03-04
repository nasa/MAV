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

import org.apache.log4j.Logger;

import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.common.IObject;
import gov.nasa.arc.brahms.vm.api.common.IParameter;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

//import gov.nasa.arc.brahms.jac.AbstractExternalActivity;
//import gov.nasa.arc.brahms.jac.ExternalException;

public class Increment_IntBoxBox extends AbstractExternalActivity {

	public final static Logger LOGGER = Logger.getLogger(Increment_IntBoxBox.class);

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
			
			IObject ibox = null;
			if (iboxbox.hasBeliefAttributeAnyValue(iboxbox, "box", false, ctx)){
			      ibox = (IObject)iboxbox.getBeliefAttributeConcept(iboxbox, "box", ctx);
			};
			
			int ival = 0;
			if (ibox.hasBeliefAttributeAnyValue(ibox, "val", false, ctx)){
			      ival = ibox.getBeliefAttributeInt(ibox, "val", ctx);
			};
			LOGGER.info("Increment_IntBoxBox: ibox.val = " + ++ival);			
			ibox.setBeliefAttributeInt(ibox, "val", ival, ctx);
			
		} catch (Exception e) {
			e.printStackTrace();
		} // end try
	}
}
