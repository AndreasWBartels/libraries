// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.icons.description;

import net.anwiba.eclipse.icons.io.IconContext;
import net.anwiba.tools.icons.configuration.IIconSizesConfiguration;
import net.anwiba.tools.icons.schema.configuration.Class;

import java.io.File;

import org.eclipse.swt.graphics.Device;

public class GuiIconDescriptionFactory {

  private final Device device;

  public GuiIconDescriptionFactory(final Device device) {
    this.device = device;
  }

  public IGuiIconDescription create(final Class clazz, final IconContext iconContext) {
    final Constant constant = new Constant(clazz.getPackage(), clazz.getName(), iconContext.getResource().getName());
    final IIconSizesConfiguration iconSizesConfiguration = iconContext.getResource().getIconSizesConfiguration();
    final String folder = iconSizesConfiguration.getFolder();
    final File iconsPath = folder == null
        ? iconContext.getIconsPath()
        : new File(iconContext.getIconsPath(), folder);
    final String imageName = iconContext.getResource().getImage();
    return create(constant, iconsPath, iconSizesConfiguration, imageName, iconContext.getSource());
  }

  public IGuiIconDescription create(
      final Class clazz,
      final IconContext iconContext,
      final IconContext referenzedContext) {
    final Constant constant = new Constant(clazz.getPackage(), clazz.getName(), iconContext.getResource().getName());
    final IIconSizesConfiguration iconSizesConfiguration = referenzedContext.getResource().getIconSizesConfiguration();
    final String folder = iconSizesConfiguration.getFolder();
    final File iconsPath = folder == null
        ? referenzedContext.getIconsPath()
        : new File(referenzedContext.getIconsPath(), folder);
    final String imageName = referenzedContext.getResource().getImage();
    if (imageName == null) {
      return null;
    }
    return create(constant, iconsPath, iconSizesConfiguration, imageName, iconContext.getSource());
  }

  private IGuiIconDescription create(
      final Constant constant,
      final File iconsPath,
      final IIconSizesConfiguration iconSizesConfiguration,
      final String imageName,
      final String source) {
    final File smallIcon = create(iconsPath, iconSizesConfiguration.small().path(), imageName);
    final File mediumIcon = create(iconsPath, iconSizesConfiguration.medium().path(), imageName);
    final File largeIcon = create(iconsPath, iconSizesConfiguration.large().path(), imageName);
    return new GuiIconDescription(this.device, constant, smallIcon, mediumIcon, largeIcon, source);
  }

  private File create(final File iconsPath, final String folder, final String image) {
    // if (folder == null) {
    // System.out.println();
    // }
    // if (iconsPath == null) {
    // System.out.println();
    // }
    // if (image == null) {
    // System.out.println();
    // }
    final File file = new File(new File(iconsPath, folder), image);
    if (!file.exists()) {
      return null;
    }
    return file;
  }
}