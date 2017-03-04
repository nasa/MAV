package gov.nasa.arc.atm.atmmodel.objects;

class timeLine{

		attributes:
		protected map tssTimeLine;
		public int size;
		public int index;
		protected int m_iTimeStamp;
		public boolean newPlane;

		public int schedulingConcept;
		public int dsasTimeHorizon;


	relations: 
		public Airplane hasSlotMarker;


	initial_beliefs: 
		(current.size = 0);
		(current.index = 0);
		(current.m_iTimeStamp = 0);
		(current.newPlane = false);
		//default just TSS 
		//TODO: Fix to read from defined Constants
		(current.schedulingConcept = 2);
		(current.dsasTimeHorizon = 1200);

	initial_facts: 
		(current.size = 0);
		(current.index = 0);
		(current.m_iTimeStamp = 0);
		(current.newPlane = false);
		//default just TSS 
		//TODO: Fix to read from defined Constants
		(current.schedulingConcept = 2);
		(current.dsasTimeHorizon = 1200);
		
		
		
	
	activities:  
	
	
communicate sendTimeLine(){
  		max_duration: 0;
      with: ZNY_118;
      about:
      	send(current.tssTimeLine = unknown),
				send(current.index = unknown);
      when: end;
    } 

			
	java updateTSSTimeline(timeLine tl, int globalTime) {
		  max_duration: 1;
			class: "gov.nasa.arc.brahms.atmjava.activities.UpdateTSSTimeline";
			when: end;
	}	
	
	java performDSASComputation(timeLine tl, int globalTime) {
		  class: "gov.nasa.arc.brahms.atmjava.activities.DSASComputation";
	}

	java performGateToGate(timeLine tl, int globalTime) {
		  class: "gov.nasa.arc.brahms.atmjava.activities.DepartureTimeBasedSchedule";
	}
	
	java performDSASWithHorizonComputation(timeLine tl, int globalTime, int horizonTime) {
		  class: "gov.nasa.arc.brahms.atmjava.activities.DSASWithHorizonComputation";
	}
	
	java synchronizeSlotMarkerWithPlane(Airplane i_AFO, slotMarker i_slotAFO) {
			max_duration: 1;
			class: "gov.nasa.arc.brahms.atmjava.activities.SynchronizeSlotMarkerWithPlane";
			when: end;
	}	
	
	java retractBeliefs(Concept subject, string attribute, int index) {
		class: "brahms.base.system.RetractBeliefsActivity";
	}
	
	java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
	}
	
		
	java println(string message) { 
				class: "brahms.base.system.PrintlnActivity"; 
	} 
			

		
workframes:

//the prioirty of the addSlotMarker to time line is higher
//than the priority of the compute schedule workframe
//all the planes (slot markers) that start a particular time should
//be added to the timeline before the TSS schedule is computed
workframe addSlotMarkerToTimeLine {
	repeat: true;
	priority: 2;
	variables: 
		forone(Airplane) i_AFO;
		forone(slotMarker) i_slotAFO;
		forone(timeLine) i_TSS;
		forone(int) sizeVal;
	detectables:
	when(
	knownval(sizeVal = current.size) and
	  not(current hasSlotMarker i_AFO) and
		knownval(i_AFO.startTime <= globalClock.time) and
		knownval(i_AFO.iStatus = 1) and
		knownval(i_AFO.is_departure = false) and
		knownval(i_AFO.Name = i_slotAFO.Name) and
		knownval(i_TSS = current)
		
	)
	do {
		
		// Update all the beliefs in the slot marker
		synchronizeSlotMarkerWithPlane(i_AFO, i_slotAFO);
		
		// Add the slot marker to the current timeline for TSS scheduling
		conclude((current.tssTimeLine(sizeVal) = i_slotAFO));
		printBelief(current, size, "attribute");
	  printBelief(current, tssTimeLine, "attribute");
		conclude((current.size = current.size+1));
		conclude((current hasSlotMarker i_AFO is true));
		printBelief(current, hasSlotMarker, "relation");
		conclude((current.newPlane = true));
		
	}
}

//nsr: there is an ordering dependency between addSlotMarkerToTimeLine and
//the computeTSSSchedule, because this frame gets activated only when 
//addSlotMarkerToTimeline sets current.newPlane to true which set to false
//in the computeTSSSchedule. These cannot be active at the time. There should
//be no case under which there can be a race between the two frames. 
workframe computeTSSSchedule {
	repeat: true;
	priority: 1;
	variables: 
		forone(timeLine) i_TSS;
		forone(int) i_TIME_INCREMENT;
		forone(int) time;
	detectables:
	when(
	knownval(i_TSS = current)
	and knownval(i_TIME_INCREMENT = 1) 
	and knownval(i_TIME_INCREMENT <= globalClock.time - i_TSS.m_iTimeStamp)
	and knownval(i_TSS.newPlane = true)
	and knownval(globalClock.time = time)
	and knownval(i_TSS.schedulingConcept = 0)
	)
	do {
		// Update the TSS timeline
		updateTSSTimeline(i_TSS, time);
		println("TSS Update");
		sendTimeLine();
		conclude((i_TSS.newPlane = false));
		conclude((i_TSS.m_iTimeStamp = i_TSS.m_iTimeStamp + i_TIME_INCREMENT));
			
	}
}


workframe performDSAS {
	repeat: true;
	priority: 1;
	variables: 
		forone(timeLine) i_TSS;
		forone(int) i_TIME_INCREMENT;
		forone(int) time;
	detectables:
	when(
	 knownval(i_TSS = current)
	and knownval(i_TIME_INCREMENT = 1) 
	and knownval(i_TIME_INCREMENT <= globalClock.time - i_TSS.m_iTimeStamp)
	and knownval(i_TSS.newPlane = true)
	and knownval(globalClock.time = time)
	and knownval(i_TSS.schedulingConcept = 2)
	)
	do {
		// Update the TSS timeline
		println("DSAS Computation");
		//this does not have the updated TSS yet
		performDSASComputation(i_TSS, time);
		sendTimeLine();
		conclude((i_TSS.newPlane = false));
		conclude((i_TSS.m_iTimeStamp = i_TSS.m_iTimeStamp + i_TIME_INCREMENT));
			
	}
}

workframe performDSAS_20minHorizon {
	repeat: true;
	priority: 1;
	variables: 
		forone(timeLine) i_TSS;
		forone(int) i_TIME_INCREMENT;
		forone(int) time;
		forone(int) dsasHorizon;
	detectables:
	when(
	 knownval(i_TSS = current)
	and knownval(i_TIME_INCREMENT = 1) 
	and knownval(i_TIME_INCREMENT <= globalClock.time - i_TSS.m_iTimeStamp)
	and knownval(i_TSS.newPlane = true)
	and knownval(globalClock.time = time)
	and knownval(dsasHorizon = i_TSS.dsasTimeHorizon)
	and knownval(i_TSS.schedulingConcept = 3)
	)
	do {
		// Update the TSS timeline
		println("DSAS with horizon Computation");
		//this does not have the updated TSS yet
		performDSASWithHorizonComputation(i_TSS, time, dsasHorizon);
		sendTimeLine();
		conclude((i_TSS.newPlane = false));
		conclude((i_TSS.m_iTimeStamp = i_TSS.m_iTimeStamp + i_TIME_INCREMENT));
			
	}
}


workframe performGateToGate {
	repeat: true;
	priority: 1;
	variables: 
		forone(timeLine) i_TSS;
		forone(int) i_TIME_INCREMENT;
		forone(int) time;
	detectables:
	when(
	 knownval(i_TSS = current)
	and knownval(i_TIME_INCREMENT = 1) 
	and knownval(i_TIME_INCREMENT <= globalClock.time - i_TSS.m_iTimeStamp)
	and knownval(i_TSS.newPlane = true)
	and knownval(globalClock.time = time)
	and knownval(i_TSS.schedulingConcept = 1)
	)
	do {
		println("DSAS based on Departure time");
		performGateToGate(i_TSS, time);
		sendTimeLine();
		conclude((i_TSS.newPlane = false));
		conclude((i_TSS.m_iTimeStamp = i_TSS.m_iTimeStamp + i_TIME_INCREMENT));
			
	}
}


//TODO: maybe write a java activity to actually remove the plane
//workframe incrementTimeLineCounter {
//		repeat: true;
//		priority: 1;
//		variables:
//			forone(AFO) i_AFO;
//			forone (int) var_index;
//			when(
//			//	not(current.size = 0) and 
//				knownval(current.index = var_index) and
//				knownval(current.tssTimeLine(var_index) = i_AFO) and
//				knownval(i_AFO.iStatus = 3) 
//			)
//			do {
//				println("coming to increment time line counter");
//				printBelief(current, index, "attribute");
//				//retractBeliefs(current, tssTimeLine, var_index);
//				conclude((current.index = current.index + 1));
//				conclude((current.lastArrivalTime = globalClock.time));
//				sendTimeLine();
//				printBelief(current, lastArrivalTime, "attribute");
//			}
//		
//		}


}

