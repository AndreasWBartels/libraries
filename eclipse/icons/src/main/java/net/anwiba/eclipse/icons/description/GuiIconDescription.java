/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class GuiIconDescription implements IGuiIconDescription {

  private final Device device;
  private final IConstant constant;
  private Image image;
  private final File smallIcon;
  private final File mediumIcon;
  private final File largeIcon;
  private final String source;

  public GuiIconDescription(
    final Device device,
    final IConstant constant,
    final File smallIcon,
    final File mediumIcon,
    final File largeIcon,
    final String source) {
    this.device = device;
    this.constant = constant;
    this.smallIcon = smallIcon;
    this.mediumIcon = mediumIcon;
    this.largeIcon = largeIcon;
    this.source = source;
  }

  @Override
  public IConstant getConstant() {
    return this.constant;
  }

  @Override
  public String getSource() {
    return this.source;
  }

  @Override
  public Image getImage() {
    if (this.image == null && getSmallIcon() != null) {
      try {
        this.image = new Image(this.device, new ImageData(getSmallIcon().getCanonicalPath()));
      } catch (final IOException exception) {
        this.image = null;
      }
    }
    return this.image;
  }

  @Override
  public void dispose() {
    if (this.image == null) {
      return;
    }
    this.image.dispose();
    this.image = null;
  }

  @Override
  public File getSmallIcon() {
    return this.smallIcon;
  }

  @Override
  public File getMediumIcon() {
    return this.mediumIcon;
  }

  @Override
  public File getLargeIcon() {
    return this.largeIcon;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof IGuiIconDescription) {
      final IGuiIconDescription other = (IGuiIconDescription) obj;
      return Arrays
          .equals(new Object[] { getConstant(), getSmallIcon(), getMediumIcon(), getLargeIcon() }, new Object[] {
              other.getConstant(), other.getSmallIcon(), other.getMediumIcon(), other.getLargeIcon() });
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] { getConstant(), getSmallIcon(), getMediumIcon(), getLargeIcon() });
  }
}
