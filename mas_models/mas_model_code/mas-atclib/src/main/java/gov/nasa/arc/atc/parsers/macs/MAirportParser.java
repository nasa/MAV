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


import gov.nasa.arc.atc.export.ATCGeographyExporter;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Waypoint;
import gov.nasa.arc.atc.utils.CalculationTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author ahamon
 */
public class MAirportParser {

    private static final String COMMENT_CHAR = "#";

    private MAirportParser() {
        // private utility constructor
    }

    public static List<Airport> parseWaypointFile(File file) {
        List<Airport> airportList = new LinkedList<>();
        try (Stream<String> lines = Files.lines(file.toPath())) {
            lines.forEach(line -> parseLine(line, airportList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableList(airportList);
    }


    private static void parseLine(String line, List<Airport> airportList) {
        if (!line.startsWith(COMMENT_CHAR) && !line.isEmpty()) {

            String[] splitLine = line.trim().replace("\t", " ").replaceAll(" +", " ").split(" ");

            // FixName     Latitude   Longitude  MagVar   RelEntry  Cat  IcaoCode/FixId
            String fixName = splitLine[1];
            //
            String latString = splitLine[2];
            double lat = CalculationTools.toCoordinate(latString);
            //
            String lngString = splitLine[3];
            double lng = CalculationTools.toCoordinate(lngString);
            //
            int elevation = Integer.parseInt(splitLine[4]);
            //
            airportList.add(new Airport(fixName, lat, lng, elevation));

        }
    }


    public static void main(String[] args) {
        String filePath = "/Desktop/AOL Data/national_airports";
        File file = new File(filePath);
        List<Airport> airportList = parseWaypointFile(file);

        System.err.println(" found " + airportList.size() + " airports");

        ATCGeography geography = new ATCGeography("NAS_Airports");
        airportList.forEach(geography::addAirport);


        String outputFilePath = "/Desktop/AOL Data/NAS_Airports.xml";
        File outputFile = new File(outputFilePath);
        ATCGeographyExporter.exportGeographyToXML(geography, outputFile);
    }

}
