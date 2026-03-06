package com.Reisblog.dto.auth;

import lombok.Data;

// 注册信息
@Data
public class RegisterDTO {
    private String account;
    private String code;
    private String password;
    private String nickname;
}
