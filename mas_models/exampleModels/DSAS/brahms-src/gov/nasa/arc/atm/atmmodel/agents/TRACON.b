/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.agents;

group Tracon memberof Controller{
	initial_beliefs:
	
	activities:
		
		java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
		}
	workframes:


}
