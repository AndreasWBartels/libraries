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

import java.io.Serializable;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.IObserver;
import net.anwiba.commons.lang.functional.IObserverFactory;

public interface ICanceler extends Serializable {

  public ICanceler DummyCanceler = new ICanceler() {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public boolean isCanceled() {
      return false;
    }

    @Override
    public void cancel() {
      // nothing to do
    }

    @Override
    public void check() throws CanceledException {
      // nothing to do
    }

    @Override
    public void addCancelerListener(final ICancelerListener listener) {
      // nothing to do
    }

    @Override
    public void removeCancelerListener(final ICancelerListener listener) {
      // nothing to do
    }

    @Override
    public void removeAllCancelerListener() {
      // nothing to do
    }
  };
  
  public static ICanceler dummy() {
    return DummyCanceler;
  }

  public void cancel();

  public boolean isCanceled();

  public boolean isEnabled();

  public void check() throws CanceledException;

  void addCancelerListener(ICancelerListener listener);

  void removeCancelerListener(ICancelerListener listener);
  
  default IObserver observer(Runnable runnable) {
    final ICancelerListener listener = new ICancelerListener() {
      @Override
      public void canceled() {
        removeCancelerListener(this);
        runnable.run();
      }
    };
    addCancelerListener(listener);
    return new IObserver() {

      @Override
      public void close() throws RuntimeException {
        removeCancelerListener(listener);
      }
    };
  }

  default IObserverFactory observerFactory() {
    return runnable -> ICanceler.this.observer(runnable);
  }

  void removeAllCancelerListener();

}
