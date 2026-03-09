package com.Reisblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.auth.LoginDTO;
import com.Reisblog.dto.auth.RegisterDTO;
import com.Reisblog.dto.auth.UserDTO;
import com.Reisblog.dto.collection.PublicCollectionDTO;
import com.Reisblog.dto.user.UserProfileDTO;
import com.Reisblog.entity.User;
import com.Reisblog.exception.BusinessException;
import com.Reisblog.mapper.UserMapper;
import com.Reisblog.service.CollectionService;
import com.Reisblog.service.UserService;
import com.Reisblog.utils.JwtUtils;
import com.Reisblog.utils.PasswordEncoder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final CollectionService collectionService;

    @Override
    public UserDTO register(RegisterDTO dto) {
        // 1. 验证验证码（模拟）
        // 实际应该从 Redis 获取验证码并比较，这里简化
        // 假设验证码固定为 123456（仅用于测试）
        if (!"123456".equals(dto.getCode())) {
            throw new BusinessException("验证码错误");
        }

        // 2. 检查账号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, dto.getAccount());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("账号已存在");
        }

        // 3. 创建用户
        User user = new User();
        user.setAccount(dto.getAccount());
        // 判断账号类型：包含@为邮箱，否则手机号
        int accountType = dto.getAccount().contains("@") ? 1 : 2;
        user.setAccountType(accountType);
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // 加密
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : "用户" + System.currentTimeMillis());
        user.setRole("USER");
        this.save(user);

        return BeanUtil.copyProperties(user, UserDTO.class);
    }

    @Override
    public String login(LoginDTO dto) {
        // 1. 根据账号查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, dto.getAccount());
        User user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        // 3. 生成 JWT token
        return jwtUtils.generateToken(user.getId());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return BeanUtil.copyProperties(user, UserDTO.class);
    }

    @Override
    public UserProfileDTO getUserProfile(Long userId, int page, int size) {
        // 1. 查询用户基本信息
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 2. 查询用户的公开收藏列表（调用 CollectionService）
        PageResult<PublicCollectionDTO> publicCollections = collectionService.getPublicCollections(userId, page, size);
        // 3. 组装 DTO
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUserId(user.getId());
        profile.setNickname(user.getNickname());
        profile.setAvatar(user.getAvatar());
        profile.setPublicCollections(publicCollections);
        return profile;
    }
}
