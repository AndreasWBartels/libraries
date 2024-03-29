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
package net.anwiba.tools.icons.generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import net.anwiba.tools.icons.configuration.IIconSizesConfiguration;
import net.anwiba.tools.icons.configuration.IOutput;
import net.anwiba.tools.icons.configuration.IconResource;
import net.anwiba.tools.icons.configuration.IconSizeConfiguration;
import net.anwiba.tools.icons.configuration.IconSizesConfiguration;
import net.anwiba.tools.icons.schema.configuration.Class;

import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class GuiIconsClassWriterTest {

  @Test
  public void write() throws IOException {
    final StringWriter stringWriter = new StringWriter();
    final Class iconClass = getIconClass();
    final Class targetClass = getTargetClass();
    try (GuiIconsClassWriter classWriter = new GuiIconsClassWriter(
        stringWriter,
        iconClass,
        targetClass,
        "// Copyright (c) ${year} by Andreas W. Bartels (bartels@anwiba.net)\n", //$NON-NLS-1$
        new IOutput() {

          @Override
          public void warn(final String message) {
            System.err.println(message);
          }

          @Override
          public void info(final String message) {
            System.out.println(message);
          }

          @Override
          public void error(final String message) {
            System.err.println(message);
          }

          @Override
          public void error(String message, Throwable throwable) {
            System.err.println(message);
            throwable.printStackTrace(System.err);
          }
        })) {
      final HashMap<String, String> folders = new HashMap<>();
      folders.put("misc", "MISC"); //$NON-NLS-1$//$NON-NLS-2$
      classWriter.write(folders, getConfiguration(targetClass));
    }
    assertThat(stringWriter.toString(), equalTo(getClassText()));
  }

  private Class getTargetClass() {
    final Class clazz = new Class();
    clazz.setName("GuiIcons"); //$NON-NLS-1$
    clazz.setPackage("net.anwiba.gui.icon"); //$NON-NLS-1$
    return clazz;
  }

  private Class getIconClass() {
    final Class clazz = new Class();
    clazz.setName("GuiIcon"); //$NON-NLS-1$
    clazz.setPackage("net.anwiba.commons.swing.icon"); //$NON-NLS-1$
    return clazz;
  }

  private Class getReferenzeClazz() {
    final Class clazz = new Class();
    clazz.setName("MediaGuiIcons"); //$NON-NLS-1$
    clazz.setPackage("net.anwiba.media.gui.icon"); //$NON-NLS-1$
    return clazz;
  }

  private String getClassText() {
    final StringBuilder builder = new StringBuilder();
    builder.append(MessageFormat.format(
        "// Copyright (c) {0} by Andreas W. Bartels (bartels@anwiba.net)\n", //$NON-NLS-1$
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")))); //$NON-NLS-1$
    builder.append("package net.anwiba.gui.icon;\n"); //$NON-NLS-1$
    builder.append("\n"); //$NON-NLS-1$
    builder.append("import net.anwiba.commons.swing.icon.GuiIcon;\n"); //$NON-NLS-1$
    builder.append("import net.anwiba.commons.swing.icon.IconSize;\n"); //$NON-NLS-1$
    builder.append("import java.net.URL;\n"); //$NON-NLS-1$
    builder.append("import java.util.function.Function;"); //$NON-NLS-1$
    builder.append("\n"); //$NON-NLS-1$
    builder.append("public class GuiIcons {\n"); //$NON-NLS-1$
    builder.append("\n"); //$NON-NLS-1$
    builder.append("  private static class Helper implements Function<String, URL> {\n");
    builder.append("    public URL apply(String path) {\n");
    builder.append("      return getClass().getResource(path);\n");
    builder.append("    }\n");
    builder.append("  }\n");
    builder.append("\n"); //$NON-NLS-1$
    builder.append("  private static Helper _h = new Helper();\n");
    builder.append("\n"); //$NON-NLS-1$
    builder.append("  private final static String _FOLDER = \"net/anwiba/gui/icon/icons/\";\n"); //$NON-NLS-1$
    builder.append("  private final static String _MISC = _FOLDER+\"misc/\";\n"); //$NON-NLS-1$
    builder.append("\n"); //$NON-NLS-1$

    builder.append("  public final static GuiIcon CANCEL_ICON = GuiIcon.of(_h,//\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_FOLDER,\"small/cancel.png\",16),\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_FOLDER,\"medium/cancel.png\",22),\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_FOLDER,\"large/cancel.png\",32));\n"); //$NON-NLS-1$

    builder.append("  public final static GuiIcon EXIT_ICON = net.anwiba.media.gui.icon.MediaGuiIcons.FINISH_ICON;\n"); //$NON-NLS-1$

    builder.append("  public final static GuiIcon MISC_ICON = GuiIcon.of(_h,//\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_MISC,\"small/gear.png\",16),\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_MISC,\"medium/gear.png\",22),\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_MISC,\"large/gear.png\",32));\n"); //$NON-NLS-1$

    builder.append("  public final static GuiIcon RELOAD_ICON = GuiIcon.of(_h,//\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_FOLDER,\"small/exit.png\",16),\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_FOLDER,\"medium/exit.png\",22),\n"); //$NON-NLS-1$
    builder.append("      IconSize.of(_FOLDER,\"large/exit.png\",32));\n"); //$NON-NLS-1$

    builder.append("  public final static GuiIcon START_ICON = net.anwiba.media.gui.icon.MediaGuiIcons.START_ICON;\n"); //$NON-NLS-1$

    builder.append("  public final static GuiIcon STOP_ICON = net.anwiba.media.gui.icon.MediaGuiIcons.STOP_ICON;\n"); //$NON-NLS-1$

    builder.append("  public final static GuiIcon WORK_ICON = MISC_ICON;\n"); //$NON-NLS-1$

    builder.append("\n"); //$NON-NLS-1$
    builder.append("}\n"); //$NON-NLS-1$
    return builder.toString();
  }

  private Map<String, IconResource> getConfiguration(final Class clazz) {
    final IconSizeConfiguration small = new IconSizeConfiguration(16, "small"); //$NON-NLS-1$
    final IconSizeConfiguration medium = new IconSizeConfiguration(22, "medium"); //$NON-NLS-1$
    final IconSizeConfiguration large = new IconSizeConfiguration(32, "large"); //$NON-NLS-1$
    final IIconSizesConfiguration iconSizesConfiguration = new IconSizesConfiguration(null, small, medium, large);
    final HashMap<String, IconResource> map = new HashMap<>();
    map.put(
        "MISC_ICON", //$NON-NLS-1$
        new IconResource(
            new IconSizesConfiguration("misc", small, medium, large), //$NON-NLS-1$
            "MISC_ICON", //$NON-NLS-1$
            "gear.png", //$NON-NLS-1$
            null,
            clazz,
            false));
    map.put("WORK_ICON", new IconResource(iconSizesConfiguration, "WORK_ICON", null, "MISC_ICON", clazz, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    map.put("CANCEL_ICON", new IconResource(iconSizesConfiguration, "CANCEL_ICON", "cancel.png", null, clazz, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    map.put(
        "EXIT_ICON", //$NON-NLS-1$
        new IconResource(iconSizesConfiguration, "EXIT_ICON", null, "FINISH_ICON", getReferenzeClazz(), false)); //$NON-NLS-1$ //$NON-NLS-2$
    map.put("RELOAD_ICON", new IconResource(iconSizesConfiguration, "RELOAD_ICON", "exit.png", null, clazz, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    map.put("STOP_ICON", new IconResource(iconSizesConfiguration, "STOP_ICON", null, null, getReferenzeClazz(), false)); //$NON-NLS-1$ //$NON-NLS-2$
    map.put(
        "START_ICON", //$NON-NLS-1$
        new IconResource(iconSizesConfiguration, "START_ICON", null, null, getReferenzeClazz(), false)); //$NON-NLS-1$

    // map
    // .put(
    // "NEWS_SUBSCRIBE", new IconResource(iconSizesConfiguration, "NEWS_SUBSCRIBE", null, "FOO_SUBSCRIBE",
    // getReferenzeClazz(), false)); //$NON-NLS-1$ //$NON-NLS-2$
    return map;
  }
}