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
import gov.nasa.arc.atc.geography.Region;
import gov.nasa.arc.atc.geography.Sector;
import gov.nasa.arc.atc.utils.CalculationTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class MACSSectorParser {

    private static final String COMMENT_CHAR = "#";

    private MACSSectorParser() {
        // private utility constructor
    }


    public static List<Sector> parseSectorFile(File file) {
        List<Sector> sectorList = new LinkedList<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            // not optimized, to test with streams
            final long nbLines = lines.size();
            int nextLineIndex = 0;
            while (nextLineIndex < nbLines) {
                nextLineIndex = parseSector(lines, nextLineIndex, nbLines, sectorList);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableList(sectorList);
    }

    private static int parseSector(List<String> lines, int nextLineIndex, long nbLines, List<Sector> sectorList) {
        int nextLineLoopIndex = nextLineIndex;
        String nextLine = lines.get(nextLineLoopIndex);

        //find name
        while ((nextLine.isEmpty() || nextLine.startsWith(COMMENT_CHAR)) && nextLineLoopIndex < nbLines) {
            nextLine = lines.get(nextLineLoopIndex);
            nextLineLoopIndex++;
        }
        // sector name
        String name = nextLine.trim().replaceAll(" +", " ").replace("\t", "");
        if (name.isEmpty()) {
            nextLineLoopIndex++;
            return nextLineLoopIndex;
        }
        nextLineLoopIndex++;


        Sector s = new Sector(name, nextLineIndex);
        Region r = new Region(0, 40000, "R1");
        s.addRegion(r);

        Region currentRegion = r;

        while (nextLineLoopIndex < nbLines) {
            nextLine = lines.get(nextLineLoopIndex);
            if (nextLine.isEmpty()) {
                break;
            }
            if(nextLine.contains("---")){
                Region rx = new Region(0, 40000, "R"+nextLineLoopIndex);
                s.addRegion(rx);
                currentRegion = rx;
            }else if (!nextLine.startsWith(COMMENT_CHAR)) {

                // add coordinates to list
                String[] splitLine = nextLine.trim().replaceAll(" +", " ").replace("\t", " ").split(" ");

                // Latitude   Longitude
                String latString = splitLine[0];
                double lat = CalculationTools.toCoordinate(latString);
                //
                String lngString = splitLine[1];
                double lng = -CalculationTools.toCoordinate(lngString);
                currentRegion.addVertex(lat, lng);

            }
            nextLineLoopIndex++;
        }
        sectorList.add(s);

        return nextLineLoopIndex;
    }


    public static void main(String[] args) {
        String filePath = "/Desktop/AOL Data/national_center_boundaries_0318";
        File file = new File(filePath);
        List<Sector> sectorList = parseSectorFile(file);

        System.err.println(" found " + sectorList.size() + " sectors");
        System.err.println(" Sectors :: " + sectorList.stream().map(sector -> sector.getName() + "[" + sector.getRegions().size() + "] ").collect(Collectors.joining(", ")));

        ATCGeography geography = new ATCGeography("NAS_Sectors");
        sectorList.forEach(geography::addSector);

        String outputFilePath = "/Desktop/AOL Data/NAS_Sectors.xml";
        File outputFile = new File(outputFilePath);
        ATCGeographyExporter.exportGeographyToXML(geography, outputFile);
    }

}
