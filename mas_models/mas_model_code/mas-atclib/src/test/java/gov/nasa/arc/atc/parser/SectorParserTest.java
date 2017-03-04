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

package gov.nasa.arc.atc.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.scenarios.TestData;
import org.junit.Ignore;
import org.junit.Test;

import gov.nasa.arc.atc.geography.ATCArea;
import gov.nasa.arc.atc.parsers.SectorParser;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * 
 * @author ahamon
 *
 */
public class SectorParserTest {

	private static final Logger LOG = Logger.getGlobal();

	@Ignore
	@Test
	public void test() {
		ConsoleUtils.setLoggingLevel(Level.INFO);
		File file = new File(TestData.class.getResource("nas_sector_boundaries_22_31").getPath());
		ATCArea result = SectorParser.parseATCArea(file);
		LOG.log(Level.INFO, " found {0} sectors", result.getSectors().size());
		assertEquals(19, result.getSectors().size());
	}

}
