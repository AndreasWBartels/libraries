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
package net.anwiba.commons.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.anwiba.commons.lang.functional.IClosableIterator;
import net.anwiba.commons.lang.functional.ICloseableConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IProcedure;

public class JsonObjectUtilities {

  public static <T> T unmarshall(final Class<T> clazz, final String string) throws IOException {
    final JsonObjectUnmarshaller<T> unmarshaller = new JsonObjectUnmarshaller<>(clazz);
    return unmarshaller.unmarshal(string);
  }

  public static <T> String marshall(final T bean) {
    try {
      @SuppressWarnings("unchecked")
      final JsonObjectMarshaller<T> marshaller = new JsonObjectMarshaller<>((Class<T>) bean.getClass(), false);
      final StringWriter outputStream = new StringWriter();
      marshaller.marshall(outputStream, bean);
      return outputStream.toString();
    } catch (final IOException exception) {
      throw new RuntimeException("Unreachable code reached", exception); //$NON-NLS-1$
    }
  }

  public static <I, O> IClosableIterator<O, IOException> iterator(
      final InputStream inputStream,
      final Class<I> clazz,
      final IProcedure<JsonParser, IOException> initializer,
      final IConverter<I, O, IOException> converter,
      final IProcedure<JsonParser, IOException> closer) {
    return new IClosableIterator<O, IOException>() {

      private boolean isClosed = false;
      private O value;
      private JsonParser parser;
      private MappingIterator<I> iterator;

      @Override
      public void close() throws IOException {
        check();
        this.isClosed = true;
        IOException exception = JsonObjectUtilities.close(() -> closer.execute(this.parser), null);
        exception = JsonObjectUtilities.close(this.iterator, exception);
        exception = JsonObjectUtilities.close(this.parser, exception);
        if (exception != null) {
          throw exception;
        }
      }

      @Override
      public boolean hasNext() throws IOException {
        check();
        if (this.value != null) {
          return true;
        }
        if (this.iterator == null) {
          initialize();
        }
        if (!this.iterator.hasNext()) {
          return false;
        }
        this.value = converter.convert(this.iterator.next());
        return true;
      }

      @Override
      public O next() throws IOException {
        check();
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        try {
          return this.value;
        } finally {
          this.value = null;
        }
      }

      private void check() throws IOException {
        if (this.isClosed) {
          throw new IOException("initializer is closed"); //$NON-NLS-1$
        }
      }

      private void initialize() throws IOException {
        this.parser = new JsonFactory().createParser(new InputStreamReader(inputStream, "utf-8")); //$NON-NLS-1$
        initializer.execute(this.parser);
        this.iterator = new ObjectMapper().readerFor(clazz).readValues(this.parser);
      }
    };
  }

  public static <I, O> ICloseableConsumer<I, Boolean, IOException> consumer(
      final OutputStream outputStream,
      final Class<O> clazz,
      final IProcedure<JsonGenerator, IOException> initializer,
      final IConverter<I, O, IOException> converter,
      final IProcedure<JsonGenerator, IOException> closer) {
    return new ICloseableConsumer<I, Boolean, IOException>() {

      private boolean isClosed = false;
      private JsonGenerator generator;
      private SequenceWriter writer;

      @Override
      public void close() throws IOException {
        check();
        this.isClosed = true;
        IOException exception = JsonObjectUtilities.close(() -> closer.execute(this.generator), null);
        exception = JsonObjectUtilities.close(this.writer, exception);
        exception = JsonObjectUtilities.close(this.generator, exception);
        if (exception != null) {
          throw exception;
        }
      }

      @Override
      public Boolean consume(final I object) throws IOException {
        check();
        if (this.generator == null) {
          initialize();
        }
        final O value = converter.convert(object);
        if (value == null) {
          return false;
        }
        this.writer.write(value);
        return true;
      }

      private void check() throws IOException {
        if (this.isClosed) {
          throw new IOException("consumer is closed"); //$NON-NLS-1$
        }
      }

      private void initialize() throws IOException {
        this.generator = new JsonFactory().createGenerator(outputStream).configure(Feature.AUTO_CLOSE_TARGET, false);
        initializer.execute(this.generator);
        this.writer = new ObjectMapper()
            .writerFor(clazz)
            .with(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)
            .writeValuesAsArray(this.generator);
      }
    };
  }

  private static IOException close(final Closeable closeable, final IOException exception) {
    if (closeable == null) {
      return exception;
    }
    try {
      closeable.close();
      return exception;
    } catch (final IOException ioException) {
      if (exception == null) {
        return ioException;
      }
      exception.addSuppressed(ioException);
      return exception;
    }
  }
}
