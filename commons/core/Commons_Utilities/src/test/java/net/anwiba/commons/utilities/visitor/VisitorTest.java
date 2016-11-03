/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.utilities.visitor;

import static net.anwiba.commons.utilities.visitor.VisitorTest.VisitorTestEnum.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.text.MessageFormat;
import java.util.Optional;

import net.anwiba.commons.lang.functional.IFunction;

import org.junit.Test;

public class VisitorTest {

  enum VisitorTestEnum {
    ONE, TWO, THREE, FOUR;
  }

  @Test
  public void closur() throws Exception {
    final ClosurVisitor<VisitorTestEnum, VisitorTestEnum, RuntimeException> visitor = EnumVisitors //
        .ifCase(() -> ONE, ONE)
        .ifCase(() -> THREE, TWO)
        .defaultCase(() -> FOUR);
    assertThat(visitor.accept(ONE), equalTo(ONE));
    assertThat(visitor.accept(TWO), equalTo(THREE));
    assertThat(visitor.accept(THREE), equalTo(FOUR));
    assertThat(visitor.accept(FOUR), equalTo(FOUR));
    assertThat(visitor.accept(null), equalTo(FOUR));
  }

  @SuppressWarnings("nls")
  @Test
  public void function() throws Exception {
    final FunctionVisitor<VisitorTestEnum, Integer, Integer, RuntimeException> visitor = EnumVisitors //
        .ifCase((IFunction<Integer, Integer, RuntimeException>) value -> value % 2, VisitorTestEnum.TWO)
        .ifCase(value -> value % 3, VisitorTestEnum.THREE)
        .defaultCase(value -> value % 4);
    assertThat(visitor.accept(ONE, 5), equalTo(1));
    assertThat(visitor.accept(TWO, 5), equalTo(1));
    assertThat(visitor.accept(THREE, 5), equalTo(2));
    assertThat(visitor.accept(FOUR, 5), equalTo(1));
    assertThat(visitor.accept(null, 5), equalTo(1));

    @SuppressWarnings("rawtypes")
    final FunctionSwitch<Class, Number, String, RuntimeException> classSwitch = ClassSwitches.create();
    classSwitch
        .ifCase(value -> MessageFormat.format("{0,number,+#;-#}", value), Integer.class)
        .ifCase(value -> MessageFormat.format("{0,number,+#.00;-#.00}", value), Double.class)
        .defaultCase(value -> MessageFormat.format("{0}", value));

    assertThat(classSwitch.switchTo(5000), equalTo("+5000"));
    assertThat(classSwitch.switchTo(5000.), equalTo("+5000.00"));
    assertThat(classSwitch.switchTo(5000f), equalTo("5,000"));

    final int value = 5;
    final FunctionSwitch<VisitorTestEnum, VisitorTestEnum, Integer, Exception> enumSwitch = EnumSwitches.create();
    enumSwitch.ifCase(i -> value, ONE) //
        .ifCase(i -> value * 2, TWO, THREE)
        .defaultCase(i -> 0);

    assertThat(enumSwitch.switchTo(ONE), equalTo(5));
    assertThat(enumSwitch.switchTo(TWO), equalTo(10));
    assertThat(enumSwitch.switchTo(THREE), equalTo(10));
    assertThat(enumSwitch.switchTo(FOUR), equalTo(0));

    final FunctionSwitch<String, Object, Integer, Exception> stringSwitch //
    = new FunctionSwitch<>(input -> Optional.of(input).map(Object::toString).get());
    stringSwitch.ifCase(i -> value, ONE.name()) //
        .ifCase(i -> value * 2, TWO.name())
        .defaultCase(i -> 0);

    assertThat(stringSwitch.switchTo(ONE.name()), equalTo(5));
    assertThat(stringSwitch.switchTo(TWO.name()), equalTo(10));
    assertThat(stringSwitch.switchTo(FOUR.name()), equalTo(0));
  }
}
