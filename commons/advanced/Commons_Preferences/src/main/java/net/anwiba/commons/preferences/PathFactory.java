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
package net.anwiba.commons.preferences;

final class PathFactory {

  private final String basePath;

  public PathFactory(final String... nodes) {
    if (nodes.length == 0) {
      this.basePath = null;
      return;
    }
    final StringBuilder builder = new StringBuilder();
    builder.append("/"); //$NON-NLS-1$ 
    builder.append(createPath(nodes));
    this.basePath = builder.toString();
  }

  public String create(final String... nodes) {
    if (nodes.length == 0) {
      return this.basePath == null ? "" : this.basePath; //$NON-NLS-1$
    }
    final StringBuilder builder = new StringBuilder();
    if (this.basePath != null) {
      builder.append(this.basePath);
      builder.append("/"); //$NON-NLS-1$ 
    }
    builder.append(createPath(nodes));
    return builder.toString();
  }

  private String createPath(final String... nodes) {
    final StringBuilder builder = new StringBuilder();
    boolean flag = false;
    for (final String node : nodes) {
      if (flag) {
        builder.append("/"); //$NON-NLS-1$ 
      }
      builder.append(cleanSplashes(node));
      flag = true;
    }
    return builder.toString();
  }

  private String cleanSplashes(final String string) {
    final StringBuilder builder = new StringBuilder();
    boolean flag = true;
    for (final char c : string.toCharArray()) {
      switch (c) {
        case '/': {
          if (flag) {
            break;
          }
          flag = true;
          builder.append(c);
          break;
        }
        default: {
          flag = false;
          builder.append(c);
        }
      }
    }
    return builder.toString();
  }
}