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
package org.jogamp.glg2d.impl.gl2;

import com.jogamp.common.nio.Buffers;
import java.awt.geom.AffineTransform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jogamp.glg2d.GLGraphics2D;
import org.jogamp.glg2d.impl.AbstractMatrixHelper;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

public class GL2Transformhelper extends AbstractMatrixHelper {
  protected GL2 gl;

  private float[] matrixBuf = new float[16];

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);
    gl = g2d.getGLContext().getGL().getGL2();

    setupGLView();
    flushTransformToOpenGL();
  }

    @Override
    public void setG2D(GLGraphics2D g2d, GLContext context) {
        super.setG2D(g2d);
//        gl = context.getGL().getGL2();

        setupGLView();
        flushTransformToOpenGL();
    }
  

  protected void setupGLView() {
    IntBuffer viewportDimensions = Buffers.newDirectIntBuffer(16);
    glGetInteger(GL.GL_VIEWPORT, viewportDimensions);
    int width = viewportDimensions.get(2);
    int height = viewportDimensions.get(3);

    // setup projection
   glMatrixMode(GLMatrixFunc.GL_PROJECTION);
   glLoadIdentity();
   glOrtho(0, width, 0, height, -1, 1);

    // the MODELVIEW matrix will get adjusted later

   glMatrixMode(GL.GL_TEXTURE);
   glLoadIdentity();
  }

  /**
   * Sends the {@code AffineTransform} that's on top of the stack to the video
   * card.
   */
  protected void flushTransformToOpenGL() {
    FloatBuffer matrix = getGLMatrix(stack.peek());

   glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
   glLoadMatrix(matrix);
  }

  /**
   * Gets the GL matrix for the {@code AffineTransform} with the change of
   * coordinates inlined. Since Java2D uses the upper-left as 0,0 and OpenGL
   * uses the lower-left as 0,0, we have to pre-multiply the matrix before
   * loading it onto the video card.
   */
  protected FloatBuffer getGLMatrix(AffineTransform transform) {
//    matrixBuf[0] = (float) transform.getScaleX();
//    matrixBuf[1] = -(float) transform.getShearY();
//    matrixBuf[4] = (float) transform.getShearX();
//    matrixBuf[5] = -(float) transform.getScaleY();
//    matrixBuf[10] = 1;
//    matrixBuf[12] = (float) transform.getTranslateX();
//    matrixBuf[13] = g2d.getCanvasHeight() - (float) transform.getTranslateY();
//    matrixBuf[15] = 1;
//
//    return matrixBuf;
        FloatBuffer matrixBuf = Buffers.newDirectFloatBuffer(16);
        matrixBuf.put(0, (float) transform.getScaleX());
        matrixBuf.put(1,  -(float) transform.getShearY());
        matrixBuf.put(4,  (float) transform.getShearX());
        matrixBuf.put(5,  -(float) transform.getScaleY());
        matrixBuf.put(10,  1);
        matrixBuf.put(12,  (float) transform.getTranslateX());
        matrixBuf.put(13,  g2d.getCanvasHeight() - (float) transform.getTranslateY());
        matrixBuf.put(15,  1);
        return matrixBuf;
  }
}
