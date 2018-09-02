/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.utilities.string;

import net.anwiba.commons.lang.collection.CollectionAcceptorBuilder;
import net.anwiba.commons.lang.functional.IAcceptor;

public class StringCollectionAcceptorBuilder extends CollectionAcceptorBuilder<String> {

  private boolean ignoreCase = false;

  public StringCollectionAcceptorBuilder ignoreCase() {
    this.ignoreCase = true;
    return this;
  }

  public StringCollectionAcceptorBuilder caseSensitive() {
    this.ignoreCase = false;
    return this;
  }

  public StringCollectionAcceptorBuilder accept(final String... values) {
    return accept(
        (IAcceptor<String>) value -> StringCollectionAcceptorBuilder.this.ignoreCase
            ? StringUtilities.containsIgnoreCase(value, values)
            : StringUtilities.contains(value, values));
  }

  @Override
  public StringCollectionAcceptorBuilder accept(final IAcceptor<String> acceptor) {
    super.accept(acceptor);
    return this;
  }

  @Override
  public StringCollectionAcceptorBuilder otherwise(final IAcceptor<String> otherwise) {
    super.otherwise(otherwise);
    return this;
  }
}
