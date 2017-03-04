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

package gov.nasa.arc.brahms.atmjava.activities;

import org.apache.log4j.Logger;

import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class ValuesForTower extends AbstractExternalActivity {

	public final static Logger LOGGER = Logger.getLogger(ValuesForTower.class);
	
	@Override
	public void doActivity() throws ExternalException {
	
		Utils instance = Utils.getInstance();
		synchronized(instance) {
			this.setParameterInt("o_lastArrival", instance.lastArrivalTime);
			this.setParameterDouble("o_topETA", instance.topETA);
		}
	}
}
