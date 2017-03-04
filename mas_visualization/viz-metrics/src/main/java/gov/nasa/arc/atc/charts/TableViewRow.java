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

package gov.nasa.arc.atc.charts;

import gov.nasa.arc.atc.airborne.TrafficType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ------Table View Row-------
 */
public class TableViewRow {
    public TableViewStructure tableStructure;

    public TableViewRow() {
    }

    public TableViewRow(Boolean defaultView, String plane, String del, String met, TrafficType type, String airlineName) {
        createRow(defaultView, plane, del, met, type, airlineName);
    }

    public void createRow(Boolean defaultView, String plane, String del, String met, TrafficType type, String airlineName) {
        TableViewStructure temp = new TableViewStructure(defaultView, plane, del, met, type.name(), airlineName);
        this.tableStructure = temp;
    }

    /**
     * ------Table Structure Class-------
     */

    public class TableViewStructure {
        private StringProperty columnID;
        private StringProperty sort1;
        private StringProperty sort2;
        private StringProperty sort3;
        private StringProperty sort4;
        public BooleanProperty view;

        public TableViewStructure(Boolean defaultView, String column1Name, String sort1, String sort2, String sort3, String sort4) {
            this.columnID = new SimpleStringProperty(column1Name);
            this.view = new SimpleBooleanProperty(defaultView);
            this.sort1 = new SimpleStringProperty(sort1);
            this.sort2 = new SimpleStringProperty(sort2);
            this.sort3 = new SimpleStringProperty(sort3);
            this.sort4 = new SimpleStringProperty(sort4);
        }

        public void setView(boolean value) {
            view.set(value);
        }

        public void setColumnID(String name) {
            this.columnID.set(name);
        }

        /*
         * ------Table Structure Properties-------
         */
        // property methods must match column names used in TableValueFactory

        /**
         *
         * @return the view property
         */
        public BooleanProperty viewProperty() {
            return this.view;
        }

        //Matching example MetricModel.COLUMN_ID = "columnID"
        public StringProperty columnIDProperty() {
            return this.columnID;
        }

        public StringProperty sort1Property() {
            return this.sort1;
        }

        public StringProperty sort2Property() {
            return this.sort2;
        }

        public StringProperty sort3Property() {
            return this.sort3;
        }

        public StringProperty sort4Property() {
            return sort4;
        }

    }

}
