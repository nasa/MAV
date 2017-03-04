package gov.nasa.arc.atm.atmmodel.objects;

object AFOStatus instanceof Constants {
	attributes:
		public int		IS_FLYING; // Status of the aircraft is flying
		public int		ON_GROUND; // Status of the aircraft is on ground
		public int		FINISHED;  // Status of the aircraft is finished
		public int		NO_STA;    // A big number, indicating flight not yet sheduled
		public int		NO_STD;
	
	initial_beliefs:
		(current.IS_FLYING = 1);
		(current.ON_GROUND = 2);	
		(current.FINISHED = 3);
		(current.NO_STA	= 8888888);
		(current.NO_STD = 0);
	
	initial_facts:

		(current.IS_FLYING = 1);
		(current.ON_GROUND = 2);	
		(current.FINISHED = 3);
		(current.NO_STA	= 8888888);
		(current.NO_STD = 0);

}
