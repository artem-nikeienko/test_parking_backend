package org.test.parking.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String type;
    private String message;

    public ErrorResponse(String code, Throwable rootCause) {
        this.code = code;
        this.type = rootCause.getClass().getName();
        this.message = rootCause.getMessage();
    }
}
