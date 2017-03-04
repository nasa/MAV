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

package gov.nasa.arc.atc.functions.factories;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.functions.CandidateFunction;
import gov.nasa.arc.atc.functions.MeterFixCandidateFunction;
import gov.nasa.arc.atc.geography.ATCGeography;

/**
 * 
 * @author ahamon
 *
 */
public class MeterFixCandidateFunctionFactory implements CandidateFunctionFactory {

	private static final Logger LOG = Logger.getGlobal();

	private MeterFixJFXConfigurator configurator;

	@Override
	public CandidateFunction createFunction() {
		Object[] parameters = configurator.getParameters();
		if (parameters == null) {
			throw new IllegalArgumentException("MeterFixCandidateFunction parameters cannot be null, requires an ATCGeography and a time");
		} else if (parameters.length < 2) {
			throw new IllegalArgumentException("MeterFixCandidateFunction needs to parameters parameters, requires an ATCGeography and a time but was given only " + parameters.length + " parameter(s)");
		}
		if (parameters.length > 2) {
			for (int i = 2; i < parameters.length; i++) {
				LOG.log(Level.WARNING, "Paramater not taken into account for MeterFixCandidateFunction: {0}", parameters[i]);
			}
		}
		// retreive geography
		ATCGeography geography = getGeography(parameters[0]);

		// retreive time stamp
		int time = getTime(parameters[1]);

		return new MeterFixCandidateFunction(geography, time);
	}

	@Override
	public FunctionConfigurator getFunctionConfigurator() {
		if (configurator == null) {
			configurator = new MeterFixJFXConfigurator();
		}
		return configurator;
	}

	/*
	 * Methods used for function parameter retreival
	 */

	private ATCGeography getGeography(Object param) {
		if (param instanceof ATCGeography) {
			return (ATCGeography) param;
		} else {
			throw new IllegalArgumentException("MeterFixCandidateFunction first parameter given shall be an ATCGeography but is " + param);
		}
	}

	private int getTime(Object param) {
		if (param instanceof Integer) {
			return (int) param;
		} else {
			throw new IllegalArgumentException("MeterFixCandidateFunction second parameter given shall be an Integer but is " + param);
		}
	}
}
