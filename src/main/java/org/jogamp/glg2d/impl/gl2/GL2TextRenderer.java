package org.jogamp.glg2d.impl.gl2;

import java.awt.Color;
import java.awt.Font;
import com.digiturtle.ui.GLFont;

/**
 *
 * @author mscalabre
 */
public class GL2TextRenderer {
    private Font font;
    private boolean antiAlias;
    private Color color = Color.white;
    
    private GLFont glFont = null;

    public GL2TextRenderer(Font font, boolean antiAlias) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.glFont = new GLFont(font, font.getSize());
    }

    void draw3D(String string, int x, int y, int width, int height) {
        glFont.drawText(x, y, string, this.color);
   }

    void setColor(Color color) {
        this.color = color;
    }

    void begin3DRendering() {
    }

    void end3DRendering() {
    }

    void dispose() {
    }
    
    
}
