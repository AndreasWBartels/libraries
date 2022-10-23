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
package net.anwiba.commons.reference.utilities;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PathUtilitiesTest {

  @Test
  @Disabled
  public void collectFolders() throws IOException {
    List<Path> folders = PathUtilities.collectFolders(Path.of(System.getProperty("user.home"), ".m2", "repository"),
        path -> {
          try {
            return Files.list(path)
                .filter(item -> item.toString().endsWith(".pom"))
                .map(item -> {
                  try {
                    return Files.readAttributes(item, BasicFileAttributes.class);
                  } catch (IOException exception) {
                    return (BasicFileAttributes) null;
                  }
                })
                .map(attribute -> attribute.lastAccessTime())
                .filter(time -> Duration.between(time.toInstant(), ZonedDateTime.now().toInstant())
                    .compareTo(Duration.ofDays(365)) > 0)
                .findFirst()
                .isPresent();
          } catch (IOException exception) {
            // nothing
          }
          return false;
        });
    //    for (Path folder : folders) {
    //      if (Files.exists(folder)) {
    //        PathUtilities.deleteIfExits(folder);
    //      }
    //    }
    assertThat(null, nullValue());
  }
}
