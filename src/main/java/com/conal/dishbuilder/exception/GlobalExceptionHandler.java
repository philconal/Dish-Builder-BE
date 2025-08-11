package com.conal.dishbuilder.exception;

import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.ErrorResponse;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotFoundException(Exception e) {
        log.error("Not found Exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<BaseResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<FieldErrorResponse> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    error.getRejectedValue();
                    return FieldErrorResponse.builder()
                            .setField(error.getField())
                            .setRejectedValue(error.getRejectedValue().toString())
                            .setMessage(error.getDefaultMessage())
                            .build();
                })
                .toList();

        ErrorResponse errorDetail = mapeErrorResponse(fieldErrors);

        return ResponseEntity.badRequest()
                .body(BaseResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        errorDetail.getMessage(),
                        errorDetail
                ));
    }

    @ExceptionHandler(MultipleFieldValidationException.class)
    public ResponseEntity<BaseResponse<Void>> handleManualValidationException(MultipleFieldValidationException ex) {
        ErrorResponse errorDetail = mapeErrorResponse(ex.getFieldErrors());

        return ResponseEntity.badRequest()
                .body(BaseResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        errorDetail.getMessage(),
                        errorDetail
                ));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<BaseResponse<Void>> handleInternalServerException(InternalServerException ex) {
        return ResponseEntity.internalServerError()
                .body(BaseResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage()
                ));
    }

    private static ErrorResponse mapeErrorResponse(List<FieldErrorResponse> ex) {
        ErrorResponse errorDetail = new ErrorResponse();
        errorDetail.setCode("VALIDATION_ERROR");
        errorDetail.setMessage("Validation failed for " + ex.size() + " field(s)");
        errorDetail.setDetails("One or more fields failed validation");
        errorDetail.setFieldErrors(ex);
        return errorDetail;
    }

}
