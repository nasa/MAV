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

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nasa.arc.atc.utils.Constants;

/**
 * 
 * @author ahamon
 *
 */
public class SimpleDepartureGapTest {

	@Test
	public void testGetEarliestStartTimeAtIndex() {
		int arr1;
		int arr2;
		final int gapStart = 10;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN);
		arr1 = gap1.getEarliestTimeAtNumber(1);
		assertEquals(1, gap1.getNbArrivalsPossible());
		assertEquals(gapStart + Constants.DEP_ARR_MIN, arr1);
		//
		SimpleDepartureGap gap2 = new SimpleDepartureGap(gapStart, gapStart + Constants.DEP_ARR_MIN + Constants.ARR_DEP_MIN + Constants.ARR_ARR_MIN);
		assertEquals(2, gap2.getNbArrivalsPossible());
		arr1 = gap2.getEarliestTimeAtNumber(1);
		assertEquals(gapStart + Constants.DEP_ARR_MIN, arr1);
		arr2 = gap2.getEarliestTimeAtNumber(2);
		assertEquals(gapStart + Constants.DEP_ARR_MIN + Constants.ARR_ARR_MIN, arr2);
	}

	@Test
	public void testGetters() {
		final int gapStart = 10;
		final int gapEnd = 46;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapEnd);
		assertEquals(gapStart, gap1.getStartTime());
		assertEquals(gapEnd, gap1.getEndTime());
		assertEquals(gapEnd - gapStart, gap1.getGapDuration());
		assertNotNull(gap1.toString());
		assertTrue(gap1.toString().contains(Integer.toString(gap1.getStartTime())));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadArg2() {
		final int gapStart = 10;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart - 5);
		gap1.getEndTime();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEarliestStartTimeAtIndexFail1() {
		final int gapStart = 10;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + 5);
		gap1.getEarliestTimeAtNumber(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEarliestStartTimeAtIndexFail2() {
		final int gapStart = 10;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + 5);
		gap1.getEarliestTimeAtNumber(1);
	}

	@Test
	public void testSetStartOK() {
		final int gapStart = 10;
		final int gapDuration = 15;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + gapDuration);
		gap1.setStartTime(gapStart + gapDuration - 2);
		assertEquals(2, gap1.getGapDuration());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetStartNotOK() {
		final int gapStart = 10;
		final int gapDuration = 15;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + gapDuration);
		gap1.setStartTime(gapStart + gapDuration + 1);
	}

	@Test
	public void testSetEndOK() {
		final int gapStart = 10;
		final int gapDuration = 15;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + gapDuration);
		gap1.setEndTime(gapStart + 1);
		assertEquals(1, gap1.getGapDuration());
	}

	@Test
	public void testSetEndOK2() {
		final int gapStart = 10;
		final int gapDuration = 15;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + gapDuration);
		gap1.setEndTime(gapStart);
		assertEquals(0, gap1.getGapDuration());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetEndNotOK() {
		final int gapStart = 10;
		final int gapDuration = 15;
		SimpleDepartureGap gap1 = new SimpleDepartureGap(gapStart, gapStart + gapDuration);
		gap1.setEndTime(gapStart - 1);
	}
}
