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

package gov.nasa.arc.atc.parsing.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHTMLExporter {

	private static final Logger LOG = Logger.getGlobal();

	private static final boolean LOG_OK_MESSAGES = true;
	private static final boolean LOG_IGNORED_MESSAGES = true;

    private static final StringBuilder SB = new StringBuilder();

	private LogHTMLExporter() {
		// private utility constructor
	}

	public static void addOKMessage(String message) {
		if (LOG_OK_MESSAGES) {
            SB.append("<span style=\"color:green\">");
            SB.append(message);
            SB.append("</span><br />");
		}
	}

	public static void addIgnoredMessage(String message) {
		if (LOG_IGNORED_MESSAGES) {
            SB.append("<span style=\"color:gray\">");
            SB.append(message);
            SB.append("</span><br />");
		}
	}

	public static void addWarningMessage(String message) {
        SB.append("<span style=\"color:orange\">");
        SB.append(message);
        SB.append("</span><br />");
	}

	public static void addErrorMessage(String message) {
        SB.append("<span style=\"color:red\">");
        SB.append(message);
        SB.append("</span><br />");
	}

	public static void exportFile(File logFile) {
		BufferedWriter writer = null;
		String htmlPath = logFile.getParentFile().getPath().concat(File.separator + "log.html");
        try (FileWriter fileWriter = new FileWriter(htmlPath)) {
			writer = new BufferedWriter(fileWriter);
			writer.write("<p>");
            writer.write(SB.toString());
			writer.write("</p>");

			LogRawViewer.loadFile(new File(htmlPath).toURI().toURL().toString());
        } catch (IOException e) {
        	LOG.log(Level.SEVERE, "Exception while exporting file {0}: {1}",new Object[]{logFile,e});
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Exception while writing in file {0}: {1}",new Object[]{logFile,e});
			}
		}
	}

}
