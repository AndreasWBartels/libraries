package net.anwiba.eclipse.icons.description;

import java.io.File;

import org.eclipse.swt.graphics.Image;

public interface IGuiIconDescription {

  IConstant getConstant();

  void dispose();

  Image getImage();

  File getLargeIcon();

  File getMediumIcon();

  File getSmallIcon();

  String getSource();

}
