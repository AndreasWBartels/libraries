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

import java.util.Map;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.ResolvingException;
import net.anwiba.commons.lang.map.HasMapBuilder;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.string.IStringAppender;
import net.anwiba.commons.utilities.string.StringAppender;
import net.anwiba.commons.utilities.string.StringResolverBuilder;

public class JdbcServiceUrlStringResolver {

  private static final String PLACEHOLDER_PROTOCOL = "protocol"; //$NON-NLS-1$
  private static final String PLACEHOLDER_DATABASE = "database"; //$NON-NLS-1$
  private static final String PLACEHOLDER_PORT = "port"; //$NON-NLS-1$
  private static final String PLACEHOLDER_HOST = "host"; //$NON-NLS-1$
  private final String urlPattern;
  private final Map<String, String> replacements;

  public JdbcServiceUrlStringResolver(final String urlPattern) {
    this(urlPattern, new HasMapBuilder<String, String>().build());
  }

  public JdbcServiceUrlStringResolver(final String urlPattern, final Map<String, String> replacements) {
    this.urlPattern = urlPattern;
    this.replacements = replacements;
  }

  public String create(final String protocol, final String host, final String port, final String database)
      throws CreationException {
    final IStringAppender errorHandler = new StringAppender();
    try {
      final StringResolverBuilder builder = new StringResolverBuilder();
      Optional.of(host).consume(v -> builder.add(PLACEHOLDER_HOST, v));
      Optional.of(port).consume(v -> builder.add(PLACEHOLDER_PORT, v));
      this.replacements.forEach((k, v) -> builder.add(k, v));
      return builder
          .errorHandler(errorHandler)
          .optional(PLACEHOLDER_PROTOCOL, protocol)
          .add(PLACEHOLDER_DATABASE, database)
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
}