/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.definition.schema.json.gramma.element;

import java.text.MessageFormat;

public class JDimension {

  public static final JDimension INFINITY = new JDimension(Integer.MAX_VALUE);
  private static final JDimension DEFAULT = INFINITY;
  private final int value;

  private JDimension(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    return prime * 1 + this.value;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof JDimension)) {
      return false;
    }
    final JDimension other = (JDimension) obj;
    return this.value == other.value;
  }

  public static JDimension valueOf(final String value) {
    if (value == null) {
      return DEFAULT;
    }
    final StringBuilder builder = new StringBuilder();
    boolean isBracketOpen = false;
    boolean isFirstValueStarted = false;
    boolean isFirstValueFinished = false;
    final String text = value.trim();
    for (int index = 0; index < text.length(); index++) {
      switch (text.charAt(index)) {
        case '*': {
          if (index == 0) {
            return new JDimension(Integer.MAX_VALUE);
          }
          throw createException(text, index);
        }
        case '[': {
          if (!isBracketOpen) {
            isBracketOpen = true;
            continue;
          }
          throw createException(text, index);
        }
        case ']': {
          if (!isBracketOpen) {
            throw createException(text, index);
          }
          if (!isFirstValueStarted) {
            return new JDimension(Integer.MAX_VALUE);
          }
          if (isFirstValueStarted) {
            return new JDimension(Integer.valueOf(builder.toString()).intValue());
          }
          throw createException(text, index);
        }
        case '\r':
        case '\t':
        case '\n':
        case ' ': {
          if (isFirstValueStarted) {
            isFirstValueFinished = true;
          }
          continue;
        }
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9': {
          if (!isBracketOpen) {
            throw createException(text, index);
          }
          if (isFirstValueFinished) {
            throw createException(text, index);
          }
          isFirstValueStarted = true;
          builder.append(text.charAt(index));
          continue;
        }
        default:
          throw createException(text, index);
      }
    }
    throw new RuntimeException("Unreachable code reached"); //$NON-NLS-1$
  }

  @Override
  public String toString() {
    return this.value == Integer.MAX_VALUE ? "INFINITY" : String.valueOf(this.value); //$NON-NLS-1$
  }

  public static JDimension valueOf(final int value) {
    return new JDimension(value);
  }

  public static IllegalArgumentException createException(final String text, final int index) {
    return new IllegalArgumentException(MessageFormat.format("value ''{0}'' at index {1}", //$NON-NLS-1$
        text,
        Integer.toString(index)));
  }
}
