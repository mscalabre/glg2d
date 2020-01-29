/*
 * Copyright 2015 Brandon Borkholder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jogamp.glg2d;

import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javax.swing.JComponent;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;



import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjglfx.util.stream.RenderStream;
import org.lwjglfx.util.stream.StreamHandler;
import org.lwjglfx.util.stream.StreamUtil;

public class GLG2DUtils {
  private static final Logger LOGGER = Logger.getLogger(GLG2DUtils.class.getName());

  public static void setColor(Color c, float preMultiplyAlpha) {
    int rgb = c.getRGB();
   glColor4ub((byte) (rgb >> 16 & 0xFF), (byte) (rgb >> 8 & 0xFF), (byte) (rgb & 0xFF), (byte) ((rgb >> 24 & 0xFF) * preMultiplyAlpha));
  }

  public static float[] getGLColor(Color c) {
    return c.getComponents(null);
  }

  public static int getViewportHeight() {
   
    int canvasHeight = glGetInteger(GL_VIEWPORT);
    return canvasHeight;
  }

  public static void logGLError() {
    int error =glGetError();
    if (error != GL_NO_ERROR) {
      LOGGER.log(Level.SEVERE, "GL Error: code " + error);
    }
  }

  public static int ensureIsGLBuffer(int bufferId) {
    if( glIsBuffer(bufferId)) {
      return bufferId;
    } else {
      return genBufferId();
    }
  }

  public static int genBufferId() {
    return glGenBuffers();
  }
  
  
  public static GLG2DPanel streamAWTfromGLtoFX(final JComponent jpanel, final Pane mainContainer, final ImageView imageView, final int fixFps){
      
        final GLG2DPanel panel;
        try{
            
            panel = new GLG2DPanel(jpanel, false);
            Dimension size = new Dimension(1000, 1000);
            panel.setSize(size);
            
            new Thread(new Runnable(){
                    @Override
                    public void run() {
                        Pbuffer pbuffer = null;
                        try {
                            pbuffer = new Pbuffer(1, 1, new PixelFormat(), null, null, new ContextAttribs().withDebug(true));
                            pbuffer.makeCurrent();
                            Display.setDisplayMode(new DisplayMode(1000,1000));
                            panel.setPbuffer(pbuffer);
                        } catch (LWJGLException e) {
                            e.printStackTrace();
                            System.exit(0);
                        }

                        StreamHandler readHandler = StreamUtil.getReadHandler(imageView);
                        StreamUtil.RenderStreamFactory renderStreamFactory = StreamUtil.getRenderStreamImplementation();
                        RenderStream renderStream = renderStreamFactory.create(readHandler, 16, 2);

                        panel.setRenderStream(renderStream);
                        while (panel.isAlive()) {
                            
                            if((mainContainer!=null && mainContainer.getBoundsInLocal()!=null && jpanel!=null) && 
                                    (mainContainer.getBoundsInLocal().getWidth()!=jpanel.getWidth()
                                    || mainContainer.getBoundsInLocal().getHeight()!=jpanel.getHeight())){
                                Dimension size2 = new Dimension((int)mainContainer.getBoundsInLocal().getWidth(), (int)mainContainer.getBoundsInLocal().getHeight());
                                jpanel.setSize(size2);
                                panel.setSize(size2);
                                ((Pane)mainContainer.getChildren().get(0)).setPrefSize(mainContainer.getBoundsInLocal().getWidth(), mainContainer.getBoundsInLocal().getHeight());
                                
                            }
                            
                            if(panel.needRepaint()){
                                
                                long time = System.currentTimeMillis();
                                
                                double acualRepaintNumber = panel.getRepaintRandomNumber();
                                try{
                                    panel.paint(panel.getGraphics());
                                }catch(Throwable th){
                                    th.printStackTrace();
                                }
                                if(fixFps>0){
                                    try {
                                        Thread.sleep((long)Math.max(0, (1000/fixFps - (System.currentTimeMillis()-time))));
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                        break;
                                    }
                                }
                                panel.setRepaintLastNumber(acualRepaintNumber);
    
                                if(panel.isShowFPS()){
                                    System.out.println("FPS : " + (1000 / (Math.max(1, (System.currentTimeMillis()-time)))));
                                }
                            }
                        }

                    }

            }).start();
            
        
            return panel;
            
        }catch(Throwable th){
            th.printStackTrace();
        }
        
        return null;
  }
}
