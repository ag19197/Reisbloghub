package com.Reisblog.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

// 用户信息（登录后返回）
@Data
public class UserDTO {
    private Long id;
    private String account;
    private String nickname;
    private String avatar;
    private String role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
