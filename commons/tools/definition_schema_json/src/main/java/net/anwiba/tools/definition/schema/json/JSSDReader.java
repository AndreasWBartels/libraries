/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.tools.definition.schema.json;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;

import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.AnnotationContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.AnnotationNameContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.ClassNameContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.CommentContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.DimensionContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.GenericsContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.JssdContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.MemberContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.MemberNameContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.NameContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.ObjectArrayContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.ObjectContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.ParameterContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.ParameterValueContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.TypeContext;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser.ValueContext;
import net.anwiba.tools.definition.schema.json.gramma.element.JAnnotation;
import net.anwiba.tools.definition.schema.json.gramma.element.JAnnotationBuilder;
import net.anwiba.tools.definition.schema.json.gramma.element.JDimension;
import net.anwiba.tools.definition.schema.json.gramma.element.JField;
import net.anwiba.tools.definition.schema.json.gramma.element.JFieldBuilder;
import net.anwiba.tools.definition.schema.json.gramma.element.JObject;
import net.anwiba.tools.definition.schema.json.gramma.element.JObjectBuilder;
import net.anwiba.tools.definition.schema.json.gramma.element.JParameter;
import net.anwiba.tools.definition.schema.json.gramma.element.JType;
import net.anwiba.tools.definition.schema.json.gramma.element.JTypeBuilder;
import net.anwiba.tools.definition.schema.json.gramma.element.JValue;
import net.anwiba.tools.definition.schema.json.gramma.parser.JSSDParserFactory;
import net.anwiba.tools.definition.schema.json.gramma.parser.JssdParserException;
import net.anwiba.tools.generator.java.bean.JavaConstants;

public class JSSDReader {

  public final class ErrorListener extends BaseErrorListener {

    final private List<RecognitionException> exceptions = new ArrayList<>();
    final private List<String> messages = new ArrayList<>();

    @Override
    public void syntaxError(
        final Recognizer<?, ?> recognizer,
        final Object offendingSymbol,
        final int line,
        final int charPositionInLine,
        final String msg,
        final RecognitionException exception) {
      Optional.ofNullable(exception).ifPresent(e -> this.exceptions.add(e));
      this.messages.add("line " + line + ":" + charPositionInLine + ", " + msg); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public void reportContextSensitivity(
        final Parser recognizer,
        final DFA dfa,
        final int startIndex,
        final int stopIndex,
        final int prediction,
        final ATNConfigSet configs) {
      this.messages
          .add("Context sensitivity violation, at " + startIndex + " until " + stopIndex + ", " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
              + recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex)));
    }

    @Override
    public void reportAmbiguity(
        final Parser recognizer,
        final DFA dfa,
        final int startIndex,
        final int stopIndex,
        final boolean exact,
        final BitSet ambigAlts,
        final ATNConfigSet configs) {
      this.messages
          .add("ambiguity, at " + startIndex + " until " + stopIndex + ", " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
              + recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex)));
    }

    public List<RecognitionException> getExceptions() {
      return this.exceptions;
    }

    public List<String> getMessages() {
      return this.messages;
    }

    public boolean isEmpty() {
      return this.messages.isEmpty();
    }
  }

  private final JSSDParserFactory factory = new JSSDParserFactory();

  public Iterable<JObject> read(final String text) throws IOException, JssdParserException {
    try (InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(text.getBytes("UTF-8")))) { //$NON-NLS-1$
      return _read(inputStream, "UTF-8"); //$NON-NLS-1$
    }
  }

  public Iterable<JObject> read(final InputStream inputStream, final String encoding)
      throws IOException,
      JssdParserException {
    return _read(inputStream, encoding);
  }

  private Iterable<JObject> _read(final InputStream byteArrayInputStream, final String encoding)
      throws JssdParserException,
      IOException {
    try {
      final ErrorListener errorListener = new ErrorListener();
      final JSSDParser parser = this.factory.create(byteArrayInputStream, encoding, errorListener);
      final JssdContext jssd = parser.jssd();
      if (!errorListener.isEmpty()) {
        final String message = StringUtilities.concatenatedString("\n", errorListener.getMessages()); //$NON-NLS-1$
        final JssdParserException conversionException = new JssdParserException(message);
        for (final RecognitionException recognitionException : errorListener.getExceptions()) {
          conversionException.addSuppressed(recognitionException);
        }
        throw conversionException;
      }
      final ArrayList<JObject> result = new ArrayList<>();
      if (jssd.object() != null) {
        final ObjectContext object = jssd.object();
        result.add(convert(object));
        return result;
      }
      final ObjectArrayContext objectarray = jssd.objectArray();
      if (objectarray != null) {
        for (final ObjectContext object : objectarray.object()) {
          result.add(convert(object));
        }
      }
      return result;
    } catch (final RecognitionException exception) {
      final JssdParserException conversionException = new JssdParserException(exception.getMessage());
      conversionException.initCause(exception);
      throw conversionException;
    }
  }

  private JObject convert(final ObjectContext object) {
    final JObjectBuilder builder = new JObjectBuilder();
    for (final AnnotationContext annotation : object.annotation()) {
      builder.add(convert(annotation));
    }
    builder.comment(toString(object.comment()));
    for (final MemberContext member : object.member()) {
      builder.add(convert(member));
    }
    return builder.build();
  }

  private JField convert(final MemberContext member) {
    final JFieldBuilder builder = new JFieldBuilder();
    for (final AnnotationContext annotation : member.annotation()) {
      builder.add(convert(annotation));
    }

    final MemberNameContext name = member.memberName();
    addTo(builder, member.comment());
    builder.name(convert(name));
    JType type = convert(member.type());
    builder.type(type);
    builder.value(convert(type, member.value()));

    return builder.build();
  }

  private void addTo(final JFieldBuilder builder, final CommentContext memberComment) {
    if (memberComment == null) {
      return;
    }
    builder.fieldComment(toString(memberComment));
//    builder.setterComment(toString(memberComment.setterComment()));
//    builder.getterComment(toString(memberComment.getterComment()));
  }

  private String toString(final RuleContext rule) {
    return rule == null ? null : rule.getText();
  }

  private String convert(final MemberNameContext name) {
    final String text = name.getText();
    return text.substring(1, text.length() - 1);
  }

  private JValue convert(final JType type, final ValueContext value) {
    if (value == null) {
      if (type.isArray()) {
        return null;
      }
      switch (type.name()) {
        case JavaConstants.BOOLEAN: {
          return new JValue(false);
        }
        case JavaConstants.SHORT:
        case JavaConstants.INT:
        case JavaConstants.LONG: {
          return new JValue(0);
        }
        case JavaConstants.FLOAT:
        case JavaConstants.DOUBLE: {
          return new JValue(0.);
        }
      }
      return null;
    }
    if (value.array() != null) {
      final ArrayList<Object> array = new ArrayList<>();
      for (final ValueContext valueContext : value.array().value()) {
        final JValue arrayValue = convert(type, valueContext);
        array.add(arrayValue.value());
      }
      return new JValue(array);
    }
    if (value.NUMBER() != null) {
      final String valueString = value.NUMBER().getText();
      if (valueString.indexOf('.') >= 0 || valueString.contains("E") || valueString.contains("e")) { //$NON-NLS-1$//$NON-NLS-2$
        return new JValue(Double.parseDouble(valueString));
      }
      return new JValue(Long.parseLong(valueString));
    }
    if (value.STRING() != null) {
      final String text = value.getText();
      return new JValue(text.substring(1, text.length() - 1));
    }
    switch (value.getText()) {
      case "false": { //$NON-NLS-1$
        return new JValue(false);
      }
      case "true": { //$NON-NLS-1$
        return new JValue(true);
      }
      case "null": { //$NON-NLS-1$
        return null;
      }
      default: {
        return new JValue(value.getText());
      }
    }
  }

  private JType convert(final TypeContext type) {
    final JTypeBuilder builder = new JTypeBuilder();
    builder.name(convert(type.className()));
    final GenericsContext generics = type.generics();
    if (generics != null) {
      for (final ClassNameContext classnameContext : generics.className()) {
        builder.generic(convert(classnameContext));
      }
    }
    for (final DimensionContext dimensionContext : type.dimension()) {
      builder.add(JDimension.valueOf(dimensionContext.getText()));
    }
    return builder.build();
  }

  private String convert(final ClassNameContext classname) {
    return classname.getText();
  }

  private JAnnotation convert(final AnnotationContext annotation) {
    final JAnnotationBuilder builder = new JAnnotationBuilder();
    final String name = convert(annotation.annotationName());
    builder.name(name);
    for (final ParameterContext parameter : annotation.parameter()) {
      builder.add(convert(parameter));
    }
    return builder.build();
  }

  private String convert(final AnnotationNameContext annotationname) {
    return annotationname.getText();
  }

  private JParameter convert(final ParameterContext parameter) {
    final String name = convert(parameter.name());
    final Object value = convert(parameter.parameterValue());
    return new JParameter(name, value);
  }

  private Object convert(final ParameterValueContext value) {
    if (value.NUMBER() != null) {
      final String valueString = value.NUMBER().getText();
      if (valueString.indexOf('.') >= 0 || valueString.contains("E") || valueString.contains("e")) { //$NON-NLS-1$//$NON-NLS-2$
        return Double.parseDouble(valueString);
      }
      return Long.parseLong(valueString);
    }
    if (value.STRING() != null) {
      final String text = value.getText();
      return text.substring(1, text.length() - 1);
    }
    switch (value.getText()) {
      case "false": { //$NON-NLS-1$
        return false;
      }
      case "true": { //$NON-NLS-1$
        return true;
      }
      case "null": { //$NON-NLS-1$
        return null;
      }
      default: {
        return value.getText();
      }
    }
  }

  private String convert(final NameContext name) {
    return name.getText();
  }
}
