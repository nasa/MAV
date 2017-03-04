agent Bob2_Agent memberof Student, Builder{


initial_beliefs: 

(ClassroomClock.time = 1);
(hammer2.material = "aluminum");
(bobhammer wasGiftFrom dora);
(hammer1 wasGiftFrom tim);
(current.hunger = 4);

activities: 

	java println(string message) { 
				class: "brahms.base.system.PrintlnActivity"; 
			}
			
	java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
			}

workframes: 

workframe wf_test {
	repeat : false;
	
	variables : 
		foreach(Hammer) hmrVar;
		
	when ( 
				// known(current.hunger) and
				 known(hmrVar wasGiftFrom) and
	       unknown(hmrVar.material) )
	do{
		//println("***found the right binding");
		printBelief(hmrVar, "wasGiftFrom", "relation");
	}
//		
}

}
