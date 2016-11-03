/*
 * #%L
 * anwiba commons core
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

package net.anwiba.commons.reflection.utilities;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.reflection.IInjektionAnalyserResult;

public final class ListValueHolder implements IValueHolder {

  private final List<Object> values = new ArrayList<>();

  public ListValueHolder() {
  }

  public void add(final Object value) {
    this.values.add(value);
  }

  public List<Object> getValue() {
    return new ArrayList<>(this.values);
  }

  public void remove(final IInjektionAnalyserResult result) {
    this.values.remove(result);
  }

  public boolean isEmty() {
    return this.values.isEmpty();
  }
} 
