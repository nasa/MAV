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

package gov.nasa.arc.atc.algos;

import gov.nasa.arc.atc.algos.dsas.ParametricDSAS;
import gov.nasa.arc.atc.algos.tss.TSS;
import gov.nasa.arc.atc.functions.DistanceSeparationFunction;
import gov.nasa.arc.atc.functions.MeterFixCandidateFunction;
import gov.nasa.arc.atc.functions.SimpleGapScheludingFunction;
import gov.nasa.arc.atc.geography.ATCGeography;

/**
 * 
 * @author ahamon
 *
 */
public class AlgorithmFactory {

	public static final int DEFAULT_DSAS_HORIZON = 1200;

	private AlgorithmFactory() {
		// private utility constructor
	}

	public static TSS createTSSAlgorithm(DistanceSeparationFunction function) {
		return new TSS(function);
	}

	public static DepartureArrivalAlgorithm createDefaultParameteredDSAS() {
		return new ParametricDSAS((traj, time) -> true, (traj, time) -> true, new SimpleGapScheludingFunction());
	}

	public static DepartureArrivalAlgorithm createParameteredDSAS(ATCGeography geography) {
		return new ParametricDSAS(new MeterFixCandidateFunction(geography, DEFAULT_DSAS_HORIZON), (traj, time) -> true, new SimpleGapScheludingFunction());
	}

}
