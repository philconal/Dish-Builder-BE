package com.conal.dishbuilder.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Builder(setterPrefix = "set")
public class BaseResponse<T> {
    private final Integer status;
    private final String message;
    private final T data;
    private final ErrorResponse error;
    private final Boolean success;
    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private final Timestamp timestamp = Timestamp.from(Instant.now());

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .setStatus(HttpStatus.OK.value())
                .setData(data)
                .setMessage("success")
                .setSuccess(true)
                .build();
    }

    public static <T> BaseResponse<T> ok(T data, String message) {
        return BaseResponse.<T>builder()
                .setStatus(HttpStatus.OK.value())
                .setData(data)
                .setMessage(message)
                .setSuccess(true)
                .build();
    }

    public static <T> BaseResponse<T> error(int status, String message) {
        return BaseResponse.<T>builder()
                .setMessage(message)
                .setSuccess(false)
                .setStatus(status)
                .build();
    }

    public static <T> BaseResponse<T> error(int status, String message, ErrorResponse errorDetail) {
        return BaseResponse.<T>builder()
                .setMessage(message)
                .setSuccess(false)
                .setStatus(status)
                .setError(errorDetail)
                .build();
    }
}
