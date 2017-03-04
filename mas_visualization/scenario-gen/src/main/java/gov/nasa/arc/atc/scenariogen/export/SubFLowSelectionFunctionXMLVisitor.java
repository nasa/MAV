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

package gov.nasa.arc.atc.scenariogen.export;

import gov.nasa.arc.atc.scenariogen.core.CombinedFunction;
import gov.nasa.arc.atc.scenariogen.core.SimpleFunction;
import gov.nasa.arc.atc.scenariogen.core.SubFLowSelectionFunction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author ahamon
 */
class SubFLowSelectionFunctionXMLVisitor {

    static final String FUNCTION_ELEMENT = "Function";

    static final String FUNCTION_CLASS_ATTRIBUTE = "fClass";
    static final String FUNCTION_NODE_ATTRIBUTE = "fNode";
    static final String FUNCTION_TYPE_ATTRIBUTE = "fType";

    private SubFLowSelectionFunctionXMLVisitor() {
        // private utility constructor
    }

    static void createXML(Document doc, Element parentElement, SubFLowSelectionFunction function) {
        if (function instanceof CombinedFunction) {
            createXML(doc, parentElement, (CombinedFunction) function);
        } else if (function instanceof SimpleFunction) {
            createXML(doc, parentElement, (SimpleFunction) function);
        } else {
            throw new UnsupportedOperationException("Does not support function " + function);
        }
    }

    private static void createXML(Document doc, Element parentElement, SimpleFunction function) {
        Element functionElement = doc.createElement(FUNCTION_ELEMENT);
        functionElement.setAttribute(FUNCTION_CLASS_ATTRIBUTE,function.getClass().getSimpleName());
        functionElement.setAttribute(FUNCTION_NODE_ATTRIBUTE,function.getNode());
        functionElement.setAttribute(FUNCTION_TYPE_ATTRIBUTE,function.getType().name());
        parentElement.appendChild(functionElement);
    }


    private static void createXML(Document doc, Element parentElement, CombinedFunction function) {
        Element functionElement = doc.createElement(FUNCTION_ELEMENT);
        functionElement.setAttribute(FUNCTION_CLASS_ATTRIBUTE,function.getClass().getSimpleName());
        createXML(doc, functionElement, function.getF1());
        createXML(doc, functionElement, function.getF2());
        parentElement.appendChild(functionElement);
    }
}
