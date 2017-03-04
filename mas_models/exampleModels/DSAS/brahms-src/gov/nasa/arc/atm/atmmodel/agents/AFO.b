/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.agents;

group AFO{
	attributes:
		
		public string Name;							// Name of object
		
		// Flatten: protected IAircraft aircraft;
		protected int m_iAirSpeed; 			// kts - nautical miles per hour
    protected double m_dVerticalSpeed; // feet per second of ascent or descent
    protected double m_dLatitude; 	// decimal degrees - positive if North, negative if South
    protected double m_dLongitude; 	// decimal degrees - positive if East, negative if West
    protected double m_dAltitude; 	// in feet
    protected double m_dBearing; 		// in decimal degrees
    protected int m_headingEnum; 		// North, South, South-West, etc.
	
		public flightPlan flightPlan;		// The flight plan
		public int iCurrentSegment;			// Which segment in flightplan
		protected waypoint toWaypoint;  // toWaypoint on current segment
		protected int iStatus;					// Is the airplane flying or on ground
	
		protected int m_iTimeStamp;
		public boolean flying;
		public int startTime;
		public boolean is_departure;    // used to distinguish between arrivals and departures
	

		public double ETA; 							// Used by the visualization to report differences
		public double DTA; 	            // Distance to airport
		public Controller controller;   // The current controller assigned to this AFO
		protected int is_Metering;
		

		
	initial_beliefs:

		(current.m_iTimeStamp = 0);
		(current.flying = false);
		(current.is_Metering = 0);
		
		
	initial_facts:
		
		(current.m_iTimeStamp = 0);
		(current.flying = false);
		(current.is_Metering = 0);
		
		
	
		
	activities:
				
		java updateAFOPosition(AFO i_AFO, int i_TIME_INCREMENT) {
			class: "gov.nasa.arc.brahms.atmjava.activities.UpdateAFOPosition";
		}

		java initializeAFO(AFO i_AFO, int i_TIME_INCREMENT) {	
			class: "gov.nasa.arc.brahms.atmjava.activities.InitializeAFO";
		}

		java logAFOFlightPlan(AFO iAFO) {	
			class: "gov.nasa.arc.brahms.atmjava.activities.LogAFOFlightPlan";
		}
								
		java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
		}
		
		java println(string message) { 
				class: "brahms.base.system.PrintlnActivity"; 
		}
		
		primitive_activity delaySimulatorClock(int val) {
		max_duration: val;
		}
		
		communicate sendAFOData(AFO i_AFO, Controller i_controller) {
			max_duration: 0;
			with: i_controller;
			about:
				// airspeed, latitude, longitude, altitude, name, and waypoint it is flying to
				send(i_AFO.Name = unknown),
				send(i_AFO.m_iAirSpeed = unknown),
				send(i_AFO.m_dVerticalSpeed = unknown),
    		send(i_AFO.m_dLatitude = unknown),
    		send(i_AFO.m_dLongitude = unknown),
    		send(i_AFO.m_dAltitude = unknown),
				send(i_AFO.toWaypoint = unknown),
				send(i_AFO.ETA = unknown),
				send(i_AFO.controller = unknown),
				send(i_AFO.is_Metering = unknown);
			when: end;
		}
				
	workframes:
		
		// Planes only start flying _in all cases_ based on the
		// start time and _not_ the status! Critical to keep 
		// all values in sync.
		workframe beginFlying {
		repeat : false;
		priority : 1;
		variables:
		  forone(int) i_TIME_INCREMENT;
		detectables:
		when(
			knownval(current.startTime <= globalClock.time)
			and knownval(current.iStatus = 0)
			and knownval(i_TIME_INCREMENT = 1)
			) do {
				// Updates the following attributes:
				//    - i_AFO.m_iAirSpeed 
				//    - i_AFO.m_dLatitude
				//    - i_AFO.m_dLongitude
				//    - i_AFO.m_dAltitude
				//    - i_AFO.iCurrentSegment
				//    - i_AFO.toWaypoint
				//    - i_AFO.iStatus
				//    - i_AFO.ETA
				//    - i_AFO.controller
        //    - i_AFO.m_iTimeStamp
				initializeAFO(current, i_TIME_INCREMENT);
				logAFOFlightPlan(current);
			}
		}


		// Make these workframes mutually exclusive.
		workframe Fly_To_Waypoint {
			repeat: true;
			priority: 1;
			variables:
				forone(AFO) i_AFO;
				forone(Controller) i_controller;
				forone(int) i_TIME_INCREMENT;
			detectables:
			when(
				knownval(i_AFO = current) and
				knownval(i_AFO.iStatus = 1) and
				knownval(i_controller = i_AFO.controller) and
				// TODO: use a constant for i_TIME_INCREMENT--find all all-cap attributes and connect to constants!
				knownval(i_TIME_INCREMENT = 1) and
				knownval(i_TIME_INCREMENT <= globalClock.time - i_AFO.m_iTimeStamp)
			)
			do {
				// Updates the following attributes:
				//    - i_AFO.m_iAirSpeed 
				//    - i_AFO.m_dLatitude
				//    - i_AFO.m_dLongitude
				//    - i_AFO.m_dAltitude
				//    - i_AFO.iCurrentSegment
				//    - i_AFO.toWaypoint
				//    - i_AFO.iStatus
				//    - i_AFO.ETA
				//    - i_AFO.controller
        //    - i_AFO.m_iTimeStamp
				updateAFOPosition(i_AFO, i_TIME_INCREMENT);
				sendAFOData(i_AFO, i_controller);			
			}
		}//wf Track_GPS_To_Waypoint 
		

	thoughtframes:
	
	
  thoughtframe updateFlyingBeliefFalse{
   when(knownval(current.iStatus = 0) and
				knownval (current.flying = true))
   do {
     conclude((current.flying = false)); 
   }
  }
 
  thoughtframe updateFlyingBeliefTrue {
  when(knownval(current.iStatus = 1) and
       knownval(current.flying = false))
   do {
     conclude((current.flying = true));
   }
  } 

}
