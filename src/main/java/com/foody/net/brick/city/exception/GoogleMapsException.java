package com.foody.net.brick.city.exception;

public class GoogleMapsException extends Exception {

  public GoogleMapsException(String message) {
    super(message);
  }

  public GoogleMapsException(String message, Throwable cause) {
    super(message, cause);
  }

  public GoogleMapsException(Throwable cause) {
    super(cause);
  }

  protected GoogleMapsException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
