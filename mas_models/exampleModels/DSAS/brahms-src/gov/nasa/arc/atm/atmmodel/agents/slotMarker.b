/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.agents;

group slotMarker memberof AFO{
	attributes:	

	relations:
	
	initial_beliefs:
	
	initial_facts:

	activities:

		java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
		}
		
		java println(string message) { 
				class: "brahms.base.system.PrintlnActivity"; 
		}

		communicate sendSlotInfoToController(Controller controller, slotMarker i_slot) {
			max_duration: 0;
			with: controller;
			about:
				send(i_slot.Name = unknown),
				send(controller newSlot i_slot is true);
			when: end;
		}

	workframes:		

		workframe sendSlotInfoToController {
			repeat: false;
			priority: 1;
			variables: 
				forone (slotMarker) i_Slot;
				forone (Controller) i_Controller;
			when (
			knownval(i_Slot = current)
			and knownval (i_Slot.controller = i_Controller)
			) do {
			//println("sending slot to controller");
			//printBelief(i_Slot, "Name", attribute);
			//printBelief(i_Slot, "controller", attribute);
			conclude((i_Slot.controller newSlot i_Slot));
			sendSlotInfoToController(i_Controller, i_Slot);
			//printBelief(i_Controller, "newSlot", "relation");
			}
		} 	
}

