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

import java.util.List;
import org.apache.log4j.Logger;

import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.common.IObject;
import gov.nasa.arc.brahms.vm.api.common.IParameter;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

//import gov.nasa.arc.brahms.jac.AbstractExternalActivity;
//import gov.nasa.arc.brahms.jac.ExternalException;

public class Update_Bucket_Two extends AbstractExternalActivity {

	public final static Logger LOGGER = Logger.getLogger(Update_Bucket_Two.class);

	@Override
	public void doActivity() throws ExternalException {
		try {

			IParameter ibucketParameter = this.getParameter("ibucket");
			IObject ibucket = null;

			assert(ibucketParameter != null);
			
			switch (ibucketParameter.getContentType()) {
			case IParameter.VARIABLE: {
				ibucket = (IObject) ibucketParameter.getVariableValue().getConceptValue();
				break;
			} // VARIABLE
			case IParameter.CONCEPT: {
				ibucket = (IObject) ibucketParameter.getConceptValue();
				break;
			} // CONCEPT
			default: {
				throw new ExternalException(
						"Invalid parameter value for 'icoord' parameter, must be a variable of type 'Coordinate'");
			} // default
			} // end switch

			IParameter twoParameter = this.getParameter("two");
			IObject two = null;

			assert(twoParameter != null);
			switch (twoParameter.getContentType()) {
			case IParameter.VARIABLE: {
				two = (IObject) twoParameter.getVariableValue().getConceptValue();
				break;
			} // VARIABLE
			case IParameter.CONCEPT: {
				two = (IObject) twoParameter.getConceptValue();
				break;
			} // CONCEPT
			default: {
				throw new ExternalException(
						"Invalid parameter value for 'icoord' parameter, must be a variable of type 'Coordinate'");
			} // default
			} // end switch

			IContext ctx = this.getContext();
			
			List<IConcept> myboxes = null;
			if (ibucket.hasBeliefRelationAnyValue(ibucket, "myboxes", false, ctx)){
				myboxes = (List<IConcept>)ibucket.getBeliefRelationConcepts(ibucket, "myboxes", ctx);	
			};

			LOGGER.info("hasBeliefRelationValue = " + ibucket.hasBeliefRelationAnyValue(ibucket, "myboxes", true, ctx));

			if (myboxes == null) {
				LOGGER.info("myboxes is empty");
				return;
			}

			LOGGER.info(myboxes.toString());

			for (IConcept c : myboxes) {
				if (c.getName().equals(two.getName())) {
					ibucket.removeBeliefRelationConcept(ibucket, "myboxes", two, ctx);
					return;
				}
			}
			
			ibucket.addBeliefRelationConcept(ibucket, "myboxes", two, ctx);
			
//			if (myboxes.contains(two)) {
//				ibucket.removeBeliefRelationConcept(ibucket, "myboxes", two, ctx);
//			} else {
//				ibucket.addBeliefRelationConcept(ibucket, "myboxes", two, ctx);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} // end try
	}
}
