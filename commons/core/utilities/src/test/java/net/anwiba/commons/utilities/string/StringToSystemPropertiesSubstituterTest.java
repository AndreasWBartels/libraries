/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class StringToSystemPropertiesSubstituterTest {

  @Test
  public void replace() {
    IStringSubstituter substituter = new StringToSystemPropertiesSubstituter(List.of("foo", "bar"), () -> {
      final Properties properties = new Properties();
      properties.put("foo", "footeloose");
      properties.put("bar", "barbeque");
      return properties;
    });
    assertEquals("$SYSTEM{foo} movie 1984", substituter.substitute("footeloose movie 1984"));
    assertEquals("movie 1984, $SYSTEM{foo}", substituter.substitute("movie 1984, footeloose"));
    assertEquals("$SYSTEM{foo} movie 1984, $SYSTEM{foo}", substituter.substitute("footeloose movie 1984, footeloose"));
    assertEquals("movie $SYSTEM{foo} 1984", substituter.substitute("movie footeloose 1984"));
    assertEquals("$SYSTEM{foo}, $SYSTEM{bar}, 1984", substituter.substitute("footeloose, barbeque, 1984"));
  }
}
