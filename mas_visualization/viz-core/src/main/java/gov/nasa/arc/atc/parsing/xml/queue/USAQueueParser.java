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
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nasa.arc.atc.geography.Region;

/**
 * 
 * @author ahamon
 *
 */
public class USAQueueParser implements XMLParserQueued {

	public static final String STATE_ELEMENT = "state";
	public static final String POINT_ELEMENT = "point";

	public static final String NAME_ATTRIBUTE = "name";
	public static final String LATITUDE_ATTRIBUTE = "lat";
	public static final String LONGITUDE_ATTRIBUTE = "lng";

	private static final Logger LOG = Logger.getGlobal();

	/**
	 * 
	 * @param usaStatesFile xml the file to parse
	 * @return the list of USA States as regions
	 */
	public List<Region> parseFile(File usaStatesFile) {
		DocumentBuilderFactory builderFactory;
		builderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			return parseFile(usaStatesFile, docBuilder);
		} catch (ParserConfigurationException e) {
			LOG.log(Level.SEVERE, "Error while creating document builder {0}", e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<Region> parseFile(File usaStatesFile, DocumentBuilder docBuilder) {
		if (usaStatesFile != null) {
			List<Region> states = new LinkedList<>();
			//
			Document document;
			try {
				InputSource source = new InputSource(usaStatesFile.getAbsolutePath());
				document = docBuilder.parse(source);
				Element e = document.getDocumentElement();
				// get states
				parseStates(e, states);
				//
				return Collections.unmodifiableList(states);
			} catch (IOException | SAXException ex) {
				Logger.getGlobal().log(Level.SEVERE, " {0}", ex);
			}
		}
		return Collections.emptyList();
	}

	/*
	 * WAYPOINTS
	 */

	private void parseStates(Element e, List<Region> states) {
		NodeList nodeList = e.getElementsByTagName(STATE_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			Region state = parseSingleState(newE);
			states.add(state);
		}
	}

	private Region parseSingleState(Element e) {
		final String stateName = e.getAttribute(NAME_ATTRIBUTE);
		Region state = new Region(0, 0, stateName);
		NodeList nodeList = e.getElementsByTagName(POINT_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element newE = (Element) nodeList.item(i);
			state.addVertex(Double.parseDouble(newE.getAttribute(LATITUDE_ATTRIBUTE)), Double.parseDouble(newE.getAttribute(LONGITUDE_ATTRIBUTE)));
		}
		return state;
	}

}
