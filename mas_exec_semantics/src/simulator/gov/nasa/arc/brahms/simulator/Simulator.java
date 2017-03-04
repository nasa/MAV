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

package gov.nasa.arc.brahms.simulator;


import gov.nasa.arc.brahms.model.MultiAgentSystem;
import gov.nasa.arc.brahms.simulator.scheduler.Scheduler;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class Simulator {
	
	static Logger logger = Logger.getLogger(Simulator.class);
	public static boolean DEBUG = false; //general debugging
	
	
	public static void startSim(MultiAgentSystem mas) {
		
		BasicConfigurator.configure();
		logger.info("initializing the scheduler");
		
		if(Simulator.DEBUG)
			System.out.println("initializing the scheduler");
		Scheduler scheduler = new Scheduler(mas);		
		while(scheduler.checkAndExecuteActiveFrames()); //{
		//	System.out.println("in the loop");
		//}
		//System.exit(1);
		//scheduler.Sch_Star();
	}
	

}
