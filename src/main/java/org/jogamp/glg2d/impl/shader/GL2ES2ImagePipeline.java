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

import static org.jogamp.glg2d.GLG2DUtils.ensureIsGLBuffer;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;

import com.jogamp.common.nio.Buffers;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glIsBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class GL2ES2ImagePipeline extends AbstractShaderPipeline {
  protected int vertexBufferId = -1;

  protected int textureLocation = -1;
  protected int vertCoordLocation = -1;
  protected int texCoordLocation = -1;

  public GL2ES2ImagePipeline() {
    this("TextureShader.v", "TextureShader.f");
  }

  public GL2ES2ImagePipeline(String vertexShaderFileName, String fragmentShaderFileName) {
    super(vertexShaderFileName, null, fragmentShaderFileName);
  }

  public void setTextureUnit(GL2ES2 gl, int unit) {
    if (textureLocation >= 0) {
     glUniform1i(textureLocation, unit);
    }
  }

  protected void bufferData(GL2ES2 gl, FloatBuffer buffer) {
    vertexBufferId = ensureIsGLBuffer(gl, vertexBufferId);

   glEnableVertexAttribArray(vertCoordLocation);
   glEnableVertexAttribArray(texCoordLocation);

   glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId);
   glBufferData(GL.GL_ARRAY_BUFFER, buffer, GL.GL_STATIC_DRAW);

   glVertexAttribPointer(vertCoordLocation, 2, GL.GL_FLOAT, false, 4 * Buffers.SIZEOF_FLOAT, 0);
   glVertexAttribPointer(texCoordLocation, 2, GL.GL_FLOAT, false, 4 * Buffers.SIZEOF_FLOAT, 2 * Buffers.SIZEOF_FLOAT);
  }

  public void draw(GL2ES2 gl, FloatBuffer interleavedVertTexBuffer) {
    bufferData(gl, interleavedVertTexBuffer);

   glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);

   glDisableVertexAttribArray(vertCoordLocation);
   glDisableVertexAttribArray(texCoordLocation);
   glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }

  @Override
  protected void setupUniformsAndAttributes(GL2ES2 gl) {
    super.setupUniformsAndAttributes(gl);

    transformLocation =glGetUniformLocation(programId, "u_transform");
    colorLocation =glGetUniformLocation(programId, "u_color");
    textureLocation =glGetUniformLocation(programId, "u_tex");

    vertCoordLocation =glGetAttribLocation(programId, "a_vertCoord");
    texCoordLocation =glGetAttribLocation(programId, "a_texCoord");
  }

  @Override
  public void delete(GL2ES2 gl) {
    super.delete(gl);

    if( glIsBuffer(vertexBufferId)) {
     glDeleteBuffers(1);
    }
  }
}
