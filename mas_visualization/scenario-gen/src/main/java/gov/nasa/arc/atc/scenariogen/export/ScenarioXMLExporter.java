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


import gov.nasa.arc.atc.scenariogen.base.FlighPlanDataExporter;
import gov.nasa.arc.atc.scenariogen.core.Flow;
import gov.nasa.arc.atc.scenariogen.core.ScheduledFlightPlan;
import gov.nasa.arc.atc.scenariogen.core.SubFlow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class ScenarioXMLExporter {


    static final String SCENARIO_ROOT_ELEMENT = "SCENARIO";
    static final String FLOW_ELEMENT = "FLOW";
    static final String SUBFLOW_ELEMENT = "SubFlow";
    static final String SCHEDULED_FLIGHT_PLAN_ELEMENT = "ScheduledFlihtPlan";


    // flow attributes
    static final String RUNWAY_ATTRIBUTE = "runway";
    static final String DELIVERY_ATTRIBUTE = "delivery";
    static final String DELIVERY_TIME_ATTRIBUTE = "deliveryTime";
    static final String TYPE_ATTRIBUTE = "type";
    static final String NB_QUARTERS_ATTRIBUTE = "nbQuarters";
    static final String BASE_RATE_ATTRIBUTE = "baseRate";

    // sub flow attributes
    static final String NAME_ATTRIBUTE = "name";
    static final String ID_ATTRIBUTE = "id";
    static final String VALUE_ATTRIBUTE = "value";
    static final String COLOR_ATTRIBUTE = "color";

    private static final Logger LOG = Logger.getGlobal();

    private ScenarioXMLExporter() {
        // private utility constructor
    }

    public static boolean exportScenarioConfiguration(List<Flow> flows, File outputFile, boolean withGeneratedScenario) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(SCENARIO_ROOT_ELEMENT);
            doc.appendChild(rootElement);
            // Create flows
            flows.forEach(flow -> createFlow(doc, rootElement, flow, withGeneratedScenario));
            //
            rootElement.normalize();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result;
            if (outputFile.getName().endsWith(".xml")) {
                result = new StreamResult(outputFile);
            } else {
                File correctFile = new File(outputFile.getAbsolutePath() + ".xml");
                result = new StreamResult(correctFile);
            }
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            LOG.log(Level.SEVERE, " Exception while exporting atc geography :: {0}", ex);
            return false;
        }
        return true;
    }

    private static void createFlow(Document doc, Element parentElement, Flow flow, boolean withGeneratedScenario) {
        Element flowElement = doc.createElement(FLOW_ELEMENT);
        flowElement.setAttribute(RUNWAY_ATTRIBUTE, flow.getRunway());
        flowElement.setAttribute(DELIVERY_ATTRIBUTE, flow.getDeliveryPoint());
        flowElement.setAttribute(TYPE_ATTRIBUTE, flow.getFlowType().name());
        flowElement.setAttribute(NB_QUARTERS_ATTRIBUTE, Integer.toString(flow.getNbQuarters()));
        flowElement.setAttribute(BASE_RATE_ATTRIBUTE, Integer.toString(flow.getBaseRate()));
        flow.getSubFlows().forEach(subFlow -> createSubFlow(doc, flowElement, subFlow, withGeneratedScenario));
        parentElement.appendChild(flowElement);
    }

    private static void createSubFlow(Document doc, Element flowElement, SubFlow subFlow, boolean withGeneratedScenario) {
        Element subFlowElement = doc.createElement(SUBFLOW_ELEMENT);
        subFlowElement.setAttribute(NAME_ATTRIBUTE, subFlow.getName());
        subFlowElement.setAttribute(COLOR_ATTRIBUTE, subFlow.getColor().toString());
        SubFLowSelectionFunctionXMLVisitor.createXML(doc, subFlowElement, subFlow.getIdentifierFunction());
        for (int i = 0; i < subFlow.getNbQuarters(); i++) {
            Element quarterElement = doc.createElement("quarter");
            quarterElement.setAttribute(ID_ATTRIBUTE, Integer.toString(i));
            quarterElement.setAttribute(VALUE_ATTRIBUTE, Integer.toString(subFlow.getRateAtQuarter(i)));
            subFlowElement.appendChild(quarterElement);
        }
        if (withGeneratedScenario) {
            subFlow.getGeneratedFlightPlans().forEach(fpl -> createScheduledFlightPlan(doc, subFlowElement, fpl));
        }
        flowElement.appendChild(subFlowElement);
    }

    private static void createScheduledFlightPlan(Document doc, Element subFlowElement, ScheduledFlightPlan scheduledFlightPlan) {
        Element scheduledFPLElement = doc.createElement(SCHEDULED_FLIGHT_PLAN_ELEMENT);
        scheduledFPLElement.setAttribute(NAME_ATTRIBUTE, scheduledFlightPlan.getName());
        scheduledFPLElement.setAttribute(TYPE_ATTRIBUTE, scheduledFlightPlan.getDeliveryNodeFlowType().name());
        scheduledFPLElement.setAttribute(DELIVERY_TIME_ATTRIBUTE, Integer.toString(scheduledFlightPlan.getTimeAtDeliveryWPT()));
        FlighPlanDataExporter.createRichFlightPlan(doc, subFlowElement, scheduledFlightPlan.getFlightPlan());
        subFlowElement.appendChild(scheduledFPLElement);
    }


}
