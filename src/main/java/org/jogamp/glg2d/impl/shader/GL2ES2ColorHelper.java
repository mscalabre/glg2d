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
package org.jogamp.glg2d.impl.shader;




import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.nio.FloatBuffer;

import org.jogamp.glg2d.GLGraphics2D;
import org.jogamp.glg2d.impl.AbstractColorHelper;
import org.jogamp.glg2d.impl.shader.UniformBufferObject.ColorHook;
import org.lwjgl.BufferUtils;

public class GL2ES2ColorHelper extends AbstractColorHelper implements ColorHook {
  protected FloatBuffer foregroundRGBA = BufferUtils.createFloatBuffer(4);

  protected GL2ES2ImagePipeline pipeline;

  public GL2ES2ColorHelper() {
    this(new GL2ES2ImagePipeline());
  }

  public GL2ES2ColorHelper(GL2ES2ImagePipeline pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public void setG2D(GLGraphics2D g2d) {
    if (g2d instanceof GLShaderGraphics2D) {
      ((GLShaderGraphics2D) g2d).getUniformsObject().colorHook = this;
    } else {
      throw new IllegalArgumentException(GLGraphics2D.class.getName() + " implementation must be instance of "
          + GLShaderGraphics2D.class.getSimpleName());
    }

    super.setG2D(g2d);
  }
  
    @Override
  public void setColorNoRespectComposite(Color c) {
    foregroundRGBA.put(0, c.getRed() / 255f);
    foregroundRGBA.put(1, c.getGreen() / 255f);
    foregroundRGBA.put(2, c.getBlue() / 255f);
    foregroundRGBA.put(3, c.getAlpha() / 255f);
  }

  @Override
  public void setColorRespectComposite(Color c) {
    float alpha = getAlpha();
    foregroundRGBA.put(0, c.getRed() / 255f);
    foregroundRGBA.put(1, c.getGreen() / 255f);
    foregroundRGBA.put(2, c.getBlue() / 255f);
    foregroundRGBA.put(3, (c.getAlpha() / 255f) * alpha);
  }

  @Override
  public void setPaintMode() {
    // not implemented yet
  }

  @Override
  public void setXORMode(Color c) {
    // not implemented yet
  }

  @Override
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
//    GL2ES2 gl = g2d.getGLContext().getGL().getGL2ES2();
//
//    if (!pipeline.isSetup()) {
//      pipeline.setup(gl);
//    }
//
//    pipeline.use(gl, true);
//
//    float[] glMatrix = new float[16];
//    glMatrix[0] = 1;
//    // glMatrix[1] = 0;
//    // glMatrix[2] = 0;
//    // glMatrix[3] = 0;
//
//    // glMatrix[4] = 0;
//    glMatrix[5] = 1;
//    // glMatrix[6] = 0;
//    // glMatrix[7] = 0;
//
//    // glMatrix[8] = 0;
//    // glMatrix[9] = 0;
//    glMatrix[10] = 1;
//    // glMatrix[11] = 0;
//
//    // glMatrix[12] = 0;
//    // glMatrix[13] = 0;
//    // glMatrix[14] = 0;
//    glMatrix[15] = 1;
//
//    pipeline.setColor(gl, new float[] { 1, 1, 1, 1 });
//    pipeline.setTextureUnit(gl, GL_TEXTURE0);
//    pipeline.setTransform(gl, glMatrix);
//
//    int numPixels = width * height;
//    FloatBuffer data = BufferUtils.createFloatBuffer(numPixels);
//
//    int glX = x;
//    int glY = g2d.getCanvasHeight() - (y + height);
//
//   glReadPixels(glX, glY, width, height, GL_RGBA, GL_FLOAT, data);
//
//   glEnable(GL_TEXTURE_2D);
//   glActiveTexture(GL_TEXTURE0);
//
//    int[] ids = new int[1];
//   glGenTextures(1, ids, 0);
//
//   glBindTexture(GL_TEXTURE_2D, ids[0]);
//
//   glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
//   glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
//
//    /*
//     * TODO This will need to be power-of-2
//     */
//   glTexImage2D(GL_TEXTURE_2D, 0,GL_RGBA, width, height, 0,GL_RGBA,GL_FLOAT, data);
//
//    data.clear();
//
//    // translate to new location
//    glX += dx;
//    glY -= dy;
//
//    // interleave vertex and Texturable coordinates
//    data.put(glX);
//    data.put(glY);
//    data.put(0);
//    data.put(0);
//
//    data.put(glX + width);
//    data.put(glY);
//    data.put(1);
//    data.put(0);
//
//    data.put(glX + width);
//    data.put(glY + height);
//    data.put(1);
//    data.put(1);
//
//    data.put(glX);
//    data.put(glY + height);
//    data.put(0);
//    data.put(1);
//
//    data.flip();
//
//    pipeline.draw(gl, data);
//
//   glDeleteTextures(1, ids, 0);
//
//   glDisable(GL_TEXTURE_2D);
//
//    pipeline.use(gl, false);
  }

  @Override
  public float getAlpha() {
    Composite composite = getComposite();
    float alpha = 1;
    if (composite instanceof AlphaComposite) {
      alpha = ((AlphaComposite) composite).getAlpha();
    }

    return alpha;
  }

  @Override
  public FloatBuffer getRGBA() {
    return foregroundRGBA;
  }
}
