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

import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.algos.AlgorithmFactory;
import gov.nasa.arc.atc.functions.MeterFixCandidateFunction;
import gov.nasa.arc.atc.functions.SimpleGapScheludingFunction;

/**
 * 
 * @author ahamon
 *
 */
public class ParametricDSASTest {

	private static final int MAX_CANDIDATE_TIME = 700;

	@BeforeClass
	public static void setUpBeforeClass() {
		DSASTestUtils utils = new DSASTestUtils();
		System.err.println(" utils created ::" + utils);
	}

	@Test
	public void test1() {
		DSASTestUtils.test1((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	@Test
	public void test1Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test1(dsas);
	}

	@Test
	public void test2() {
		// schedule a departure in a single gap (gap being created)
		// and a second one after the gap
		DSASTestUtils.test2((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	@Test
	public void test2Meter() {
		// schedule a departure in a single gap (gap being created)
		// and a second one after the gap
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test2(dsas);
	}

	/**
	 * schedule 2 departures in a double gap (gap being created)
	 */
	@Test
	public void test3() {
		DSASTestUtils.test3((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	/**
	 * schedule 2 departures in a double gap (gap being created)
	 */
	@Test
	public void test3Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test3(dsas);
	}

	/**
	 * schedule 3 departures in a triple gap (gap being created)
	 */
	@Test
	public void test4() {
		DSASTestUtils.test4((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	/**
	 * schedule 3 departures in a triple gap (gap being created)
	 */
	@Test
	public void test4Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test4(dsas);
	}

	/**
	 * schedule 4 departures in a double and single gap and after (gaps being created)
	 */
	@Test
	public void test5() {
		DSASTestUtils.test5((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	/**
	 * schedule 4 departures in a double and single gap and after (gaps being created)
	 */
	@Test
	public void test5Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test5(dsas);
	}

	@Test
	public void test6() {
		DSASTestUtils.test6((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	@Test
	public void test6Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test6(dsas);
	}

	@Test
	public void test7() {
		DSASTestUtils.test7((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	@Test
	public void test7Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test7(dsas);
	}

	/**
	 * / one arrival, departures after
	 */
	@Test
	public void test8() {
		DSASTestUtils.test8((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	/**
	 * / one arrival, departures after
	 */
	@Test
	public void test8Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test8(dsas);
	}

	/**
	 * / two departures before an arrival, on departures after
	 */
	@Test
	public void test9() {
		DSASTestUtils.test9((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	/**
	 * / two departures before an arrival, on departures after
	 */
	@Test
	public void test9Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test9(dsas);
	}

	/**
	 * / six departures in a very large gap
	 */
	@Test
	public void test10() {
		DSASTestUtils.test10((DSAS) AlgorithmFactory.createDefaultParameteredDSAS());
	}

	/**
	 * / six departures in a very large gap
	 */
	@Test
	public void test10Meter() {
		MeterFixCandidateFunction candidateFunction = new MeterFixCandidateFunction(DSASTestUtils.getSimpleGeography(), MAX_CANDIDATE_TIME);
		ParametricDSAS dsas = new ParametricDSAS(candidateFunction, (traj, time) -> true, new SimpleGapScheludingFunction());
		DSASTestUtils.test10(dsas);
	}

}
