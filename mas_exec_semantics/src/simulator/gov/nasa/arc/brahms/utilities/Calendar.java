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

package gov.nasa.arc.brahms.utilities;


import gov.nasa.arc.brahms.model.*; 

import gov.nasa.arc.brahms.model.concept.Class_b; 



public class Calendar extends Class_b { 
	public Calendar() { 
		super("Calendar", "Calendar", 0.0, 0);
	}

	protected void addclassExtended() {
		classesExtended.add("SerializableObject");
	} 

	protected void addAttributes() {
		Attribute attr0 = new Attribute("weekOfYear", "public", "int"); 
		attributes.add(attr0);

		Attribute attr1 = new Attribute("timems", "public", "long"); 
		attributes.add(attr1);

		Attribute attr2 = new Attribute("dayOfMonth", "public", "int"); 
		attributes.add(attr2);

		Attribute attr3 = new Attribute("weekOfMonth", "public", "int"); 
		attributes.add(attr3);

		Attribute attr4 = new Attribute("sMonth", "public", "string"); 
		attributes.add(attr4);

		Attribute attr5 = new Attribute("timeZone", "public", "string"); 
		attributes.add(attr5);

		Attribute attr6 = new Attribute("timeStamp", "public", "string"); 
		attributes.add(attr6);

		Attribute attr7 = new Attribute("hour", "public", "int"); 
		attributes.add(attr7);

		Attribute attr8 = new Attribute("minute", "public", "int"); 
		attributes.add(attr8);

		Attribute attr9 = new Attribute("timeZoneShort", "public", "string"); 
		attributes.add(attr9);

		Attribute attr10 = new Attribute("second", "public", "int"); 
		attributes.add(attr10);

		Attribute attr11 = new Attribute("year", "public", "int"); 
		attributes.add(attr11);

		Attribute attr12 = new Attribute("sDayOfWeek", "public", "string"); 
		attributes.add(attr12);

		Attribute attr13 = new Attribute("dayOfWeek", "public", "int"); 
		attributes.add(attr13);

		Attribute attr14 = new Attribute("date", "public", "int"); 
		attributes.add(attr14);

		Attribute attr15 = new Attribute("month", "public", "int"); 
		attributes.add(attr15);

		Attribute attr16 = new Attribute("dayOfWeekInMonth", "public", "int"); 
		attributes.add(attr16);

		Attribute attr17 = new Attribute("millisecond", "public", "int"); 
		attributes.add(attr17);

	}

	protected void addRelations() {
	}

	protected void addBeliefs() {
	}

	protected void addFacts() {
	}

	protected void addThoughtFrames() {
	}

	protected void addWorkFrames() { 
	}

} //End class
