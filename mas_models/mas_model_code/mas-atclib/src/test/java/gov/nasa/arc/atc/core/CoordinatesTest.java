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

package gov.nasa.arc.atc.core;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author ahamon
 *
 */
public class CoordinatesTest {

	private static final double EPSILON = 0.0000001;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		final double lat1 = 13.6;
		final double long1 = 45;
		Coordinates c1 = new Coordinates(lat1, long1);
		assertEquals(lat1, c1.getLatitude(), EPSILON);
		assertEquals(long1, c1.getLongitude(), EPSILON);
		assertNotNull(c1.toString());
	}

	@Ignore
	@Test(expected = IllegalArgumentException.class)
	public void testFailLat1() {
		final double lat1 = -91;
		final double long1 = 45;
		Coordinates c1 = new Coordinates(lat1, long1);
		fail(c1.toString());
	}

	@Ignore
	@Test(expected = IllegalArgumentException.class)
	public void testFailLat2() {
		final double lat1 = 91;
		final double long1 = 45;
		Coordinates c1 = new Coordinates(lat1, long1);
		fail(c1.toString());
	}

	@Ignore
	@Test(expected = IllegalArgumentException.class)
	public void testFailLong1() {
		final double lat1 = 23;
		final double long1 = 180.1;
		Coordinates c1 = new Coordinates(lat1, long1);
		fail(c1.toString());
	}

	@Ignore
	@Test(expected = IllegalArgumentException.class)
	public void testFailLong2() {
		final double lat1 = 23;
		final double long1 = -180.1;
		Coordinates c1 = new Coordinates(lat1, long1);
		fail(c1.toString());
	}

}
