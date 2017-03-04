/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.objects;

object BearningIndicator instanceof Constants {

	attributes:
		
		public int NORTH;
		public int NORTH_NORTH_EAST;
		public int NORTH_EAST;
		public int EAST_NORTH_EAST;
  	public int EAST;
		public int EAST_SOUTH_EAST;
		public int SOUTH_EAST;
		public int SOUTH_SOUTH_EAST;
		public int SOUTH;
		public int SOUTH_SOUTH_WEST;
		public int SOUTH_WEST;
		public int WEST_SOUTH_WEST;
		public int WEST;
  	public int WEST_NORTH_WEST;
		public int NORTH_WEST;
		public int NORTH_NORTH_WEST;
	
	initial_beliefs:

		(current.NORTH = 1);
		(current.NORTH_NORTH_EAST = 2);
		(current.NORTH_EAST = 3);
		(current.EAST_NORTH_EAST = 4);
  	(current.EAST = 5);
		(current.EAST_SOUTH_EAST = 6);
		(current.SOUTH_EAST = 7);
		(current.SOUTH_SOUTH_EAST = 8);
		(current.SOUTH = 9);
		(current.SOUTH_SOUTH_WEST = 10);
		(current.SOUTH_WEST = 11);
		(current.WEST_SOUTH_WEST = 12);
		(current.WEST = 13);
  	(current.WEST_NORTH_WEST = 14);
		(current.NORTH_WEST = 15);
		(current.NORTH_NORTH_WEST = 16);

	initial_facts:

		(current.NORTH = 1);
		(current.NORTH_NORTH_EAST = 2);
		(current.NORTH_EAST = 3);
		(current.EAST_NORTH_EAST = 4);
  	(current.EAST = 5);
		(current.EAST_SOUTH_EAST = 6);
		(current.SOUTH_EAST = 7);
		(current.SOUTH_SOUTH_EAST = 8);
		(current.SOUTH = 9);
		(current.SOUTH_SOUTH_WEST = 10);
		(current.SOUTH_WEST = 11);
		(current.WEST_SOUTH_WEST = 12);
		(current.WEST = 13);
  	(current.WEST_NORTH_WEST = 14);
		(current.NORTH_WEST = 15);
		(current.NORTH_NORTH_WEST = 16);	
}
