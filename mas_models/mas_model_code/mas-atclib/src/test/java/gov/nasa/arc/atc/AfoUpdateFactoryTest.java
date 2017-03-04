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

package gov.nasa.arc.atc;

import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.utils.Constants;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.*;

/**
 * @author ahamon
 */
public class AfoUpdateFactoryTest {

    private static Document doc;

    @BeforeClass
    public static void initTest() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.newDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testOnEmptyUpdate() {
        AfoUpdate update = AfoUpdateFactory.EMPTY_UPDATE;
        Element updateElement = AfoUpdateFactory.createXML(doc, update);
        AfoUpdate parsedUpdate = AfoUpdateFactory.parseElement(updateElement);

        assertEquals(update, parsedUpdate);
    }

    @Test
    public void testNonEmptyUpdate() {
        AfoUpdate update = new AfoUpdateImpl("name", 17, new Position(40, -71, 150), 250, 500, 90, 3, "MyDest", Constants.IS_FLYING, 12, -23, 48, "Bob", 1);
        Element updateElement = AfoUpdateFactory.createXML(doc, update);
        AfoUpdate parsedUpdate = AfoUpdateFactory.parseElement(updateElement);
        assertEquals(update, parsedUpdate);
    }

}
