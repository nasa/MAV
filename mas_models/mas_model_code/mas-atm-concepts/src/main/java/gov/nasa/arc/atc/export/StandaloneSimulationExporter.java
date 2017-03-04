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

package gov.nasa.arc.atc.export;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.AfoUpdateFactory;
import gov.nasa.arc.atc.AfoUpdateImpl;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.simulation.TimedFlightParameters;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * @author ahamon
 */
public final class StandaloneSimulationExporter {

    // TODO: put the constants in the right place
    public static final String ROOT_ELEMENT = "SIMULATION_RESULT";
    public static final String ARRIVALS_GROUP_ELEMENT = "ARRIVALS";
    //
    public static final String ARRIVAL_ELEMENT = "Arrival";
    public static final String ARRIVAL_ATTRIBUTE = "arrivesAt";
    //
    public static final String NO_ATCONTROLLER = "no Controller";

    // TODO change this
    public static final String SLOT_AGENT_PREFIX = "slot_";

    private StandaloneSimulationExporter() {
        // private utility constructor
    }

    /**
     * @param context      the context containing the slots updated by the standalone Java simulation
     * @param file         the output file to save the simulation to
     * @param nbSteps
     * @param stepDuration
     * @return true if the export was OK
     */
    public static boolean saveStandAloneSimulationRun(SimulationContext context, File file, final int nbSteps, final int stepDuration) {
        ConsoleUtils.setLoggingLevel(Level.FINE);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(ROOT_ELEMENT);
            doc.appendChild(rootElement);
            // Create arrival slots
            createArrivalSlots(doc, rootElement, context.getAllArrivalTrajectories(), nbSteps, stepDuration);
            // TODO: departures
            //
            rootElement.normalize();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result;
            if (file.getName().endsWith(".xml")) {
                result = new StreamResult(file);
            } else {
                File correctFile = new File(file.getAbsolutePath() + ".xml");
                result = new StreamResult(correctFile);
            }
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getGlobal().log(Level.SEVERE, "export NOT ok >>> : Ex: {0}", ex);
            return false;
        }
        Logger.getGlobal().log(Level.INFO, "Export OK for file {0}", file);
        return true;
    }

    private static void createArrivalSlots(Document doc, Element rootElement, List<SimulatedTrajectory> arrivalSlotTrajectories, final int nbSteps, final int stepDuration) {
        Element arrivalTrajectoriesGroupElement = doc.createElement(ARRIVALS_GROUP_ELEMENT);
        arrivalSlotTrajectories.forEach(trajectory -> createArrivalTrajectory(doc, arrivalTrajectoriesGroupElement, trajectory, nbSteps, stepDuration));
        rootElement.appendChild(arrivalTrajectoriesGroupElement);
    }

    private static void createArrivalTrajectory(Document doc, Element arrivalTrajectoriesGroupElement, SimulatedTrajectory trajectory, final int nbSteps, final int stepDuration) {
        Element arrivalElement = doc.createElement(ARRIVAL_ELEMENT);
        // TODO: save flight plan
        arrivalElement.setAttribute(XMLConstants.NAME_ATTRIBUTE, XMLConstants.SLOT_PREFIX + trajectory.getSlotMarker().getName());

        arrivalElement.setAttribute(ARRIVAL_ATTRIBUTE, trajectory.getSlotMarker().getFlightPlan().getArrivalRunway().getName());
        //
        // hum 0?
        final int maxTime = Math.min(nbSteps * stepDuration, trajectory.getArrivalTime());
        for (int i = 0; i < maxTime; i++) {
            if (i % stepDuration == 0) {
                arrivalElement.appendChild(AfoUpdateFactory.createXML(doc, toAfoUpdate(trajectory, trajectory.getParametersAtSimulationTime(i))));
            }
        }
        arrivalTrajectoriesGroupElement.appendChild(arrivalElement);
    }

    private static AfoUpdate toAfoUpdate(SimulatedTrajectory trajectory, TimedFlightParameters p) {
        return new AfoUpdateImpl(SLOT_AGENT_PREFIX + trajectory.getSlotMarker().getName(), p.getTimeStamp(), p.getPosition(), p.getAirSpeed(), p.getVerticalSpeed(), p.getHeading(), trajectory.getSlotMarker().getFlightPlan().getSegmentIndexEndingAt(p.getToWPT()), p.getToWPT(), p.getStatus(),
                trajectory.getSlotMarker().getStartTime(), trajectory.getETAAtSimulationTime(p.getTimeStamp()) - p.getTimeStamp(), NO_ATCONTROLLER, 0);
    }

}
