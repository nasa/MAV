/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.objects;

class Constants {
	
}

object seperationConstants instanceof Constants {

	attributes: 
	public int ARR_ARR_MIN;
	public int ARR_DEP_MIN;
	public double DEP_ARR_MIN;
	public int DEP_DEP_MIN;
	public int SINGLE;
	public int DOUBLE;
	public int TRIPLE;
	public int B757;
	
	initial_beliefs:
	
	// Minimum separation between two arrivals in seconds
	(current.ARR_ARR_MIN = 72);
	// Minimum separation between arrival - departure in seconds
	(current.ARR_DEP_MIN = 17);
	// Minimum separation between departure - arrival in seconds
	(current.DEP_ARR_MIN = 40.0);
	// Minimum separation between departure - departure in seconds
	(current.DEP_DEP_MIN = 50);
	// Min separation between arrivals to allow for single departure	
	(current.SINGLE = 75);
	// Arrival spacing for double
	(current.DOUBLE = 135); 
	// Arrival spacing for triple
	(current.TRIPLE = 195);
	// Arrival spacing for B757
	(current.B757 = 210);
	
	initial_facts:
	
	(current.ARR_ARR_MIN = 72);
	(current.ARR_DEP_MIN = 17);
	(current.DEP_ARR_MIN = 40.0);
	(current.DEP_DEP_MIN = 50);
	// Min separation between arrivals to allow for single departure	
	(current.SINGLE = 75);
	// Arrival spacing for double
	(current.DOUBLE = 135); 
	// Arrival spacing for triple
	(current.TRIPLE = 195);
	// Arrival spacing for B757
	(current.B757 = 210);
	
	activities:
	
		broadcast constantValues() {
						random: false;
						max_duration: 0;
						about: 
							send(current.DEP_DEP_MIN = current.DEP_DEP_MIN),
							send(current.ARR_DEP_MIN = current.ARR_DEP_MIN),
							send(current.DEP_ARR_MIN = current.DEP_ARR_MIN);
						when: end;	 
		}
		
	workframes: 
	
	workframe broadcastConstantValues {
	repeat: false;
	priority: 1;
	when( ) 
	do {
		constantValues();
	}
	
	}

}


