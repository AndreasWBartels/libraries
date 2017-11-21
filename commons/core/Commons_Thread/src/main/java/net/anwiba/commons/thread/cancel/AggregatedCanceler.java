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

public class AggregatedCanceler extends Canceler implements IAggregatedCanceler {

  private static final long serialVersionUID = 1L;
  private final List<ICanceler> cancelers = new ArrayList<>();

  public AggregatedCanceler(final boolean isEnabled) {
    super(isEnabled);
  }

  @Override
  public synchronized void add(final ICanceler canceler) {
    if (canceler == this) {
      throw new IllegalArgumentException();
    }
    this.cancelers.add(canceler);
    canceler.addCancelerListener(new ICancelerListener() {

      @Override
      public void canceled() {
        cancel();
      }
    });
  }

  @Override
  public synchronized void cancel() {
    for (final ICanceler canceler : this.cancelers) {
      canceler.cancel();
    }
    super.cancel();
  }
}