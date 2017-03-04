/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.scenarios.dsas2test1.agents;

agent plane_A1 memberof Airplane {
  initial_beliefs:
  	(current.Name = A1);
	(current.m_dLatitude = 39.4);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_A1);
	(current.iCurrentSegment = 0);
	(current.startTime = 8);
	(current.m_iAirSpeed = 280);
	(current.m_dAltitude = 5000.0);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = Tracon_Alfred);
  initial_facts:
  	(current.Name = A1);
	(current.m_dLatitude = 39.4);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_A1);
	(current.iCurrentSegment = 0);
	(current.startTime = 8);
	(current.m_iAirSpeed = 280);
	(current.m_dAltitude = 5000.0);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = Tracon_Alfred);
}

agent slot_A1 memberof slotMarker {
  initial_beliefs:
	(current.Name = A1);
	(current.flightPlan = flightPlan_A1);
  initial_facts:
	(current.Name = A1);
	(current.flightPlan = flightPlan_A1);
}


agent plane_A2 memberof Airplane {
  initial_beliefs:
  	(current.Name = A2);
	(current.m_dLatitude = 39.4);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_A2);
	(current.iCurrentSegment = 0);
	(current.startTime = 113);
	(current.m_iAirSpeed = 280);
	(current.m_dAltitude = 5000.0);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = Tracon_Alfred);
  initial_facts:
  	(current.Name = A2);
	(current.m_dLatitude = 39.4);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_A2);
	(current.iCurrentSegment = 0);
	(current.startTime = 113);
	(current.m_iAirSpeed = 280);
	(current.m_dAltitude = 5000.0);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = Tracon_Alfred);
}

agent slot_A2 memberof slotMarker {
  initial_beliefs:
	(current.Name = A2);
	(current.flightPlan = flightPlan_A2);
  initial_facts:
	(current.Name = A2);
	(current.flightPlan = flightPlan_A2);
}

agent plane_A3 memberof Airplane {
  initial_beliefs:
  	(current.Name = A3);
	(current.m_dLatitude = 39.4);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_A1);
	(current.iCurrentSegment = 0);
	(current.startTime = 205);
	(current.m_iAirSpeed = 280);
	(current.m_dAltitude = 5000.0);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = Tracon_Alfred);
  initial_facts:
  	(current.Name = A3);
	(current.m_dLatitude = 39.4);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_A3);
	(current.iCurrentSegment = 0);
	(current.startTime = 205);
	(current.m_iAirSpeed = 280);
	(current.m_dAltitude = 5000.0);
	(current.is_departure = false);
	(current.iStatus = 0);
	(current.controller = Tracon_Alfred);
}

agent slot_A3 memberof slotMarker {
  initial_beliefs:
	(current.Name = A3);
	(current.flightPlan = flightPlan_A3);
  initial_facts:
	(current.Name = A3);
	(current.flightPlan = flightPlan_A3);
}


agent plane_D1 memberof Airplane {
  initial_beliefs:
  	(current.Name = D1);
	(current.m_dLatitude = 40);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_D1);
	(current.iCurrentSegment = 0);
	(current.startTime = 544);
	(current.m_iAirSpeed = 220);
	(current.m_dAltitude = 0.0);
	(current.is_departure = true);
	(current.iStatus = 0);
	(current.controller = Tower_Bob);
  initial_facts:
  	(current.Name = D1);
	(current.m_dLatitude = 40);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_D1);
	(current.iCurrentSegment = 0);
	(current.startTime = 544);
	(current.m_iAirSpeed = 220);
	(current.m_dAltitude = 0.0);
	(current.is_departure = true);
	(current.iStatus = 0);
	(current.controller = Tower_Bob);
}


agent plane_D2 memberof Airplane {
  initial_beliefs:
  	(current.Name = D2);
	(current.m_dLatitude = 40);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_D2);
	(current.iCurrentSegment = 0);
	(current.startTime = 549);
	(current.m_iAirSpeed = 220);
	(current.m_dAltitude = 0.0);
	(current.is_departure = true);
	(current.iStatus = 0);
	(current.controller = Tower_Bob);
  initial_facts:
  	(current.Name = D2);
	(current.m_dLatitude = 40);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_D2);
	(current.iCurrentSegment = 0);
	(current.startTime = 549);
	(current.m_iAirSpeed = 220);
	(current.m_dAltitude = 0.0);
	(current.is_departure = true);
	(current.iStatus = 0);
	(current.controller = Tower_Bob);
}


agent plane_D3 memberof Airplane {
  initial_beliefs:
  	(current.Name = D3);
	(current.m_dLatitude = 40);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_D3);
	(current.iCurrentSegment = 0);
	(current.startTime = 554);
	(current.m_iAirSpeed = 220);
	(current.m_dAltitude = 0.0);
	(current.is_departure = true);
	(current.iStatus = 0);
	(current.controller = Tower_Bob);
  initial_facts:
  	(current.Name = D3);
	(current.m_dLatitude = 40);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_D3);
	(current.iCurrentSegment = 0);
	(current.startTime = 554);
	(current.m_iAirSpeed = 220);
	(current.m_dAltitude = 0.0);
	(current.is_departure = true);
	(current.iStatus = 0);
	(current.controller = Tower_Bob);
}


agent plane_D4 memberof Airplane {
  initial_beliefs:
  	(current.Name = D4);
	(current.m_dLatitude = 40);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_D4);
	(current.iCurrentSegment = 0);
	(current.startTime = 559);
	(current.m_iAirSpeed = 220);
	(current.m_dAltitude = 0.0);
	(current.is_departure = true);
	(current.iStatus = 0);
	(current.controller = Tower_Bob);
  initial_facts:
  	(current.Name = D4);
	(current.m_dLatitude = 40);
	(current.m_dLongitude = 70);
	(current.landed = false);
	(current.flightPlan = flightPlan_D4);
	(current.iCurrentSegment = 0);
	(current.startTime = 559);
	(current.m_iAirSpeed = 220);
	(current.m_dAltitude = 0.0);
	(current.is_departure = true);
	(current.iStatus = 0);
	(current.controller = Tower_Bob);
}
