/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.scenarios.small2plane2depart.agents;

//center south 
agent ZNY_29 memberof Center {
  initial_beliefs:
 	(current hasWaypoint GVE);
 	(current hasWaypoint COLIN);
 	(current hasWaypoint GARED);
 	(current hasWaypoint AGARD);
 	(current hasWaypoint ENO);
 	(current hasWaypoint SKIPY);
 	(current hasWaypoint BESSI);
 	(current hasWaypoint EDJER);
 	(current hasWaypoint DAVYS);
 	(current hasWaypoint HOLEY);
 	(current hasWaypoint RIDGY);
	(current.handoffWaypoint = HOLEY);
	(current.handoffTo = ZNY_114); //HO to Empyr
  initial_facts:
 	(current hasWaypoint GVE);
 	(current hasWaypoint COLIN);
 	(current hasWaypoint GARED);
 	(current hasWaypoint AGARD);
 	(current hasWaypoint ENO);
 	(current hasWaypoint SKIPY);
 	(current hasWaypoint BESSI);
 	(current hasWaypoint EDJER);
 	(current hasWaypoint DAVYS);
 	(current hasWaypoint HOLEY);
 	(current hasWaypoint RIDGY);
	(current.handoffWaypoint = HOLEY);
	(current.handoffTo = ZNY_114);
}
//center west
agent ZNY_28 memberof Center {
	 initial_beliefs:
	 	(current hasWaypoint SFK);
		(current hasWaypoint MIP);
		(current hasWaypoint MARRC);
		(current hasWaypoint BILEY);
		(current hasWaypoint VIBES);
		(current hasWaypoint FJC);
		(current hasWaypoint FINSI);
		(current hasWaypoint ETG);
		(current.handoffWaypoint = FINSI);
		(current.handoffTo = ZNY_114); //HO to Empyr
	 initial_facts:
		(current hasWaypoint SFK);
		(current hasWaypoint MIP);
		(current hasWaypoint MARRC);
		(current hasWaypoint BILEY);
		(current hasWaypoint VIBES);
		(current hasWaypoint FJC);
		(current hasWaypoint FINSI);
		(current hasWaypoint ETG);
		(current.handoffWaypoint = FINSI);
		(current.handoffTo = ZNY_114); //HO to Empyr
}

//center north
agent ZNY_27 memberof Center {
	 initial_beliefs:
	 	(current hasWaypoint IGN);
		(current hasWaypoint VALRE);
		(current hasWaypoint BASYE);
		(current hasWaypoint BDL);
		(current.handoffWaypoint = BASYE);
		(current.handoffTo = ZNY_110); //HO to Haarp
	 initial_facts:
		(current hasWaypoint IGN);
		(current hasWaypoint VALRE);
		(current hasWaypoint BASYE);
		(current hasWaypoint BDL);
		(current.handoffWaypoint = BASYE);
		(current.handoffTo = ZNY_110); //HO to Haarp
}
//Empyr Arrival sector
agent ZNY_114 memberof Tracon {
	 initial_beliefs:
	 	(current hasWaypoint BRAND);
		(current hasWaypoint KORRY);
		(current hasWaypoint RBV);
		(current hasWaypoint SHVAL);
		(current hasWaypoint TYKES);
		(current hasWaypoint MINKS);
		(current hasWaypoint RABBA);
		(current hasWaypoint RENUE);
		(current hasWaypoint CONIL);
		(current hasWaypoint VASTI);
		(current hasWaypoint ARRYA);
		(current hasWaypoint AWARE);
		(current hasWaypoint FOLAM);
		(current hasWaypoint HAYER);
		(current hasWaypoint COSTL);
		(current hasWaypoint TYKES);
		(current.handoffWaypoint = ARRYA);
		(current.handoffTo = ZNY_112); //HO to Final
	//	(current hasPlane plane_SWA1837);
//		(current hasSlot slot_SWA1837);
//		(plane_SWA1837.Name = SWA1837); // Needed for the "beginning of the world" so Controllers work
///		(slot_SWA1837.Name = SWA1837);
	 initial_facts:
	 	(current hasWaypoint BRAND);
		(current hasWaypoint KORRY);
		(current hasWaypoint RBV);
		(current hasWaypoint SHVAL);
		(current hasWaypoint TYKES);
		(current hasWaypoint MINKS);
		(current hasWaypoint RABBA);
		(current hasWaypoint RENUE);
		(current hasWaypoint CONIL);
		(current hasWaypoint VASTI);
		(current hasWaypoint ARRYA);
		(current hasWaypoint AWARE);
		(current hasWaypoint FOLAM);
		(current hasWaypoint HAYER);
		(current hasWaypoint COSTL);
		(current hasWaypoint TYKES);
		(current.handoffWaypoint = ARRYA);
		(current.handoffTo = ZNY_112); //HO to Final
	//	(current hasPlane plane_SWA1837);
	//	(current hasSlot slot_SWA1837);
	//	(plane_SWA1837.Name = SWA1837);
	//	(slot_SWA1837.Name = SWA1837);
}

//Haarp Arrival sector
agent ZNY_110 memberof Tracon {
	 initial_beliefs:
	 	(current hasWaypoint FODAK);
		(current hasWaypoint DOSWL);
		(current hasWaypoint ELZEE);
		(current hasWaypoint HAARP);
		(current hasWaypoint FAMMA);
		(current.handoffWaypoint = FAMMA);
		(current.handoffTo = ZNY_112); //HO to Final
	//	(current hasPlane plane_JBU6365);
	//	(current hasSlot slot_JBU6365);	
	//	(plane_JBU6365.Name = JBU6365);
	//	(slot_JBU6365.Name = JBU6365);
	 initial_facts:
		(current hasWaypoint FODAK);
		(current hasWaypoint DOSWL);
		(current hasWaypoint ELZEE);
		(current hasWaypoint HAARP);
		(current hasWaypoint FAMMA);
		(current.handoffWaypoint = FAMMA);
		(current.handoffTo = ZNY_112); //HO to Final
		//(current hasPlane plane_JBU6365);
		//(current hasSlot slot_JBU6365);
		//(plane_JBU6365.Name = JBU6365);
		//(slot_JBU6365.Name = JBU6365);
}

//Final sector
agent ZNY_112 memberof Tracon {
	initial_beliefs: 
		(current hasWaypoint YOMAN);
		(current hasWaypoint OMAAR);
		(current hasWaypoint GREKO);
		(current hasWaypoint KYLIE);
		(current hasWaypoint CARAA);
		(current hasWaypoint EVANZ);
		(current hasWaypoint MIRRA);
		(current.handoffWaypoint = KYLIE);
		(current.handoffTo = ZNY_118); //HO to Tower
	initial_facts:
		(current hasWaypoint YOMAN);
		(current hasWaypoint OMAAR);
		(current hasWaypoint GREKO);
		(current hasWaypoint KYLIE);
		(current hasWaypoint CARAA);
		(current hasWaypoint EVANZ);
		(current hasWaypoint MIRRA);
		(current.handoffWaypoint = KYLIE);
		(current.handoffTo = ZNY_118); //HO to Tower
}



