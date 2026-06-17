package com.example.transaction_service.exception;
import com.example.transaction_service.dto.ErrorResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(DownstreamException.class)
  public ResponseEntity<ErrorResponse> downstream(DownstreamException ex) {
    return ResponseEntity.status(ex.getStatus())
      .body(new ErrorResponse(ex.getMessage()));
  }
  @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
  public ResponseEntity<ErrorResponse> invalid(Exception ex) {
    return ResponseEntity.badRequest()
      .body(new ErrorResponse(ex.getMessage()));
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> generic(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(new ErrorResponse("Internal error"));
  }
}
