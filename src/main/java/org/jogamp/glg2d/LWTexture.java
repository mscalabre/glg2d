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
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import org.lwjgl.opengl.GL12;

/**
 *
 * @author mscalabre
 */
public class LWTexture extends Texture{
    private int textureId;
    private int width;
    private int height;

    public LWTexture() {
        super(0);
    }

    public LWTexture(int textureId, int i) {
        super(i);
        this.textureId = textureId;
    }

    public LWTexture(int textureId, GL gl, TextureData td) throws GLException {
        super(gl, td);
        this.textureId = textureId;
    }

    public LWTexture(int textureId, int i, int i1, int i2, int i3, int i4, int i5, boolean bln) {
        super(i, i1, i2, i3, i4, i5, bln);
        this.textureId = textureId;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }
    
    public LWTexture(BufferedImage image){
        super(0);
        
        
        ByteBuffer textureBuffer = BufferUtils.createByteBuffer(image.getWidth()*image.getHeight()*4);
        
        int textureID = GL11.glGenTextures();
        
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
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
        
        glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureBuffer);
        
        this.textureId = textureID;
        this.width = image.getWidth();
        this.height = image.getHeight();
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
}
