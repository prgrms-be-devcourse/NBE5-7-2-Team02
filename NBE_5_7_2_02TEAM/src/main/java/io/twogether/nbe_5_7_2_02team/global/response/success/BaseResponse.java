package io.twogether.nbe_5_7_2_02team.global.response.success;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import java.net.URI;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseResponse<T> {

    private final String code;
    private final String status;
    private final String message;
    private final T data;

    public static <T> ResponseEntity<BaseResponse<T>> of(
            SuccessCode code, T data, URI createdLocation) {
        return switch (code.getStatus()) {
            case OK ->
                    ResponseEntity.ok(
                            new BaseResponse<>(
                                    code.getCode(),
                                    code.getStatus().name(),
                                    code.getMessage(),
                                    data));
            case CREATED ->
                    ResponseEntity.created(createdLocation)
                            .body(
                                    new BaseResponse<>(
                                            code.getCode(),
                                            code.getStatus().name(),
                                            code.getMessage(),
                                            data));
            case NO_CONTENT -> ResponseEntity.noContent().build();
        };
    }
}
