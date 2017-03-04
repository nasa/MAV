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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.arc.atc.viewer;

import gov.nasa.arc.atc.DisplayType;
import gov.nasa.arc.atc.SimulationEngine;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.Sector;
import gov.nasa.arc.atc.simulation.SeparationViolation;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static javafx.application.Platform.runLater;

/**
 * FXML Controller class for the right application panel
 *
 * @author hamon
 */
public class RightPanelController implements Initializable {

    private static final Logger LOG = Logger.getLogger(RightPanelController.class.getName());

    private final Map<Sector, Button> sectorZoomButtons = new HashMap<>();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(RightPanelController.this);

    /*
     * GENERAL
     */
    @FXML
    private ChoiceBox<DisplayType> displayModeBox;
    @FXML
    private CheckBox showMapCheckB;

    /*
     * Aircrafts
     */
    @FXML
    private CheckBox showAircraftsCheckB;
    @FXML
    private CheckBox showAircraftsNameCheckB;
    @FXML
    private CheckBox showAircraftsGhostCheckB;

    /*
     * Airports
     */
    @FXML
    private CheckBox showAirportsCheckB;

    /*
     * Airspaces
     */
    @FXML
    private CheckBox showAirspaceCheckB;

    /*
     * Routes & Segments
     */
    @FXML
    private CheckBox showRoutesCheckB;
    @FXML
    private CheckBox showSegmentsCheckB;

    /*
     * Slots
     */
    @FXML
    private CheckBox showSlotsCheckB;
    @FXML
    private CheckBox showSlotsNameCheckB;
    @FXML
    private CheckBox showSlotsGhostsCheckB;

    /*
     * Waypoints
     */
    @FXML
    private CheckBox showWayPointCheckB;
    @FXML
    private CheckBox showWayPointNameCheckB;

    @FXML
    private VBox displayOptionsVBox;
    @FXML
    private TableView<NewPlane> departureTable;
    @FXML
    private ListView<SeparationViolation> separationVList;
    @FXML
    private Button zoomAllElementsB;
    @FXML
    private Button zoomAllSectorsB;

    private DataModel dataModel = null;
    private ATCGeography atcGeography = null;

    private TableColumn<NewPlane, String> simpleDepartureColumn;
    private List<NewPlane> departures;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showWayPointCheckB.setSelected(true);
        showWayPointNameCheckB.setSelected(false);
        showWayPoints(true);
        showWayPointsName(false);
        displayModeBox.getItems().setAll(DisplayType.values());
        displayModeBox.getSelectionModel().select(DisplayOptionsManager.getDisplayType());
        displayModeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            LOG.log(Level.FINE, "displayModeBox selectedItemProperty changed:: {0} {1} {2}", new Object[]{observable, oldValue, newValue});
            DisplayOptionsManager.setDisplayType(displayModeBox.getSelectionModel().getSelectedItem());
        });
        initDepartureQueue();
        initStaticZoomButtons();
        SimulationManager.addPropertyChangeListener(this::handleSimulationChanged);
        SimulationEngine.addPropertyChangeListener(this::handleSimulationTimeChanged);
        //
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }


	/*
     * MAP
	 */

    /**
     * Modifies the map's background visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowMapAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowMapAction {0}", event);
        showMap(showMapCheckB.isSelected());
    }

	/*
     * WAYPOINTS
	 */

    /**
     * Modifies the waypoints' name visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowWayPointNameAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowWayPointNameAction {0}", event);
        showWayPointsName(showWayPointNameCheckB.isSelected());
    }

    /**
     * Modifies the waypoints visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowWayPointAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowWayPointAction {0}", event);
        showWayPoints(showWayPointCheckB.isSelected());
        showWayPointNameCheckB.setDisable(!showWayPointCheckB.isSelected());
    }

	/*
     * AIRCRAFTS
	 */

    /**
     * Modifies the aircrafts' visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowAircraftsAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowAircraftsAction {0}", event);
        showAircrafts(showAircraftsCheckB.isSelected());
        showAircraftsNameCheckB.setDisable(!showAircraftsCheckB.isSelected());
    }

    /**
     * Modifies aircraft's name visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowAircraftsNameAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowAircraftsNameAction {0}", event);
        showAircraftsName(showAircraftsNameCheckB.isSelected());
    }

    /**
     * Modifies the aircraft's ghost visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowAircraftsGhostsAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowAircraftsGhostsAction {0}", event);
        showAircraftsGhost(showAircraftsGhostCheckB.isSelected());
    }

	/*
     * SLOTS
	 */

    /**
     * Modifies the slots' visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowSlotsAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowSlotsAction {0}", event);
        showSlots(showSlotsCheckB.isSelected());
        showSlotsNameCheckB.setDisable(!showSlotsCheckB.isSelected());
    }

    /**
     * Modifies the slots' name visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowSlotsNameAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowSlotsNameAction {0}", event);
        showSlotsNames(showSlotsNameCheckB.isSelected());
    }

    /**
     * Modifies the slots' ghost visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowSlotsGhostAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowSlotsGhostAction {0}", event);
        showSlotsGhosts(showSlotsGhostsCheckB.isSelected());
    }

	/*
	 * ROUTES
	 */

    /**
     * Modifies the routes' visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowRoutesAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowRoutesAction {0}", event);
        showRoutes(showRoutesCheckB.isSelected());
    }

    /**
     * Modifies the segments' visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowSegmentsAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowSegmentsAction {0}", event);
        showSegments(showSegmentsCheckB.isSelected());
    }

	/*
	 * AIRPORTS
	 */

    /**
     * Modifies the airports' visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowAirportsAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowAirportsAction {0}", event);
        showAirports(showAirportsCheckB.isSelected());
    }

	/*
	 * SECTORS
	 */

    /**
     * Modifies the sectors' visibility
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onShowAirspaceAction(ActionEvent event) {
        LOG.log(Level.FINE, "onShowAirspaceAction {0}", event);
        showSectors(showAirspaceCheckB.isSelected());
    }

	/*
	 * PRIVATE METHODS
	 */

    private void initStaticZoomButtons() {
        zoomAllElementsB.setDisable(true);
        zoomAllSectorsB.setDisable(true);
        zoomAllElementsB.setOnAction(this::handleZoomAllAction);
        zoomAllSectorsB.setOnAction(this::handleZoomSectorsAction);
    }

    private void handleSimulationChanged(PropertyChangeEvent event) {
        if (SimulationManager.DATA_MODEL_CHANGED.equals(event.getPropertyName())) {
            dataModel = (DataModel) event.getNewValue();
            updateDisplayControlsOnDataModelChange();
        } else if (SimulationManager.ATC_GEOGRAPHY_CHANGED.equals(event.getPropertyName())) {
            atcGeography = (ATCGeography) event.getNewValue();
            updateGeographyControls();
        }
    }

    private void handleSimulationTimeChanged(PropertyChangeEvent event) {
        if (SimulationEngine.SIMULATION_TIME_CHANGED.equals(event.getPropertyName()) && dataModel != null) {
            final int time = (int) event.getNewValue();
            Platform.runLater(() -> {
                updateSeparationViolations(time);
                departures = dataModel.getDepartureQueue().getCurrentDepartureQueue();
                departureTable.getItems().clear();
                departureTable.getItems().setAll(departures);
            });
        }
    }

    private void handleZoomAllAction(ActionEvent event) {
        LOG.log(Level.FINE, "handleZoomAllAction {0}", event);
        propertyChangeSupport.firePropertyChange("zoomBoxChange", null, atcGeography.getElements().stream().map(e -> new Coordinates(e.getLatitude(), e.getLongitude())).collect(Collectors.toList()));
    }

    private void handleZoomSectorsAction(ActionEvent event) {
        LOG.log(Level.FINE, "handleZoomSectorsAction {0}", event);
        List<Coordinates> coordinates = new LinkedList<>();
        atcGeography.getSectors().forEach(sector -> sector.getRegions().forEach(region -> coordinates.addAll(region.getVertices())));
        propertyChangeSupport.firePropertyChange("zoomBoxChange", null, coordinates);

    }

    private void showMap(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.BACKGROUND_MAP_VISIBILITY, visibility);
    }

    private void showWayPoints(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.WAY_POINT_VISIBILITY, visibility);
    }

    private void showWayPointsName(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.WAY_POINT_NAME_VISIBILITY, visibility);
    }

    private void showAircrafts(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.AIRCRAFT_VISIBILITY, visibility);
    }

    private void showAircraftsName(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.AIRCRAFT_NAME_VISIBILITY, visibility);
    }

    private void showAircraftsGhost(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.AIRCRAFT_GHOST_VISIBILITY, visibility);
    }

    private void showSlots(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.SLOT_VISIBILITY, visibility);
    }

    private void showSlotsNames(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.SLOT_NAME_VISIBILITY, visibility);
    }

    private void showSlotsGhosts(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.SLOT_GHOST_VISIBILITY, visibility);
    }

    private void showRoutes(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.ROUTE_VISIBILITY, visibility);
    }

    private void showSegments(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.SEGMENT_VISIBILITY, visibility);
    }

    private void showAirports(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.AIRPORT_VISIBILITY, visibility);
    }

    private void showSectors(boolean visibility) {
        DisplayOptionsManager.setDisplayOption(DisplayOptionsManager.SECTOR_VISIBILITY, visibility);
    }

    private void updateGeographyControls() {
        runLater(() -> {
            sectorZoomButtons.forEach((sector, button) -> displayOptionsVBox.getChildren().remove(button));
            sectorZoomButtons.clear();
            if (atcGeography != null) {
                atcGeography.getSectors().forEach(this::addSectorButton);
            }
            zoomAllElementsB.setDisable(atcGeography == null);
            zoomAllSectorsB.setDisable(atcGeography == null);
        });
    }

    private void addSectorButton(Sector sector) {
        final Button sectorButton = new Button("Zoom on sector: " + sector.getName());
        sectorButton.setMaxWidth(Double.MAX_VALUE);
        sectorZoomButtons.put(sector, sectorButton);
        sectorButton.setOnAction(event -> {
            LOG.log(Level.FINE, "click on button {0}, event={1}", new Object[]{sectorButton.getText(), event});
            List<Coordinates> coordinates = new LinkedList<>();
            sector.getRegions().forEach(region -> coordinates.addAll(region.getVertices()));
            propertyChangeSupport.firePropertyChange("zoomBoxChange", null, coordinates);
        });
        displayOptionsVBox.getChildren().add(sectorButton);
    }

    private void initDepartureQueue() {
//        departureTable.getColumns().clear();
//
//        // Creating index column
//        TableColumn<DepartureInfo, String> indexColumn = new TableColumn<>("INDEX");
//        indexColumn.setPrefWidth(25);
//        indexColumn.setCellValueFactory((TableColumn.CellDataFeatures<DepartureInfo, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getQueueIndex())));
//        indexColumn.setComparator(new IndexComparator());
//
//        // Creating name column
//        TableColumn<DepartureInfo, String> nameColumn = new TableColumn<>("NAME");
//        nameColumn.setPrefWidth(100);
//        nameColumn.setCellValueFactory((TableColumn.CellDataFeatures<DepartureInfo, String> param) -> new CustomReadOnlyStringWrapper(param.getValue().getPlaneName()));
//
//        // Creating original departure time column
//        TableColumn<DepartureInfo, String> originalDepTimeColumn = new TableColumn<>("ORIGINAL DEPARTURE");
//        originalDepTimeColumn.setPrefWidth(50);
//        originalDepTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<DepartureInfo, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getOriginalDepartureTime())));
//
//        // Creating current departure time column
//        TableColumn<DepartureInfo, String> currentDepTimeColumn = new TableColumn<>("CURRENT DEPARTURE");
//        currentDepTimeColumn.setPrefWidth(50);
//        currentDepTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<DepartureInfo, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getCurrentDepartureTime())));
//
//        departureTable.getColumns().add(indexColumn);
//        departureTable.getColumns().add(nameColumn);
//        departureTable.getColumns().add(originalDepTimeColumn);
//        departureTable.getColumns().add(currentDepTimeColumn);
//        indexColumn.setSortable(true);
        //

        simpleDepartureColumn = new TableColumn<>("Departures");
        simpleDepartureColumn.setPrefWidth(200);
        simpleDepartureColumn.setCellValueFactory((TableColumn.CellDataFeatures<NewPlane, String> param) -> new ReadOnlyStringWrapper("(" + departures.indexOf(param.getValue()) + ") - " + param.getValue().getSimpleName()));
        departureTable.getColumns().add(simpleDepartureColumn);
        simpleDepartureColumn.setSortable(true);
    }

    private void updateDisplayControlsOnDataModelChange() {
        boolean withGhost = dataModel != null && dataModel.hasGhostModel();
        showAircraftsGhostCheckB.setDisable(!withGhost);
        showSlotsGhostsCheckB.setDisable(!withGhost);
        showAircraftsGhostCheckB.setSelected(withGhost);
        showSlotsGhostsCheckB.setSelected(withGhost);
        showAircraftsGhost(withGhost);
        showSlotsGhosts(withGhost);
    }

    private void updateSeparationViolations(final int time) {
        if (dataModel != null) {
            final List<SeparationViolation> list = dataModel.getSeparationViolators().get(time);
            if (list != null) {
                separationVList.getItems().setAll(list);
            } else {
                separationVList.getItems().clear();
            }
        }
    }

    private class CustomReadOnlyStringWrapper extends ReadOnlyStringWrapper {

        private CustomReadOnlyStringWrapper(String arg0) {
            super(arg0);
        }

        @Override
        public String getValue() {
            if ("0".equals(super.getValue())) {
                return "";
            }
            return super.getValue();
        }
    }

	/*
	 * IndexComparator
	 */

    private class IndexComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int i1;
            int i2;
            if ("".equals(o1)) {
                i1 = 0;
            } else {
                i1 = Integer.parseInt(o1);
            }
            if ("".equals(o2)) {
                i2 = 0;
            } else {
                i2 = Integer.parseInt(o2);
            }
            return Integer.compare(i1, i2);
        }

    }

}
