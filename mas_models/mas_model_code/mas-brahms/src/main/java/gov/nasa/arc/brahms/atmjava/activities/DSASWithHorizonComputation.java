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
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

/**
 * 
 * @author ahamon
 *
 */
public class DSASWithHorizonComputation extends AbstractExternalActivity {

	public static final Logger LOGGER = Logger.getLogger(DSASWithHorizonComputation.class);

	@Override
	public void doActivity() {

		Utils instance = Utils.getInstance();
		synchronized (instance) {
			IObject iTSS = (IObject) this.getParameterConcept("tl");
			int globalClockTime = this.getParameterInt("globalTime");
			int dsasHorizon = this.getParameterInt("horizonTime");
			System.out.print("DSASComputation:: dsasHorizon ="+dsasHorizon+"\n");
			
			IContext ctx = this.getContext();

			List<AFO> aFOs = new ArrayList<>();
			Map<AFO, IAgent> slotMarkers = new HashMap<>();

			Map<Object, Object> tssTimeLine = Utils.readSomeTimeLineMapData(iTSS, ctx, slotMarkers, aFOs, "tssTimeLine");

			System.out.print("DSASComputation::doActivity([ ");
			for (AFO afoObject : aFOs) {
				System.out.print(afoObject.getName() + " " + afoObject.getFlightPlan().getCurrentSegmentIndex() + " ");
			}
			System.out.println("])");

			try {
				aFOs = BrahmsWrapper.invokeTSS(this.getClass().getName(),aFOs, globalClockTime);
				aFOs.addAll(Utils.getInstance().deptCache);
				aFOs = BrahmsWrapper.invokeParametricDSAS(this.getClass().getName(),aFOs, globalClockTime,dsasHorizon);
				
				assert aFOs != null;
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			tssTimeLine.clear();

			for (Map.Entry<AFO, IAgent> i : slotMarkers.entrySet()) {
				AFO afoObject = i.getKey();
				IAgent slotMarker = i.getValue();
				try {
					Utils.syncrhronizeAFOObject(afoObject, slotMarker, ctx);
				} catch (AssertionError e) {
					LOGGER.error(e);
					System.exit(1);
				}
			}
		}
	}
}
