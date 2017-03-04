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

package gov.nasa.arc.atc.scenariogen.export;

import gov.nasa.arc.atc.scenariogen.core.Flow;
import gov.nasa.arc.atc.scenariogen.core.FlowType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class ScenarioXMLParser {

    private static final Logger LOG = Logger.getGlobal();

    private ScenarioXMLParser() {
        // private utility class
    }

    public static List<Flow> parseScenario(File file) {
        List<Flow> result = new LinkedList<>();
        if (file != null) {
            //
            Document document;
            DocumentBuilderFactory builderFactory;
            builderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                InputSource source = new InputSource(file.getAbsolutePath());
                document = builder.parse(source);
                Element e = document.getDocumentElement();
                // get flows
                parseFlows(e, result);
            } catch (IOException | SAXException | ParserConfigurationException ex) {
                LOG.log(Level.SEVERE, " {0}", ex);
            }

        }
        return Collections.unmodifiableList(result);
    }


    private static void parseFlows(Element rootElement, List<Flow> flows) {
        assert rootElement.getNodeName().equals(ScenarioXMLExporter.SCENARIO_ROOT_ELEMENT);
        NodeList flowElements = rootElement.getElementsByTagName(ScenarioXMLExporter.FLOW_ELEMENT);
        for (int i = 0; i < flowElements.getLength(); i++) {
            Element flowE = (Element) flowElements.item(i);
            flows.add(parseSingleFlow(flowE));
        }
    }

    private static Flow parseSingleFlow(Element flowE) {

        String runway = flowE.getAttribute(ScenarioXMLExporter.RUNWAY_ATTRIBUTE);
        String delivery = flowE.getAttribute(ScenarioXMLExporter.DELIVERY_ATTRIBUTE);
        FlowType type = FlowType.valueOf(flowE.getAttribute(ScenarioXMLExporter.TYPE_ATTRIBUTE));
        int nbQuarters = Integer.parseInt(flowE.getAttribute(ScenarioXMLExporter.NB_QUARTERS_ATTRIBUTE));
        int baseRate = Integer.parseInt(flowE.getAttribute(ScenarioXMLExporter.BASE_RATE_ATTRIBUTE));
        //
        Flow flow = new Flow();
        flow.setFlowType(type);
        flow.setBaseRate(baseRate);
        flow.setRunway(runway);
        flow.setDeliveryPoint(delivery);
        flow.setSimulationDuration(nbQuarters);
        return flow;
    }


    public static void main(String[] args) {
        String defaultOutputPath = "/Desktop/ScenarioGen/defaultScenario.xml";
        parseScenario(new File(defaultOutputPath));
    }
}
