/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.image.graphic;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;

public interface IGraphics {

  void draw(Shape s);

  boolean drawImage(Image img, AffineTransform xform, ImageObserver obs);

  void drawImage(BufferedImage img, BufferedImageOp op, int x, int y);

  void drawRenderedImage(RenderedImage img, AffineTransform xform);

  void drawRenderableImage(RenderableImage img, AffineTransform xform);

  void drawString(String str, int x, int y);

  void drawString(String str, float x, float y);

  void drawString(AttributedCharacterIterator iterator, int x, int y);

  void drawString(AttributedCharacterIterator iterator, float x, float y);

  void drawGlyphVector(GlyphVector g, float x, float y);

  void fill(Shape s);

  boolean hit(Rectangle rect, Shape s, boolean onStroke);

  GraphicsConfiguration getDeviceConfiguration();

  void setComposite(Composite comp);

  void setPaint(Paint paint);

  void setStroke(Stroke s);

  Object getRenderingHint(Key hintKey);

  RenderingHints getRenderingHints();

  void translate(int x, int y);

  void translate(double tx, double ty);

  void rotate(double theta);

  void rotate(double theta, double x, double y);

  void scale(double sx, double sy);

  void shear(double shx, double shy);

  void transform(AffineTransform Tx);

  void setTransform(AffineTransform Tx);

  AffineTransform getTransform();

  Paint getPaint();

  Composite getComposite();

  void setBackground(Color color);

  Color getBackground();

  Stroke getStroke();

  void clip(Shape s);

  FontRenderContext getFontRenderContext();

  IGraphics create();

  Color getColor();

  void setColor(Color c);

  void setPaintMode();

  void setXORMode(Color c1);

  Font getFont();

  void setFont(Font font);

  FontMetrics getFontMetrics(Font f);

  Rectangle getClipBounds();

  void clipRect(int x, int y, int width, int height);

  void setClip(int x, int y, int width, int height);

  Shape getClip();

  void setClip(Shape clip);

  void copyArea(int x, int y, int width, int height, int dx, int dy);

  void drawLine(int x1, int y1, int x2, int y2);

  void fillRect(int x, int y, int width, int height);

  void clearRect(int x, int y, int width, int height);

  void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

  void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

  void drawOval(int x, int y, int width, int height);

  void fillOval(int x, int y, int width, int height);

  void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

  void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

  void drawPolyline(int[] xPoints, int[] yPoints, int nPoints);

  void drawPolygon(int[] xPoints, int[] yPoints, int nPoints);

  void fillPolygon(int[] xPoints, int[] yPoints, int nPoints);

  boolean drawImage(Image img, int x, int y, ImageObserver observer);

  boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer);

  boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer);

  boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer);

  boolean drawImage(
      Image img,
      int dx1,
      int dy1,
      int dx2,
      int dy2,
      int sx1,
      int sy1,
      int sx2,
      int sy2,
      ImageObserver observer);

  boolean drawImage(
      Image img,
      int dx1,
      int dy1,
      int dx2,
      int dy2,
      int sx1,
      int sy1,
      int sx2,
      int sy2,
      Color bgcolor,
      ImageObserver observer);

}
