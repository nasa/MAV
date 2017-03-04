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

package gov.nasa.arc.atc.parsers.macs;

import gov.nasa.arc.atc.export.WindsExporter;
import gov.nasa.arc.atc.physics.*;
import javafx.util.Pair;
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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class WindsParser {

    private static final Logger LOG = Logger.getGlobal();

    private WindsParser() {
        // private utility constructor
    }

    public static Winds parseWinds(File windsFile) {
        if (windsFile == null || !windsFile.exists()) {
            return null;
        }
        Document document;
        DocumentBuilderFactory builderFactory;
        builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            InputSource source = new InputSource(windsFile.getAbsolutePath());
            document = builder.parse(source);
            Element rootElement = document.getDocumentElement();

            NodeList windElements = rootElement.getElementsByTagName(WindsExporter.WIND_ELEMENT);

            // at the moment only support one real and one forecast wind
            WindData realWind = null;
            WindData forecastWind = null;

            for (int i = 0; i < windElements.getLength(); i++) {
                final Pair<WindData, WindType> wind = parseWindData((Element) windElements.item(i));
                if (wind == null) {
                    throw new IllegalStateException("could not parse wind " + windElements.item(i));
                }
                switch (wind.getValue()) {
                    case FORECAST:
                        forecastWind = wind.getKey();
                        break;
                    case REAL:
                        realWind = wind.getKey();
                        break;
                    default:
                        throw new IllegalArgumentException("wind type :: " + wind.getValue());
                }
            }
            assert realWind != null;
            assert forecastWind != null;
            //
            return new Winds(forecastWind, realWind);
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            LOG.log(Level.SEVERE, " {0}", ex);
            ex.printStackTrace();
        }
        return null;
    }

    private static Pair<WindData, WindType> parseWindData(Element windDataElement) {
        if (windDataElement.getAttribute(WindsExporter.TYPE_ATTRIBUTE).equals(SimpleAltitudeConstantWind.class.getSimpleName())) {
            return parseSimpleAltitudeConstantWind(windDataElement);
        }
        return null;
    }

    private static Pair<WindData, WindType> parseSimpleAltitudeConstantWind(Element windDataElement) {
        WindType type = WindType.valueOf(windDataElement.getAttribute(WindsExporter.NATURE_ATTRIBUTE));
        List<SimpleWindLayer> layers = new LinkedList<>();
        NodeList layersElements = windDataElement.getElementsByTagName(WindsExporter.LAYER_ELEMENT);
        // only handles SimpleWindLayer at the moment
        for (int i = 0; i < layersElements.getLength(); i++) {
            final SimpleWindLayer layer = parseSimpleWindLayer((Element) layersElements.item(i));
            layers.add(layer);
        }
        return new Pair<>(new SimpleAltitudeConstantWind(layers), type);
    }

    protected static SimpleWindLayer parseSimpleWindLayer(Element layerElement) {
        final int minimumAltitude = Integer.parseInt(layerElement.getAttribute(WindsExporter.MINIMUM_ALTITUDE_ATTRIBUTE));
        final int maximumAltitude = Integer.parseInt(layerElement.getAttribute(WindsExporter.MAXIMUM_ALTITUDE_ATTRIBUTE));
        NodeList parameterElements = layerElement.getElementsByTagName(WindsExporter.WIND_PARAMETERS_ELEMENT);
        assert parameterElements.getLength() == 1;
        WindParameters windParameters = parseWindParameters((Element) parameterElements.item(0));
        return new SimpleWindLayer(minimumAltitude, maximumAltitude, windParameters);
    }

    protected static WindParameters parseWindParameters(Element wPElement) {
        final double direction = Double.parseDouble(wPElement.getAttribute(WindsExporter.DIRECTION_ATTRIBUTE));
        final double speed = Double.parseDouble(wPElement.getAttribute(WindsExporter.SPEED_ATTRIBUTE));
        return new WindParameters(direction, speed);
    }
}
