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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedrawRoutine implements Runnable {
    //Things used to draw.
    private Canvas screenCanvas;
    private BufferStrategy buffer;
    private Graphics2D g2d;
    private Graphics graphics;
    private BufferedImage bufImage;
    private GraphicsConfiguration gc;
    
    ATCVisViewer viewRefresher;
    
    //If you ever need to make sure the view is not being redrawn while you do work under the hood, 
    //you can use this semaphore.
    private Semaphore isDrawing = new Semaphore(1);
    
    //Private constants.
    private static final double FRAME_TIME = 200;
    
    private static RedrawRoutine instance;
    
    private RedrawRoutine() {
        //Set up the drawing area.
        //screenCanvas.setIgnoreRepaint(true);
    }
    
    public static RedrawRoutine inst() {
        if (instance == null)
            instance = new RedrawRoutine();
        return instance;
    }
    
    public void initialize(Canvas s, ATCVisViewer v) {
        viewRefresher = v;
        screenCanvas = s;
        screenCanvas.createBufferStrategy(2);
        buffer = screenCanvas.getBufferStrategy();
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
    }

    @Override
    public void run() {
        double time = System.currentTimeMillis();
        try {
            isDrawing.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(RedrawRoutine.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        while (true) {
            try {
                //Make sure that if all else fails, you sleep for at least a millisecond
                //to let the other logic run.
                isDrawing.release();
                Thread.sleep((long)(1));
                if (time<FRAME_TIME)
                    Thread.sleep((long)(FRAME_TIME-time));
                
                //When game logic relenquishes control, move forward.
                isDrawing.acquire();

                refreshView();
                
                //Draw the image to the buffer.
                graphics = buffer.getDrawGraphics();
                graphics.drawImage(bufImage, 0, 0, null);

                if(!buffer.contentsLost())
                    buffer.show();
            }
            catch (InterruptedException ex) {
                Logger.getLogger(RedrawRoutine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Refreshes the view. It does this by calling the refresh method on the provided viewRefresher.
     */
    public void refreshView() {
        //Get an image, and the graphics of that image.
        bufImage = gc.createCompatibleImage(screenCanvas.getWidth(), screenCanvas.getHeight());
        g2d = bufImage.createGraphics();
        viewRefresher.refreshView(g2d);

        //Draw the image to the buffer.
        graphics = buffer.getDrawGraphics();
        graphics.drawImage(bufImage, 0, 0, null);

        if(!buffer.contentsLost())
            buffer.show();
    }
}
