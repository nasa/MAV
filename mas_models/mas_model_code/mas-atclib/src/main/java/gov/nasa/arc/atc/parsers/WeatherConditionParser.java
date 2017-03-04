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

package gov.nasa.arc.atc.parsers;

import gov.nasa.arc.atc.export.WeatherConditionsExporter;
import gov.nasa.arc.atc.parsers.macs.WindsParser;
import gov.nasa.arc.atc.physics.WeatherConditions;
import gov.nasa.arc.atc.physics.Winds;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class WeatherConditionParser {

    private static final Logger LOG = Logger.getGlobal();

    private WeatherConditionParser() {
        // private utility constructor
    }

    public static WeatherConditions parseWeatherConditions(String path) {

        System.err.println("  $$ parsing parseWeatherConditions");
        if (path == null) {
            return null;
        }
        File weatherFile = new File(path);
        return weatherFile.exists() ? parseWeatherConditions(weatherFile) : null;
    }


    public static WeatherConditions parseWeatherConditions(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        Document document;
        DocumentBuilderFactory builderFactory;
        builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            InputSource source = new InputSource(file.getAbsolutePath());
            document = builder.parse(source);
            Element rootElement = document.getDocumentElement();
            // get wind
            Winds winds = parseWind(file, rootElement);
            //
            return new WeatherConditions(winds);
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            LOG.log(Level.SEVERE, " {0}", ex);
        }
        return null;
    }

    private static Winds parseWind(File weatherFile, Element rootElement) {
        // at the moment only one wind file
        NodeList windElements = rootElement.getElementsByTagName(WeatherConditionsExporter.WINDS_ELEMENT);
        assert windElements.getLength() == 1;
        Element windElement = (Element) windElements.item(0);
        String windPath = weatherFile.getParent() + windElement.getAttribute(WeatherConditionsExporter.PATH_ATTRIBUTE);
        File windFile = new File(windPath);
        return WindsParser.parseWinds(windFile);
    }


}
