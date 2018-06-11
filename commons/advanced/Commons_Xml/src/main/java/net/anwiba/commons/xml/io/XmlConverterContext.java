/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.xml.io;

import java.util.Objects;

import net.anwiba.commons.version.IVersion;

public final class XmlConverterContext implements IXmlConverterContext {
  private final String name;
  private final IVersion version;
  private final Class<?> clazz;

  public XmlConverterContext(final String name, final IVersion version, final Class<?> clazz) {
    this.name = name;
    this.version = version;
    this.clazz = clazz;
  }

  @Override
  public String getElementName() {
    return this.name;
  }

  @Override
  public IVersion getVersion() {
    return this.version;
  }

  @Override
  public Class<?> getTargetObjectClass() {
    return this.clazz;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.clazz == null) ? 0 : this.clazz.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.version == null) ? 0 : this.version.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IXmlConverterContext)) {
      return false;
    }
    final IXmlConverterContext other = (IXmlConverterContext) obj;
    return Objects.equals(getElementName(), other.getElementName())
        && (getVersion() == null || Objects.equals(getVersion(), other.getVersion()))
        && Objects.equals(getTargetObjectClass(), other.getTargetObjectClass());
  }

}
