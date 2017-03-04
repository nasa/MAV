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
/*Code adapted from http://www.mkyong.com/java/search-directories-recursively-for-file-in-java/*/

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {

	private String fileNameToSearch;
	private List<String> result = new ArrayList<String>();
	private List<File> files = new ArrayList<File>();
	boolean DEBUG = false;

	public FileSearch(){
		
	}
	
	public String getFileNameToSearch() {
		return fileNameToSearch;
	}

	public void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	}

	public List<String> getResult() {
		return result;
	}

	public List<File> initSearch(String buildDir) {
		files.clear();
		//FileSearch fileSearch = new FileSearch();

		//try different directory and filename :)
		searchDirectory(new File(buildDir), ".bcc");

		int count = getResult().size();
		if(count ==0){
			System.out.println("\nNo result found!");
		}else{
			if(DEBUG)
				System.out.println("\nFound " + count + " files!\n");
			for (String matched : getResult()){
				if(DEBUG)
					System.out.println("Found : " + matched);
			}
		}
		return files;
	}

	public void searchDirectory(File directory, String fileNameToSearch) {

		setFileNameToSearch(fileNameToSearch);

		if (directory.isDirectory()) {
			search(directory);
		} else {
			//System.out.println(directory.getAbsoluteFile() + " is not a directory!");
		}

	}

	private void search(File file) {

		if (file.isDirectory()) {
			//System.out.println("Searching directory ... " + file.getAbsoluteFile());

			//do you have permission to read this directory?	
			if (file.canRead()) {
				for (File temp : file.listFiles()) {
					if (temp.isDirectory()) {
						search(temp);
					} else {
						
						if (temp.getName().contains(getFileNameToSearch())) {
							files.add(temp);
							result.add(temp.getAbsoluteFile().toString());
						}

					}
				}

			} else {
				System.out.println(file.getAbsoluteFile() + "Permission Denied");
			}
		}

	}
}
