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

package blocks;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


@SuppressWarnings("serial")
public class Config extends Properties{
	
	Properties properties;
	
	
	public Config(String propertiesFile) {
		System.out.println("Setting up configuration...");
		properties = new Properties();
	
		try {
			properties.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			System.err.println("error when loading the properties file");
			e.printStackTrace();
		}		
	}
	
	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}
	
	public String getStringProperty(String key) {
		return properties.getProperty(key).toString();
	}
}
