package com.example.accountservice.exception;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
@ExceptionHandler(NotFoundException.class)
public ResponseEntity<?> nf(NotFoundException ex){
 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
}
@ExceptionHandler(InsufficientFundsException.class)
public ResponseEntity<?> bad(InsufficientFundsException ex){
 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
}
@ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class, MethodArgumentNotValidException.class})
public ResponseEntity<?> invalid(Exception ex){
 return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
}
@ExceptionHandler(Exception.class)
public ResponseEntity<?> err(Exception ex){
 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal error"));
}
}