package com.Reisblog.dto.auth;

import lombok.Data;

// 登录请求
@Data
public class LoginDTO {
    private String account;
    private String password;
}
