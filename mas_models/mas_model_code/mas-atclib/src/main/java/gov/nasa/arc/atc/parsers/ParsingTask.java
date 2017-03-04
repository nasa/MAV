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

package gov.nasa.arc.atc.parsers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParsingTask implements Runnable {
	
	private static final Logger LOG = Logger.getGlobal();
	/**
	 * Name of the event sent when parsing progresses
	 */
	public static final String PROGRESS_UPDATE = "progressUpdate";
	
	/**
	 * Name of the event sent when parsing is completed
	 */
	public static final String PARSING_COMPLETED = "parsingCompleted";

	private final PropertyChangeSupport propertyChangeSupport;
	private Parser parser;

	private double progress;
	private boolean isDone;

	private Object result;

	public ParsingTask(Parser aParser) {
		propertyChangeSupport = new PropertyChangeSupport(this);
		progress = 0.0;
		setParser(aParser);
	}
	
	public ParsingTask() {
		this(null);
	}

	@Override
	public void run() {
		if (parser != null) {
			LOG.log(Level.INFO, "Running parser {0}", parser);
			progress=0;
			fireProgressUpdate();
			result = parser.parse();
		}
	}
	
	public final void setParser(Parser aParser){
		parser = aParser;
		if(parser!=null){
			parser.setParsingTask(this);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public boolean isDone() {
		return isDone;
	}

	public Object getResult() {
		return result;
	}

	protected void updateProgress(double newProgress) {
		progress = newProgress;
		fireProgressUpdate();
	}
	
	protected void setCompletedSuccessfully(final boolean sucess){
		propertyChangeSupport.firePropertyChange(PARSING_COMPLETED, null, sucess);
	}

	private void fireProgressUpdate() {
		propertyChangeSupport.firePropertyChange(PROGRESS_UPDATE, null, progress);
	}

}
