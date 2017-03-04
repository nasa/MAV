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

package gov.nasa.arc.atc;

import gov.nasa.arc.atc.algos.viewer.reports.ReportApp;
import gov.nasa.arc.atc.metrics.ArgmaxInfo;
import gov.nasa.arc.atc.metrics.SimulationLinkApp;
import gov.nasa.arc.atc.metrics.SimulationMetricsApp;
import gov.nasa.arc.atc.metrics.imagecreation.ImageCreationCreator;
import gov.nasa.arc.atc.viewer.RawDataApp;
import gov.nasa.arc.atc.viewer.flightplan.FlightPlanViewerApp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Kelsey
 * @author ahamon
 */
public final class DisplayViewConfigurations {

    /**
     * name of the property change event fired when a new stage is created
     */
    public static final String NEW_STAGE_EVENT = "newStage";


    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(new DisplayViewConfigurations());

    private static SimulationLinkApp sl = null;

    private static SimulationMetricsApp sm = null;
    private static ReportApp r = null;
    private static RawDataApp rawDataApp = null;
    private static ImageCreationCreator imageCreator = null;
    private static FlightPlanViewerApp flightPlanViewerApp = null;

    /**
     * --------------------------------
     */
    private DisplayViewConfigurations() {
        // private utility constructor
        // !! only called once, for property change support instantiation
    }

    /**
     * @param listener the listener to be added
     */
    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    /**
     * @param listener the listener to be removed
     */
    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

    public static ImageCreationCreator getImageCreator() {
        if (imageCreator == null) {
            imageCreator = new ImageCreationCreator();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_STAGE_EVENT, null, imageCreator);
        }
        return imageCreator;
    }

    public static SimulationLinkApp getSimulationLinkApp() {
        if (sl == null) {
            sl = new SimulationLinkApp();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_STAGE_EVENT, null, sl);
        }
        return sl;
    }

    public static void openSimulation(int time, ArgmaxInfo simulationInfo) {
        getSimulationLinkApp().openSimulationDialog(time, simulationInfo);
    }

    public static SimulationMetricsApp getSM() {
        if (sm == null) {
            sm = new SimulationMetricsApp();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_STAGE_EVENT, null, sm);
        }
        return sm;
    }

    public static SimulationMetricsApp reloadSM() {
        sm = new SimulationMetricsApp();
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_STAGE_EVENT, null, imageCreator);
        return sm;
    }

    public static void openSM() {
        SimulationMetricsApp smApp = DisplayViewConfigurations.getSM();
        smApp.openApp();
    }

    public static void closeSM() {
        SimulationMetricsApp smApp = DisplayViewConfigurations.getSM();
        smApp.closeApp();
    }

    public static ReportApp getReport() {
        if (r == null) {
            r = new ReportApp();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_STAGE_EVENT, null, r);
        }
        return r;
    }

    public static RawDataApp getRawDataApp() {
        if (rawDataApp == null) {
            rawDataApp = new RawDataApp();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_STAGE_EVENT, null, rawDataApp);
        }
        return rawDataApp;
    }

    public static void registerApp(ManagedApp app) {
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_STAGE_EVENT, null, app);
    }

    public static FlightPlanViewerApp getFlightViewerApp() {
        if (flightPlanViewerApp == null) {
            flightPlanViewerApp = new FlightPlanViewerApp();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_STAGE_EVENT, null, flightPlanViewerApp);
        }
        return flightPlanViewerApp;
    }
}
