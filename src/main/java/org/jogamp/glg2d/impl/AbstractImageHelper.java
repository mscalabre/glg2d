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
package org.jogamp.glg2d.impl;


import com.jogamp.opengl.util.texture.TextureCoords;
import static org.jogamp.glg2d.GLG2DRenderingHints.KEY_CLEAR_TEXTURES_CACHE;
import static org.jogamp.glg2d.GLG2DRenderingHints.VALUE_CLEAR_TEXTURES_CACHE_DEFAULT;
import static org.jogamp.glg2d.GLG2DRenderingHints.VALUE_CLEAR_TEXTURES_CACHE_EACH_PAINT;
import static org.jogamp.glg2d.impl.GLG2DNotImplemented.notImplemented;

import java.awt.Color;
import java.awt.Image;
import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.VolatileImage;
import java.awt.image.renderable.RenderableImage;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jogamp.glg2d.GLG2DImageHelper;
import org.jogamp.glg2d.GLG2DRenderingHints;
import org.jogamp.glg2d.GLGraphics2D;
import org.jogamp.glg2d.LWTexture;
import org.jogamp.glg2d.Texturable;





public abstract class AbstractImageHelper implements GLG2DImageHelper {
  private static final Logger LOGGER = Logger.getLogger(AbstractImageHelper.class.getName());

  /**
   * See {@link GLG2DRenderingHints#KEY_CLEAR_TEXTURES_CACHE}
   */
  protected TextureCache imageCache = new TextureCache();
  protected Object clearCachePolicy;

  protected GLGraphics2D g2d;

  protected abstract void begin(Texturable texture, AffineTransform xform, Color bgcolor);

  protected abstract void applyTexture(Texturable texture, int dx1, int dy1, int dx2, int dy2,
      float sx1, float sy1, float sx2, float sy2);

  protected abstract void end(Texturable texture);
  
  @Override
  public void setG2D(GLGraphics2D g2d) {
    this.g2d = g2d;

    if (clearCachePolicy == VALUE_CLEAR_TEXTURES_CACHE_EACH_PAINT) {
      imageCache.clear();
    }
  }
  

  @Override
  public void push(GLGraphics2D newG2d) {
    // nop
  }

  @Override
  public void pop(GLGraphics2D parentG2d) {
    // nop
  }

  @Override
  public void setHint(Key key, Object value) {
    if (key == KEY_CLEAR_TEXTURES_CACHE) {
      clearCachePolicy = value;
    }
  }

  @Override
  public void resetHints() {
    clearCachePolicy = VALUE_CLEAR_TEXTURES_CACHE_DEFAULT;
  }

  @Override
  public void dispose() {
    while(!imageCache.isEmpty()){
        List<WeakKey<Image>> images = new ArrayList<WeakKey<Image>>();
        for(WeakKey<Image> weakKey : imageCache.keySet()){
            images.add(weakKey);
        }
        for(WeakKey<Image> image : images){
            if(image!=null){
                imageCache.remove(image, false);
                if(imageCache.get(image)!=null){
                    destroy(imageCache.get(image), false);
                }
            }
        }
        imageCache.clear();
    }
    System.out.println("imageCache length " + imageCache.size()); 
    imageCache = null;
  }

  @Override
  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
    return drawImage(img, AffineTransform.getTranslateInstance(x, y), bgcolor, observer);
  }

  @Override
  public boolean drawImage(Image img, AffineTransform xform, ImageObserver observer) {
    return drawImage(img, xform, (Color) null, observer);
  }

  @Override
  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
    double imgHeight = img.getHeight(null);
    double imgWidth = img.getWidth(null);

    if (imgHeight < 0 || imgWidth < 0) {
      return false;
    }

    AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
    transform.scale(width / imgWidth, height / imgHeight);
    return drawImage(img, transform, bgcolor, observer);
  }

  @Override
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2,
      int sy2, Color bgcolor, ImageObserver observer) {
    Texturable texture = getTexture(img, observer);
    if (texture == null) {
      return false;
    }

    float height = texture.getHeight();
    float width = texture.getWidth();
    begin(texture, null, bgcolor);
    applyTexture(texture, dx1, dy1, dx2, dy2, sx1 / width, sy1 / height, sx2 / width, sy2 / height);
    end(texture);

    return true;
  }

  protected boolean drawImage(Image img, AffineTransform xform, Color color, ImageObserver observer) {
    Texturable texture = getTexture(img, observer);
    if (texture == null) {
      return false;
    }

    begin(texture, xform, color);
    applyTexture(texture);
    end(texture);

    return true;
  }

  protected void applyTexture(Texturable texture) {
    int width = texture.getWidth();
    int height = texture.getHeight();
    TextureCoords coords = texture.getImageTexCoords();

    applyTexture(texture, 0, 0, width, height, coords.left(), coords.top(), coords.right(), coords.bottom());
  }

  /**
   * Cache the Texturable if possible. I have a feeling this will run into issues
   * later as images change. Just not sure how to handle it if they do. I
   * suspect I should be using the ImageConsumer class and dumping pixels to the
   * screen as I receive them.
   * 
   * <p>
   * If an image is a BufferedImage, turn it into a Texturable and cache it. If
   * it's not, draw it to a BufferedImage and see if all the image data is
   * available. If it is, cache it. If it's not, don't cache it. But if not all
   * the image data is available, we will draw it what we have, since we draw
   * anything in the image to a BufferedImage.
   * </p>
   */
  protected Texturable getTexture(Image image, ImageObserver observer) {
    Texturable texture = imageCache.get(image);
    if (texture == null) {
      BufferedImage bufferedImage;
      if (image instanceof BufferedImage && ((BufferedImage) image).getType() != BufferedImage.TYPE_CUSTOM) {
        bufferedImage = (BufferedImage) image;
      } else {
        bufferedImage = toBufferedImage(image);
      }

      if (bufferedImage != null) {
        texture = create(bufferedImage);
        addToCache(image, texture);
      }
    }
    
    return texture;
  }

  protected Texturable create(BufferedImage image) {
    // we'll assume the image is complete and can be rendered
    return null;
    //Cannot work with the new system
//    return AWTTextureIO.newTexture(g2d.getGLContext().getGL().getGLProfile(), image, false);
  }

  protected void destroy(final Texturable texture, boolean onExecutor) {
      Runnable runnable = new Runnable(){
          @Override
          public void run() {
            if(!(texture instanceof LWTexture)){
                texture.destroy(g2d.getGLContext().getGL());
            }else{
                texture.destroy(null);
            }
          }
          
      };
      if(onExecutor){
          g2d.getExecutor().execute(runnable);
      }else{
          runnable.run();
      }
  }

  protected void addToCache(Image image, Texturable texture) {
    if (clearCachePolicy instanceof Number) {
      int maxSize = ((Number) clearCachePolicy).intValue();
      if (imageCache.size() > maxSize) {
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.fine("Clearing Texturable cache with size " + imageCache.size());
        }

        imageCache.clear();
      }
    }

    imageCache.put(image, texture);
  }

  protected BufferedImage toBufferedImage(Image image) {
    if (image instanceof VolatileImage) {
      return ((VolatileImage) image).getSnapshot();
    }

    int width = image.getWidth(null);
    int height = image.getHeight(null);
    if (width < 0 || height < 0) {
      return null;
    }

    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    bufferedImage.createGraphics().drawImage(image, null, null);
    return bufferedImage;
  }

  @Override
  public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
    notImplemented("drawImage(BufferedImage, BufferedImageOp, int, int)");
  }

  @Override
  public void drawImage(RenderedImage img, AffineTransform xform) {
    notImplemented("drawImage(RenderedImage, AffineTransform)");
  }

  @Override
  public void drawImage(RenderableImage img, AffineTransform xform) {
    notImplemented("drawImage(RenderableImage, AffineTransform)");
  }
    public Texturable invalidateImage(BufferedImage image){
        return this.imageCache.remove(image);
    }
  /**
   * We could use a WeakHashMap here, but we want access to the ReferenceQueue
   * so we can dispose the Textures when the Image is no longer referenced.
   */
  @SuppressWarnings("serial")
  protected class TextureCache extends HashMap<WeakKey<Image>, Texturable> {
    private ReferenceQueue<Image> queue = new ReferenceQueue<Image>();

    public void expungeStaleEntries() {
        expungeStaleEntries(true);
    }
    public void expungeStaleEntries(boolean onExecutor) {
      Reference<? extends Image> ref = queue.poll();
      while (ref != null) {
        Texturable texture = remove(ref);
        if (texture != null) {
          destroy(texture, onExecutor);
        }

        ref = queue.poll();
      }
    }

    public Texturable get(Image image) {
      expungeStaleEntries();
      WeakKey<Image> key = new WeakKey<Image>(image, null);
      return get(key);
    }
    
    public Texturable remove(Image image) {
        return remove(image, true);
    }
    
    public Texturable remove(Image image, boolean onExecutor) {
      expungeStaleEntries(onExecutor);
      WeakKey<Image> key = new WeakKey<Image>(image, null);
      return super.remove(key);
    }

    public Texturable put(Image image, Texturable texture) {
      expungeStaleEntries();
      WeakKey<Image> key = new WeakKey<Image>(image, queue);
      return put(key, texture);
    }
  }

    @Override
    public void clearCacheImage(Image image) {
        imageCache.remove(image);
    }

  protected static class WeakKey<T> extends WeakReference<T> {
    private final int hash;

    public WeakKey(T value, ReferenceQueue<T> queue) {
      super(value, queue);
      hash = value.hashCode();
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if (obj instanceof WeakKey) {
        WeakKey<?> other = (WeakKey<?>) obj;
        return other.hash == hash && get() == other.get();
      } else {
        return false;
      }
    }
  }
}