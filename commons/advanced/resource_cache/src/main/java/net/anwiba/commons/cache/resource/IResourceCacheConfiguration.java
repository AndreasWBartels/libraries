/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.cache.resource;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.reference.utilities.ContentType;
import net.anwiba.commons.reference.utilities.IFileExtensions;

public interface IResourceCacheConfiguration {

  default Duration getLatencyTime() {
    return Duration.ofSeconds(5);
  }

  default Duration getMaximumAgeOnStartupTime() {
    return getCachingRule().getPreferedLifeTime().getDuration().multipliedBy(2);
  }

  default boolean isCleanUpOnStartEnabled() {
    return getCachingFolder().isAccepted();
  }

  ICachingRule getCachingRule();

  default String getExtensionFor(final String contentType) {
    return ContentType.getByValue(contentType)
        .convert(c -> c.getDefaultFileExtension()
//            .getOr(Optional.of(c.getPrimaryType())
//                .convert(p -> p.getDefaultFileExtension())
                .getOr(() -> IFileExtensions.BDF))
        .getOr(() -> IFileExtensions.BDF);
  }

  String getName();

  IOptional<File, IOException> getCachingFolder();

  boolean isApplicable(Object key, String contentType);
}
