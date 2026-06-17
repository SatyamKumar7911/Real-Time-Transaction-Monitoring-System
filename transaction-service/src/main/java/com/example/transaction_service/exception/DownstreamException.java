package com.example.transaction_service.exception;
import org.springframework.http.HttpStatusCode;
public class DownstreamException extends RuntimeException {
  private final HttpStatusCode status;
  public DownstreamException(String message, HttpStatusCode httpStatusCode) {
    super(message);
    this.status = httpStatusCode;
  }
  public HttpStatusCode getStatus() {
    return status;
  }
}