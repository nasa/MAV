package gov.nasa.arc.atm.atmmodel.objects;

object SimulationControl instanceof Constants {
	attributes:
	
		public int		START_TIME;
		public int 	TIME_INCREMENT;		// Time increment to run simulation

	initial_beliefs:
	
		(current.START_TIME		= 0);
		(current.TIME_INCREMENT 	= 1);	
	
	initial_facts:

		(current.START_TIME		= 0);
		(current.TIME_INCREMENT 	= 1);	
	
}
