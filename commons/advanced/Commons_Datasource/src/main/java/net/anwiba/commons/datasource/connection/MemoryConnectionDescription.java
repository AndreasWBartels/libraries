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
package net.anwiba.commons.datasource.connection;

import java.awt.datatransfer.DataFlavor;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.datasource.DataSourceVersion;
import net.anwiba.commons.lang.optional.Optional;

public class MemoryConnectionDescription implements IMemoryConnectionDescription {

  private final ZonedDateTime timeStamp;
  private static final long serialVersionUID = 1L;
  private final Object content;
  private final String mimeType;

  public MemoryConnectionDescription(final Serializable content) {
    this(content, null);
  }

  @SuppressWarnings("rawtypes")
  public MemoryConnectionDescription(final Serializable content, final ZonedDateTime timeStamp) {
    this(
        content,
        MessageFormat.format(
            "{0};class=\"{1}\"", //$NON-NLS-1$
            DataFlavor.javaJVMLocalObjectMimeType,
            Optional.of(content).convert(c -> (Class) c.getClass()).getOr(() -> Object.class)),
        timeStamp);
  }

  public MemoryConnectionDescription(final Serializable content, final String mimeType, final ZonedDateTime timeStamp) {
    this.content = content;
    this.mimeType = mimeType;
    this.timeStamp = Optional.of(timeStamp).getOr(() -> ZonedDateTime.now());
  }

  @Override
  public String getMimeType() {
    return this.mimeType;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getContent() {
    return (T) this.content;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Class<?> getContentClass() {
    return Optional.of(this.content).convert(c -> (Class) c.getClass()).getOr(() -> Object.class);
  }

  @Override
  public URI getURI() {
    try {
      return new URI(getUrl());
    } catch (final URISyntaxException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public String getUrl() {
    return "memory:/" + this.timeStamp.format(DateTimeFormatter.ISO_INSTANT); //$NON-NLS-1$
  }

  @Override
  public ZonedDateTime getTimeStamp() {
    return this.timeStamp;
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof IMemoryConnectionDescription;
  }

  @Override
  public int hashCode() {
    return getUrl().hashCode();
  }

  @Override
  public DataSourceType getDataSourceType() {
    return DataSourceType.MEMORY;
  }

  @Override
  public String getFormat() {
    return getDataSourceType().name();
  }

  @Override
  public DataSourceVersion getVersion() {
    return new DataSourceVersion(1, 0, null);
  }
}
