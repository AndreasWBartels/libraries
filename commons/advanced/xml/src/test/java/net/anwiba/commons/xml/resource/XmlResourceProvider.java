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
package net.anwiba.commons.xml.resource;

import java.io.IOException;

import org.dom4j.Document;

import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.ResourceReferenceHandler;
import net.anwiba.commons.resource.reflaction.AbstractTextResourceProvider;
import net.anwiba.commons.xml.dom.DocumentUtilities;

public class XmlResourceProvider extends AbstractTextResourceProvider {

  private final IResourceReferenceHandler resourceReferenceHandler = new ResourceReferenceHandler();

  public XmlResourceProvider(final IResourceReference resourceReference) {
    super(resourceReference);
  }

  public Document asDocument() throws IOException {
    return DocumentUtilities.read(this.resourceReferenceHandler.openInputStream(getResource()));
  }

  public static String getExtention() {
    return "xml"; //$NON-NLS-1$
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof XmlResourceProvider) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
