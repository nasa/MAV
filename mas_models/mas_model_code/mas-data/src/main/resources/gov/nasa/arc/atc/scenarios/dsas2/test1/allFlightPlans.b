/********************************************************************
 *                 (c)2011 Nasa Ames Research Center                *
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atm.atmmodel.scenarios.dsas2test1.objects;

object flightPlan_A1 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(0) = E_TO_F_A1);
	(current.flightPlanMap(1) = F_TO_G_A1);
	(current.flightPlanMap(2) = G_TO_H_A1);
	(current.flightPlanMap(3) = H_TO_R_A1);
  initial_facts:
	(current.flightPlanMap(0) = E_TO_F_A1);
	(current.flightPlanMap(1) = F_TO_G_A1);
	(current.flightPlanMap(2) = G_TO_H_A1);
	(current.flightPlanMap(3) = H_TO_R_A1);
}

object flightPlan_A2 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(0) = E_TO_F_A2);
	(current.flightPlanMap(1) = F_TO_G_A2);
	(current.flightPlanMap(2) = G_TO_H_A2);
	(current.flightPlanMap(3) = H_TO_R_A2);
  initial_facts:
	(current.flightPlanMap(0) = E_TO_F_A2);
	(current.flightPlanMap(1) = F_TO_G_A2);
	(current.flightPlanMap(2) = G_TO_H_A2);
	(current.flightPlanMap(3) = H_TO_R_A2);
}

object flightPlan_A3 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(0) = E_TO_F_A3);
	(current.flightPlanMap(1) = F_TO_G_A3);
	(current.flightPlanMap(2) = G_TO_H_A3);
	(current.flightPlanMap(3) = H_TO_R_A3);
  initial_facts:
	(current.flightPlanMap(0) = E_TO_F_A3);
	(current.flightPlanMap(1) = F_TO_G_A3);
	(current.flightPlanMap(2) = G_TO_H_A3);
	(current.flightPlanMap(3) = H_TO_R_A3);
}

object flightPlan_D1 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(0) = R_TO_A_D1);
	(current.flightPlanMap(1) = A_TO_B_D1);
  initial_facts:
	(current.flightPlanMap(0) = R_TO_A_D1);
	(current.flightPlanMap(1) = A_TO_B_D1);
}

object flightPlan_D2 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(0) = R_TO_A_D2);
	(current.flightPlanMap(1) = A_TO_B_D2);
  initial_facts:
	(current.flightPlanMap(0) = R_TO_A_D2);
	(current.flightPlanMap(1) = A_TO_B_D2);
}

object flightPlan_D3 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(0) = R_TO_A_D3);
	(current.flightPlanMap(1) = A_TO_B_D3);
  initial_facts:
	(current.flightPlanMap(0) = R_TO_A_D3);
	(current.flightPlanMap(1) = A_TO_B_D3);
}

object flightPlan_D4 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(0) = R_TO_A_D4);
	(current.flightPlanMap(1) = A_TO_B_D4);
  initial_facts:
	(current.flightPlanMap(0) = R_TO_A_D4);
	(current.flightPlanMap(1) = A_TO_B_D4);
}
