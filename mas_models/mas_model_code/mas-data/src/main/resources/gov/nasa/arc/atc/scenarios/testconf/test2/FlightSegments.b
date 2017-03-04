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

object DEPB_TO_B_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = DEPB);
  	(current.toWaypoint = B);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = DEPB);
  	(current.toWaypoint = B);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object DEPC_TO_C_S3 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = DEPC);
  	(current.toWaypoint = C);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = DEPC);
  	(current.toWaypoint = C);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object A_TO_MERGE_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = MERGE);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = MERGE);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object B_TO_MERGE_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = B);
  	(current.toWaypoint = MERGE);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = B);
  	(current.toWaypoint = MERGE);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}


object C_TO_MERGE_S3 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = C);
  	(current.toWaypoint = MERGE);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = C);
  	(current.toWaypoint = MERGE);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object MERGE_TO_D_S1 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = MERGE);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = MERGE);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}

object MERGE_TO_D_S2 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = MERGE);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = MERGE);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}


object MERGE_TO_D_S3 instanceof FlightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = MERGE);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
  initial_facts:
  	(current.fromWaypoint = MERGE);
  	(current.toWaypoint = D);
  	(current.end_altitude = 10000.0);
  	(current.end_speed = 450.0);
}
