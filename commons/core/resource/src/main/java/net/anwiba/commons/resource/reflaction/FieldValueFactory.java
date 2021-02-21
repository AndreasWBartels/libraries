/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.resource.reflaction;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceHandler;
import net.anwiba.commons.reference.ResourceReferenceUtilities;
import net.anwiba.commons.reflection.ReflectionConstructorInvoker;

public final class FieldValueFactory {

  private static ILogger logger = Logging.getLogger(FieldValueFactory.class.getName());
  private static final ResourceReferenceFactory resourceReferenceFactory = new ResourceReferenceFactory();
  private final IResourceReferenceHandler resourceReferenceHandler = new ResourceReferenceHandler();

  @SuppressWarnings("unchecked")
  public Object create(
      final Class<?> type,
      final boolean isStatic,
      final URL url,
      final Charset charset)
      throws InvocationTargetException {
    try {
      final Class<?> clazz = Class.forName(type.getName());
      if (IResourceProvider.class.isAssignableFrom(clazz)) {
        final IResourceReference resourceReference = createResource(isStatic, url);
        return createResourceProvider((Class<? extends IResourceProvider>) clazz, resourceReference);
      }
      if (IResourceReference.class.equals(clazz)) {
        return createResource(isStatic, url);
      }
      final byte[] buffer = read(resourceReferenceFactory.create(url));
      final CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(buffer, 0, buffer.length));
      if (type.isArray()) {
        return createArray(charBuffer.toString());
      }
      return charBuffer.toString();
    } catch (final ClassNotFoundException | CreationException exception) {
      throw new InvocationTargetException(exception);
    }
  }

//  @SuppressWarnings("unchecked")
//  public Object create(
//      final ClassLoader classLoader,
//      final Class<?> type,
//      final boolean isStatic,
//      final String resourceUrl,
//      final Charset charset)
//      throws InvocationTargetException {
//    try {
//      final Class<?> clazz = Class.forName(type.getName());
//      if (IResourceProvider.class.isAssignableFrom(clazz)) {
//        final IResourceReference resourceReference = createResource(classLoader, isStatic, resourceUrl);
//        return createResourceProvider((Class<? extends IResourceProvider>) clazz, resourceReference);
//      }
//      if (IResourceReference.class.equals(clazz)) {
//        return createResource(classLoader, isStatic, resourceUrl);
//      }
//      final byte[] buffer = read(createResourceReference(classLoader, resourceUrl));
//      final CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(buffer, 0, buffer.length));
//      if (type.isArray()) {
//        return createArray(charBuffer.toString());
//      }
//      return charBuffer.toString();
//    } catch (final ClassNotFoundException | CreationException exception) {
//      throw new InvocationTargetException(exception);
//    }
//  }

//  private IResourceReference createResource(
//      final ClassLoader classLoader,
//      final boolean isStatic,
//      final String resourceUrl)
//      throws CreationException {
//    if (isStatic) {
//      final IResourceReference resourceReference = createResourceReference(classLoader, resourceUrl);
//      final byte[] buffer = read(resourceReference);
//      return resourceReferenceFactory
//          .create(
//              buffer,
//              this.resourceReferenceHandler.getContentType(resourceReference),
//              Charset.defaultCharset().name());
//    }
//    return createResourceReference(classLoader, resourceUrl);
//  }

  private IResourceReference createResource(
      final boolean isStatic,
      final URL resourceUrl)
      throws CreationException {
    if (isStatic) {
      final IResourceReference resourceReference = resourceReferenceFactory.create(resourceUrl);
      final byte[] buffer = read(resourceReference);
      return resourceReferenceFactory
          .create(
              buffer,
              this.resourceReferenceHandler.getContentType(resourceReference),
              Charset.defaultCharset().name());
    }
    return resourceReferenceFactory.create(resourceUrl);
  }

  private Object createResourceProvider(
      final Class<? extends IResourceProvider> clazz,
      final IResourceReference resourceReference)
      throws InvocationTargetException {
    if (clazz.equals(IByteArrayResourceProvider.class)) {
      return new ByteArrayResourceProvider(resourceReference);
    }
    final ReflectionConstructorInvoker<IResourceProvider> invoker =
        new ReflectionConstructorInvoker<>(clazz, IResourceReference.class);
    return invoker.invoke(resourceReference);
  }

  private String[] createArray(final String fieldValue) {
    final List<String> values = new ArrayList<>();
    final StringTokenizer tokenizer = new StringTokenizer(fieldValue.replace("\r\n", "\n").replace("\r", "\n"), "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    while (tokenizer.hasMoreElements()) {
      final String nextToken = tokenizer.nextToken().trim();
      if (nextToken == null || nextToken.isEmpty()) {
        continue;
      }
      values.add(nextToken);
    }
    return values.toArray(new String[values.size()]);
  }

  private byte[] read(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      final String message = "Error loading text resource, resource value is not defined"; //$NON-NLS-1$
      logger.log(ILevel.ERROR, message);
      throw new RuntimeException(message);
    }
    try (InputStream input = this.resourceReferenceHandler.openInputStream(resourceReference);) {
      return read(input);
    } catch (final Exception e) {
      final String message = MessageFormat
          .format("Error loading text resource ''{0}''", ResourceReferenceUtilities.toString(resourceReference)); //$NON-NLS-1$
      logger.log(ILevel.ERROR, message, e);
      throw new RuntimeException(message, e);
    }
  }

  private byte[] read(final InputStream inputStream) throws IOException {
    final byte[] array = new byte[1024];
    byte[] buffer = new byte[0];
    int length = 0;
    while ((length = inputStream.read(array)) > -1) {
      buffer = concat(buffer, array, length);
    }
    return buffer;
  }

//  private IResourceReference createResourceReference(final ClassLoader classLoader, final String resourceUrl)
//      throws CreationException {
//    if (resourceUrl.toLowerCase().startsWith("file:") || resourceUrl.toLowerCase().startsWith("http:")) { //$NON-NLS-1$ //$NON-NLS-2$
//      return resourceReferenceFactory.create(resourceUrl);
//    }
//    final ClassLoader loader = resourceUrl.getClass().getClassLoader();
//    final URL url = loader == null
//        ? ClassLoader.getSystemResource(resourceUrl)
//        : loader.getResource(resourceUrl);
//    if (url == null) {
//      URL resource = classLoader.getResource(resourceUrl);
//      return resource == null
//          ? null
//          : resourceReferenceFactory.create(resource);
//    }
//    return url == null
//        ? null
//        : resourceReferenceFactory.create(url);
//  }

  private byte[] concat(final byte[] buffer, final byte[] array, final int length) {
    final byte[] result = new byte[buffer.length + length];
    try {
      System.arraycopy(buffer, 0, result, 0, buffer.length);
      System.arraycopy(array, 0, result, buffer.length, length);
      return result;

    } catch (final RuntimeException exception) {
      throw exception;
    }
  }

}