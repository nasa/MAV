object FlightPlan_S1 instanceof FlightPlan {
  initial_beliefs:
	(current.flightPlanMap(1) = DEPA_TO_A_S1);
	(current.flightPlanMap(2) = A_TO_B_S1);
	(current.flightPlanMap(3) = B_TO_C_S1);
	(current.flightPlanMap(3) = C_TO_D_S1);
  initial_facts:
	(current.flightPlanMap(1) = DEPA_TO_A_S1);
	(current.flightPlanMap(2) = A_TO_B_S1);
	(current.flightPlanMap(3) = B_TO_C_S1);
	(current.flightPlanMap(3) = C_TO_D_S1);
}

