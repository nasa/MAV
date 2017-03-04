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

package gov.nasa.arc.brahms.vm.api.common;

import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;
import gov.nasa.arc.brahms.vm.api.exceptions.UnknownValueException;

public interface IActiveInstance {
	public boolean hasBeliefAttributeAnyValue(IConcept obj, String beliefName,
			boolean b, IContext ctx) throws ExternalException;
	
	public double getBeliefAttributeDouble(IConcept obj, String beliefName,
			IContext ctx) throws UnknownValueException,
								 InvalidTypeException,
								 ExternalException;
	
	public int getBeliefAttributeInt(IConcept obj, String beliefName,
					IContext ctx)  throws UnknownValueException,
										  InvalidTypeException,
										  ExternalException;
	
	java.lang.String getBeliefAttributeString(IConcept lhsConcept,
            java.lang.String attributeName,
            IContext ctx)
            throws UnknownValueException,
                   InvalidTypeException,
                   ExternalException;

	public void setBeliefAttributeInt(IConcept obj, String beliefName, int setVal,
			IContext ctx) throws InvalidTypeException,
            ExternalException;
	
	void setBeliefAttributeDouble(IConcept lhsConcept,
            java.lang.String attributeName,
            double value,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;
	
	void setBeliefAttributeSymbol(IConcept lhsConcept,
            java.lang.String attributeName,
            java.lang.String value,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;
	
	void setBeliefAttributeString(IConcept lhsConcept,
            java.lang.String attributeName,
            java.lang.String value,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;
	
	void setBeliefAttributeConcept(IConcept lhsConcept, 
								   java.lang.String attributeName, 
								   IConcept value, 
								   IContext ctx)
	                               throws InvalidTypeException,
	                                      ExternalException;
	


	void setBeliefAttributeConcept(IConcept lhsConcept,
                               java.lang.String attributeName,
                               int index,
                               IConcept value,
                               IContext ctx)
                               throws InvalidTypeException,
                                      ExternalException;
	
	IConcept getBeliefAttributeConcept(IConcept lhsConcept,
            java.lang.String attributeName,
            IContext ctx)
            throws UnknownValueException,
                   InvalidTypeException,
                   ExternalException;
	
	java.util.Map<java.lang.Object,java.lang.Object> getBeliefAttributeMap(IConcept lhsConcept,
            java.lang.String attributeName,
            boolean includeUnknown,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;
	
	void setBeliefAttributeJava(IConcept lhsConcept,
            java.lang.String attributeName,
            int index,
            java.lang.Object value,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;

	boolean hasBeliefRelationAnyValue(IConcept lhsConcept,
            java.lang.String relationName,
            boolean includeUnknown,
            IContext ctx)
            throws ExternalException;

	java.util.List<IConcept> getBeliefRelationConcepts(IConcept lhsConcept,
            java.lang.String relationName,
            IContext ctx)
            throws ExternalException;
        
	void addBeliefRelationConcept(IConcept lhsConcept,
            java.lang.String relationName,
            IConcept rhsConcept,
            IContext ctx)
            throws ExternalException;
    
	void removeBeliefRelationConcept(IConcept lhsConcept,
            java.lang.String relationName,
            IConcept rhsConcept,
            IContext ctx)
            throws ExternalException;
	
	boolean getBeliefAttributeBoolean(IConcept lhsConcept,
            java.lang.String attributeName,
            IContext ctx)
            throws UnknownValueException,
                   InvalidTypeException,
                   ExternalException;
	
	void setBeliefAttributeBoolean(IConcept lhsConcept,
            java.lang.String attributeName,
            boolean value,
            IContext ctx)
            throws InvalidTypeException,
                   ExternalException;
}
