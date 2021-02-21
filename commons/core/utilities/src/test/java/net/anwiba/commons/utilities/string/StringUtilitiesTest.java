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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Locale;

import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class StringUtilitiesTest {

  @Test
  public void testNullOrEmpty() throws Exception {
    assertTrue(StringUtilities.isNullOrTrimmedEmpty(null));
    assertTrue(StringUtilities.isNullOrTrimmedEmpty("")); //$NON-NLS-1$
    assertTrue(StringUtilities.isNullOrTrimmedEmpty(" ")); //$NON-NLS-1$
  }

  @Test
  public void testNumeric() throws Exception {
    assertFalse(StringUtilities.isNumericValue(null));
    assertFalse(StringUtilities.isNumericValue("")); //$NON-NLS-1$
    assertFalse(StringUtilities.isNumericValue(" ")); //$NON-NLS-1$
    Locale.setDefault(Locale.GERMAN);
    assertTrue(StringUtilities.isNumericValue("0")); //$NON-NLS-1$
    assertTrue(StringUtilities.isNumericValue("0,0")); //$NON-NLS-1$
    assertTrue(StringUtilities.isNumericValue("1000,0")); //$NON-NLS-1$
    assertTrue(StringUtilities.isNumericValue("1.000,0")); //$NON-NLS-1$
    Locale.setDefault(Locale.ENGLISH);
    assertTrue(StringUtilities.isNumericValue("0.0")); //$NON-NLS-1$
    assertTrue(StringUtilities.isNumericValue("1000.0")); //$NON-NLS-1$
    assertTrue(StringUtilities.isNumericValue("1,000.0")); //$NON-NLS-1$
    assertFalse(StringUtilities.isNumericValue("1.000.,0")); //$NON-NLS-1$
    assertFalse(StringUtilities.isNumericValue("a1.000,0")); //$NON-NLS-1$
    assertFalse(StringUtilities.isNumericValue("1.00s0,0")); //$NON-NLS-1$
    assertFalse(StringUtilities.isNumericValue("100s0,0")); //$NON-NLS-1$
    assertFalse(StringUtilities.isNumericValue("a1000,0")); //$NON-NLS-1$
  }

  @Test
  public void testCreateUniqueName() throws Exception {
    final String sid = "SID"; //$NON-NLS-1$
    assertEquals(sid, StringUtilities.createUniqueName(sid, new HashSet<String>()));
    assertEquals(sid + "1", StringUtilities.createUniqueName(sid, new String[] { sid })); //$NON-NLS-1$
    assertEquals(sid, StringUtilities.createUniqueName(sid, new String[] { "SID1" })); //$NON-NLS-1$
    assertEquals(sid + "2", StringUtilities.createUniqueName(sid, new String[] { sid, "SID1" })); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals(sid + "124", StringUtilities.createUniqueName(sid, new String[] { sid, "SID1", "SID123" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals(sid + "1", StringUtilities.createUniqueName(sid, new String[] { "sid" })); //$NON-NLS-1$//$NON-NLS-2$
  }

  @Test
  public void testCreateUniqueName2() throws Exception {
    final String sid = "XXX_NAME_0"; //$NON-NLS-1$
    assertEquals(sid, StringUtilities.createUniqueName(sid, new HashSet<String>()));
    assertEquals(sid + "1", StringUtilities.createUniqueName(sid, new String[] { sid })); //$NON-NLS-1$
    assertEquals(sid, StringUtilities.createUniqueName(sid, new String[] { "XXX_NAME_01" })); //$NON-NLS-1$
  }

  @Test
  public void testRemoveEqualEnd() {
    assertThat(StringUtilities.removeEqualEnd("VALUEE", "E"), equalTo("VALUE")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertThat(StringUtilities.removeEqualEnd("VALUEE", "Z"), equalTo("VALUEE")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    assertThat(StringUtilities.removeEqualEnd("VALUEE", "A"), equalTo("VALUEE")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
  }

  @Test
  public void testGetStringPositions() {
    assertTrue(StringUtilities.getStringPositions("text", null).isEmpty()); //$NON-NLS-1$
    assertTrue(StringUtilities.getStringPositions("text", "").isEmpty()); //$NON-NLS-1$ //$NON-NLS-2$
    assertTrue(StringUtilities.getStringPositions("text", "condition").isEmpty()); //$NON-NLS-1$ //$NON-NLS-2$
    assertTrue(StringUtilities.getStringPositions("", "condition").isEmpty()); //$NON-NLS-1$ //$NON-NLS-2$
    assertTrue(StringUtilities.getStringPositions(null, "condition").isEmpty()); //$NON-NLS-1$
    assertEquals(1, StringUtilities.getStringPositions("text", "te").size()); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals(2, StringUtilities.getStringPositions("texte", "te").size()); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Test
  public void testTokens() throws Exception {
    assertThat(StringUtilities.tokens("/test/token/", '/'), equalTo(new String[] { "test", "token" }));
    assertThat(StringUtilities.tokens("/test/token/T", '/'), equalTo(new String[] { "test", "token", "T" }));
    assertThat(StringUtilities.tokens("T/test/token/T", '/'), equalTo(new String[] { "T", "test", "token", "T" }));
    assertThat(StringUtilities.tokens(null, '/'), equalTo(new String[] {}));
    assertThat(StringUtilities.tokens("", '/'), equalTo(new String[] {}));
  }

  @Test
  public void removeWhiteSpaces() {
    assertThat(StringUtilities.removeWhiteSpaces("VALUE"), equalTo("VALUE"));
    assertThat(StringUtilities.removeWhiteSpaces("VAL UE"), equalTo("VAL UE"));
    assertThat(StringUtilities.removeWhiteSpaces("VAL  UE"), equalTo("VAL UE"));
    assertThat(StringUtilities.removeWhiteSpaces("VAL\n\fUE"), equalTo("VAL UE"));
    assertThat(StringUtilities.removeWhiteSpaces("VA\"L\n\fU\"E"), equalTo("VA\"L\n\fU\"E"));
    assertThat(StringUtilities.removeWhiteSpaces("VA\\n\"L\n\fU\"E"), equalTo("VA\\n\"L\n\fU\"E"));
    assertThat(StringUtilities.removeWhiteSpaces("VA\"L   U\"\n E"), equalTo("VA\"L   U\" E"));
  }
}
