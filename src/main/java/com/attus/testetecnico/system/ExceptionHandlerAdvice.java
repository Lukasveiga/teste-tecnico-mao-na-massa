package com.attus.testetecnico.system;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<HttpResponseResult> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        var message = ex.getMessage();

        if (message.contains("java.time.LocalDate")) {
            message = "Invalid date format. Follow the following pattern: dd/MM/yyyy";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new HttpResponseResult(
                        false,
                        "Provided arguments are invalid, see data for details.",
                        LocalDateTime.now(),
                        message
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<HttpResponseResult> handleValidationException(MethodArgumentNotValidException ex) {

        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        Map<String, String> map = new HashMap<>(errors.size());

        errors.forEach((error) -> {
            String key = ((FieldError) error).getField();
            String val = error.getDefaultMessage();
            map.put(key, val);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new HttpResponseResult(
                        false,
                        "Provided arguments are invalid, see data for details.",
                        LocalDateTime.now(),
                        map
                )
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<HttpResponseResult> handleOthersExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new HttpResponseResult(
                        false,
                        "Internal Server Error",
                        LocalDateTime.now(),
                        null
                )
        );
    }
}
