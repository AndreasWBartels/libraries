/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.injection.impl;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.anwiba.commons.reflection.CreationException;

public class ImitateObjectProxyFactory {

  @SuppressWarnings("rawtypes")
  public Object create(final Class clazz) throws CreationException {
    if (!clazz.isInterface()) {
      throw new CreationException("Couldn't create proxy for class '" + clazz + "', only interfaces are supported"); //$NON-NLS-1$//$NON-NLS-2$
    }
    final Method[] methods = clazz.getMethods();
    for (final Method method : methods) {
      final String name = method.getName();
      final Class<?> returnType = method.getReturnType();
      if (Void.class.equals(returnType) || void.class.equals(returnType)) {
        continue;
      }
      if (!method.isDefault()) {
        throw new CreationException(
            "Couldn't create proxy for class '" //$NON-NLS-1$
                + clazz
                + "', missing default implementation of method '" //$NON-NLS-1$
                + name
                + "'"); //$NON-NLS-1$
      }
    }
    final Class[] interfaces = new Class[]{ clazz };
    return Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, new InvocationHandler() {

      private Constructor<Lookup> constructor;

      @Override
      public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        if (method.isDefault()) {
          synchronized (proxy) {
            if (this.constructor == null) {
              final Constructor<Lookup> tmp = Lookup.class.getDeclaredConstructor(Class.class);
              this.constructor = tmp;
            }
          }
          this.constructor.setAccessible(true);
          return this.constructor
              .newInstance(clazz)
              .in(clazz)
              .unreflectSpecial(method, clazz)
              .bindTo(proxy)
              .invokeWithArguments(args);
        }

        final String name = method.getName();
        if (name.equals("equals")) { //$NON-NLS-1$
          return proxy == args[0];
        }
        if (name.equals("waite")) { //$NON-NLS-1$
          switch (args.length) {
            case 0: {
              this.wait();
              return null;
            }
            case 1: {
              this.wait(((Long) args[0]).longValue());
              return null;
            }
            case 2: {
              this.wait(((Long) args[0]).longValue(), ((Integer) args[1]).intValue());
              return null;
            }
          }
        }
        if (name.equals("getClass")) { //$NON-NLS-1$
          return clazz;
        }
        if (name.equals("toString")) { //$NON-NLS-1$
          return clazz.getName() + "@" + Integer.toHexString(hashCode()); //$NON-NLS-1$
        }
        if (Void.class.equals(method.getReturnType()) || void.class.equals(method.getReturnType())) {
          return null;
        }
        throw new RuntimeException();
      }
    });
  }
}
