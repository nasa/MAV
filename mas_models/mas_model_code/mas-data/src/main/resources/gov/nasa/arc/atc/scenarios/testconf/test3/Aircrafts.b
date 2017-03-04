agent plane_S1 memberof Airplane {
  initial_beliefs:
	(current.latitude = 38.6);
	(current.longitude = 70.2);
	(current.landed = false);
	(current.myFlightPlan = FlightPlan_S1);
	(current.startTime = 10);
	(current.currentFlightSegment = C_TO_D_S1);
	(current.speed = 450);
	(current.desired_speed = 450);
	(current.altitude = 10000);
	(current.desired_altitude = 10000);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
  initial_facts:
	(current.latitude = 38.6);
	(current.longitude = 70.2);
	(current.landed = false);
	(current.myFlightPlan = FlightPlan_S1);
	(current.startTime = 10);
	(current.currentFlightSegment = C_TO_D_S1);
	(current.speed = 450);
	(current.desired_speed = 450);
	(current.altitude = 10000);
	(current.desired_altitude = 10000);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
}
