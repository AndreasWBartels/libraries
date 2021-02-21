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
package net.anwiba.commons.image.imageio;

import java.awt.RenderingHints;
import java.util.function.Function;

import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;

import net.anwiba.commons.lang.collection.IObjectList;

public interface IImageIoImageContainerSettings {

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

  default boolean isEnabled() {
    return true;
  }

  IImageIoImageContainerSettings
      adaptImageTypeSelection(Function<IObjectList<ImageTypeSpecifier>, ImageTypeSpecifier> imageTypeSelection);

  IImageIoImageContainerSettings adaptReaderSelection(Function<IObjectList<ImageReader>, ImageReader> readerSelection);

  ImageReader getImageReader(IObjectList<ImageReader> imageReaders);

  ImageTypeSpecifier getImageTypeSpecifier(IObjectList<ImageTypeSpecifier> imageTypeSpecifier);

  public static IImageIoImageContainerSettings getSettings(final RenderingHints hints) {
    return (IImageIoImageContainerSettings) hints.getOrDefault(
        KEY_IMAGE_IO_IMAGE_CONTAINER_SETTINGS,
        IImageIoImageContainerSettings.of());
  }

  static class ImageIoImageContainerSettings implements IImageIoImageContainerSettings {

    Function<IObjectList<ImageReader>, ImageReader> readerSelection;
    Function<IObjectList<ImageTypeSpecifier>, ImageTypeSpecifier> imageTypeSelection;

    private ImageIoImageContainerSettings() {
      this(r -> r.stream().first().get(), t -> t.stream().first().get());
    }

    private ImageIoImageContainerSettings(final Function<IObjectList<ImageReader>, ImageReader> readerSelection,
        final Function<IObjectList<ImageTypeSpecifier>, ImageTypeSpecifier> imageTypeSelection) {
      this.readerSelection = readerSelection;
      this.imageTypeSelection = imageTypeSelection;
    }

    @Override
    public ImageReader getImageReader(final IObjectList<ImageReader> imageReaders) {
      return this.readerSelection.apply(imageReaders);
    }

    @Override
    public ImageTypeSpecifier getImageTypeSpecifier(final IObjectList<ImageTypeSpecifier> imageTypeSpecifiers) {
      return this.imageTypeSelection.apply(imageTypeSpecifiers);
    }

    @Override
    public ImageIoImageContainerSettings
        adaptReaderSelection(final Function<IObjectList<ImageReader>, ImageReader> readerSelection) {
      return new ImageIoImageContainerSettings(readerSelection, this.imageTypeSelection);
    }

    @Override
    public ImageIoImageContainerSettings
        adaptImageTypeSelection(
            final Function<IObjectList<ImageTypeSpecifier>, ImageTypeSpecifier> imageTypeSelection) {
      return new ImageIoImageContainerSettings(this.readerSelection, imageTypeSelection);
    }

  }

  public static IImageIoImageContainerSettings of() {
    return new ImageIoImageContainerSettings();
  }

  static final int IMAGE_IO_IMAGE_CONTAINER_SETTINGS = 0;
  public static RenderingHints.Key KEY_IMAGE_IO_IMAGE_CONTAINER_SETTINGS =
      new RenderingKey(IMAGE_IO_IMAGE_CONTAINER_SETTINGS, IImageIoImageContainerSettings.class);
}
