package gov.nasa.arc.atm.atmmodel.scenarios.small2plane2depart.agents;

agent ZNY_118  memberof Tower {
  initial_beliefs:
(current hasWaypoint LGA22);
	(laGuardiaDepts.departure_queue(1) = plane_ASQ5573 );
	(laGuardiaDepts.departure_queue(2) = plane_TCF5584 );
	(current.handoffWaypoint = KWANN);
	(current.handoffTo = ZNY_118);
  initial_facts:
(current hasWaypoint LGA22);
	(laGuardiaDepts.departure_queue(1) = plane_ASQ5573 );
	(laGuardiaDepts.departure_queue(2) = plane_TCF5584 );
	(current.handoffWaypoint = KWANN);
	(current.handoffTo = ZNY_118);
}
