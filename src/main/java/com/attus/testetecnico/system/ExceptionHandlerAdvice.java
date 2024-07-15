package com.attus.testetecnico.system;

import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import com.attus.testetecnico.services.exceptions.MainAddressException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<HttpResponseResult> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        var message = ex.getMessage();

        if (message.contains("java.time.LocalDate")) {
            message = "Invalid date format. Follow the following pattern: dd/MM/yyyy";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new HttpResponseResult(
                        false,
                        "Provided arguments are invalid, see data for details",
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
                        "Provided arguments are invalid, see data for details",
                        LocalDateTime.now(),
                        map
                )
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<HttpResponseResult> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new HttpResponseResult(
                        false,
                        ex.getMessage(),
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @ExceptionHandler(MainAddressException.class)
    ResponseEntity<HttpResponseResult> handleMainAddressException(MainAddressException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new HttpResponseResult(
                        false,
                        ex.getMessage(),
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    ResponseEntity<HttpResponseResult> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        LOGGER.info(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new HttpResponseResult(
                        false,
                        "API endpoint not found",
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<HttpResponseResult> handleOthersExceptions(Exception ex) {
        LOGGER.error(ex.getMessage());
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
