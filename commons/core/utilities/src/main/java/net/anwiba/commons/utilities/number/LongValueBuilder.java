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
package net.anwiba.commons.utilities.number;

import java.text.MessageFormat;

public class LongValueBuilder {
  private final StringBuilder valueStringBuilder = new StringBuilder();
  private boolean isNegativ = false;
  private final String originalString;

  public LongValueBuilder(final String originalString) {
    this.originalString = originalString;
  }

  public void add(final char c) {
    if (this.valueStringBuilder.length() < 17) {
      this.valueStringBuilder.append(c);
    }
  }

  public void setNegativ() {
    if (this.valueStringBuilder.length() == 0) {
      this.isNegativ = true;
    } else {
      throw new NumberFormatException(MessageFormat.format("Unable to parse string ''{0}'' as Double.", //$NON-NLS-1$
          this.originalString));
    }
  }

  public long build() {
    final String valueString = this.valueStringBuilder.toString();
    if (valueString.length() == 0) {
      return 0;
    }
    return Long.parseLong(valueString);
  }

  public int length() {
    return this.valueStringBuilder.length();
  }

  public boolean isNegativ() {
    return this.isNegativ;
  }
}