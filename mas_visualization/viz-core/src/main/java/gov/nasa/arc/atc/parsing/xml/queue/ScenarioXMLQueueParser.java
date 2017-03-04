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

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.AfoUpdateFactory;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.export.StandaloneSimulationExporter;

/**
 * 
 * @author ahamon
 *
 */
public class ScenarioXMLQueueParser implements XMLParserQueued {

	private static final Logger LOG = Logger.getGlobal();

	private static final String SLOT_UPDATE_TYPE = "slot-update";

	/**
	 * 
	 * @param scenarioFile the xml file to parse
	 * @return a {@link DataModel} instance
	 */
	public DataModel parseFile(File scenarioFile) {
		DocumentBuilderFactory builderFactory;
		builderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			return parseFile(scenarioFile, docBuilder);
		} catch (ParserConfigurationException e) {
			LOG.log(Level.SEVERE, "Error while creating document builder {0}", e);
		}
		return null;
	}

	@Override
	public DataModel parseFile(File scenarioFile, DocumentBuilder docBuilder) {
		if (scenarioFile != null) {
			DataModelInput dataInputs = new DataModelInput();
			//
			Document document;
			try {
				InputSource source = new InputSource(scenarioFile.getAbsolutePath());
				document = docBuilder.parse(source);
				Element e = document.getDocumentElement();
				//
				parseArrivals(e, dataInputs);
				//
				dataInputs.lock();
				return new DataModel(dataInputs);
			} catch (IOException | SAXException ex) {
				Logger.getGlobal().log(Level.SEVERE, " {0}", ex);
			}
		}
		return null;
	}

	/*
	 * ARRIVALS SLOTS
	 */
	private void parseArrivals(Element e, DataModelInput dataModelInput) {
		NodeList nodeList = e.getElementsByTagName(StandaloneSimulationExporter.ARRIVAL_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			parseSingleArrival((Element) nodeList.item(i), dataModelInput);
		}
	}

	private void parseSingleArrival(Element arrivalElement, DataModelInput dataModelInput) {
		NodeList nodeList = arrivalElement.getElementsByTagName(AfoUpdateFactory.AFO_UPDATE_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			AfoUpdate update = AfoUpdateFactory.parseElement((Element) nodeList.item(i));
			dataModelInput.addAFOUpdate(update.getAfoName(),SLOT_UPDATE_TYPE,update);
		}

	}

}
