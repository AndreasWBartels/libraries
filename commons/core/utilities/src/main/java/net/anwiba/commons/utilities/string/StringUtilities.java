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

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

import net.anwiba.commons.lang.object.ObjectUtilities;

public class StringUtilities {

  public static boolean isNumericValue(final String text) {
    if (isNullOrTrimmedEmpty(text)) {
      return false;
    }
    final Locale local = Locale.getDefault();
    if (isNumericValue(text, local)) {
      return true;
    }
    return isNumericValue(text, Locale.US);
  }

  public static boolean isNumericValue(final String text, final Locale local) {
    final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(local);
    final char groupingSeparator = decimalFormatSymbols.getGroupingSeparator();
    final char decimalSeparator = decimalFormatSymbols.getDecimalSeparator();
    final String ungrouped = "[+-]?[0-9]+[" + decimalSeparator + "]?[0-9]*([eE][-+]?[0-9]+)?"; //$NON-NLS-1$//$NON-NLS-2$
    final String grouped = "[+-]?[0-9]{1,3}+(" + groupingSeparator + "[0-9]{3}+)*[" + decimalSeparator + "]?[0-9]*"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    final String patternString = ungrouped + "|" + grouped; //$NON-NLS-1$
    final Pattern p = Pattern.compile(patternString);
    final Matcher m = p.matcher(text.trim());
    if (m.matches()) {
      return true;
    }
    return false;
  }

  public static boolean isNullOrEmpty(final String text) {
    return text == null || text.isEmpty();
  }

  public static boolean isNullOrTrimmedEmpty(final String text) {
    return text == null || text.trim().isEmpty();
  }

  public static boolean trimedEquals(final String org, final String other) {
    return ObjectUtilities.equals(getTrimed(org), getTrimed(other));
  }

  private static String getTrimed(final String string) {
    if (string == null) {
      return null;
    }
    return string.trim();
  }

  public static boolean trimedAndUpperCaseEquals(final String org, final String other) {
    return ObjectUtilities.equals(getTrimedAndUpperCase(org), getTrimedAndUpperCase(other));
  }

  private static String getTrimedAndUpperCase(final String string) {
    if (string == null) {
      return null;
    }
    return string.trim().toUpperCase();
  }

  public static String repeatString(final String string, final int length) {
    if (string == null) {
      return null;
    }
    final StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      builder.append(string);
    }
    return builder.toString();
  }

  public static String createUniqueName(final String prefix, final String[] names) {
    return createUniqueName(prefix, new HashSet<>(Arrays.asList(names)));
  }

  public static String createUniqueName(final String string, final Set<String> names) {
    int counter = -1;
    boolean flag = true;
    for (final String name : names) {
      if (name.toUpperCase().matches(convertToRegEx(string.toUpperCase()) + "\\d*")) { //$NON-NLS-1$
        if (name.toUpperCase().matches(string.toUpperCase())) {
          counter = Math.max(0, counter);
          flag = false;
          continue;
        }
        counter = Math.max(counter, Integer.valueOf(name.substring(string.length())).intValue());
      }
    }
    return string + (flag || counter == -1
        ? "" //$NON-NLS-1$
        : String.valueOf(counter + 1));
  }

  public static String createConstantsName(final String name) {
    return convertToVariable(removeWhiteSpaces(removeAllControlCharacter(name))).toUpperCase();
  }

  private static String convertToVariable(final String string) {
    final StringBuilder builder = new StringBuilder();
    final char[] charArray = string.toCharArray();
    if (!(Character.isUpperCase(charArray[0]) || Character.isLowerCase(charArray[0]))) {
      builder.append("C"); //$NON-NLS-1$
    }
    for (final char c : charArray) {
      switch (c) {
        case '.':
        case '*':
        case '+':
        case '-':
        case '?':
        case '\\':
        case '[':
        case ']':
        case '{':
        case '}':
        case '(':
        case ')': {
          builder.append('_');
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

  private static String convertToRegEx(final String string) {
    final StringBuilder builder = new StringBuilder();
    for (final char c : string.toCharArray()) {
      switch (c) {
        case '.':
        case '*':
        case '+':
        case '?':
        case '\\':
        case '[':
        case ']':
        case '{':
        case '}':
        case '(':
        case ')': {
          builder.append('\\');
          builder.append(c);
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

  public static String removeEqualEnd(final String path, final String descriptionFile) {
    return net.anwiba.commons.reference.utilities.StringUtilities.removeEqualEnd(path, descriptionFile);
  }

  public static String getStringAfterLastChar(final String s, final char separatorChar) {
    return net.anwiba.commons.reference.utilities.StringUtilities.getStringAfterLastChar(s, separatorChar);
  }

  public static String getStringBeforLastChar(final String s, final char separatorChar) {
    return net.anwiba.commons.reference.utilities.StringUtilities.getStringBeforLastChar(s, separatorChar);
  }

  public static boolean containsIgnoreCase(final String value, final String... strings) {
    for (final String string : strings) {
      if (StringUtilities.equalsIgnoreCase(value, string)) {
        return true;
      }
    }
    return false;
  }

  public static boolean contains(final String value, final String... strings) {
    for (final String string : strings) {
      if (Objects.equals(value, string)) {
        return true;
      }
    }
    return false;
  }

  public static boolean equalsIgnoreCase(final String value, final String other) {
    return value == null
        ? other == null
        : value.equalsIgnoreCase(other);
  }

  public static List<IStringPart> getStringPositions(final String text, final String condition) {
    final ArrayList<IStringPart> positions = new ArrayList<>();
    if (isNullOrEmpty(condition) || isNullOrEmpty(text)) {
      return positions;
    }
    int position = 0;
    for (int i = 0; i < text.length(); i++) {
      final int index = i - position;
      if (text.charAt(i) == condition.charAt(index)) {
        if (index + 1 == condition.length()) {
          positions.add(new StringPart(position, condition.length()));
          position = i + 1;
        }
        continue;
      }
      position = i + 1;
    }
    return positions;
  }

  public static String removeWhiteSpaces(final String string) {
    final StringBuilder builder = new StringBuilder();
    boolean isEscaped = false;
    boolean isLiteral = false;
    boolean isWidthespace = false;
    for (final char c : string.toCharArray()) {
      if (isEscaped) {
        builder.append(c);
        isEscaped = false;
        isWidthespace = false;
        continue;
      }
      if (isLiteral) {
        builder.append(c);
        isLiteral = c != '"';
        continue;
      }
      switch (c) {
        case '"': {
          builder.append(c);
          isWidthespace = false;
          isLiteral = !isLiteral;
          break;
        }
        case '\\': {
          builder.append(c);
          isEscaped = true;
          isWidthespace = false;
          break;
        }
        case ' ':
        case '\t':
        case '\n':
        case '\f':
        case '\r': {
          if (isWidthespace) {
            break;
          }
          builder.append(' ');
          isWidthespace = true;
          break;
        }
        case '\0':
        case '\b': {
          break;
        }
        default: {
          builder.append(c);
        }
      }
    }
    return builder.toString();
  }

  public static String removeAllControlCharacter(final String string) {
    final StringBuilder builder = new StringBuilder();
    for (final char c : string.toCharArray()) {
      switch (c) {
        case '\0':
        case '\b':
        case '\t':
        case '\n':
        case '\f':
        case '\r': {
          break;
        }
        default: {
          builder.append(c);
        }
      }
    }
    return builder.toString();
  }

  public static String getEqualPrefix(final String string, final String other) {
    if (string == null || other == null) {
      return ""; //$NON-NLS-1$
    }
    final int length = Math.min(string.length(), other.length());
    int i = 0;
    for (i = 0; i < length; i++) {
      if (string.charAt(i) != other.charAt(i)) {
        break;
      }
    }
    if (i == 0) {
      return ""; //$NON-NLS-1$
    }
    return string.substring(0, i);
  }

  public static String[] tokens(final String string, final char seperator) {
    return tokens(string, String.valueOf(seperator));
  }

  public static String[] tokens(final String string, final String seperator) {
    if (string == null) {
      return new String[0];
    }
    final StringTokenizer tokenizer = new StringTokenizer(string, seperator);
    final List<String> tokens = new ArrayList<>();
    while (tokenizer.hasMoreTokens()) {
      tokens.add(tokenizer.nextToken());
    }
    return tokens.toArray(new String[tokens.size()]);
  }

  public static String[] trimedTokens(final String string, final char seperator) {
    return trimedTokens(string, String.valueOf(seperator));
  }

  public static String[] trimedTokens(final String string, final String seperator) {
    if (string == null) {
      return new String[0];
    }
    final StringTokenizer tokenizer = new StringTokenizer(string, seperator);
    final List<String> tokens = new ArrayList<>();
    while (tokenizer.hasMoreTokens()) {
      final String token = tokenizer.nextToken().trim();
      if (token.isEmpty()) {
        continue;
      }
      tokens.add(token);
    }
    return tokens.toArray(new String[tokens.size()]);
  }

  public static String setFirstTrimedCharacterToUpperCase(final String string) {
    return Optional
        .ofNullable(string)
        .map(s -> s.trim())
        .filter(s -> !s.isEmpty())
        .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1, s.length()))
        .orElse(null);
  }

  public static String concatenatedString(final String separator, final Collection<String> values) {
    return concat(separator, values.toArray(new String[values.size()]));
  }

  public static String concat(final String separator, final String[] values) {
    final IStringAppender builder = new StringAppender(separator);
    builder.append(values);
    return builder.toString();
  }

  public static String concat(final String separator, final Iterable<String> values) {
    final IStringAppender builder = new StringAppender(separator);
    builder.append(values);
    return builder.toString();
  }

  public static String concat(final String separator, final double[] values) {
    final IStringAppender builder = new StringAppender(separator);
    for (double value : values) {
      builder.append(String.valueOf(value));
    }
    return builder.toString();
  }

  public static String concat(final String separator, final int[] values) {
    final IStringAppender builder = new StringAppender(separator);
    for (double value : values) {
      builder.append(String.valueOf(value));
    }
    return builder.toString();
  }

  public static String concat(final String separator, final long[] values) {
    final IStringAppender builder = new StringAppender(separator);
    for (double value : values) {
      builder.append(String.valueOf(value));
    }
    return builder.toString();
  }

  public static int numberOfMatches(final String string, final String pattern) {
    if (isNullOrEmpty(string) || isNullOrEmpty(pattern)) {
      return 0;
    }
    final int stringLength = string.length();
    final int patternLength = pattern.length();
    if (patternLength > stringLength) {
      return 0;
    }
    int counter = 0;
    for (int i = 0; i < stringLength; i++) {
      int j = 0;
      for (j = 0; i + j < stringLength && j < patternLength; j++) {
        if (string.charAt(i + j) != pattern.charAt(j)) {
          break;
        }
      }
      if (j == patternLength) {
        counter++;
      }
    }
    return counter;
  }

  public static String reduce(final String string, final int maximumNumberOfRows, final int maximumColumnLength) {
    if (string == null) {
      return null;
    }
    if (string.length() < maximumColumnLength && string.indexOf("\n") < 0) {
      return string;
    }
    final StringBuilder builder = new StringBuilder();
    builder.append("<html><body>"); //$NON-NLS-1$
    final int stringLength = string.length();
    final int maximumLength = Math.min(maximumColumnLength * maximumNumberOfRows, stringLength);
    int endIndex = 0;
    for (int i = 0; i < maximumNumberOfRows; i++) {
      final int beginIndex = endIndex;
      endIndex = beginIndex + maximumColumnLength;
      String substring = string.substring(beginIndex, Math.min(endIndex, stringLength));
      if (substring.contains("\n")) {
        endIndex = beginIndex + substring.indexOf('\n') + 1;
        substring = string.substring(beginIndex, endIndex);
      }
      builder.append(substring);
      if (maximumLength <= endIndex) {
        if (maximumLength != stringLength) {
          builder.append("..."); //$NON-NLS-1$
        }
        builder.append("</body></html>"); //$NON-NLS-1$
        return builder.toString();
      }
      builder.append("<br>"); //$NON-NLS-1$
    }
    if (maximumLength != stringLength) {
      builder.append("..."); //$NON-NLS-1$
    }
    builder.append("</body></html>"); //$NON-NLS-1$
    return builder.toString();
  }

  public static boolean in(final String value, final String... values) {
    return new HashSet<>(Arrays.asList(values)).contains(value);
  }

  @SuppressWarnings("deprecation")
  public static String
      wrap(final String str, final int wrapLength, final String newLineStr, final boolean wrapLongWords) {
    return WordUtils.wrap(str, wrapLength, newLineStr, wrapLongWords);
  }
}