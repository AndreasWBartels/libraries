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
package net.anwiba.commons.swing.preference;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class WindowPrefereneceUpdatingListener extends WindowAdapter implements ComponentListener {
  private final Window window;
  private final IWindowPreferences preferences;
  private boolean isVisible = false;
  private Rectangle rectangle = new Rectangle();

  public WindowPrefereneceUpdatingListener(final Window window, final IWindowPreferences windowPreferences) {
    this.window = window;
    this.preferences = windowPreferences;
  }

  @Override
  public synchronized void componentResized(final ComponentEvent e) {
    update();
  }

  @Override
  public synchronized void componentMoved(final ComponentEvent e) {
    update();
  }

  private void update() {
    if (!this.isVisible || this.window.getBounds().equals(this.rectangle)) {
      return;
    }
    if (this.window instanceof Frame && ((Frame) this.window).getState() != Frame.NORMAL) {
      return;
    }
    this.rectangle = this.window.getBounds();
    this.preferences.setBounds(this.rectangle);
  }

  @Override
  public synchronized void componentShown(final ComponentEvent e) {
    this.rectangle = this.window.getBounds();
    this.isVisible = true;
  }

  @Override
  public synchronized void componentHidden(final ComponentEvent e) {
    this.isVisible = false;
  }

  @Override
  public void windowClosed(final WindowEvent e) {
    this.window.removeComponentListener(this);
    this.window.removeWindowListener(this);
  }
}