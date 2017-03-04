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

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.GapUtils;

/**
 * 
 * @author ahamon
 *
 */
public class ArrivalGapTypeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testNone() {
		int gapStart = 1;
		int gapEnd = gapStart + Constants.ARR_DEP_MIN + Constants.DEP_ARR_MIN - 1;
		Gap gap = Mockito.mock(ArrivalGap.class);
		Mockito.when(gap.getStartTime()).thenReturn(gapStart);
		Mockito.when(gap.getEndTime()).thenReturn(gapEnd);
		Mockito.when(gap.getGapDuration()).thenReturn(Constants.ARR_DEP_MIN + Constants.DEP_ARR_MIN - 1);
		int nb = GapUtils.calculateNbDepartures(gap);
		assertEquals(0, nb);
	}

	@Test
	public void testSimple() {
		int gapStart = 1;
		int gapEnd = gapStart + Constants.SINGLE;
		Gap gap = Mockito.mock(ArrivalGap.class);
		Mockito.when(gap.getStartTime()).thenReturn(gapStart);
		Mockito.when(gap.getEndTime()).thenReturn(gapEnd);
		Mockito.when(gap.getGapDuration()).thenReturn(Constants.SINGLE);
		int nb = GapUtils.calculateNbDepartures(gap);
		assertEquals(1, nb);
	}

	@Test
	public void testDouble() {
		int gapStart = 1;
		int gapEnd = gapStart + Constants.DOUBLE;
		Gap gap = Mockito.mock(ArrivalGap.class);
		Mockito.when(gap.getStartTime()).thenReturn(gapStart);
		Mockito.when(gap.getEndTime()).thenReturn(gapEnd);
		Mockito.when(gap.getGapDuration()).thenReturn(Constants.DOUBLE);
		int nb = GapUtils.calculateNbDepartures(gap);
		assertEquals(2, nb);
	}

	@Test
	public void testTriple() {
		int gapStart = 1;
		int gapEnd = gapStart + Constants.TRIPLE;
		Gap gap = Mockito.mock(ArrivalGap.class);
		Mockito.when(gap.getStartTime()).thenReturn(gapStart);
		Mockito.when(gap.getEndTime()).thenReturn(gapEnd);
		Mockito.when(gap.getGapDuration()).thenReturn(Constants.TRIPLE);
		int nb = GapUtils.calculateNbDepartures(gap);
		assertEquals(3, nb);
	}

}
