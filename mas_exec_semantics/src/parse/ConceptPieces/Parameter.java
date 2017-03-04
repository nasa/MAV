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

package ConceptPieces;

public class Parameter {

	String name;
	String type;
	String typeDetail;
	
	public Parameter(String new_name, String new_type, String new_typeDetail){
		name = new_name;
		type = new_type;
		if(name.contains(".")){
			int index = name.lastIndexOf(".");
			name = name.substring(index+1);
		}
		if(type.contains(".")){
			int index = type.lastIndexOf(".");
			type = type.substring(index+1);
		}
		typeDetail = new_typeDetail;
		
	}
	
	public String getName(){
		return name;
	}

	public String getType(){
		return type;
	}

	public String getTypeDetail(){
		return typeDetail;
	}
	
}
