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
package net.anwiba.eclipse.icons.description;

import java.text.MessageFormat;
import java.util.Arrays;

public class Constant implements IConstant {

  private final String constantName;
  private final String className;
  private final String packageName;

  public Constant(final String packageName, final String className, final String constantName) {
    this.packageName = packageName;
    this.className = className;
    this.constantName = constantName;
  }

  @Override
  public String getConstantName() {
    return this.constantName;
  }

  @Override
  public String getClassName() {
    return this.className;
  }

  @Override
  public String getPackageName() {
    return this.packageName;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof IConstant) {
      final IConstant other = (IConstant) obj;
      return Arrays.equals(
          new String[] { getPackageName(), getClassName(), getConstantName() },
          new String[] { other.getPackageName(), other.getClassName(), other.getConstantName() });
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new String[] { getPackageName(), getClassName(), getConstantName() });
  }

  @Override
  public String getName() {
    return MessageFormat.format("{0}.{1}.{2}", //$NON-NLS-1$
        this.packageName,
        this.className,
        this.constantName);
  }
}
