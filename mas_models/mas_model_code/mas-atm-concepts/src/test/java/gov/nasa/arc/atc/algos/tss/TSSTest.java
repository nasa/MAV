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

package gov.nasa.arc.atc.algos.tss;

import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.nasa.arc.atc.functions.DistanceSeparationFunction;
import gov.nasa.arc.atc.functions.TimeSeparationFunction;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * 
 * @author ahamon
 *
 */
public class TSSTest {

	@BeforeClass
	public static void setUp() {
		ConsoleUtils.setLoggingLevel(Level.OFF);
	}

	@Ignore
	@Test
	public void testDistance1() {
		TSS tss = new TSS(new DistanceSeparationFunction(TSSTestUtils.AIRPORT));
		TSSTestUtils.testDistance1(tss);
	}

	@Ignore
	@Test
	public void testTime1() {
		int timeSeparation1 = 75;
		TSS tss1 = new TSS(new TimeSeparationFunction(timeSeparation1));
		TSSTestUtils.testTime1(tss1, timeSeparation1);
		//
		int timeSeparation2 = 51;
		TSS tss2 = new TSS(new TimeSeparationFunction(timeSeparation2));
		TSSTestUtils.testTime1(tss2, timeSeparation2);

	}

}
