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

package gov.nasa.arc.atc.reports;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.reports.dsas.DSASReportInputs;

/**
 * 
 * @author ahamon
 *
 */
public class DSASReporter {

	private static final Logger LOG = Logger.getGlobal();

	private static final List<DSASReportInputs> REPORT_INPUTS = new LinkedList<>();

	private DSASReporter() {
		// private utility constructor
	}

	/**
	 * 
	 * @param reportInputs the report inputs to register
	 */
	public static void registerReportInputs(DSASReportInputs reportInputs) {
		LOG.log(Level.WARNING, "adding reportInputs: {0}", reportInputs);
		REPORT_INPUTS.add(reportInputs);
	}

	/**
	 * 
	 * @return the registered {@link DSASReportInputs}
	 */
	public static List<DSASReportInputs> getReportInputs() {
		return Collections.unmodifiableList(REPORT_INPUTS);
	}

}
