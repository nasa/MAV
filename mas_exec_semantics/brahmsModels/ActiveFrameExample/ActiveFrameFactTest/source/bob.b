object bob instanceof Student{

display: "bob_cylon";

initial_beliefs: 

(current.name="bob");
(ClassroomClock.time = 1);
(hammer2.material = "aluminum");
(bobhammer wasGiftFrom dora);
(hammer1 wasGiftFrom tim);

initial_facts:

(current.name="bob");
(ClassroomClock.time = 1);
(current.testObjAtt = bobhammer);
(current.myHammer = hammer3);
(bobhammer wasGiftFrom dora);



activities: 

	java println(string message) { 
				class: "brahms.base.system.PrintlnActivity"; 
			}
			
	java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
			}

	java readBeliefs(Student fromActor, string attribute) {
				display: "readBeliefs";
				class: "brahms.base.system.ReadBeliefsActivity";
			}
			
workframes: 

workframe wf_test {
	repeat : false;
	
	variables : 
		foreach(Student) stdVar;
		
	when ( 
				 known(stdVar.testObjAtt wasGiftFrom) and
	       unknown(stdVar.myHammer wasGiftFrom) 
			)
				
	do{
		println("found the right binding");
		conclude((stdVar.name = stdVar.name));
		//readBeliefs(stdVar, "name"); 
		printBelief(stdVar, "name", "attribute");
	}
	
}

} 

