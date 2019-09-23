package com.shoufeng.server.common.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * 结果封装
 *
 * @author shoufeng
 */
@Data
@Builder
public class Result<T> {

    private int code;

    private String message;

    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result ok(String message, Object data) {
        return Result.builder().code(200).message(message == null ? "success" : message).data(data).build();
    }

    public static Result error(String message, Object data) {
        return Result.builder().code(400).message(message == null ? "fail" : message).data(data).build();
    }
}
