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

import gov.nasa.arc.atc.physics.*;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class WindsExporter {

    public static final String WIND_GROUP_ELEMENT = "WINDS";
    public static final String WIND_ELEMENT = "Wind";
    public static final String LAYER_ELEMENT = "Layer";
    public static final String WIND_PARAMETERS_ELEMENT = "WindParameters";

    public static final String TYPE_ATTRIBUTE = "type";
    public static final String NATURE_ATTRIBUTE = "nature";
    public static final String MINIMUM_ALTITUDE_ATTRIBUTE = "minAlt";
    public static final String MAXIMUM_ALTITUDE_ATTRIBUTE = "maxAlt";
    public static final String DIRECTION_ATTRIBUTE = "dir";
    public static final String SPEED_ATTRIBUTE = "speed";

    private static final Logger LOG = Logger.getGlobal();

    private WindsExporter() {
        // private utility constructor
    }

    public static boolean exportWinds(Winds winds, File destFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(WIND_GROUP_ELEMENT);
            doc.appendChild(rootElement);
            // Export data
            exportWindData(doc, rootElement, winds.getPredictedWind(), WindType.FORECAST);
            exportWindData(doc, rootElement, winds.getRealWind(), WindType.REAL);
            //
            rootElement.normalize();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result;
            if (destFile.getName().endsWith(".xml")) {
                result = new StreamResult(destFile);
            } else {
                String correctFilePath = destFile.getAbsolutePath() + ".xml";
                File correctFile = new File(correctFilePath);
                result = new StreamResult(correctFile);
            }
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            LOG.log(Level.SEVERE, " Exception while exporting atc geography :: {0}", ex);
            return false;
        }
        return true;
    }

    protected static void exportWindData(Document doc, Element parentElement, WindData windData, WindType windType) {
        Element windDataElement = doc.createElement(WIND_ELEMENT);
        windDataElement.setAttribute(TYPE_ATTRIBUTE, windData.getClass().getSimpleName());
        windDataElement.setAttribute(NATURE_ATTRIBUTE, windType.name());
        windData.getLayers().forEach(windLayer -> exportLayer(doc, windDataElement, windLayer));


        parentElement.appendChild(windDataElement);
    }

    private static void exportLayer(Document doc, Element windDataElement, WindLayer windLayer) {
        // only handles simple layers
        if (windLayer instanceof SimpleWindLayer) {
            exportSimpleWindLayer(doc, windDataElement, (SimpleWindLayer) windLayer);
        } else {
            throw new UnsupportedOperationException("Cannot export wind layer " + windLayer);
        }
    }

    private static void exportSimpleWindLayer(Document doc, Element parentElement, SimpleWindLayer layer) {
        Element layerElement = doc.createElement(LAYER_ELEMENT);
        layerElement.setAttribute(MINIMUM_ALTITUDE_ATTRIBUTE,Integer.toString(layer.getMinimumAltitude()));
        layerElement.setAttribute(MAXIMUM_ALTITUDE_ATTRIBUTE,Integer.toString(layer.getMaximumAltitude()));
        //
        Element windParametersElement = doc.createElement(WIND_PARAMETERS_ELEMENT);
        windParametersElement.setAttribute(DIRECTION_ATTRIBUTE,Double.toString(layer.getWindParameters().getDirection()));
        windParametersElement.setAttribute(SPEED_ATTRIBUTE,Double.toString(layer.getWindParameters().getSpeed()));
        //
        layerElement.appendChild(windParametersElement);
        parentElement.appendChild(layerElement);
    }
}
