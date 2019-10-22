package org.jogamp.glg2d.examples;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.ImageView;

import javax.swing.JComponent;

import org.jogamp.glg2d.GLG2DPanel;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjglfx.Gears;
import org.lwjglfx.util.LWJGLUtils;
import org.lwjglfx.util.stream.RenderStream;
import org.lwjglfx.util.stream.StreamHandler;
import org.lwjglfx.util.stream.StreamUtil;

public class AWTExample {
  public static void main(String[] args) {
      
    LWJGLUtils.bindNativePath();
    
    if(true){
        
        JComponent comp = Example.createComponent();
        comp.setSize(new Dimension(1920, 1080));
        final GLG2DPanel panel = new GLG2DPanel(comp);
        panel.setSize(new Dimension(1920, 1080));
    
        try {
            Display.setDisplayMode(new DisplayMode(800,600));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
//        ImageView imageView = new ImageView();
//        imageView.setFitWidth(100);
//        imageView.setFitHeight(100);
//        StreamHandler readHandler = StreamUtil.getReadHandler(imageView);
//        StreamUtil.RenderStreamFactory renderStreamFactory = StreamUtil.getRenderStreamImplementation();
//        RenderStream renderStream = renderStreamFactory.create(readHandler, 1, 2);
        
        // init OpenGL
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 800, 0, 600, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        while (!Display.isCloseRequested()) {


            panel.repaint();
            
//            Display.update();
        }

        Display.destroy();
        return;
    }
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
