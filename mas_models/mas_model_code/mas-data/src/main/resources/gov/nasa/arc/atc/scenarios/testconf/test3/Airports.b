object LGA instanceof Airport {
  initial_beliefs:
 	(current.latitude = 40.77725);
 	(current.longitude = 73.872611);
  initial_facts:
 	(current.latitude = 40.77725);
 	(current.longitude = 73.872611);
}

object LGA22 instanceof Runway {
  initial_beliefs:
 	(current.qfu = 22);
 	(current.airport = LGA);
  initial_facts:
 	(current.qfu = 22);
 	(current.airport = LGA);
}


object LGA4 instanceof Runway {
  initial_beliefs:
 	(current.qfu = 4);
 	(current.airport = LGA);
  initial_facts:
 	(current.qfu = 4);
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

object JFK instanceof Airport {
  initial_beliefs:
 	(current.latitude = 40.639722);
 	(current.longitude = 73.778889);
  initial_facts:
 	(current.latitude = 40.639722);
 	(current.longitude = 73.778889);
}

object JFK4L instanceof Runway {
  initial_beliefs:
 	(current.qfu = 4);
 	(current.airport = JFK);
  initial_facts:
 	(current.qfu = 4);
 	(current.airport = JFK);
}


object JFK4R instanceof Runway {
  initial_beliefs:
 	(current.qfu = 4);
 	(current.airport = JFK);
  initial_facts:
 	(current.qfu = 4);
 	(current.airport = JFK);
}

object JFK22R instanceof Runway {
  initial_beliefs:
 	(current.qfu = 22);
 	(current.airport = JFK);
  initial_facts:
 	(current.qfu = 22);
 	(current.airport = JFK);
}

object JFK22L instanceof Runway {
  initial_beliefs:
 	(current.qfu = 22);
 	(current.airport = JFK);
  initial_facts:
 	(current.qfu = 22);
 	(current.airport = JFK);
}


object JFK13L instanceof Runway {
  initial_beliefs:
 	(current.qfu = 13);
 	(current.airport = JFK);
  initial_facts:
 	(current.qfu = 13);
 	(current.airport = JFK);
}

object JFK13R instanceof Runway {
  initial_beliefs:
 	(current.qfu = 13);
 	(current.airport = JFK);
  initial_facts:
 	(current.qfu = 13);
 	(current.airport = JFK);
}

object JFK31R instanceof Runway {
  initial_beliefs:
 	(current.qfu = 31);
 	(current.airport = JFK);
  initial_facts:
 	(current.qfu = 31);
 	(current.airport = JFK);
}


object JFK31L instanceof Runway {
  initial_beliefs:
 	(current.qfu = 31);
 	(current.airport = JFK);
  initial_facts:
 	(current.qfu = 31);
 	(current.airport = JFK);
}
