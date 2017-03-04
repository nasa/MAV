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

package gov.nasa.arc.atc.parsing.trac;

import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.parsing.DataBlocks;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ahamon
 *
 */
public class TracGeographyFileParser {

	private static final Logger LOG = Logger.getGlobal();

	private TracGeographyFileParser() {
		// private contructor for utility class
	}

	public static ATCGeography parseTracFile(File file) {
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
            ATCGeography atcGeography;
            try (BufferedReader br = new BufferedReader(fileReader)) {
                List<String> lines = Files.readAllLines(file.toPath());
                //
                atcGeography = new ATCGeography(file.getName());
                //
                parseLines(lines, atcGeography);
                //
            }
            fileReader.close();
			//
			return atcGeography;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception: {0}", e);
		}
		return null;
	}

	private static void parseLines(List<String> lines, ATCGeography geography) {
		final int nbLines = lines.size();
		int nbLineToParse = 0;
		String nextLineToParse;
		List<GeographyDataBlock> blocks = DataBlocks.getGeographyDataBlocks();
		//
		while (nbLineToParse < nbLines) {
			nextLineToParse = lines.get(nbLineToParse);
			// try to find a matching data block
			for (GeographyDataBlock block : blocks) {
				if (block.headerMatches(nextLineToParse)) {
					block.parseBlock(lines, nbLineToParse, geography);
					break;
				}
			}
			nbLineToParse++;
		}
	}



}
