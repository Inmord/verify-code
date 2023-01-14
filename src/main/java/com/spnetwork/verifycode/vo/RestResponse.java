package com.spnetwork.verifycode.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author aiker
 * @implSpec Nu bug no errors
 * @since 2022/12/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {

    public static final int SUCCESS_CODE = 200;
    private Integer code;
    private String message;
    private T data;

    public static <T> RestResponse<T> success() {
        return RestResponse.success(null);
    }

    public static <T> RestResponse<T> success(T data) {
        return RestResponse.of(SUCCESS_CODE, "OK", data);
    }

    public static <T> RestResponse<T> serverFailure(String message) {
        return RestResponse.failure(FailCode.SERVER_ERROR.getCode(), message);
    }

    public static <T> RestResponse<T> failure(FailCode failCode) {
        return RestResponse.failure(failCode, null);
    }

    public static <T> RestResponse<T> failure(FailCode failCode, T data) {
        return RestResponse.of(failCode.getCode(), failCode.name(), data);
    }

    public static <T> RestResponse<T> failure(int code, String message) {
        return RestResponse.of(code, message, null);
    }

    public static <T> RestResponse<T> failureWithCode(int code, String message) {
        return RestResponse.of(code, message, null);
    }

    public static <T> RestResponse<T> of(int code, String message, T data) {
        return new RestResponse<>(code, message, data);
    }

    public static boolean isSuccess(RestResponse restResponse) {
        return restResponse.getCode() == 200;
    }

    public enum FailCode {
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        ARGS(422),
        SERVER_ERROR(500),
        SERVICE_ERROR(501);

        @Getter
        private int code;

        FailCode(int code) {
            this.code = code;
        }
    }
}
