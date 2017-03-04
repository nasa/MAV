/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.agents;

group Controller{
	attributes:
		
		protected Controller handoffTo;
		protected waypoint handoffWaypoint;
		protected int m_iTimeStamp;
		public int totalClearances;
		
	relations:
	
		public Airplane hasPlane;
		public slotMarker hasSlot;
		protected waypoint hasWaypoint;
		
		public Airplane newPlane;
		public slotMarker newSlot;
				
		public Airplane isMetering;
		
	initial_beliefs:
		
		(current.m_iTimeStamp = 0);
		(current.totalClearances = 0);
		//(current hasPlane unknown);
		
	initial_facts:

		(current.m_iTimeStamp = 0);
		(current.totalClearances = 0);
		//(current hasPlane unknown);
	
	activities:
		
		java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
	 	}
		
		java println(string message) { 
				class: "brahms.base.system.PrintlnActivity"; 
		}
		
		java printHandOff(int i_Time, Concept i_Controller,Concept i_Airplane ) { 
				class: "gov.nasa.arc.brahms.atmjava.activities.PrintHandOff"; 
		}
		
		java initializeController(Controller iController) {	
			class: "gov.nasa.arc.brahms.atmjava.activities.InitializeController";
		}
		
		communicate receiveAFOData(AFO i_AFO) {
			max_duration: 0;
			with: i_AFO;
			about:
				// airspeed, latitude, longitude, altitude, name, and waypoint it is flying to
				receive(i_AFO.Name = unknown),
				receive(i_AFO.m_iAirSpeed = unknown),
				receive(i_AFO.m_dVerticalSpeed = unknown),
    		receive(i_AFO.m_dLatitude = unknown),
    		receive(i_AFO.m_dLongitude = unknown),
    		receive(i_AFO.m_dAltitude = unknown),
				receive(i_AFO.toWaypoint = unknown),
				receive(i_AFO.ETA = unknown),
				receive(i_AFO.controller = unknown);
			when: end;
		}
		
		communicate updateHandOffController(Controller controller, Airplane i_Airplane, slotMarker i_slotMarker) {
			max_duration: 8;
			with: controller;
			about:
				send(controller newPlane i_Airplane is true),
				send(controller newSlot i_slotMarker is true),
				send(i_Airplane.Name = unknown),
				send(i_slotMarker.Name = unknown);
			when: end;
		}
	
		communicate updateAirplane(Controller i_Controller, Airplane i_Airplane) {
			max_duration: 8;
			with: i_Airplane;
			about:
				send(i_Airplane.controller = i_Controller);
			when: end;	
		}

		communicate updateSlotMarker(Controller i_Controller, slotMarker i_slotMarker) {
			max_duration: 4;
			with: i_slotMarker;
			about:
				send(i_slotMarker.controller = i_Controller);
			when: end;	
		}
		
		communicate sendNewAirspeed(Airplane i_Airplane) {
			max_duration: 0;
			with: i_Airplane;
			about:
				send(i_Airplane.m_iAirSpeed = unknown),
				send(i_Airplane.is_Metering = unknown);
			when: end;
		}
		
	workframes:
	
	workframe init {
		repeat : false;
		priority : 2;
		variables:
		detectables:
		when(
			knownval(current.m_iTimeStamp <= globalClock.time)) 
			do {
				// log init status
				initializeController(current);
			}
		}
				
		// Workframe: when the plane is headed to the hand-off, put it into the 
		// "newplane" relation for the hand-off controller. 
		workframe sendHandOff {
			repeat: true;
			priority: 1;
			variables:
				forone(Controller) i_Controller;
				forone(Airplane) i_Airplane;
				forone(slotMarker) i_slotMarker;
				forone(waypoint) toWaypoint;
				forone(waypoint) handOffWaypoint;
				forone(Controller) handOffController;
				forone(int) i_Time;
			detectables:
			when(
				knownval(i_Controller = current)
				and knownval(i_Controller hasPlane i_Airplane)
				and knownval(i_Controller hasSlot i_slotMarker)
				and knownval(i_Airplane.Name = i_slotMarker.Name)
				and knownval(toWaypoint = i_Airplane.toWaypoint) 
				and knownval(handOffWaypoint = i_Controller.handoffWaypoint)
				and knownval(toWaypoint = handOffWaypoint)
				and knownval(handOffController = i_Controller.handoffTo)
				and not(handOffController = i_Controller)
				and knownval(i_Time = globalClock.time))
			do {
				printHandOff(i_Time,i_Controller,i_Airplane);
				conclude((handOffController newPlane i_Airplane is true));
				conclude((handOffController newSlot i_slotMarker is true)); 
				updateHandOffController(handOffController, i_Airplane, i_slotMarker);
				conclude((i_Controller hasPlane i_Airplane is false));
				conclude((i_Controller hasSlot i_slotMarker is false));
				conclude((i_Controller isMetering i_Airplane is false));
			}
		}

		// Workframe: when a plane is in the newplane relation, move it to the hasplane (hasSlot)
		// relation and remove it from the controller that sent it. 
		workframe receiveHandOff {
			repeat: true;
			priority: 1;
			variables:
				forone(Controller) i_Controller;
				forone(Airplane) i_Airplane;
				forone(slotMarker) i_slotMarker;
			detectables:
			when(
				knownval(i_Controller = current) and
				knownval(i_Controller newPlane i_Airplane) and
				knownval(i_Controller newSlot i_slotMarker) and
				knownval(i_Airplane.Name = i_slotMarker.Name))
			do {
				conclude((i_Controller hasPlane i_Airplane));
				conclude((i_Controller hasSlot i_slotMarker));
				conclude((i_Controller newPlane i_Airplane is false));
				conclude((i_Controller newSlot i_slotMarker is false));
				conclude((i_Controller isMetering i_Airplane is false));	
				// Must update both the airplane and slot with the controller
				// information because the AFO is what sends the controller
				// the current plane position, speed, etc.
				conclude((i_Airplane.controller = i_Controller));
				conclude((i_slotMarker.controller = i_Controller));
				updateAirplane(i_Controller, i_Airplane);
				updateSlotMarker(i_Controller, i_slotMarker);
				println("receiveHandOff");
				printBelief(i_Controller, "hasPlane", "relation");
			
			}
		}						
		

		workframe meterToSlot {
			repeat: true;
			priority: 1;
			variables:
				forone(Controller) i_Controller;
				forone(Airplane) i_Airplane;
				forone(slotMarker) i_slotMarker;
				forone(double) ETA_Airplane;
				forone(double) ETA_slotMarker;
				forone(int) airspeed_Airplane;
				forone(int) newAirspeed;
				forone(double) separation;
			detectables:
			when(
				knownval(i_Controller = current)
				and knownval(i_Controller hasPlane i_Airplane)
				and knownval(i_Controller isMetering i_Airplane is false)
				and knownval(i_Controller hasSlot i_slotMarker)
				and knownval(i_Airplane.Name = i_slotMarker.Name)
				and knownval(ETA_Airplane = i_Airplane.ETA)
				and knownval(ETA_slotMarker = i_slotMarker.ETA)
				and knownval(separation = ETA_slotMarker - ETA_Airplane) 
				and knownval(separation > 10.0)
				and knownval(airspeed_Airplane = i_Airplane.m_iAirSpeed)
				and knownval(newAirspeed = airspeed_Airplane - 40)
				)
			do {				
				conclude((i_Airplane.m_iAirSpeed = newAirspeed));
				conclude((i_Airplane.is_Metering = 1));
				sendNewAirspeed(i_Airplane);
				conclude((i_Controller isMetering i_Airplane is true));
				conclude((current.totalClearances = current.totalClearances + 1));
				printBelief(current, "totalClearances", attribute);
			}
		}						
		
		workframe fixMeteringrelation {
			repeat: true;
			priority: 1;
			variables:
				forone(Controller) i_Controller;
				forone(Airplane) i_Airplane;
			detectables:
			when (
				knownval(i_Controller = current)
				and knownval(i_Controller hasPlane i_Airplane)
				and knownval(i_Airplane.is_Metering = 1)
				and knownval(i_Controller isMetering i_Airplane is false)
			)
			do {
				conclude((i_Controller isMetering i_Airplane is true));
			}
		}
		
			// Workframe: detects when a plane is in its slot and issues a return to STDOPs.
		workframe stopMeterToStot {
			repeat: true;
			priority: 1;
			variables:
				forone(Controller) i_Controller;
				forone(Airplane) i_Airplane;
				forone(slotMarker) i_slotMarker;
				forone(double) ETA_Airplane;
				forone(double) ETA_slotMarker;
				forone(int) airspeed;
				forone(double) separation;
			detectables:
			when(
				knownval(i_Controller = current)
				and knownval(i_Controller isMetering i_Airplane is true)
				and knownval(i_Controller hasPlane i_Airplane)
				and knownval(i_Controller hasSlot i_slotMarker)
				and knownval(i_Airplane.Name = i_slotMarker.Name)
				and knownval(ETA_Airplane = i_Airplane.ETA)
				and knownval(ETA_slotMarker = i_slotMarker.ETA)
				and knownval(separation = ETA_slotMarker - ETA_Airplane) 
				and knownval(separation <= 0.0)
				and knownval(airspeed = i_Airplane.m_iAirSpeed))
			do {
				conclude((i_Airplane.m_iAirSpeed = airspeed + 40));
				conclude((i_Airplane.is_Metering = 0));
				sendNewAirspeed(i_Airplane);
				conclude((i_Controller isMetering i_Airplane is false));
				conclude((current.totalClearances = current.totalClearances + 1));
				printBelief(current, "totalClearances", attribute);

			}
		}						
			 

}

		
