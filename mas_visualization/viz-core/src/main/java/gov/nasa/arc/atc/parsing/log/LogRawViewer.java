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

import static javafx.application.Platform.runLater;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class LogRawViewer {

    private static LogRawViewer instance = null;

    private Stage stage;
//	private TextFlow flow;
    private WebView browser;
    private WebEngine webEngine;

    public static LogRawViewer getInstance() {
        if (instance == null) {
            instance = new LogRawViewer();
        }
        return instance;
    }

    public LogRawViewer() {
        runLater(LogRawViewer.this::start);
    }

    private void start() {
        stage = new Stage();
//		flow = new TextFlow();
//		flow.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
//		stage.setScene(new Scene(new ScrollPane(flow), 800, 600));
        browser = new WebView();
        webEngine = browser.getEngine();
        stage.setScene(new Scene(browser, 800, 600));
        stage.hide();
    }

    public void addIgnoredMessage(final String message) {
        LogHTMLExporter.addIgnoredMessage(message);
//		Platform.runLater(() -> {
//			Text t1 = new Text();
//			t1.setStyle("-fx-fill: LIGHTGRAY;-fx-font-weight:normal;");
//			t1.setText(message.concat( "\n"));
//			flow.getChildren().add(t1);
//		});
    }

    public void addOKMessage(final String message) {
        LogHTMLExporter.addOKMessage(message);
//		Platform.runLater(() -> {
//			Text t1 = new Text();
//			t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:normal;");
//			t1.setText(message.concat( "\n"));
//			flow.getChildren().add(t1);
//		});
    }

    public void addWarningMessage(final String message) {
        LogHTMLExporter.addWarningMessage(message);
//		Platform.runLater(() -> {
//			Text t1 = new Text();
//			t1.setStyle("-fx-fill: ORANGE;-fx-font-weight:normal;");
//			t1.setText(message.concat( "\n"));
//			flow.getChildren().add(t1);
//		});

    }

    public void addERRORMessage(final String message) {
        LogHTMLExporter.addErrorMessage(message);
//		Platform.runLater(() -> {
//			Text t1 = new Text();
//			t1.setStyle("-fx-fill: RED;-fx-font-weight:bold;");
//			t1.setText(message.concat( "\n"));
//			flow.getChildren().add(t1);
//		});
    }

    public static void loadFile(String path) {
        getInstance().webEngine.load(path);
    }

    public void hide() {
        runLater(stage::hide);
    }

    public void show() {
        runLater(stage::show);
    }

}
