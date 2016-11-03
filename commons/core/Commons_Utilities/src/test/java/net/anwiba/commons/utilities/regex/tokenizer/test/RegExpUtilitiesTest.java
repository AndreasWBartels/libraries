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

import net.anwiba.commons.utilities.regex.tokenizer.RegExpUtilities;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegExpUtilitiesTest {

  @SuppressWarnings("nls")
  @Test
  public void like() throws Exception {
    assertTrue(RegExpUtilities.like("test", "test", '\''));
    assertFalse(RegExpUtilities.like("test", "testt", '\''));
    assertTrue(RegExpUtilities.like("test", "%test", '\''));
    assertTrue(RegExpUtilities.like("test", "test%", '\''));
    assertTrue(RegExpUtilities.like("test", "te%st", '\''));
    assertTrue(RegExpUtilities.like("teesst", "te%st", '\''));
    assertTrue(RegExpUtilities.like("tee sst", "te%st", '\''));
    assertTrue(RegExpUtilities.like("tee tee sst sst", "%te%st%", '\''));
    assertTrue(RegExpUtilities.like("tee sst", "te___st", '\''));
  }

  @SuppressWarnings("nls")
  @Test
  public void likeEscape() throws Exception {
    assertTrue(RegExpUtilities.like("tee_sst", "te_'__st", '\''));
    assertFalse(RegExpUtilities.like("teessst", "te_'__st", '\''));
  }

  @SuppressWarnings("nls")
  @Test
  public void specialCharacters() throws Exception {
    assertTrue(RegExpUtilities.like("(.)*", "(.)*", '\''));
    assertTrue(RegExpUtilities.like("\\p{alpa}", "\\p{alpa}", '\''));
    assertTrue(RegExpUtilities.like("[]", "[]", '\''));
    assertTrue(RegExpUtilities.like("$", "$", '\''));
    assertTrue(RegExpUtilities.like("?", "?", '\''));
    assertTrue(RegExpUtilities.like("+", "+", '\''));
    assertTrue(RegExpUtilities.like("-", "-", '\''));
    assertTrue(RegExpUtilities.like("&", "&", '\''));
    assertTrue(RegExpUtilities.like("|", "|", '\''));
  }
}
