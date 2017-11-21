/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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
// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.icons.description;

import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.eclipse.icons.io.IconContext;
import net.anwiba.tools.icons.configuration.generated.Class;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.Device;

public final class GuiIconDescriptionsFactory {

  private final Device device;

  public GuiIconDescriptionsFactory(final Device device) {
    this.device = device;
  }

  public List<IGuiIconDescription> create(
      final ICanceler canceler,
      final Map<Class, List<IconContext>> configurations) {
    final List<IGuiIconDescription> descriptions = new ArrayList<>();
    final Map<IConstant, IconContext> map = new HashMap<>();
    for (final Entry<Class, List<IconContext>> entry : configurations.entrySet()) {
      if (canceler.isCanceled()) {
        return new ArrayList<>();
      }
      final Class clazz = entry.getKey();
      for (final IconContext iconContext : entry.getValue()) {
        map.put(new Constant(clazz.getPackage(), clazz.getName(), iconContext.getResource().getName()), iconContext);
      }
    }
    final GuiIconDescriptionFactory factory = new GuiIconDescriptionFactory(this.device);
    for (final Entry<Class, List<IconContext>> entry : configurations.entrySet()) {
      final Class clazz = entry.getKey();
      for (final IconContext iconContext : entry.getValue()) {
        if (canceler.isCanceled()) {
          return new ArrayList<>();
        }
        if (isRefernced(iconContext)) {
          final HashSet<IConstant> set = new HashSet<>();
          final Constant constant =
              new Constant(clazz.getPackage(), clazz.getName(), iconContext.getResource().getName());
          set.add(constant);
          final IconContext referenzedContext = getReferenzedIconContext(map, constant, iconContext, set);
          if (referenzedContext == null) {
            continue;
          }
          final IGuiIconDescription description = factory.create(clazz, iconContext, referenzedContext);
          if (description == null) {
            continue;
          }
          descriptions.add(description);
          continue;
        }
        if (iconContext.getResource().getImage() == null) {
          continue;
        }
        final IGuiIconDescription description = factory.create(clazz, iconContext);
        if (description == null) {
          continue;
        }
        descriptions.add(description);
      }
    }
    return descriptions;
  }

  private IconContext getReferenzedIconContext(
      final Map<IConstant, IconContext> map,
      final IConstant constant,
      final IconContext context,
      final HashSet<IConstant> set) {
    final IConstant referenzedConstant = getReferenzedConstant(constant, context);
    final IconContext referencedContext = map.get(referenzedConstant);
    if (referencedContext == null || !isRefernced(context)) {
      return context;
    }
    if (set.contains(referenzedConstant)) {
      return null;
    }
    set.add(referenzedConstant);
    return getReferenzedIconContext(map, referenzedConstant, referencedContext, set);
  }

  private IConstant getReferenzedConstant(final IConstant constant, final IconContext context) {
    System.out.println(MessageFormat.format("{0}.{1}.{2}", //$NON-NLS-1$
        constant.getPackageName(), constant.getClassName(), constant.getConstantName()));
    final Class referencedClass = context.getResource().getClazz();
    final String reference = context.getResource().getReference() == null
        ? context.getResource().getName()
        : context.getResource().getReference();
    if (referencedClass == null || constant.equals(referencedClass)) {
      return new Constant(constant.getPackageName(), constant.getClassName(), reference);
    }
    return new Constant(referencedClass.getPackage(), referencedClass.getName(), reference);
  }

  private boolean isRefernced(final IconContext iconContext) {
    return iconContext.getResource().getReference() != null || iconContext.getResource().getImage() == null;
    // return iconContext.getResource().getReference() != null || iconContext.getResource().getImage() != null;
  }
}