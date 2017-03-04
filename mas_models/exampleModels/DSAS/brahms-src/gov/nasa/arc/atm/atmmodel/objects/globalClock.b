/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.objects;

object globalClock instanceof theClock{
	initial_beliefs:
		(current.time = 0);
		(current.interval = 1);
		(current.endTime = 100);

	initial_facts:
		(current.time = 0);
		(current.interval = 1);
		(current.endTime = 100);
}
