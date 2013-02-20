package org.jogamp.glg2d.newt;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PageAttributes;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.AWTEventListener;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Properties;

import sun.awt.KeyboardFocusManagerPeerProvider;

public class HackedToolkit extends Toolkit implements KeyboardFocusManagerPeerProvider {
  private static Toolkit delegate;

  public static void init() {
    final String actualToolkit = System.getProperty("awt.toolkit");
    delegate = AccessController.doPrivileged(new PrivilegedAction<Toolkit>() {
      @Override
      public Toolkit run() {
        try {
          @SuppressWarnings("unchecked")
          Class<Toolkit> clazz = (Class<Toolkit>) Class.forName(actualToolkit);
          return clazz.newInstance();
        } catch (ClassNotFoundException ex) {
          throw new RuntimeException(ex);
        } catch (InstantiationException ex) {
          throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
          throw new RuntimeException(ex);
        }
      }
    });

    System.setProperty("awt.toolkit", HackedToolkit.class.getName());
    Toolkit.getDefaultToolkit();
  }

  public static HackedToolkit getToolkit() {
    return (HackedToolkit) Toolkit.getDefaultToolkit();
  }

  public void setDynamicLayout(boolean dynamic) throws HeadlessException {
    delegate.setDynamicLayout(dynamic);
  }

  public boolean isDynamicLayoutActive() throws HeadlessException {
    return delegate.isDynamicLayoutActive();
  }

  public Dimension getScreenSize() throws HeadlessException {
    return delegate.getScreenSize();
  }

  public int getScreenResolution() throws HeadlessException {
    return delegate.getScreenResolution();
  }

  public Insets getScreenInsets(GraphicsConfiguration gc) throws HeadlessException {
    return delegate.getScreenInsets(gc);
  }

  public ColorModel getColorModel() throws HeadlessException {
    return delegate.getColorModel();
  }

  @SuppressWarnings("deprecation")
  public String[] getFontList() {
    return delegate.getFontList();
  }

  @SuppressWarnings("deprecation")
  public FontMetrics getFontMetrics(Font font) {
    return delegate.getFontMetrics(font);
  }

  public void sync() {
    delegate.sync();
  }

  public Image getImage(String filename) {
    return delegate.getImage(filename);
  }

  public Image getImage(URL url) {
    return delegate.getImage(url);
  }

  public Image createImage(String filename) {
    return delegate.createImage(filename);
  }

  public Image createImage(URL url) {
    return delegate.createImage(url);
  }

  public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
    return delegate.prepareImage(image, width, height, observer);
  }

  public int checkImage(Image image, int width, int height, ImageObserver observer) {
    return delegate.checkImage(image, width, height, observer);
  }

  public Image createImage(ImageProducer producer) {
    return delegate.createImage(producer);
  }

  public Image createImage(byte[] imagedata) {
    return delegate.createImage(imagedata);
  }

  public Image createImage(byte[] imagedata, int imageoffset, int imagelength) {
    return delegate.createImage(imagedata, imageoffset, imagelength);
  }

  public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props) {
    return delegate.getPrintJob(frame, jobtitle, props);
  }

  public PrintJob getPrintJob(Frame frame, String jobtitle, JobAttributes jobAttributes, PageAttributes pageAttributes) {
    return delegate.getPrintJob(frame, jobtitle, jobAttributes, pageAttributes);
  }

  public void beep() {
    delegate.beep();
  }

  public Clipboard getSystemClipboard() throws HeadlessException {
    return delegate.getSystemClipboard();
  }

  public Clipboard getSystemSelection() throws HeadlessException {
    return delegate.getSystemSelection();
  }

  public int getMenuShortcutKeyMask() throws HeadlessException {
    return delegate.getMenuShortcutKeyMask();
  }

  public boolean getLockingKeyState(int keyCode) throws UnsupportedOperationException {
    return delegate.getLockingKeyState(keyCode);
  }

  public void setLockingKeyState(int keyCode, boolean on) throws UnsupportedOperationException {
    delegate.setLockingKeyState(keyCode, on);
  }

  public Cursor createCustomCursor(Image cursor, Point hotSpot, String name) throws IndexOutOfBoundsException, HeadlessException {
    return delegate.createCustomCursor(cursor, hotSpot, name);
  }

  public Dimension getBestCursorSize(int preferredWidth, int preferredHeight) throws HeadlessException {
    return delegate.getBestCursorSize(preferredWidth, preferredHeight);
  }

  public int getMaximumCursorColors() throws HeadlessException {
    return delegate.getMaximumCursorColors();
  }

  public boolean isFrameStateSupported(int state) throws HeadlessException {
    return delegate.isFrameStateSupported(state);
  }

  public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dge) throws InvalidDnDOperationException {
    return delegate.createDragSourceContextPeer(dge);
  }

  public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> abstractRecognizerClass, DragSource ds, Component c,
      int srcActions, DragGestureListener dgl) {
    return delegate.createDragGestureRecognizer(abstractRecognizerClass, ds, c, srcActions, dgl);
  }

  public void addPropertyChangeListener(String name, PropertyChangeListener pcl) {
    delegate.addPropertyChangeListener(name, pcl);
  }

  public void removePropertyChangeListener(String name, PropertyChangeListener pcl) {
    delegate.removePropertyChangeListener(name, pcl);
  }

  public PropertyChangeListener[] getPropertyChangeListeners() {
    return delegate.getPropertyChangeListeners();
  }

  public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
    return delegate.getPropertyChangeListeners(propertyName);
  }

  public boolean isAlwaysOnTopSupported() {
    return delegate.isAlwaysOnTopSupported();
  }

  public boolean isModalityTypeSupported(ModalityType modalityType) {
    return delegate.isModalityTypeSupported(modalityType);
  }

  public boolean isModalExclusionTypeSupported(ModalExclusionType modalExclusionType) {
    return delegate.isModalExclusionTypeSupported(modalExclusionType);
  }

  public void addAWTEventListener(AWTEventListener listener, long eventMask) {
    delegate.addAWTEventListener(listener, eventMask);
  }

  public void removeAWTEventListener(AWTEventListener listener) {
    delegate.removeAWTEventListener(listener);
  }

  public AWTEventListener[] getAWTEventListeners() {
    return delegate.getAWTEventListeners();
  }

  public AWTEventListener[] getAWTEventListeners(long eventMask) {
    return delegate.getAWTEventListeners(eventMask);
  }

  public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException {
    return delegate.mapInputMethodHighlight(highlight);
  }

  @Override
  protected DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createDesktopPeer", Desktop.class);
      m.setAccessible(true);
      return (DesktopPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected ButtonPeer createButton(Button target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createButton", Button.class);
      m.setAccessible(true);
      return (ButtonPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected TextFieldPeer createTextField(TextField target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createTextField", TextField.class);
      m.setAccessible(true);
      return (TextFieldPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected LabelPeer createLabel(Label target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createLabel", Label.class);
      m.setAccessible(true);
      return (LabelPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected ListPeer createList(List target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createList", List.class);
      m.setAccessible(true);
      return (ListPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected CheckboxPeer createCheckbox(Checkbox target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createCheckbox", Checkbox.class);
      m.setAccessible(true);
      return (CheckboxPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected ScrollbarPeer createScrollbar(Scrollbar target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createScrollbar", Scrollbar.class);
      m.setAccessible(true);
      return (ScrollbarPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected ScrollPanePeer createScrollPane(ScrollPane target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createScrollPane", ScrollPane.class);
      m.setAccessible(true);
      return (ScrollPanePeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected TextAreaPeer createTextArea(TextArea target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createTextArea", TextArea.class);
      m.setAccessible(true);
      return (TextAreaPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected ChoicePeer createChoice(Choice target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createChoice", Choice.class);
      m.setAccessible(true);
      return (ChoicePeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected FramePeer createFrame(Frame target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createFrame", Frame.class);
      m.setAccessible(true);
      return (FramePeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected CanvasPeer createCanvas(Canvas target) {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createCanvas", Canvas.class);
      m.setAccessible(true);
      return (CanvasPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected PanelPeer createPanel(Panel target) {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createPanel", Panel.class);
      m.setAccessible(true);
      return (PanelPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected WindowPeer createWindow(Window target) throws HeadlessException {
    if (target instanceof NewtHiddenParent) {
      return new NewtPeer(((NewtHiddenParent) target).newtWindow);
    }

    try {
      Method m = Toolkit.class.getDeclaredMethod("createWindow", Window.class);
      m.setAccessible(true);
      return (WindowPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected DialogPeer createDialog(Dialog target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createDialog", Dialog.class);
      m.setAccessible(true);
      return (DialogPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createMenuBar", MenuBar.class);
      m.setAccessible(true);
      return (MenuBarPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected MenuPeer createMenu(Menu target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createMenu", Menu.class);
      m.setAccessible(true);
      return (MenuPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected PopupMenuPeer createPopupMenu(PopupMenu target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createPopupMenu", PopupMenu.class);
      m.setAccessible(true);
      return (PopupMenuPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected MenuItemPeer createMenuItem(MenuItem target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createMenuItem", MenuItem.class);
      m.setAccessible(true);
      return (MenuItemPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected FileDialogPeer createFileDialog(FileDialog target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createFileDialog", FileDialog.class);
      m.setAccessible(true);
      return (FileDialogPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target) throws HeadlessException {
    try {
      Method m = Toolkit.class.getDeclaredMethod("createCheckboxMenuItem", CheckboxMenuItem.class);
      m.setAccessible(true);
      return (CheckboxMenuItemPeer) m.invoke(delegate, target);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  @Deprecated
  protected FontPeer getFontPeer(String name, int style) {
    try {
      Method m = Toolkit.class.getDeclaredMethod("getFontPeer", String.class, int.class);
      m.setAccessible(true);
      return (FontPeer) m.invoke(delegate, name, style);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  protected EventQueue getSystemEventQueueImpl() {
    try {
      Method m = Toolkit.class.getDeclaredMethod("getSystemEventQueueImpl");
      m.setAccessible(true);
      return (EventQueue) m.invoke(delegate);
    } catch (Exception e) {
      throw new RuntimeException("Could not delegate to toolkit", e);
    }
  }

  @Override
  public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager arg) {
    return ((KeyboardFocusManagerPeerProvider) delegate).createKeyboardFocusManagerPeer(arg);
  }
}
