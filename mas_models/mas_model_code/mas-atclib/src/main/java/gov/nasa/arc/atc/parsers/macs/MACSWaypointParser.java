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
import gov.nasa.arc.atc.geography.Waypoint;
import gov.nasa.arc.atc.utils.CalculationTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Double.valueOf;

/**
 * @author ahamon
 */
public class MACSWaypointParser {

    private static final String COMMENT_CHAR = "#";


    private MACSWaypointParser(){
        // private utility constructor
    }


    public static List<Waypoint> parseWaypointFile(File file) {
        List<Waypoint> waypoints = new LinkedList<>();
        try (Stream<String> lines = Files.lines(file.toPath())) {
            lines.forEach(line -> parseLine(line, waypoints));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableList(waypoints);
    }

    private static void parseLine(String line, List<Waypoint> waypoints) {
        if (!line.startsWith(COMMENT_CHAR)) {

            String[] splitLine = line.trim().replaceAll(" +", " ").split(" ");

            // FixName     Latitude   Longitude  MagVar   RelEntry  Cat  IcaoCode/FixId
            String fixName = splitLine[0];
            //
            String latString = splitLine[1];
            double lat = CalculationTools.toCoordinate(latString);
            //
            String lngString = splitLine[2];
            double lng = CalculationTools.toCoordinate(lngString);
            //
            waypoints.add(new Waypoint(fixName, lat, lng));

        }
    }


    public static void main(String[] args) {
        String filePath = "/Desktop/AOL Data/national_waypoints";
        File file = new File(filePath);
        List<Waypoint> waypointList = parseWaypointFile(file);

        System.err.println(" found " + waypointList.size() + " waypoints");

        ATCGeography geography = new ATCGeography("NAS_WPTs");
        waypointList.forEach(geography::addWaypoint);


        String outputFilePath = "/Desktop/AOL Data/NAS_WPTs.xml";
        File outputFile = new File(outputFilePath);
        ATCGeographyExporter.exportGeographyToXML(geography, outputFile);
    }
}
