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
import static org.lwjgl.opengl.GL11.GL_FLOAT;




import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glIsBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class AnyModePipeline extends AbstractShaderPipeline {
  protected int vertCoordBuffer = -1;
  protected int vertCoordLocation = -1;

  public AnyModePipeline() {
    this("FixedFuncShader.v", "FixedFuncShader.f");
  }

  public AnyModePipeline(String vertexShaderFileName, String fragmentShaderFileName) {
    super(vertexShaderFileName, null, fragmentShaderFileName);
  }

  public void bindBuffer() {
   glEnableVertexAttribArray(vertCoordLocation);
    vertCoordBuffer = ensureIsGLBuffer(vertCoordBuffer);

   glBindBuffer(GL_ARRAY_BUFFER, vertCoordBuffer);
   glVertexAttribPointer(vertCoordLocation, 2, GL_FLOAT, false, 0, 0);
  }

  public void bindBufferData(FloatBuffer vertexBuffer) {
    bindBuffer();

    int count = vertexBuffer.limit() - vertexBuffer.position();
   glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STREAM_DRAW);
  }

  public void unbindBuffer() {
   glDisableVertexAttribArray(vertCoordLocation);
   glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  public void draw(int mode, FloatBuffer vertexBuffer) {
    bindBufferData(vertexBuffer);

    int numPts = (vertexBuffer.limit() - vertexBuffer.position()) / 2;
   glDrawArrays(mode, 0, numPts);

    unbindBuffer();
  }

  @Override
  protected void setupUniformsAndAttributes() {
    super.setupUniformsAndAttributes();

    transformLocation =glGetUniformLocation(programId, "u_transform");
    colorLocation =glGetUniformLocation(programId, "u_color");

    vertCoordLocation =glGetAttribLocation(programId, "a_vertCoord");
  }

  @Override
  public void delete() {
    super.delete();

    if( glIsBuffer(vertCoordBuffer)) {
     glDeleteBuffers(1);
    }
  }
}
