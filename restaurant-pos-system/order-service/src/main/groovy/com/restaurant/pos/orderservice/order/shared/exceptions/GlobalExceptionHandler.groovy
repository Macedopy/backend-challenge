package com.restaurant.pos.orderservice.shared.exception

import com.restaurant.pos.orderservice.order.exception.MenuItemUnavailableException
import com.restaurant.pos.orderservice.order.exception.OrderNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException)
    ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderNotFoundException ex) {
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(buildError(HttpStatus.NOT_FOUND, ex.message))
    }

    @ExceptionHandler(MenuItemUnavailableException)
    ResponseEntity<Map<String, Object>> handleMenuItemUnavailable(MenuItemUnavailableException ex) {
        ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException)
    ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.bindingResult.fieldErrors
            .collect { FieldError fe -> "${fe.field}: ${fe.defaultMessage}" }

        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, "Validation failed", errors))
    }

    @ExceptionHandler(HttpMessageNotReadableException)
    ResponseEntity<Map<String, Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body"))
    }

    @ExceptionHandler(MissingServletRequestParameterException)
    ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, "Missing required parameter: '${ex.parameterName}'"))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException)
    ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, "Invalid value '${ex.value}' for parameter '${ex.name}'"))
    }

    @ExceptionHandler(Exception)
    ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"))
    }

    private static Map<String, Object> buildError(HttpStatus status, String message, List<String> errors = null) {
        def body = [
            timestamp: LocalDateTime.now().toString(),
            status   : status.value(),
            error    : status.reasonPhrase,
            message  : message
        ] as Map<String, Object>

        if (errors) body.put("errors", errors)

        body
    }
}
