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

import java.util.ArrayList; 
import gov.nasa.arc.brahms.model.*; 
import gov.nasa.arc.brahms.model.activity.*; 
import gov.nasa.arc.brahms.model.concept.Class_b; 
import gov.nasa.arc.brahms.model.expression.*;

public class CalendarUtilClass extends Class_b { 
	public CalendarUtilClass() { 
		super("CalendarUtilClass", "CalendarUtilClass", 0.0, 0);
	}

	protected void addclassExtended() {
	} 

	protected void addAttributes() {
	}

	protected void addRelations() {
	}

	protected void addBeliefs() {
	}

	protected void addFacts() {
	}

	protected void addActivitygetCurrentTime() {
		StringValue nameAct2 = new StringValue("getCurrentTime");
		StringValue dispAct2 = new StringValue("getCurrentTime");
		IntegerValue maxAct2 = new IntegerValue(0);
		IntegerValue minAct2 = new IntegerValue(0);
		IntegerValue priorAct2 = new IntegerValue(0);
		BooleanValue randAct2 = new BooleanValue(false);
		ArrayList<Parameter> paramsAct2 = new ArrayList<Parameter>();
		paramsAct2.add(new Parameter("Calendar", "out")); 
		Activity act2 = new JavaActivity(nameAct2, dispAct2, maxAct2, minAct2, priorAct2, randAct2, paramsAct2);
		((JavaActivity) act2).setJavaClass("GetCurrentTimeActivity");
		((JavaActivity) act2).setWhen("end");
		activities.add(act2);

	} 
	/*
protected void addActivitygetCurrentTime2() {
StringValue nameAct0 = new StringValue("getCurrentTime2");
StringValue dispAct0 = new StringValue("getCurrentTime2");
IntegerValue maxAct0 = new IntegerValue(0);
IntegerValue minAct0 = new IntegerValue(0);
IntegerValue priorAct0 = new IntegerValue(0);
BooleanValue randAct0 = new BooleanValue(false);
ArrayList<Parameter> paramsAct0 = new ArrayList<Parameter>();
paramsAct0.add(new Parameter("symbol", "timeType")); 
paramsAct0.add(new Parameter("Calendar", "out")); 
Activity act0 = new JavaActivity(nameAct0, dispAct0, maxAct0, minAct0, priorAct0, randAct0, paramsAct0);
((JavaActivity) act0).setJavaClass("GetCurrentTimeActivity");
((JavaActivity) act0).setWhen("end");
activities.add(act0);

} 

protected void addActivitygetCalendar() {
StringValue nameAct1 = new StringValue("getCalendar");
StringValue dispAct1 = new StringValue("getCalendar");
IntegerValue maxAct1 = new IntegerValue(0);
IntegerValue minAct1 = new IntegerValue(0);
IntegerValue priorAct1 = new IntegerValue(0);
BooleanValue randAct1 = new BooleanValue(false);
ArrayList<Parameter> paramsAct1 = new ArrayList<Parameter>();
paramsAct1.add(new Parameter("string", "timeStamp")); 
paramsAct1.add(new Parameter("Calendar", "out")); 
Activity act1 = new JavaActivity(nameAct1, dispAct1, maxAct1, minAct1, priorAct1, randAct1, paramsAct1);
((JavaActivity) act1).setJavaClass("GetCalendarActivity");
((JavaActivity) act1).setWhen("end");
activities.add(act1);

} 



protected void addActivitygetTomorrow() {
StringValue nameAct3 = new StringValue("getTomorrow");
StringValue dispAct3 = new StringValue("getTomorrow");
IntegerValue maxAct3 = new IntegerValue(0);
IntegerValue minAct3 = new IntegerValue(0);
IntegerValue priorAct3 = new IntegerValue(0);
BooleanValue randAct3 = new BooleanValue(false);
ArrayList<Parameter> paramsAct3 = new ArrayList<Parameter>();
paramsAct3.add(new Parameter("Calendar", "in")); 
paramsAct3.add(new Parameter("Calendar", "out")); 
Activity act3 = new JavaActivity(nameAct3, dispAct3, maxAct3, minAct3, priorAct3, randAct3, paramsAct3);
((JavaActivity) act3).setJavaClass("GetTomorrowActivity");
((JavaActivity) act3).setWhen("end");
activities.add(act3);

} 

protected void addActivitygetYesterday() {
StringValue nameAct4 = new StringValue("getYesterday");
StringValue dispAct4 = new StringValue("getYesterday");
IntegerValue maxAct4 = new IntegerValue(0);
IntegerValue minAct4 = new IntegerValue(0);
IntegerValue priorAct4 = new IntegerValue(0);
BooleanValue randAct4 = new BooleanValue(false);
ArrayList<Parameter> paramsAct4 = new ArrayList<Parameter>();
paramsAct4.add(new Parameter("Calendar", "in")); 
paramsAct4.add(new Parameter("Calendar", "out")); 
Activity act4 = new JavaActivity(nameAct4, dispAct4, maxAct4, minAct4, priorAct4, randAct4, paramsAct4);
((JavaActivity) act4).setJavaClass("GetYesterdayActivity");
((JavaActivity) act4).setWhen("end");
activities.add(act4);

} 

protected void addActivitygetTimeStamp() {
StringValue nameAct5 = new StringValue("getTimeStamp");
StringValue dispAct5 = new StringValue("getTimeStamp");
IntegerValue maxAct5 = new IntegerValue(0);
IntegerValue minAct5 = new IntegerValue(0);
IntegerValue priorAct5 = new IntegerValue(0);
BooleanValue randAct5 = new BooleanValue(false);
ArrayList<Parameter> paramsAct5 = new ArrayList<Parameter>();
paramsAct5.add(new Parameter("Calendar", "in")); 
paramsAct5.add(new Parameter("string", "out")); 
Activity act5 = new JavaActivity(nameAct5, dispAct5, maxAct5, minAct5, priorAct5, randAct5, paramsAct5);
((JavaActivity) act5).setJavaClass("GetTimeStampActivity");
((JavaActivity) act5).setWhen("end");
activities.add(act5);

} 

protected void addActivityaddTime() {
StringValue nameAct6 = new StringValue("addTime");
StringValue dispAct6 = new StringValue("addTime");
IntegerValue maxAct6 = new IntegerValue(0);
IntegerValue minAct6 = new IntegerValue(0);
IntegerValue priorAct6 = new IntegerValue(0);
BooleanValue randAct6 = new BooleanValue(false);
ArrayList<Parameter> paramsAct6 = new ArrayList<Parameter>();
paramsAct6.add(new Parameter("Calendar", "in")); 
paramsAct6.add(new Parameter("symbol", "field")); 
paramsAct6.add(new Parameter("int", "amount")); 
paramsAct6.add(new Parameter("Calendar", "out")); 
Activity act6 = new JavaActivity(nameAct6, dispAct6, maxAct6, minAct6, priorAct6, randAct6, paramsAct6);
((JavaActivity) act6).setJavaClass("AddTimeActivity");
((JavaActivity) act6).setWhen("end");
activities.add(act6);

} 

protected void addActivityconvertToTimeZone() {
StringValue nameAct7 = new StringValue("convertToTimeZone");
StringValue dispAct7 = new StringValue("convertToTimeZone");
IntegerValue maxAct7 = new IntegerValue(0);
IntegerValue minAct7 = new IntegerValue(0);
IntegerValue priorAct7 = new IntegerValue(0);
BooleanValue randAct7 = new BooleanValue(false);
ArrayList<Parameter> paramsAct7 = new ArrayList<Parameter>();
paramsAct7.add(new Parameter("Calendar", "in")); 
paramsAct7.add(new Parameter("string", "timeZone")); 
paramsAct7.add(new Parameter("Calendar", "out")); 
Activity act7 = new JavaActivity(nameAct7, dispAct7, maxAct7, minAct7, priorAct7, randAct7, paramsAct7);
((JavaActivity) act7).setJavaClass("ConvertToTimeZoneActivity");
((JavaActivity) act7).setWhen("end");
activities.add(act7);

} 

protected void addActivityinDateRange() {
StringValue nameAct8 = new StringValue("inDateRange");
StringValue dispAct8 = new StringValue("inDateRange");
IntegerValue maxAct8 = new IntegerValue(0);
IntegerValue minAct8 = new IntegerValue(0);
IntegerValue priorAct8 = new IntegerValue(0);
BooleanValue randAct8 = new BooleanValue(false);
ArrayList<Parameter> paramsAct8 = new ArrayList<Parameter>();
paramsAct8.add(new Parameter("string", "startRange")); 
paramsAct8.add(new Parameter("string", "endRange")); 
paramsAct8.add(new Parameter("string", "date")); 
paramsAct8.add(new Parameter("boolean", "out")); 
Activity act8 = new JavaActivity(nameAct8, dispAct8, maxAct8, minAct8, priorAct8, randAct8, paramsAct8);
((JavaActivity) act8).setJavaClass("InDateRangeActivity");
((JavaActivity) act8).setWhen("end");
activities.add(act8);

} 
	 */

	protected void addActivities() {
		addActivitygetCurrentTime();
		/*	addActivitygetCurrentTime2();
addActivitygetCalendar();

addActivitygetTomorrow();
addActivitygetYesterday();
addActivitygetTimeStamp();
addActivityaddTime();
addActivityconvertToTimeZone();
addActivityinDateRange();*/
	} 

	protected void addThoughtFrames() {
	}

	protected void addWorkFrames() { 
	}

} //End class
