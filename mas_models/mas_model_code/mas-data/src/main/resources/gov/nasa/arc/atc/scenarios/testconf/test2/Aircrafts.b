agent plane_S1 memberof Airplane {
  initial_beliefs:
	(current.m_dLatitude = 39.001);
	(current.m_dLongitude = 71.001);
	(current.landed = false);
	(current.flightPlan = FlightPlan_S1);
	(current.startTime = 10);
	(current.iCurrentSegment = 2);
	(current.m_iAirSpeed = 450);
	(current.desired_speed = 450);
	(current.m_dAltitude = 10000);
	(current.desired_altitude = 10000);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
  initial_facts:
	(current.m_dLatitude = 39.001);
	(current.m_dLongitude = 71.001);
	(current.landed = false);
	(current.flightPlan = FlightPlan_S1);
	(current.startTime = 10);
	(current.iCurrentSegment = 2);
	(current.m_iAirSpeed = 450);
	(current.desired_speed = 450);
	(current.m_dAltitude = 10000);
	(current.desired_altitude = 10000);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
}

agent plane_S2 memberof Airplane {
  initial_beliefs:
	(current.m_dLatitude = 39.389);
	(current.m_dLongitude = 70.5);
	(current.landed = false);
	(current.flightPlan = FlightPlan_S2);
	(current.startTime = 12);
	(current.iCurrentSegment = 2);
	(current.m_iAirSpeed = 450);
	(current.desired_speed = 50);
	(current.m_dAltitude = 10000);
	(current.desired_altitude = 10000);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
  initial_facts:
	(current.m_dLatitude = 39.389);
	(current.m_dLongitude = 70.5);
	(current.landed = false);
	(current.flightPlan = FlightPlan_S2);
	(current.startTime = 12);
	(current.iCurrentSegment = 2);
	(current.m_iAirSpeed = 450);
	(current.desired_speed = 50);
	(current.m_dAltitude = 10000);
	(current.desired_altitude = 10000);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
}

agent plane_S3 memberof Airplane {
  initial_beliefs:
	(current.m_dLatitude = 39);
	(current.m_dLongitude = 69.99);
	(current.landed = false);
	(current.flightPlan = FlightPlan_S3);
	(current.startTime = 14);
	(current.iCurrentSegment = 1);
	(current.m_iAirSpeed = 450);
	(current.desired_speed = 50);
	(current.m_dAltitude = 10000);
	(current.desired_altitude = 10000);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
  initial_facts:
	(current.m_dLatitude = 39);
	(current.m_dLongitude = 69.99);
	(current.landed = false);
	(current.flightPlan = FlightPlan_S3);
	(current.startTime = 14);
	(current.iCurrentSegment = 1);
	(current.m_iAirSpeed = 450);
	(current.desired_speed = 50);
	(current.m_dAltitude = 10000);
	(current.desired_altitude = 10000);
	(current.rate_of_ascention = 0);
	(current.rate_of_acceleration = 0);
	(current.is_departure = false);
}
