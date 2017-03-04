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

package gov.nasa.arc.atc.parsing.xml.queue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLMaster {

	private static final Logger LOG = Logger.getGlobal();

	private static int uniqueID = 0;

	private static final Map<Integer, File> FILES = new HashMap<>();
	private static final Map<Integer, XMLParserQueued> PARSERS = new HashMap<>();
	private static final Map<Integer, Object> REQUESTORS = new HashMap<>();
	private static final Map<Integer, Object> RESULTS = new HashMap<>();

	private XMLMaster() {
		// private utility class
	}

	public static Object requestParsing(File fileToParse, XMLParserQueued parser, Object requestor) {
		uniqueID++;
		FILES.put(uniqueID, fileToParse);
		PARSERS.put(uniqueID, parser);
		REQUESTORS.put(uniqueID, requestor);

		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Object result = parser.parseFile(fileToParse, builder);
			RESULTS.put(uniqueID, result);
			LOG.log(Level.WARNING, "end parsing for {0}", new Object[] { result });
			return result;
		} catch (ParserConfigurationException e) {
			LOG.log(Level.SEVERE, "Exception while parsing :: {0}", new Object[] { e });
		}
		return "toto";
	}

}
