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

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.activity.EndCondition;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.Detectable;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.ThoughtFrame;
import gov.nasa.arc.brahms.model.WorkFrame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * 
 * @author josie
 *
 *composite-activity 	::= 	composite-activity PAC.activity-name (
 *{ PAC.param-decl [ , PAC.param-decl ]* } )
 *{
 *{ display : ID.literal-string ; }
 *{ priority : [ ID.unsigned | PAC.param-name ] ; }
 *{ end_condition : [ detectable | nowork ] ; }
 *{ WFR.detectable-decl }
 *{ GRP.activities }
 *{ GRP.workframes }
 *{ GRP.thoughtframes }
 *}
 */
public class CompositeActivity extends Activity {
	
	protected Set<Activity> activities;
	protected Set<WorkFrame> workFrames;
	protected Set<ThoughtFrame> thoughtFrames;
	protected List<Detectable> detectables;
	protected EndCondition endCondition; //no-work (default) or detectable
	//protected int level;
	
	
	public CompositeActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
	
		super(name, display, max_duration, min_duration, 
				priority, random, params);
		activities = new HashSet<Activity>();
		workFrames = new HashSet<WorkFrame>();
		thoughtFrames = new HashSet<ThoughtFrame>();
		detectables = new ArrayList<Detectable>();
		endCondition = EndCondition.NOWORK;
		//level = 0;
	}
	
	public CompositeActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params) {
	
		super(name, display, max_duration, min_duration, 
				priority, random, params);
		activities = new HashSet<Activity>();
		workFrames = new HashSet<WorkFrame>();
		thoughtFrames = new HashSet<ThoughtFrame>();
		detectables = new ArrayList<Detectable>();
		endCondition = EndCondition.NOWORK;
		//level = 0;
	}
	
	public void setEndCondition(EndCondition ec) {
		endCondition = ec;
	}
	
	public EndCondition getEndCondition() {
		return endCondition;
	}
	
	public void addDetectable(Detectable detect) {
		detectables.add(detect);
	}
	
	public List<Detectable> getDetectables() {
		return detectables;
	}
	
	public void add(Activity act) {
		activities.add(act);
	}
	public void addActivity(Activity act) {
		activities.add(act);
	}
	public void add(WorkFrame wf) {
		workFrames.add(wf);
	}
	public void addWorkframe(WorkFrame wf) {
		workFrames.add(wf);
	}
	public void add(ThoughtFrame tf) {
		thoughtFrames.add(tf);
	}
	public void addThoughtframes(ThoughtFrame tf) {
		thoughtFrames.add(tf);
	}
	
	public Set<Activity> getActivities() {
		return activities;
	}
	
	public Set<WorkFrame> getWorkFrames() {
		return workFrames;
	}
	
	public Set<ThoughtFrame> getThoughtFrames() {
		return thoughtFrames;
	}
	
	public void addThoughtframe(ThoughtFrame tf) {
		thoughtFrames.add(tf);
	}
	
	/**
	 * Removes the given wf from the set of workframes
	 * @param wf to be removed
	 */
	public void removeWorkFrame(WorkFrame wf) {
		assert workFrames.contains(wf);
		workFrames.remove(wf);
	}
	
	/**
	 * Removes the given tf from the set of thoughtframes
	 * @param tf to be removed
	 */
	public void removeThoughtFrame(ThoughtFrame tf) {
		assert thoughtFrames.contains(tf);
		thoughtFrames.remove(tf);
	}

	@Override
	public Object clone() {
		try {
			CompositeActivity act = ((CompositeActivity) super.clone());
			act.name = new String(name);
			if(display != null)
				act.display = new String(display);
			act.params = new ArrayList <Parameter>(params);
			Set<Activity> newActs = new HashSet<Activity>();
			Iterator<Activity> actIt = activities.iterator();
			while(actIt.hasNext()){
				Activity newAct = (Activity) actIt.next().clone();
				newActs.add(newAct);
			}
			act.activities = newActs;
			Set<WorkFrame> wfs = getWorkFrames();
			Iterator <WorkFrame> wf_it = wfs.iterator();
			Set<WorkFrame> newWfs = new HashSet<WorkFrame>();
			while(wf_it.hasNext()){
				WorkFrame wf = wf_it.next();
				newWfs.add((WorkFrame) wf.clone());
			}
			Set<ThoughtFrame> tfs = getThoughtFrames();
			Iterator <ThoughtFrame> tf_it = tfs.iterator();
			Set<ThoughtFrame> newTfs = new HashSet<ThoughtFrame>();
			while(tf_it.hasNext()){
				ThoughtFrame tf = tf_it.next();
				newTfs.add((ThoughtFrame) tf.clone());
			}
			List<Detectable> dets = getDetectables();
			Iterator <Detectable> det_it = dets.iterator();
			List<Detectable> newDets = new ArrayList<Detectable>();
			while(det_it.hasNext()){
				Detectable det = det_it.next();
				newDets.add((Detectable) det.clone());
			}
			act.workFrames = newWfs;
			act.thoughtFrames = newTfs;
			act.detectables = newDets;
			act.endCondition = endCondition;
			return act;
		}//catch (CloneNotSupportedException e) {
		catch (Exception e) {
			System.out.println("Error in composite activity " + e);
			return null;
		}
	}	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Composite Activity:" + super.toString());
		s.append("\tdetectables: " + detectables.toString() + "\n");
		s.append("\tactivities: " + activities.toString() + "\n");
		s.append("\tworkframes: " + workFrames.toString() + "\n");
		s.append("\tthoughtframes: " + thoughtFrames.toString() + "\n");
		s.append("-------\n");
		return s.toString();
	}
}
