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

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.glsl.ShaderUtil;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.Scanner;
import javax.swing.JComponent;
import org.jogamp.glg2d.impl.gl2.GL2ColorHelper;
import org.jogamp.glg2d.impl.gl2.GL2StringDrawer;
import org.jogamp.glg2d.impl.gl2.GL2Transformhelper;
import org.jogamp.glg2d.impl.shader.GL2ES2ColorHelper;
import org.jogamp.glg2d.impl.shader.GL2ES2ImageDrawer;
import org.jogamp.glg2d.impl.shader.GL2ES2ShapeDrawer;
import org.jogamp.glg2d.impl.shader.GL2ES2TransformHelper;
import org.jogamp.glg2d.impl.shader.GLShaderGraphics2D;
import org.jogamp.glg2d.impl.shader.text.GL2ES2TextDrawer;
import org.lwjglfx.Gears;

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

  @Override
  public void display(GLAutoDrawable drawable) {
      
        boolean useGears = false;
//        if(gears!=null){
//            useGears = true;
//            gears.getRenderStream().setGL(drawable.getGL());
//            gears.setupOpenGL();
//            gears.setupQuad();
//        }
//        if(useGears){
//            gears.getRenderStream().bind();
//        }
//        prePaint(drawable);
//        paintGL(g2d);
//        postPaint(drawable);
//        if(useGears){
//            gears.test();
//            gears.getRenderStream().swapBuffers();
//        }
        if(gears!=null){
            gears.getRenderStream().setGL(drawable.getGL());
            prePaint(drawable);
            gears.getRenderStream().bind();
            paintGL(g2d);
            gears.getRenderStream().swapBuffers();
            postPaint(drawable);
        }
  }
  
  /**
   * Called before any painting is done. This should setup the matrices and ask
   * the {@code GLGraphics2D} object to setup any client state.
   */
  protected void prePaint(GLAutoDrawable drawable) {
    setupViewport(drawable);
    g2d.prePaint(drawable.getContext());

    // clip to only the component we're painting
    g2d.translate(comp.getX(), comp.getY());
    g2d.clipRect(0, 0, comp.getWidth(), comp.getHeight());
  }

  /**
   * Defines the viewport to paint into.
   */
  protected void setupViewport(GLAutoDrawable drawable) {
    drawable.getGL().glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
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
    
    isInit=true;
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
  
}
