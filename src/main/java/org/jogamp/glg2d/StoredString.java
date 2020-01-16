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

import java.awt.image.BufferedImage;

/**
 *
 * @author mscalabre
 */
public class StoredString {
    private String text;
    private String font;
    private BufferedImage image;

    public StoredString(String text, String font, BufferedImage image) {
        this.text = text;
        this.font = font;
        this.image = image;
    }

    @Override
    public boolean equals(Object obj) {
        
        if(obj instanceof StoredString){
            StoredString st = (StoredString)obj;
            return st.font.equals(this.font) && st.text.equals(this.text);
        }
        
        return false;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }
    
}
