object flightPlan_S1 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(1) = DEP_TO_A_S1);
	(current.flightPlanMap(2) = A_TO_C_S1);
	(current.flightPlanMap(3) = C_TO_D_S1);
	(current.flightPlanMap(4) = D_TO_E_S1);
  initial_facts:
	(current.flightPlanMap(1) = DEP_TO_A_S1);
	(current.flightPlanMap(2) = A_TO_B_S1);
	(current.flightPlanMap(3) = C_TO_D_S1);
	(current.flightPlanMap(4) = D_TO_E_S1);
}

object flightPlan_S2 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(1) = DEP_TO_B_S2);
	(current.flightPlanMap(2) = B_TO_C_S2);
	(current.flightPlanMap(3) = C_TO_D_S2);
	(current.flightPlanMap(4) = D_TO_E_S2);
  initial_facts:
	(current.flightPlanMap(1) = DEP_TO_B_S2);
	(current.flightPlanMap(2) = B_TO_C_S2);
	(current.flightPlanMap(3) = C_TO_D_S2);
	(current.flightPlanMap(4) = D_TO_E_S2);
}
