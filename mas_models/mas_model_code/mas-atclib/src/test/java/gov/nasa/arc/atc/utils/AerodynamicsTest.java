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

package gov.nasa.arc.atc.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nasa.arc.atc.utils.Aerodynamics;
import gov.nasa.arc.atc.utils.Constants;

/**
 * 
 * @author ahamon
 *
 */
public class AerodynamicsTest {

	@Test
	public void testTrueAirSpeedISA1() {
		// Altitude [m]
		double alt = 10000;
		// Air temperature [K]
		double temp = Aerodynamics.tempISA(alt);
		// Static air pressure [Pa]
		double press = Aerodynamics.pressureISA(alt);
		// Speed of sound [m/s]
		double a = Aerodynamics.speedOfSoundISA(alt);
		// Desity of air [kg/m^3]
		double rho = Aerodynamics.densityISA(alt);
		// Calibrated Air Speed, converted from [kts] to [m/s]
		double cAS = Constants.KTS2MS * 290;
		// True Air Speed in [m/s]
		double tAS = Aerodynamics.trueAirSpeedISA(alt, cAS);
		double tASkts = tAS / Constants.KTS2MS;

		// calculated values from http://www.hochwarth.com/misc/AviationCalculator.html
		double refTAS = 472.0032987337459;
		assertEquals(refTAS, tASkts, Aerodynamics.AERO_PRECISION);
		double refTemp = 223.14999999999998;
		assertEquals(refTemp, temp, Aerodynamics.AERO_PRECISION);
		double refPress = 26436.25867877213;
		assertEquals(refPress, press, Aerodynamics.AERO_PRECISION);
		double refRHO = 0.4127062174077711;
		assertEquals(refRHO, rho, Aerodynamics.AERO_PRECISION);
		double refA = 982.4909208817216 * Constants.FT2METER;
		assertEquals(refA, a, Aerodynamics.AERO_PRECISION);
		//
	}

	@Test
	public void testTrueAirSpeedISA2() {
		// Altitude [m]
		double alt = 10000 * Constants.FT2METER;
		// Air temperature [K]
		double temp = Aerodynamics.tempISA(alt);
		// Static air pressure [Pa]
		double press = Aerodynamics.pressureISA(alt);
		// Speed of sound [m/s]
		double a = Aerodynamics.speedOfSoundISA(alt);
		// Desity of air [kg/m^3]
		double rho = Aerodynamics.densityISA(alt);
		// Calibrated Air Speed, converted from [kts] to [m/s]
		double cAS = Constants.KTS2MS * 290;
		// True Air Speed in [m/s]
		double tAS = Aerodynamics.trueAirSpeedISA(alt, cAS);
		double tASkts = tAS / Constants.KTS2MS;

		// calculated values from http://www.hochwarth.com/misc/AviationCalculator.html
		double refTAS = 334.07693902072765;
		assertEquals(refTAS, tASkts, Aerodynamics.AERO_PRECISION);
		//
		double refTemp = 268.33799999999997;
		assertEquals(refTemp, temp, Aerodynamics.AERO_PRECISION);
		//
		double refPress = 69681.65343853258;
		assertEquals(refPress, press, Aerodynamics.AERO_PRECISION);
		//
		double refRHO = 0.9046366502547819;
		assertEquals(refRHO, rho, Aerodynamics.AERO_PRECISION);
		//
		double refA = 1077.3856567076107 * Constants.FT2METER;
		assertEquals(refA, a, Aerodynamics.AERO_PRECISION);
		//
	}
}
