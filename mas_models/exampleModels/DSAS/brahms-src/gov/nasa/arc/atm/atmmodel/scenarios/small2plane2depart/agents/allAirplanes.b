/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.scenarios.small2plane2depart.agents;

agent plane_JBU6365 memberof Airplane {
  initial_beliefs:
  (current.Name = JBU6365);
	(current.m_dLatitude = 41.25311);
	(current.m_dLongitude = -73.71723);
	(current.landed = false);
	(current.flightPlan = flightPlan_JBU6365);
	(current.iCurrentSegment = 0);
	(current.startTime = 40);
	(current.m_iAirSpeed = 239);
	(current.m_dAltitude = 8755.76);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = ZNY_110);
  initial_facts:
  (current.Name = JBU6365);
	(current.m_dLatitude = 41.25311);
	(current.m_dLongitude = -73.71723);
	(current.landed = false);
	(current.flightPlan = flightPlan_JBU6365);
	(current.iCurrentSegment = 0);
	(current.startTime = 40);
	(current.m_iAirSpeed = 239);
	(current.m_dAltitude = 8755.76);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = ZNY_110);

}

agent slot_JBU6365 memberof slotMarker {
  initial_beliefs:
	(current.Name = JBU6365);
	(current.flightPlan = flightPlan_JBU6365);
  initial_facts:
	(current.Name = JBU6365);
	(current.flightPlan = flightPlan_JBU6365);
}

agent plane_SWA1837 memberof Airplane {
  initial_beliefs:
	(current.Name = SWA1837);
	(current.m_dLatitude = 40.68986);
	(current.m_dLongitude = -74.0087);
	(current.landed = false);
	(current.flightPlan = flightPlan_SWA1837);
	(current.iCurrentSegment = 0);
	(current.startTime = 12);
	(current.m_iAirSpeed = 218);
	(current.m_dAltitude = 8561.12);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = ZNY_114);
  initial_facts:
	(current.Name = SWA1837);
	(current.m_dLatitude = 40.68986);
	(current.m_dLongitude = -74.0087);
	(current.landed = false);
	(current.flightPlan = flightPlan_SWA1837);
	(current.iCurrentSegment = 0);
	(current.startTime = 12);
	(current.m_iAirSpeed = 218);
	(current.m_dAltitude = 8561.12);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = ZNY_114);
}

agent slot_SWA1837 memberof slotMarker {
  initial_beliefs:
  (current.Name = SWA1837);
  (current.flightPlan = flightPlan_SWA1837);
  initial_facts:
  (current.Name = SWA1837);
  (current.flightPlan = flightPlan_SWA1837);
}

agent plane_ASQ5573 memberof Airplane {
  initial_beliefs:
	(current.Name = ASQ5573);
	(current.m_dLatitude = 40.78143);
	(current.m_dLongitude = -73.87316);
	(current.landed = false);
	(current.flightPlan = flightPlan_ASQ5573);
	(current.iCurrentSegment = 0);
	(current.m_iAirSpeed = 165);
	(current.m_dAltitude = 67.28);
	(current.is_departure = true);
	(current.iStatus = 2);
	(current.startTime = 18);
	(current.controller = ZNY_118);
  initial_facts:
	(current.Name = ASQ5573);
	(current.m_dLatitude = 40.78143);
	(current.m_dLongitude = -73.87316);
	(current.landed = false);
	(current.flightPlan = flightPlan_ASQ5573);
	(current.iCurrentSegment = 0);
	(current.m_iAirSpeed = 165);
	(current.m_dAltitude = 67.28);
	(current.is_departure = true);
	(current.iStatus = 2);
	(current.startTime = 18);
	(current.controller = ZNY_118);
}

agent plane_TCF5584 memberof Airplane {
  initial_beliefs:
	(current.Name = TCF5584);
	(current.m_dLatitude = 40.78093);
	(current.m_dLongitude = -73.87424);
	(current.m_iAirSpeed = 161);
	(current.m_dAltitude = 117.57);
	(current.is_departure = true);
	(current.flightPlan = flightPlan_TCF5584);
	(current.iCurrentSegment = 0);
	(current.iStatus = 2);
	(current.startTime = 89);
	(current.controller = ZNY_118);
  initial_facts:
	(current.Name = TCF5584);
	(current.m_dLatitude = 40.78093);
	(current.m_dLongitude = -73.87424);
	(current.m_iAirSpeed = 161);
	(current.m_dAltitude = 117.57);
	(current.flightPlan = flightPlan_TCF5584);
	(current.iCurrentSegment = 0);
	(current.is_departure = true);
	(current.iStatus = 2);
	(current.startTime = 89);
	(current.controller = ZNY_118);
}
