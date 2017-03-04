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

package gov.nasa.arc.atc.algos.tss.creator;

import gov.nasa.arc.atc.algos.Algorithm;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.algos.tss.TSS;
import gov.nasa.arc.atc.functions.factories.CandidateFunctionFactory;
import gov.nasa.arc.atc.functions.factories.DefaultCandidateFunctionFactory;
import gov.nasa.arc.atc.functions.factories.DistanceSeparationFunctionFactory;
import gov.nasa.arc.atc.functions.factories.MeterFixCandidateFunctionFactory;
import gov.nasa.arc.atc.functions.factories.SeparationFunctionFactory;
import gov.nasa.arc.atc.geography.Runway;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
public class TSSCreatorFxControler implements Initializable {

	private static final Logger LOG = Logger.getGlobal();

	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox<CandidateFunctionFactory> candidateFunctionBox;
	@FXML
	private ComboBox<SeparationFunctionFactory> separationFunctionBox;
	@FXML
	private ComboBox<Runway> runwayBox;

    private final BooleanProperty configurationOK = new SimpleBooleanProperty(false);

    private SimulationContext context;
    private Runway runway;

	private CandidateFunctionFactory candidateFunctionFactory;
    private SeparationFunctionFactory separationFunctionFactory;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCandiateFunctions();
		initSeparationFunctions();
		initRunwayBox();
	}

	public void setSimulationContext(SimulationContext simulationContext) {
		context = simulationContext;
		updateRunways();
	}

	protected Algorithm createTSS() {
		TSS tss = new TSS(Collections.emptyMap(), candidateFunctionFactory.createFunction(), separationFunctionFactory.createFunction());
		tss.initializeData(context, runway);
		return tss;
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

	private void initCandiateFunctions() {
		// TEMP
		candidateFunctionBox.getItems().add(new DefaultCandidateFunctionFactory());
		candidateFunctionBox.getItems().add(new MeterFixCandidateFunctionFactory());
		// END TEMP
		candidateFunctionBox.setButtonCell(new CandidateFunctionFactoryListCell());
		candidateFunctionBox.setCellFactory(p -> {
			LOG.log(Level.INFO, "setting candidateFunctionBox cell factory for {0}", p);
			return new CandidateFunctionFactoryListCell();
		});
		candidateFunctionBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			Node node = getNodeFromGridPane(2, 0);
			if (node != null) {
				gridPane.getChildren().remove(node);
			}
			candidateFunctionFactory = newValue;
			candidateFunctionFactory.getFunctionConfigurator().setScenario(context);
			candidateFunctionFactory.getFunctionConfigurator().addConfigOKListener(this::booleanPropertyListener);
			updateCandidateFunctionConfigurator();
		});
	}

	private void initSeparationFunctions() {
		// TEMP
		separationFunctionBox.getItems().add(new DistanceSeparationFunctionFactory());
		// END TEMP
		separationFunctionBox.setButtonCell(new SeparationFunctionFactoryListCell());
		separationFunctionBox.setCellFactory(p -> {
			LOG.log(Level.INFO, "setting candidateFunctionBox cell factory for {0}", p);
			return new SeparationFunctionFactoryListCell();
		});
		separationFunctionBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			Node node = getNodeFromGridPane(2, 1);
			if (node != null) {
				gridPane.getChildren().remove(node);
			}
			separationFunctionFactory = newValue;
			separationFunctionFactory.getFunctionConfigurator().setScenario(context);
			separationFunctionFactory.getFunctionConfigurator().addConfigOKListener(this::booleanPropertyListener);
			updateSeparationFunctionConfigurator();
		});
	}

	private void initRunwayBox() {
		runwayBox.setButtonCell(new RunwayListCell());
		runwayBox.setCellFactory(p -> {
			LOG.log(Level.INFO, "setting runwayBox cell factory for {0}", p);
			return new RunwayListCell();
		});
		runwayBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			runway = newValue;
			checkConfiguration();
		});
	}

	private void updateRunways() {
		runwayBox.getItems().clear();
		if (context != null) {
			List<Runway> runways = new ArrayList<>();
			context.getGeography().getAirports().forEach(airport -> airport.getRunways().forEach(runways::add));
			runwayBox.getItems().setAll(runways);
		}
	}

	private void updateCandidateFunctionConfigurator() {
		if (candidateFunctionFactory != null) {
			Node configNode = candidateFunctionFactory.getFunctionConfigurator().getNode();
			gridPane.getChildren().add(configNode);
			GridPane.setColumnIndex(configNode, 2);
			GridPane.setRowIndex(configNode, 0);
		}
		checkConfiguration();
	}

	private void updateSeparationFunctionConfigurator() {
		if (separationFunctionFactory != null) {
			Node configNode = separationFunctionFactory.getFunctionConfigurator().getNode();
			gridPane.getChildren().add(configNode);
			GridPane.setColumnIndex(configNode, 2);
			GridPane.setRowIndex(configNode, 1);
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
		checkConfiguration();
	}

	private void checkConfiguration() {
		boolean ok = candidateFunctionFactory != null ? candidateFunctionFactory.getFunctionConfigurator().isConfigurationOK() : false;
		ok = ok && runway != null;
		ok = ok && separationFunctionFactory != null ? separationFunctionFactory.getFunctionConfigurator().isConfigurationOK() : false;
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

	private class SeparationFunctionFactoryListCell extends ListCell<SeparationFunctionFactory> {
		@Override
		protected void updateItem(SeparationFunctionFactory item, boolean empty) {
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
