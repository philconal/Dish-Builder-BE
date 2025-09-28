package com.conal.dishbuilder.util;

import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import jakarta.validation.ConstraintViolation;

import java.security.SecureRandom;

public class CommonUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String genOTP(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return otp.toString();
    }
    public static FieldErrorResponse buildFieldErrorResponse(ConstraintViolation<Object> request) {
        return FieldErrorResponse.builder()
                .setField(request.getPropertyPath().toString()) // error field name
                .setRejectedValue(request.getInvalidValue() != null ? request.getInvalidValue().toString() : null) // rejected value
                .setMessage(request.getMessage()) // message from @NotBlank, @Email,...
                .build();
    }

    public static FieldErrorResponse buildFieldErrorResponse(String field, String rejectedValue, String message) {
        return FieldErrorResponse.builder()
                .setMessage(message)
                .setRejectedValue(rejectedValue)
                .setField(field)
                .build();
    }
}
