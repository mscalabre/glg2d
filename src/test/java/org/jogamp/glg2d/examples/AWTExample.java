package org.jogamp.glg2d.examples;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.jogamp.glg2d.GLG2DPanel;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjglfx.util.LWJGLUtils;

public class AWTExample {
  public static void main(String[] args) {
      
    LWJGLUtils.bindNativePath();
//      
//    JFrame frame = new JFrame("GLG2D AWT Example");
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    frame.setPreferredSize(new Dimension(300, 300));

    JComponent comp = Example.createComponent();
    comp.setSize(new Dimension(1920, 1080));
    final GLG2DPanel panel = new GLG2DPanel(comp);
    panel.setSize(new Dimension(1920, 1080));
      

//    frame.pack();
//    frame.setVisible(true);
            
          new Thread(new Runnable(){
              @Override
              public void run() {
//                  while(panel.getCanvas().getContext()!=null){
//                    if ( (Pbuffer.getCapabilities() & Pbuffer.PBUFFER_SUPPORTED) == 0 )
//                            throw new UnsupportedOperationException("Support for pbuffers is required.");
//
//                    try {
//                            Pbuffer pbuffer = new Pbuffer(1, 1, new PixelFormat(), null, null, new ContextAttribs().withDebug(true));
//                            pbuffer.makeCurrent();
//                    } catch (LWJGLException e) {
//                            throw new RuntimeException(e);
//
//                    }
//                  }
//                  try {
//                      Display.create();
//                  } catch (LWJGLException ex) {
//                      Logger.getLogger(AWTExample.class.getName()).log(Level.SEVERE, null, ex);
//                  }
                try {
                        Pbuffer pbuffer = new Pbuffer(1, 1, new PixelFormat(), null, null, new ContextAttribs().withDebug(true));
                        pbuffer.makeCurrent();
                      Display.setDisplayMode(new DisplayMode(800,600));
                      Display.create();
                }catch (LWJGLException ex){
                    ex.printStackTrace();
                }
//                            
                  while(true){
                      try {
                          panel.repaint();
                          Thread.sleep(10);
                      } catch (InterruptedException ex) {
                          Logger.getLogger(AWTExample.class.getName()).log(Level.SEVERE, null, ex);
                      }
//                      if(panel.getCanvas().getContext()!=null){
//                            if ( (Pbuffer.getCapabilities() & Pbuffer.PBUFFER_SUPPORTED) == 0 )
//                                    throw new UnsupportedOperationException("Support for pbuffers is required.");
//
//                            try {
//                                    Pbuffer pbuffer = new Pbuffer(1, 1, new PixelFormat(), null, null, new ContextAttribs().withDebug(true));
//                                    pbuffer.makeCurrent();
//                            } catch (LWJGLException e) {
//                                    throw new RuntimeException(e);
//                            }
//                          panel.repaint();
//                          Display.update();
//                      }
                  }
              }
              
          }).start();
    
    
  }
}
