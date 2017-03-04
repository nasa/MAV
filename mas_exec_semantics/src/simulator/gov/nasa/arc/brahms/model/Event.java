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

package gov.nasa.arc.brahms.model;

import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.simulator.scheduler.DQEvent;
import gov.nasa.arc.brahms.simulator.scheduler.EventSequence;

/**
 * Superclass for Brahms "events" such as concludes and activities to inherit from
 *
 */

public abstract class Event extends DQEvent 
				implements Cloneable {
	
	public Object clone() {
		try {
			return ((Event) super.clone());
		}catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}
	}
	
	public abstract void createDeltaQueueEvent
					(EventSequence es, Basic b, Frame f);
}
