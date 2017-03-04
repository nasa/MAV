Running Departure Sensitive Arrival Spacing (DSAS) models:

Pre-requisites: 
Java 1.8
Ant
Brahms
Git

Users need to get installer and license from

https://www.ejenta.com/developers


Based on the architecture select the appropriate one and install it.

Once this is installed, there will be an "AgentEnvironment"
directory. At the root of the AgentEnvironment folder add the
brahms.lic file.

To run the model on the terminal:

> cd exampleModels/DSAS

Update paths to the Brahms installation in the following files:

bvm: set the BRAHMS_ROOT to where you have installed Brahms

build.xml: 

vm.cfg: 

vmLogger.cfg: 

> ant clean 
> ant brahms-sim -Dsrc.package=gov/nasa/arc/atm/atmmodel/scenarios/week2run6/

The input parameter to the ant target provides which scenario to run
the. The week2run6 represents the traffic scenario from the
human-in-the-loop (HITL). Each scenario consists of approximately 1.5
hours real time of simulation. During this time, there are between 130
and 150 airplanes being managed by four enroute controllers, three
TRACON controllers, and one tower controller at LGA who is responsible
for departures and arrivals. The planes are landing at approximately
36 to 40 planes an hour.

Details about the DSAS concept can be found in the following paper: 

Lee, Paul U., et al. "Reducing Departure Delays at LaGuardia Airport
with Departure-Sensitive Arrival Spacing (DSAS) Operations."
Proceedings of the Eleventh USA/Europe Air Traffic Management Research
and Development Seminar. 2015.

The details about the models, its abstraction and other design choices
can be found in:

Rungta, Neha, et al. "Modeling complex air traffic management
systems." Proceedings of the 8th International Workshop on Modeling in
Software Engineering. ACM, 2016.

Hamon, Arnaud, et al. "Modeling Functional Specifications of Ground
Systems in the National Airspace System."

The DSAS models consists of

* Multi-agent system Brahms models (exampleModels/DSAS)
* An abstraction of the flight dynamics (mas_model_code/mas-atclib)
* Implementation of the NextGen concepts (mas_model_code/mas-atm-concepts)
* Java wrapper for connecting Brahms constructions to Java code (mas_model_code/mas-brahms).

The other projects are: 

* Generate scenarios from ADRS files generated from MACS
  (mas_model_code/mas-model-gen) 

* Standalone interface that allows simulation of ATM concepts without
  Brahms constructs (mas_model_code/mas-hmi)

* Example scenario files for used in the standalone simulation based
  on MACS data (mas_model_code/mas-data)

* Generate scenarios based on random traffic along different sectors
  in the NY metroplex. (mas_model_code/mas-sim-conf)

