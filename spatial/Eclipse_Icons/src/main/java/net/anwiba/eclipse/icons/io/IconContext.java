/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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
