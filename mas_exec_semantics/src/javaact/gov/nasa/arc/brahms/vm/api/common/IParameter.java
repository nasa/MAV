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

import gov.nasa.arc.brahms.common.IConstants;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidContentTypeException;

public interface IParameter {
	
	  //Constants taken from the Brahms simulator code
	  /** The constant indicating the value is an integer */
	  public final static int INT      = IConstants.INT;
	  /** The constant indicating the value is a character */
	  public final static int CHAR     = IConstants.CHAR;
	  /** The constant indicating the value is a byte */
	  public final static int BYTE     = IConstants.BYTE;
	  /** The constant indicating the value is an integer */
	  public final static int SHORT    = IConstants.SHORT;
	  /** The constant indicating the value is a long */
	  public final static int LONG     = IConstants.LONG;
	  /** The constant indicating the value is a float */
	  public final static int FLOAT    = IConstants.FLOAT;
	  /** The constant indicating the value is a double */
	  public final static int DOUBLE   = IConstants.DOUBLE;
	  /** The constant indicating the value is a boolean */
	  public final static int BOOLEAN  = IConstants.BOOLEAN;
	  /** The constant indicating the value is a symbol */
	  public final static int SYMBOL   = IConstants.SYMBOL;
	  /** The constant indicating the value is a string */
	  public final static int STRING   = IConstants.STRING;
	  /** The constant indicating the value is a concept */
	  public final static int CONCEPT  = IConstants.CONCEPT;
	  /** The constant indicating the value is a Java Object*/
	  public final static int JAVA_TYPE  = IConstants.JAVA_TYPE;
	  /** The constant indicating the value is a variable */
	  public final static int VARIABLE = IConstants.VARIABLE;
	  /** The constant indicating the value is unknown */
	  public final static int UNKNOWN  = IConstants.UNKNOWN;

	//this is the getter method for the containing parameter	
	public Parameter getContainingParam(); 
	
	public String getVariablePassedName();
	
	public int getContentType() throws ExternalException; 
	
	public IConcept getConceptValue() throws InvalidContentTypeException, ExternalException;

	public IVariable getVariableValue() throws InvalidContentTypeException, ExternalException;
	
	double getDoubleValue() throws InvalidContentTypeException, ExternalException;
	
	java.lang.String getStringValue()
            throws InvalidContentTypeException,
                   ExternalException;
	
	java.lang.String getSymbolValue()
            throws InvalidContentTypeException,
                   ExternalException;
	
	int getIntValue()
            throws InvalidContentTypeException,
                   ExternalException;
	
	boolean getBooleanValue()
            throws InvalidContentTypeException,
                   ExternalException;
	
	java.lang.Object getJavaObjectValue()
            throws InvalidContentTypeException,
                   ExternalException;
	
	long getLongValue()
            throws InvalidContentTypeException,
                   ExternalException;

	java.lang.String getName()
            throws ExternalException;

	
	
}
