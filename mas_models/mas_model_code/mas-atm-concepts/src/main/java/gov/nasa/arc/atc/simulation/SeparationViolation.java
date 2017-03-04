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

package gov.nasa.arc.atc.simulation;

/**
 * 
 * @author ahamon
 *
 */
public class SeparationViolation {

	// Do we need to add a time stamp?

	private final double reqSep;
	private final double actualSep;
	private final String afo1;
	private final String afo2;
	private final int timeStamp;

	/**
	 * Creates a separation violation report. There is no order needed between both AFOs.
	 * 
	 * @param requiredSeparation the required separation between two afo in [NM]
	 * @param actualSeparation the actual separation in [NM]
	 * @param ovni1 the name of the first AFO involved
	 * @param ovni2 the name of the second AFO involved
	 * @param time the simulation time the loss of separation occured
	 */
	public SeparationViolation(double requiredSeparation, double actualSeparation, String ovni1, String ovni2, int time) {
		reqSep = requiredSeparation;
		actualSep = actualSeparation;
		afo1 = ovni1;
		afo2 = ovni2;
		timeStamp = time;
	}

	/**
	 * 
	 * @return the actual separation observed (in [NM])
	 */
	public double getActualSep() {
		return actualSep;
	}

	/**
	 * 
	 * @return the required separation in [NM]
	 */
	public double getReqSep() {
		return reqSep;
	}

	/**
	 * 
	 * @return the first AFO involved
	 */
	public String getAfo1() {
		return afo1;
	}

	/**
	 * 
	 * @return the second AFO involved
	 */
	public String getAfo2() {
		return afo2;
	}

	/**
	 * 
	 * @return return the time stamp of the separation loss
	 */
	public int getTimeStamp() {
		return timeStamp;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Separation violation between ");
		sb.append(afo1);
		sb.append(" and ");
		sb.append(afo2);
		sb.append(" :: sep=");
		sb.append(actualSep);
		sb.append("[NM] << ");
		sb.append(reqSep);
		sb.append("[NM] required, loss at t=");
		sb.append(timeStamp);
		return sb.toString();
	}
}
