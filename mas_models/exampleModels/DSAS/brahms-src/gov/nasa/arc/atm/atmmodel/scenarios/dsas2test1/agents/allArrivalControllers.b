/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.scenarios.dsas2test1.agents;

 
agent Tower_Bob memberof Tower {
  initial_beliefs:
 	(current hasWaypoint R);
	(current.handoffTo = Tracon_Alfred);
	(RDepartures.departure_queue(1) = plane_D1 );
	(RDepartures.departure_queue(2) = plane_D2 );
	(RDepartures.departure_queue(1) = plane_D3 );
	(RDepartures.departure_queue(2) = plane_D4 );
  initial_facts:
 	(current hasWaypoint R);
	(current.handoffTo = Tracon_Alfred);
}

agent Tracon_Alfred memberof Tracon {
  initial_beliefs:
 	(current hasWaypoint E);
 	(current hasWaypoint F);
 	(current hasWaypoint G);
 	(current hasWaypoint H);
	(current.handoffWaypoint = H);
	(current.handoffTo = Tower_Bob);
  initial_facts:
 	(current hasWaypoint E);
 	(current hasWaypoint F);
 	(current hasWaypoint G);
 	(current hasWaypoint H);
	(current.handoffWaypoint = H);
	(current.handoffTo = Tower_Bob);
}

agent ZNY_118 memberof Center {
	 initial_beliefs:

	 initial_facts:

}

