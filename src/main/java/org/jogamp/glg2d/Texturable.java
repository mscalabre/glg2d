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
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 *
 * @author mscalabre
 */
public interface Texturable {
    

    public int getTextureId();

    public void setTextureId(int textureId);

    public int getWidth();

    public int getHeight();

    public TextureCoords getImageTexCoords();

    public void destroy(GL gl);
    
    
    public int getTextureObject();
    
    public void enable(GL gl);
    public void disable(GL gl);
    public void bind(GL gl);
}
