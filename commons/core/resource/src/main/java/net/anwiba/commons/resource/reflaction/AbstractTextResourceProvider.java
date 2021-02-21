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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.ResourceReferenceHandler;

public abstract class AbstractTextResourceProvider extends AbstractResourceProvider implements
    IByteArrayResourceProvider {
  private final IResourceReferenceHandler resourceReferenceHandler = new ResourceReferenceHandler();

  public AbstractTextResourceProvider(final IResourceReference resourceReference) {
    super(resourceReference);
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof IByteArrayResourceProvider)) {
      return false;
    }
    final IByteArrayResourceProvider other = (IByteArrayResourceProvider) obj;
    return Arrays.equals(getBytes(), other.getBytes());
  }

  private byte[] read() {
    try (InputStream inputStream = this.resourceReferenceHandler.openInputStream(getResource())) {
      final byte[] array = new byte[1024];
      byte[] buffer = new byte[0];
      int length = 0;
      while ((length = inputStream.read(array)) > -1) {
        buffer = concat(buffer, array, length);
      }
      return buffer;
    } catch (final IOException exception) {
      final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
      final PrintStream outputStream = new PrintStream(arrayOutputStream);
      exception.printStackTrace(outputStream);
      return arrayOutputStream.toByteArray();
    }
  }

  private byte[] concat(final byte[] buffer, final byte[] array, final int length) {
    final byte[] result = new byte[buffer.length + length];
    System.arraycopy(buffer, 0, result, 0, buffer.length);
    System.arraycopy(array, 0, result, buffer.length, length);
    return result;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return toString("UTF-8"); //$NON-NLS-1$
  }

  @Override
  public String toString(final String encoding) {
    final byte[] buffer = read();
    return Charset.forName(encoding).decode(ByteBuffer.wrap(buffer, 0, buffer.length)).toString();
  }

  public static String getExtention() {
    return "txt"; //$NON-NLS-1$
  }

  @Override
  public byte[] getBytes() {
    return read();
  }
}