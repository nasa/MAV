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

package gov.nasa.arc.atc.log;

import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 */
public class FXLogger {

    private static final Logger LOG = Logger.getGlobal();

    private static final FXLoggerApp FX_LOGGER_APP;

    static {
        FX_LOGGER_APP = new FXLoggerApp();
    }


    public static void log(Level level, String msg, Object... params) {
        if (params != null && params.length > 0) {
            LOG.log(level, msg, params);
        } else {
            LOG.log(level, msg);
        }
        runLater(() -> FX_LOGGER_APP.log(level, msg, params));

    }

    public static void showConsole() {
        FX_LOGGER_APP.show();
    }

    public static void hideConsole() {
        FX_LOGGER_APP.hide();
    }

    public static FXLoggerApp getFxLoggerApp(){
        return FX_LOGGER_APP;
    }


}
