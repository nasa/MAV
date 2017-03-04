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

package gov.nasa.arc.brahms.vm.api.components;

import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;
import gov.nasa.arc.brahms.vm.api.exceptions.UnknownValueException;

public interface IWorldState {
	
	void setFactAttributeDouble(IConcept lhsConcept,
            java.lang.String attributeName,
            double value,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;
	


	void setFactAttributeInt(IConcept lhsConcept,
                         java.lang.String attributeName,
                         int value,
                         IContext ctx)
                         throws InvalidTypeException,
                                ExternalException;
	
	void setFactAttributeConcept(IConcept lhsConcept,
            java.lang.String attributeName,
            IConcept value,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;
	
	boolean getFactAttributeBoolean(IConcept lhsConcept,
            java.lang.String attributeName)
            throws UnknownValueException,
                   InvalidTypeException,
                   ExternalException;
	
	void setFactAttributeBoolean(IConcept lhsConcept,
            java.lang.String attributeName,
            boolean value,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;
	
	
}
