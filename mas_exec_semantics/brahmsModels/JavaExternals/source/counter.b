class IntBox {
	attributes:
		public int val;
}

object zero instanceof IntBox {
	display: "zero";
	initial_beliefs:
		(current.val = 0);
	initial_facts:
		(current.val = 0);
}

object one instanceof IntBox {
	display: "one";
	initial_beliefs:
		(current.val = 0);
	initial_facts:
		(current.val = 0);
}

object two instanceof IntBox {
	display: "two";
	initial_beliefs:
		(current.val = 2);
	initial_facts:
		(current.val = 2);
}
 
class IntBoxBox {
	attributes:
		public IntBox box;
		public map mp;
}

object boxZero instanceof IntBoxBox {
	display: "boxZero";
	initial_beliefs:
		(current.box = one);
		(current.mp(0) = 1);
		(current.mp(1) = 2);
		(current.mp(3) = "foo");
	initial_facts:
		(current.box = one);
				(current.mp(0) = 1);
		(current.mp(1) = 2);
		(current.mp(3) = "foo");

activities: 

java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
			}

java retractBeliefs(Concept subject, string attribute, int index) {
		class: "brahms.base.system.RetractBeliefsActivity";
	}


 /**workframes:
      workframe increment{
         repeat: 
            false;
	when(
	knownval(current.box = one)
	) do {
	printBelief(current, "mp", "attribute");
	retractBeliefs(current, "mp", 0);
	printBelief(current, "mp", "attribute");
	}
   }**/
}

class Bucket {
	relations:
		public IntBox myboxes;
}

object bckt instanceof Bucket {
	initial_beliefs:
		(current myboxes zero);
		(current myboxes one);
		
	initial_facts:
		(current myboxes zero);
		(current myboxes one);
}

agent tester {
   attributes:
      public int i;
			public IntBox box_i;
			public IntBoxBox boxbox_i;
			public Bucket bucket_i;
			
   initial_beliefs:
      (current.i = 0);
			(current.box_i = zero);
			(current.boxbox_i = boxZero);
			(current.bucket_i = bckt);
   
	 initial_facts:
      (current.i = 0);
			(current.box_i = zero);
			(current.boxbox_i = boxZero);
			(current.bucket_i = bckt);
					
   activities:

      primitive_activity pause_for_thought() {
         max_duration: 1;
      }

      java increment(int ival, int oval) {
				 max_duration: 1;
         class: "external.Increment";
      }

      java increment_IntBox(IntBox ibox) {
				 max_duration: 1;
         class: "external.Increment_IntBox";
      }

			java increment_IntBoxBox(IntBoxBox iboxbox) {
				max_duration: 1;
				class: "external.Increment_IntBoxBox";
			}

			java read_IntBoxBox_Map(IntBoxBox iboxbox) {
				max_duration: 1;
				class: "external.Read_IntBoxBox_Map";
			}
				
			java update_Bucket_Two(Bucket ibucket, IntBox two) {
				max_duration: 1;
				class: "external.Update_Bucket_Two";
			}
					
			java println(string message) { 
				class: "brahms.base.system.PrintlnActivity"; 
			}

			/**create_object copyIntBox(IntBox ibox, IntBox obox) {
				display: "createIntBox";
				action: copy;
				source: ibox;
				destination: obox;
				when: start;
			}**/
			
			java readBeliefs(IntBox subject) {
				display: "readBeliefs";
				class: "brahms.base.system.ReadBeliefsActivity";
			}
			
			java printBelief(Concept aboutConcept, string aboutAttribute, string aboutAttributeType) {
				class: "brahms.base.system.PrintBeliefActivity";
			}


			
   workframes:
      workframe increment{
         repeat: 
            true;
         variables:
            forone(int) ival;
						forone(int) ovalParam;
						forone(IntBox) ibox;
						forone(IntBoxBox) iboxbox;
						forone(Bucket) ibucket;
						forone(IntBox) tmpBox;
         when (knownval(ival = current.i) and
               knownval(ival < 5) and
							 knownval(ibox = current.box_i) and
							 knownval(iboxbox = current.boxbox_i) and
							 knownval(ibucket = current.bucket_i))
         do{
	          pause_for_thought();
		        increment(ival, ovalParam);
		        conclude((current.i = ovalParam));
						increment_IntBox(ibox);
					  conclude((current.box_i = ibox));
						increment_IntBoxBox(iboxbox);
					  conclude((current.boxbox_i = iboxbox));
						//copyIntBox(ibox, tmpBox);
						printBelief(tester, "i", "attribute");
						printBelief(current, "box_i", "attribute");
						printBelief(tester, "boxbox_i", "attribute");
				
						//readBeliefs(zero);
						//printBelief(zero, "val", "attribute");
						///increment_IntBox(tmpBox);
						read_IntBoxBox_Map(iboxbox);
						update_Bucket_Two(ibucket,two);
						println("\n");
         }
      }

}
