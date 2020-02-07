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
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.Threading;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.Animator;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.util.logging.Logger;












import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.RepaintManager;


import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Pbuffer;
import org.lwjglfx.Gears;
import org.lwjglfx.util.stream.RenderStream;

/**
 * This canvas redirects all paints to an OpenGL canvas. The drawable component
 * can be any JComponent. This is a simple implementation to allow manual
 * painting of a JComponent scene to OpenGL. A {@code G2DGLCanvas} is more
 * appropriate when rendering a complex scene using
 * {@link JComponent#paintComponent(Graphics)} and the {@code Graphics2D}
 * object.
 *
 * <p>
 * GL drawing can be enabled or disabled using the {@code setGLDrawing(boolean)}
 * method. If GL drawing is enabled, all full paint requests are intercepted and
 * the drawable component is drawn to the OpenGL canvas.
 * </p>
 *
 * <p>
 * Override {@link #createGLComponent(GLCapabilitiesImmutable, GLContext)} to
 * create the OpenGL canvas. The returned canvas may be a {@code GLJPanel} or a
 * {@code GLCanvas}. {@link #createG2DListener(JComponent)} is used to create
 * the {@code GLEventListener} that will draw to the OpenGL canvas. Use
 * {@link #getGLDrawable()} if you want to attach an {@code Animator}.
 * Otherwise, paints will only happen when requested (either with
 * {@code repaint()} or from AWT).
 * </p>
 */
public class GLG2DCanvas extends JComponent {
  private static final long serialVersionUID = -471481443599019888L;

  protected GLAutoDrawable canvas;
  protected GLCapabilitiesImmutable chosenCapabilities;

  protected GLEventListener g2dglListener;

  /**
   * @see #removeNotify()
   */
  private GLAutoDrawable sideContext;

  private JComponent drawableComponent;

  private boolean drawGL;

  private BufferedImage imageRender = null;
  
  private boolean useGL2ES2; 
  
  private ExecutorService executor;
  
  private Predicate predicate;

  /**
   * Returns the default, desired OpenGL capabilities needed for this component.
   */
  public static GLCapabilities getDefaultCapabalities() {
    GLCapabilities caps = new GLCapabilities(GLProfile.getGL2ES1());
    caps.setRedBits(8);
    caps.setGreenBits(8);
    caps.setBlueBits(8);
    caps.setAlphaBits(8);
    caps.setDoubleBuffered(true);
    caps.setHardwareAccelerated(true);
    caps.setNumSamples(4);
    caps.setBackgroundOpaque(false);
    caps.setSampleBuffers(true);
    return caps;
  }
    private Gears gears;
    private RenderStream renderStream;
    private Pbuffer pbuffer;
    protected boolean alive = true;
    private double repaintRandomNumber;
    private double repaintLastNumber;
    private boolean destroyInContext;

  /**
   * Creates a new, blank {@code G2DGLCanvas} using the default capabilities
   * from {@link #getDefaultCapabalities()}.
   */
  public GLG2DCanvas() {
    this(getDefaultCapabalities());
  }

  /**
   * Creates a new, blank {@code G2DGLCanvas} using the given OpenGL
   * capabilities.
   */
  public GLG2DCanvas(GLCapabilities capabilities) {
    canvas = createGLComponent(capabilities, null);

    /*
     * Set both canvas and drawableComponent to be the same size, but we never
     * draw the drawableComponent except into the canvas.
     */
    setLayout(new GLOverlayLayout());
    add((Component) canvas);

    setGLDrawing(true);

    RepaintManager.setCurrentManager(GLAwareRepaintManager.INSTANCE);
  }
  public GLG2DCanvas(GLCapabilities capabilities, GLAutoDrawable canvas) {
    this.canvas = canvas;

    /*
     * Set both canvas and drawableComponent to be the same size, but we never
     * draw the drawableComponent except into the canvas.
     */
    setLayout(new GLOverlayLayout());
    add((Component) canvas);

    setGLDrawing(true);

    RepaintManager.setCurrentManager(GLAwareRepaintManager.INSTANCE);
  }

    public boolean isDestroyInContext() {
        return destroyInContext;
    }

  

    public void setPbuffer(Pbuffer pbuffer) {
        this.pbuffer = pbuffer;
    }

    public boolean isAlive() {
        return alive;
    }
    
    public boolean needRepaint(){
//        return true;
        return this.repaintLastNumber!=this.repaintRandomNumber;
    }

    public void destroy(){
        this.destroyInContext = true;
    }
    
    public void realDestroy(){
        this.alive = false;
        if(renderStream!=null){
            renderStream.destroy();
        }
        if(pbuffer!=null){
            pbuffer.destroy();
        }
    }
  /**
   * Creates a new {@code G2DGLCanvas} where {@code drawableComponent} fills the
   * canvas. This uses the default capabilities from
   * {@link #getDefaultCapabalities()}.
   */
  public GLG2DCanvas(JComponent drawableComponent) {
    this();
    setDrawableComponent(drawableComponent);
  }
  public GLG2DCanvas(JComponent drawableComponent, boolean useGL2ES2) {
    this();
    this.useGL2ES2 = useGL2ES2;
    setDrawableComponent(drawableComponent);
  }
  public GLG2DCanvas(JComponent drawableComponent, GLAutoDrawable canvas) {
    this(getDefaultCapabalities(), canvas);
    setDrawableComponent(drawableComponent);
  }
  public GLG2DCanvas(JComponent drawableComponent, GLAutoDrawable canvas, boolean useGL2ES2) {
    this(getDefaultCapabalities(), canvas);
    setDrawableComponent(drawableComponent);
    this.useGL2ES2 = useGL2ES2;
  }

  /**
   * Creates a new {@code G2DGLCanvas} where {@code drawableComponent} fills the
   * canvas.
   */
  public GLG2DCanvas(GLCapabilities capabilities, JComponent drawableComponent) {
    this(capabilities);
    setDrawableComponent(drawableComponent);
  }
  
  /**
   * Returns {@code true} if the {@code drawableComonent} is drawn using OpenGL
   * libraries. If {@code false}, it is using normal Java2D drawing routines.
   */
  public boolean isGLDrawing() {
    return drawGL;
  }
  
  public void setGears(Gears gears){
      if(canvas.getGL() != null){
        int error = canvas.getGL().glGetError();
        System.out.println("setGears error0 : " + error);
      }
      if(this.g2dglListener!=null && this.g2dglListener instanceof GLG2DSimpleEventListener){
          ((GLG2DSimpleEventListener)this.g2dglListener).setGears(gears);
      }else{
          System.out.println("No listener, can't set gear");
      }
      if(canvas.getGL() != null){
        int error = canvas.getGL().glGetError();
        System.out.println("setGears error1 : " + error);
      }
      gears.getRenderStream().setGL(canvas.getGL());
      this.gears = gears;
  }
  
  public void setRenderStream(RenderStream renderStream){
      this.renderStream = renderStream;
      if(getCanvas().getGL()!=null){
            int error = canvas.getGL().glGetError();
            System.out.println("setRenderStream error0 : " + error);
            renderStream.setGL(getCanvas().getGL());
            error = canvas.getGL().glGetError();
            System.out.println("setRenderStream error1 : " + error);
      }
  }

    public RenderStream getRenderStream() {
        return renderStream;
    }

  /**
   * Sets the drawing path, {@code true} for OpenGL, {@code false} for normal
   * Java2D.
   *
   * @see #isGLDrawing()
   */
  public void setGLDrawing(boolean drawGL) {
    if (this.drawGL != drawGL) {
      this.drawGL = drawGL;
      ((Component) canvas).setVisible(drawGL);
      setOpaque(drawGL);

      firePropertyChange("gldrawing", !drawGL, drawGL);

      repaint();
    }
  }

  /**
   * Gets the {@code JComponent} to be drawn to the OpenGL canvas.
   */
  public JComponent getDrawableComponent() {
    return drawableComponent;
  }

  /**
   * Sets the {@code JComponent} that will be drawn to the OpenGL canvas.
   */
  public void setDrawableComponent(JComponent component) {
      setDrawableComponent(component, false);
  }
  /**
   * Sets the {@code JComponent} that will be drawn to the OpenGL canvas.
   */
  public void setDrawableComponent(JComponent component, boolean force) {
    if (!force && component == drawableComponent) {
      return;
    }

    if (g2dglListener != null) {
      canvas.removeGLEventListener(g2dglListener);
      if (sideContext != null) {
        sideContext.removeGLEventListener(g2dglListener);
      }
    }

    if (drawableComponent != null) {
      remove(drawableComponent);
    }

    drawableComponent = component;
    if (drawableComponent != null) {
      verifyHierarchy(drawableComponent);

      g2dglListener = createG2DListener(drawableComponent);
      canvas.addGLEventListener(g2dglListener);
      if (sideContext != null) {
        sideContext.addGLEventListener(g2dglListener);
      }
      try{
          add(drawableComponent);
      }catch(Throwable th){
          th.printStackTrace();
      }
    }
  }
  public void setDrawableComponent(JComponent component, GLEventListener listener) {
    if (component == drawableComponent) {
      return;
    }

    if (g2dglListener != null) {
      canvas.removeGLEventListener(g2dglListener);
      if (sideContext != null) {
        sideContext.removeGLEventListener(g2dglListener);
      }
    }

    if (drawableComponent != null) {
      remove(drawableComponent);
    }

    drawableComponent = component;
    if (drawableComponent != null) {
      verifyHierarchy(drawableComponent);

      g2dglListener = listener;
      canvas.addGLEventListener(g2dglListener);
      if (sideContext != null) {
        sideContext.addGLEventListener(g2dglListener);
      }
      try{
          add(drawableComponent);
      }catch(Throwable th){
          th.printStackTrace();
      }
    }
  }

  /**
   * Checks the component and all children to ensure that everything is pure
   * Swing. We can only draw lightweights.
   *
   *
   * We'll also set PopupMenus to heavyweight and fix JViewport blitting.
   */
  protected void verifyHierarchy(Component comp) {
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    if (comp instanceof JComponent) {
      ((JComponent) comp).setDoubleBuffered(false);
    }

    if (!(comp instanceof JComponent)) {
      Logger.getLogger(GLG2DCanvas.class.getName()).warning("Drawable component and children should be pure Swing: " +
          comp + " does not inherit JComponent");
    }

    if (comp instanceof JViewport) {
      ((JViewport) comp).setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    }

    if (comp instanceof Container) {
      Container cont = (Container) comp;
      for (int i = 0; i < cont.getComponentCount(); i++) {
        verifyHierarchy(cont.getComponent(i));
      }
    }
  }

  /**
   * Gets the {@code GLAutoDrawable} used for drawing. By default this is a
   * {@link GLCanvas}, but can be changed by overriding
   * {@link #createGLComponent(GLCapabilitiesImmutable, GLContext)}.
   *
   * <p>
   * Use the returned {@code GLAutoDrawable} as input to an {@link Animator} to
   * automate painting.
   * </p>
   */
  public GLAutoDrawable getGLDrawable() {
    return canvas;
  }

  /**
   * Creates a {@code Component} that is also a {@code GLAutoDrawable}. This is
   * where all the drawing takes place. The advantage of a {@code GLCanvas} is
   * that it is faster, but a {@code GLJPanel} is more portable. The component
   * should also be disabled so that it does not receive events that should be
   * sent to the {@code drawableComponent}. A {@code GLCanvas} is a heavyweight
   * component and on some platforms will not pass through mouse events even
   * though it is disabled. A {@code GLJPanel} supports this better.
   */
  protected GLAutoDrawable createGLComponent(GLCapabilitiesImmutable capabilities, GLContext shareWith) {
    GLCanvas canvas = new GLCanvas(capabilities);
    if (shareWith != null) {
        canvas.setSharedContext(shareWith);
    }

    canvas.setEnabled(true);
    chosenCapabilities = (GLCapabilitiesImmutable) capabilities.cloneMutable();
    return canvas;
  }

  /**
   * Creates the GLEventListener that will draw the given component to the
   * canvas.
   */
  protected GLEventListener createG2DListener(JComponent drawingComponent) {
    return new GLG2DSimpleEventListener(drawingComponent, this.useGL2ES2);
  }

  /**
   * Calling {@link GLCanvas#removeNotify()} destroys the GLContext. We could
   * mess with that internally, but this is slightly easier.
   * <p>
   * This method is particularly important for docking frameworks and moving the
   * panel from one window to another. This is simple for normal Swing
   * components, but GL contexts are destroyed when {@code removeNotify()} is
   * called.
   * </p>
   * <p>
   * Our workaround is to use context sharing. The pbuffer is initialized and by
   * drawing into it at least once, we automatically share all textures, etc.
   * with the new pbuffer. This pbuffer holds the data until we can initialize
   * our new JOGL canvas. We share the pbuffer canvas with the new JOGL canvas
   * and everything works nicely from then on.
   * </p>
   * <p>
   * This has the unfortunate side-effect of leaking memory. I'm not sure how to
   * fix this yet.
   * </p>
   */
  @Override
  public void removeNotify() {
    prepareSideContext();

    remove((Component) canvas);
    super.removeNotify();

    canvas = createGLComponent(chosenCapabilities, sideContext.getContext());
    canvas.addGLEventListener(g2dglListener);
    add((Component) canvas, 0);
  }

  private void prepareSideContext() {
    if (sideContext == null) {
      GLDrawableFactory factory = canvas.getFactory();
      sideContext = factory.createOffscreenAutoDrawable(null, chosenCapabilities, null, 1, 1);
      ((GLOffscreenAutoDrawable) sideContext).setSharedContext(canvas.getContext());
      sideContext.addGLEventListener(g2dglListener);
    }

    Runnable work = new Runnable() {
      @Override
      public void run() {
        sideContext.display();
      }
    };

    if (Threading.isOpenGLThread()) {
      work.run();
    } else {
      Threading.invokeOnOpenGLThread(false, work);
    }
  }
  
  public boolean isUseUpscale(){
      return true;
  }

    private int nbPaintStack = 0;
    @Override
    public void repaint() {
//        try{
//            this.repaintRandomNumber = Math.random();
            if(executor!=null && predicate!=null && predicate.test(null) && nbPaintStack<10){ 
                nbPaintStack++;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        paint(getGraphics());
                        nbPaintStack--;
                    }
                };
                executor.execute(runnable);
            }
//        }catch(Throwable th){
//            th.printStackTrace();
//        }
    }

    public double getRepaintRandomNumber() {
        return repaintRandomNumber;
    }

    public double getRepaintLastNumber() {
        return repaintLastNumber;
    }

    public void setRepaintLastNumber(double repaintLastNumber) {
        this.repaintLastNumber = repaintLastNumber;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public Predicate getPredicate() {
        return predicate;
    }
    
  
  @Override
  public void paint(Graphics g) {
      
    if (isGLDrawing() && drawableComponent != null && canvas != null) {
        if(this.canvas instanceof GLJPanel){
            ((GLJPanel)canvas).paint(g);
        }else{
            if(useLWJGL() && executor!=null){
                if(g2dglListener instanceof GLG2DSimpleEventListener){
                    if(((GLG2DSimpleEventListener)g2dglListener).getG2D()==null){
                        g2dglListener.init(canvas);
                        System.out.println("init ok");
                    }
                    if(useStream()){
                        renderStream.bind();
                    }
                    g2dglListener.display(canvas);
                    if(useStream()){
                        renderStream.swapBuffers();
                    }
                }
//                Display.update();
            }else{
                canvas.display();
            }
        }
    } else {
        // Bug with context, I need to comment for the moment
//        super.paint(g);
    }
  }

  public boolean isShowFPS(){
    return false;
  }
  
    @Override
    public void setSize(Dimension d) {
        super.setSize(d); 
    }
  
  

  @Override
  protected void paintChildren(Graphics g) {
    /*
     * Don't paint the drawableComponent. If we'd use a GLJPanel instead of a
     * GLCanvas, we'd have to paint it here.
     */
    if (!isGLDrawing()) {
      super.paintChildren(g);
    }
  }

  private boolean useLWJGL(){
      return true;
  }
  
    public GLAutoDrawable getCanvas() {
        return canvas;
    }

    public void setCanvas(GLAutoDrawable canvas) {
        remove((Component)this.canvas);
        this.canvas.destroy();
        
        this.canvas = canvas;
        
        setLayout(new GLOverlayLayout());
        add((Component) canvas);

        setGLDrawing(true);

        RepaintManager.setCurrentManager(GLAwareRepaintManager.INSTANCE);
        
        setDrawableComponent(drawableComponent, true);
    }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    if (comp == canvas || comp == drawableComponent) {
      super.addImpl(comp, constraints, index);
    } else {
      throw new IllegalArgumentException("Do not add component to this. Add them to the object in getDrawableComponent()");
    }
  }

    public void setLWJGLContext(final Object context) {
      try {
          EventQueue.invokeAndWait(new Runnable(){
              @Override
              public void run() {
                  try {
                      org.lwjgl.opengl.GLContext.useContext(context);
                  } catch (LWJGLException ex) {
                      ex.printStackTrace();
                  }
              }
              
          });
      } catch (InterruptedException ex) {
          Logger.getLogger(GLG2DCanvas.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvocationTargetException ex) {
          Logger.getLogger(GLG2DCanvas.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    private boolean useStream() {
        return renderStream != null;
    }

  /**
   * Implements a simple layout where all the components are the same size as
   * the parent.
   */
  protected static class GLOverlayLayout implements LayoutManager2, Serializable {
    private static final long serialVersionUID = -8248213786715565045L;

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      if (parent.isPreferredSizeSet() || parent.getComponentCount() == 0) {
        return parent.getPreferredSize();
      } else {
        int x = -1, y = -1;
        for (Component child : parent.getComponents()) {
          Dimension dim = child.getPreferredSize();
          x = Math.max(dim.width, x);
          y = Math.max(dim.height, y);
        }

        return new Dimension(x, y);
      }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      if (parent.getComponentCount() == 0) {
        return new Dimension(0, 0);
      } else {
        int x = Integer.MAX_VALUE, y = Integer.MAX_VALUE;
        for (Component child : parent.getComponents()) {
          Dimension dim = child.getMinimumSize();
          x = Math.min(dim.width, x);
          y = Math.min(dim.height, y);
        }

        return new Dimension(x, y);
      }
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
      if (parent.getComponentCount() == 0) {
        return new Dimension(0, 0);
      } else {
        int x = -1, y = -1;
        for (Component child : parent.getComponents()) {
          Dimension dim = child.getMaximumSize();
          x = Math.max(dim.width, x);
          y = Math.max(dim.height, y);
        }

        return new Dimension(x, y);
      }
    }

    @Override
    public void layoutContainer(Container parent) {
      for (Component child : parent.getComponents()) {
        child.setSize(parent.getSize());
      }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
      // nop
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
      // nop
    }

    @Override
    public void removeLayoutComponent(Component comp) {
      // nop
    }

    @Override
    public void invalidateLayout(Container target) {
      // nop
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
      return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
      return 0.5f;
    }
  }
}
