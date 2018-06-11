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
package net.anwiba.commons.utilities.regex.tokenizer.test;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

import junit.framework.AssertionFailedError;
import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.regex.DoNothingStringConverter;
import net.anwiba.commons.utilities.regex.tokenizer.IRegExpTokenConverter;
import net.anwiba.commons.utilities.regex.tokenizer.RegExpTokenizingConverter;
import net.anwiba.commons.utilities.string.StringAppender;

public class RegExpTokenizingConverterTest {

  @Test
  public void testConvertWithoutConverters() throws ConversionException {
    final RegExpTokenizingConverter converter1 = new RegExpTokenizingConverter(
        new DoNothingStringConverter(),
        new IRegExpTokenConverter[0]);
    assertEquals("", converter1.convert("", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
    assertEquals("bla", converter1.convert("bla", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$

    final RegExpTokenizingConverter converter2 = new RegExpTokenizingConverter(
        new DoNothingStringConverter(),
        new IRegExpTokenConverter[0]);
    assertEquals("", converter2.convert("", new StringAppender("foo"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("bla", converter2.convert("bla", new StringAppender("foo"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
  }

  @Test
  public void testConvertWithoutConvertersUsesStringConverter() throws ConversionException {
    final RegExpTokenizingConverter converter = new RegExpTokenizingConverter(
        new IConverter<String, String, RuntimeException>() {
          @Override
          public String convert(final String text) {
            return "a" + text + "b"; //$NON-NLS-1$//$NON-NLS-2$
          }
        },
        new IRegExpTokenConverter[0]);
    assertEquals("", converter.convert("", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
    assertEquals("ablab", converter.convert("bla", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
  }

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{(.+?)(?::(.+?))?}"); //$NON-NLS-1$

  @Test
  public void testConvertWithOneRegExpConverterNotMatching() throws ConversionException {
    final RegExpTokenizingConverter converter = new RegExpTokenizingConverter(
        new DoNothingStringConverter(),
        new IRegExpTokenConverter[]{ new IRegExpTokenConverter() {
          @Override
          public Pattern getRegExpPattern() {
            return VARIABLE_PATTERN;
          }

          @Override
          public String convert(final String[] groups) {
            throw new AssertionFailedError("Using converter, although not matching"); //$NON-NLS-1$
          }
        } });
    assertEquals("", converter.convert("", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
    assertEquals("ab", converter.convert("ab", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
    assertEquals("$$", converter.convert("$$", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
  }

  @Test
  public void testConvertWithOneRegExpConverterMatching() throws ConversionException {
    final IRegExpTokenConverter[] tokenConverters = new IRegExpTokenConverter[]{ new IRegExpTokenConverter() {
      @Override
      public Pattern getRegExpPattern() {
        return VARIABLE_PATTERN;
      }

      @Override
      public String convert(final String[] groups) {
        return "match"; //$NON-NLS-1$
      }
    } };

    final RegExpTokenizingConverter converter1 = new RegExpTokenizingConverter(
        new DoNothingStringConverter(),
        tokenConverters);
    assertEquals("match", converter1.convert("${variable}", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
    assertEquals("match", converter1.convert("${other_variable}", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$

    final RegExpTokenizingConverter converter2 = new RegExpTokenizingConverter(
        new DoNothingStringConverter(),
        tokenConverters);
    assertEquals("match", converter2.convert("${variable}", new StringAppender("bla"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

    final RegExpTokenizingConverter converter3 = new RegExpTokenizingConverter(
        new IConverter<String, String, RuntimeException>() {
          @Override
          public String convert(final String text) {
            return "a" + text + "b"; //$NON-NLS-1$//$NON-NLS-2$
          }
        },
        tokenConverters);
    assertEquals("match", converter3.convert("${variable}", new StringAppender(""))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
  }

  @Test
  public void testConvertTokenizedWithOneRegExpConverterMatching() throws ConversionException {
    final IRegExpTokenConverter[] tokenConverters = new IRegExpTokenConverter[]{ new IRegExpTokenConverter() {
      @Override
      public Pattern getRegExpPattern() {
        return VARIABLE_PATTERN;
      }

      @Override
      public String convert(final String[] groups) {
        return "match"; //$NON-NLS-1$
      }
    } };

    final RegExpTokenizingConverter converter1 = new RegExpTokenizingConverter(
        new DoNothingStringConverter(),
        tokenConverters);
    assertEquals("AmatchB", converter1.convert("A${variable}B", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
    assertEquals("match2", converter1.convert("${other_variable}2", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$

    final RegExpTokenizingConverter converter2 = new RegExpTokenizingConverter(
        new DoNothingStringConverter(),
        tokenConverters);
    assertEquals("A<>match<>B", converter2.convert("A${variable}B", new StringAppender("<>"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("match<>2", converter2.convert("${other_variable}2", new StringAppender("<>"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("1<>match", converter2.convert("1${other_variable}", new StringAppender("<>"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

    final RegExpTokenizingConverter converter3 = new RegExpTokenizingConverter(
        new IConverter<String, String, RuntimeException>() {
          @Override
          public String convert(final String text) {
            return "(" + text + ")"; //$NON-NLS-1$ //$NON-NLS-2$
          }
        },
        tokenConverters);
    assertEquals("(A)+match+(B)", converter3.convert("A${variable}B", new StringAppender("+"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("match+(2)", converter3.convert("${other_variable}2", new StringAppender("+"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
  }

  @Test
  public void testConverterReceivesMatchedGroups() throws ConversionException {
    final RegExpTokenizingConverter converter = new RegExpTokenizingConverter(
        new DoNothingStringConverter(),
        new IRegExpTokenConverter[]{ new IRegExpTokenConverter() {
          @Override
          public Pattern getRegExpPattern() {
            return VARIABLE_PATTERN;
          }

          @Override
          public String convert(final String[] groups) {
            org.junit.Assert.assertArrayEquals((new String[]{ "${ab:cd}", "ab", "cd" }), groups); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            return "matched"; //$NON-NLS-1$
          }
        } });
    assertEquals("matched", converter.convert("${ab:cd}", new StringAppender())); //$NON-NLS-1$//$NON-NLS-2$
  }

  private static final Pattern OTHER_VARIABLE_PATTERN = Pattern.compile("\\%\\{(.+?)(?::(.+?))?}"); //$NON-NLS-1$

  @Test
  public void testConverterWithMultipleTokenConverters() throws Exception {
    final RegExpTokenizingConverter converter = new RegExpTokenizingConverter(
        new IConverter<String, String, RuntimeException>() {
          @Override
          public String convert(final String text) {
            return '(' + text + ')';
          }
        },
        new IRegExpTokenConverter[]{ new IRegExpTokenConverter() {
          @Override
          public String convert(final String[] groups) {
            return "match1"; //$NON-NLS-1$
          }

          @Override
          public Pattern getRegExpPattern() {
            return VARIABLE_PATTERN;
          }
        }, new IRegExpTokenConverter() {
          @Override
          public String convert(final String[] groups) {
            return "match2"; //$NON-NLS-1$
          }

          @Override
          public Pattern getRegExpPattern() {
            return OTHER_VARIABLE_PATTERN;
          }
        } });

    assertEquals("", converter.convert("", new StringAppender("+"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("(A)", converter.convert("A", new StringAppender("+"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("match1", converter.convert("${ab:cd}", new StringAppender("+"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("match2", converter.convert("%{ab:cd}", new StringAppender("+"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("match1+match2", converter.convert("${ab:cd}%{ab:cd}", new StringAppender("+"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("(1)+match1+(#)+match2+(2)", converter.convert("1${ab:cd}#%{ab:cd}2", new StringAppender("+"))); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
  }
}
