/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.generator.java.bean.factory;

import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.*;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import net.anwiba.tools.generator.java.bean.configuration.Bean;
import net.anwiba.tools.generator.java.bean.configuration.Member;

public class ConstructorFactory extends AbstractSourceFactory {

  private final EnsurePredicateFactory ensurePredicateFactory;
  private final boolean isNoneNullableValuesOnlyEnabled;

  public ConstructorFactory(
      final JCodeModel codeModel,
      final EnsurePredicateFactory ensurePredicateFactory,
      final boolean isNoneNullableValuesOnlyEnabled) {
    super(codeModel);
    this.ensurePredicateFactory = ensurePredicateFactory;
    this.isNoneNullableValuesOnlyEnabled = isNoneNullableValuesOnlyEnabled;
  }

  public void constructor(final Bean configuration, final JDefinedClass bean, final Iterable<JFieldVar> fields) {
    if (configuration.isMutable() && !configuration.isBuilderEnabled()) {
      return;
    }
    final JMethod constructor = bean.constructor(JMod.FINAL | JMod.PUBLIC);
    for (final JFieldVar field : fields) {
      final Member member = configuration.member(field.name());
      if (constructor == null
          || !member.setter().isEnabled()
          || (this.isNoneNullableValuesOnlyEnabled && member.isNullable())) {
        continue;
      }
      if (isInstanceOfMap(field.type())) {
        if (member.isNullable()) {
          if (configuration.isCollectionNullable()) {
            setMapParameters(constructor, field, false, createAddIfNullClearMapAndReturnClosure(constructor, null));
            return;
          }
          setMapParameters(constructor, field, false);
          return;
        }
        setMapParameters(
            constructor,
            field,
            false,
            createEnsureArgumentNotNullClosure(this.ensurePredicateFactory, constructor));
        return;
      }
      if (isInstanceOfList(field.type())) {
        if (member.isNullable()) {
          if (configuration.isCollectionNullable()) {
            addListParameter(constructor, field, false, createAddIfNullClearListAndReturnClosure(constructor, null));
            return;
          }
          addListParameter(constructor, field, false, createAddIfNullReturnClosure(constructor, null));
          return;
        }
        addListParameter(
            constructor,
            field,
            false,
            createEnsureArgumentNotNullClosure(this.ensurePredicateFactory, constructor));
        return;
      }
      if (member.isNullable()) {
        addObjectParameter(constructor, field);
        continue;
      }
      addObjectParameter(
          constructor,
          field,
          createEnsureArgumentNotNullClosure(this.ensurePredicateFactory, constructor));
    }
  }
}