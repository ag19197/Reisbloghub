package com.Reisblog.service;

import com.Reisblog.dto.auth.LoginDTO;
import com.Reisblog.dto.auth.RegisterDTO;
import com.Reisblog.dto.auth.UserDTO;
import com.Reisblog.entity.Comment;
import com.Reisblog.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param dto 注册信息
     * @return 注册成功的用户信息
     */
    UserDTO register(RegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录信息
     * @return JWT token
     */
    String login(LoginDTO dto);

    /**
     * 根据用户ID获取用户信息
     */
    UserDTO getUserById(Long id);
}
