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
package net.anwiba.commons.jdbc.resource;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.file.Path;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.ResolvingException;
import net.anwiba.commons.utilities.string.IStringAppender;
import net.anwiba.commons.utilities.string.StringAppender;
import net.anwiba.commons.utilities.string.StringResolverBuilder;

public class JdbcFileSystemUrlStringResolver implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final String PLACEHOLDER_PROTOCOL = "protocol"; //$NON-NLS-1$
  private static final String PLACEHOLDER_DATABASE = "database"; //$NON-NLS-1$
  private final String urlPattern;

  public JdbcFileSystemUrlStringResolver(final String urlPattern) {
    this.urlPattern = urlPattern;
  }

  public String create(final String protocol, final File database) throws CreationException {
    Ensure.ensureArgumentNotNull(database);
    final IStringAppender errorHandler = new StringAppender();
    try {
      return new StringResolverBuilder()
          .errorHandler(errorHandler)
          .add(PLACEHOLDER_PROTOCOL, protocol)
          .add(PLACEHOLDER_DATABASE, addFileProtocoll(database))
          .build()
          .resolve(this.urlPattern);
    } catch (final ResolvingException exception) {
      throw new CreationException(errorHandler.toString());
    } finally {
      if (!errorHandler.isEmpty()) {
        throw new CreationException(errorHandler.toString());
      }
    }
  }

  private String addFileProtocoll(File file) {
    return "file://" + file.toPath().normalize().toAbsolutePath().toString();
  }
}