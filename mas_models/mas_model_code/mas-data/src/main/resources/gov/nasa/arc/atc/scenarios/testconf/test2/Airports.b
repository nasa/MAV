object AirD instanceof Airport {
  initial_beliefs:
 	(current.latitude = 38);
 	(current.longitude = 70.5);
  initial_facts:
 	(current.latitude = 38);
 	(current.longitude = 70.5);
}

object D instanceof Runway {
  initial_beliefs:
 	(current.qfu = 07);
 	(current.airport = AirD);
  initial_facts:
 	(current.qfu = 07);
 	(current.airport = AirD);
}
