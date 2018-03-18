// Copyright (c) 2016 by Andreas W. Bartels 

package net.anwiba.commons.swing.label;

import javax.swing.JLabel;

import net.anwiba.commons.lang.object.IObjectProvider;
import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public final class LabelUpdater<T> implements IChangeableObjectListener {
  private final JLabel label;
  private final IObjectProvider<T> model;
  private final IObjectToStringConverter<T> converter;

  public LabelUpdater(final JLabel label, final IObjectToStringConverter<T> converter, final IObjectProvider<T> model) {
    this.label = label;
    this.converter = converter;
    this.model = model;
  }

  @Override
  public void objectChanged() {
    final T value = this.model.get();
    GuiUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        LabelUpdater.this.label.setText(LabelUpdater.this.converter.toString(value));
      }
    });
  }
}
