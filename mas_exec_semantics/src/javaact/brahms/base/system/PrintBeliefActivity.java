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

package brahms.base.system;

import java.util.List;

import gov.nasa.arc.brahms.model.Belief;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.vm.api.common.CommonUtils;
import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IObject;
import gov.nasa.arc.brahms.vm.api.common.IParameter;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

import org.apache.log4j.Logger;

public class PrintBeliefActivity extends AbstractExternalActivity {

	final static Logger LOGGER = Logger.getLogger(PrintBeliefActivity.class);

	private void printBeliefs(IConcept concept, List<Belief> beliefs) {
		try{ 
		for (Belief b : beliefs) {
			Expression exp = b.getBelief();
			LOGGER.info(concept.getName() + " : " + exp.toString());
		} } catch(Exception e) {
			System.out.println(concept.getName());
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void doActivity() throws ExternalException {

		IParameter oConceptParameter = getParameter("aboutConcept");
		IParameter oAttributeParameter = getParameter("aboutAttribute");
		IParameter oAttributeTypeParameter = getParameter("aboutAttributeType");

		IConcept iConcept = oConceptParameter.getConceptValue();
	
		String attributeName = oAttributeParameter.getVariablePassedName();
		String attributeType = oAttributeTypeParameter.getVariablePassedName();
		
		String identifier;
		if(attributeType.equals("belief") || 
				attributeType.equals("attribute")) {
			identifier = CommonUtils.constructBeliefName(iConcept, iConcept,
				attributeName);
		} else if(attributeType.equals("relation")) {
			identifier = CommonUtils.constructRelationName(iConcept, iConcept,
			   attributeName);
		} else {
			throw new RuntimeException("the type can be belief or ");
		}
		if (iConcept instanceof IAgent) {
			IAgent agent = (IAgent) iConcept;
			Agent brahmsAgent = agent.getBrahmsAgent();
			printBeliefs(agent, brahmsAgent.getBeliefs().get(identifier));
		} else if (iConcept instanceof IObject) {
			IObject obj = (IObject) iConcept;
			Object_b brahmsObj = obj.getBrahmsObject();
			printBeliefs(obj, brahmsObj.getBeliefs().get(identifier));
		}
	}

}
