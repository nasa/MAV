object MRS instanceof Airport {
  initial_beliefs:
 	(current.latitude = 40);
 	(current.longitude = 70);
  initial_facts:
 	(current.latitude = 40);
 	(current.longitude = 70);
}

object R instanceof Runway {
  initial_beliefs:
 	(current.qfu = 17);
 	(current.airport = MRS);
  initial_facts:
 	(current.qfu = 17);
 	(current.airport = MRS);
}
