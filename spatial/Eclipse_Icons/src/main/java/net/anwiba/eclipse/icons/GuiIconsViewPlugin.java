/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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
package net.anwiba.eclipse.icons;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class GuiIconsViewPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "net.anwiba.eclipse.icons"; //$NON-NLS-1$

  private static GuiIconsViewPlugin plugin;

  public GuiIconsViewPlugin() {
    // nothing to do
  }

  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static GuiIconsViewPlugin getDefault() {
    return plugin;
  }

  public static void log(final int level, final int code, final Throwable throwable) {
    getLogger().log(new Status(level, PLUGIN_ID, code, throwable.getLocalizedMessage(), throwable));
  }

  private static ILog getLogger() {
    return getDefault().getLog();
  }
}
