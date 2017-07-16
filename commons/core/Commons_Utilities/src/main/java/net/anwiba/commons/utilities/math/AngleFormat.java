/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
// Copyright (c) 2007 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.commons.utilities.math;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

public class AngleFormat extends Format {

  private static final long serialVersionUID = 1L;

  public final static AngleFormat getInstance() {
    return new AngleFormat();
  }

  @Override
  public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
    if (obj == null) {
      return toAppendTo;
    }
    StringBuffer buffer = new StringBuffer();
    buffer = getAngle(obj);
    if (toAppendTo == null) {
      return buffer;
    }
    if (pos == null) {
      return toAppendTo.append(buffer);
    }
    return toAppendTo.replace(pos.getBeginIndex(), pos.getEndIndex(), buffer.toString());
  }

  private StringBuffer getAngle(final Object obj) {
    final StringBuffer buffer = new StringBuffer();
    if (obj instanceof Angle) {
      final Angle angle = (Angle) obj;
      final double degree = angle.degree();
      toString(buffer, degree);
    }
    if (obj instanceof Double) {
      final double angle = ((Double) obj).doubleValue();
      toString(buffer, angle);
    }
    return buffer;
  }

  public void toString(final StringBuffer buffer, final double value) {
    final DecimalFormat sf = new DecimalFormat("0000"); //$NON-NLS-1$
    double angle = value;
    final String degree = String.format("%4d", (int) angle); //$NON-NLS-1$
    if (angle < 0d) {
      angle *= -1d;
    }
    angle = (angle - (int) angle) * 60d;
    final String minutes = String.format("%02d", (int) angle); //$NON-NLS-1$
    angle = (angle - (int) angle) * 60d;
    final String seconds = String.format("%02d", (int) angle); //$NON-NLS-1$
    angle = (angle - (int) angle) * 10000d;
    final String ms = sf.format((int) angle);
    buffer
        .append(degree)
        .append("°") //$NON-NLS-1$
        .append(minutes)
        .append("\'") //$NON-NLS-1$
        .append(seconds)
        .append("\"") //$NON-NLS-1$
        .append(".") //$NON-NLS-1$
        .append(ms);
  }

  public Double parse(final String text) throws ParseException {
    final ParsePosition pos = new ParsePosition(0);
    final Double result = parse(text, pos);
    if (pos.getIndex() == 0) {
      throw new ParseException("Unparseable string: \"" + text + "\"", pos.getErrorIndex()); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return result;
  }

  public Double parse(final String text, final ParsePosition pos) {
    if (text == null) {
      return null;
    }
    if (pos == null) {
      return null;
    }
    int index = pos.getIndex();
    if (index == text.length()) {
      return null;
    }
    String value = ""; //$NON-NLS-1$
    String degree = null;
    String minutes = null;
    String seconds = null;
    while (text.length() > index) {
      if (text.charAt(index) == ' ') {
        if (value.length() > 0) {
          if (degree == null) {
            if (value.equals("+") || value.equals("-")) { //$NON-NLS-1$ //$NON-NLS-2$
              value += "0"; //$NON-NLS-1$
            }
            degree = value;
            value = ""; //$NON-NLS-1$
          } else if (minutes == null) {
            minutes = value;
          } else {
            pos.setErrorIndex(index);
            return null;
          }
        }
      } else if (text.charAt(index) == '-' || text.charAt(index) == '+') {
        if (degree != null) {
          pos.setErrorIndex(index);
          return null;
        }
        value += text.charAt(index);
      } else if (text.charAt(index) >= '0' && '9' >= text.charAt(index)) {
        value += text.charAt(index);
      } else if (text.charAt(index) == '.' || text.charAt(index) == '°') {
        if (degree != null) {
          pos.setErrorIndex(index);
          return null;
        }
        if (value.length() > 0) {
          if (value.equals("+") || value.equals("-")) { //$NON-NLS-1$ //$NON-NLS-2$
            value += "0"; //$NON-NLS-1$
          }
          degree = value;
        } else {
          degree = "0"; //$NON-NLS-1$
        }
        value = ""; //$NON-NLS-1$
      } else if (text.charAt(index) == '\'') {
        if (minutes != null) {
          pos.setErrorIndex(index);
          return null;
        }
        if (value.length() > 2) {
          pos.setErrorIndex(index);
          return null;
        }
        if (degree == null) {
          degree = "0"; //$NON-NLS-1$
        }
        minutes = value;
        value = ""; //$NON-NLS-1$
      } else if (text.charAt(index) == '\"') {
        if (seconds != null) {
          pos.setErrorIndex(index);
          return null;
        }
        if (value.length() > 2) {
          pos.setErrorIndex(index);
          return null;
        }
        if (degree == null) {
          degree = "0"; //$NON-NLS-1$
        }
        if (minutes == null) {
          minutes = "0"; //$NON-NLS-1$
        }
        seconds = value;
        value = ""; //$NON-NLS-1$
      } else {
        pos.setErrorIndex(index);
        return null;
      }
      index++;
    }
    if (value.length() > 0) {
      if (degree == null) {
        if (value.equals("+") || value.equals("-")) { //$NON-NLS-1$ //$NON-NLS-2$
          value += "0"; //$NON-NLS-1$
        }
        degree = value;
      } else if (minutes == null) {
        minutes = value;
      } else {
        pos.setErrorIndex(index);
        return null;
      }
    }
    double angle = 0d;
    if (seconds != null) {
      angle = Double.parseDouble(seconds);
      angle = angle / 60d;
    }
    if (minutes != null) {
      angle += Double.parseDouble(minutes);
    }
    angle = angle / 60d;
    if (degree != null) {
      angle += Double.parseDouble(degree);
    }
    angle = angle % 360d;
    pos.setIndex(index);
    return new Double(angle);
  }

  @Override
  public Object parseObject(final String source, final ParsePosition pos) {
    return parse(source, pos);
  }
}
