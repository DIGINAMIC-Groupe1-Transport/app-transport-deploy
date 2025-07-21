package com.diginamic.groupe1.transport.exception;

import com.diginamic.groupe1.transport.utils.ResponseApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseApi<Object>> handleGenericException(Exception ex) {
        log.error("Une exception est survenue : ", ex);
        ResponseApi<Object> body = ResponseApi.error(
                "Une erreur interne est survenue",
                null
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseApi<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.info("Business event happened : ", ex);
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .toList();

        ResponseApi<Object> body = ResponseApi.error(
                "Erreur de validation des champs",
                errors
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseApi<Object>> handleBusinessException(BusinessException ex) {
        log.info("Business event happened : ", ex);
        ResponseApi<Object> body = ResponseApi.error(ex.getMessage(), null);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseApi<Object>> handleNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found with id: ", ex);
        ResponseApi<Object> body = ResponseApi.error(ex.getMessage(), null);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}

