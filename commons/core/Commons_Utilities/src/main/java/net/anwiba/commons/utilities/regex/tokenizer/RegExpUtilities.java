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
package net.anwiba.commons.utilities.regex.tokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpUtilities {

  public static String[] getGroups(final Matcher matcher) {
    final String[] groups = new String[matcher.groupCount() + 1];
    for (int i = 0; i < groups.length; ++i) {
      groups[i] = matcher.group(i);
    }
    return groups;
  }

  public static boolean like(final String valueString, final String likeString, final char escapeCharacter) {
    final Pattern pattern = createLikePattern(likeString, escapeCharacter);
    final Matcher matcher = pattern.matcher(valueString);
    return matcher.matches();
  }

  public static Pattern createLikePattern(final String likeString, final char escapeCharacter) {
    final String regExp = createRegularExpressionString(likeString, escapeCharacter);
    final Pattern pattern = Pattern.compile(regExp);
    return pattern;
  }

  public static String createRegularExpressionString(final String likeString, final char escapeCharacter) {
    final StringBuilder builder = new StringBuilder();
    boolean isEscaped = false;
    for (final char c : likeString.toCharArray()) {
      if (c == escapeCharacter) {
        if (!isEscaped) {
          isEscaped = true;
          continue;
        }
      }
      if (isEscaped) {
        switch (c) {
          case '%': {
            builder.append('%');
            continue;
          }
          case '_': {
            builder.append('_');
            isEscaped = false;
            continue;
          }
        }
      }
      isEscaped = false;
      switch (c) {
        case '\\': {
          builder.append("\\\\"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '[': {
          builder.append("\\["); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case ']': {
          builder.append("\\]"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '{': {
          builder.append("\\{"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '}': {
          builder.append("\\}"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '(': {
          builder.append("\\("); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case ')': {
          builder.append("\\)"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '.': {
          builder.append("\\."); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '^': {
          builder.append("\\^"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '$': {
          builder.append("\\$"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '?': {
          builder.append("\\?"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '*': {
          builder.append("\\*"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '+': {
          builder.append("\\+"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '|': {
          builder.append("\\|"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '-': {
          builder.append("\\-"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '&': {
          builder.append("\\&"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '%': {
          builder.append("(.)*"); //$NON-NLS-1$
          isEscaped = false;
          continue;
        }
        case '_': {
          builder.append('.');
          continue;
        }
        default: {
          builder.append(c);
          continue;
        }
      }
    }
    return builder.toString();
  }
}