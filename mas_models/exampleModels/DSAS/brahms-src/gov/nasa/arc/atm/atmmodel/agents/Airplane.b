/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.agents;


group Airplane memberof AFO {
	attributes:	

	public boolean landed;       // Used in clock.b (verify for usefulness)
		
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

		communicate sendPlaneInfoToController(Controller controller, Airplane i_AFO) {
			max_duration: 0;
			with: controller;
			about:
				send(i_AFO.Name = unknown),
				send(controller newPlane i_AFO is true);
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
				conclude((i_AFO.controller newPlane i_AFO));
				sendPlaneInfoToController(i_Controller, i_AFO);
			}	
		}
}
