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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import atcGUI.ATCParser;
import atcGUI.WaypointFileParser;
import atcGUI.components.ATCVisFrame;
import atcGUI.components.GUIFunctions;
import atcGUI.model.SimulationDataModel;
import gov.nasa.arc.atc.MainResources;

public class ATCVisController implements MouseListener, MouseMotionListener {

	private int MAXSCROLLBARSIZE = 1024;
	private SimulationDataModel data;
	
	// variables dealing with scrolling and zoom
	private double scale;
	private int scrollbarSize;
	private Point2D.Double worldUpperLeft;
	
	private double wDragStartX;
	private double wDragStartY;
	private double wX;
	private double wY;
	
	private static Logger logger = Logger.getLogger("GUIManager");
	
	public ATCVisController(SimulationDataModel model) {
		data = model;
			
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        if(width <= MAXSCROLLBARSIZE || height <= MAXSCROLLBARSIZE)
				MAXSCROLLBARSIZE = height - 112;

        // zoom & scrolling 
     	scale = 1.0;
		scrollbarSize = MAXSCROLLBARSIZE;
		worldUpperLeft = new Point2D.Double(0, 0);
	}

	public void zoomInButtonHit() {
		double prevSize = scrollbarSize;
		scale = scale * 2.0;
		scrollbarSize *= 0.5;

		GUIFunctions.setHScrollBarKnob(scrollbarSize);	
		GUIFunctions.setVScrollBarKnob(scrollbarSize);

		double newX = worldUpperLeft.getX() + (prevSize * 0.25);
		double newY = worldUpperLeft.getY() + (prevSize * 0.25);
		if (newX < 0)
			newX = 0;
		if (newY < 0)
			newY = 0;
		worldUpperLeft = new Point2D.Double(newX, newY);

		if (worldUpperLeft.getX() + scrollbarSize > MAXSCROLLBARSIZE) 
			GUIFunctions.setHScrollBarPosit(MAXSCROLLBARSIZE - scrollbarSize);
		else 
			GUIFunctions.setHScrollBarPosit((int) worldUpperLeft.getX());

		if (worldUpperLeft.getY() + scrollbarSize > MAXSCROLLBARSIZE) 
			GUIFunctions.setVScrollBarPosit(MAXSCROLLBARSIZE - scrollbarSize);
		else 
			GUIFunctions.setVScrollBarPosit((int) worldUpperLeft.getY());
	}

	public void zoomOutButtonHit() {
		double prevSize = new Double(scrollbarSize);

		if (scale >= 2.0) {
			scale = scale / 2.0;
			scrollbarSize *= 2.0;

			double newX = worldUpperLeft.getX() - (prevSize * 0.5);
			double newY = worldUpperLeft.getY() - (prevSize * 0.5);
			if(newX < 0)
				newX = 0;
			if(newY < 0)
				newY = 0;

			worldUpperLeft = new Point2D.Double(newX, newY);

			if (worldUpperLeft.getX() + scrollbarSize > MAXSCROLLBARSIZE)
				GUIFunctions.setHScrollBarPosit(MAXSCROLLBARSIZE - scrollbarSize);
			else
				GUIFunctions.setHScrollBarPosit((int) worldUpperLeft.getX());

			if (worldUpperLeft.getY() + scrollbarSize > MAXSCROLLBARSIZE)
				GUIFunctions.setVScrollBarPosit(MAXSCROLLBARSIZE - scrollbarSize);
			else
				GUIFunctions.setVScrollBarPosit((int) worldUpperLeft.getY());

			GUIFunctions.setHScrollBarKnob(scrollbarSize);	
			GUIFunctions.setVScrollBarKnob(scrollbarSize);
		}
	}

	public void hScrollbarChanged(int value) {
		double space = (double) MAXSCROLLBARSIZE - scrollbarSize;
		
		if(value > space) 
			value = MAXSCROLLBARSIZE - scrollbarSize;
		if(value < 0)
			value = 0;
		
		double y = worldUpperLeft.getY();
		worldUpperLeft = new Point2D.Double(value, y);	
		GUIFunctions.refresh();
	}

	public void vScrollbarChanged(int value) {
		double space = (double) MAXSCROLLBARSIZE - scrollbarSize;
		
		if(value > space) 
			value = MAXSCROLLBARSIZE - scrollbarSize;
		if(value < 0)
			value = 0;

		double x = worldUpperLeft.getX();
		worldUpperLeft = new Point2D.Double(x, value);		
		GUIFunctions.refresh();		
	}

	/////////////////////////////////////////////////////////
	//***************** Mouse Listener ********************//
	/////////////////////////////////////////////////////////
	
	@Override
	public void mouseClicked(MouseEvent e) {
//		Point2D.Double view = new Point2D.Double(e.getX(), e.getY());
//		Point2D.Double worldPoint = new Point2D.Double();
//		AffineTransform viewToWorld = new AffineTransform(1.0 / scale, 0, 0, 1.0 / scale, worldUpperLeft.getX(), worldUpperLeft.getY());
//		viewToWorld.transform(view, worldPoint);
//	
//		// check here if clicking a waypoint
//		int index = data.hitWaypointTest(worldPoint.getX(), worldPoint.getY());
//		if(index != -1) {
////			Waypoint w = data.getWaypoint(index);
//			GUIFunctions.updateWaypointDataFields(w);
//		} else 
//			GUIFunctions.updateWaypointDataFields(null);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point2D.Double view = new Point2D.Double(e.getX(), e.getY());
		Point2D.Double worldPoint = new Point2D.Double();
		AffineTransform viewToWorld = new AffineTransform(1.0 / scale, 0, 0, 1.0 / scale, worldUpperLeft.getX(), worldUpperLeft.getY());
		viewToWorld.transform(view, worldPoint);
		
		int index = data.hitWaypointTest(worldPoint.getX(), worldPoint.getY());
		if(index == -1) {
			wDragStartX = worldPoint.getX();
			wDragStartY = worldPoint.getY();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	/////////////////////////////////////////////////////////
	//************** Mouse Motion Listener ****************//
	/////////////////////////////////////////////////////////
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(scale != 1.0) {
			Point2D.Double view = new Point2D.Double(e.getX(), e.getY());
			Point2D.Double worldPoint = new Point2D.Double();
			AffineTransform viewToWorld = new AffineTransform(1.0 / scale, 0, 0, 1.0 / scale, worldUpperLeft.getX(), worldUpperLeft.getY());
			viewToWorld.transform(view, worldPoint);
			
			wX = worldPoint.getX();
			wY = worldPoint.getY();
	
			double diffX = wDragStartX - wX;
			double diffY = wDragStartY - wY;
			double wulX = worldUpperLeft.getX();
			double wulY = worldUpperLeft.getY();
			double newUpX = diffX + wulX;
			double newUpY = diffY + wulY;
			
			if(newUpX < 0)
				newUpX = 0;
			if(newUpY < 0)
				newUpY = 0;
			if(scale == 2.0 && newUpX > 512)
				newUpX = 512;
			if(scale == 2.0 && newUpY > 512)
				newUpY = 512;
			if(scale == 4.0 && newUpX > 768)
				newUpX = 768;
			if(scale == 4.0 && newUpY > 768)
				newUpY = 768;
			
			worldUpperLeft = new Point2D.Double(newUpX, newUpY);
			
			if(worldUpperLeft.getX() + scrollbarSize > MAXSCROLLBARSIZE) 
				GUIFunctions.setHScrollBarPosit(MAXSCROLLBARSIZE - scrollbarSize);
			else 
				GUIFunctions.setHScrollBarPosit((int) worldUpperLeft.getX());
			
			if(worldUpperLeft.getY() + scrollbarSize > MAXSCROLLBARSIZE) 
				GUIFunctions.setVScrollBarPosit(MAXSCROLLBARSIZE - scrollbarSize);
			else 
				GUIFunctions.setVScrollBarPosit((int) worldUpperLeft.getY());
			GUIFunctions.refresh();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {}
			
	public double getScale() {
		return scale;
	}
	
	public Point2D.Double getUpperLeftW () {
		return worldUpperLeft;
	}
		
	public String parseFile(String simFile, String formatFlag) {
		return ATCParser.chooseFile(data, simFile, formatFlag);
	}

	public void parseWaypointFile(String wayFile, String formatFlag) {
		WaypointFileParser bwg = new WaypointFileParser();
		String fileName = wayFile;
		if (wayFile.equals("")) {
			fileName = data.getAtmDir();
			if (formatFlag.equals(ATCVisFrame.XML_OUTPUT_FLAG) || formatFlag.equals(ATCVisFrame.BRAHMS_OUTPUT_FLAG) || formatFlag.equals(ATCVisFrame.BRAHMS_TRANSLATE_OUTPUT_FLAG)) {
				fileName += File.separator + "WaypointFiles" + File.separator + "DSAS-Macs-waypoints";
				fileName = MainResources.class.getResource("DSAS-Macs-waypoints").getPath();
			} else {
				if (formatFlag.equals(ATCVisFrame.TRX_OUTPUT_FLAG)) // DSAS output file
					fileName += File.separator + "WaypointFiles" + File.separator + "afcsWaypoints";
				else {
					data.addError("Error in ATCVisController:parseWaypointFile method");
					GUIFunctions.updateErrorList(data.getErrorOutput());
					logger.log(Level.SEVERE, "Error in ATCVisController:parseWaypointFile method");
				}
			}
		} 
		bwg.parseTracWayFile(fileName, data);
	}
	
	public void createBackgroundTimelineImage(Canvas can) {
		int minutesShown = (int) ArrivalRedrawRoutine.TIMELINE_LENGTH;
		String filePath = data.getCurDir() + "backgroundTimelineImage.png";

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        
        BufferedImage backImage = gc.createCompatibleImage(can.getWidth(), can.getHeight());
        Graphics2D g2d = backImage.createGraphics();
        
        g2d.setPaint(Color.black);
        g2d.setColor(Color.white);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        int leftXHorizontalLine = can.getWidth() / 5;
        int rightXHorizontalLine = leftXHorizontalLine * 4;
        int topYHorizontalLine = can.getHeight() - 31;
        int centerX = can.getWidth() / 2;
        int height = can.getHeight();
        int textY = height - 5;
        
        int leftX = centerX - 9;
        int rightX = centerX + 9;
        g2d.drawLine(leftX, 10, leftX, topYHorizontalLine);
        g2d.drawLine(rightX, 10, rightX, topYHorizontalLine);
        
        int diffY = topYHorizontalLine - 10;
        int distBetween = diffY / minutesShown;
        int yLoc = topYHorizontalLine - distBetween;
        for(int i = 1; i < minutesShown; i++, yLoc -= distBetween) {
        	if(i == 5)
        		g2d.drawString(String.valueOf(i), centerX - 2, yLoc + 4);
        	else if(i % 5 == 0) 
        		g2d.drawString(String.valueOf(i), centerX - 5, yLoc + 4);
        	else
        		g2d.drawLine(centerX - 2, yLoc, centerX + 2, yLoc);
        }
                
        g2d.drawLine(leftXHorizontalLine, topYHorizontalLine, rightXHorizontalLine, topYHorizontalLine);
        g2d.drawString("0", centerX - 2, height - 19);
        g2d.drawLine(leftXHorizontalLine, height - 17, rightXHorizontalLine, height - 17);
        g2d.drawString("ETA", leftXHorizontalLine, textY);
        g2d.drawString("LGA", centerX - 7, textY);
        g2d.drawString("STA", rightXHorizontalLine - 17, textY);
		try {
			File file = new File(filePath);
			file.deleteOnExit();
			ImageIO.write(backImage, "png", file);
		} catch (IOException e) {
			data.addError("Error creating timeline background image");
			GUIFunctions.updateErrorList(data.getErrorOutput());
			logger.log(Level.SEVERE, "ATCVisController:createBackgroundTimelineImage - Error creating timeline background image" + "\n" + e);
		}
	}
}
