/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.utilities.io.url.parser;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.utilities.string.StringUtilities;

class PathBuilder {

  private final List<String> items = new ArrayList<>();
  private StringBuilder stringBuilder = new StringBuilder();

  public List<String> build() {
    final String string = this.stringBuilder.toString();
    if (!StringUtilities.isNullOrEmpty(string)) {
      this.items.add(string);
    }
    return this.items;
  }

  public void add(final char character) {
    if (character == '/') {
      final String string = this.stringBuilder.toString();
      if (!StringUtilities.isNullOrEmpty(string)) {
        this.items.add(string);
        this.stringBuilder = new StringBuilder();
      }
    }
    this.stringBuilder.append(character);
  }
}
