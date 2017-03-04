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

import java.util.List;
import gov.nasa.arc.atc.geography.ATCGeography;

/**
 * 
 * @author ahamon
 *
 */
public interface GeographyDataBlock {

	// TODO: in the future, use NetBeans API for service provider!!
	// TODO: find a way to remove all the parameters in the parse method

	/**
	 * 
	 * @param nextLine the line to analyze
	 * @return if the line matches the datablock header
	 */
	boolean headerMatches(String nextLine);

	/**
	 * 
	 * @return the number of lines the data block has
	 */
	int getNbLines();

	/**
	 * 
	 * @param lines the lines in the log file
	 * @param firstLine the index of the data block's first line
	 * @param geography the ATCGeography to build
	 */
	void parseBlock(List<String> lines, int firstLine, ATCGeography geography);

}
