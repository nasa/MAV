object LGA instanceof Airport {
  initial_beliefs:
 	(current.latitude = 40.78528);
 	(current.longitude = 73.87055);
  initial_facts:
 	(current.latitude = 40.78528);
 	(current.longitude = 73.87055);
}

object LGA22 instanceof Runway {
  initial_beliefs:
 	(current.qfu = 22);
 	(current.airport = LGA);
  initial_facts:
 	(current.qfu = 22);
 	(current.airport = LGA);
}

object LGA04 instanceof Runway {
  initial_beliefs:
 	(current.qfu = 04);
 	(current.airport = LGA);
  initial_facts:
 	(current.qfu = 04);
 	(current.airport = LGA);
}

object LGA13 instanceof Runway {
  initial_beliefs:
 	(current.qfu = 13);
 	(current.airport = LGA);
  initial_facts:
 	(current.qfu = 13);
 	(current.airport = LGA);
}
object LGA31 instanceof Runway {
  initial_beliefs:
 	(current.qfu = 31);
 	(current.airport = LGA);
  initial_facts:
 	(current.qfu = 31);
 	(current.airport = LGA);
}
