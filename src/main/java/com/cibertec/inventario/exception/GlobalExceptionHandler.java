package com.cibertec.inventario.exception;

import com.cibertec.inventario.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Atrapa nuestros errores de logica de negocio (ej. "Stock insuficiente", "Categoria no existe")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                List.of("Error en las reglas del negocio"),
                LocalDateTime.now()
        );
        // Devolvemos 400 Bad Request
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 2. Atrapa los errores de validacion de los DTOs (los @Valid que pusimos en los records)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Extraemos todos los mensajes de error de los campos que fallaron
        List<String> errores = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Error de validacion en los datos enviados",
                errores,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 3. Atrapa CUALQUIER otro error no previsto (ej. caida de base de datos)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Ocurrio un error interno en el servidor",
                List.of(ex.getMessage()), // En produccion, es mejor no enviar el mensaje real por seguridad
                LocalDateTime.now()
        );

        // Devolvemos 500 Internal Server Error
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
