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
package net.anwiba.tools.icons.generator;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.anwiba.tools.icons.configuration.IIconSizesConfiguration;
import net.anwiba.tools.icons.configuration.IOutput;
import net.anwiba.tools.icons.configuration.IconResource;
import net.anwiba.tools.icons.configuration.generated.Class;

public class GuiIconsClassWriter implements Closeable {

  private final Writer writer;
  private final Class targetClass;
  private final Class iconClass;
  private final IOutput output;

  public GuiIconsClassWriter(final Writer writer, final Class iconClass, final Class targetClass, final IOutput output) {
    this.writer = writer;
    this.iconClass = iconClass;
    this.targetClass = targetClass;
    this.output = output;
  }

  public void write(final Map<String, String> folders, final Map<String, IconResource> configuration)
      throws IOException {
    final Set<String> set = configuration.keySet();
    final String[] names = set.toArray(new String[set.size()]);
    Arrays.sort(names);
    this.writer
        .append(MessageFormat
            .format(
                "// Copyright (c) {0} by Andreas W. Bartels (bartels@anwiba.net)\n", new SimpleDateFormat("yyyy").format(new Date()))); //$NON-NLS-1$//$NON-NLS-2$
    this.writer.append(MessageFormat.format("package {0};\n", this.targetClass.getPackage())); //$NON-NLS-1$
    this.writer.append("\n"); //$NON-NLS-1$
    this.writer
        .append(MessageFormat.format("import {0}.{1};\n", this.iconClass.getPackage(), this.iconClass.getName())); //$NON-NLS-1$
    this.writer.append("import net.anwiba.commons.swing.icon.IconSize;\n"); //$NON-NLS-1$
    this.writer.append("\n"); //$NON-NLS-1$
    this.writer.append("@SuppressWarnings(\"nls\")\n"); //$NON-NLS-1$
    this.writer.append(MessageFormat.format("public class {0} '{\n", this.targetClass.getName())); //$NON-NLS-1$
    this.writer.append("\n"); //$NON-NLS-1$

    for (final Map.Entry<String, String> folder : folders.entrySet()) {
      this.writer.append(MessageFormat.format(
          "  private final static String {0} = \"{1}\";\n", folder.getValue(), folder.getKey())); //$NON-NLS-1$
    }
    this.writer.append("\n"); //$NON-NLS-1$
    final List<String> referencingNames = new ArrayList<>();
    for (final String name : names) {
      final IconResource iconResource = configuration.get(name);
      if (Arrays.equals(new Object[]{ this.targetClass.getPackage(), this.targetClass.getName() }, new Object[]{
          iconResource.getClazz().getPackage(),
          iconResource.getClazz().getName() })) {
        if (iconResource.getReference() != null) {
          referencingNames.add(name);
          continue;
        }
        final IIconSizesConfiguration iconSizesConfiguration = iconResource.getIconSizesConfiguration();
        this.output.info("add icon '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        this.writer.append(MessageFormat.format("  public final static {0} {1} = new GuiIcon( //\n" //$NON-NLS-1$
            + "      IconSize.create({3},\"{4}/{2}\",{5}),\n" //$NON-NLS-1$
            + "      IconSize.create({3},\"{6}/{2}\",{7}),\n" //$NON-NLS-1$
            + "      IconSize.create({3},\"{8}/{2}\",{9}),\n" //$NON-NLS-1$
            + "      {10});\n", //$NON-NLS-1$
            this.iconClass.getName(),
            name,
            iconResource.getImage(),
            getParentPath(folders.get(iconSizesConfiguration.getFolder())),
            iconSizesConfiguration.small().path(),
            String.valueOf(iconSizesConfiguration.small().size()),
            iconSizesConfiguration.medium().path(),
            String.valueOf(iconSizesConfiguration.medium().size()),
            iconSizesConfiguration.large().path(),
            String.valueOf(iconSizesConfiguration.large().size()),
            String.valueOf(iconResource.isDecorator())));
        continue;
      }
      this.output.info("add icon '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
      if (iconResource.getReference() != null) {
        this.writer.append(MessageFormat.format("  public final static {0} {1} = {2}.{3}.{4};\n", //$NON-NLS-1$
            this.iconClass.getName(),
            name,
            iconResource.getClazz().getPackage(),
            iconResource.getClazz().getName(),
            iconResource.getReference()));
        continue;
      }
      this.writer.append(MessageFormat.format("  public final static {0} {1} = {2}.{3}.{1};\n", //$NON-NLS-1$
          this.iconClass.getName(),
          name,
          iconResource.getClazz().getPackage(),
          iconResource.getClazz().getName()));
    }

    for (final String name : referencingNames) {
      this.output.info("add icon '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
      final IconResource iconResource = configuration.get(name);
      this.writer.append(MessageFormat.format("  public final static {0} {1} = {2};\n", //$NON-NLS-1$
          this.iconClass.getName(),
          name,
          iconResource.getReference()));
    }
    this.writer.append("\n"); //$NON-NLS-1$
    this.writer.append("}\n"); //$NON-NLS-1$
    this.writer.flush();
  }

  private Object getParentPath(final String folder) {
    return folder == null ? "\"icons/\"" : "\"icons/\"+" + folder + "+\"/\""; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
  }

  @Override
  public void close() throws IOException {
    this.writer.close();
  }
}
