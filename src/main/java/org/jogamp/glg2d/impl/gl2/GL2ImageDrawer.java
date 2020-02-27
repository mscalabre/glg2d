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

import java.awt.Color;
import java.awt.geom.AffineTransform;







import org.jogamp.glg2d.GLGraphics2D;
import org.jogamp.glg2d.Texturable;
import org.jogamp.glg2d.impl.AbstractImageHelper;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;


import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glVertex2i;

public class GL2ImageDrawer extends AbstractImageHelper {

  protected AffineTransform savedTransform;

  @Override
  public void setG2D(final GLGraphics2D g2d) {
    super.setG2D(g2d);
  }


  @Override
  protected void begin(Texturable texture, AffineTransform xform, Color bgcolor) {
   glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
   glTexParameterf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_BLEND);

    /*
     * FIXME This is unexpected since we never disable blending, but in some
     * cases it interacts poorly with multiple split panes, scroll panes and the
     * text renderer to disable blending.
     */
    g2d.setComposite(g2d.getComposite());

    texture.enable(null);//TODO lwjgl
    texture.bind(null);//TODO lwjgl

    savedTransform = null;
    if (xform != null && !xform.isIdentity()) {
      savedTransform = g2d.getTransform();
      g2d.transform(xform);
    }

    g2d.getColorHelper().setColorRespectComposite(bgcolor == null ? Color.white : bgcolor);
  }

  @Override
  protected void end(Texturable texture) {
    if (savedTransform != null) {
      g2d.setTransform(savedTransform);
    }

    texture.disable(null);//TODO lwjgl
    g2d.getColorHelper().setColorRespectComposite(g2d.getColor());
  }

  @Override
  protected void applyTexture(Texturable texture, int dx1, int dy1, int dx2, int dy2, float sx1, float sy1, float sx2, float sy2) {
   glBegin(GL_QUADS);

    // SW
   glTexCoord2f(sx1, sy2);
   glVertex2i(dx1, dy2);
    // SE
   glTexCoord2f(sx2, sy2);
   glVertex2i(dx2, dy2);
    // NE
   glTexCoord2f(sx2, sy1);
   glVertex2i(dx2, dy1);
    // NW
   glTexCoord2f(sx1, sy1);
   glVertex2i(dx1, dy1);

   glEnd();
  }
}
