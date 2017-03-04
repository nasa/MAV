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

package gov.nasa.arc.atc.scenariogen.core;


import java.util.function.Predicate;

/**
 * @author ahamon
 */
public class SimpleFunction implements SubFLowSelectionFunction {

    private final FlowType type;
    private final String nodeName;
    private final Predicate<? super RichFlightPlan> identifierFunction;


    public SimpleFunction(FlowType type, String nodeName) {
        this.type = type;
        this.nodeName = nodeName;
        switch (type) {
            case ARRIVAL:
                identifierFunction = fpl -> fpl.arrivesAt(nodeName);
                break;
            case DEPARTURE:
                identifierFunction = fpl -> fpl.departsFrom(nodeName);
                break;
            case THROUGH:
                identifierFunction = fpl -> fpl.crosses(nodeName);
                break;
            default:
                throw new IllegalArgumentException("Cannot handle flow type " + type);
        }
    }

    public FlowType getType() {
        return type;
    }

    public String getNode() {
        return nodeName;
    }


    @Override
    public boolean isCandidate(RichFlightPlan fpl) {
        return identifierFunction.test(fpl);
    }

    @Override
    public String toString() {
        return "["+ type.name()+" "+nodeName+"]";
    }
}
