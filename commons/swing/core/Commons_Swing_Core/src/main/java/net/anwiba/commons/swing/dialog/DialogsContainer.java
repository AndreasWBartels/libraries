// Copyright (c) 2016 by Andreas W. Bartels

package net.anwiba.commons.swing.dialog;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

public class DialogsContainer implements IDialogsContainer {

  Map<Object, JDialog> dialogs = new HashMap<>();

  @Override
  public JDialog remove(final Object identifier) {
    return this.dialogs.remove(identifier);
  }

  @Override
  public boolean contains(final Object identifier) {
    return this.dialogs.containsKey(identifier);
  }

  @Override
  public JDialog get(final Object identifier) {
    return this.dialogs.get(identifier);
  }

  @Override
  public void add(final Object identifier, final JDialog dialog) {
    this.dialogs.put(identifier, dialog);
  }

}
