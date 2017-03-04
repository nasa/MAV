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

package atcGUI.control_view;

import gov.nasa.arc.atc.utils.SimulationProperties;
import gov.nasa.arc.brahms.visualization.elements.Agent;
import gov.nasa.arc.brahms.visualization.elements.BeliefUpdate;
import gov.nasa.arc.brahms.visualization.elements.CommunicateActivity;
import gov.nasa.arc.brahms.visualization.elements.Event;
import gov.nasa.arc.brahms.visualization.elements.Plane;
import atcGUI.model.SimulationDataModel;
import gov.nasa.arc.brahms.visualization.elements.Slot;
import gov.nasa.arc.brahms.visualization.elements.Waypoint;
import javaSwingVizCompatibility.Methods;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import atcGUI.components.GUIFunctions;

public class ATCVisViewer implements Observer {

	private List<Waypoint> waypoints;
	List<Line2D> sectors;
	List<Line2D> segments;
	private SimulationDataModel data;
	private ATCVisController controller;
	private List<String> atControllers;
	private Map<String, String> controllerDist;

	private TreeSet<Plane> etaList;
	private Logger logger = Logger.getLogger("GUIManager");

	private Color[] agentColors = {new Color(255, 138, 128), new Color(68, 138, 255), new Color(224, 64, 251), new Color(0, 191, 165), new Color(255, 64, 129), new Color(255, 171, 64), new Color(24, 255, 255), new Color(215, 204, 200)};

	public ATCVisViewer() {
		waypoints = new ArrayList<>();
		sectors = new ArrayList<>();
		segments = new ArrayList<>();
		atControllers = new ArrayList<>();
		controllerDist = new HashMap<>();
		etaList = new TreeSet<>();
		data = null;
	}

	/**
	 * @param g2d
	 *            the canvas that the shapes will be drawn on
	 */
	public void refreshView(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double scale = controller.getScale();
		Point2D.Double upLeft = controller.getUpperLeftW();

		g2d.setPaint(Color.black);
		g2d.setColor(Color.white);

		if (scale == 1.0)
			g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
		else if (scale == 2.0)
			g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 4));
		else if (scale == 4.0)
			g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 3));
		else if (scale == 8.0)
        	g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 2));
		else
			g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 1));
		
		double width = 1.0 / scale;
		g2d.setStroke(new BasicStroke((float) width));

		boolean drawWayNames = GUIFunctions.waypointNamesVisible();
		for (int i = 0; i < waypoints.size(); i++) {
			Waypoint wp = waypoints.get(i);
			DrawableWaypoint dw = new DrawableWaypoint();
			dw.drawWaypoint(g2d, wp, scale, upLeft, drawWayNames);
		}
		
		// drawing lines between waypoints for arrival routes
		for (int i = 0; i < segments.size(); i++)
			g2d.draw(segments.get(i));
		
		// drawing sector boundaries
		if (GUIFunctions.isSectorBoundariesVisible()) {
			for (int i = 0; i < sectors.size(); i++)
				g2d.draw(sectors.get(i));
		}

		if (GUIFunctions.isSimRunning()) {
			String displayMode = GUIFunctions.getDisplayMode();
			for (Plane p : data.getAllPlanes()) {
				if (!p.isLanded() && p.isFlying())
					drawPlane(g2d, p, scale, upLeft, displayMode);
			}

			// drawEtaLine(g2d);

			if (GUIFunctions.isSlotsVisible() && !displayMode.equals("Supervisor")) {
				g2d.setColor(new Color(255, 255, 0));
				BasicStroke bs = new BasicStroke(0.25f);
				g2d.setStroke(bs);
				for (Slot s : data.getSlots()) {
					if (s.isFlying())
						drawSlot(g2d, s, scale, upLeft, displayMode);
				}
			}
		}
	}

	public void loadSimulationData() {
		List<Agent> agents = data.getAgents();
		for (Agent ag : agents) {
			if (ag.getName().contains("plane_")) {

				Plane p = Methods.createPlane(ag);
				data.addPlane(p);
				if (p.isDeparture()) {
					data.addDepartingPlane(p, p.getStartTime());
				} else {
					data.addArrivingPlane(p);
				}
			} else if (ag.getName().contains("slot_")) {
				Slot s = Methods.createSlot(ag);
				data.addSlot(s);
			} else if (ag.getName().contains("ZNY_")) {
				atControllers.add(ag.getName());
				Map<Integer, List<BeliefUpdate>> beUp = ag.getBeliefs();
				if (beUp != null) {
					for (BeliefUpdate bu : beUp.get(0)) {
						if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.SEP_DIST_PPTY)))
							controllerDist.put(ag.getName(), bu.getValue());
					}
				}
			}
		}
		data.setControllers(controllerDist);
		data.setControllerList(atControllers);
		GUIFunctions.updateDepartureQueue(data.getDepartureSequence());
		GUIFunctions.refresh();
		finishSimulation();
	}

	public void finishSimulation() {
		boolean cont = true;
		while (cont) {
			GUIFunctions.updateSimTime();

			// this is for if step through simulation is enabled
			boolean pause = GUIFunctions.isStepEnabled();
			GUIFunctions.runNextStep();
			while (pause) {
				if (GUIFunctions.runNextStep())
					break;
			}

			updateCommWindow(); // this is for communication window output

			boolean planeUpdate = updatePlanes(); // this is for plane
													// position/movement
			boolean slotUpdate = updateSlots(); // this is for slot
												// position/movement
			if (planeUpdate || slotUpdate)
				GUIFunctions.refresh();

			GUIFunctions.updateEtaArrivalWindowOutput();
			GUIFunctions.updateDepartureQueue(data.getDepartureSequence());
			GUIFunctions.updateSeparationViolation(data.getSeparationViolation());
			GUIFunctions.updateErrorList(data.getErrorOutput());

			if (data.endOfSim()) {
				cont = false;
				atControllers.clear();
				controllerDist.clear();
				etaList.clear();
				data.resetPlaneSequences();

				GUIFunctions.clearCommWindowOutput();
				GUIFunctions.refresh();
				GUIFunctions.clearDepartureQueue();
				GUIFunctions.clearSepViolatorList();
				GUIFunctions.updateEtaArrivalWindowOutput();
				GUIFunctions.resetSimTime();
			}

			try {
				int pauseTime = GUIFunctions.getExecutionSpeed();
				Thread.sleep(pauseTime);
			} catch (InterruptedException e) {

			}
		}
	}

	/**
	 * Updates the GUI's communication window
	 */
	public void updateCommWindow() {
		List<Agent> agents = data.getAgents();
		int ci = 0;
		for (Agent ag : agents) {
			if (ci >= agentColors.length)
				ci = 0;
			Color color = agentColors[ci];
			ci++;
			Map<Integer, List<Event>> events = ag.getActivities();
			int index = data.getSimTime();

			if (events != null && (events.containsKey(index))) {
				String text = "";
				List<Event> acts = events.get(index);
				if (acts == null)
					continue;
				String endAltitude = "";
				for (Event ev : acts) {
					int simTime = index;
					if (ev.getActivityName().equals("changeSpeed")) {
						List<CommunicateActivity> comms = ev.getCommsList();
						String desiredSpeed = "";
						boolean normalSpeed = false;
						String plane = "";
						for (CommunicateActivity ca : comms) {
							if (ca.getAttribute().equals("current.desired_speed")) {
								desiredSpeed = ca.getValue();
								plane = ca.getWhoWith().substring(6);
							} else if (ca.getAttribute().equals("current.normal_speeds")) {
								normalSpeed = new Boolean(ca.getValue());
							}
						}
						simTime = index;
						if (normalSpeed) {
							text = ag.getName() + " (" + simTime + "):\n" + plane + ", resume normal speed";
						} else {
							Double speed = Double.parseDouble(desiredSpeed);
							Plane p = data.getPlane(plane);
							if (p != null) {
								if (Double.parseDouble(p.getSpeed()) > speed) {
									text += ag.getName() + " (" + simTime + "):\n" + plane + " decrease speed to " + speed + "\n\n";
								} else {
									text += ag.getName() + " (" + simTime + "):\n" + plane + " increase speed to " + speed + "\n\n";
								}
							}
						}
					} else if (ev.getActivityName().equals("handOffViaFlightDataBlock")) {
						List<CommunicateActivity> comms = ev.getCommsList();
						for (CommunicateActivity ca : comms) {
							if (ca.getAttribute().equals(ag.getName() + ".handOffAccepted")) {
								text += ag.getName() + " (" + simTime + "h):\nInitiating hand off with " + ca.getWhoWith() + " via the Flight Data Block\n\n";
							}
						}
					} else if (ev.getActivityName().equals("acceptHandOff")) {
						text += ag.getName() + " (" + simTime + "h):\nAccepted the hand off via the Flight Data Block\n\n";
					} else if (ev.getActivityName().equals("changeFrequency")) {
						List<CommunicateActivity> comms = ev.getCommsList();
						for (CommunicateActivity ca : comms) {
							if (ca.getAttribute().contains(".currentController")) {
								text += ag.getName() + " (" + simTime + "h):\n" + ca.getWhoWith().substring(6) + ", please change your radio frequency to " + ca.getValue() + "'s frequency\n\n";
							}
						}
					} else if (ev.getActivityName().equals("comm_readFlightDetails")) {
						List<CommunicateActivity> comms = ev.getCommsList();
						for (CommunicateActivity ca : comms) {
							if (ca.getAttribute().contains("end_altitude"))
								endAltitude = ca.getValue().substring(1, ca.getValue().length() - 1);
						}
					} else if (ev.getWorkframe().equals("adjustAltitude")) {
						text += ag.getName() + " (" + simTime + "h):\nAdjusting altitude to " + endAltitude + " feet\n\n";
					} else if (ev.getWorkframe().equals("confirmControllerChange")) {
						List<CommunicateActivity> comms = ev.getCommsList();
						CommunicateActivity ca = comms.get(0);
						text += ag.getName() + " (" + simTime + "h):\n" + ca.getWhoWith().substring(6) + ", I am now with you\n\n";
					}
				}
				if (!text.equals(""))
					GUIFunctions.updateCommWindowOutput(text, color);
			}
		}
	}

	/**
	 * Updates the plane's position as appropriate during simulation
	 */
	public boolean updatePlanes() {
		Iterator<Plane> iter = data.getAllPlanes().iterator();
		// TODO: shouldn't this be in the simulationDataModel?
		boolean update = false;
		int time = data.getSimTime();
		while (iter.hasNext()) {
			Plane p = iter.next();
			if(p.updateBeliefs(time, etaList))
				update = true;
			data.updateArrivalSequence(p);
		}
		return update;
	}

	public void drawPlane(Graphics2D g2d, Plane p, double scale, Point2D.Double wUpLeft, String displayMode) {
		try {
			p = DrawUtils.convertLatLongToScreenPoints(p);
		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "plane_" + p.getName() + "'s lat or long is set as empty string at simulation time " + data.getSimTime());
			return;
		}

		g2d.setColor(Color.white); // default if plane is not set to a
									// controller and is not a departure
		if (p.isDeparture())
			g2d.setColor(new Color(118, 255, 3));
		else {
			for (int i = 0; i < atControllers.size(); i++) {
				if (p.getController().equals(atControllers.get(i))) {
					g2d.setColor(agentColors[i]);
					break;
				}
			}
		}

		Iterator<Entry<String, String>> it = controllerDist.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> pair = (Entry<String, String>) it.next();
			if (p.getController().equals(pair.getKey())) {
				double val = Double.parseDouble(pair.getValue());
				if (p.getSeparationDist() < val && p.getSeparationDist() > 0.0) {
					if (p.isFlashing())
						g2d.setColor(new Color(255, 200, 255));
				}
				break;
			}
		}
		DrawablePlane.drawPlane(g2d, p, scale, wUpLeft, displayMode);
	}

	public boolean updateSlots() {
		Iterator<Slot> iter = data.getSlots().iterator();
		boolean update = false;
		int time = data.getSimTime();
		while (iter.hasNext()) {
			Slot s = iter.next();
			if(s.updateBelief(time))
				update = true;
		}
		return update;
	}


	public void drawSlot(Graphics2D g2d, Slot s, double scale, Point2D.Double wUpLeft, String displayMode) {
		try {
			s = DrawUtils.convertLatLongToScreenPoints(s);
		} catch (NumberFormatException e) {
			String error = s.getName() + "'s lat or long is set as empty string at simulation time " + data.getSimTime() + "\nOR\n";
			error += "Slot " + s.getName() + " has no speed set at time " + data.getSimTime();
			logger.log(Level.WARNING, "ATCVisViewer: drawSlot - " + error);
			data.addError(error);
			GUIFunctions.updateErrorList(data.getErrorOutput());
			return;
		}
		DrawableSlot.drawSlot(g2d, s, scale, wUpLeft, displayMode);
	}

	/**
	 * draws a line between 2 planes if they will be within 60 seconds of each
	 * other at a merge point
	 */
	public void drawEtaLine(Graphics2D g2d) {
		Iterator<Plane> it = etaList.iterator();
		while (it.hasNext()) {
			Plane p1 = it.next();
			Plane p2 = etaList.higher(p1);
			if (p2 != null) {
				if (Math.abs(p1.getEta() - p2.getEta()) > 60.0)
					continue;
				else {
					if (p1.getController().equals(p2.getController())) {
						Point2D p1d = p1.getScreenCoord();
						Point2D p2d = p2.getScreenCoord();
						g2d.drawLine((int) p1d.getX(), (int) p1d.getY(), (int) p2d.getX(), (int) p2d.getY());
					}
				}
			}
		}
	}

	public void addController(ATCVisController sc) {
		controller = sc;
	}

	/**
	 * @param o	the WaypointDataModel object
	 * @param arg the list of shapes stored in the model
	 */
	@Override
	public void update(Observable o, Object arg) {
		data = (SimulationDataModel) o;
		waypoints = data.getVisibleWaypoints();
		//sectors = data.getSectorBoundaries();
		segments = data.getSegmentLines();
		segments = data.getSegmentLines();
		GUIFunctions.refresh();
	}

}
