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
package net.anwiba.commons.utilities.string;

import net.anwiba.commons.ensure.Ensure;

public class StringAppender implements IStringAppender {

  private final String separator;
  private final StringBuffer stringBuffer = new StringBuffer();
  private boolean appended = false;

  public StringAppender() {
    this(""); //$NON-NLS-1$
  }

  public StringAppender(final String separator) {
    Ensure.ensureArgumentNotNull(separator);
    this.separator = separator;
  }

  @Override
  public String toString() {
    return this.stringBuffer.toString();
  }

  @Override
  public void append(final int value) {
    append(String.valueOf(value));
  }

  @Override
  public void append(final Iterable<String> values) {
    for (final String value : values) {
      append(value);
    }
  }

  @Override
  public void append(final String[] values) {
    for (final String value : values) {
      append(value);
    }
  }

  @Override
  public void append(final String value) {
    if (this.appended) {
      this.stringBuffer.append(this.separator);
    } else {
      this.appended = true;
    }
    this.stringBuffer.append(value);
  }

  @Override
  public boolean isEmpty() {
    return this.stringBuffer.length() == 0;
  }
}
