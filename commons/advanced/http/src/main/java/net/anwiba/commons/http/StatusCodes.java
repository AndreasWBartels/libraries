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
package net.anwiba.commons.http;

public enum StatusCodes {

  OK(200, "OK"),
  CREATED(201, "Created"),
  Accepted(202, "Accepted"),
  NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
  NO_CONTENT(204, "No Content"),
  RESET_CONTENT(205, "Reset Content"),
  PARTIAL_CONTENT(206, "Partial Content"),
  MULTI_STATUS(207, "Multi-Status"),
  ALREADY_REPORTED(208, "Already Reported"),
  IM_USED(226, "IM Used"),

  BAD_REQUEST(400, "Bad Request"),
  UNAUTHORIZED(401, "Unauthorized"),
  PAYMENT_REQUIRED(402, "Payment Required"),
  FORBIDDEN(403, "Forbidden"),
  NOT_FOUND(404, "Not Found"),
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
  NOT_ACCEPTABLE(406, "Not Acceptable"),
  PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
  REQUEST_TIMEOUT(408, "Request Timeout"),
  CONFLICT(409, "Conflict"),
  GONE(410, "Gone"),
  LENGTH_REQUIRED(411, "Length Required"),
  PRECONDITION_FAILED(412, "Precondition Failed"),
  PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
  URI_TOO_LONG(414, "URI Too Long"),
  UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
  RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
  EXPECTATION_FAILED(417, "Expectation Failed"),
  I_M_A_TEAPOT(418, "I'm a teapot"),
  MISDIRECTED_REQUEST(421, "Misdirected Request"),
  UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
  LOCKED(423, "Locked"),
  FAILED_DEPENDENCY(424, "Failed Dependency"),
  TOO_EARLY(425, "Too Early"),
  UPGRADE_REQUIRED(426, "Upgrade Required"),
  PRECONDITION_REQUIRED(428, "Precondition Required"),
  TOO_MANY_REQUESTS(429, "Too Many Requests"),
  REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
  UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),

  INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
  NOT_IMPLEMENTED(501, "Not Implemented"),
  BAD_GATEWAY(502, "Bad Gateway"),
  SERVICE_UNAVAILABLE(503, "Service Unavailable"),
  GATEWAY_TIMEOUT(504, "Gateway Timeout"),
  HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
  VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
  INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
  LOOP_DETECTED(508, "Loop Detected"),
  NOT_EXTENDED(510, "Not Extended"),
  NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"),

  CONTINUE(100, "Continue"),
  SWITCHING_PROTOCOL(101, "Switching Protocol"),
  PROCESSING(102, "Processing"),
  EARLY_HINTS(103, "Early Hints"),

  Multiple_Choice(300, "Multiple Choice"),
  MOVED_PERMANENTLY(301, "Moved Permanently"),
  MOVED_TEMPORARY(302, "Moved Temporary"),
//  FOUND(302, "Found"),
  SEE_OTHER(303, "See Other"),
  NOT_MODIFIED(304, "Not Modified"),
  USE_PROXY(305, "Use Proxy"),
//  (306,"unused"),
  TEMPORARY_REDIRECT(307, "Temporary Redirect"),
  PERMANENT_REDIRECT(308, "Permanent Redirect");

  private int code;
  private String phrase;

  private StatusCodes(int code, String phrase) {
    this.code = code;
    this.phrase = phrase;
  }

  public int getCode() {
    return this.code;
  }

  public String getPhrase() {
    return this.phrase;
  }

  public static String getPhrase(int code) {
    for (StatusCodes statusCodes : values()) {
      if (code == statusCodes.code) {
        return statusCodes.phrase;
      }
    }
    if (isInformation(code)) {
      return "Information";
    }
    if (isSuccessful(code)) {
      return "Successful";
    }
    if (isRedirection(code)) {
      return "Redirection";
    }
    if (isClientError(code)) {
      return "Client Error";
    }
    if (isServerError(code)) {
      return "Server Error";
    }
    return "Unknown category for status code";
  }

  public static boolean isOk(int code) {
    return code == OK.code;
  }

  public static boolean isNotFound(int code) {
    return code == NOT_FOUND.code;
  }

  public static boolean isInformation(int code) {
    return 100 <= code && code < 200;
  }

  public static boolean isSuccessful(int code) {
    return 200 <= code && code < 300;
  }

  public static boolean isRedirection(int code) {
    return code == MOVED_PERMANENTLY.code
        || code == MOVED_TEMPORARY.code
        || code == SEE_OTHER.code
        || code == PERMANENT_REDIRECT.code
        || code == TEMPORARY_REDIRECT.code;
  }

  public static boolean isClientError(int code) {
    return 400 <= code && code < 500;
  }

  public static boolean isServerError(int code) {
    return 500 <= code && code < 600;
  }

}
