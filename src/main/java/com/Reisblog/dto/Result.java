package com.Reisblog.dto;

import com.Reisblog.dto.comment.CommentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//统一返回结果类
// 通用响应
@Data
public class Result<T> {
    private int code;          // 200成功，其他失败
    private String message;
    private T data;

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        return r;
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    // 失败响应（自定义错误码和消息）
    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    // 常用失败快捷方法（400 错误）
    public static <T> Result<T> fail(String message) {
        return error(400, message);
    }
}

