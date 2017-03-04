object AirPortArrival instanceof Airport {
  initial_beliefs:
 	(current.latitude = 37.5);
 	(current.longitude = 70);
  initial_facts:
 	(current.latitude = 37.5);
 	(current.longitude = 70);
}

object wpArrival instanceof Runway {
  initial_beliefs:
 	(current.qfu = 22);
 	(current.airport = AirPortArrival);
  initial_facts:
 	(current.qfu = 22);
 	(current.airport = AirPortArrival);
}


