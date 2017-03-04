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

package gov.nasa.jpf.mas.extract;

import gov.nasa.jpf.vm.ElementInfo;

public class ExtractValues {
	
	 private final static String BoolValClass =
			 "Lgov/nasa/arc/brahms/model/expression/BooleanValue;";
	 private final static String DblValClass =
			 "Lgov/nasa/arc/brahms/model/expression/DoubleValue;";
	 private final static String IntValClass =
			 "Lgov/nasa/arc/brahms/model/expression/IntegerValue;";
	 private final static String ParamValClass =
			 "Lgov/nasa/arc/brahms/model/expression/ParameterValue;";
	 private final static String SgtValClass = 
			 "Lgov/nasa/arc/brahms/model/expression/SglObjRef;";
	 private final static String StringValClass =
			 "Lgov/nasa/arc/brahms/model/expression/StringValue;";
	 private final static String SymValClass =
			 "Lgov/nasa/arc/brahms/model/expression/SymbolValue;";
	 private final static String TplValClass =
			 "Lgov/nasa/arc/brahms/model/expression/TplObjRef;";
	 private final static String VarValClass =
			 "Lgov/nasa/arc/brahms/model/expression/VariableValue;";
	
	 
	 public static String getValue(String val, 
			 							ElementInfo ei2Val) { 
		 String typeName = ei2Val.getType();
		 
		 val = val + "=";
		 
		 switch(typeName) {
		 case (BoolValClass) : 
			 return getBoolValue(val, ei2Val);
		 case (DblValClass) : 
			 return getDblValue(val, ei2Val);
		 case (IntValClass) :
			 return getIntValue(val, ei2Val);
		 case (ParamValClass) : 
			 return getParamValue(val, ei2Val);
		 case (SgtValClass) :
			 return getSgtRef(val, ei2Val);
		 case (StringValClass) :
			 return getString(val, ei2Val);
		 case (SymValClass) :
			 return getSymValue(val, ei2Val);
		 case (TplValClass) : 
			 return getTplRef(val, ei2Val);
		 case (VarValClass) :
			 return getVariableValue(val, ei2Val);
		 default : 
			 
		 }
		 throw new RuntimeException("could not find class for : " + typeName);
	 }
	 
	 private static String getBoolValue(String val, ElementInfo ei) {
		 return val + Boolean.toString(ei.getBooleanField("val"));
	 }
	 
	 private static String getDblValue(String val, ElementInfo ei) {
		 return val + ei.getDoubleField("val");
	 }
	 
	 private static String getIntValue(String val, ElementInfo ei) { 
		 return val + Integer.toString(ei.getIntField("val"));
	 }
	 
	 private static String getParamValue(String val, ElementInfo ei) {
		return val + ei.getStringField("name");
	 }
	 
	 private static String getSgtRef(String val, ElementInfo ei) {
		  return val + ei.getStringField("objRefName");
	 }
	 
	 private static String getString(String val, ElementInfo ei) {
		 return val +  ei.getStringField("val");  
	 }
	 
	 private static String getSymValue(String val, ElementInfo ei) {
		 return val + ei.getStringField("val");
	 }
	 
	 private static String getTplRef(String val, ElementInfo ei) {
		 return val + ei.getStringField("objRefName");
	 }
	 
	 private static String getVariableValue(String val, ElementInfo ei) {
		 return val + ei.getStringField("varName");
	 }
	
}
