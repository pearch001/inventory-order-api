package com.pearchInventory.ioa.exceptions;

import com.pearchInventory.ioa.enums.ResponseCode;
import com.pearchInventory.ioa.utils.GenericData;
import com.pearchInventory.ioa.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Resource not found");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Method not allowed");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unsupported media type");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Missing request parameter");
        errorResponse.put("parameterName", ex.getParameterName());
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            UserNotFoundException.class,
            EmailExistsException.class,
            InsufficientStockException.class,
            ProductExistsException.class,
            BadCredentialsException.class,
            OrderNotFoundException.class,
            CustomerNotFoundException.class,
            ProductNotFoundException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<Response> handleBadRequestExceptions(Exception ex) {
        ResponseCode responseCode;
        String errorMessage;

        if (ex instanceof MethodArgumentNotValidException) {
            Map<String, String> errors = new HashMap<>();
            ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            responseCode = ResponseCode.BAD_REQUEST;
            errorMessage = errors.toString();
        } else if (ex instanceof UserNotFoundException) {
            responseCode = ResponseCode.INVALID_CREDENTIALS;
            errorMessage = ex.getMessage();
        } else if (ex instanceof EmailExistsException) {
            responseCode = ResponseCode.EMAIL_EXISTS;
            errorMessage = ex.getMessage();
        } else if (ex instanceof InsufficientStockException) {
            responseCode = ResponseCode.INSUFFICIENT_BALANCE;
            errorMessage = ex.getMessage();
        } else if (ex instanceof ProductExistsException) {
            responseCode = ResponseCode.ACCOUNT_ALREADY_EXISTS;
            errorMessage = ex.getMessage();
        } else if (ex instanceof BadCredentialsException) {
            responseCode = ResponseCode.BAD_REQUEST;
            errorMessage = ex.getMessage();
        } else if (ex instanceof OrderNotFoundException) {
            responseCode = ResponseCode.ACCOUNT_NOT_FOUND;
            errorMessage = ex.getMessage();
        }else if (ex instanceof CustomerNotFoundException) {
            responseCode = ResponseCode.CUSTOMER_NOT_FOUND;
            errorMessage = ex.getMessage();
        }else if (ex instanceof ProductNotFoundException) {
            responseCode = ResponseCode.PRODUCT_NOT_FOUND;
            errorMessage = ex.getMessage();
        } else if (ex instanceof ConstraintViolationException) {
            responseCode = ResponseCode.BAD_REQUEST;
            errorMessage = ex.getMessage();
        } else {
            responseCode = ResponseCode.BAD_REQUEST;
            errorMessage = "Bad Request";
        }

        return new ResponseEntity<>(new Response(responseCode,new GenericData<>(errorMessage)), HttpStatus.BAD_REQUEST);
    }
}