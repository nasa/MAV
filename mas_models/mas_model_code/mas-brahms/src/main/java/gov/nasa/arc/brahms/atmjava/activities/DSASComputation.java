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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.simulation.wrapper.BrahmsWrapper;
import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.common.IObject;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class DSASComputation extends AbstractExternalActivity {

	public final static Logger LOGGER = Logger.getLogger(DSASComputation.class);
	
	@Override
	public void doActivity() throws ExternalException {
		
//		Utils instance = Utils.getInstance();
		synchronized (Utils.getInstance()) {
			IObject i_TSS = (IObject) this.getParameterConcept("tl");
			int globalClockTime = this.getParameterInt("globalTime");
			IContext ctx = this.getContext();

			List<AFO> tssTimeLine = new ArrayList<>();
			Map<AFO, IAgent> slotMarkers = new HashMap<>();

			Map<Object, Object> TSSTimeLine = Utils.readSomeTimeLineMapData(i_TSS, ctx, slotMarkers, tssTimeLine,
					"tssTimeLine");

			System.out.print("DSASComputation::doActivity([ ");
			for (AFO afoObject : tssTimeLine) {
				System.out.print(afoObject.getName() + " " + afoObject.getFlightPlan().getCurrentSegmentIndex() + " ");
			}
			System.out.println("])");


			// TSS.scheduler(tssTimeLine);
			try {
				tssTimeLine = BrahmsWrapper.invokeTSS(this.getClass().getName(),tssTimeLine, globalClockTime);
				tssTimeLine.addAll(Utils.getInstance().deptCache);
				tssTimeLine = BrahmsWrapper.invokeDSAS2(this.getClass().getName(),tssTimeLine, globalClockTime);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			TSSTimeLine.clear();

			for (Map.Entry<AFO, IAgent> i : slotMarkers.entrySet()) {
				AFO afoObject = i.getKey();
				IAgent slotMarker = i.getValue();
				try {
					Utils.syncrhronizeAFOObject(afoObject, slotMarker, ctx);
				} catch (AssertionError e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}	
}
