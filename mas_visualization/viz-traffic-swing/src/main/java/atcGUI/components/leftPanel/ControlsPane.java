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

package atcGUI.components.leftPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import atcGUI.components.ATCVisFrame;
import atcGUI.components.GUIFunctions;

public class ControlsPane extends JPanel {

	private JButton execute;
	private JButton zoomIn;
	private JButton zoomOut;
	private JSlider speedSlider;
	
	private JTextArea simTime;
	
	public ControlsPane(String imageDirPath) {
		instantiateExecuteButton();
		instantiateZoomButtons(imageDirPath);
		instantiateSpeedSlider();
		instantiateStyle();
		buildControlPanel();
	}
	
	private void instantiateExecuteButton() {
		execute = new JButton("Play");
		execute.setMaximumSize(new Dimension(300, 100));
		execute.setAlignmentX(CENTER_ALIGNMENT);
		execute.setEnabled(false);
		execute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIFunctions.execute();
			}
		});
	}
	
	public void updateExecuteText(String text) {
		execute.setText(text);
	}
	
	private void instantiateZoomButtons(String imageDirPath) {
		String path = findImageDirectory(imageDirPath) + File.separator;
		zoomIn = new JButton();
		zoomIn.setIcon(new ImageIcon(path + "ZoomIn.png"));
		zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIFunctions.zoomIn();
			}
		});
		
		zoomOut = new JButton();
		zoomOut.setIcon(new ImageIcon(path + "ZoomOut.png"));
		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIFunctions.zoomOut();
			}
		});
	}
	
	private String findImageDirectory(String imageDirPath) {
		if (imageDirPath.contains("mas_visualization"))
			return imageDirPath;
		else {
			int pos = imageDirPath.indexOf("mas_");
			String file = imageDirPath.substring(0, pos);
			File tryFile = new File(file + "mas_visualization");
			if (tryFile.exists() && tryFile.isDirectory())
				return searchDirectory(tryFile);
			return searchDirectory(new File(file));
		}
	}
	
	private String searchDirectory(File file) {
		File[] buttons = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("ZoomOut.png") || name.contains("ZoomIn.png");
			}
		});
		
		if (buttons != null && buttons.length == 2)
			return file.getAbsolutePath();
		else if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				String path = searchDirectory(f);
				if (path != null)
					return path;
			}
		} 
		return null;
	}
	
	private void instantiateSpeedSlider() {
		int minSleep = 0;
		int maxSleep = 100;
		int initSleep = 50;
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		labelTable.put(new Integer(minSleep), new JLabel("Fast"));
		labelTable.put(new Integer(maxSleep), new JLabel("Slow"));
		
		speedSlider = new JSlider(JSlider.HORIZONTAL, minSleep, maxSleep, initSleep);
		speedSlider.setLabelTable(labelTable);
		speedSlider.setPaintLabels(true);
		speedSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				GUIFunctions.updateSleepTime(source.getModel().getValue());
			}
		});
	}
	
	private void instantiateStyle() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(Color.lightGray);
		this.setBorder(BorderFactory.createTitledBorder("Controls"));
	}
	
	private JPanel makeTimePanel() {
		JPanel timePanel = new JPanel();
		timePanel.setAlignmentX(CENTER_ALIGNMENT);
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
		
		JTextArea t = new JTextArea("Sim Time:");
		t.setBackground(new Color(241, 241, 241));
		timePanel.add(t);
		simTime = new JTextArea("0");
		simTime.setBackground(new Color(241, 241, 241));
		timePanel.add(simTime);
		return timePanel;
	}
	
	private JPanel makeZoomPanel() {
		JPanel zoomPanel = new JPanel();
		zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.X_AXIS));
		zoomPanel.setAlignmentX(CENTER_ALIGNMENT);
		zoomPanel.add(zoomIn);
		zoomPanel.add(zoomOut);
		return zoomPanel;
	}
	
	private JPanel makeExecutePanel() {
		JPanel timePanel = makeTimePanel();
		JPanel zoomPanel = makeZoomPanel();
		
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
		tempPanel.add(execute);
		tempPanel.add(timePanel);
		
		JPanel executePanel = new JPanel();
		executePanel.setLayout(new BoxLayout(executePanel, BoxLayout.X_AXIS));
		executePanel.add(tempPanel);
		executePanel.add(zoomPanel);
		executePanel.setMaximumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 46));
		
		return executePanel;
	}
	
	private void buildControlPanel() {
		JPanel executePanel = makeExecutePanel();
		
		JPanel runSimPanel = new JPanel(new GridLayout(3, 2));
		runSimPanel.setBorder(BorderFactory.createTitledBorder(""));
		runSimPanel.setLayout(new BoxLayout(runSimPanel, BoxLayout.Y_AXIS));
		runSimPanel.add(executePanel);
		runSimPanel.add(speedSlider);
		
		this.add(runSimPanel);
	}
	
	public void updateSimTimePanel(String time) {
		simTime.setText(time);
	}
	
	public void enableExecute(boolean enabled) {
		execute.setEnabled(enabled);
	}
}
