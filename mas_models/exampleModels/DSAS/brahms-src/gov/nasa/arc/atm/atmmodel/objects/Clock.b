/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.objects;

class theClock  {
	attributes:
		public int time;
		public int interval;
		public int endTime;
		
	activities:			
			
			broadcast announceTime(int dur) {
						random: false;
						max_duration: dur;
						about: 
							send(current.time = current.time);
						when: end;	 
			}
			
			java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
			}
			
			java computeSeparation() {
				max_duration: 1;
			  class: "gov.nasa.arc.brahms.atmjava.activities.ComputeSeparation";
			when:end;
		  }
		
			java printReport(int globalTime, int timeInc) {
				 class: "gov.nasa.arc.brahms.atmjava.activities.WriteReport";
			}
		
	workframes:
			
			workframe advance_time {
					repeat: true;
					priority: 2;
					variables:
						forone(int) interval;
						forone(int) adv_dur;
					when(knownval(current.time < current.endTime) and
							 knownval(interval = current.interval) and
							 knownval(adv_dur = interval + 5)
							 )	
					do {
						computeSeparation();
						conclude((current.time = current.time + interval), bc:100, fc:100);
						
						//nsr: the value 5 comes from the delays inserted in the updateTSSTimeline
						//and the synchronizeSlotMarkerWithPlane activities in the timeline and the
						//delaySimulatorClock in the AFO object which prints the values of the AFOs.
						//The final delay is within the computeSeparation activity. 
						//This ensures that there is no race condition between the timeline activities
						//and the global clock sending the new time to everyone. The durations are
						//based on the simulator clock, while the current.time represents the time
						//in the model where agents and objects advance.
						announceTime(adv_dur);
						printBelief(current, "time", attribute);
					}

			}
		
		workframe print_report {
		repeat: false;
		priority: 1;
		variables: 
		forone(int) val;
		forone(int) inc;
		when(
			knownval(current.time = current.endTime)
			and knownval(val = current.endTime)
			and knownval(inc = current.interval)
		)
		do {
			printReport(val, inc);
		}
		}
}

