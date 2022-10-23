/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.lang.exception;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThrowableMessageExtractorTest {

  private static class AdditionalExceptionHoldingException extends Exception {

    private Throwable additionalThrowable;

    public AdditionalExceptionHoldingException(String message) {
      super(message);
    }

    public void setAdditionalException(Throwable throwable) {
      additionalThrowable = throwable;
    }

    public Throwable getAdditionalThrowable() {
      return additionalThrowable;
    }

    private String getAdditionalInformation() {
      return "additional information";
    }
  }

  public static ThrowableMessageExtractor extractor = ThrowableMessageExtractor.of(
        Set.of(new IThrowableToStringConverter() {
          @Override
          public ThrowableConverterResult convert(Throwable throwable) {
            return ThrowableConverterResult.of(toString(throwable));
          }

          private String toString(Throwable throwable) {
            return throwable.getMessage()
                + "; "
                + ((AdditionalExceptionHoldingException) throwable).getAdditionalInformation();
          }

          @Override
          public boolean isApplicable(Throwable throwable) {
            return throwable instanceof AdditionalExceptionHoldingException;
          }
        }),
        Set.of(new IAdditionalThrowableIterable() {
          @Override
          public Iterable<Throwable> iterable(Throwable throwable) {
            return Arrays.asList(((AdditionalExceptionHoldingException) throwable).getAdditionalThrowable());
          }

          @Override
          public boolean isApplicable(Throwable throwable) {
            return throwable instanceof AdditionalExceptionHoldingException;
          }
        }));

  @Test
  public void visitConcurrentModificationException() {
    ConcurrentModificationException throwable = new ConcurrentModificationException();
    String message = extractor.extract(throwable);
    Assertions.assertNotNull(message);
    Assertions.assertEquals(message, throwable.toString());
  }

  @Test
  public void visitEmptyExceptionLimb() {
    Throwable throwable = new Exception(new Exception(new Exception(new Exception())));
    String message = extractor.extract(throwable);
    Assertions.assertNotNull(message);
    Assertions.assertEquals(message, "java.lang.Exception");
  }

  @Test
  public void visitExceptionLimb() {
    Throwable throwable = new Exception(new Exception(new Exception(new Exception("exception"))));
    String message = extractor.extract(throwable);
    Assertions.assertNotNull(message);
    Assertions.assertEquals(message, "exception");
  }

  @Test
  public void visitNone() {
    String message = extractor.extract(null);
    Assertions.assertNull(message);
  }

  @Test
  public void visitOne() {
    String message = extractor.extract(new Exception("exception"));
    Assertions.assertEquals("exception", message);
  }

  @Test
  public void visitCause() {
    String message = extractor.extract(
        new Exception("exception", new Exception("cause")));
    Assertions.assertEquals("""
        exception
        cause""", message);
  }

  @Test
  public void equalsCauseMessage() {
    String message = extractor.extract(
        new Exception("exception", new Exception("exception")));
    Assertions.assertEquals("exception", message);
  }

  @Test
  public void visitSuppressed() {
    Exception exception = new Exception("exception");
    exception.addSuppressed(new Exception("suppressed"));
    String message = extractor.extract(
        exception);
    Assertions.assertEquals("""
        exception
        suppressed""", message);
  }

  @Test
  public void visitMultipleSuppressed() {
    Exception exception = new Exception("exception");
    exception.addSuppressed(new Exception("suppressed1"));
    exception.addSuppressed(new Exception("suppressed2"));
    String message = extractor.extract(
        exception);
    Assertions.assertEquals("""
        exception
        suppressed1
        suppressed2""", message);
  }

  @Test
  public void visitMultipleEqualSuppressed() {
    Exception exception = new Exception("exception");
    exception.addSuppressed(new Exception("suppressed"));
    exception.addSuppressed(new Exception("suppressed"));
    String message = extractor.extract(
        exception);
    Assertions.assertEquals("""
        exception
        suppressed""", message);
  }
  @Test
  public void visitMultipleSuppressedWithContainingMessageByFirst() {
    Exception exception = new Exception("exception");
    exception.addSuppressed(new Exception("suppressed: other"));
    exception.addSuppressed(new Exception("other"));
    String message = extractor.extract(
        exception);
    Assertions.assertEquals("""
        exception
        suppressed: other""", message);
  }

  @Test
  public void visitMultipleSuppressedWithContainingMessageBySecond() {
    Exception exception = new Exception("exception");
    exception.addSuppressed(new Exception("other"));
    exception.addSuppressed(new Exception("suppressed: other"));
    String message = extractor.extract(
        exception);
    Assertions.assertEquals("""
        exception
        suppressed: other""", message);
  }

  @Test
  public void equalsSuppressedMessage() {
    Exception exception = new Exception("exception");
    exception.addSuppressed(new Exception("exception"));
    String message = extractor.extract(
        exception);
    Assertions.assertEquals("exception", message);
  }

  @Test
  public void visitWithAdditionalInformation() {
    AdditionalExceptionHoldingException throwable
        = new AdditionalExceptionHoldingException("exception");
    String message = extractor.extract(throwable);
    Assertions.assertEquals("exception; additional information", message);
  }

  @Test
  public void visitWithAdditionalException() {
    Exception additional1 = new Exception("additional1");
    AdditionalExceptionHoldingException throwable
        = new AdditionalExceptionHoldingException("exception");
    throwable.setAdditionalException(additional1);
    String message = extractor.extract(throwable);
    Assertions.assertEquals("""
        exception; additional information
        additional1""", message);
  }
}
