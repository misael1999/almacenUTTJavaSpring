package com.flamel.almacenutt.util;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(value = MultipartException.class)
    public ResponseEntity<?> handleFileUploadException(MultipartException mpex, HttpServletRequest request) {


        CustomErrorType errorType = new CustomErrorType("Solo se puede subir archivos menores de 25mb",
                "subida de archivos");

        return new ResponseEntity<>(errorType.getResponse(), HttpStatus.CONFLICT);

    }



}
