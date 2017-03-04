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

import javafx.stage.FileChooser;

/**
 * 
 * @author ahamon
 *
 */
public class FileUtils {

	/**
	 * XML files extension
	 */
	public static final FileChooser.ExtensionFilter EX_XML = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");

	/**
	 * LOG files extensions
	 */
	public static final FileChooser.ExtensionFilter EX_LOG = new FileChooser.ExtensionFilter("LOG files (*.log)", "*.log", "*.txt", "*");

	/**
	 * TRAC Geography files extensions
	 */
	public static final FileChooser.ExtensionFilter EX_TRAC = new FileChooser.ExtensionFilter("TRAC files (*)", "*", "*.*");

	/**
	 * Property file extension
	 */
	public static final FileChooser.ExtensionFilter EX_PROPERTIES = new FileChooser.ExtensionFilter("Properties files (*.properties)", "*.properties");

	/**
	 * PNG file extension
	 */
	public static final FileChooser.ExtensionFilter EX_PNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");

	private FileUtils() {
		// private utility constuctor
	}
}
