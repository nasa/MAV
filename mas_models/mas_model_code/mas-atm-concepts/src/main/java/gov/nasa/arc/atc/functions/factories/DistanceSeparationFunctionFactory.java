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

import gov.nasa.arc.atc.functions.DistanceSeparationFunction;
import gov.nasa.arc.atc.functions.SeparationFunction;
import gov.nasa.arc.atc.geography.Airport;

/**
 * 
 * @author ahamon
 *
 */
public class DistanceSeparationFunctionFactory implements SeparationFunctionFactory {

	private static final Logger LOG = Logger.getGlobal();

	private DistanceSeparationJFXConfigurator configurator;

	@Override
	public SeparationFunction createFunction() {
		Object[] parameters = configurator.getParameters();
		if (parameters == null) {
			throw new IllegalArgumentException("DistanceSeparationFunction parameters cannot be null, requires an Airport and a time");
		} else if (parameters.length < 1) {
			throw new IllegalArgumentException("DistanceSeparationFunction needs to parameters parameters, requires an Airport but was given only " + parameters.length + " parameter(s)");
		}
		if (parameters.length > 1) {
			for (int i = 2; i < parameters.length; i++) {
				LOG.log(Level.WARNING, "Parameter not taken into account for DistanceSeparationFunction: {0}", parameters[i]);
			}
		}
		// retrieve airport
		Airport airport = getAirport(parameters[0]);

		return new DistanceSeparationFunction(airport);
	}

	@Override
	public FunctionConfigurator getFunctionConfigurator() {
		if (configurator == null) {
			configurator = new DistanceSeparationJFXConfigurator();
		}
		return configurator;
	}

	/*
	 * Methods used for function parameter retrieval
	 */

	private Airport getAirport(Object param) {
		if (param instanceof Airport) {
			return (Airport) param;
		} else {
			throw new IllegalArgumentException("DistanceSeparationFunction first parameter given shall be an Airport but is " + param);
		}
	}

}
