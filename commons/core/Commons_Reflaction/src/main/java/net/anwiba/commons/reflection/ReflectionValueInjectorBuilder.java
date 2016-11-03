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

package net.anwiba.commons.reflection;

public class ReflectionValueInjectorBuilder implements IReflectionValueInjectorBuilder {

  private final IReflectionValueProviderBuilder builder = new ReflectionValueProviderBuilder();

  @Override
  public IReflectionValueInjector build() throws CreationException {
    final IReflectionValueProvider provider = this.builder.build();
    return new ReflectionValueInjector(provider);
  }

  @Override
  public <T, S extends T> IReflectionValueInjectorBuilder set(final Class<T> clazz, final S object) {
    this.builder.set(clazz, object);
    return this;
  }

  @Override
  public <T, S extends T> IReflectionValueInjectorBuilder add(final Class<T> clazz, final S object) {
    this.builder.add(clazz, object);
    return this;
  }

  @Override
  public <T> IReflectionValueInjectorBuilder set(final Class<T> clazz, final Class<? extends T> objectClass) {
    this.builder.set(clazz, objectClass);
    return this;
  }

  @Override
  public <T> IReflectionValueInjectorBuilder add(final Class<T> clazz, final Class<? extends T> objectClass) {
    this.builder.add(clazz, objectClass);
    return this;
  }

  @Override
  public <T> IReflectionValueInjectorBuilder set(final Class<T> clazz, final IInjectingFactory<T> objectFactory) {
    this.builder.set(clazz, objectFactory);
    return this;
  }

  @Override
  public <T> IReflectionValueInjectorBuilder add(final Class<T> clazz, final IInjectingFactory<T> objectFactory) {
    this.builder.add(clazz, objectFactory);
    return this;
  }

  @Override
  public <T> IReflectionValueInjectorBuilder link(final Class<T> clazz, final Class<? extends T> link) {
    this.builder.link(clazz, link);
    return this;
  }
}
