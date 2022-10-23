/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2015 by Andreas W. Bartels

package net.anwiba.commons.utilities.io;

import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.lang.parameter.Parameters;
import net.anwiba.commons.lang.parameter.ParametersBuilder;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

public class DefinitionReader {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(DefinitionReader.class.getName());

  private final IResourceReferenceHandler resourceReferenceHandler;

  public DefinitionReader(final IResourceReferenceHandler resourceReferenceHandler) {
    this.resourceReferenceHandler = resourceReferenceHandler;
  }

  public IParameters read(final IResourceReference definitionResource, final Charset charset) {
    if (definitionResource == null) {
      return Parameters.empty();
    }
    if (this.resourceReferenceHandler.canRead(definitionResource)) {
      try (Reader reader =
          new InputStreamReader(this.resourceReferenceHandler.openInputStream(definitionResource), charset)) {
        final ParametersBuilder builder = Parameters.builder();
        final Properties properties = new Properties();
        properties.load(reader);
        properties.entrySet().forEach(e -> builder.add((String) e.getKey(), (String) e.getValue()));
        return builder.build();
      } catch (final IOException exception) {
        logger
            .log(ILevel.DEBUG,
                "Couldn't read " + this.resourceReferenceHandler.toString(definitionResource), //$NON-NLS-1$
                exception);
        return Parameters.empty();
      }
    }
    return Parameters.empty();
  }
}
