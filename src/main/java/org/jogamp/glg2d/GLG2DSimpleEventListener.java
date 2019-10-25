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



import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Future;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.jogamp.glg2d.impl.shader.GL2ES2ColorHelper;
import org.jogamp.glg2d.impl.shader.GL2ES2ImageDrawer;
import org.jogamp.glg2d.impl.shader.GL2ES2ShapeDrawer;
import org.jogamp.glg2d.impl.shader.GL2ES2TransformHelper;
import org.jogamp.glg2d.impl.shader.GLShaderGraphics2D;
import org.jogamp.glg2d.impl.shader.text.GL2ES2TextDrawer;
import static org.lwjgl.opengl.GL11.glViewport;
import org.lwjglfx.Gears;
import org.jogamp.glg2d.util.GLG2DThreadable;

/**
 * Wraps a {@code JComponent} and paints it using a {@code GLGraphics2D}. This
 * object will paint the entire component fully for each frame.
 *
 * <p>
 * {@link GLG2DHeadlessListener} may also be used to listen for reshapes and
 * update the size and layout of the painted Swing component.
 * </p>
 */
public class GLG2DSimpleEventListener implements GLEventListener {
  /**
   * The cached object.
   */
  protected GLGraphics2D g2d;

  /**
   * The component to paint.
   */
  protected JComponent comp;
  private boolean useGL2ES2;
    private Gears gears;
    
    private BufferedImage unsupportedGLImage = null;
    private BufferedImage unsupportedGLImage2 = null;
    private BufferedImage unsupportedGLImageToReturn = null;
    
    private Graphics2D trueGraphics = null;
    
    private GLG2DThreadable threadable = null;
    public static final GLG2DThreadable threadableDEFAULT = new GLG2DThreadable() {
      @Override
      public Future<Object> run(Runnable runnable) {
          SwingUtilities.invokeLater(runnable);
          return null;
      }

      @Override
      public void runOrReplace(String key, Runnable runnable) {
          runnable.run();
      }

      @Override
      public void runAfterLock(String key, Runnable runnable) {
          runnable.run();
      }

      @Override
      public void setRender(boolean b) {
      }

      @Override
      public boolean isRender() {
          return false;
      }
      
  };

  public GLG2DSimpleEventListener(JComponent component) {
      this(component, false);
  }
  public GLG2DSimpleEventListener(JComponent component, boolean useGL2ES2) {
    if (component == null) {
      throw new NullPointerException("component is null");
    }
    this.useGL2ES2 = useGL2ES2;
    this.comp = component;
  }

    public void setThreadable(GLG2DThreadable threadable) {
//        this.threadable = threadable;
    }

    public GLG2DThreadable getThreadable() {
        if(threadable==null){
            return threadableDEFAULT;
        }
        return threadable;
    }
    
  @Override
  public void display(final GLAutoDrawable drawable) {
        prePaint(drawable);
//        getThreadable().setRender(true);
        paintGL(g2d);
//        getThreadable().setRender(false);
//        paintUnsupported();
        postPaint(drawable);
  }
  
  /**
   * Called before any painting is done. This should setup the matrices and ask
   * the {@code GLGraphics2D} object to setup any client state.
   */
  protected void prePaint(GLAutoDrawable drawable) {
    if(g2d.getThreadable()!=getThreadable()){
        g2d.setThreadable(threadable);
    }
    setupViewport(drawable);
    g2d.prePaint(comp.getHeight());

    // clip to only the component we're painting
    g2d.translate(comp.getX(), comp.getY());
    g2d.clipRect(0, 0, comp.getWidth(), comp.getHeight());
  }

  /**
   * Defines the viewport to paint into.
   */
  protected void setupViewport(GLAutoDrawable drawable) {
    glViewport(0, 0, comp.getWidth(), comp.getHeight());
//    if(this.comp.getWidth()>0 && this.comp.getHeight()>0){
//        if(this.unsupportedGLImage == null
//                || (this.unsupportedGLImage.getWidth()!=this.comp.getWidth())
//                || (this.unsupportedGLImage.getHeight()!=this.comp.getHeight())){
//            unsupportedGLImage = new BufferedImage((int)(this.comp.getWidth()), (int)(this.comp.getHeight()), BufferedImage.TYPE_INT_ARGB);
//            unsupportedGLImage.createGraphics();
//            
//            unsupportedGLImage2 = new BufferedImage((int)(this.comp.getWidth()), (int)(this.comp.getHeight()), BufferedImage.TYPE_INT_ARGB);
//            unsupportedGLImage2.createGraphics();
//            
//        }
//        if(g2d!=null){
//            getThreadable().runAfterLock("paint", new Runnable(){
//                @Override
//                public void run() {
//                    getThreadable().runOrReplace("switchRender", new Runnable() {
//                        @Override
//                        public void run() {
//                            System.out.println("switchImageToRender");
//                            if(unsupportedGLImageToReturn == unsupportedGLImage){
//                                g2d.setUnsupportedGLImage(unsupportedGLImage2);
//                            }else{
//                                g2d.setUnsupportedGLImage(unsupportedGLImage);
//                            }
//                        }
//                    });
//                }
//
//            });
//        }
//    }
  }

  /**
   * Called after all Java2D painting is complete.
   */
  protected void postPaint(GLAutoDrawable drawable) {
    g2d.postPaint();
  }

  /**
   * Paints using the {@code GLGraphics2D} object. This could be forwarded to
   * any code that expects to draw using the Java2D framework.
   * <p>
   * Currently is paints the component provided, turning off double-buffering in
   * the {@code RepaintManager} to force drawing directly to the
   * {@code Graphics2D} object.
   * </p>
   */
  protected void paintGL(GLGraphics2D g2d) {
    boolean wasDoubleBuffered = comp.isDoubleBuffered();
    comp.setDoubleBuffered(false);

    comp.paint(g2d);

    comp.setDoubleBuffered(wasDoubleBuffered);
  }

  private boolean isInit = false;
  @Override
  public void init(GLAutoDrawable drawable) {
    g2d = createGraphics2D(drawable);
    
    g2d.setCanvas(drawable, null);
    
    isInit=true;
  }

    public GLGraphics2D getG2D() {
        return g2d;
    }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
  }

  /**
   * Creates the {@code Graphics2D} object that forwards Java2D calls to OpenGL
   * calls.
   */
  protected GLGraphics2D createGraphics2D(GLAutoDrawable drawable) {
      if(useGL2ES2){
        return new GLShaderGraphics2D() {
            @Override
            protected void createDrawingHelpers() {

              shapeHelper = new GL2ES2ShapeDrawer();

              imageHelper = new GL2ES2ImageDrawer();
              stringHelper = new GL2ES2TextDrawer();

              colorHelper = new GL2ES2ColorHelper();
              matrixHelper = new GL2ES2TransformHelper();

              addG2DDrawingHelper(shapeHelper);
              addG2DDrawingHelper(imageHelper);
              addG2DDrawingHelper(stringHelper);
              addG2DDrawingHelper(colorHelper);
              addG2DDrawingHelper(matrixHelper);
            }
          };
      }else{
          return new GLGraphics2D();
      }
  }

  @Override
  public void dispose(GLAutoDrawable arg0) {
    if (g2d != null) {
      g2d.glDispose();
      g2d = null;
    } 
  }

    public void setGears(Gears gears) {
        this.gears = gears;
    }

    public BufferedImage getUnsupportedGLImage() {
        return unsupportedGLImageToReturn;
    }

    private void paintUnsupported() {
        getThreadable().runOrReplace("switchDisplay", new Runnable() {
            @Override
            public void run() {
                getThreadable().runAfterLock("render", new Runnable(){
                    @Override
                    public void run() {
                        System.out.println("switchImageToDisplay");
                        unsupportedGLImageToReturn = unsupportedGLImageToReturn == unsupportedGLImage ? unsupportedGLImage2 : unsupportedGLImage;
                    }
                });
            }
        });
    }
  
}
