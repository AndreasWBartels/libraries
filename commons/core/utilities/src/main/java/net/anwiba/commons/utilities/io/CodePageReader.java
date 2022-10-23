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

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class CodePageReader {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(CodePageReader.class.getName());

  private final IResourceReferenceHandler resourceReferenceHandler;

  public CodePageReader(final IResourceReferenceHandler resourceReferenceHandler) {
    this.resourceReferenceHandler = resourceReferenceHandler;
  }

  public ICharEncoding read(final IResourceReference codepageResource, final ICharEncoding defaultEncoding) {
    if (codepageResource == null) {
      return defaultEncoding;
    }
    if (this.resourceReferenceHandler.canRead(codepageResource)) {
      try {
        final String content = this.resourceReferenceHandler.getContent(codepageResource);
        return Optional.ofNullable(CharEncoding.getByName(content.trim())).orElseGet(() -> defaultEncoding);
      } catch (final IOException exception) {
        logger
            .log(ILevel.DEBUG, "Couldn't read " + this.resourceReferenceHandler.toString(codepageResource), exception); //$NON-NLS-1$
        return defaultEncoding;
      }
    }
    return defaultEncoding;
  }

  public Charset read(final IResourceReference codepageResource, final Charset defaultCharset) {
    if (codepageResource == null) {
      return defaultCharset;
    }
    if (this.resourceReferenceHandler.canRead(codepageResource)) {
      try {
        final String content = this.resourceReferenceHandler.getContent(codepageResource);
        return Optional.ofNullable(Charset.forName(content.trim())).orElseGet(() -> defaultCharset);
      } catch (final IOException exception) {
        logger
            .log(ILevel.DEBUG, "Couldn't read " + this.resourceReferenceHandler.toString(codepageResource), exception); //$NON-NLS-1$
        return defaultCharset;
      }
    }
    return defaultCharset;
  }
}
