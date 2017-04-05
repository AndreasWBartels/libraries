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
package net.anwiba.tools.icons.configuration;

import java.util.Arrays;

import net.anwiba.tools.icons.configuration.generated.Class;

public class IconResource {

  private final String name;
  private final String image;
  private final String reference;
  private final Class clazz;
  private final IIconSizesConfiguration iconSizesConfiguration;
  private final boolean isDecorator;

  public IconResource(
      final IIconSizesConfiguration iconSizesConfiguration,
      final String name,
      final String image,
      final String reference,
      final Class clazz,
      final boolean isDecorator) {
    this.iconSizesConfiguration = iconSizesConfiguration;
    this.name = name;
    this.image = image;
    this.reference = reference;
    this.clazz = clazz;
    this.isDecorator = isDecorator;
  }

  public String getName() {
    return this.name;
  }

  public String getImage() {
    return this.image;
  }

  public String getReference() {
    return this.reference;
  }

  public Class getClazz() {
    return this.clazz;
  }

  public IIconSizesConfiguration getIconSizesConfiguration() {
    return this.iconSizesConfiguration;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof IconResource)) {
      return false;
    }
    final IconResource other = (IconResource) obj;
    return Arrays.equals(
        new Object[]{ this.name, this.image, this.reference, this.clazz.getPackage(), this.clazz.getName() }, //
        new Object[]{ other.name, other.image, other.reference, other.clazz.getPackage(), other.clazz.getName() });
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{
        this.name,
        this.image,
        this.reference,
        this.clazz.getPackage(),
        this.clazz.getName() });
  }

  public boolean isDecorator() {
    return this.isDecorator;
  }
}
