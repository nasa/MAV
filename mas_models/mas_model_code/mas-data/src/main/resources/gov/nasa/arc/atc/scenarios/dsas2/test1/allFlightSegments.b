/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.scenarios.dsas2test1.objects;

object E_TO_F_A1 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = F);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 280.0);
  initial_facts:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = F);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 280.0);
}

object F_TO_G_A1 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = F);
  	(current.toWaypoint = G);
  	(current.end_altitude = 2500.0);
  	(current.end_speed = 250.0);
  initial_facts:
  	(current.fromWaypoint = F);
  	(current.toWaypoint = G);
  	(current.end_altitude = 2500.0);
  	(current.end_speed = 250.0);
}

object G_TO_H_A1 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = G);
  	(current.toWaypoint = H);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 230.0);
  initial_facts:
  	(current.fromWaypoint = G);
  	(current.toWaypoint = H);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 230.0);
}

object H_TO_R_A1 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = R);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 220.0);
  initial_facts:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = R);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 220.0);
}


object E_TO_F_A2 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = F);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 280.0);
  initial_facts:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = F);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 280.0);
}

object F_TO_G_A2 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = F);
  	(current.toWaypoint = G);
  	(current.end_altitude = 2500.0);
  	(current.end_speed = 250.0);
  initial_facts:
  	(current.fromWaypoint = F);
  	(current.toWaypoint = G);
  	(current.end_altitude = 2500.0);
  	(current.end_speed = 250.0);
}

object G_TO_H_A2 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = G);
  	(current.toWaypoint = H);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 230.0);
  initial_facts:
  	(current.fromWaypoint = G);
  	(current.toWaypoint = H);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 230.0);
}

object H_TO_R_A2 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = R);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 220.0);
  initial_facts:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = R);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 220.0);
}


object E_TO_F_A3 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = F);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 280.0);
  initial_facts:
  	(current.fromWaypoint = E);
  	(current.toWaypoint = F);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 280.0);
}

object F_TO_G_A3 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = F);
  	(current.toWaypoint = G);
  	(current.end_altitude = 2500.0);
  	(current.end_speed = 250.0);
  initial_facts:
  	(current.fromWaypoint = F);
  	(current.toWaypoint = G);
  	(current.end_altitude = 2500.0);
  	(current.end_speed = 250.0);
}

object G_TO_H_A3 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = G);
  	(current.toWaypoint = H);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 230.0);
  initial_facts:
  	(current.fromWaypoint = G);
  	(current.toWaypoint = H);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 230.0);
}

object H_TO_R_A3 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = R);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 220.0);
  initial_facts:
  	(current.fromWaypoint = H);
  	(current.toWaypoint = R);
  	(current.end_altitude = 0.0);
  	(current.end_speed = 220.0);
}


object R_TO_A_D1 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = R);
  	(current.toWaypoint = A);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 220.0);
  initial_facts:
  	(current.fromWaypoint = R);
  	(current.toWaypoint = A);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 220.0);
}


object A_TO_B_D1 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 270.0);
  initial_facts:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 270.0);
}


object R_TO_A_D2 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = R);
  	(current.toWaypoint = A);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 220.0);
  initial_facts:
  	(current.fromWaypoint = R);
  	(current.toWaypoint = A);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 220.0);
}


object A_TO_B_D2 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 270.0);
  initial_facts:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 270.0);
}


object R_TO_A_D3 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = R);
  	(current.toWaypoint = A);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 220.0);
  initial_facts:
  	(current.fromWaypoint = R);
  	(current.toWaypoint = A);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 220.0);
}


object A_TO_B_D3 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 270.0);
  initial_facts:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 270.0);
}


object R_TO_A_D4 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = R);
  	(current.toWaypoint = A);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 220.0);
  initial_facts:
  	(current.fromWaypoint = R);
  	(current.toWaypoint = A);
  	(current.end_altitude = 2000.0);
  	(current.end_speed = 220.0);
}


object A_TO_B_D4 instanceof flightSegment { 
  initial_beliefs:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 270.0);
  initial_facts:
  	(current.fromWaypoint = A);
  	(current.toWaypoint = B);
  	(current.end_altitude = 5000.0);
  	(current.end_speed = 270.0);
}
