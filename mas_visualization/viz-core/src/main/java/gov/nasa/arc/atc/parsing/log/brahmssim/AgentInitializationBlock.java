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

package gov.nasa.arc.atc.parsing.log.brahmssim;

import java.util.List;
import java.util.Properties;

import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.parsing.log.LogDataBlock;

/**
 * INFO : Starting engine for 'gov.nasa.arc.atm.atmmodel.scenarios.small2plane2depart.agents.ZNY_28'
 * 
 * @author ahamon
 *
 */
public class AgentInitializationBlock implements LogDataBlock {

	private static final String AGENT_PATTERN_PROPERTY = "agentPattern";
	private static final String START_AGENT_PROPERTY = "startAgentProperty";

	// INFO : Starting engine for 'gov.nasa.arc.atm.atmmodel.scenarios.small2plane2depart.agents.ZNY_28'

	private final String agentPattern;
	private final String startAgent;

	/**
	 * 
	 * @param properties the properties to configure the block parsing and matching pattern
	 */
	public AgentInitializationBlock(Properties properties) {
		agentPattern = properties.getProperty(AGENT_PATTERN_PROPERTY);
		startAgent = properties.getProperty(START_AGENT_PROPERTY);
	}

	@Override
	public void parseBlock(List<String> lines, int firstLine, DataModelInput inputs) {
		String line = lines.get(firstLine);
		if (line.contains(agentPattern)) {
			parseAgent(line, inputs);
		}
	}

	@Override
	public boolean headerMatches(String nextLine) {
		return nextLine.contains(startAgent);
	}

	@Override
	public int getNbLines() {
		return 1;
	}

	private void parseAgent(String line, DataModelInput inputs) {
		String[] splitLine = line.split("'");
		String fullAgentName = splitLine[splitLine.length - 1];
		String[] agentNameSplit = fullAgentName.split("\\.");
		String agentName = agentNameSplit[agentNameSplit.length - 1];
		//temp TODO: log the agent type at initialization
//        inputs.addAgentUpdates(agentName, new HashMap<>());
	}

}
