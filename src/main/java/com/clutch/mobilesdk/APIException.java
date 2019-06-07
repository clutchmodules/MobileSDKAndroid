package com.clutch.mobilesdk;

/**
 * Indicates an exception happened with API communication.
 */
public class APIException extends RuntimeException {

  public APIException(String message, Throwable cause) {
    super(message, cause);
  }

  public APIException(String message) {
    super(message);
  }

}
