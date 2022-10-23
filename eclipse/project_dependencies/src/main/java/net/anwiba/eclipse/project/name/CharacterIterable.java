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

public class CharacterIterable implements Iterable<Character> {

  private final String string;

  public CharacterIterable(final String name) {
    this.string = name;
  }

  @Override
  public Iterator<Character> iterator() {
    final char[] characters = this.string.toCharArray();
    return new Iterator<>() {

      Character character = null;
      int counter = -1;

      @Override
      public boolean hasNext() {
        if (this.character != null) {
          return true;
        }
        this.counter++;
        if (this.counter >= characters.length) {
          return false;
        }
        this.character = Character.valueOf(characters[this.counter]);
        return true;
      }

      @Override
      public Character next() {
        try {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return this.character;
        } finally {
          this.character = null;
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
