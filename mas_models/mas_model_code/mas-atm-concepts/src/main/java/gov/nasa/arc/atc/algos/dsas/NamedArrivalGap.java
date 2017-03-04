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

package gov.nasa.arc.atc.algos.dsas;

import gov.nasa.arc.atc.AfoUpdate;

/**
 * @author ahamon
 *
 */
public class NamedArrivalGap extends SimpleArrivalGap {

	private final AfoUpdate sUpdate;
	private final AfoUpdate eUpdate;
	private final int time;

	/**
	 * 
	 * @param simulationTime the time the gap is mesured in the simulation
	 * @param startUpdate the update corresponding to the first arriving afo
	 * @param endUpdate the update corresponding to the second arriving afo
	 */
	public NamedArrivalGap(final int simulationTime, final AfoUpdate startUpdate, final AfoUpdate endUpdate) {
		super((int) startUpdate.getEta() + simulationTime, (int) endUpdate.getEta() + simulationTime);
		sUpdate = startUpdate;
		eUpdate = endUpdate;
		time = simulationTime;
	}

	public String getFirstArrivalName() {
		return sUpdate.getAfoName();
	}

	public String getLastArrivalName() {
		return eUpdate.getAfoName();
	}

	public int getSimulationTime() {
		return time;
	}

	public double getFirstETA() {
		return sUpdate.getEta();
	}

	public double getLastETA() {
		return eUpdate.getEta();
	}

}
