object wpDeparture_TO_A_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = wpDeparture);
  	(current.toWaypoint = A);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 250.0);
  initial_facts:
  	(current.fromWaypoint = wpDeparture);
  	(current.toWaypoint = A);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 250.0);
}

object A_TO_B_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 300.0);
  initial_facts:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 300.0);
}


object B_TO_E_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = B);
  	(current.toWaypoint = E);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 350.0);
  initial_facts:
  	(current.fromWaypoint = B);
  	(current.toWaypoint = E);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 350.0);
}


object E_TO_H_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = H);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 350.0);
  initial_facts:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = H);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 350.0);
}


object H_TO_I_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = I);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 300.0);
  initial_facts:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = I);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 300.0);
}

object I_TO_wpArrival_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = I);
  	(current.toWaypoint = wpArrival);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 250.0);
  initial_facts:
  	(current.fromWaypoint = I);
  	(current.toWaypoint = wpArrival);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 250.0);
}

object wpDeparture_TO_C_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = wpDeparture);
  	(current.toWaypoint = C);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 250.0);
  initial_facts:
  	(current.fromWaypoint = wpDeparture);
  	(current.toWaypoint = C);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 250.0);
}

object C_TO_D_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = C);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 300.0);
  initial_facts:
  	(current.fromWaypoint = C);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 300.0);
}


object D_TO_E_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = D);
  	(current.toWaypoint = E);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 350.0);
  initial_facts:
  	(current.fromWaypoint = D);
  	(current.toWaypoint = E);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 350.0);
}


object E_TO_H_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = H);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 350.0);
  initial_facts:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = H);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 350.0);
}


object H_TO_I_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = I);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 300.0);
  initial_facts:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = I);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 300.0);
}


object I_TO_wpArrival_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = I);
  	(current.toWaypoint = wpArrival);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 250.0);
  initial_facts:
  	(current.fromWaypoint = I);
  	(current.toWaypoint = wpArrival);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 250.0);
}
