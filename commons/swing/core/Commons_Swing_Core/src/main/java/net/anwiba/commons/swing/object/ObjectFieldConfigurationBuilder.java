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

import net.anwiba.commons.lang.object.IObjectContainer;
import net.anwiba.commons.lang.object.ObjectContainer;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.object.ObjectFieldConfiguration.ObjectToStringConverter;
import net.anwiba.commons.swing.object.ObjectFieldConfiguration.StringToObjectConverter;
import net.anwiba.commons.utilities.validation.AllwaysValidValidator;
import net.anwiba.commons.utilities.validation.IValidationResult;

public class ObjectFieldConfigurationBuilder
    extends
    AbstractObjectFieldConfigurationBuilder<Object, ObjectFieldConfigurationBuilder> {

  public ObjectFieldConfigurationBuilder() {
    this(new ObjectContainer<>());
  }

  private ObjectFieldConfigurationBuilder(final IObjectContainer<Object> broker) {
    super(new AllwaysValidValidator(), new StringToObjectConverter(broker), new ObjectToStringConverter(broker));
    setEditable(false);
    setValidStateModel(new ObjectModel<>(IValidationResult.valid()));
  }
}
