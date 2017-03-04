agent plane_S1 memberof Airplane {
  initial_beliefs:
	(current.latitude = 39.5);
	(current.longitude = 70);
	(current.landed = false);
	(current.myFlightPlan = FlightPlan_S1);
	(current.startTime = 2);
	(current.currentFlightSegment = wpDeparture_TO_A_S1);
	(current.speed = 250);
	(current.desired_speed = 250);
	(current.altitude = 0);
	(current.desired_altitude = 0);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
  initial_facts:
	(current.latitude = 39.5);
	(current.longitude = 70);
	(current.landed = false);
	(current.myFlightPlan = FlightPlan_S1);
	(current.startTime = 2);
	(current.currentFlightSegment = wpDeparture_TO_A_S1);
	(current.speed = 250);
	(current.desired_speed = 250);
	(current.altitude = 0);
	(current.desired_altitude = 0);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
}

agent plane_S2 memberof Airplane {
  initial_beliefs:
	(current.latitude = 39.5);
	(current.longitude = 70);
	(current.landed = false);
	(current.myFlightPlan = FlightPlan_S2);
	(current.startTime = 4);
	(current.currentFlightSegment = wpDeparture_TO_C_S2);
	(current.speed = 250);
	(current.desired_speed = 250);
	(current.altitude = 0);
	(current.desired_altitude = 0);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
  initial_facts:
	(current.latitude = 39.5);
	(current.longitude = 70);
	(current.landed = false);
	(current.myFlightPlan = FlightPlan_S2);
	(current.startTime = 4);
	(current.currentFlightSegment = wpDeparture_TO_C_S2);
	(current.speed = 250);
	(current.desired_speed = 250);
	(current.altitude = 0);
	(current.desired_altitude = 0);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
}
