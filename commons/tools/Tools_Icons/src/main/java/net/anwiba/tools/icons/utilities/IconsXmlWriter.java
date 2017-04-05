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

package net.anwiba.tools.icons.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.anwiba.commons.resource.utilities.FileIterableFactory;
import net.anwiba.commons.resource.utilities.FileUtilities;
import net.anwiba.tools.icons.configuration.IIconSizesConfiguration;
import net.anwiba.tools.icons.configuration.IconSizeConfiguration;
import net.anwiba.tools.icons.configuration.IconSizesConfiguration;
import net.anwiba.tools.icons.configuration.generated.Class;
import net.anwiba.tools.icons.configuration.generated.Folder;
import net.anwiba.tools.icons.configuration.generated.Icon;
import net.anwiba.tools.icons.configuration.generated.IconSize;
import net.anwiba.tools.icons.configuration.generated.IconSizes;
import net.anwiba.tools.icons.configuration.generated.Icons;

public class IconsXmlWriter {

  @SuppressWarnings("nls")
  public void write(final File sourceFolder, final IIconSizesConfiguration configuration) throws JAXBException {
    final String sourceFolderPath = sourceFolder.getAbsolutePath();
    final Iterable<String> iterable = new FileIterableFactory().create(
        file -> FileUtilities.hasExtension(file, "png"),
        file -> {
          final String path = file.getAbsolutePath();
          final String relativePath = path.substring(sourceFolderPath.length(), path.length());
          if (relativePath.startsWith(File.separator + configuration.small().path() + File.separator)) {
            return relativePath.substring(
                (File.separator + configuration.small().path() + File.separator).length(),
                relativePath.length());
          }
          if (relativePath.startsWith(File.separator + configuration.medium().path() + File.separator)) {
            return relativePath.substring(
                (File.separator + configuration.medium().path() + File.separator).length(),
                relativePath.length());
          }
          if (relativePath.startsWith(File.separator + configuration.large().path() + File.separator)) {
            return relativePath.substring(
                (File.separator + configuration.large().path() + File.separator).length(),
                relativePath.length());
          }
          return relativePath;
        },
        sourceFolder);
    final List<String> names = new ArrayList<>();
    final Map<String, Set<String>> images = new HashMap<>();
    for (final String string : iterable) {
      final int start = string.lastIndexOf(File.separator);
      final int end = string.lastIndexOf(".png");
      final String name = string
          .substring(start == -1 ? 0 : start + 1, end)
          .toUpperCase()
          .replaceAll("-", "_")
          .replaceAll("\\.", "_")
          .replaceAll("\\+", "_");
      if (!images.containsKey(name)) {
        names.add(name);
        images.put(name, new HashSet<>());
      }
      images.get(name).add(string);
    }
    Collections.sort(names);
    final String iconsClassName = "GuiIcons";
    final String iconsPackageName = "net.anwiba.commons.swing.icon";
    final Icons icons = createIcons(
        configuration,
        iconsClassName,
        iconsPackageName,
        sourceFolder.getName(),
        names,
        images);
    write(icons, sourceFolder);
  }

  public void write(final Icons icons, final File sourceFolder) throws JAXBException {
    final JAXBContext jaxbContext = JAXBContext.newInstance(
        net.anwiba.tools.icons.configuration.generated.Icons.class,
        net.anwiba.tools.icons.configuration.generated.Class.class,
        net.anwiba.tools.icons.configuration.generated.IconSizes.class,
        net.anwiba.tools.icons.configuration.generated.IconSize.class,
        net.anwiba.tools.icons.configuration.generated.Icon.class);
    final Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.marshal(icons, new File(sourceFolder, "icons.xml")); //$NON-NLS-1$
  }

  public Icons createIcons(
      final IIconSizesConfiguration configuration,
      final String iconsClassName,
      final String iconsPackageName,
      final String folderName,
      final List<String> names,
      final Map<String, Set<String>> images) {

    final Folder folder = new Folder();
    folder.setName(folderName);
    final List<Icon> iconsList = folder.getIcon();
    final IconSizes sizes = new IconSizes();
    final IconSize small = new IconSize();
    small.setSize(configuration.small().size());
    small.setPath(configuration.small().path());
    sizes.setSmall(small);
    final IconSize medium = new IconSize();
    medium.setSize(configuration.medium().size());
    medium.setPath(configuration.medium().path());
    sizes.setMedium(medium);
    final IconSize large = new IconSize();
    large.setSize(configuration.large().size());
    large.setPath(configuration.large().path());
    sizes.setLarge(large);
    folder.setSizes(sizes);
    for (final String name : names) {
      final Set<String> paths = images.get(name);
      if (paths.size() != 1) {
        continue;
      }
      final Icon icon = new Icon();
      icon.setName(name);
      icon.setImage(paths.iterator().next());
      iconsList.add(icon);
    }
    final Icons icons = new Icons();
    final Class clazz = new Class();
    clazz.setName(iconsClassName);
    clazz.setPackage(iconsPackageName);
    icons.setClazz(clazz);
    icons.getFolder().add(folder);
    return icons;
  }

  public static void main(final String[] args) {
    final IconSizeConfiguration small = new IconSizeConfiguration(16, "16x16"); //$NON-NLS-1$
    final IconSizeConfiguration medium = new IconSizeConfiguration(22, "22x22"); //$NON-NLS-1$
    final IconSizeConfiguration large = new IconSizeConfiguration(32, "32x32"); //$NON-NLS-1$
    final IIconSizesConfiguration iconSizesConfiguration = new IconSizesConfiguration("gnome", small, medium, large);

    try {
      new IconsXmlWriter()
          .write(
              new File(
                  "/home/andreas/work/JGISShell/development/trunk/workspace/gnome-icons/src/main/resources/icons/gnome"),
              iconSizesConfiguration);

    } catch (final Exception exception) {
      // TODO_NOW: handle exception
    }
  }
}
