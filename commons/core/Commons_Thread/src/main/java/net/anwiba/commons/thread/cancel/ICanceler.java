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

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.IWatcher;

public interface ICanceler extends Serializable {

  public ICanceler DummyCancler = new ICanceler() {

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
    public void check() throws InterruptedException {
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
  };

  public void cancel();

  public boolean isCanceled();

  public boolean isEnabled();

  public void check() throws InterruptedException;

  void addCancelerListener(ICancelerListener listner);

  void removeCancelerListener(ICancelerListener listner);

  default <T, E extends Exception> IFactory<IBlock<RuntimeException>, IWatcher, RuntimeException> watcherFactory() {
    return closure -> {
      final ICancelerListener listener = () -> closure.execute();
      addCancelerListener(listener);
      return new IWatcher() {

        @Override
        public void check() throws InterruptedException {
          ICanceler.this.check();
        }

        @Override
        public void close() throws RuntimeException {
          removeCancelerListener(listener);
        }
      };
    };
  }

}
