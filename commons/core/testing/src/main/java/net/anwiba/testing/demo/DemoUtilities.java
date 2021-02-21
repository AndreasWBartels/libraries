/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.testing.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.junit.jupiter.api.Assertions;

public class DemoUtilities {

  public static final long DELAY_TIME = 500;
  public static final Duration DEFAULT_TIMEOUT_MILLIS = Duration.ofMillis(30000);

  public static final Font DEFAULT_FIXED_WIDTH_FONT = new Font("Monospaced", Font.PLAIN, 11);
  public static final Font DEFAULT_PROPORTIONAL_FONT = new Font("Dialog", Font.PLAIN, 11);

  private static class WindowTestCase {

    private boolean wasOpened = false;
    private final List<Window> windows = new ArrayList<Window>();

    public void removeListensers(final Window window) {
      WindowListener[] windowListeners = window.getWindowListeners();
      for (WindowListener windowListener : windowListeners) {
        if (windowListener instanceof TestCaseWindowListener) {
          window.removeWindowListener(windowListener);
        }
      }
    }

    public void setOpen(final Window window) {
      this.wasOpened = true;
    }

    public void dispose() {
      this.windows.stream()
          .filter(w -> w.isDisplayable())
          .collect(Collectors.toCollection(ArrayDeque::new))
          .descendingIterator()
          .forEachRemaining(w -> {
            if (w.isVisible()) {
              w.setVisible(false);
            }
            w.dispose();
          });
    }

    public void add(final Window window) {
      this.windows.add(window);
    }

    public boolean wasOpened() {
      return this.wasOpened;
    }

  }

  private static class TestCaseWindowListener extends WindowAdapter {

    private final WindowTestCase testCase;

    public TestCaseWindowListener(final WindowTestCase testCase) {
      this.testCase = testCase;
    }

    @Override
    public void windowClosing(final WindowEvent e) {
      // received when user requests close() on the window
      this.testCase.removeListensers((Window) e.getComponent());
    }

    @Override
    public void windowOpened(final WindowEvent e) {
      this.testCase.setOpen((Window) e.getComponent());
    }

    @Override
    public void windowClosed(final WindowEvent e) {
      // received when window.dispose(); called
      this.testCase.removeListensers((Window) e.getComponent());
    }
  }

  private DemoUtilities() {
    // not instantiable
  }

  public static JDialog createDialog(final Component parentComponent) {
    if (parentComponent == null) {
      return new JDialog();
    }
    if (parentComponent instanceof Dialog) {
      return new JDialog((Dialog) parentComponent);

    }
    if (parentComponent instanceof Frame) {
      return new JDialog((Frame) parentComponent);
    }
    return createDialog(parentComponent.getParent());
  }

  public static JWindow createWindow(final Component parentComponent) {
    if (parentComponent == null) {
      return new JWindow();
    }
    if (parentComponent instanceof Dialog) {
      return new JWindow((Dialog) parentComponent);

    }
    if (parentComponent instanceof Frame) {
      return new JWindow((Frame) parentComponent);
    }
    return createWindow(parentComponent.getParent());
  }

  public static void setNativeLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.out.println("Error setting native LAF: " + e); //$NON-NLS-1$
    }
  }

  public static void expandTreeNode(final JTree tree, final TreeNode tNode) {
    TreePath tp = new TreePath(((DefaultMutableTreeNode) tNode).getPath());
    tree.expandPath(tp);
    for (int i = 0; i < tNode.getChildCount(); i++) {
      expandTreeNode(tree, tNode.getChildAt(i));
    }
  }

  public static void centerOnScreen(final Window window) {
    try {
      Dimension screenSize = window.getToolkit().getScreenSize();
      Dimension windowSize = window.getSize();
      int x = (screenSize.width - windowSize.width) / 2;
      int y = (screenSize.height - windowSize.height) / 2;
      window.setLocation(x, y);
    } catch (Exception e) {
      // Unable to get location (IllegalComponentStateException) or unable to set it
      // => ignore
    }
  }

  public static Window getWindowForComponent(final EventObject event) {
    Object object = event.getSource();
    if (object instanceof Component) {
      return getWindowForComponent((Component) object);
    }
    return JOptionPane.getRootFrame();
  }

  public final static Window getWindowForComponent(final Component component) {
    if (component == null) {
      return JOptionPane.getRootFrame();
    }
    if (component instanceof JPopupMenu) {
      return getWindowForComponent(((JPopupMenu) component).getInvoker());
    }
    if (component instanceof Window) {
      return (Window) component;
    }
    return getWindowForComponent(component.getParent());
  }

  public static <T extends Window> void showAndDispose(final Supplier<T> windowSupplier,
      final Consumer<T> afterOpenedTask) {
    WindowTestCase testCase = new WindowTestCase();
    Object[] holder = new Object[] { null };
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        T window = windowSupplier.get();
        holder[0] = window;
        assertNotNull(window, "The window to show must not be null.");
        if (window instanceof Dialog) {
          final Dialog dialog = (Dialog) window;
          dialog.setModal(false);
          Optional.ofNullable(window.getParent())
              .filter(c -> c instanceof Window)
              .map(c -> (Window) c)
              .ifPresent(w -> registerWindow(testCase, w));
        }
        registerWindow(testCase, window);
        window.setVisible(true);
        window.toFront();
      }
    };
    runOnEventDispatchThread(runnable);
    afterOpenedTask.accept((T) holder[0]);
    runOnEventDispatchThread(() -> testCase.dispose());
    if (testCase.wasOpened()) {
      return;
    }
    Assertions.fail("gui wasn't opened");
  }

  private static void registerWindow(final WindowTestCase testCase, final Window window) {
    testCase.add(window);
    window.addWindowListener(new TestCaseWindowListener(testCase));
  }

  private static void runOnEventDispatchThread(final Runnable runnable) {
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      try {
        SwingUtilities.invokeAndWait(runnable);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e.getCause());
      }
    }
  }

  public static JFrame createJFrame() {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    return frame;
  }

  public static void pause() {
    try {
      Thread.sleep(DemoUtilities.DELAY_TIME);
    } catch (InterruptedException exception) {
      Thread.currentThread().isInterrupted();
    }
  }
}
