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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author ahamon
 * @author Kelsey
 */
public class FXLoggerController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextFlow textFlow;

    private final Map<Level, List<Text>> lines = new HashMap<>();
    // temp not supported yet
    private final Map<Level, Boolean> visibilities = new HashMap<>();

    private final StringBuilder fullLogBuilder = new StringBuilder();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lines.put(Level.ALL, new LinkedList<>());
        lines.put(Level.CONFIG, new LinkedList<>());
        lines.put(Level.FINE, new LinkedList<>());
        lines.put(Level.FINER, new LinkedList<>());
        lines.put(Level.FINEST, new LinkedList<>());
        lines.put(Level.INFO, new LinkedList<>());
        lines.put(Level.OFF, new LinkedList<>());
        lines.put(Level.SEVERE, new LinkedList<>());
        lines.put(Level.WARNING, new LinkedList<>());
        //
        visibilities.put(Level.ALL, true);
        visibilities.put(Level.CONFIG, true);
        visibilities.put(Level.FINE, true);
        visibilities.put(Level.FINER, true);
        visibilities.put(Level.FINEST, true);
        visibilities.put(Level.INFO, true);
        visibilities.put(Level.OFF, true);
        visibilities.put(Level.SEVERE, true);
        visibilities.put(Level.WARNING, true);
    }

    @FXML
    protected void onCopyClipBoardAction(ActionEvent event) {
        LOG.log(Level.FINE, "onCopyClipBoardAction on event {0}", event);
        StringSelection stringSelection = new StringSelection(fullLogBuilder.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        log(Level.FINE, "Copy to clipBoard done");
    }

    void log(Level level, String msg, Object... params) {
        String outputMessage = msg + paramToString(params) + "\n";
        fullLogBuilder.append(outputMessage);
        Text newText = new Text(outputMessage);
        setStyle(newText, level);
        textFlow.getChildren().add(newText);
        scrollPane.setVvalue(scrollPane.getVmax());
        //
        lines.get(level).add(newText);
        newText.setVisible(visibilities.get(level));
    }

    private String paramToString(Object[] params) {
        if (params == null || params.length == 0) {
            return "";
        }
        return " [" + Arrays.stream(params).map(o -> {
            if (o != null) {
                return o.toString();
            } else {
                return "";
            }
        }).collect(Collectors.joining(" - ")) + "]";
    }

    private void setStyle(Text text, Level level) {
        if (level.equals(Level.INFO)) {
            text.setStyle("-fx-font-size: 11; -fx-fill: chartreuse;");
        } else if (level.equals(Level.WARNING)) {
            text.setStyle("-fx-font-size: 11; -fx-fill: orange;");
        } else if (level.equals(Level.SEVERE)) {
            text.setStyle("-fx-font-size: 11; -fx-fill: red;");
        } else {
            text.setStyle("-fx-font-size: 11; -fx-fill: grey;");
        }
    }
}
