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

package gov.nasa.arc.brahms.model.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.elems.Activity_Sim;

/**
 * 
 * @author josie
 *move-activity 	::= 	move PAC.activity-name (
{ PAC.param-decl [ , PAC.param-decl ]* } )
{
{ display : ID.literal-string ; }
{ priority : [ ID.unsigned | PAC.param-name ] ; }
{ random : [ ID.truth-value | PAC.param-name ] ; }
{ min_duration : [ ID.unsigned | PAC.param-name ] ; }
{ max_duration : [ ID.unsigned | PAC.param-name ] ; }
{ PAC.resources }
location : [ ARE.area-name | PAC.param-name ] ;
{ detectDepartureIn : [ ARE.area-name | PAC.param-name ] [ , [ ARE.area-name | PAC.param-name ] ]* ; }
{ detectDepartureInSubAreas : [ ID.truth-value | PAC.param-name ] ; }
{ detectArrivalIn : [ ARE.area-name | PAC.param-name ] [ , [ ARE.area-name | PAC.param-name ] ]* ; }
{ detectArrivalInSubAreas : [ ID.truth-value | PAC.param-name ] ; }
}
 */
public class MoveActivity extends Activity 
						  implements Cloneable{

	protected String location = "";
	protected ParameterValue locVal = null;
	
	public MoveActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
	}
	
	public MoveActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
	}
	
	/**
	 * Sets the location to that of the given agent
	 * @param ag
	 * @param location
	 */
	public void setLocation(Agent ag, String location) {
		ag.setLocation(location);
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setLocation(Value location) {
		if (location instanceof StringValue)
			this.location = ((StringValue) location).getStringValue();
		else if (location instanceof ParameterValue) {
			this.location = null;
			this.locVal = (ParameterValue) location;
		} else throw new RuntimeException("move loc must be string");
	}
	
	
	@SuppressWarnings("unused")
	private Parameter getParentParam(Basic b, String paramName) {
		if (b == null)
			return null;
		Stack<Object> stack = b.getCurrentWorkFrame();
		if (stack.size() > 1) {
			for (int i = stack.size() - 1; i >= 0; i--) {
				if (stack.get(i) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) stack.get(i);
					if (ai.getActivity() instanceof CompositeActivity) {
						CompositeActivity comp = (CompositeActivity) ai.getActivity();
						List<Parameter> parameters = comp.getParams();
						for (int j = 0; j < parameters.size(); j++) {
							if (parameters.get(j).getName().equals(paramName)) {
								return parameters.get(j);
							}
						}
					}
				}
			}
		} else throw new RuntimeException("***ERROR: couldn't find parameter for activity " + name);
		return null;
	}
	
	public String getLocation(Basic b) {
		List<Parameter> params = super.getParams();
		//System.out.println(params);
		//System.out.println(location);
		if(!params.isEmpty()){
			for(int i = 0; i < params.size(); i++){
				Parameter p = params.get(i);
				if(p.getName().equals(location)){
					//System.out.println("We're off to " + p.getVals().get(0).toString());
					return p.getVals().get(0).toString();
				}
				
			}
		}
		//System.out.println("We're off to " + location);
		return location;
/*		Josies code
 * 		if (locVal != null) {
			Parameter p = getParentParam(b, locVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s = ((StringValue) p.getVals().get(0)).getStringValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: when parameter needs to be a string");
			}
			return s;
		}
		return location;
		*/
	}
	
	/**
	 * The duration of the activity is calculated by the simulation engine
	 *  using the path definitions defining the distance between two locations.
	 *  The simulation will determine the shortest path of travel from the
	 *  current location to the goal location and calculate the travel time
	 *  based on the distance. The duration of the move activity will in that
	 *  case be the same as the calculated travel time. It is possible 
	 *  however to still define the duration of the activity. If the 
	 *  simulation engine cannot find a travel path then the defined duration
	 *  will be used. The duration of the activity can be defined to be a 
	 *  fixed amount of time.
	 * 
	 * 
	 * From Chin: if max and min durations are given they override distances
	 * random is only applicable for max and min.  This method is called from
	 * WorkFrame_Sim.pushNextEvent only when max == 0 (default when none given)
	 * 
	 * @param ag
	 * @return
	 */
	
	public int getDistance(Agent ag) {
		String oldLoc = ag.getLocation();
		String newLoc = getLocation(ag);
		return Activity_Sim.findPathDist(oldLoc, newLoc);
	/*	if (locVal == null)
			return Activity_Sim.findPathDist(oldLoc, location);
		else {
			String l = getLocation(ag);
			return Activity_Sim.findPathDist(oldLoc, l);
		}*/
	}

	public String toString(){	
		return "Move Activity:" +
		super.toString() + 
		"location: " + location + "\n" +
		"-------\n";
	}
	
	/*public Object clone() {
		MoveActivity move = ((MoveActivity) super.clone());
		move.location = new String(location);
		return move;
	}*/
	
}
