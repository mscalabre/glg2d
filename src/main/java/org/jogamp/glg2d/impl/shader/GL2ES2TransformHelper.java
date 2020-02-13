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


import java.awt.geom.AffineTransform;



import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jogamp.glg2d.GLGraphics2D;
import org.jogamp.glg2d.impl.AbstractMatrixHelper;
import org.jogamp.glg2d.impl.shader.UniformBufferObject.TransformHook;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetInteger;

public class GL2ES2TransformHelper extends AbstractMatrixHelper implements TransformHook {
  protected FloatBuffer glMatrix;
  protected boolean dirtyMatrix;

  protected IntBuffer viewportDimensions=BufferUtils.createIntBuffer(16);

  @Override
  public void setG2D(GLGraphics2D g2d) {
    super.setG2D(g2d);

    dirtyMatrix = true;
    glMatrix = BufferUtils.createFloatBuffer(16);

    glGetInteger(GL_VIEWPORT, viewportDimensions);

    if (g2d instanceof GLShaderGraphics2D) {
      ((GLShaderGraphics2D) g2d).getUniformsObject().transformHook = this;
    } else {
      throw new IllegalArgumentException(GLGraphics2D.class.getName() + " implementation must be instance of "
          + GLShaderGraphics2D.class.getSimpleName());
    }
  }
  
  @Override
  public FloatBuffer getGLMatrixData() {
    return getGLMatrixData(null);
  }

  @Override
  protected void flushTransformToOpenGL() {
    // only set dirty, we'll update lazily
    dirtyMatrix = true;
  }

  @Override
  public FloatBuffer getGLMatrixData(AffineTransform concat) {
    if (concat == null || concat.isIdentity()) {
      if (dirtyMatrix) {
        updateGLMatrix(getTransform0());
        dirtyMatrix = false;
      }
    } else {
      AffineTransform tmp = getTransform();
      tmp.concatenate(concat);
      updateGLMatrix(tmp);
      dirtyMatrix = true;
    }

    return glMatrix;
  }

  protected void updateGLMatrix(AffineTransform xform) {
    // add the GL->G2D coordinate transform and perspective inline here

    // Note this isn't quite the same as the GL2 implementation because GL2 has
    // an orthographic projection matrix

    float x1 = viewportDimensions.get(0);
    float y1 = viewportDimensions.get(1);
    float x2 = viewportDimensions.get(2);
    float y2 = viewportDimensions.get(3);

    float invWidth = 1f / (x2 - x1);
    float invHeight = 1f / (y2 - y1);
    
    glMatrix.put(0, ((float) (2 * xform.getScaleX() * invWidth)));
    glMatrix.put(1, ((float) (2 * xform.getShearY() * invHeight)));
    // glMatrix[2] = 0;
    // glMatrix[3] = 0;

    glMatrix.put(4, ((float) (2 * xform.getShearX() * invWidth)));
    glMatrix.put(5, ((float) (2 * xform.getScaleY() * invHeight)));
    // glMatrix[6] = 0;
    // glMatrix[7] = 0;

    // glMatrix[8] = 0;
    // glMatrix[9] = 0;
    glMatrix.put(10, 1);
    // glMatrix[11] = 0;

    glMatrix.put(12, ((float) (2 * xform.getTranslateX() * invWidth - 1)));
    glMatrix.put(13, ((float) (2 * xform.getTranslateY() * invHeight - 1)));
    // glMatrix[14] = 0;
    glMatrix.put(15, 1);
  }
}
