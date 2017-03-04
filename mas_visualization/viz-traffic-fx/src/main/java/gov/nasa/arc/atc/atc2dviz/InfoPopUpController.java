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

package gov.nasa.arc.atc.atc2dviz;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.metrics.Metrics;
import gov.nasa.arc.atc.metrics.PlaneCalculation;
import org.controlsfx.control.PopOver;

import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.utils.ColorFXFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * 
 * @author ahamon
 *
 */
public class InfoPopUpController implements Initializable {

	private static final Logger LOG = Logger.getGlobal();

	private final CornerRadii cornerRadii = new CornerRadii(5.0);
	private final Insets insets = new Insets(5.0);

	@FXML
	private AnchorPane popUpAnchorPane;
	@FXML
	private Label idTagLabel;
	@FXML
	private Label altitudeTagLabel;
	@FXML
	private Label speedTaglabel;
	@FXML
	private Label headingTagLabel;
	@FXML
	private Label etaTagLabel;
	@FXML
	private Label iDValueLabel;
	@FXML
	private Label altitudeValueLabel;
	@FXML
	private Label speedValueLabel;
	@FXML
	private Label headingValueLabel;
	@FXML
	private Label etaValueLabel;
	@FXML
	private Label meteringTagLabel;
	@FXML
	private Label meteringValueLabel;

	private NewPlane afo;
	private PlaneCalculation calculation;

	private PopOver popOver;

	private BackgroundFill backgroundFill = new BackgroundFill(Color.BLUE, cornerRadii, insets);
	private Background background = new Background(backgroundFill);

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		popUpAnchorPane.setBackground(background);

	}

	/**
	 * 
	 * @param event {@link ActionEvent} triggering the onClose invocation
	 */
	@FXML
	public void onClose(ActionEvent event) {
		LOG.log(Level.FINE, "onClose on event {0}", event);
		if (popOver != null) {
			popOver.hide();
		}
	}

	public void setFillColor(Color color) {
		backgroundFill = new BackgroundFill(color, cornerRadii, insets);
		updateBackground();
		updateLabels(ColorFXFactory.getTextColor(color));
	}

	protected void setAFO(NewPlane theAFO) {
		afo = theAFO;
		calculation= Metrics.getPlaneCalculation(afo);
		updateInfos();
	}

	protected void setPopUpOver(PopOver pOver) {
		popOver = pOver;
	}

	protected void updateInfos() {
		if (afo != null) {
			iDValueLabel.setText(afo.getSimpleName());
			headingValueLabel.setText(Integer.toString((int) afo.getHeading()));
			altitudeValueLabel.setText(Integer.toString((int) afo.getAltitude()));
			speedValueLabel.setText(Double.toString(afo.getSpeed()));
			etaValueLabel.setText(Integer.toString((int) afo.getEta()));
			meteringValueLabel.setText(Integer.toString((int) calculation.getMeteredTimesAt(afo.getSimTime())));
		}
	}

	private void updateBackground() {
		background = new Background(backgroundFill);
		popUpAnchorPane.setBackground(background);
	}

	private void updateLabels(Color color) {
		idTagLabel.setTextFill(color);
		altitudeTagLabel.setTextFill(color);
		speedTaglabel.setTextFill(color);
		headingTagLabel.setTextFill(color);
		etaTagLabel.setTextFill(color);
		iDValueLabel.setTextFill(color);
		altitudeValueLabel.setTextFill(color);
		speedValueLabel.setTextFill(color);
		headingValueLabel.setTextFill(color);
		etaValueLabel.setTextFill(color);
	}

}
