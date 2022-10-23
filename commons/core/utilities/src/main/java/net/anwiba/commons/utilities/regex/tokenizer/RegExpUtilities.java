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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpUtilities {

  private record Replacement(int start, int end, String value) {
  }

  public static String replace(Pattern pattern, String value, Collection<String> names) {
    int length = value.length();
    Replacement replacement = new Replacement(length, length, value);
    do {
      replacement = replace(pattern, replacement.value(), replacement.start(), names);
    } 
    while (replacement.start() > 0);
    return replacement.value();
  }

  private static Replacement replace(Pattern pattern, String value, int until, Collection<String> names) {
    Matcher matcher = pattern.matcher(value.substring(0, until));
    if (!matcher.find() || matcher.groupCount() == 0) {
      return new Replacement(-1, -1, value);
    }
    List<Replacement> replacements = new LinkedList<>();
    for (String name : names) {
      int start = matcher.start(name);
      int end = matcher.end(name);
      if (start > -1) {
        replacements.add(new Replacement(start, end, name));
      }
    }
    if (replacements.isEmpty()) {
      return new Replacement(-1, -1, value);
    }
    Collections.sort(replacements, Comparator.comparing(object -> Integer.valueOf(object.start())));
    StringBuilder builder = new StringBuilder();
    int pos = 0;
    for (Replacement replacement : replacements) {
      if (replacement.start() > pos) {
        builder.append(value.substring(pos, replacement.start()));
      }
      builder.append("${");
      builder.append(replacement.value());
      builder.append("}");
      pos = replacement.end();
    }
    if (pos < value.length()) {
      builder.append(value.substring(pos));
    }
    String string = builder.toString();
    return new Replacement(replacements.iterator().next().start(), string.length(), string);
  }

  public static String replace(
      Pattern pattern,
      String group,
      String value,
      IntFunction<String> valueFactory) {
    if (value == null) {
      return null;
    }
    int length = value.length();
    Replacement replacement = new Replacement(length, length, value);
    do {
      replacement = replace(pattern, group, replacement.value(), replacement.start(), valueFactory);
    }
    while (replacement.start() > 0);
    return replacement.value();
  }

  private static Replacement replace(
      Pattern pattern,
      String group,
      String value,
      int until,
      IntFunction<String> valueFactory) {
    Matcher matcher = pattern.matcher(value.substring(0, until));
    int start = 0;
    if (!matcher.find()
        || matcher.groupCount() == 0
        || (start = matcher.start(group)) < 0) {
      return new Replacement(-1, -1, value);
    }
    StringBuilder builder = new StringBuilder();
    if (start > 0) {
      builder.append(value.substring(0, start));
    }
    int end = matcher.end(group);
    builder.append(valueFactory.apply(end - start));
    if (end < value.length()) {
      builder.append(value.substring(end));
    }
    String string = builder.toString();
    return new Replacement(start, string.length(), string);
  }

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