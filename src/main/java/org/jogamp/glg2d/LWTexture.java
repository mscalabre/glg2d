/*
 * Copyright 2020 mscalabre.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jogamp.glg2d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import org.lwjgl.opengl.GL12;

/**
 *
 * @author mscalabre
 */
public class LWTexture implements Texturable{
    private int textureId;
    private int width;
    private int height;
    
    public LWTexture() {
    }

    public LWTexture(int textureId, int i) {
        this.textureId = textureId;
    }

    public LWTexture(int textureId, GL gl, TextureData td) throws GLException {
        this.textureId = textureId;
    }

    public LWTexture(int textureId, int i, int i1, int i2, int i3, int i4, int i5, boolean bln) {
        this.textureId = textureId;
    }

    public int getTextureId() {
        return textureId;
    }

    @Override
    public int getTextureObject() {
        return getTextureId();
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }
    
    public LWTexture(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        
        int textureID = GL11.glGenTextures();
        
        ByteBuffer textureBuffer = BufferUtils.createByteBuffer(width*height*4);
        
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixel = pixels[y * width + x];
                textureBuffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                textureBuffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                textureBuffer.put((byte) (pixel & 0xFF));               // Blue component
                textureBuffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }
        textureBuffer.flip();
         
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
//        
        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureBuffer);
        
        this.textureId = textureID;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public TextureCoords getImageTexCoords() {
        return new TextureCoords(0,1,1,0);
    }

    @Override
    public void destroy(GL gl) throws GLException {
        glDeleteTextures(this.textureId);
    }

    @Override
    public void enable(GL gl) {
    }

    @Override
    public void disable(GL gl) {
    }

    @Override
    public void bind(GL gl) {
    }
    
}
