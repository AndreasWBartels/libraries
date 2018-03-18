// Copyright (c) 2016 by Andreas W. Bartels

package net.anwiba.commons.swing.dialog;

import javax.swing.JDialog;

public interface IDialogsContainer {

  JDialog remove(Object identifier);

  boolean contains(Object identifier);

  JDialog get(Object identifier);

  void add(Object identifier, JDialog dialog);

}
