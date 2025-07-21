package com.diginamic.groupe1.transport.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseApi<T> {

    private String status;
    private T data;
    private String message;
    private List<String> errors;

    public static <T> ResponseApi<T> success(T data, String message) {
        return ResponseApi.<T>builder()
                .status("success")
                .data(data)
                .message(message)
                .errors(null)
                .build();
    }

    public static <T> ResponseApi<T> error(String message, List<String> errors) {
        return ResponseApi.<T>builder()
                .status("error")
                .data(null)
                .message(message)
                .errors(errors)
                .build();
    }
}
