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
package net.anwiba.commons.utilities.io.number;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.lang.io.FilteringInputStream;
import net.anwiba.commons.lang.io.filter.IFilteringInputStreamValidator;
import net.anwiba.commons.lang.io.filter.PatternFilter;
import net.anwiba.commons.lang.queue.IntValueQueue;
import net.anwiba.commons.reference.utilities.IoUtilities;

@SuppressWarnings("resource")
public class FilteringInputStreamTest {

  static final class FiveFilteringValidator implements IFilteringInputStreamValidator {
    private final IntValueQueue queue = new IntValueQueue();

    @Override
    public boolean accept(final int value) {
      if (value != '5') {
        this.queue.add(value);
        return true;
      }
      return false;
    }

    @Override
    public IntValueQueue getQueue() {
      return this.queue;
    }
  }

  static final class NeutralValidator implements IFilteringInputStreamValidator {
    private final IntValueQueue queue = new IntValueQueue();

    @Override
    public boolean accept(final int value) {
      this.queue.add(value);
      return true;
    }

    @Override
    public IntValueQueue getQueue() {
      return this.queue;
    }
  }

  @Test
  public void read() throws IOException {
    final String value = "0123456789"; //$NON-NLS-1$
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(value.getBytes()),
        new NeutralValidator());
    assertThat(read(inputStream), equalTo(value));
  }

  @Test
  public void filtering() throws IOException {
    final String value = "0123456789"; //$NON-NLS-1$
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(value.getBytes()),
        new FiveFilteringValidator());
    assertThat(read(inputStream), equalTo("012346789")); //$NON-NLS-1$
  }

  @Test
  public void matching456() throws IOException {
    final String value = "0123456789"; //$NON-NLS-1$
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(value.getBytes()),
        new PatternFilter("456".getBytes())); //$NON-NLS-1$
    assertThat(read(inputStream), equalTo("0123789")); //$NON-NLS-1$
  }

  @Test
  public void matchingCoords() throws IOException {
    final String value = "{\n" +
        "\"Id\" : \"susanna-ali-mordfall-101\",\n" +
        "\"geoId\":\"(Keine Auswahl)\",\n" +
        "\"geoInfo\":{\n" +
        "\"continent\":\"(Keine Auswahl)\",\n" +
        "\"country\":\"\",\n" +
        "\"location\":\"(Keine Auswahl)\",\n" +
        "\"coords\":\n" +
        "},\n" +
        "\"headline\": \"Anklage fordert lebenslange Haft\",\n" +
        "\"topline\": \"Mordfall Susanna\",\n" +
        "\"shorttext\": \"Im Prozess um den Tod der 14-jährigen Mainzer Schülerin Susanna hat die Staatsanwaltschaft die Höchststrafe für Ali B. gefordert. Außerdem soll er im Anschluss an die Haft in Sicherungsverwahrung genommen werden.\",\n"
        +
        "\"storyUrl\":\"/ardimport/regional/hessen/susanna-ali-mordfall-101.html\",\n" +
        "\"imgUrl\":\"/multimedia/bilder/susanna-mordfall-101~_v-videowebs.jpg\",\n" +
        "\"imgAlttext\":\"Angeklagter Ali B. im Mordfall Susanna | Bildquelle: dpa\",\n" +
        "\"date\":\"02.07.2019 14:34\",\n" +
        "\"timestamp\": \"201907021434\",\n" +
        "\"videos\":\"empty\",\n" +
        "\"audios\": \"empty\"\n" +
        "}"; //$NON-NLS-1$
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(value.getBytes()),
        new PatternFilter(",\n\"coords\":".getBytes())); //$NON-NLS-1$
    assertThat(read(inputStream),
        equalTo("{\n" +
            "\"Id\" : \"susanna-ali-mordfall-101\",\n" +
            "\"geoId\":\"(Keine Auswahl)\",\n" +
            "\"geoInfo\":{\n" +
            "\"continent\":\"(Keine Auswahl)\",\n" +
            "\"country\":\"\",\n" +
            "\"location\":\"(Keine Auswahl)\"\n" +
            "},\n" +
            "\"headline\": \"Anklage fordert lebenslange Haft\",\n" +
            "\"topline\": \"Mordfall Susanna\",\n" +
            "\"shorttext\": \"Im Prozess um den Tod der 14-jährigen Mainzer Schülerin Susanna hat die Staatsanwaltschaft die Höchststrafe für Ali B. gefordert. Außerdem soll er im Anschluss an die Haft in Sicherungsverwahrung genommen werden.\",\n"
            +
            "\"storyUrl\":\"/ardimport/regional/hessen/susanna-ali-mordfall-101.html\",\n" +
            "\"imgUrl\":\"/multimedia/bilder/susanna-mordfall-101~_v-videowebs.jpg\",\n" +
            "\"imgAlttext\":\"Angeklagter Ali B. im Mordfall Susanna | Bildquelle: dpa\",\n" +
            "\"date\":\"02.07.2019 14:34\",\n" +
            "\"timestamp\": \"201907021434\",\n" +
            "\"videos\":\"empty\",\n" +
            "\"audios\": \"empty\"\n" +
            "}")); //$NON-NLS-1$
  }

  @Test
  public void matchingDoubleComma() throws IOException {
    final String value =
        "\"audios\": \"empty\"\n" +
            "},\n" +
            ",\n" +
            "{\n" +
            "\"Id\" : \"3\",\n";
    final byte[] bytes = value.getBytes();
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(bytes),
        new IFilteringInputStreamValidator() {

          private final IntValueQueue queue = new IntValueQueue();
          private int c0 = -1;
          private int c1 = -1;

          @Override
          public boolean accept(final int value) {
            if (!(this.c1 == ',' && this.c0 == '\n' && value == ',')) {
              this.queue.add(value);
              this.c1 = this.c0;
              this.c0 = value;
              return true;
            }
            this.c1 = this.c0;
            this.c0 = value;
            return false;
          }

          @Override
          public IntValueQueue getQueue() {
            return this.queue;
          }
        }); // $NON-NLS-1$
    assertThat(read(inputStream),
        equalTo(
            "\"audios\": \"empty\"\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\"Id\" : \"3\",\n")); //$NON-NLS-1$
  }

  @Test
  public void notMatching456() throws IOException {
    final String value = "012345789"; //$NON-NLS-1$
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(value.getBytes()),
        new PatternFilter("456".getBytes())); //$NON-NLS-1$
    assertThat(read(inputStream), equalTo("012345789")); //$NON-NLS-1$
  }

  @Test
  public void skipFiltering() throws IOException {
    final String value = "0123456789"; //$NON-NLS-1$
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(value.getBytes()),
        new FiveFilteringValidator());
    assertThat(2l, equalTo(inputStream.skip(2)));
    assertThat(read(inputStream), equalTo("2346789")); //$NON-NLS-1$
  }

  @Test
  public void readArray() throws IOException {
    final String value = "0123456789"; //$NON-NLS-1$
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(value.getBytes()),
        new NeutralValidator());
    assertThat(readArray(inputStream), equalTo(value));
  }

  @Test
  public void filteringArray() throws IOException {
    final String value = "0123456789"; //$NON-NLS-1$
    final FilteringInputStream inputStream = new FilteringInputStream(
        new ByteArrayInputStream(value.getBytes()),
        new FiveFilteringValidator());
    assertThat(readArray(inputStream), equalTo("012346789")); //$NON-NLS-1$
  }

  private String read(final FilteringInputStream inputStream) throws IOException {
    ByteArrayOutputStream outputStream = null;
    try {
      outputStream = new ByteArrayOutputStream();
      int value = -1;
      while ((value = inputStream.read()) > -1) {
        outputStream.write(value);
      }
      return outputStream.toString();
    } finally {
      IoUtilities.close(outputStream);
      IoUtilities.close(inputStream);
    }
  }

  private String readArray(final FilteringInputStream inputStream) throws IOException {
    ByteArrayOutputStream outputStream = null;
    try {
      outputStream = new ByteArrayOutputStream();
      final byte[] array = new byte[10];
      final int length = inputStream.read(array);
      outputStream.write(array, 0, length);
      return outputStream.toString();
    } finally {
      IoUtilities.close(outputStream);
      IoUtilities.close(inputStream);
    }
  }
}
