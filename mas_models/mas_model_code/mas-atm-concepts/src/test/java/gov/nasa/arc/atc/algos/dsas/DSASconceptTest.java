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

import gov.nasa.arc.atc.utils.ConsoleUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ahamon
 *
 */
public class DSASconceptTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		ConsoleUtils.setLoggingLevel(Level.INFO);
		DSASTestUtils utils = new DSASTestUtils();
        Logger.getGlobal().log(Level.INFO,"Set up DSASTestUtils done: {0}",utils);
	}

	@Test
	public void test1() {
		DSASTestUtils.test1(new DSASConcept());
	}

	@Test
	public void test2() {
		// schedule a departure in a single gap (gap being created)
		// and a second one after the gap
		DSASTestUtils.test2(new DSASConcept());
	}

	/**
	 * schedule 2 departures in a double gap (gap being created)
	 */
	@Test
	public void test3() {
		DSASTestUtils.test3(new DSASConcept());
	}

	/**
	 * schedule 3 departures in a triple gap (gap being created)
	 */
	@Test
	public void test4() {
		DSASTestUtils.test4(new DSASConcept());
	}

	/**
	 * schedule 4 departures in a double and single gap and after (gaps being created)
	 */
	@Test
	public void test5() {
		DSASTestUtils.test5(new DSASConcept());
	}

	@Test
	public void test6() {
		DSASTestUtils.test6(new DSASConcept());
	}

	@Test
	public void test7() {
		DSASTestUtils.test7(new DSASConcept());
	}

	/**
	 * / one arrival, departures after
	 */
	@Test
	public void test8() {
		DSASTestUtils.test8(new DSASConcept());
	}

	/**
	 * / two departures before an arrival, on departures after
	 */
	@Test
	public void test9() {
		DSASTestUtils.test9(new DSASConcept());
	}

	/**
	 * / six departures in a very large gap
	 */
	@Test
	public void test10() {
		DSASTestUtils.test10(new DSASConcept());
	}

}
