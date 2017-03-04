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

package gov.nasa.arc.atc.algos.dsas.creator;

import gov.nasa.arc.atc.algos.Algorithm;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.algos.dsas.ParametricDSAS;
import gov.nasa.arc.atc.functions.factories.CandidateFunctionFactory;
import gov.nasa.arc.atc.functions.factories.DefaultCandidateFunctionFactory;
import gov.nasa.arc.atc.functions.factories.GapSchedulingFunctionFactory;
import gov.nasa.arc.atc.functions.factories.MeterFixCandidateFunctionFactory;
import gov.nasa.arc.atc.functions.factories.SimpleGapSchedulingFunctionFactory;
import gov.nasa.arc.atc.geography.Runway;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

/**
 * 
 * @author ahamon
 *
 */
public class ParametricDSASCreatorFxControler implements Initializable {

	private static final Logger LOG = Logger.getGlobal();

	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox<CandidateFunctionFactory> slotsCandidateFunctionBox;
	@FXML
	private ComboBox<CandidateFunctionFactory> depCandidateFunctionBox;
	@FXML
	private ComboBox<GapSchedulingFunctionFactory> gapSchedulingFunctionBox;
	@FXML
	private ComboBox<Runway> arrivalRunwayBox;
	@FXML
	private ComboBox<Runway> departureRunwayBox;

	private SimulationContext context;

	private CandidateFunctionFactory slotCandidateFunctionFactory;
	private CandidateFunctionFactory departureCandidateFunctionFactory;
	private GapSchedulingFunctionFactory gapSchedulingFunctionFactory;
	private Runway arrivalRunway;
	private Runway departureRunway;

    private final BooleanProperty configurationOK = new SimpleBooleanProperty(false);

	/**
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initSlotCandiateFunctions();
		initDepartureCandiateFunctions();
		initGapSchedulingFunctions();
		initArrivalRunwayBox();
		initDepartureRunwayBox();
	}

	public void setSimulationContext(SimulationContext simulationContext) {
		context = simulationContext;
		updateRunways();
	}

	protected Algorithm createDSAS() {
		ParametricDSAS pDSAS = new ParametricDSAS(slotCandidateFunctionFactory.createFunction(), departureCandidateFunctionFactory.createFunction(), gapSchedulingFunctionFactory.createFunction());
		pDSAS.initializeData(context, departureRunway, arrivalRunway);
		return pDSAS;
	}

	protected boolean isConfigurationOK() {
		return configurationOK.get();
	}

	protected void addConfigurationPropertyListener(ChangeListener<Boolean> listener) {
		configurationOK.addListener(listener);
	}

	/*
	 * Initialization methods
	 */

	private void initSlotCandiateFunctions() {
		// TEMP
		slotsCandidateFunctionBox.getItems().add(new DefaultCandidateFunctionFactory());
		slotsCandidateFunctionBox.getItems().add(new MeterFixCandidateFunctionFactory());
		// END TEMP
		slotsCandidateFunctionBox.setButtonCell(new CandidateFunctionFactoryListCell());
		slotsCandidateFunctionBox.setCellFactory(p -> {
			LOG.log(Level.INFO, "setting slotsCandidateFunctionBox cell factory for {0}", p);
			return new CandidateFunctionFactoryListCell();
		});
		slotsCandidateFunctionBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			Node node = getNodeFromGridPane(2, 0);
			if (node != null) {
				gridPane.getChildren().remove(node);
			}
			slotCandidateFunctionFactory = newValue;
			slotCandidateFunctionFactory.getFunctionConfigurator().setScenario(context);
			slotCandidateFunctionFactory.getFunctionConfigurator().addConfigOKListener(this::booleanPropertyListener);
			updateSlotCondidateFunctionConfigurator();
		});
	}

	private void initDepartureCandiateFunctions() {
		// TEMP
		depCandidateFunctionBox.getItems().add(new DefaultCandidateFunctionFactory());
		depCandidateFunctionBox.getItems().add(new MeterFixCandidateFunctionFactory());
		// END TEMP
		depCandidateFunctionBox.setButtonCell(new CandidateFunctionFactoryListCell());
		depCandidateFunctionBox.setCellFactory(p -> {
			LOG.log(Level.INFO, "setting depCandidateFunctionBox cell factory for {0}", p);
			return new CandidateFunctionFactoryListCell();
		});
		depCandidateFunctionBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			Node node = getNodeFromGridPane(2, 1);
			if (node != null) {
				gridPane.getChildren().remove(node);
			}
			departureCandidateFunctionFactory = newValue;
			departureCandidateFunctionFactory.getFunctionConfigurator().setScenario(context);
			departureCandidateFunctionFactory.getFunctionConfigurator().addConfigOKListener(this::booleanPropertyListener);
			updateDepartureCondidateFunctionConfigurator();
		});
	}

	private void initGapSchedulingFunctions() {
		// TEMP
		gapSchedulingFunctionBox.getItems().add(new SimpleGapSchedulingFunctionFactory());
		// END TEMP
		gapSchedulingFunctionBox.setButtonCell(new GapSchedulingFunctionFactoryListCell());
		gapSchedulingFunctionBox.setCellFactory(p -> {
			LOG.log(Level.INFO, "setting gapSchedulingFunctionBox cell factory for {0}", p);
			return new GapSchedulingFunctionFactoryListCell();
		});
		gapSchedulingFunctionBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			Node node = getNodeFromGridPane(2, 2);
			if (node != null) {
				gridPane.getChildren().remove(node);
			}
			gapSchedulingFunctionFactory = newValue;
			gapSchedulingFunctionFactory.getFunctionConfigurator().setScenario(context);
			gapSchedulingFunctionFactory.getFunctionConfigurator().addConfigOKListener(this::booleanPropertyListener);
			updateGapSchedulingFunctionConfigurator();
		});
	}

	private void initArrivalRunwayBox() {
		arrivalRunwayBox.setButtonCell(new RunwayListCell());
		arrivalRunwayBox.setCellFactory(p -> {
			LOG.log(Level.INFO, "setting arrivalRunwayBox cell factory for {0}", p);
			return new RunwayListCell();
		});
		arrivalRunwayBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			arrivalRunway = newValue;
			checkConfiguration();
		});
	}

	private void initDepartureRunwayBox() {
		departureRunwayBox.setButtonCell(new RunwayListCell());
		departureRunwayBox.setCellFactory(p -> {
			LOG.log(Level.INFO, "setting departureRunwayBox cell factory for {0}", p);
			return new RunwayListCell();
		});
		departureRunwayBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			departureRunway = newValue;
			checkConfiguration();
		});
	}

	private void updateRunways() {
		arrivalRunwayBox.getItems().clear();
		departureRunwayBox.getItems().clear();
		if (context != null) {
			List<Runway> runways = new ArrayList<>();
			context.getGeography().getAirports().forEach(airport -> airport.getRunways().forEach(runways::add));
			arrivalRunwayBox.getItems().setAll(runways);
			departureRunwayBox.getItems().setAll(runways);
		}
	}

	private void updateSlotCondidateFunctionConfigurator() {
		if (slotCandidateFunctionFactory != null) {
			Node configNode = slotCandidateFunctionFactory.getFunctionConfigurator().getNode();
			gridPane.getChildren().add(configNode);
			GridPane.setColumnIndex(configNode, 2);
			GridPane.setRowIndex(configNode, 0);
		}
		checkConfiguration();
	}

	private void updateDepartureCondidateFunctionConfigurator() {
		if (departureCandidateFunctionFactory != null) {
			Node configNode = departureCandidateFunctionFactory.getFunctionConfigurator().getNode();
			gridPane.getChildren().add(configNode);
			GridPane.setColumnIndex(configNode, 2);
			GridPane.setRowIndex(configNode, 1);
		}
		checkConfiguration();
	}

	private void updateGapSchedulingFunctionConfigurator() {
		if (gapSchedulingFunctionFactory != null) {
			Node configNode = gapSchedulingFunctionFactory.getFunctionConfigurator().getNode();
			gridPane.getChildren().add(configNode);
			GridPane.setColumnIndex(configNode, 2);
			GridPane.setRowIndex(configNode, 2);
		}
		checkConfiguration();
	}

	private Node getNodeFromGridPane(int column, int row) {
		int nodeColumnIndex;
		int nodeRowIndex;
		for (Node node : gridPane.getChildren()) {
			nodeColumnIndex = GridPane.getColumnIndex(node) != null ? GridPane.getColumnIndex(node) : -1;
			nodeRowIndex = GridPane.getRowIndex(node) != null ? GridPane.getRowIndex(node) : -1;
			if (nodeColumnIndex == column && nodeRowIndex == row) {
				return node;
			}
		}
		return null;
	}

	private void booleanPropertyListener(Observable obs, Boolean oldValue, Boolean newValue) {
		LOG.log(Level.FINE, "booleanPropertyListener {0} {1} {2}", new Object[] { obs, oldValue, newValue });
		checkConfiguration();
	}

	private void checkConfiguration() {
		boolean ok = slotCandidateFunctionFactory != null ? slotCandidateFunctionFactory.getFunctionConfigurator().isConfigurationOK() : false;
		ok = ok && departureCandidateFunctionFactory != null ? departureCandidateFunctionFactory.getFunctionConfigurator().isConfigurationOK() : false;
		ok = ok && gapSchedulingFunctionFactory != null ? gapSchedulingFunctionFactory.getFunctionConfigurator().isConfigurationOK() : false;
		ok = ok && arrivalRunway != null;
		ok = ok && departureRunway != null;
		configurationOK.set(ok);
	}

	/*
	 * Rendering class, to be moved in a while and factor the code in one unique class
	 */

	private class CandidateFunctionFactoryListCell extends ListCell<CandidateFunctionFactory> {
		@Override
		protected void updateItem(CandidateFunctionFactory item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null) {
				setText(item.getClass().getSimpleName());
			}
		}
	}

	private class GapSchedulingFunctionFactoryListCell extends ListCell<GapSchedulingFunctionFactory> {
		@Override
		protected void updateItem(GapSchedulingFunctionFactory item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null) {
				setText(item.getClass().getSimpleName());
			}
		}
	}

	private class RunwayListCell extends ListCell<Runway> {
		@Override
		protected void updateItem(Runway item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null) {
				int nbArrivals = context.getAllArrivals().get(item.getName()).size();
				int nbDepartures = context.getAllDepartures().get(item.getName()).size();
				StringBuilder sb = new StringBuilder();
				sb.append("Airport: ").append(item.getAirport().getName());
				sb.append(" - Runway: ").append(item.getName());
				sb.append(" (Arr=").append(nbArrivals);
				sb.append(" Dep=").append(nbDepartures);
				sb.append(")");
				setText(sb.toString());
			}
		}
	}

}
