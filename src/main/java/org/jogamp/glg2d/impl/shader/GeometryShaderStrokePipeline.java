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


import java.awt.BasicStroke;
import java.nio.FloatBuffer;





import org.jogamp.glg2d.GLG2DUtils;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.ARBGetProgramBinary.glProgramParameteri;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;


import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glIsBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_INPUT_TYPE;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_OUTPUT_TYPE;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_VERTICES_OUT;

public class GeometryShaderStrokePipeline extends AbstractShaderPipeline {
  public static final int DRAW_END_NONE = 0;
  public static final int DRAW_END_FIRST = -1;
  public static final int DRAW_END_LAST = 1;
  public static final int DRAW_END_BOTH = 2;

  protected FloatBuffer vBuffer = BufferUtils.createFloatBuffer(500);

  protected int maxVerticesOut = 32;

  protected int vertCoordLocation;
  protected int vertBeforeLocation;
  protected int vertAfterLocation;
  protected int vertCoordBuffer;

  protected int lineWidthLocation;
  protected int miterLimitLocation;
  protected int joinTypeLocation;
  protected int capTypeLocation;
  protected int drawEndLocation;

  public GeometryShaderStrokePipeline() {
    this("StrokeShader.v", "StrokeShader.g", "StrokeShader.f");
  }

  public GeometryShaderStrokePipeline(String vertexShaderFileName, String geometryShaderFileName, String fragmentShaderFileName) {
    super(vertexShaderFileName, geometryShaderFileName, fragmentShaderFileName);
  }

  public void setStroke(BasicStroke stroke) {
    if (lineWidthLocation >= 0) {
     glUniform1f(lineWidthLocation, stroke.getLineWidth());
    }

    if (miterLimitLocation >= 0) {
     glUniform1f(miterLimitLocation, stroke.getMiterLimit());
    }

    if (joinTypeLocation >= 0) {
     glUniform1i(joinTypeLocation, stroke.getLineJoin());
    }

    if (capTypeLocation >= 0) {
     glUniform1i(capTypeLocation, stroke.getEndCap());
    }
  }

  protected void setDrawEnd(int drawType) {
    if (drawEndLocation >= 0) {
     glUniform1i(drawEndLocation, drawType);
    }
  }

  protected void bindBuffer(FloatBuffer vertexBuffer) {
   glEnableVertexAttribArray(vertCoordLocation);
   glEnableVertexAttribArray(vertBeforeLocation);
   glEnableVertexAttribArray(vertAfterLocation);

    if( glIsBuffer(vertCoordBuffer)) {
     glBindBuffer(GL_ARRAY_BUFFER, vertCoordBuffer);
     glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);
    } else {
      vertCoordBuffer = GLG2DUtils.genBufferId();

     glBindBuffer(GL_ARRAY_BUFFER, vertCoordBuffer);
     glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STREAM_DRAW);
    }

   glVertexAttribPointer(vertCoordLocation, 2, GL_FLOAT, false, 0, 0);
   glVertexAttribPointer(vertBeforeLocation, 2, GL_FLOAT, false, 0, 0);
   glVertexAttribPointer(vertAfterLocation, 2, GL_FLOAT, false, 0, 0);
  }

  public void draw(FloatBuffer vertexBuffer, boolean close) {
    int pos = vertexBuffer.position();
    int lim = vertexBuffer.limit();
    int numPts = (lim - pos) / 2;

    if (numPts * 2 + 6 > vBuffer.capacity()) {
      vBuffer = BufferUtils.createFloatBuffer(numPts * 2 + 4);
    }

    vBuffer.clear();

    if (close) {
      vBuffer.put(vertexBuffer.get(lim - 2));
      vBuffer.put(vertexBuffer.get(lim - 1));
      vBuffer.put(vertexBuffer);
      vBuffer.put(vertexBuffer.get(pos));
      vBuffer.put(vertexBuffer.get(pos + 1));
      vBuffer.put(vertexBuffer.get(pos + 2));
      vBuffer.put(vertexBuffer.get(pos + 3));
    } else {
      vBuffer.put(0);
      vBuffer.put(0);
      vBuffer.put(vertexBuffer);
      vBuffer.put(0);
      vBuffer.put(0);
    }

    vBuffer.flip();

    bindBuffer(vBuffer);

    if (close) {
      setDrawEnd(DRAW_END_NONE);
     glDrawArrays(GL_LINES, 0, numPts + 1);
     glDrawArrays(GL_LINES, 1, numPts);
    } else if (numPts == 2) {
      setDrawEnd(DRAW_END_BOTH);
     glDrawArrays(GL_LINES, 0, 2);
    } else {
      setDrawEnd(DRAW_END_NONE);
     glDrawArrays(GL_LINES, 1, numPts - 2);
     glDrawArrays(GL_LINES, 2, numPts - 3);

      setDrawEnd(DRAW_END_FIRST);
     glDrawArrays(GL_LINES, 0, 2);

      setDrawEnd(DRAW_END_LAST);
     glDrawArrays(GL_LINES, numPts - 2, 2);
    }

   glDisableVertexAttribArray(vertCoordLocation);
   glDisableVertexAttribArray(vertBeforeLocation);
   glDisableVertexAttribArray(vertAfterLocation);

   glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  @Override
  protected void setupUniformsAndAttributes() {
    super.setupUniformsAndAttributes();

    transformLocation =glGetUniformLocation(programId, "u_transform");
    colorLocation =glGetUniformLocation(programId, "u_color");
    lineWidthLocation =glGetUniformLocation(programId, "u_lineWidth");
    miterLimitLocation =glGetUniformLocation(programId, "u_miterLimit");
    joinTypeLocation =glGetUniformLocation(programId, "u_joinType");
    drawEndLocation =glGetUniformLocation(programId, "u_drawEnd");
    capTypeLocation =glGetUniformLocation(programId, "u_capType");

    vertCoordLocation =glGetAttribLocation(programId, "a_vertCoord");
    vertBeforeLocation =glGetAttribLocation(programId, "a_vertBefore");
    vertAfterLocation =glGetAttribLocation(programId, "a_vertAfter");
  }

  @Override
  protected void attachShaders() {
    super.attachShaders();

    glProgramParameteri(programId, GL_GEOMETRY_INPUT_TYPE, GL_LINES);
    glProgramParameteri(programId, GL_GEOMETRY_OUTPUT_TYPE, GL_TRIANGLE_STRIP);
    glProgramParameteri(programId, GL_GEOMETRY_VERTICES_OUT, maxVerticesOut);
  }

  @Override
  public void delete() {
    super.delete();

   glDeleteBuffers(1);
  }
}
