/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
package net.anwiba.eclipse.project.name;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NamePartsIterable implements INamePartsIterable {

  private final String name;

  public NamePartsIterable(final String name) {
    this.name = name;
  }

  @Override
  public Iterator<String> iterator() {
    final Iterator<Character> characterIterator = new CharacterIterable(this.name).iterator();
    return new Iterator<String>() {

      StringBuilder builder = new StringBuilder();
      String namePart = null;

      @Override
      public boolean hasNext() {
        if (this.namePart != null) {
          return true;
        }
        while (characterIterator.hasNext()) {
          final char character = characterIterator.next().charValue();
          if (Character.isAlphabetic(character) || Character.isDigit(character)) {
            if (Character.isUpperCase(character)) {
              if (this.builder.length() == 0) {
                this.builder.append(character);
                continue;
              }
              if (this.builder.length() == 1) {
                this.builder = new StringBuilder();
                this.builder.append(character);
                continue;
              }
              this.namePart = this.builder.toString();
              this.builder = new StringBuilder();
              this.builder.append(character);
              return true;
            }
            this.builder.append(character);
          }
        }
        if (this.builder.length() > 1) {
          this.namePart = this.builder.toString();
          this.builder = new StringBuilder();
          return true;
        }
        return false;
      }

      @Override
      public String next() {
        try {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return this.namePart;
        } finally {
          this.namePart = null;
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
