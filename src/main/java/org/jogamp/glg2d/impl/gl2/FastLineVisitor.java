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



import java.awt.BasicStroke;
import java.nio.FloatBuffer;




import org.jogamp.glg2d.VertexBuffer;
import org.jogamp.glg2d.impl.SimplePathVisitor;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_LINE_BIT;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STIPPLE;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_POINT_BIT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLineStipple;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * Draws a line using the native GL implementation of a line. This is only
 * appropriate if the width of the line is less than a certain number of pixels
 * (not coordinate units) so that the user cannot see that the join and
 * endpoints are different. See {@link #isValid(BasicStroke)} for a set of
 * useful criteria.
 */
public class FastLineVisitor extends SimplePathVisitor {
  protected FloatBuffer testMatrix = BufferUtils.createFloatBuffer(16);

  protected VertexBuffer buffer = VertexBuffer.getSharedBuffer();

  protected BasicStroke stroke;

  protected float glLineWidth;

  @Override
  public void setStroke(BasicStroke stroke) {
   glLineWidth(glLineWidth);
   glPointSize(glLineWidth);
    
    /*
     * Not perfect copy of the BasicStroke implementation, but it does get
     * decently close. The pattern is pretty much the same. I think it's pretty
     * much impossible to do with out a fragment shader and only the fixed
     * function pipeline.
     */
    float[] dash = stroke.getDashArray();
    if (dash != null) {
      float totalLength = 0;
      for (float f : dash) {
        totalLength += f;
      }

      float lengthSoFar = 0;
      int prevIndex = 0;
      int mask = 0;
      for (int i = 0; i < dash.length; i++) {
        lengthSoFar += dash[i];

        int nextIndex = (int) (lengthSoFar / totalLength * 16);
        for (int j = prevIndex; j < nextIndex; j++) {
          mask |= (~i & 1) << j;
        }

        prevIndex = nextIndex;
      }

      /*
       * XXX Should actually use the stroke phase, but not sure how yet.
       */

     glEnable(GL_LINE_STIPPLE);
      int factor = (int) totalLength;
     glLineStipple(factor >> 4, (short) mask);
    } else {
     glDisable(GL_LINE_STIPPLE);
    }

    this.stroke = stroke;
  }

  /**
   * Returns {@code true} if this class can reasonably render the line. This
   * takes into account whether or not the transform will blow the line width
   * out of scale and it obvious that we aren't drawing correct corners and line
   * endings.
   * 
   * <p>
   * Note: This must be called before {@link #setStroke(BasicStroke)}. If this
   * returns {@code false} then this renderer should not be used.
   * </p>
   */
  public boolean isValid(BasicStroke stroke) {
    // if the dash length is odd, I don't know how to handle that yet
    float[] dash = stroke.getDashArray();
    if (dash != null && (dash.length & 1) == 1) {
      return false;
    }

//    glGetFloat(GL_MODELVIEW_MATRIX, testMatrix);
//
//    float scaleX = Math.abs(testMatrix.get(0));
//    float scaleY = Math.abs(testMatrix.get(5));
//
//    // scales are different, we can't get a good line width
//    if (Math.abs(scaleX - scaleY) > 1e-6) {
//      return false;
//    }

    float strokeWidth = stroke.getLineWidth();

    // gl line width is in pixels, convert to pixel width
    glLineWidth = strokeWidth;

    if(glLineWidth<0.1f){
        System.out.println("stop");
    }
    
    // we'll only try if it's a thin line
    //EDIT : I don't understand why :s. When false there is artefact of stroke. I always return true then -- MS
//    return glLineWidth <= 2;
        return true;
  }

  @Override
  public void moveTo(float[] vertex) {
    drawLine(false);
    buffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void lineTo(float[] vertex) {
    buffer.addVertex(vertex, 0, 1);
  }

  @Override
  public void closeLine() {
    drawLine(true);
  }

  protected void drawLine(boolean close) {
    FloatBuffer buf = buffer.getBuffer();
    int p = buf.position();
    buffer.drawBuffer(close ? GL_LINE_LOOP : GL_LINE_STRIP);

    /*
     * We'll ignore butt endcaps, but we'll pretend like we're drawing round,
     * bevel or miter corners as well as round or square corners by just putting
     * a point there. Since our line should be very thin, pixel-wise, it
     * shouldn't be noticeable.
     */
    
    //EDIT : Create a too strong stroke, i comment -- MS
    
//    if (stroke.getDashArray() == null) {
//      buf.position(p);
//      buffer.drawBuffer(GL_POINTS);
//    }else{
//        System.out.println("stop");
//    }
    
    buffer.clear();
  }

  @Override
  public void beginPoly(int windingRule) {
    buffer.clear();

    /*
     * pen hangs down and to the right. See java.awt.Graphics
     */
   glMatrixMode(GL_MODELVIEW);
   glPushMatrix();
   glTranslatef(0.5f, 0.5f, 0);

   glPushAttrib(GL_LINE_BIT | GL_POINT_BIT);
  }

  @Override
  public void endPoly() {
    drawLine(false);
   glDisable(GL_LINE_STIPPLE);
   glPopMatrix();

   glPopAttrib();
  }
}
