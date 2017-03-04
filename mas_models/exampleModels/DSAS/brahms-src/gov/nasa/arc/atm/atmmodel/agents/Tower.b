/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.agents;


group Tower memberof Controller{
	attributes:
	
		public double topArrivalETA;
		public int lastArrival;
		
		public int departureCounter;
		public int lastDepartureTime;
		
		protected int m_Time;
	
		public map departure_queue;
	
	
		
		
	initial_beliefs:
		
		//the first departure
		(current.departureCounter = 1);
		(current.lastDepartureTime = -100);
		(current.m_Time = 0);
		
	initial_facts:
	
		(current.departureCounter = 1);
		(current.lastDepartureTime = -100);
		(current.m_Time = 0);
	
		
	activities:
	
		java initializeAFO(AFO i_AFO, int i_TIME_INCREMENT) {	
			class: "gov.nasa.arc.brahms.atmjava.activities.InitializeAFO";
		}
	
		java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
		}
		
		java println(string message) { 
				class: "brahms.base.system.PrintlnActivity"; 
		}
		
		java cacheDepartures(Tower towerCtrl) {
		  class: "gov.nasa.arc.brahms.atmjava.activities.CacheDepartureQueue";
		}	
		
		java getValues(int o_lastArrival, double o_topETA) {
			class: "gov.nasa.arc.brahms.atmjava.activities.ValuesForTower";
		}
			
		communicate getBeliefsOfPlane(AFO i_AFO) {
			max_duration: 0;
			with: i_AFO;
			about:
				receive(i_AFO.startTime = unknown),
				receive(i_AFO.flying = unknown),
				receive(i_AFO.iStatus = unknown),
				receive(i_AFO.Name = unknown),
				receive(i_AFO.controller = unknown),
				receive(i_AFO.ETA = unknown);
			when: end;
		}		
		
		communicate updateBeliefsOfDeparturePlane(AFO i_AFO) {
			max_duration: 0;
			with: i_AFO;
			about:
				send(i_AFO.iStatus = unknown),
				send(i_AFO.controller = unknown),
				send(i_AFO.startTime = unknown);
			when: end;
		}	
			
	
	workframes:
	
	  workframe cacheDepartureQueue {
	  repeat: false;
		priority: 10;
		variables:
				forone(departures) i_Departures;
				forone(Tower) towerCtrl;
		when (
			known(i_Departures.departure_queue)
			and knownval (towerCtrl = current)
		) do {
			conclude((current.departure_queue = i_Departures.departure_queue));
			cacheDepartures(towerCtrl);
		} 
	  }

		workframe clearToDepart{
		repeat: true;
			priority: 1;
			variables:
				forone(departures) i_Departures;
				forone(AFO) i_AFO;
				forone(int) i_pointer;
				forone (int) arrTime;
				forone(int) i_TIME_INCREMENT; 
			when(
			  knownval(current.lastArrival = arrTime) and
			  knownval(current.topArrivalETA >= seperationConstants.DEP_ARR_MIN) and
				knownval(current.m_Time >= current.lastDepartureTime + seperationConstants.DEP_DEP_MIN) and
				knownval(current.m_Time >= arrTime + seperationConstants.ARR_DEP_MIN) and
				knownval(i_pointer = current.departureCounter) and
				knownval(i_Departures.departure_queue(i_pointer) = i_AFO) and
				knownval(i_TIME_INCREMENT = 1) 
			)
			do{
				conclude((current.departureCounter = i_pointer + 1));
				conclude((current.lastDepartureTime = globalClock.time));
				getBeliefsOfPlane(i_AFO);
				//setting the status to 0 and start time to the current global
				//clock time will activate begin flying workframe for the
				//departure in its corresponding AFO object. 
				conclude((i_AFO.iStatus = 0));
				conclude((i_AFO.startTime = globalClock.time));
				conclude((i_AFO.controller = current));
				updateBeliefsOfDeparturePlane(i_AFO);
			}
		}	
		
		workframe getETAOfFirstPlane {
			repeat: true;
			priority: 1;
			variables: 
				forone(int) i_TIME_INCREMENT;
				forone(int) o_lastArrival;
				forone(double) o_topETA;
			when(
			knownval(i_TIME_INCREMENT = 1)
			and	knownval(i_TIME_INCREMENT < globalClock.time - current.m_Time)
			)
			do {
			
			conclude((current.m_Time = current.m_Time + i_TIME_INCREMENT));
			getValues(o_lastArrival, o_topETA);
			conclude((current.topArrivalETA = o_topETA));
			conclude((current.lastArrival = o_lastArrival));
			}
		}	
}



