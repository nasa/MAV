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

package atcGUI.components;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;

import atcGUI.ATCParser;
import atcGUI.components.leftPanel.LeftPanel;
import atcGUI.components.rightPanel.RightPanel;
import atcGUI.control_view.ATCVisController;
import atcGUI.control_view.ATCVisViewer;
import atcGUI.control_view.ArrivalRedrawRoutine;
import atcGUI.control_view.ArrivalVisViewer;
import atcGUI.control_view.DrawUtils;
import atcGUI.control_view.RedrawRoutine;
import atcGUI.model.SimulationDataModel;

@SuppressWarnings("serial")
public class ATCVisFrame extends JFrame implements Runnable, WindowListener {
	public static final String SUPERVISOR = "Supervisor";
	public static final String CONTROLLER = "Controller";
	public static final String NAMEONLY = "Name Only";
	public static final String ALL_SECTORS = "All Sectors";
	public static final String FINAL_SECTOR = "Final Sector";
	public static final String BRAHMS_OUTPUT_FLAG = "brahms";
	public static final String BRAHMS_TRANSLATE_OUTPUT_FLAG = "brahms-translate";
	public static final String XML_OUTPUT_FLAG = "xml";
	public static final String TRX_OUTPUT_FLAG = "trx";
	
	private final String PLAY = "Play";
	private final String PAUSE = "Pause";
	private static ATCVisFrame instance;
	private ATCVisController controller;
	private final ATCVisViewer viewRefresher;
	private final ArrivalVisViewer arriveRefresher;
	private SimulationDataModel data;

	private static Logger logger;

	public static int CANVASSIZE = 1024;
	public static int SCROLLBARSIZE = 1023;

	public static final int SIDEGUIWIDTH = 256;

	// Default animation speed
	private int sleepTime = 50;

	private JMenuBar jMenuBar1;
	// file menu options
	private JMenu jMenuFile;
	private JMenuItem exitAction;
	private JMenuItem helpAction;
	private JMenuItem aboutAction;
	// view menu options
	private JMenu jMenuView;
	private JMenuItem slotMarkAction;
	private JMenuItem waypointNameAction;
	private JMenuItem sectorBoundariesAction;
	private JMenu jMenudisplayMode;
	private JCheckBoxMenuItem nameOnlyViewAction;
	private JCheckBoxMenuItem controllerViewAction;
	private JCheckBoxMenuItem supervisorViewAction;

	JCheckBoxMenuItem allSectorsAction;
	JCheckBoxMenuItem finalSectorAction;

	private String selectedDisplayMode;
	private String selectedSectorView;

	// Radio button used as aid in pausing simulation
	private JRadioButton step = new JRadioButton();

	// GUI Panels
	private LeftPanel leftPanel;
	private CenterPanel centerPanel;
	private RightPanel rightPanel;

	private boolean parsedSimulation;
	private boolean loadedWaypoints;
	private boolean simRunning;
	private AtomicBoolean nextStep;
	private boolean slotsVisible;
	private boolean wayNamesVisible;
	private boolean boundariesVisible;

	private String simFile;
	private String wayFile;
	private String formatFlag; // flag for kind of output file being parsed

	public static ATCVisFrame inst() {
		if (instance == null)
			throw new IllegalStateException("Tried to get instance of ATCVisFrame without initializing it first!");
		return instance;
	}

	public static void createATCVisFrame(ATCVisController c, ATCVisViewer viewRefresher, ArrivalVisViewer arriveRefresher, MouseListener mouseListener, MouseMotionListener mouseMotionListener, SimulationDataModel data, String args[]) {
		instance = new ATCVisFrame(c, viewRefresher, arriveRefresher, mouseListener, mouseMotionListener, data, args);
		instance.setTitle("Air Traffic Control Visualization");
		instance.setMinimumSize(new Dimension(800, 600));
		instance.setResizable(false);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		instance.setMaximumSize(screenSize);
		int x = (screenSize.width - instance.getWidth()) / 2;
		int y = (screenSize.height - instance.getHeight()) / 2;
		instance.setLocation(x, y);

		logger = Logger.getLogger("GUIManager");
	}

	public static boolean isInitialized() {
		return (instance != null);
	}

	private ATCVisFrame(ATCVisController c, ATCVisViewer viewRefresher, ArrivalVisViewer arriveRefresher, MouseListener mouseListener, MouseMotionListener mouseMotionListener, SimulationDataModel data, String args[]) {
		this.viewRefresher = viewRefresher;
		this.arriveRefresher = arriveRefresher;
		this.data = data;
		this.controller = c;
		addWindowListener(this);
		data.createLogger();
		parsedSimulation = false;
		loadedWaypoints = false;
		simRunning = false;
		nextStep = new AtomicBoolean(false);
		slotsVisible = true;
		wayNamesVisible = false;
		boundariesVisible = true;
		selectedDisplayMode = NAMEONLY;
		selectedSectorView = ALL_SECTORS;

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();

		if (width <= CANVASSIZE || height <= CANVASSIZE) {
			CANVASSIZE = height - 112;	// if changing here, change ATCVisController()
			SCROLLBARSIZE = CANVASSIZE - 1;
		}

		if (args.length == 0) {
			wayFile = "";
			simFile = "SELECT";
			setOutputFileFormatFlag();
		} else {
			wayFile = args[0];
			simFile = args[1];
			formatFlag = args[2];
		}
		
		parseFiles();
        loadWaypointsActionPerformed();

		initComponents();

		RedrawRoutine r = RedrawRoutine.inst();
		Canvas mainCanvas = centerPanel.getSimulationCanvas();
		r.initialize(mainCanvas, viewRefresher);
		mainCanvas.addMouseListener(mouseListener);
		mainCanvas.addMouseMotionListener(mouseMotionListener);

		ArrivalRedrawRoutine ar = ArrivalRedrawRoutine.inst();
		Canvas arrivalCanvas = leftPanel.getArrivalCanvas();
		ar.initialize(arrivalCanvas, arriveRefresher, data.getCurDir());

		controller.createBackgroundTimelineImage(arrivalCanvas);
	}

	@Override
	public void run() {
		simRunning = true;
    	viewRefresher.loadSimulationData();   	
    	simRunning = false;
    	
    	leftPanel.updateExecuteText(PLAY);
    	step.setSelected(true);
    	executeActionPerformed();
	}

	private void initComponents() {
		////////////////////////////////
		//////// MENU BAR ITEMS ////////
		////////////////////////////////
		jMenuBar1 = new JMenuBar();
		jMenuFile = new JMenu("File");
		exitAction = new JMenuItem("Exit");
		helpAction = new JMenuItem("Help");
		aboutAction = new JMenuItem("About");
		jMenuView = new JMenu("View");
		slotMarkAction = new JMenuItem("Hide Slot Markers");
		waypointNameAction = new JMenuItem("Show Waypoint Names");
		sectorBoundariesAction = new JMenuItem("Hide Sector Boundaries");
		jMenudisplayMode = new JMenu("Display Mode");
		nameOnlyViewAction = new JCheckBoxMenuItem(NAMEONLY);
		controllerViewAction = new JCheckBoxMenuItem(CONTROLLER);
		supervisorViewAction = new JCheckBoxMenuItem(SUPERVISOR);
		allSectorsAction = new JCheckBoxMenuItem(ALL_SECTORS);
		finalSectorAction = new JCheckBoxMenuItem(FINAL_SECTOR);

		helpAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				helpActionPerformed();
			}
		});

		aboutAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				aboutActionPerformed();
			}
		});

		exitAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				exitActionPerformed();
			}
		});

		jMenuFile.add(helpAction);
		jMenuFile.add(aboutAction);
		jMenuFile.add(exitAction);
		jMenuBar1.add(jMenuFile);

		nameOnlyViewAction.setSelected(true);
		nameOnlyViewAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				displayModeActionPerformed(NAMEONLY);
			}
		});

		controllerViewAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				displayModeActionPerformed(CONTROLLER);
			}
		});

		supervisorViewAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				displayModeActionPerformed(SUPERVISOR);
			}
		});

		jMenudisplayMode.add(supervisorViewAction);
		jMenudisplayMode.add(controllerViewAction);
		jMenudisplayMode.add(nameOnlyViewAction);
		jMenuView.add(jMenudisplayMode);

		slotMarkAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				slotMenuActionPerformed();
			}
		});

		waypointNameAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				waypointMenuActionPerformed();
			}
		});
		
		sectorBoundariesAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sectorBoundariesActionPerformed();
			}
		});

		jMenuView.add(slotMarkAction);
		jMenuView.add(waypointNameAction);
		jMenuView.add(sectorBoundariesAction);

		JMenu sectorView = new JMenu("Sector View");
		allSectorsAction.setSelected(true);
		allSectorsAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sectorViewActionPerformed(ALL_SECTORS);
			}
		});

		finalSectorAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sectorViewActionPerformed(FINAL_SECTOR);
			}	
		});

		sectorView.add(allSectorsAction);
		sectorView.add(finalSectorAction);
		
		jMenuView.add(sectorView);
		jMenuBar1.add(jMenuView);
		setJMenuBar(jMenuBar1);

		////////////////////////////////
		////// END MENU BAR ITEMS //////
		////////////////////////////////

		leftPanel = new LeftPanel(data.getGuiImageDirectoryPath());
        centerPanel = new CenterPanel(data.getMaxSimTime());
        centerPanel.setCanvasSize(CANVASSIZE); // Main Canvas
        rightPanel = new RightPanel();

		step.setSelected(true);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		// Adding components to the GUI		
		JPanel gap1 = new JPanel();
		gap1.setSize(new Dimension(5, this.getHeight()));
		JPanel gap2 = new JPanel();
		gap2.setSize(new Dimension(5, this.getHeight()));
		
		this.getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		this.add(leftPanel, this);
		this.add(gap1, this);
		this.add(centerPanel, this);
		this.add(gap2, this);
		this.add(rightPanel, this);
		this.pack();
	}

	public void zoomOutActionPerformed() {
		controller.zoomOutButtonHit();
	}

	public void zoomInActionPerformed() {
		controller.zoomInButtonHit();
	}

	public void jScrollBarHorizontalAdjustmentValueChanged(AdjustmentEvent evt) {
		controller.hScrollbarChanged(evt.getValue());
	}

	public void jScrollBarVerticalAdjustmentValueChanged(AdjustmentEvent evt) {
		controller.vScrollbarChanged(evt.getValue());
	}

	private void loadWaypointsActionPerformed() {
		controller.parseWaypointFile(wayFile, formatFlag);
		loadedWaypoints = true;
	}

	public void executeActionPerformed() {
		if (!simRunning) {
			if (loadedWaypoints) {
				if (parsedSimulation) {
					clearErrorList();
					Thread executeSim = new Thread(ATCVisFrame.instance);
					executeSim.start();
				} else
					JOptionPane.showMessageDialog(ATCVisFrame.instance, "You must load the simulation file first");
			} else
				JOptionPane.showMessageDialog(ATCVisFrame.instance, "You must load the waypoint file first");
		} else {// if simulation is running
			step.setSelected(!step.isSelected());
			if (step.isSelected())
				leftPanel.updateExecuteText(PLAY);
			else
				leftPanel.updateExecuteText(PAUSE);
			stepActionPerformed();
		}
	}

	private void parseFiles() {
    	String file = controller.parseFile(simFile, formatFlag);
    	switch(file) {
    		case(ATCParser.CANCELED):
    			System.exit(0);
    			break;
    		case(ATCParser.FILEFAILED):
    			JOptionPane.showMessageDialog(ATCVisFrame.instance, "Error loading the Output file, inconsistencies found in XML file\nYou must restart GUI");
    			System.exit(0);
    			break;
    		case(ATCParser.GUIFAILED):
    			JOptionPane.showMessageDialog(ATCVisFrame.instance, "Error when trying to open file chooser GUI\nYou must restart GUI");
				System.exit(0);
				break;
    		case(ATCParser.FAILED):
    			JOptionPane.showMessageDialog(ATCVisFrame.instance, "Error caused due to invalid file format, only .xml or .log files\nPlease try restarting GUI and select correct file format");
				System.exit(0);
				break;
    		case(ATCParser.UNKNOWNFAIL):
    			JOptionPane.showMessageDialog(ATCVisFrame.instance, "Unknown error caused failure\nPlease try restarting GUI");
				System.exit(0);
				break;
    		default:
    			parsedSimulation = true;
        		simFile = file;
        		break;
    	}    	
    }

	private void stepActionPerformed() {
		if (step.isSelected()) {
			if (!simRunning)
				JOptionPane.showMessageDialog(ATCVisFrame.instance, "Simulation must be running to use this function");
			else
				nextStep.set(true);
		} else
			nextStep.set(true);
	}

	private void helpActionPerformed() {
		JFrame helpFrame = new JFrame("Help");
		JTextArea helpText = new JTextArea();
		helpText.setLineWrap(true);
		helpText.setWrapStyleWord(true);
		String filePath = data.getAtmDir() + File.separator + "src" + File.separator + "Help.txt";

		InputStreamReader in;
		try {
			in = new InputStreamReader(new FileInputStream(new File(filePath)));
			helpText.read(in, "File");
		} catch (IOException e1) {
			data.addError("Can't find Help.txt file");
			GUIFunctions.updateErrorList(data.getErrorOutput());
			logger.log(Level.INFO, "ATCVisFrame:helpActionPerformed - Can't find Help.txt file");
		}
		helpFrame.setMinimumSize(new Dimension(510, 300));
		helpFrame.setMaximumSize(new Dimension(510, 300));
		helpText.setEditable(false);
		helpFrame.add(new JScrollPane(helpText));
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - helpFrame.getWidth()) / 2;
		int y = (screenSize.height - helpFrame.getHeight()) / 2;
		helpFrame.setLocation(x, y);
		helpFrame.setVisible(true);
	}

	private void aboutActionPerformed() {
		JFrame aboutFrame = new JFrame("About");
		JTextArea aboutText = new JTextArea();
		aboutText.setLineWrap(true);
		aboutText.setWrapStyleWord(true);
		String filePath = data.getAtmDir() + File.separator + "src" + File.separator + "About.txt";

		InputStreamReader in;
		try {
			in = new InputStreamReader(new FileInputStream(new File(filePath)));
			aboutText.read(in, "File");
		} catch (IOException e1) {
			data.addError("Can't find About.txt file");
			GUIFunctions.updateErrorList(data.getErrorOutput());
			logger.log(Level.INFO, "ATCVisFrame:aboutActionPerformed - Can't find About.txt file");
		}
		aboutFrame.setMinimumSize(new Dimension(510, 300));
		aboutFrame.setMaximumSize(new Dimension(510, 300));
		aboutText.setEditable(false);
		aboutFrame.add(new JScrollPane(aboutText));
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - aboutFrame.getWidth()) / 2;
		int y = (screenSize.height - aboutFrame.getHeight()) / 2;
		aboutFrame.setLocation(x, y);
		aboutFrame.setVisible(true);
	}

	private void displayModeActionPerformed(String mode) {
		selectedDisplayMode = mode;
		switch (mode) {
			case NAMEONLY:
				controllerViewAction.setSelected(false);
				supervisorViewAction.setSelected(false);
				break;
			case CONTROLLER:
				nameOnlyViewAction.setSelected(false);
				supervisorViewAction.setSelected(false);
				break;
			case SUPERVISOR:
				nameOnlyViewAction.setSelected(false);
				controllerViewAction.setSelected(false);
				break;
			default:
				throw new IllegalArgumentException("Bad display mode name!");
		}
	}
	
	private void sectorViewActionPerformed(String view) {
		selectedSectorView = view;
		switch (view) {
			case ALL_SECTORS:
				allSectorsAction.setSelected(true);
				finalSectorAction.setSelected(false);
				break;
			case FINAL_SECTOR:
				allSectorsAction.setSelected(false);
				finalSectorAction.setSelected(true);
				break;
			default:
				throw new IllegalArgumentException("Bad sector view name!");
		}
	}

	private void slotMenuActionPerformed() {
		slotsVisible = !slotsVisible;
		if (slotsVisible)
			slotMarkAction.setText("Hide Slot Markers");
		else
			slotMarkAction.setText("Show Slot Markers");
		GUIFunctions.refresh();
	}

	private void waypointMenuActionPerformed() {
		wayNamesVisible = !wayNamesVisible;
		if (wayNamesVisible)
			waypointNameAction.setText("Hide Waypoint Names");
		else
			waypointNameAction.setText("Show Waypoint Names");
		GUIFunctions.refresh();
	}
	
	private void sectorBoundariesActionPerformed() {
		boundariesVisible = !boundariesVisible;
		if (boundariesVisible)
			sectorBoundariesAction.setText("Hide Sector Boundaries");
		else
			sectorBoundariesAction.setText("Show Sector Boundaries");
		GUIFunctions.refresh();
	}

	public String getDisplayMode() {
		return selectedDisplayMode;
	}

	public boolean waypointNamesVisible() {
		return wayNamesVisible;
	}
	
	public boolean sectorBoundariesVisible() {
		return boundariesVisible;
	}

	private void exitActionPerformed() {
		Path path = FileSystems.getDefault().getPath("", data.getLogFileName());
		// this deletes the Log file iff there was nothing written to it when
		// closing the program from menu file/exit option
		try {
			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			if (reader.readLine() == null)
				Files.delete(path);
		} catch (IOException e) {
			logger.log(Level.WARNING, "ATCVisFrame:exitActionPerformed - Error reading log file when shutting down" + "\n" + e);
		}
		System.exit(0);
	}

	void setScrollAttribute(ATCVisScrollbarAttrConsts scrollBar, ATCVisScrollbarAttrConsts attribute, int number) {
		JScrollBar scrollbarToChange;
		switch (scrollBar) {
			case H_SCROLL_BAR:
				scrollbarToChange = centerPanel.getHorizontalScrollbar();
				break;
			case V_SCROLL_BAR:
				scrollbarToChange = centerPanel.getVerticalScrollbar();
				break;
			default:
				throw new IllegalArgumentException("Bad scrollbar type!");
		}
		switch (attribute) {
			case MAX:
				scrollbarToChange.setMaximum(number);
				break;
			case MIN:
				scrollbarToChange.setMinimum(number);
				break;
			case KNOB:
				scrollbarToChange.setVisibleAmount(number);
				break;
			case POSIT:
				scrollbarToChange.setValue(number);
				break;
			default:
				throw new IllegalArgumentException("Bad Attribute type!");
		}
	}

	public boolean isStepEnabled() {
		return step.isSelected();
	}

	public boolean runNextStep() {
		return nextStep.compareAndSet(true, false);
	}

	public boolean isSlotsVisible() {
		return slotsVisible;
	}
	
	public boolean isSimRunning() {
		return simRunning;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public int getCANVASSIZE() {
		return CANVASSIZE;
	}
	
	public int getMaxSimTime() {
		return data.getMaxSimTime();
	}
	
	public void updateSleepTime(int time) {
		sleepTime = time;
	}

	public void showGUI() {
		data.update();
		executeActionPerformed();
		leftPanel.enableExecuteButton(true);
		instance.setVisible(true);
	}

	/////////////////////////////////////
	//////// Left Panel Updates /////////
	/////////////////////////////////////

	public void addCommOutput(String text, Color color) {
		leftPanel.updateCommOutput(text, color);
	}

	public void clearCommOutput() {
		leftPanel.clearCommOutput();
	}

	public void updateSimTimeSlider(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		int time = source.getModel().getValue();
		data.setSimTime(time);
		leftPanel.updateSimTimeView(String.valueOf(data.getSimTime()));
		if (step.isSelected())
			nextStep.set(true);
	}

	public void updateEtaArrivalOutput() {
		arriveRefresher.updateTimeline();
	}

	/////////////////////////////////////
	//////// Center Panel Updates ///////
	/////////////////////////////////////

	public void resetSimTime() {
		centerPanel.setSimTimeSliderValue(0);
	}

	public int updateSimTime() {
		data.updateSimTime();
		int time = data.getSimTimeIndex();
		centerPanel.setSimTimeSliderValue(time);
		return time;
	}

	/////////////////////////////////////
	//////// Right Panel Updates ////////
	/////////////////////////////////////

	public void clearDepartureQueue() {
		rightPanel.clearDepartureQueue();
	}

	public void clearErrorList() {
		rightPanel.clearErrorOutput();
	}

	public void clearViolatorList() {
		rightPanel.clearViolatorPane();
	}

	public void updateDepartureQueue(String depart) {
		rightPanel.updateDepartureQueue(depart);
	}

	public void updateErrorList(String errors) {
		rightPanel.updateErrorOutput(errors);
	}

	public void updateSeparationViolation(String violators) {
		rightPanel.updateViolatorPane(violators);
	}

	// WINDOW LISTENER METHODS
	@Override
	public void windowClosing(WindowEvent e) {
		Path path = FileSystems.getDefault().getPath("", data.getLogFileName());
		// this deletes the Log file if there was nothing written to it when
		// closing program from menu file/exit option
		try {
			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			if (reader.readLine() == null)
				Files.delete(path);
			reader.close();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "ATCVisFrame:windowClosing - Error reading log file when shutting down" + "\n" + ex);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
	
	public void setOutputFileFormatFlag() {
		Object[] choices = {XML_OUTPUT_FLAG, BRAHMS_OUTPUT_FLAG, BRAHMS_TRANSLATE_OUTPUT_FLAG, TRX_OUTPUT_FLAG};
		String message = "What output file format are you going to run?";
		String title = "Simulation Output File Format Flag Selector";
		formatFlag = (String)JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(formatFlag == null) 
			System.exit(0);
	}

	public static void main(String args[]) {
		ATCVisViewer refresher = new ATCVisViewer();
		ArrivalVisViewer arriveRefresh = new ArrivalVisViewer();
		String filePath = System.getProperty("user.dir");
		SimulationDataModel dataModel = new SimulationDataModel(filePath);
		dataModel.addObserver(refresher);
		dataModel.addObserver(arriveRefresh);
		ATCVisController controller = new ATCVisController(dataModel);
		refresher.addController(controller);

		GUIFunctions.createATCVisFrame(controller, refresher, arriveRefresh, controller, controller, dataModel, args);
		DrawUtils.initialize(dataModel);
		GUIFunctions.setHScrollBarKnob(SCROLLBARSIZE);
		GUIFunctions.setVScrollBarKnob(SCROLLBARSIZE);
		GUIFunctions.refresh();
		GUIFunctions.showGUI();
	}
}
