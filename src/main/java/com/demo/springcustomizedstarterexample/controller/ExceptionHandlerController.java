package com.demo.springcustomizedstarterexample.controller;

import com.demo.springcustomizedstarterexample.services.common.GenericResponseDTO;
import com.demo.springcustomizedstarterexample.utils.exceptions.CustomAppException;
import com.demo.springcustomizedstarterexample.utils.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(final ResourceNotFoundException ex,
                                                       final HttpServletRequest request) {

        log.info("DataNotFoundException handled {} ", ex.getMessage());
        GenericResponseDTO<String> genericResponseDTO = new GenericResponseDTO<>(ex.getMessage(), null);
        return new ResponseEntity<>(genericResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex,
                                                                 final HttpServletRequest request) {

        log.info("MethodArgumentTypeMismatchException handled {} ", ex.getMessage());
        GenericResponseDTO<String> genericResponseDTO = new GenericResponseDTO<>(ex.getMessage(), null);
        return new ResponseEntity<>(genericResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(final BadCredentialsException ex,
                                                       final HttpServletRequest request) {

        log.info("badCredentialsException handled {} ", ex.getMessage());
        GenericResponseDTO<String> genericResponseDTO = new GenericResponseDTO<>(ex.getMessage(), null);
        return new ResponseEntity<>(genericResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<?> globalAppException(final CustomAppException ex,
                                                final HttpServletRequest request) {

        log.info("CustomAppException handled {}", ex.getMessage());
        GenericResponseDTO<String> genericResponseDTO = new GenericResponseDTO<>(ex.getMessage(), null);
        return new ResponseEntity<>(genericResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> globalAppException(final RuntimeException ex,
                                                final HttpServletRequest request) {

        log.info("Runtime Exception occurred {} ", ex.getMessage());
        ex.printStackTrace();
        GenericResponseDTO<String> genericResponseDTO = new GenericResponseDTO<>(ex.getMessage(), null);
        return new ResponseEntity<>(genericResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
