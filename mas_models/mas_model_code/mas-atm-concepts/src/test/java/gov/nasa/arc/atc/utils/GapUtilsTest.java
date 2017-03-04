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

import gov.nasa.arc.atc.algos.dsas.SimpleDepartureGap;

/**
 * 
 * @author ahamon
 *
 */
public class GapUtilsTest {

	// @Test
	// public void testCalculateArrivalType() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetEarliestDeparture() {
	// fail("Not yet implemented");
	// }

	@Test
	public void testCalculateNbArrivals() {
		final int gapStart = 10;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + 5);
		int nbArr1 = gap1.getNbArrivalsPossible();
		assertEquals(0, nbArr1);
		//
		SimpleDepartureGap gap2 = new SimpleDepartureGap(gapStart, gapStart + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN - 1);
		int nbArr2 = gap2.getNbArrivalsPossible();
		assertEquals(0, nbArr2);
		//
		SimpleDepartureGap gap3 = new SimpleDepartureGap(gapStart, gapStart + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN);
		int nbArr3 = gap3.getNbArrivalsPossible();
		assertEquals(1, nbArr3);
		//
		SimpleDepartureGap gap4 = new SimpleDepartureGap(gapStart, gapStart + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN + Constants.ARR_ARR_MIN - 1);
		int nbArr4 = gap4.getNbArrivalsPossible();
		assertEquals(1, nbArr4);
		//
		SimpleDepartureGap gap5 = new SimpleDepartureGap(gapStart, gapStart + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN + Constants.ARR_ARR_MIN);
		int nbArr5 = gap5.getNbArrivalsPossible();
		assertEquals(2, nbArr5);
		//
		SimpleDepartureGap gap6 = new SimpleDepartureGap(gapStart, gapStart + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN + 2 * Constants.ARR_ARR_MIN);
		int nbArr6 = gap6.getNbArrivalsPossible();
		assertEquals(3, nbArr6);
		//
		SimpleDepartureGap gap7 = new SimpleDepartureGap(gapStart, gapStart + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN + 2 * Constants.ARR_ARR_MIN - 1);
		int nbArr7 = gap7.getNbArrivalsPossible();
		assertEquals(2, nbArr7);
	}

}
