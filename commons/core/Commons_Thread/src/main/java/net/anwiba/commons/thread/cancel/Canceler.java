/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.thread.cancel;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.logging.ILevel;

public class Canceler implements ICanceler {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(Canceler.class);
  private static final long serialVersionUID = -1L;
  private boolean isCanceled;
  private final boolean isEnabled;

  public Canceler(final boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  @Override
  public void cancel() {
    synchronized (this) {
      if (isCanceled()) {
        return;
      }
      this.isCanceled = true;
      if (!isCanceled()) {
        return;
      }
    }
    fireCanceled();
  }

  @Override
  public boolean isCanceled() {
    synchronized (this) {
      return this.isCanceled && this.isEnabled;
    }
  }

  @Override
  public boolean isEnabled() {
    synchronized (this) {
      return this.isEnabled;
    }
  }

  @Override
  public void check() throws InterruptedException {
    if (isCanceled()) {
      throw new InterruptedException();
    }
  }

  private final List<ICancelerListener> listeners = new ArrayList<>();

  @Override
  public void addCancelerListener(final ICancelerListener listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
    }
  }

  @Override
  public void removeCancelerListener(final ICancelerListener listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
    }
  }

  protected final void fireCanceled() {
    final List<ICancelerListener> currentListeners;
    synchronized (this.listeners) {
      currentListeners = new ArrayList<>(this.listeners);
    }
    for (final ICancelerListener listener : currentListeners) {
      try {
        listener.canceled();
      } catch (final IllegalStateException exception) {
        logger.log(ILevel.DEBUG, exception.getMessage());
        logger.log(ILevel.ALL, exception.getMessage(), exception);
      }
    }
  }

}
