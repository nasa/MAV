/**
Copyright © 2016, United States Government, as represented
by the Administrator of the National Aeronautics and Space
Administration. All rights reserved.
 
The MAV - Modeling, analysis and visualization of ATM concepts
platform is licensed under the Apache License, Version 2.0
(the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0. 
 
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific
language governing permissions and limitations under the
License.
**/

package gov.nasa.jpf.mas.db;


/*
  Franco Raimondi, 24/02/2013
  Some parts taken from 
  http://db.apache.org/derby/papers/DerbyTut/embedded_intro.html
  (in particular SimpleApp.java)


  Usage: ReadDerbyModel /path/to/DerbyDB/


 CLASSPATH: 
 export CLASSPATH=/Applications/Brahms/AgentEnvironment/lib/apache/derby-10.5.3.0.jar:/Applications/Brahms/AgentEnvironment/lib/apache/derbytools.jar:./
 



 */

//import gov.nasa.jpf.mas.prism.PCTLModel;
//import gov.nasa.jpf.mas.prism.PCTLState;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class ReadDerbyModel
{
    /* the default framework is embedded*/
    private String framework = "embedded";
    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String protocol = "jdbc:derby:";
    private String dbName = "";
    Connection conn = null;
    
    
	List<Agent> agList;
	
    HashMap<Integer, String> actorIdLookup =
    				new HashMap<Integer, String>();
	//PCTLModel completeModel = new PCTLModel();


    private static boolean DEBUG=true;

    public ReadDerbyModel(String dbn) {
  
	// Trying to load JDBC driver...
	try {
            Class.forName(driver).newInstance();
	    if (DEBUG) {
		System.out.println("Driver loaded");
	    }
        } catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver " + driver);
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            System.err.println("\nUnable to instantiate the JDBC driver " + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            System.err.println("\nNot allowed to access the JDBC driver " + driver);
            iae.printStackTrace(System.err);
        }

	// Driver loaded, now trying to connect:
	String dbName = dbn; // the name of the database

	if (conn==null) {
	    try {
		conn = DriverManager.getConnection(protocol + dbName
						   + ";create=false");
	    }
	    catch (SQLException sqle) {
		if (DEBUG) {
		    System.out.println("Something wrong when trying to connect");		    
		}
		printSQLException(sqle);
	    }
	}
	if (DEBUG) {
	    System.out.println("Connected do DB");
	}
	agList = new ArrayList<Agent>();
    }


    public static void main(String[] args)
    {
	if (args.length < 1) {
	    System.out.println("No path provided!");
	    System.exit(0);
	}

	String pathToDB = args[0];
	if (DEBUG) {
	    System.out.println("Setting path to DB to: "+pathToDB);
	}



	ReadDerbyModel readDerbyModel = new ReadDerbyModel(pathToDB);


	// Reading traces;
	ArrayList<Integer> traces = readDerbyModel.getTraces();
	if (DEBUG) {
	    System.out.println(traces.size() + " traces read");
	}


	// Reading Agents

	readDerbyModel.analyzeAgents();
	//if (DEBUG) {
	//    System.out.println(actorIdLookup.toString() + " agents read" + actorIdLookup.size());
	//}

	// Getting events in a trace (just a random test):

	if (DEBUG) {
	    System.out.println("Getting events for trace number 1");
	}
	
	for(int traceIndex = 0; traceIndex < traces.size(); traceIndex++){
		System.out.println(traceIndex + " : is the traceIndex");
		readDerbyModel.getEvents(traceIndex+1);
    }

	
	readDerbyModel.closeDown();

	if (DEBUG) {
	    System.out.println("All done, exiting");
	}
    }


    public ArrayList<Integer> getTraces() {
	// FIXME: Need to add a bit of error checking etc...

	ArrayList<Integer> result = new ArrayList<Integer>();
	Statement s = null;
	ResultSet rs = null;

	try {
        s = conn.createStatement();
	    rs = s.executeQuery("SELECT DISTINCT * FROM VNV_TRACE");
	    
	    
	    
	    while(rs.next()) {
		result.add(rs.getInt("TRACE_ID"));
		if (DEBUG) {
		    System.out.println("In getTraces: adding TRACE_ID=" 
				       +  rs.getInt("TRACE_ID"));
		}
	    }
	} catch (SQLException sqle) {
	    printSQLException(sqle);
	}
	return result;
    }


    private void getEvents (int traceID) {    
	// FIXME: Need to add a bit of error checking etc...
        Statement s = null;
        ResultSet rs = null;

	try {
        s = conn.createStatement();
	    //rs = s.executeQuery("SELECT * FROM VNV_STATEMENT_EVENT WHERE TRACE_ID="+traceID);
        
        for (Agent agent : agList) {

            rs = s.executeQuery("SELECT * FROM VNV_STATEMENT_EVENT WHERE TRACE_ID="+traceID +
            		" AND ACTOR_ID = " + agent.getId() +
               	 	" order by INITIAL_STATEMENT DESC, SIM_TIME");
				while (rs.next()) {
					String statement = rs.getString("STATEMENT");

					if (statement.contains(" = ")
							&& (rs.getString("EVENT_TYPE").equals("ASSERTED"))) {
						String name = statement.substring(0,
								statement.indexOf("=")).trim();
						String value = statement.substring(
								statement.indexOf("=") + 1).trim();
						agent.addEvent(rs.getInt("SIM_TIME"), new Event(name,
								value));
						System.out.println("** ADDED: "+name+" -> "+value);
					}
				}
			}
	} 
	catch (SQLException sqle)
        {
            printSQLException(sqle);
        }

    }
	    
//
//        
//        //order by
//		//case when Col1 is not null then Col1
//		//when Col1 is null then Col2
//		//end
//        
//       // PCTLState firstState = new PCTLState();
//        HashMap<String, String> firstBeliefs = new HashMap<String, String>();
//        HashMap<String, String> firstFacts = new HashMap<String, String>();
//        
//	    while(rs.next()) {
//		
//		//System.out.println("INITIAL_STATEMENT="+rs.getInt("INITIAL_STATEMENT")+",");
//		if(rs.getInt("INITIAL_STATEMENT") == 1) {
//			// init state:
//			String initState = rs.getString("STATEMENT");
//			String[] vals = processStatement(initState);
//			//if(vals.length == 2) {
//			//	System.out.println(vals[0] + " ---" + vals[1]);
//			//}
//			//else 
//			//	System.out.println(vals[0]);
//
//			System.out.println(rs.getString("STATEMENT"));
//		} else {
//			//System.out.print("EVENT_ID="+rs.getString("EVENT_ID")+",");
//			//System.out.print("TRACE_ID="+rs.getInt("TRACE_ID")+",");
//			//System.out.print("ACTOR_ID="+rs.getInt("ACTOR_ID")+",");
//			//System.out.print("SIM_TIME="+rs.getInt("SIM_TIME")+",");
//			//System.out.print("EVENT_TYPE="+rs.getString("EVENT_TYPE")+",");
//			//System.out.print("EVENT_TYPE="+rs.getString("STATEMENT_TYPE")+",");
//			//System.out.println("updated="+ rs.getString("STATEMENT"));
//
//		}
//		
//		if(rs.getString("STATEMENT").contains(".collision = true")) {
//		//	System.out.println(rs.getString("SIM_TIME"));
//			System.out.println(rs.getString("SIM_TIME")+ " := " +rs.getString("STATEMENT"));
//		} else if (rs.getString("STATEMENT").contains("AEFFlight")) {
//			//System.out.println("aef="+ rs.getString("STATEMENT"));
//		} else if (rs.getString("STATEMENT").contains("gov.nasa.arc.brahms.afcsmodel.scenario.uberlingen.objects.Airbus320_AEF1135.location = gov.nasa.arc.brahms.afcsmodel.scenario.uberlingen.geography.Waypoint_EDDF_RWY_18")) {
//			System.out.println(rs.getString("SIM_TIME")+ " := " +rs.getString("STATEMENT"));
//		}	
//		
//	    }

    
    private String[] processStatement(String updateStatement) {
    	String val = updateStatement;
    	val.replace("(", "");
    	val.replace(")", "");
    	String [] words = val.split("=");
    	return words;
    	
    }


    public void analyzeAgents () {
    	Statement s = null;
    	ResultSet rs = null;
    	
    	try {
    		s = conn.createStatement();
    		// rs = s.executeQuery("SELECT ACTOR_NAME FROM VNV_TRACED_ACTOR where ACTOR_TYPE like 'AGENT'");
    		rs = s.executeQuery("SELECT ACTOR_ID,ACTOR_NAME FROM VNV_TRACED_ACTOR WHERE ACTOR_TYPE='AGENT'");
    		while(rs.next()) {
    			if (DEBUG) {
    				//System.out.println("In getAgents: adding AGENT_NAME=" 
    				//		+  rs.getString("ACTOR_ID") + " for " + rs.getString("ACTOR_NAME"));
    				//build the lookup between actor names and the ids
    				
//    				actorIdLookup.put(new Integer(rs.getString("ACTOR_ID")),rs.getString("ACTOR_NAME"));
    				agList.add(new Agent(new Integer(rs.getString("ACTOR_ID")),rs.getString("ACTOR_NAME")));
    			}
    		}
    	} catch (SQLException sqle) {
    		printSQLException(sqle);
    	}
    }

    public void closeDown() {
    	    	
    	for (Agent agent : agList) {
    		System.out.println(" AGENT: " + agent.getName());
    		for ( Integer time : agent.getEvents().keySet()) {
    			System.out.println("  Time = " + time);
    			for ( Event e : agent.getEventsAtTime(time)) {
    				System.out.println("    " + e.getName() + "=" + e.getVal());
    			}
    		}
    	}

	try {
	    if (conn != null) {
		conn.close();
		conn = null;
	    }
	} catch (SQLException sqle) {
	    printSQLException(sqle);
	}
    }

    /**
     * Reports a data verification failure to System.err with the given message.
     *
     * @param message A message describing what failed.
     */
    private void reportFailure(String message) {
        System.err.println("\nData verification failed:");
        System.err.println('\t' + message);
    }

    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }


}
    

    
