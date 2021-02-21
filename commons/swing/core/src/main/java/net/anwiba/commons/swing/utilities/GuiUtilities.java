/*
 * #%L
 * anwiba commons swing
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.swing.utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JPanel;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

public class GuiUtilities extends ContainerUtilities {

  private static ILogger logger = Logging.getLogger(GuiUtilities.class.getName());

  public static void center(final Window window) {
    if (window == null) {
      return;
    }
    final Window owner = window.getOwner();
    if (owner == null || owner.getBounds().equals(new Rectangle(0, 0, 0, 0))) {
      centerToScreen(window);
      return;
    }
    final int x = owner.getX() + owner.getWidth() / 2 - window.getWidth() / 2;
    final int y = owner.getY() + owner.getHeight() / 2 - window.getHeight() / 2;
    window.setLocation(x < 0 ? 0 : x, y < 0 ? 0 : y);
  }

  public static void centerToScreen(final Window window) {
    if (window == null) {
      return;
    }
    final DisplayMode displayMode = window.getGraphicsConfiguration().getDevice().getDisplayMode();
    final Point location = getCenterToScreenLocation(displayMode, window.getSize());
    window.setLocation(location);
    return;
  }

  public static Point getCenterToScreenLocation(final DisplayMode displayMode, final Dimension size) {
    if (displayMode == null) {
      return new Point();
    }
    final int x = displayMode.getWidth() / 2 - size.width / 2;
    final int y = displayMode.getHeight() / 2 - size.height / 2;
    return new Point(x, y);
  }

  public static void invokeLater(final Runnable runner) {
    if (EventQueue.isDispatchThread()) {
      runner.run();
      return;
    }
    EventQueue.invokeLater(runner);
  }

  public static void invokeAndWait(final Runnable runner) {
    if (EventQueue.isDispatchThread()) {
      runner.run();
      return;
    }
    try {
      EventQueue.invokeAndWait(runner);
    } catch (final InterruptedException exception) {
      // nothing to do
    } catch (final InvocationTargetException exception) {
      final Throwable cause = exception.getCause();
      logger.log(ILevel.DEBUG, cause.getLocalizedMessage(), cause);
      throw new RuntimeException(cause);
    }
  }

  public static Component createGap(final int width, final boolean isElastic) {
    final JPanel panel = new JPanel();
    panel.setMinimumSize(new Dimension(width, 0));
    if (!isElastic) {
      panel.setMaximumSize(new Dimension(width, 1000));
    }
    return panel;
  }

  public static Set<Container> setContainerEnabled(final Container control, final boolean enabled) {
    return setContainerEnabled(control, enabled, new HashSet<>());
  }

  /**
   * Enabled / Disabled einen Container mit allen Unterkomponenten
   *
   * @param container  der Container
   * @param enable     true, wenn der Container enabled werden soll
   * @param components die Komponenten, die nicht enabled / disabled werden sollen
   * @return die Komponenten, die vorher schon enabled / disabled waren
   */
  public static Set<Container> setContainerEnabled(
      final Container container,
      final boolean enable,
      final Set<Container> components) {
    final HashSet<Container> enabledComps = new HashSet<Container>();

    if (!components.contains(container)) {
      if (container.isEnabled() == enable) {
        enabledComps.add(container);
      } else {
        if (container instanceof AbstractButton) {
          AbstractButton b = (AbstractButton) container;
          Action action = b.getAction();
          if (action == null) {
            b.setEnabled(enable);
          } else {
            // button can't be enabled if the action is disabled
            b.setEnabled(action.isEnabled() && enable);
          }
        } else {
          container.setEnabled(enable);
        }
      }
    }
    return setSubComponentsEnabled(container, enable, components, enabledComps);
  }

  private static Set<Container> setSubComponentsEnabled(
      final Container container,
      final boolean enable,
      final Set<Container> components,
      final Set<Container> enabledComps) {
    final Component[] containerComponents = container.getComponents();
    for (Component component : containerComponents) {
      if (component instanceof Container) {
        Set<Container> enabledContainers = setContainerEnabled((Container) component,
            enable,
            components);
        enabledComps.addAll(enabledContainers);
      } else {
        if (component.isEnabled() == enable) {
          enabledComps.add(container);
        } else {
          component.setEnabled(enable);
        }
      }
    }
    return enabledComps;
  }
}
