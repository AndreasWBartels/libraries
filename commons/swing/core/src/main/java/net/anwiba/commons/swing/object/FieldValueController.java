/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels 
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

import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.functional.ICharFilter;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.primitive.IBooleanProvider;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public final class FieldValueController<T> {

  private boolean isDocumentListenerEnabled = true;
  private boolean isDocumentUpdateEnabled = true;
  private boolean isObjectListenerEnabled = true;
  private final PlainDocument document;
  private final IObjectModel<T> model;
  private final IConverter<String, T, RuntimeException> toObjectConverter;
  private final IConverter<T, String, RuntimeException> toStringConverter;
  private final IObjectModel<IValidationResult> validStateModel;
  private final IValidator<String> validator;
  private final IBooleanProvider isEditableProvider;
  final IConverter<PlainDocument, String, RuntimeException> documentToStringConverter = new IConverter<PlainDocument, String, RuntimeException>() {

    @Override
    public String convert(final PlainDocument plainDocument) throws RuntimeException {
      try {
        final String text = plainDocument.getText(0, plainDocument.getLength());
        if (characterFilter == null) {
          return text;
        }
        final StringBuilder builder = new StringBuilder();
        for (final char character : text.toCharArray()) {
          if (!FieldValueController.this.characterFilter.accept(character)) {
            continue;
          }
          builder.append(character);
        }
        return builder.toString();
      } catch (final BadLocationException exception) {
        return ""; //$NON-NLS-1$
      }
    }
  };
  private final ICharFilter characterFilter;

  public FieldValueController(
      final PlainDocument document,
      final IObjectModel<T> model,
      final IBooleanProvider isEditableProvider,
      final IConverter<String, T, RuntimeException> toObjectConverter,
      final IConverter<T, String, RuntimeException> toStringConverter,
      final IObjectModel<IValidationResult> validStateModel,
      final ICharFilter characterFilter,
      final IValidator<String> validator) {
    this.document = document;
    this.model = model;
    this.isEditableProvider = isEditableProvider;
    this.toObjectConverter = toObjectConverter;
    this.toStringConverter = toStringConverter;
    this.validStateModel = validStateModel;
    this.characterFilter = characterFilter;
    this.validator = validator;
  }

  public synchronized void documentChanged() {
    if (this.isEditableProvider.isTrue()) {
      if (this.isDocumentListenerEnabled) {
        try {
          this.isDocumentUpdateEnabled = false;
          updateModel();
        } finally {
          this.isDocumentUpdateEnabled = true;
        }
      }
    }
  }

  public void modelChanged() {
    if (this.isObjectListenerEnabled) {
      updateFieldText();
    }
  }

  public void format() {
    try {
      this.isObjectListenerEnabled = false;
      if (this.validStateModel.get().isValid()) {
        final String formatedText = this.toStringConverter.convert(this.model.get());
        if (getText().equals(formatedText)) {
          return;
        }
        setText(formatedText);
      }
    } finally {
      this.isObjectListenerEnabled = true;
    }
  }

  private synchronized void updateModel() {
    final String textValue = this.documentToStringConverter.convert(this.document);
    try {
      final IValidationResult validationResult = this.validator.validate(textValue);
      if (validationResult.isValid()) {
        this.model.set(this.toObjectConverter.convert(textValue));
      }
      this.validStateModel.set(validationResult);
    } catch (final Exception exception) {
      final String unsupportedValueMessage = "Unsupported input '" + textValue + "'"; //$NON-NLS-1$//$NON-NLS-2$
      final String message = exception.getMessage() == null
          ? unsupportedValueMessage
          : unsupportedValueMessage + "," + exception.getMessage(); //$NON-NLS-1$
      AbstractObjectTextField.logger.log(ILevel.ERROR, message, exception);
      this.validStateModel.set(IValidationResult.inValid(message));
    }
  }

  private synchronized void updateFieldText() {
    final T value = this.model.get();
    final String textValue = this.toStringConverter.convert(value);
    final String currentText = this.documentToStringConverter.convert(this.document);
    if (Objects.equals(textValue, currentText)) {
      return;
    }
    final IValidationResult validationResult = this.validator.validate(textValue);
    if (validationResult.isValid()) {
      setText(textValue);
    } else if (value == null) {
      setText(""); //$NON-NLS-1$
    }
    this.validStateModel.set(validationResult);
  }

  public synchronized String getText() {
    return this.documentToStringConverter.convert(this.document);
  }

  public synchronized void setText(final String textValue) {
    try {
      if (this.isDocumentUpdateEnabled) {
        try {
          this.isDocumentListenerEnabled = false;
          this.document.remove(0, this.document.getLength());
        } finally {
          this.isDocumentListenerEnabled = true;
        }
        this.document.insertString(0, textValue, null);
      }
    } catch (final BadLocationException exception) {
      throw new UnreachableCodeReachedException(exception);
    }
  }

}
