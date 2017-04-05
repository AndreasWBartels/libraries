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
package net.anwiba.commons.swing.frame;

import javax.swing.Action;
import javax.swing.KeyStroke;

public class KeyActionConfiguration implements IKeyActionConfiguration {

  private final String actionKey;
  private final KeyStroke keyStroke;
  private final Action action;

  public KeyActionConfiguration(final String actionKey, final KeyStroke keyStroke, final Action action) {
    this.actionKey = actionKey;
    this.keyStroke = keyStroke;
    this.action = action;
  }

  @Override
  public String getActionKey() {
    return this.actionKey;
  }

  @Override
  public KeyStroke getKeyStroke() {
    return this.keyStroke;
  }

  @Override
  public Action getAction() {
    return this.action;
  }

}
