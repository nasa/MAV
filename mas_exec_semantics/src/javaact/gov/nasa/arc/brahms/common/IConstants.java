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

package gov.nasa.arc.brahms.common;

public interface IConstants {
	
	/*
	 * Constants
	 */
	
	public final static int			UNKNOWN				= 200;
	
	/*
	 * Truth Value Constants
	 */
	/** Constant definining true as the truth value (1) */
	public final static int     TRUE          = 1;
	/** Constant defining false as the truth value (0) */
	public final static int     FALSE         = 0;

	/*
	 * Type Constants
	 */
	public final static int			UNDEFINED			= -1;
	// User Defined Type
	public final static int			UDT						= 1;
	// Value Types
	public final static int			INT						= 2;
	public final static int			DOUBLE				= 3;
	public final static int			FLOAT					= 4;
	public final static int			BOOLEAN				= 5;
	public final static int			STRING				= 6;
	public final static int			SYMBOL				= 7;
  public final static int     LONG          = 8;
  public final static int     BYTE          = 9;
  public final static int     SHORT         = 32;
  public final static int     CHAR          = 33;
  public final static int     VOID          = 34;
  
	public final static String	sINT					= "int";
	public final static String	sDOUBLE				= "double";
	public final static String	sFLOAT				= "float";
	public final static String	sBOOLEAN			= "boolean";
	public final static String	sSTRING				= "string";
	public final static String	sSYMBOL				= "symbol";
  public final static String	sLONG				  = "long";
  public final static String  sBYTE         = "byte";
  public final static String  sSHORT        = "short";
  public final static String  sCHAR         = "char";
  public final static String  sVOID         = "void";

	// Meta Types
	public final static int			GROUP					    = 10;
	public final static int			AGENT					    = 11;
	public final static int			CLASS					    = 12;
	public final static int			OBJECT				    = 13;
	public final static int			CONCEPTUALCLASS   = 14;
	public final static int			CONCEPTUALOBJECT  = 15;
	public final static int			AREADEF				    = 16;
	public final static int			AREA					    = 17;
	public final static int			PATH					    = 18;
	public final static int			PARAMETER			    = 19;
	public final static int			VARIABLE			    = 20;
	public final static int			CONCEPT				    = 21;
	public final static int			ATTRIBUTE			    = 22;
	public final static int			RELATION			    = 23;
	public final static int			ACTIVECONCEPT			= 25;
	public final static int			ACTIVECLASS				= 26;
	public final static int			ACTIVEINSTANCE		= 27;
	public final static int			GEOGRAPHYCONCEPT	= 28;
	public final static int			CONCEPTUALCONCEPT	= 29;

  // Collection Types
  public final static int     MAP               = 30;

  // Java Types
  public final static int     JAVA_TYPE         = 31;

  public final static String	sMAP						  = "map";

  public final static String	sJAVA_TYPE			  = "java";

  public final static String	sGROUP						= "Group";
	public final static String	sAGENT						= "Agent";
	public final static String	sCLASS						= "Class";
	public final static String	sOBJECT						= "Object";
	public final static String	sCONCEPTUALCLASS	= "ConceptualClass";
	public final static String	sCONCEPTUALOBJECT = "ConceptualObject";
	public final static String	sAREADEF					= "AreaDef";
	public final static String	sAREA							= "Area";
	public final static String	sPATH							= "Path";
	public final static String	sCONCEPT					= "Concept";
	public final static String	sACTIVECONCEPT		= "ActiveConcept";
	public final static String	sACTIVECLASS			= "ActiveClass";
	public final static String	sACTIVEINSTANCE		= "ActiveInstance";
	public final static String	sGEOGRAPHYCONCEPT = "GeographyConcept";
	public final static String	sCONCEPTUALCONCEPT= "ConceptualConcept";

	public final static String  sPARAMETER        = "Parameter";


}
