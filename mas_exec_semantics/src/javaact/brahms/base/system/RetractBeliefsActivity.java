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
import java.util.Map;

import org.apache.log4j.Logger;

import gov.nasa.arc.brahms.common.IConstants;
import gov.nasa.arc.brahms.model.Belief;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.vm.api.common.CommonUtils;
import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IObject;
import gov.nasa.arc.brahms.vm.api.common.IParameter;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class RetractBeliefsActivity extends AbstractExternalActivity{
	final static Logger LOGGER = Logger.getLogger(RetractBeliefsActivity.class);
	
	@Override
	public void doActivity() throws ExternalException {
		System.out.println("TODO: Need to implement RetractBeliefsActivity");
		
		IParameter oConceptParameter = getParameter("subject");
		IParameter oAttributeParameter = getParameter("attribute");
		IParameter oIndexParameter = null;
		
		try {
	        oIndexParameter = getParameter("index");
	      } catch (Exception ee) {
	        // no index parameter declared
	    	  System.out.println("there is no parameter index");
	      } // end try
		
		IConcept iConcept = oConceptParameter.getConceptValue();
		IConcept currentConcept = this.getPerformedBy();
		String attributeName = oAttributeParameter.getVariablePassedName();

		String identifier = CommonUtils.constructBeliefName(currentConcept, iConcept,
				attributeName);
		
		System.out.println("identifer:" + identifier);
		List<Belief> beliefs;
		if (currentConcept instanceof IAgent) {
			IAgent agent = (IAgent) currentConcept;
			Agent brahmsAgent = agent.getBrahmsAgent();
			beliefs = brahmsAgent.getBeliefs().get(identifier);
		} else if (currentConcept instanceof IObject) {
			IObject obj = (IObject) currentConcept;
			Object_b brahmsObj = obj.getBrahmsObject();
			beliefs = brahmsObj.getBeliefs().get(identifier);
		} else {
			throw new RuntimeException("concept needs to be an agent or object");
		}
		System.out.println("name of concept is :" + currentConcept.getName());
		if(oAttributeParameter != null && oIndexParameter != null) {
			retractBeliefFromMapIndex(beliefs, oIndexParameter);
		}
		//System.exit(1);
	}
	
	protected void retractBeliefFromMapIndex(List<Belief> beliefs, IParameter index) {
		System.out.println("coming to retractBeliefFromMap" + index.getContentType());
		if(index.getContentType() == IConstants.INT) {
			System.out.println(index.getIntValue());
		}
		for(Belief b : beliefs) {
			System.out.println(b.getBelief().getClass().getName());
			if(!(b.getBelief() instanceof MapExpression)) return;
			MapExpression mapExp = (MapExpression) b.getBelief();
			Map<String, Value> map = mapExp.getMap();
			System.out.println("the value of the map before" + map.toString());
			String removeIndex = null;
			for(String str : map.keySet()) {
				if(CommonExpUtils.isInteger(str) &&
						Integer.valueOf(str) == index.getIntValue()) {
					removeIndex = str;
					break;
				}
				
			}
			if(removeIndex != null)
				map.remove(removeIndex);
			System.out.println("the value of the map after" + map.toString());

		}
	}
	
}
