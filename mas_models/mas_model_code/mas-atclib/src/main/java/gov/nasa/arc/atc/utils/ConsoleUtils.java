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

package gov.nasa.arc.atc.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ahamon
 *
 */
public class ConsoleUtils {
	
	private static final Logger LOG = Logger.getGlobal();
	private static final CustomRecordFormatter FORMATTER = new CustomRecordFormatter();
	private static final ConsoleHandler CONSOLE_HANDLER = new ConsoleHandler();
	private static ConsoleUtils instance=null;
	
	private ConsoleUtils(){
		LOG.setUseParentHandlers(false);
		CONSOLE_HANDLER.setFormatter(FORMATTER);
		Handler[] handlers = LOG.getHandlers();
        for (Handler handler : handlers) {
            LOG.removeHandler(handler);
        }
		CONSOLE_HANDLER.setLevel(Level.ALL);
		LOG.addHandler(CONSOLE_HANDLER);
		LOG.log(Level.CONFIG, "LOG Level is {0}", Level.ALL);
	}
	
	public static final void setLoggingLevel(Level level){
		if(instance==null){
			instance= new ConsoleUtils();
		}
		CONSOLE_HANDLER.setLevel(level);
	}

}
