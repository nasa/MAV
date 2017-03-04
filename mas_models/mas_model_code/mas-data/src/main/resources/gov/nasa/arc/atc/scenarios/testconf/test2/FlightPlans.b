object flightPlan_S1 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(1) = DEPA_TO_A_S1);
	(current.flightPlanMap(2) = A_TO_MERGE_S1);
	(current.flightPlanMap(3) = MERGE_TO_D_S1);
  initial_facts:
	(current.flightPlanMap(1) = DEPA_TO_A_S1);
	(current.flightPlanMap(2) = A_TO_MERGE_S1);
	(current.flightPlanMap(3) = MERGE_TO_D_S1);
}

object flightPlan_S2 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(1) = DEPB_TO_B_S2);
	(current.flightPlanMap(2) = B_TO_MERGE_S2);
	(current.flightPlanMap(3) = MERGE_TO_D_S2);
  initial_facts:
	(current.flightPlanMap(1) = DEPB_TO_B_S2);
	(current.flightPlanMap(2) = B_TO_MERGE_S2);
	(current.flightPlanMap(3) = MERGE_TO_D_S2);
}

object flightPlan_S3 instanceof flightPlan {
  initial_beliefs:
	(current.flightPlanMap(1) = DEPC_TO_C_S3);
	(current.flightPlanMap(2) = C_TO_MERGE_S3);
	(current.flightPlanMap(3) = MERGE_TO_D_S3);
  initial_facts:
	(current.flightPlanMap(1) = DEPC_TO_C_S3);
	(current.flightPlanMap(2) = C_TO_MERGE_S3);
	(current.flightPlanMap(3) = MERGE_TO_D_S3);
}
