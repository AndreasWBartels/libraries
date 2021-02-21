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
package net.anwiba.tools.definition.schema.json.generator.java.bean;

import java.text.MessageFormat;
import static net.anwiba.tools.generator.java.bean.JavaConstants.*;

public class BeanNameConverter {
  private final String packageName;

  public BeanNameConverter(final String packageName) {
    this.packageName = packageName;
  }

  public String convert(final String name) {
    return isSimpleClassName(name)
        ? MessageFormat.format("{0}.{1}", this.packageName, firstLetterToUpperCase(name)) : name; //$NON-NLS-1$
  }

  private String firstLetterToUpperCase(final String name) {
    if (name.length() == 1) {
      return name.toUpperCase();
    }
    return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
  }

  private boolean isSimpleClassName(final String name) {
    if (name.contains(".")) { //$NON-NLS-1$
      return false;
    }
    if (primitives.contains(name)) {
      return false;
    }
    if (isInJavaLang(name)) {
      return false;
    }
    return true;
  }

  private boolean isInJavaLang(final String name) {
    try {
      Class.forName(MessageFormat.format("java.lang.{0}", name)); //$NON-NLS-1$
      return true;
    } catch (final ClassNotFoundException exception) {
      return false;
    }
  }
}