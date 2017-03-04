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
public class CombinedFunction implements SubFLowSelectionFunction {


    public enum CombineType {AND, OR}

    private final SubFLowSelectionFunction f1;
    private final SubFLowSelectionFunction f2;
    private final CombineType type;
    private final Predicate<? super RichFlightPlan> identifierFunction;

    public CombinedFunction(SubFLowSelectionFunction f1, SubFLowSelectionFunction f2, CombineType type) {
        this.f1 = f1;
        this.f2 = f2;
        this.type = type;
        switch (type) {
            case AND:
                identifierFunction = fpl -> f1.isCandidate(fpl) && f2.isCandidate(fpl);
                break;
            case OR:
                identifierFunction = fpl -> f1.isCandidate(fpl) || f2.isCandidate(fpl);
                break;
            default:
                throw new IllegalArgumentException("Cannot handle flow type " + type);
        }
    }

    @Override
    public boolean isCandidate(RichFlightPlan fpl) {
        return identifierFunction.test(fpl);
    }

    @Override
    public String toString() {
        return "{ " + f1 + " " + type.name() + " " + f2 + " }";
    }

    public SubFLowSelectionFunction getF1() {
        return f1;
    }

    public SubFLowSelectionFunction getF2() {
        return f2;
    }

    public CombineType getType() {
        return type;
    }


}
