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
