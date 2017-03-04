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

package gov.nasa.arc.atc.geography;

import gov.nasa.arc.atc.core.Controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ahamon
 */
public class Sector {

    public static final String NB_AIRCRAFT_CHANGED = "nbAircraftChanged";

    private final PropertyChangeSupport propertyChangeSupport;
    private final String name;
    private final int iD;
    private final List<Region> regions;
    private Controller controller;

    //private int nbAircrafts; // ???
    // new variables
//	private final int maxCapacity;
//	private int currCapacity; // ????
    //private Controller controller;
//	private double perceptualWorkload;
//	private double temporalWorkload;
//	private double decisionWorkload;
//	private String time;
    //

    // dont change
    public Sector(String sectorName, int sectorID) {
        name = sectorName;
        iD = sectorID;
        regions = new ArrayList<>();
        controller = null;
        propertyChangeSupport = new PropertyChangeSupport(Sector.this);
    }

    public Sector(String sectorName, int sectorID, Controller aController) {
        name = sectorName;
        iD = sectorID;
        regions = new ArrayList<>();
        controller = aController;

        propertyChangeSupport = new PropertyChangeSupport(Sector.this);
    }

    public void addRegion(Region region) {
        regions.add(region);
    }

    public List<Region> getRegions() {
        return Collections.unmodifiableList(regions);
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return iD;
    }

    @Override
    public String toString() {
        return "Sector: " + name + " iD=" + iD;
    }

    public void addPropertyListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public Controller getController() {
        return controller;
    }

    @SuppressWarnings("static-access")
    protected void assignController(Controller aController) {
        controller = aController;
        // to be sure the new controller has the right number of aircrafts
        //setNbAircrafts(nbAircrafts);
    }


    public boolean containsCoordinate(double latitude, double longitude) {
        return getRegions().stream().anyMatch(region -> region.containsPoint(latitude, longitude));
    }
}
