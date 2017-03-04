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

package gov.nasa.arc.atc.geography;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class RouteTest {

	private static ATCNode A = new ATCNode("A", 1, 1, 1);
	private static ATCNode B = new ATCNode("B", 2, 2, 2);
	private static ATCNode C = new ATCNode("C", 3, 3, 3);
	private static ATCNode D = new ATCNode("D", 4, 4, 4);
	private static ATCNode E = new ATCNode("E", 5, 5, 5);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		Route large = new Route();
		large.addAtStart(A);
		large.addAtEnd(B);
		large.addAtEnd(C);
		large.addAtEnd(D);
		large.addAtEnd(E);
		//
		Route short1 = new Route();
		//
		Route short2 = new Route();
		short2.addAtStart(A);
		//
		Route short3 = new Route();
		short3.addAtStart(A);
		short3.addAtEnd(B);
		short3.addAtEnd(C);
		//
		Route short4 = new Route();
		short4.addAtStart(C);
		short4.addAtEnd(D);
		short4.addAtEnd(E);
		//
		Route cut = new Route();
		cut.addAtStart(A);
		cut.addAtEnd(D);
		cut.addAtEnd(E);
		//
		boolean short1ContainedInLarge = short1.isContained(large);
		boolean short2ContainedInLarge = short2.isContained(large);
		boolean short3ContainedInLarge = short3.isContained(large);
		boolean short4ContainedInLarge = short4.isContained(large);
		//
		assertTrue(short1ContainedInLarge);
		assertTrue(short2ContainedInLarge);
		assertTrue(short3ContainedInLarge);
		assertTrue(short4ContainedInLarge);
		//
		assertTrue(short1.isContained(short1));
		assertTrue(short1.isContained(short2));
		assertTrue(short1.isContained(short3));
		assertTrue(short1.isContained(short4));
		assertTrue(short1.isContained(large));
		assertTrue(short1.isContained(cut));
		//
		boolean cutContainedinLarge = cut.isContained(large);
		assertFalse(cutContainedinLarge);
		//
		boolean largeContainedShort1 = large.isContained(short1);
		boolean largeContainedShort2 = large.isContained(short2);
		boolean largeContainedShort3 = large.isContained(short3);
		boolean largeContainedShort4 = large.isContained(short4);
		boolean largeContainedCut = large.isContained(cut);
		assertFalse(largeContainedShort1);
		assertFalse(largeContainedShort2);
		assertFalse(largeContainedShort3);
		assertFalse(largeContainedShort4);
		assertFalse(largeContainedCut);
		
		
	}

}
