/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.object;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.utilities.JTextComponentUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public abstract class AbstractObjectTextField<T> implements IObjectTextField<T> {

  static ILogger logger = Logging.getLogger(AbstractObjectTextField.class.getName());

  @SuppressWarnings("serial")
  public static final class TextField<T> extends JTextField {
    private final IObjectFieldConfiguration<T> configuration;
    private boolean isInititalized = false;
    private final IObjectDistributor<IValidationResult> validationResult;

    public TextField(
        final PlainDocument document,
        final IObjectFieldConfiguration<T> configuration,
        final IObjectDistributor<IValidationResult> validationResult) {
      super(document, null, configuration.getColumns());
      this.validationResult = validationResult;
      this.isInititalized = true;
      this.configuration = configuration;
      final ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
      if (configuration.getToolTipFactory() != null) {
        toolTipManager.registerComponent(this);
      }
    }

    @Override
    public void setBorder(final Border border) {
      super.setBorder(border);
    }

    @Override
    public void setDocument(final Document doc) {
      if (this.isInititalized) {
        throw new UnsupportedOperationException();
      }
      if (doc instanceof PlainDocument) {
        super.setDocument(doc);
        return;
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public PlainDocument getDocument() {
      return (PlainDocument) super.getDocument();
    }

    @Override
    public void setToolTipText(final String text) {
      super.setToolTipText(text);
      final ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
      if (text == null && this.configuration.getToolTipFactory() != null) {
        toolTipManager.registerComponent(this);
      }
    }

    @Override
    public String getToolTipText() {
      final IToolTipFactory toolTipFactory = this.configuration.getToolTipFactory();
      if (toolTipFactory == null) {
        return super.getToolTipText();
      }
      final String value = getText();
      final int columnWidth = getWidth();
      final double valueWidth = JTextComponentUtilities.getValueWidth(this, value);
      if (valueWidth > columnWidth - 2) {
        return toolTipFactory.create(this.validationResult.get(), value);
      }
      return toolTipFactory.create(this.validationResult.get(), null);
    }
  }

  private final IObjectModel<T> model;
  private final IObjectModel<IValidationResult> validStateModel;
  private final TextField<T> textField;
  private final JComponent component;
  private final IValidator<String> validator;
  private final IConverter<String, T, RuntimeException> toObjectConverter;
  private final IConverter<T, String, RuntimeException> toStringConverter;
  private final IActionNotifier actionNotifier;
  private FieldValueController<T> controller;

  public AbstractObjectTextField(final IObjectFieldConfiguration<T> configuration) {
    this.model = configuration.getModel();
    this.validStateModel = configuration.getValidationResultModel();
    this.validator = configuration.getValidator();
    this.toObjectConverter = configuration.getToObjectConverter();
    this.toStringConverter = configuration.getToStringConverter();
    final PlainDocument document = new PlainDocument();
    final TextField<T> field = new TextField<>(document, configuration, this.validStateModel);
    final IKeyListenerFactory<T> keyListenerFactory = configuration.getKeyListenerFactory();
    if (keyListenerFactory != null) {
      field.addKeyListener(keyListenerFactory.create(this.model, document));
    }

    this.textField = field;
    final Collection<IActionFactory<T>> actionFactorys = configuration.getActionFactorys();
    if (configuration.getBackgroundColor() != null) {
      this.textField.setBackground(configuration.getBackgroundColor());
    }
    if (actionFactorys.isEmpty()) {
      this.component = field;
    } else {
      final Border border = new MetalBorders.TextFieldBorder();
      field.getBorder();
      this.component = new JPanel(new BorderLayout());
      this.component.setBackground(field.getBackground());
      this.component.setBorder(new MetalBorders.TextFieldBorder());
      field.setBorder(BorderFactory.createEmptyBorder());
      this.component.add(field, BorderLayout.CENTER);
      final JPanel actionContainer = new JPanel();
      actionContainer.setBackground(field.getBackground());
      actionContainer.setBorder(BorderFactory.createEmptyBorder());
      int width = 0;
      int height = 0;
      for (final IActionFactory<T> actionFactory : actionFactorys) {
        final AbstractAction action = actionFactory.create(this.model, document, new IBlock<RuntimeException>() {

          @Override
          public void execute() throws RuntimeException {
            if (document.getLength() == 0) {
              return;
            }
            if (!AbstractObjectTextField.this.validStateModel.get().isValid()) {
              try {
                document.remove(0, document.getLength());
              } catch (final BadLocationException exception) {
                // nothing to do
              }
              return;
            }
            AbstractObjectTextField.this.model.set(null);
          }
        });
        final JButton buttom = new JButton(action);
        buttom.setBackground(field.getBackground());
        buttom.setBorder(BorderFactory.createEmptyBorder());
        width = width + buttom.getMinimumSize().width;
        height = Math.max(height, buttom.getMinimumSize().height);
        actionContainer.add(buttom);
      }
      actionContainer.setMinimumSize(new Dimension(width, height));
      actionContainer.setMaximumSize(new Dimension(width, height));
      this.component.add(actionContainer, BorderLayout.EAST);
      final Insets borderInsets = border.getBorderInsets(this.component);
      final int componentHeight = borderInsets.top + borderInsets.bottom + height;
      this.component.setMinimumSize(new Dimension(field.getMinimumSize().width + width, componentHeight));
      this.component.setPreferredSize(new Dimension(field.getPreferredSize().width + width, componentHeight + 10));
      this.component.setMaximumSize(new Dimension(Integer.MAX_VALUE, componentHeight + 10));
    }
    this.textField.setEditable(configuration.isEditable());
    this.actionNotifier = new IActionNotifier() {

      @Override
      public void removeActionListener(final ActionListener listener) {
        field.removeActionListener(listener);
      }

      @Override
      public void addActionListener(final ActionListener listener) {
        field.addActionListener(listener);
      }
    };

    final FieldValueController<T> valuesController = new FieldValueController<>(
        document,
        this.model,
        () -> this.textField.isEditable(),
        this.toObjectConverter,
        this.toStringConverter,
        this.validStateModel,
        this.validator);
    this.controller = valuesController;
    document.addDocumentListener(new DocumentListener() {

      private void documentChanged() {
        valuesController.documentChanged();
      }

      @Override
      public void removeUpdate(final DocumentEvent e) {
        //        logger.log(ILevel.DEBUG, "document changed"); //$NON-NLS-1$
        documentChanged();
      }

      @Override
      public void insertUpdate(final DocumentEvent e) {
        //        logger.log(ILevel.DEBUG, "document changed"); //$NON-NLS-1$
        documentChanged();
      }

      @Override
      public void changedUpdate(final DocumentEvent e) {
        //        logger.log(ILevel.DEBUG, "document changed"); //$NON-NLS-1$
        documentChanged();
      }
    });
    this.model.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        //        logger.log(ILevel.DEBUG, "document changed"); //$NON-NLS-1$
        AbstractObjectTextField.this.controller.modelChanged();
      }
    });
    this.textField.addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(final KeyEvent event) {
        if (event.getKeyChar() == 0x1b) {
          valuesController.modelChanged();
        }
      }

      @Override
      public void keyReleased(final KeyEvent event) {
        // nothing to do
      }

      @Override
      public void keyPressed(final KeyEvent event) {
        // nothing to do
      }
    });
    field.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent e) {
        valuesController.format();
      }

      @Override
      public void focusGained(final FocusEvent e) {
        // nothing to do
      }
    });
    valuesController.modelChanged();
    this.validStateModel.set(this.validator.validate(getText()));
  }

  @Override
  public IObjectModel<T> getModel() {
    return this.model;
  }

  public void setHorizontalAlignment(final int alignment) {
    this.textField.setHorizontalAlignment(alignment);
  }

  @Override
  public JComponent getComponent() {
    return this.component;
  }

  @Override
  public IObjectDistributor<IValidationResult> getValidationResultDistributor() {
    return this.validStateModel;
  }

  @Override
  public void setEditable(final boolean isEditable) {
    this.textField.setEditable(isEditable);
  }

  @Override
  public void setText(final String text) {
    this.controller.setText(text);
  }

  @Override
  public String getText() {
    return this.controller.getText();
  }

  public IActionNotifier getActionNotifier() {
    return this.actionNotifier;
  }

  public void selectAll() {
    this.textField.selectAll();
  }

  public IColorReciever getColorReciever() {
    return new IColorReciever() {

      @Override
      public void setForeground(final Color color) {
        AbstractObjectTextField.this.textField.setForeground(color);
      }

      @Override
      public void setBackground(final Color color) {
        AbstractObjectTextField.this.textField.setBackground(color);
      }
    };
  }

}
