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

package gov.nasa.arc.brahms.parse.model;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import gov.nasa.arc.brahms.model.MultiAgentSystem;
import gov.nasa.arc.brahms.parse.BrahmsLexer;
import gov.nasa.arc.brahms.parse.BrahmsParser;

public class ParseBrahmsInput {
	
	public static void main(String[] args) throws IOException {
	    //Reading the DSL script
	    InputStream is = 
	            ClassLoader.getSystemResourceAsStream("test.b");
	    
	    //Loading the DSL script into the ANTLR stream.
	    CharStream cs = new ANTLRInputStream(is);
	    
	    BrahmsLexer bl = new BrahmsLexer(cs);
	    
	    CommonTokenStream tokens = new CommonTokenStream(bl);
	    
	    BrahmsParser brahmsParser = new BrahmsParser(tokens);
	    brahmsParser.setBuildParseTree(true);
	    
	    //Adding the listener to facilitate walking through parse tree. 
	    MultiAgentSystem mas = new MultiAgentSystem();
	    brahmsParser.addParseListener(new FirstPassBrahmsListener(mas));
	   
	    //invoking the parser. 
	    brahmsParser.compilationUnit();
	    
	    System.out.println("classes:" + mas.getClasses().toString());
	    System.out.println("agents: " + mas.getAgents().toString());
	    System.out.println("groups :" + mas.getGroups().toString());
	    System.out.println("objects :" + mas.getObjects().toString() );
	}
}
