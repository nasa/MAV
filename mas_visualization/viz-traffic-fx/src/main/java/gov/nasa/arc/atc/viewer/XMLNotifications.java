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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.controlsfx.control.Notifications;

import javafx.util.Duration;

/**
 *
 * @author hamon
 */
public class XMLNotifications implements PropertyChangeListener {

	private final Notifications notifications;

	public XMLNotifications() {
		notifications = Notifications.create();
		notifications.darkStyle();
		notifications.hideAfter(Duration.INDEFINITE);
		notifications.text("The XML is being parsed");
		//
		notifications.showInformation();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

}
