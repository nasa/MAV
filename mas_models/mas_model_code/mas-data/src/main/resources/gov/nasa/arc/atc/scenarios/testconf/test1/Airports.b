object ARR instanceof Airport {
  initial_beliefs:
 	(current.latitude = 38);
 	(current.longitude = 68);
  initial_facts:
 	(current.latitude = 38);
 	(current.longitude = 68);
}

object E instanceof Runway {
  initial_beliefs:
 	(current.qfu = 22);
 	(current.airport = ARR);
  initial_facts:
 	(current.qfu = 22);
 	(current.airport = ARR);
}

