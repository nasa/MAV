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

package gov.nasa.arc.brahms.simulator;

import java.util.ArrayList;

public class DistanceMatrix{
	ArrayList<ArrayList<Integer>> distanceMatrix;
	int numOfElements;
	public DistanceMatrix(int numOfElements){
		
	   this.numOfElements = numOfElements;
	   distanceMatrix = new ArrayList<ArrayList<Integer>>();
		
		for(int contextIndex = 0 ; contextIndex < numOfElements; contextIndex++) {
			ArrayList<Integer> vals = new ArrayList<Integer>();	
			for(int valsIndex = 0; valsIndex < numOfElements; valsIndex++){
				if(contextIndex == valsIndex) {
					vals.add(0); // across the diagonal 
				}
				else {
					vals.add(Integer.MAX_VALUE); // this is supposed to infinity
				}
			}
			distanceMatrix.add(vals);
		}
	}
	public ArrayList<ArrayList<Integer>> getDistanceMatrix(){
		return distanceMatrix;
	}
	
	public void setValue(int i, int j, int value) {
		Integer val = new Integer(value);
		distanceMatrix.get(i).set(j, val);
	}
	
	public int getValue(int i, int j) {
		return distanceMatrix.get(i).get(j);
	}
	
	public void computeShortestPathEstimates(){
		// use floyds algorithm 
		for(int k = 0; k < numOfElements; k++) {
			for(int i = 0; i < numOfElements; i++) {
				for(int j = 0; j < numOfElements; j++){
					// D[i,j], D[i,k], D[k,j]
					ArrayList<Integer> rowI = new ArrayList<Integer>();
					rowI.addAll(distanceMatrix.get(i));
					
					Integer elementi_j = rowI.get(j);
					Integer elementi_k = rowI.get(k);
					
					ArrayList<Integer> rowK = new ArrayList<Integer>();
					rowK.addAll(distanceMatrix.get(k));
				
					Integer elementk_j = rowK.get(j);
					Integer secondElement = null;
					if(elementi_k == Integer.MAX_VALUE || elementk_j == Integer.MAX_VALUE) {
						secondElement = Integer.MAX_VALUE;
					}
					else {
					 secondElement = elementi_k + elementk_j;
					}
				
					if(elementi_j.intValue() < secondElement.intValue()) {
						rowI.set(j, elementi_j);
					}
					else{
						rowI.set(j, secondElement);
					}
					distanceMatrix.set(i, rowI);
				}
			}
		}
	}
	

	public void printMatrix(){
		System.out.println(" ");
		for(int i = 0; i < numOfElements; i++){
			System.out.print(" " + i + " ");
		}
		System.out.println();
		for(int i = 0; i < numOfElements; i++) {
			ArrayList<Integer> rowI = new ArrayList<Integer>();
			rowI.addAll(distanceMatrix.get(i));
			System.out.print(i + ":");
			for(int j = 0; j < numOfElements; j++) {
				if(rowI.get(j).intValue() < Integer.MAX_VALUE) {
					System.out.print(rowI.get(j).intValue() + " ");
				}
				else{
					System.out.print("-- ");
					}
			}
			System.out.print("\n");
		}
	}
}
