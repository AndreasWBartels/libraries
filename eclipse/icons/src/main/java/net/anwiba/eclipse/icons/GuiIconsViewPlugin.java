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
