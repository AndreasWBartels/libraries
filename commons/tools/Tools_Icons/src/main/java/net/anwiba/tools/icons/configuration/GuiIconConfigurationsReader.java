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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.tools.icons.configuration.generated.Class;
import net.anwiba.tools.icons.configuration.generated.Folder;
import net.anwiba.tools.icons.configuration.generated.Icon;
import net.anwiba.tools.icons.configuration.generated.IconSize;
import net.anwiba.tools.icons.configuration.generated.IconSizes;
import net.anwiba.tools.icons.configuration.generated.Icons;

public class GuiIconConfigurationsReader {

  private Class clazz = null;
  private final Map<String, IconResource> iconConfigurations = new HashMap<>();
  private final Map<String, String> folders = new HashMap<>();
  private final boolean isForced;
  private final IOutput output;
  private final IImageExistsValidator imageExistsValidator;

  public GuiIconConfigurationsReader(final IImageExistsValidator imageExistsValidator, final IOutput output) {
    this(imageExistsValidator, output, true);
  }

  public GuiIconConfigurationsReader(
      final IImageExistsValidator imageExistsValidator,
      final IOutput output,
      final boolean isForced) {
    this.imageExistsValidator = imageExistsValidator;
    this.output = output;
    this.isForced = isForced;
  }

  public void add(final File file) throws IOException {
    try {
      final JAXBContext jc = JAXBContext.newInstance(
          net.anwiba.tools.icons.configuration.generated.Icons.class,
          net.anwiba.tools.icons.configuration.generated.Class.class,
          net.anwiba.tools.icons.configuration.generated.IconSizes.class,
          net.anwiba.tools.icons.configuration.generated.IconSize.class,
          net.anwiba.tools.icons.configuration.generated.Icon.class);
      final Unmarshaller u = jc.createUnmarshaller();
      final Icons icons = (Icons) u.unmarshal(file);
      final String parentFolder = file.getParent();

      add(icons, parentFolder);
    } catch (final JAXBException exception) {
      throw new IOException(exception);
    }
  }

  public void add(final URL url) throws IOException {
    try {
      final JAXBContext jc = JAXBContext.newInstance(
          net.anwiba.tools.icons.configuration.generated.Icons.class,
          net.anwiba.tools.icons.configuration.generated.Class.class,
          net.anwiba.tools.icons.configuration.generated.IconSizes.class,
          net.anwiba.tools.icons.configuration.generated.IconSize.class,
          net.anwiba.tools.icons.configuration.generated.Icon.class);
      final Unmarshaller u = jc.createUnmarshaller();
      final Icons icons = (Icons) u.unmarshal(url);
      add(icons, null);
    } catch (final JAXBException exception) {
      throw new IOException(exception);
    }
  }

  public void add(final Icons icons, final String parentFolder) throws IOException {
    final Class iconsClazz = icons.getClazz();
    if (this.clazz == null) {
      this.clazz = iconsClazz;
    }
    final String iconsPackageName = iconsClazz.getPackage();
    final String iconsClassName = iconsClazz.getName();
    final List<Folder> iconFolders = icons.getFolder();
    final Set<String> names = new HashSet<>();
    for (final Folder folder : iconFolders) {
      names.addAll(icons.getIcon().stream().map(i -> i.getName()).collect(Collectors.toSet()));
      add(
          iconsClazz,
          iconsPackageName,
          iconsClassName,
          parentFolder,
          getIconSizesConfiguration(folder.getName(), folder.getSizes()),
          folder.getIcon());
    }
    names.addAll(icons.getIcon().stream().map(i -> i.getName()).collect(Collectors.toSet()));
    add(
        iconsClazz,
        iconsPackageName,
        iconsClassName,
        parentFolder,
        getIconSizesConfiguration(null, icons.getSizes()),
        icons.getIcon());
    for (final Folder folder : iconFolders) {
      this.folders.put(folder.getName(), createVariableName(names, "FOLDER_" + folder.getName())); //$NON-NLS-1$
    }
  }

  private String createVariableName(final Set<String> names, final String name) {
    return StringUtilities.createUniqueName(StringUtilities.createConstantsName(name), names);
  }

  public void add(
      final Class iconsClazz,
      final String iconsPackageName,
      final String iconsClassName,
      final String parentFolder,
      final IIconSizesConfiguration iconSizesConfiguration,
      final List<Icon> iconsList) throws IOException {
    for (final Icon icon : iconsList) {
      if (this.iconConfigurations.containsKey(icon.getName())) {
        final IconResource resource = this.iconConfigurations.get(icon.getName());
        final String message = MessageFormat
            .format("conflict, multiple definition for image resource {0}", icon.getName()); //$NON-NLS-1$
        this.output.error(message);
        this.output.error(
            MessageFormat.format("\tclass  {0}.{1}", resource.getClazz().getPackage(), resource.getClazz().getName())); //$NON-NLS-1$
        this.output.error(MessageFormat.format("\tclass  {0}.{1}", iconsPackageName, iconsClassName)); //$NON-NLS-1$
        if (!this.isForced) {
          throw new IOException(message);
        }
      }
      if (icon.getImage() != null && icon.getClazz() == null && parentFolder != null) {
        if (!this.imageExistsValidator.exists(parentFolder, iconSizesConfiguration, icon.getImage())) {
          this.output.warn(MessageFormat.format("\tclass {0} {1}.{2}", parentFolder, iconsPackageName, iconsClassName)); //$NON-NLS-1$
          if (!this.isForced) {
            final String message = MessageFormat.format(
                "no image {0} available.\tclass {0} {1} {2}.{3}", //$NON-NLS-1$
                parentFolder,
                icon.getImage(),
                iconsPackageName,
                iconsClassName);
            throw new IOException(message);
          }
        }
      }
      this.iconConfigurations.put(
          icon.getName(),
          new IconResource(
              iconSizesConfiguration,
              icon.getName(),
              icon.getImage(),
              icon.getRef(),
              icon.getClazz() != null ? icon.getClazz() : iconsClazz,
              icon.isDecorator()));
    }
  }

  public IIconSizesConfiguration getIconSizesConfiguration(final String parent, final IconSizes sizes) {
    if (sizes == null) {
      final IconSizeConfiguration small = new IconSizeConfiguration(16, "small"); //$NON-NLS-1$
      final IconSizeConfiguration medium = new IconSizeConfiguration(22, "medium"); //$NON-NLS-1$
      final IconSizeConfiguration large = new IconSizeConfiguration(32, "large"); //$NON-NLS-1$
      return new IconSizesConfiguration(parent, small, medium, large);
    }
    final IconSizeConfiguration small = getSizeConfiguration(sizes.getSmall());
    final IconSizeConfiguration medium = getSizeConfiguration(sizes.getMedium());
    final IconSizeConfiguration large = getSizeConfiguration(sizes.getLarge());
    return new IconSizesConfiguration(parent, small, medium, large);
  }

  public IconSizeConfiguration getSizeConfiguration(final IconSize size) {
    return new IconSizeConfiguration(size.getSize(), size.getPath());
  }

  public Class getClazz() {
    return this.clazz;
  }

  public Map<String, IconResource> getIconConfigurations() {
    return this.iconConfigurations;
  }

  public Map<String, String> getFolders() {
    return this.folders;
  }
}
