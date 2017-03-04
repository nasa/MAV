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

package gov.nasa.arc.atc.viewer;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.SimulatedFlyingElement;
import gov.nasa.arc.atc.metrics.Metrics;
import gov.nasa.arc.atc.metrics.SimulationCalculations;
import gov.nasa.arc.atc.utils.CustomReadOnlyStringWrapper;
import gov.nasa.arc.atc.utils.IndexComparator;
import gov.nasa.arc.atc.utils.SimulatedFlyingElementNameCell;
import gov.nasa.arc.atc.metrics.MetricsUtil;

import static javafx.application.Platform.runLater;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * 
 * @author ahamon
 *
 */
public class RawViewerController implements Initializable {

	private static final Logger LOG = Logger.getGlobal();

	@FXML
	private ListView<SimulatedFlyingElement> afoList;
	@FXML
	private CheckBox departureCheckB;
	@FXML
	private CheckBox arrivalCheckB;
	@FXML
	private TableView<AfoUpdate> updatesTable;

	private DataModel dataModel = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initTable();
		afoList.setCellFactory(cell -> new SimulatedFlyingElementNameCell());
		afoList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			SimulatedFlyingElement itemSelected = afoList.getSelectionModel().getSelectedItem();
			if (itemSelected != null && dataModel != null) {
				updatesTable.getItems().setAll(dataModel.getAllDataUpdates().get(itemSelected.getFullName()).values());
			}
		});
		arrivalCheckB.selectedProperty().addListener(this::handleCheckBoxChange);
		departureCheckB.selectedProperty().addListener(this::handleCheckBoxChange);
		departureCheckB.setSelected(true);
		arrivalCheckB.setSelected(false);
	}

	public void setDataModel(DataModel model) {
		dataModel = model;
		updateItemList();
	}

	private void initTable() {
		updatesTable.getColumns().clear();
		//
		// Creating time column
		TableColumn<AfoUpdate, String> timeColumn = new TableColumn<>("TIME");
		timeColumn.setPrefWidth(75);
		timeColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getTimeStamp())));
		timeColumn.setComparator(new IndexComparator());

		// Creating status column
		TableColumn<AfoUpdate, String> statusColumn = new TableColumn<>("STATUS");
		statusColumn.setPrefWidth(75);
		statusColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getStatus())));

		// Creating ETA column
		TableColumn<AfoUpdate, String> etaColumn = new TableColumn<>("ETA");
		etaColumn.setPrefWidth(75);
		etaColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new ReadOnlyStringWrapper(Integer.toString((int) param.getValue().getEta())));

		// Creating position column
		TableColumn<AfoUpdate, String> positionColumn = new TableColumn<>("POSITION");
		positionColumn.setPrefWidth(250);
		positionColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper(param.getValue().getPosition().toString()));

		// Creating air speed column
		TableColumn<AfoUpdate, String> airSpeedColumn = new TableColumn<>("AIR SPEED");
		airSpeedColumn.setPrefWidth(75);
		airSpeedColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper(Double.toString(param.getValue().getAirSpeed())));

		// Creating vertical speed column
		TableColumn<AfoUpdate, String> vSpeedColumn = new TableColumn<>("V SPEED");
		vSpeedColumn.setPrefWidth(75);
		vSpeedColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper(Double.toString(param.getValue().getVSpeed())));

		// Creating start time column
		TableColumn<AfoUpdate, String> startTimeColumn = new TableColumn<>("START TIME");
		startTimeColumn.setPrefWidth(100);
		startTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new ReadOnlyStringWrapper(Integer.toString(param.getValue().getStartTime())));

		// Creating controller column
		TableColumn<AfoUpdate, String> controllerColumn = new TableColumn<>("CONTROLLER");
		controllerColumn.setPrefWidth(100);
		controllerColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper(param.getValue().getController()));

		// Creating isMetering column
		TableColumn<AfoUpdate, String> isMeteringColumn = new TableColumn<>("IS METERING");
		isMeteringColumn.setPrefWidth(100);
		isMeteringColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new ReadOnlyStringWrapper(Integer.toString(param.getValue().isMetering())));

		// Creating segment column
		TableColumn<AfoUpdate, String> segmentColumn = new TableColumn<>("SEG");
		segmentColumn.setPrefWidth(50);
		segmentColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new ReadOnlyStringWrapper(Integer.toString(param.getValue().getCurrentSegment())));

		// Creating to wpt column
		TableColumn<AfoUpdate, String> toWaypointColumn = new TableColumn<>("TO WPT");
		toWaypointColumn.setPrefWidth(75);
		toWaypointColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new ReadOnlyStringWrapper(param.getValue().getToWaypoint()));

		// Creating is departure column
		TableColumn<AfoUpdate, String> isDepartureColumn = new TableColumn<>("IS DEPARTURE");
		isDepartureColumn.setPrefWidth(75);
//		isDepartureColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper(Boolean.toString(param.getValue().isDeparture())));
		isDepartureColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper("TODO"));

		// Creating sector column ?
		TableColumn<AfoUpdate, String> sectorColumn = new TableColumn<>("SECTOR");
		sectorColumn.setPrefWidth(75);
		sectorColumn.setCellValueFactory((TableColumn.CellDataFeatures<AfoUpdate, String> param) -> new CustomReadOnlyStringWrapper(Metrics.getSimulationCalculation(dataModel).findSector(param.getValue().getController())));

		//
		updatesTable.getColumns().add(timeColumn);
		updatesTable.getColumns().add(statusColumn);
		updatesTable.getColumns().add(etaColumn);
		updatesTable.getColumns().add(positionColumn);
		updatesTable.getColumns().add(airSpeedColumn);
		updatesTable.getColumns().add(vSpeedColumn);
		updatesTable.getColumns().add(startTimeColumn);
		updatesTable.getColumns().add(controllerColumn);
		updatesTable.getColumns().add(isMeteringColumn);
		updatesTable.getColumns().add(segmentColumn);
		updatesTable.getColumns().add(toWaypointColumn);
		updatesTable.getColumns().add(isDepartureColumn);
		//
		updatesTable.getColumns().add(sectorColumn); // move below

		timeColumn.setSortable(true);
	}

	private void handleCheckBoxChange(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
		LOG.log(Level.FINE, "handleCheckBoxChange :: {0} {1} {2}", new Object[] { obs, oldValue, newValue });
		updateItemList();
	}

	private void updateItemList() {
		afoList.getItems().setAll(FXCollections.emptyObservableList());
		if (dataModel == null) {
			return;
		}
		runLater(() -> {
			if (departureCheckB.isSelected() && arrivalCheckB.isSelected()) {
				afoList.getItems().setAll(dataModel.getAllPlanes());
			} else if (departureCheckB.isSelected()) {
				afoList.getItems().setAll(dataModel.getDepartingPlanes());
			} else if (arrivalCheckB.isSelected()) {
				afoList.getItems().setAll(dataModel.getArrivingPlanes());
			}
		});
	}

}
