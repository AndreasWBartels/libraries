/*
 * #%L
 * anwiba commons swing
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

package net.anwiba.commons.swing.object;

import net.anwiba.commons.model.ObjectModel;

public class GenericObjectFieldBuilder<T>
    extends
    AbstractObjectFieldBuilder<T, GenericObjectFieldConfigurationBuilder<T>, GenericObjectFieldBuilder<T>> {

  public GenericObjectFieldBuilder() {
    super(new GenericObjectFieldConfigurationBuilder<T>(new ObjectModel<>()));
  }

  @Override
  protected AbstractObjectTextField<T> create(final IObjectFieldConfiguration<T> configuration) {
    return new GenericObjectField<>(configuration);
  }

}
