object DEPA_TO_A_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = DEPA);
  	(current.toWaypoint = A);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = DEPA);
  	(current.toWaypoint = A);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object DEPA_TO_B_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}


object B_TO_C_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = B);
  	(current.toWaypoint = C);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = B);
  	(current.toWaypoint = C);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}


object C_TO_D_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = C);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = C);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}
