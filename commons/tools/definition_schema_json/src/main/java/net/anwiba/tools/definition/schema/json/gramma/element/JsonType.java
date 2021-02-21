/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.definition.schema.json.gramma.element;

public enum JsonType {
  OBJECT {
    @Override
    public <O, E extends Exception> O accept(final IJsonTypeVisitor<O, E> visitor) throws E {
      return visitor.visitObject();
    }
  },
  ARRAY {
    @Override
    public <O, E extends Exception> O accept(final IJsonTypeVisitor<O, E> visitor) throws E {
      return visitor.visitArray();
    }
  },
  STRING {
    @Override
    public <O, E extends Exception> O accept(final IJsonTypeVisitor<O, E> visitor) throws E {
      return visitor.visitString();
    }
  },
  NUMBER {
    @Override
    public <O, E extends Exception> O accept(final IJsonTypeVisitor<O, E> visitor) throws E {
      return visitor.visitNumber();
    }
  },
  BOOLEAN {
    @Override
    public <O, E extends Exception> O accept(final IJsonTypeVisitor<O, E> visitor) throws E {
      return visitor.visitBoolean();
    }
  },
  CHARACTER {
    @Override
    public <O, E extends Exception> O accept(final IJsonTypeVisitor<O, E> visitor) throws E {
      return visitor.visitCharacter();
    }
  },
  NULL {
    @Override
    public <O, E extends Exception> O accept(final IJsonTypeVisitor<O, E> visitor) throws E {
      return visitor.visitNull();
    }
  };

  public abstract <O, E extends Exception> O accept(IJsonTypeVisitor<O, E> visitor) throws E;
}
