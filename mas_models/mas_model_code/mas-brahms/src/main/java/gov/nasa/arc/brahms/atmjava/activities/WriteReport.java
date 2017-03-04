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

import gov.nasa.arc.atc.simulation.wrapper.BrahmsWrapper;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class WriteReport extends AbstractExternalActivity {

	protected final static Logger LOGGER = Logger.getLogger(WriteReport.class);

	@Override
	public void doActivity() throws ExternalException {
		System.out.println("the number of landed planes :" + Utils.getLandedPlanes());
		System.out.println("coming to write the report");
		try{ 
		int globalClockTime = this.getParameterInt("globalTime");
		int timeInc = this.getParameterInt("timeInc");
		BrahmsWrapper.exportReport(System.getProperty("user.dir"), globalClockTime, timeInc, "test");
		} catch (Exception e){
			System.err.println("error while generating the report");
			e.printStackTrace();
		}
	}
	
}
