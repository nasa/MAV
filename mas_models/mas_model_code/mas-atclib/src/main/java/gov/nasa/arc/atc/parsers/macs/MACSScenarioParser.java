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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//import com.univocity.parsers.csv.CsvParser;
//import com.univocity.parsers.csv.CsvParserSettings;

/**
 * @author ahamon
 */
public class MACSScenarioParser {

    private MACSScenarioParser() {
        // private utility constructor
    }


    public static void main(String[] args) {
        parseWithUniVocity();
    }

    private static void parseWithUniVocity() {
//        CsvParserSettings settings = new CsvParserSettings();
//        settings.setLineSeparatorDetectionEnabled(true);
//        settings.setDelimiterDetectionEnabled(true);
//
//        // creates a CSV parser
//        CsvParser parser = new CsvParser(settings);
//
//        // parses all rows in one go.
//        List<String[]> allRows = parser.parseAll(new File("/Desktop/AOL Data/Scenario_GAG.txt"));
////        allRows.stream().map(Arrays::stream).forEach(System.err::println);
////        allRows.forEach(r -> {
////                    System.err.print("row :: ");
////                    for (int i = 0; i < r.length; i++) {
//////                        System.err.print(" / " + r[i] != null ? r[i].trim() : "XX");
////                        System.err.print(" / " + r[i] );
////                    }
////                    System.err.println("");
////                }
////        );
//        String[] headers = allRows.get(0);
//        System.err.println(" Columns :: " + Arrays.stream(headers).collect(Collectors.joining(", ")));
//        System.err.println(" found " + allRows.size() + " lines");
    }


}
