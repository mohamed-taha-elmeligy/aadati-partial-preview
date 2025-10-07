package com.mts.aadati.utils;

import lombok.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class ApiResponse<T> {
    private boolean state ;
    private final String message ;
    private final T data ;

    public static <T> ApiResponse<T> success (String message , T data){
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .state(true)
                .build();
    }
    public static <T> ApiResponse<T> error (String message){
        return ApiResponse.<T>builder()
                .message(message)
                .data(null)
                .state(false)
                .build();
    }

}
