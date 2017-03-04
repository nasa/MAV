object DEP_TO_A_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = DEP);
  	(current.toWaypoint = A);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = DEP);
  	(current.toWaypoint = A);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object DEP_TO_B_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = DEP);
  	(current.toWaypoint = B);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = DEP);
  	(current.toWaypoint = B);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object A_TO_C_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = C);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = A);
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

object D_TO_E_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = D);
  	(current.toWaypoint = E);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = D);
  	(current.toWaypoint = E);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object B_TO_C_S2 instanceof FlightSegment { 
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

object C_TO_D_S2 instanceof FlightSegment { 
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

object D_TO_E_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = D);
  	(current.toWaypoint = E);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = D);
  	(current.toWaypoint = E);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}
