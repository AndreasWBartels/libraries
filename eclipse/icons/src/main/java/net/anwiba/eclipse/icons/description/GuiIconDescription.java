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