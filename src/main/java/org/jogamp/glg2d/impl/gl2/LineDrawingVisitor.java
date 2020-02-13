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



import org.jogamp.glg2d.impl.BasicStrokeLineVisitor;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * Draws a line, as outlined by a {@link BasicStroke}. The current
 * implementation supports everything except dashes. This class draws a series
 * of quads for each line segment, joins corners and endpoints as appropriate.
 */
public class LineDrawingVisitor extends BasicStrokeLineVisitor {


  @Override
  public void beginPoly(int windingRule) {
    /*
     * pen hangs down and to the right. See java.awt.Graphics
     */
   glMatrixMode(GL_MODELVIEW);
   glPushMatrix();
   glTranslatef(0.5f, 0.5f, 0);

    super.beginPoly(windingRule);
  }

  @Override
  public void endPoly() {
    super.endPoly();

   glMatrixMode(GL_MODELVIEW);
   glPopMatrix();
  }

  @Override
  protected void drawBuffer() {
    vBuffer.drawBuffer(GL_TRIANGLE_STRIP);
  }
}
