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

package gov.nasa.arc.atc.brahms.parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.brahms.BrahmsInstanceInitialConfiguration;
import static gov.nasa.arc.atc.brahms.BrahmsKeyWords.INIT_BELIEFS;
import static gov.nasa.arc.atc.brahms.BrahmsKeyWords.INIT_FACTS;
import static gov.nasa.arc.atc.brahms.BrahmsKeyWords.START_AGENT_PATTERN;
import static gov.nasa.arc.atc.brahms.BrahmsKeyWords.START_OBJECT_PATTERN;
import static gov.nasa.arc.atc.brahms.BrahmsKeyWords.END_PATTERN;
import javafx.util.Pair;

/**
 * 
 * @author ahamon
 *
 */
public class BrahmsAgentInitializationParser {

	private static final Logger LOG = Logger.getGlobal();

	private BrahmsAgentInitializationParser() {
		// private utility constructor
	}

	/**
	 * 
	 * @param file the Brahms file to parse
	 * @return the list of the {@link BrahmsInstanceInitialConfiguration} described in the file
	 */
	public static final List<BrahmsInstanceInitialConfiguration> parseBrahmsInstances(File file) {
		List<BrahmsInstanceInitialConfiguration> instances = new LinkedList<>();
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			parseBrahmsInstanceConfigurations(lines, instances);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception: {0}", e);
		}
		return Collections.unmodifiableList(instances);
	}

	/**
	 * Method used to parse initial beliefs/facts in Brahms files
	 * 
	 * @param line the line to parse
	 * @return a pair containing the attribute name and its value
	 */
	public static Pair<String, String> getAttributes(String line) {
		String lineContent = line.replace("\"", "").split("[\\(\\)]")[1].replaceAll(" ", "");
		String[] splitLineContent = lineContent.split("=");
		return new Pair<>(splitLineContent[0].replaceAll("current.", ""), splitLineContent[1]);
	}

	private static void parseBrahmsInstanceConfigurations(List<String> lines, List<BrahmsInstanceInitialConfiguration> instances) {
		final int nbLines = lines.size();
		int nextLineIndex = 0;
		String nextLine;
		while (nextLineIndex < nbLines) {
			nextLine = lines.get(nextLineIndex);
			if (nextLine.contains(START_OBJECT_PATTERN) || nextLine.contains(START_AGENT_PATTERN)) {
				nextLineIndex = parseBrahmsInstanceInitialConfiguration(lines, nextLine, nextLineIndex, instances);
			} else {
				nextLineIndex++;
			}
		}
	}

	private static int parseBrahmsInstanceInitialConfiguration(List<String> lines, final String firstLine, final int firstLineIndex, List<BrahmsInstanceInitialConfiguration> instances) {
		String nextLine = firstLine;
		int nextLineNb = firstLineIndex;
		boolean toBeliefs = true;
		// first line for name and class name
		String[] splitLine = nextLine.replaceAll("\\s+", " ").split(" ");
		String instanceName = splitLine[1];
		String instanceClassName = splitLine[3];
		//
		final Map<String, String> initBeliefs = new HashMap<>();
		final Map<String, String> initFacts = new HashMap<>();
		// hum , not necessarily nice, normaly will not generate errors if the Brahms file is "Brahms" parsable
		while (true) {
			nextLineNb++;
			nextLine = lines.get(nextLineNb);
			if (nextLine.contains(INIT_BELIEFS)) {
				toBeliefs = true;
			} else if (nextLine.contains(INIT_FACTS)) {
				toBeliefs = false;
			} else if (nextLine.contains(END_PATTERN)) {
				break;
			} else {
				Pair<String, String> content = getAttributes(nextLine);
				if (toBeliefs) {
					initBeliefs.put(content.getKey(), content.getValue());
				} else {
					initFacts.put(content.getKey(), content.getValue());
				}
			}
		}
		// create instance configuration
		BrahmsInstanceInitialConfiguration instance = new BrahmsInstanceInitialConfiguration(instanceName, instanceClassName, initBeliefs, initFacts);
		// add the instance configuration to the list
		instances.add(instance);
		//
		return nextLineNb + 1;
	}

}
