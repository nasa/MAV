package gov.nasa.arc.brahms.week2;

import gov.nasa.arc.atm.atmmodel.agents.*;
import gov.nasa.arc.atm.atmmodel.objects.*;
import gov.nasa.arc.atm.atmmodel.scenarios.dsas2test1.*;
import gov.nasa.arc.atm.atmmodel.scenarios.dsas2test1.objects.*;
import gov.nasa.arc.atm.atmmodel.scenarios.dsas2test1.agents.*;

import otherClass;

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
		
}

agent slot_A1 memberof slotMarker {
  initial_beliefs:
	(current.Name = A1);
	(current.flightPlan = flightPlan_A1);
  initial_facts:
	(current.Name = A1);
	(current.flightPlan = flightPlan_A1);
}

object TSS instanceof timeLine{

}

group foo memberof bar, zaz{
      display : "Pilot Group";
      cost : 2.0;
	attributes:
		
		public string Name; //Name of the object

relations:
	
		public Airplane hasPlane;
		public slotMarker hasSlot;
		protected waypoint hasWaypoint;
		
		public Airplane newPlane;
		public slotMarker newSlot;
				
		public Airplane isMetering;

initial_beliefs:

		(current.m_iTimeStamp = 0);
		(current.flying = false);
		(current.is_Metering = 0);
		(current newSlot TSS);
		
	initial_facts:
		
		(current.m_iTimeStamp = 0);
		(current.flying = false);
		(current.is_Metering = 0);
		(current newSlot TSS); // this works syntatically not semantically

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

		workframe sendPlaneInfoToController {
			repeat: false;
			priority: 1;
			variables:
				forone (Airplane) i_AFO;
				forone (Controller) i_Controller;
			when(
				knownval(i_AFO = current)
				and knownval(i_AFO.iStatus = 1)
				and knownval(i_AFO.controller = i_Controller)
			) do {
				//println("sending plane to controller");
				//printBelief(i_AFO, "Name", attribute);
				//printBelief(i_AFO, "controller", attribute);
				conclude((i_AFO.controller newPlane i_AFO));
				sendPlaneInfoToController(i_Controller, i_AFO);
				//printBelief(i_Controller, "newPlane", "relation");
			}	
		}

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
area foo
areadef foo
path foo
