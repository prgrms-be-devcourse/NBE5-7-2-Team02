package io.twogether.nbe_5_7_2_02team.global.response.success;

import java.net.URI;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public class BaseResponse<T> {

    private int code;
    private String status;
    private String message;
    private T data;

    public static <T> ResponseEntity<Map<String, Object>> of(SuccessCode code, T data,
        URI createdLocation) {
        return switch (code.getStatus()) {
            case OK -> ResponseEntity.ok(
                buildBody(code.getCode(), code.getStatus().name(), code.getMessage(), data));
            case CREATED -> ResponseEntity.created(createdLocation)
                .body(buildBody(code.getCode(), code.getStatus().name(), code.getMessage(), data));
            case NO_CONTENT -> ResponseEntity.noContent().build();
        };
    }

    private static <T> Map<String, Object> buildBody(String code, String status, String message,
        T data) {
        return Map.of(
            "code", code,
            "status", status,
            "message", message,
            "data", data
        );
    }

}
