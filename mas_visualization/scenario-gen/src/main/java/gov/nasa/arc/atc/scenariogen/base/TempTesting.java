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

package gov.nasa.arc.atc.scenariogen.base;

import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Waypoint;

/**
 * Created by ahamon on 10/17/16.
 */
public class TempTesting {

    private static final Waypoint WPT_A = new Waypoint("A", 1, 2);
    private static final Waypoint WPT_B = new Waypoint("B", 1, 2);
    private static final Waypoint WPT_C = new Waypoint("C", 1, 2);
    private static final Waypoint WPT_D = new Waypoint("D", 1, 2);
    private static final Waypoint WPT_E = new Waypoint("E", 1, 2);


    public static void main(String[] args) {
        testStrictEquality();
        test2ShorterV1();
        test2ShorterV2();
    }

    private static void testStrictEquality() {
        System.err.println("testStrictEquality");
        FlightSegment ab1 = new FlightSegment("ab1", "toto", WPT_A, WPT_B, 10000, 350);
        FlightSegment bc1 = new FlightSegment("bc1", "toto", WPT_B, WPT_C, 10000, 350);
        FlightSegment cd1 = new FlightSegment("cd1", "toto", WPT_C, WPT_D, 10000, 350);
        FlightSegment de1 = new FlightSegment("de1", "toto", WPT_D, WPT_E, 10000, 350);

        FlightPlan fpl1 = new FlightPlan("toto");
        fpl1.addSegment(ab1);
        fpl1.addSegment(bc1);
        fpl1.addSegment(cd1);
        fpl1.addSegment(de1);

        FlightSegment ab2 = new FlightSegment("ab2", "titi", WPT_A, WPT_B, 10000, 350);
        FlightSegment bc2 = new FlightSegment("bc2", "titi", WPT_B, WPT_C, 10000, 350);
        FlightSegment cd2 = new FlightSegment("cd2", "titi", WPT_C, WPT_D, 10000, 350);
        FlightSegment de2 = new FlightSegment("de2", "titi", WPT_D, WPT_E, 10000, 350);

        FlightPlan fpl2 = new FlightPlan("titi");
        fpl2.addSegment(ab2);
        fpl2.addSegment(bc2);
        fpl2.addSegment(cd2);
        fpl2.addSegment(de2);

        boolean contains2 = BrahmsScenarioParser.isContained(fpl2, fpl1);
        boolean contains1 = BrahmsScenarioParser.isContained(fpl1, fpl2);
        System.err.println(" contains2 => " + contains2);
        System.err.println(" contains1 => " + contains1);
        System.err.println("");
        assert contains1;
        assert contains2;

    }

    private static void test2ShorterV1() {
        System.err.println("testStrictEquality");
        FlightSegment ab1 = new FlightSegment("ab1", "toto", WPT_A, WPT_B, 10000, 350);
        FlightSegment bc1 = new FlightSegment("bc1", "toto", WPT_B, WPT_C, 10000, 350);
        FlightSegment cd1 = new FlightSegment("cd1", "toto", WPT_C, WPT_D, 10000, 350);
        FlightSegment de1 = new FlightSegment("de1", "toto", WPT_D, WPT_E, 10000, 350);

        FlightPlan fpl1 = new FlightPlan("toto");
        fpl1.addSegment(ab1);
        fpl1.addSegment(bc1);
        fpl1.addSegment(cd1);
        fpl1.addSegment(de1);

        FlightSegment ab2 = new FlightSegment("ab2", "titi", WPT_A, WPT_B, 10000, 350);
        FlightSegment bc2 = new FlightSegment("bc2", "titi", WPT_B, WPT_C, 10000, 350);
        FlightSegment cd2 = new FlightSegment("cd2", "titi", WPT_C, WPT_D, 10000, 350);

        FlightPlan fpl2 = new FlightPlan("titi");
        fpl2.addSegment(ab2);
        fpl2.addSegment(bc2);
        fpl2.addSegment(cd2);

        boolean contains2 = BrahmsScenarioParser.isContained(fpl2, fpl1);
        boolean contains1 = BrahmsScenarioParser.isContained(fpl1, fpl2);
        System.err.println(" contains2 => " + contains2);
        System.err.println(" contains1 => " + contains1);
        System.err.println("");
        assert contains2;
        assert !contains1;
    }

    private static void test2ShorterV2() {
        System.err.println("testStrictEquality");
        FlightSegment ab1 = new FlightSegment("ab1", "toto", WPT_A, WPT_B, 10000, 350);
        FlightSegment bc1 = new FlightSegment("bc1", "toto", WPT_B, WPT_C, 10000, 350);
        FlightSegment cd1 = new FlightSegment("cd1", "toto", WPT_C, WPT_D, 10000, 350);
        FlightSegment de1 = new FlightSegment("de1", "toto", WPT_D, WPT_E, 10000, 350);

        FlightPlan fpl1 = new FlightPlan("toto");
        fpl1.addSegment(ab1);
        fpl1.addSegment(bc1);
        fpl1.addSegment(cd1);
        fpl1.addSegment(de1);

        FlightSegment bc2 = new FlightSegment("bc2", "titi", WPT_B, WPT_C, 10000, 350);
        FlightSegment cd2 = new FlightSegment("cd2", "titi", WPT_C, WPT_D, 10000, 350);
        FlightSegment de2 = new FlightSegment("de2", "titi", WPT_D, WPT_E, 10000, 350);

        FlightPlan fpl2 = new FlightPlan("titi");
        fpl2.addSegment(bc2);
        fpl2.addSegment(cd2);
        fpl2.addSegment(de2);

        boolean contains2 = BrahmsScenarioParser.isContained(fpl2, fpl1);
        boolean contains1 = BrahmsScenarioParser.isContained(fpl1, fpl2);
        System.err.println(" contains2 => " + contains2);
        System.err.println(" contains1 => " + contains1);
        System.err.println("");
        assert contains2;
        assert !contains1;
    }

}
