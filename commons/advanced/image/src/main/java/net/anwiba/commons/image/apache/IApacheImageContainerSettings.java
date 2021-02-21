/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
package net.anwiba.commons.image.apache;

import java.awt.RenderingHints;

public interface IApacheImageContainerSettings {

  default boolean isEnabled() {
    return true;
  }

  static int IMAGEN_IMAGE_CONTAINER_SETTINGS = 0;
  public static RenderingHints.Key KEY_IMAGEN_IMAGE_CONTAINER_SETTINGS =
      new RenderingKey(IMAGEN_IMAGE_CONTAINER_SETTINGS, IApacheImageContainerSettings.class);

  static class DefaultImagenImageContainerSettings implements IApacheImageContainerSettings {

  }

  public static IApacheImageContainerSettings getSettings(final RenderingHints hints) {
    return (IApacheImageContainerSettings) hints.getOrDefault(
        KEY_IMAGEN_IMAGE_CONTAINER_SETTINGS,
        new DefaultImagenImageContainerSettings());
  }

  static class RenderingKey extends RenderingHints.Key {

    private final Class objectClass;

    RenderingKey(final int privateKey, final Class objectClass) {
      super(privateKey);
      this.objectClass = objectClass;
    }

    @Override
    public boolean isCompatibleValue(final Object val) {
      return this.objectClass.isInstance(val);
    }
  }

}
