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

package gov.nasa.arc.brahms.model;

public class Fact extends Knowledge implements Cloneable{

	
	public Fact(Expression exp) {
		this.exp = exp;
	}
	
	public void setFact(Expression exp) {
		this.exp = exp;
	}
	
	public Expression getFact(){ 
		return exp;
		
	}
	
	public String toString(){ 
		return exp.toString();
	}
	
	public Object clone() {
		try {
			Fact f = ((Fact) super.clone());
			f.exp = ((Expression) exp.clone());
			return f;
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}
	}
	
}
