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

import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.geography.GeographyElementBounds;
import gov.nasa.arc.atc.geography.Region;
import gov.nasa.arc.atc.geography.Sector;

/**
 * 
 * @author ahamon
 *
 */
public class SectorUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test1Region() {
		Sector s1 = new Sector("S1", 0);
		Region r1_1 = new Region(0, 1000);
		r1_1.addVertex(41, -71);
		r1_1.addVertex(39, -71);
		r1_1.addVertex(41, -72);
		r1_1.addVertex(39, -71);
		s1.addRegion(r1_1);
		GeographyElementBounds bounds1 = SectorUtils.getSectorsBounds(s1);
		System.err.println("getMinLatitude  " + bounds1.getMinLatitude());
		System.err.println("getMaxLatitude  " + bounds1.getMaxLatitude());
		System.err.println("getMinLongitude " + bounds1.getMinLongitude());
		System.err.println("getMaxLongitude " + bounds1.getMaxLongitude());
		assertEquals(bounds1.getMinLatitude(), 39, 0.001);
		assertEquals(bounds1.getMaxLatitude(), 41, 0.001);
		assertEquals(bounds1.getMinLongitude(), -72, 0.001);
		assertEquals(bounds1.getMaxLongitude(), -71, 0.001);
	}

	@Test
	public void test3Regions() {
		Sector s1 = new Sector("S1", 0);
		//
		Region r1_1 = new Region(0, 1000);
		r1_1.addVertex(41, -71);
		r1_1.addVertex(39, -71);
		r1_1.addVertex(41, -72);
		r1_1.addVertex(39, -71);
		s1.addRegion(r1_1);
		//
		Region r1_2 = new Region(0, 1000);
		r1_2.addVertex(42, -71.5);
		r1_2.addVertex(38, -70);
		r1_2.addVertex(48, -72);
		r1_2.addVertex(40, -73);
		s1.addRegion(r1_2);
		//
		Region r1_3 = new Region(0, 1000);
		r1_3.addVertex(41.5, -71.5);
		r1_3.addVertex(39.5, -71);
		r1_3.addVertex(41, -72.5);
		r1_3.addVertex(34, -74);
		s1.addRegion(r1_3);
		GeographyElementBounds bounds1 = SectorUtils.getSectorsBounds(s1);
		assertEquals(bounds1.getMinLatitude(), 34, 0.001);
		assertEquals(bounds1.getMaxLatitude(), 48, 0.001);
		assertEquals(bounds1.getMinLongitude(), -74, 0.001);
		assertEquals(bounds1.getMaxLongitude(), -70, 0.001);
	}

}
