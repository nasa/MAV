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

package gov.nasa.arc.atc.atc2dviz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import javafx.scene.paint.Color;

 
/**
 * 
 * @author Kelsey
 *
 */
public class BlinkingAnimation{
  private Color[] colors = {Color.WHITE, Color.RED, Color.BLUE};
  private static final int DELAY = 50;
  
  private Rectangle rect;
  private TimerListener timerListener;
  private Timer timer;
 // = new Timer(DELAY, timerListener);


  public Rectangle getMainComp(){
	  return rect;
  }
  
  
  public BlinkingAnimation(Rectangle oldRect){
	rect = oldRect;
    rect.setFill(Color.WHITE);
    
    timerListener = new TimerListener();
    timerListener.addColorChanger(new ColorChanger(){
      public void setColor(Color c){
        Platform.runLater(()->{
        	rect.setFill(c);
            //System.err.println(" color="+c);
        });
      }
    });
    timer = new Timer(DELAY, timerListener);
  }
  
  public void blinkOff(){
	  timer.stop();
  }
  public void blinkOn(){
	  timer.start();
  }
  

  private class TimerListener implements ActionListener{
    private int counter = 0;
    private List<ColorChanger> colorChangerList = new ArrayList<ColorChanger>();
     
    public void actionPerformed(ActionEvent e){
      for (ColorChanger cc : colorChangerList){
        cc.setColor(colors[counter]);
        System.out.println("index="+counter);
      }
      counter++;
      counter %= colors.length;
    }
    public void addColorChanger(ColorChanger cc){
      colorChangerList.add(cc);
    }
  }
   
  private interface ColorChanger{
    void setColor(Color c);
  }

}
