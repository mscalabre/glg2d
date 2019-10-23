/*
 * Copyright 2019 mscalabre.
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
package org.jogamp.glg2d.examples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.glg2d.GLG2DPanel;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjglfx.util.LWJGLUtils;

/**
 *
 * @author mscalabre
 */
public class LWJGLExample {
    public static void main(String[] args) {
        try {
            LWJGLUtils.bindNativePath();

            Pbuffer pbuffer = new Pbuffer(1, 1, new PixelFormat(), null, null, new ContextAttribs().withDebug(true));
            pbuffer.makeCurrent();

            JFrame jframe = new JFrame("LWJGLExample");
            GLG2DPanel glPanel = new GLG2DPanel(new JPanel(){
                @Override
                public void paint(Graphics g) {
                    g.setColor(Color.white);
                    g.fillRect(0, 0, 1000, 1000);
                    g.setColor(Color.red);
                    g.fillRect(250,250,500,500);
                }
            }, false);
            glPanel.setLWJGLContext(pbuffer.getContext());
            jframe.setContentPane(glPanel);
            jframe.setPreferredSize(new Dimension(1000,1000));
            jframe.pack();
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jframe.setVisible(true);

        } catch (LWJGLException ex) {
            Logger.getLogger(LWJGLExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
