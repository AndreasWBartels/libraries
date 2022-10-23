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
package net.anwiba.commons.reference.utilities;

public interface IFileExtensions {

  public static final String ASC = "asc"; //$NON-NLS-1$
  public static final String BMP = "bmp"; //$NON-NLS-1$
  public static final String BPW = "bpw"; //$NON-NLS-1$
  public static final String DAT = "dat"; //$NON-NLS-1$
  public static final String DGM = "dgm"; //$NON-NLS-1$
  public static final String GFW = "gfw"; //$NON-NLS-1$
  public static final String GIF = "gif"; //$NON-NLS-1$
  public static final String JAR = "jar"; //$NON-NLS-1$
  public static final String JGW = "jgw"; //$NON-NLS-1$
  public static final String JLAYER = "jlayer"; //$NON-NLS-1$
  public static final String JPG = "jpg"; //$NON-NLS-1$
  public static final String JPE = "jpe";
  public static final String JPEG = "jpeg"; //$NON-NLS-1$
  public static final String JPGW = "jpgw"; //$NON-NLS-1$
  public static final String KEY = "key"; //$NON-NLS-1$
  public static final String MAP = "map"; //$NON-NLS-1$
  public static final String MDB = "mdb"; //$NON-NLS-1$
  public static final String PGW = "pgw"; //$NON-NLS-1$
  public static final String PNG = "png"; //$NON-NLS-1$
  public static final String PNG32 = "png32"; //$NON-NLS-1$

  public static final String DEF = "def"; // shape format; the feature geometry itself
  public static final String CPG = "cpg"; // used to specify the code page (only for .dbf) for identifying the character encoding to be used
  public static final String PRJ = "prj"; // projection description, using a well-known text representation of coordinate reference systems

  public static final String SHP = "shp"; //$NON-NLS-1$
  public static final String SHP_XML = "shp.xml";
  public static final String DBF = "dbf"; //$NON-NLS-1$
  public static final String SBN = "sbn"; // a spatial index of the features
  public static final String SBX = "sbx"; // a spatial index of the features
  public static final String SHI = "shi"; // shapefile index with 2d bbox disy
  public static final String SHX = "shx"; // shape index format; a positional index of the feature geometry to allow seeking forwards and backwards quickly
  public static final String SBB = "sbb"; // shapefile index with 2d bbox
  public static final String QIX = "qix"; // an alternative quadtree spatial index used by MapServer and GDAL/OGR software

  public static final String SDAT = "sdat"; //$NON-NLS-1$
  public static final String SGRD = "sgrd"; //$NON-NLS-1$
  public static final String TFW = "tfw"; //$NON-NLS-1$
  public static final String TIF = "tif"; //$NON-NLS-1$
  public static final String TIFF = "tiff"; //$NON-NLS-1$
  public static final String TIFW = "tifw"; //$NON-NLS-1$
  public static final String WLD = "wld"; //$NON-NLS-1$
  public static final String XYZ = "xyz"; //$NON-NLS-1$
  public static final String ZIP = "zip"; //$NON-NLS-1$
  public static final String FLML = "flml"; //$NON-NLS-1$
  public static final String SQLITE = "sqlite"; //$NON-NLS-1$
  public static final String GPKG = "gpkg"; //$NON-NLS-1$
  public static final String GRIDTILES = "gridtiles"; //$NON-NLS-1$
  public static final String TOPOJSON = "topojson"; //$NON-NLS-1$
  public static final String GEOJSON = "geojson"; //$NON-NLS-1$
  public static final String ESRIGEOJSON = "esrijson"; //$NON-NLS-1$
  public static final String MBTILES = "mbtiles"; //$NON-NLS-1$
  public static final String SQL = "sql"; //$NON-NLS-1$
  public static final String JSON = "json"; //$NON-NLS-1$
  public static final String JSONP = "jsonp"; //$NON-NLS-1$

  public static final String SLD = "sld"; //$NON-NLS-1$
  public static final String SE = "se"; //$NON-NLS-1$
  public static final String SE_XML = "se_xml";

  public static final String JAVA = "java"; //$NON-NLS-1$
  public static final String JAVASCRIPT = "js"; //$NON-NLS-1$
  public static final String GROOVY = "groovy"; //$NON-NLS-1$
  public static final String LAYER = "layer"; //$NON-NLS-1$
  public static final String H2 = "mv.db";
  public static final String BACKUP = "backup";
  public static final String CSV = "csv";

  public static final String HTML = "html";
  public static final String HTM = "htm";
  public static final String SHTML = "shtm";
  public static final String GPX = "gpx";
  public static final String PDF = "pdf";
  public static final String TXT = "txt";
  public static final String SVG = "svg";
  public static final String XML = "xml";
  public static final String GML = "gml";
  public static final String XHTML = "xhtml";
  public static final String QPJ = "qpj";
  public static final String BDF = "bdf";
  public static final String JOB = "job";

}