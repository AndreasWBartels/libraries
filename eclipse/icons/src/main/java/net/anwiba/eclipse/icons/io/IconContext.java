package net.anwiba.eclipse.icons.io;

import net.anwiba.tools.icons.configuration.IconResource;

import java.io.File;

public class IconContext {

  private final File iconsPath;
  private final IconResource resource;
  private final String source;

  public IconContext(final String source, final File iconsPath, final IconResource resource) {
    this.source = source;
    this.iconsPath = iconsPath;
    this.resource = resource;
  }

  public String getSource() {
    return this.source;
  }

  public File getIconsPath() {
    return this.iconsPath;
  }

  public IconResource getResource() {
    return this.resource;
  }

}
