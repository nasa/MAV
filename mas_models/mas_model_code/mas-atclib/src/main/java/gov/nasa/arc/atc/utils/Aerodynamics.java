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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.utils;

/**
 * 
 * @author krantz
 *
 */
public class Aerodynamics {

	/**
	 * precision value guaranted via JUnit tests
	 */
	public static final double AERO_PRECISION = 0.0001;

	/**
	 * Standard values for the atmosphare at sea level according to Torenbeek
	 * (2009) (in repository, thesis/literature) pressure [Pa]
	 */
	public static final double P0 = 1.013250 * 100000;

	/**
	 * temp [K]
	 */
	public static final double T0 = 288.15;

	/**
	 * density [kg/m3]
	 */
	public static final double RHO_0 = 1.2250;

	/**
	 * speed of sound [m/s]
	 */
	public static final double A0 = 340.29;

	/**
	 * ratio of specific heats
	 */
	public static final double GAMMA = 1.4;

	/**
	 * Gas constant
	 */
	public static final double R = 287.053;

	/**
	 * temperature gradiant up to 11km
	 */
	public static final double LAMBDA = -0.0065;

	/**
	 * gravity [m/s2]
	 */
	public static final double G = 9.80665;

	/**
	 * height of tropopause [m]
	 */
	public static final double H_TROP = 11000;

	/**
	 * sea level (0 m)
	 */
	public static final double SEA_LEVEL = 0;

	private Aerodynamics() {
		// private utility constuctor
	}

	/**
	 * tempISA. Method for calculating the air temperature in an International
	 * Standard Atmosphare (ISA)
	 * 
	 * @param altitude
	 * @return temp - temparature at specified altitude
	 */
	public static double tempISA(double altitude) {
		// Std temp at Sea Level
		double temp;
		// Between 0 and 11 km
		if (altitude <= H_TROP && altitude >= 0) {
			temp = T0 + LAMBDA * altitude;
		}
		// Isothermal layer above 11 km
		else if (altitude > H_TROP) {
			temp = T0 + LAMBDA * H_TROP;
		}
		// Else return std temp
		else {
			temp = T0;
		}
		return temp;
	}// END tempISA

	/**
	 * The pressure variation with altitude;
	 * 
	 * @param altitude [m]
	 * @return pressure [Pa]
	 */
	public static double pressureISA(double altitude) {
		double p;
		if (altitude <= H_TROP) {
			double factor = 1 + ((LAMBDA * altitude) / T0);
			double power = -(G / (LAMBDA * R));
			p = P0 * Math.pow(factor, power);
		} else {
			double temperatureISA = tempISA(H_TROP);
			double hScale = (R * temperatureISA) / G;
			double pTrop = pressureISA(H_TROP);
			p = pTrop * Math.exp(-(altitude - H_TROP) / hScale);
		}
		return p;
	}

	public static double densityISA(double altitude) {
		double rho;
		if (altitude <= H_TROP) {
			double factor = 1 + ((LAMBDA * altitude) / T0);
			double power = -(G / (LAMBDA * R)) - 1;
			rho = RHO_0 * Math.pow(factor, power);
		} else {
			double temperatureISA = tempISA(H_TROP);
			double hScale = (R * temperatureISA) / G;
			double rhoTrop = densityISA(H_TROP);
			rho = rhoTrop * Math.exp(-(altitude - H_TROP) / hScale);
		}
		return rho;

	}

	/**
	 * speedOfSound. Method for calculating the speed of sound as a function of
	 * altitude in the ISA.
	 * 
	 * @param altitude [m]
	 * @return
	 */
	public static double speedOfSoundISA(double altitude) {
		double a;
		double temperature = tempISA(altitude);
		a = Math.sqrt(GAMMA * R * temperature);
		return a;
	}// END speedOfSound

	/**
	 * impactPressure. Method for calculating the impact pressure or dynamic
	 * pressure according to: Gracey, William (1980),
	 * "Measurement of Aircraft Speed and Altitude", NASA Reference Publication
	 * 1046. (Avail. in repository)
	 * 
	 * @param cAS calibrated air speed in [kts]
	 * @param altitude in [m]
	 * @return impact pressure/dynamic pressure [Pa]
	 */
	public static double impactPressure(double cAS, double altitude) {
		// TODO check why altitude is not used
		double arg0 = 1 + ((GAMMA - 1) / (2 * GAMMA)) * (RHO_0 / P0) * Math.pow(cAS, 2);
		double arg1 = GAMMA / (GAMMA - 1);
		// Dynamic pressure
		return P0 * (Math.pow(arg0, arg1) - 1);
	}

	/**
	 * compressibilityFactor. Method for calculating the compressability factor
	 * according to: Gracey, William (1980),
	 * "Measurement of Aircraft Speed and Altitude", NASA Reference Publication
	 * 1046. (Avail. in repository)
	 * 
	 * @param cAS [kts]
   	 * @param altitude [m]
	 * @return compressibility factor
	 */
	public static double compressibilityFactor(double cAS, double altitude) {
		double qC = impactPressure(cAS, altitude);
		double p = pressureISA(altitude);
		// Compressibility factor (no unit)
		return Math.sqrt((GAMMA / (GAMMA - 1)) * (p / qC) * (Math.pow(qC / p + 1, (GAMMA - 1) / GAMMA) - 1));
	}

	/**
	 * trueAirSpeedISA. Method for calculating the True Air Speed (TAS), given
	 * the Calibrated Air Speed (CAS), and the altitude. This method accounts
	 * for compressibility effects at high speeds. Equation from: Gracey,
	 * William (1980), "Measurement of Aircraft Speed and Altitude", NASA
	 * Reference Publication 1046. (Avail. in repository)
	 * 
	 * @param altitude [m]
	 * @param cAS [m/s]
   	 * @return
	 */
	public static double trueAirSpeedISA(double altitude, double cAS) {
		double f0 = compressibilityFactor(cAS, SEA_LEVEL);
		double f = compressibilityFactor(cAS, altitude);
		return cAS * (f / f0) * Math.sqrt(RHO_0 / densityISA(altitude));
	}// END trueAirSpeed

}
