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
package net.anwiba.tools.generator.java.bean.configuration;

public class Builders {

  public static TypeBuilder type(final String name, final String... generics) {
    return type(name, 0, generics);
  }

  public static TypeBuilder type(final String name, final int dimension, final String... generics) {
    final TypeBuilder builder = new TypeBuilder(name).dimension(dimension);
    for (final String generic : generics) {
      builder.generic(generic);
    }
    return builder;
  }

  public static AnnotationBuilder annotation(final String string) {
    return new AnnotationBuilder(string);
  }

  public static MemberBuilder member(final Type type, final String name) {
    return new MemberBuilder(type, name);
  }

  public static PropertiesBuilder properties(final Type type, final String name) {
    return new PropertiesBuilder(type, name);
  }

  public static BeanBuilder bean(final String type) {
    return new BeanBuilder(type);
  }

  public static CreatorBuilder creator(final String name) {
    return new CreatorBuilder(name);
  }

}
