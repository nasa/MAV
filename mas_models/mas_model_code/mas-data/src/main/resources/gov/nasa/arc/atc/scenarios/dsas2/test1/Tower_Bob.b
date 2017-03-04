agent Tower_Bob memberof Tower {
  initial_beliefs:
(current hasWaypoint R);
	(RDepartures.departure_queue(1) = plane_D1 );
	(RDepartures.departure_queue(2) = plane_D2 );
	(RDepartures.departure_queue(1) = plane_D3 );
	(RDepartures.departure_queue(2) = plane_D4 );
  initial_facts:
 (current hasWaypoint R);
	(RDepartures.departure_queue(1) = plane_D1 );
	(RDepartures.departure_queue(2) = plane_D2 );
	(RDepartures.departure_queue(1) = plane_D3 );
	(RDepartures.departure_queue(2) = plane_D4 );
}

