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

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;





import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertexPointer;

/**
 * Wraps a simple {@code FloatBuffer} and makes it easier to push 2-D vertices
 * into the buffer and then draw them using any mode desired. The default
 * constructor uses a global buffer since drawing in OpenGL is not
 * multi-threaded.
 */
public class VertexBuffer {
  protected static VertexBuffer shared = new VertexBuffer(1024);

  protected FloatBuffer buffer;

  protected int deviceBufferId;

  /**
   * Creates a buffer that uses the shared global buffer. This is faster than
   * allocating multiple float buffers. Since OpenGL is single-threaded, we can
   * assume this won't be accessed outside the OpenGL thread and typically one
   * object is drawn completely before another one. If this is not true, one of
   * the objects being drawn simultaneously must use a private buffer. See
   * {@code #VertexBuffer(int)}.
   */
  public static VertexBuffer getSharedBuffer() {
    shared.clear();
    return shared;
  }

  protected VertexBuffer(FloatBuffer buffer) {
    this.buffer = buffer;
  }

  /**
   * Creates a private buffer. This can be used without fear of clobbering the
   * global buffer. This should only be used if you have a need to create two
   * parallel shapes at the same time.
   * 
   * @param capacity
   *          The size of the buffer in number of vertices
   */
  public VertexBuffer(int capacity) {
    this(BufferUtils.createFloatBuffer(capacity * 2));
  }

  /**
   * Adds multiple vertices to the buffer.
   * 
   * @param array
   *          The array containing vertices in the form (x,y),(x,y)
   * @param offset
   *          The starting index
   * @param numVertices
   *          The number of vertices, pairs of floats
   */
  public void addVertex(float[] array, int offset, int numVertices) {
    int numFloats = numVertices * 2;
    ensureCapacity(numFloats);
    buffer.put(array, offset, numFloats);
  }

  /**
   * Adds a vertex to the buffer.
   * 
   * @param x
   *          The x coordinate
   * @param y
   *          The y coordinate
   */
  public void addVertex(float x, float y) {
    ensureCapacity(2);
    buffer.put(x);
    buffer.put(y);
  }

  /**
   * Adds multiple vertices to the buffer.
   * 
   * @param vertices
   *          The buffer of new vertices to add.
   */
  public void addVertices(FloatBuffer vertices) {
    int size = vertices.limit() - vertices.position();
    ensureCapacity(size);

    buffer.put(vertices);
  }

  protected void ensureCapacity(int numNewFloats) {
    if (buffer.capacity() <= buffer.position() + numNewFloats) {
      FloatBuffer larger = BufferUtils.createFloatBuffer(Math.max(buffer.position() * 2, buffer.position() + numNewFloats));
      deviceBufferId = -deviceBufferId;
      int position = buffer.position();
      buffer.rewind();
      larger.put(buffer);
      buffer = larger;
      buffer.position(position);
    }
  }

  /**
   * Discard all existing points. This method is not necessary unless the points
   * already added are not needed anymore and the buffer will be reused.
   */
  public void clear() {
    buffer.clear();
  }

  public FloatBuffer getBuffer() {
    return buffer;
  }

  /**
   * Draws the vertices and rewinds the buffer to be ready to draw next time.
   * 
   * @param gl
   *          The graphics context to use to draw
   * @param mode
   *          The mode, e.g. {@code GL#GL_LINE_STRIP}
   */
  public void drawBuffer(int mode) {
    if (buffer.position() == 0) {
      return;
    }

    int count = buffer.position();
    buffer.rewind();

    glVertexPointer(2, 0, buffer);

   glEnableClientState(GL_VERTEX_ARRAY);
   glDrawArrays(mode, 0, count / 2);
   glDisableClientState(GL_VERTEX_ARRAY);

    buffer.position(count);
  }
}
