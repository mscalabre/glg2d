package org.jogamp.glg2d.impl.gl2;

import java.awt.Color;
import java.awt.Font;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author mscalabre
 */
public class GL2TextRenderer {
    private Font font;
    private boolean antiAlias;
    private Color color = Color.white;
    
    private TrueTypeFont trueFont = null;

    public GL2TextRenderer(Font font, boolean antiAlias) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.trueFont = new TrueTypeFont(font, antiAlias);
    }

    void draw3D(String string, int x, int y, int width, int height) {
        trueFont.drawString(x, y, string, new org.newdawn.slick.Color(color.getRGB()));
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
