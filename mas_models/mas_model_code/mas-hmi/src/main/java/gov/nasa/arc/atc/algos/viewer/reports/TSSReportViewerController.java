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

package gov.nasa.arc.atc.algos.viewer.reports;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.reports.SlotSummary;
import gov.nasa.arc.atc.reports.InvocationItem;
import gov.nasa.arc.atc.reports.TSSReport;
import gov.nasa.arc.atc.reports.TSSReportItem;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author ahamon
 *
 */
public class TSSReportViewerController implements Initializable {

	private static final Logger LOG = Logger.getGlobal();

	@FXML
	private Accordion tssAccordion;
	@FXML
	private TreeTableView<TSSReportItem> reportTableView;
	@FXML
	private TableView<SlotSummary> acChronologyTableView;
	@FXML
	private LineChart<Number, Number> lineChart;
	@FXML
	private StackedBarChart<String, Number> barChart;
	@FXML
	private BarChart<CategoryAxis, Number> delaysDistribChart;
	@FXML
	private CategoryAxis delaysXAxis;
	@FXML
	private NumberAxis delaysYAxis;
	@FXML
	private NumberAxis cDelaysXAxis;
	@FXML
	private NumberAxis cDelaysYAxis;
	@FXML
	private CategoryAxis delayDistXAxis;
	@FXML
	private NumberAxis delayDistYAxis;
	@FXML
	private TitledPane arrivalsEvolutionTPane;
	@FXML
	private AnchorPane arrivalsEvolutionAPane;

	private SequenceEvolutionController sequenceEvolutionController;

	private TSSReport report;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOG.log(Level.INFO, "initialize URL={0} ResourceBundle={1}", new Object[] { location, resources });
		initSequenceEvolutionView();
	}

	protected void displayReport(TSSReport tssReport) {
		LOG.log(Level.INFO, "displaying report {0}", report);
		report = tssReport;
		//
		createTSSChronology();
		createAircraftChronology();
		createDelayChart();
		createCumulativeDelayGraph();
		createDelayDistributionGraph();
		sequenceEvolutionController.displaySequences(report.getLastInfoTime(), new HashMap<>(), new HashMap<>(), report.getArrivalsIN(), report.getArrivalsOUT());
		tssAccordion.setExpandedPane(tssAccordion.getPanes().get(0));
	}

	private void initSequenceEvolutionView() {
		try {
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("SequenceEvolutionViewer.fxml"));
			fXMLLoader.load();
			AnchorPane arrivalsEvolutionPane = fXMLLoader.getRoot();
			arrivalsEvolutionTPane.setContent(arrivalsEvolutionPane);
			sequenceEvolutionController = fXMLLoader.getController();
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Error while creating TSSReportViewer : {0}", ex);
		}
	}

	private void createTSSChronology() {
		reportTableView.getColumns().clear();
		// Creating the root element
		TSSReportItem rootItem = new RootReportItem(0);
		final TreeItem<TSSReportItem> root = new TreeItem<>(rootItem);
		root.setExpanded(true);
		//
		//
		report.getReportItems().forEach((time, itemList) -> {
			TSSReportItem timeItem = new RootReportItem(time);
			final TreeItem<TSSReportItem> timeNode = new TreeItem<>(timeItem);
			for (TSSReportItem item : itemList) {
				if (item instanceof InvocationItem) {
					// no need to add this one
				} else {
					final TreeItem<TSSReportItem> itemNode = new TreeItem<>(item);
					timeNode.getChildren().add(itemNode);
				}

			}
			timeNode.setExpanded(true);
			root.getChildren().add(timeNode);
		});

		// Creating time column
		TreeTableColumn<TSSReportItem, String> timeColumn = new TreeTableColumn<>("TIME");
		timeColumn.setPrefWidth(100);
		timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TSSReportItem, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getValue().getSimulationTime())));
		timeColumn.setComparator(new TimeComparator());

		// Creating ID column
		TreeTableColumn<TSSReportItem, String> idColumn = new TreeTableColumn<>("ID");
		idColumn.setPrefWidth(75);
		idColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TSSReportItem, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getValue().getID())));
		idColumn.setComparator(new TimeComparator());

		// Creating trail column
		TreeTableColumn<TSSReportItem, String> trailColumn = new TreeTableColumn<>("TRAIL");
		trailColumn.setPrefWidth(100);
		trailColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TSSReportItem, String> param) -> new CustomReadOnlyStringWrapper(param.getValue().getValue().getTrail()));

		// Creating lead column
		TreeTableColumn<TSSReportItem, String> leadColumn = new TreeTableColumn<>("LEAD");
		leadColumn.setPrefWidth(100);
		leadColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TSSReportItem, String> param) -> new CustomReadOnlyStringWrapper(param.getValue().getValue().getLead()));

		// Creating delay column
		TreeTableColumn<TSSReportItem, String> delayColumn = new TreeTableColumn<>("DELAY");
		delayColumn.setPrefWidth(75);
		delayColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TSSReportItem, String> param) -> new CustomReadOnlyStringWrapper(Integer.toString(param.getValue().getValue().getDelay())));

		// Creating node column
		TreeTableColumn<TSSReportItem, String> nodeColumn = new TreeTableColumn<>("At NODE");
		nodeColumn.setPrefWidth(75);
		nodeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TSSReportItem, String> param) -> new CustomReadOnlyStringWrapper("" + param.getValue().getValue().getNodeName()));

		// Creating message column
		TreeTableColumn<TSSReportItem, String> toStringColumn = new TreeTableColumn<>("DETAILED MESSAGE");
		toStringColumn.setPrefWidth(1000);
		toStringColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TSSReportItem, String> param) -> new CustomReadOnlyStringWrapper(param.getValue().getValue().toString()));

		//
		reportTableView.setRoot(root);
		reportTableView.getColumns().add(timeColumn);
		reportTableView.getColumns().add(idColumn);
		reportTableView.getColumns().add(trailColumn);
		reportTableView.getColumns().add(leadColumn);
		reportTableView.getColumns().add(delayColumn);
		reportTableView.getColumns().add(nodeColumn);
		reportTableView.getColumns().add(toStringColumn);
		reportTableView.setShowRoot(false);
		timeColumn.setSortable(true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createAircraftChronology() {
		TableColumn slotNameCol = new TableColumn("Slot");
		TableColumn delayCol = new TableColumn("Delay");
		acChronologyTableView.getColumns().addAll(slotNameCol, delayCol);
		//
		List<SlotSummary> slotSummaries = new ArrayList<>();
		//
		report.getSlotDelays().forEach((slotName, delay) -> {
			final SlotSummary summary = new SlotSummary(slotName, delay);
			slotSummaries.add(summary);
		});
		//
		final ObservableList<SlotSummary> data = FXCollections.observableArrayList(slotSummaries);
		acChronologyTableView.setItems(data);
		//
		slotNameCol.setCellValueFactory(new PropertyValueFactory<SlotSummary, String>("name"));
		delayCol.setCellValueFactory(new PropertyValueFactory<SlotSummary, Integer>("delay"));
		//
		slotNameCol.setPrefWidth(100);
		slotNameCol.setStyle("-fx-alignment: CENTER;");
		delayCol.setPrefWidth(100);
		delayCol.setStyle("-fx-alignment: CENTER;");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createDelayChart() {
		// delays chart
		barChart.setTitle("TSS Simulation Delays");
		delaysXAxis.setLabel("simultion time (s)");
		delaysYAxis.setLabel("delay (s)");
		delaysXAxis.setGapStartAndEnd(true);
		XYChart.Series series1 = new XYChart.Series<>();
		series1.setName("Delays");
		int[] delays = report.getDelays();
		for (int i = 0; i < delays.length; i++) {
			if (delays[i] != 0) {
				series1.getData().add(new XYChart.Data(Integer.toString(i), delays[i]));
			}
		}
		barChart.getData().add(series1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createCumulativeDelayGraph() {
		// cumulative delay chart
		lineChart.setTitle("TSS Simulation Cumulative Delays");
		cDelaysXAxis.setLabel("simulation time (s)");
		cDelaysYAxis.setLabel("delay (s)");
		XYChart.Series series2 = new XYChart.Series();
		series2.setName("Cumulative Delay");
		int[] cumulDelays = report.getCumulativeDelays();
		series2.getData().add(new XYChart.Data(0, cumulDelays[0]));
		for (int i = 1; i < cumulDelays.length - 1; i++) {
			if (cumulDelays[i] != cumulDelays[i - 1]) {
				series2.getData().add(new XYChart.Data(i - 1, cumulDelays[i - 1]));
				series2.getData().add(new XYChart.Data(i, cumulDelays[i]));
			}
		}
		LOG.log(Level.WARNING, "Cumulative delay = {0}", cumulDelays[cumulDelays.length - 1]);
		series2.getData().add(new XYChart.Data(cumulDelays.length - 1, cumulDelays[cumulDelays.length - 1]));
		lineChart.getData().add(series2);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createDelayDistributionGraph() {
		// delays distribution chart
		delaysDistribChart.setTitle("Delay distribution among aircrafts");
		delayDistXAxis.setLabel("delay (s)");
		delayDistYAxis.setLabel("number of slots");
		delayDistYAxis.setMinorTickVisible(false);
		XYChart.Series series3 = new XYChart.Series<>();
		series3.setName("Delay distribution");
		int maxDelay = 0;
		for (Integer delay : report.getDelayDistribution().keySet()) {
			maxDelay = Math.max(maxDelay, delay);
		}

		for (int i = 0; i < maxDelay; i++) {
			if (report.getDelayDistribution().containsKey(i)) {
				series3.getData().add(new XYChart.Data(Integer.toString(i), report.getDelayDistribution().get(i)));
			} else {
				series3.getData().add(new XYChart.Data(Integer.toString(i), 0));
			}
		}

		delaysDistribChart.getData().add(series3);
		delaysDistribChart.setVerticalGridLinesVisible(false);
		delayDistYAxis.setAutoRanging(false);
		delayDistYAxis.setLowerBound(0);
		delayDistYAxis.setUpperBound(2);
		delayDistYAxis.setTickUnit(1);
	}

	/*
	 * Private classes
	 */

	private class RootReportItem implements TSSReportItem {
		private final int t;

		public RootReportItem(int time) {
			t = time;
		}

		@Override
		public int getID() {
			return 0;
		}

		@Override
		public int getSimulationTime() {
			return t;
		};

		@Override
		public int getDelay() {
			return 0;
		}

		@Override
		public String getLead() {
			return "";
		}

		@Override
		public String getReason() {
			return "";
		}

		@Override
		public String getTrail() {
			return "";
		}

		@Override
		public String getNodeName() {
			return "";
		}

		@Override
		public String toString() {
			return "";
		}
	}

	private class CustomReadOnlyStringWrapper extends ReadOnlyStringWrapper {

		public CustomReadOnlyStringWrapper(String arg0) {
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
	 * TimeComparator
	 */

	private class TimeComparator implements Comparator<String> {

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
