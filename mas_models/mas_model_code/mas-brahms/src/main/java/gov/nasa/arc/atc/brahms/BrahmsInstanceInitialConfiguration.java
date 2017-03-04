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

package gov.nasa.arc.atc.brahms;

import java.util.Collections;
import java.util.Map;

/**
 * This class aims at storing the initial facts and beliefs of a parsed Brahms agent
 * 
 * @author ahamon
 *
 */
public class BrahmsInstanceInitialConfiguration {

	private final String name;
	private final String className;
	private final Map<String, String> initialBeliefs;
	private final Map<String, String> initialFacts;

	/**
	 * @param name
	 * @param className
	 * @param initialBeliefs
	 * @param initialFacts
	 */
	public BrahmsInstanceInitialConfiguration(String name, String className, Map<String, String> initialBeliefs, Map<String, String> initialFacts) {
		this.name = name;
		this.className = className;
		this.initialBeliefs = initialBeliefs;
		this.initialFacts = initialFacts;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	public Map<String, String> getInitialBeliefs() {
		return Collections.unmodifiableMap(initialBeliefs);
	}

	public Map<String, String> getInitialFacts() {
		return Collections.unmodifiableMap(initialFacts);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(className). append(": ").append(name);
		sb.append("\n beliefs : ").append(initialBeliefs);
		sb.append("\n facts   : ").append(initialFacts);
		return sb.toString();
	}

}
